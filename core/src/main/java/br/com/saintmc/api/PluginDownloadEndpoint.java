package br.com.saintmc.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Endpoint para download de plugins.
 * Expõe o endpoint GET /plugins/:name/download que envia o arquivo do plugin.
 */
public class PluginDownloadEndpoint {

    private final HttpServer server;
    private final String pluginsDirectory;

    /**
     * Cria um novo endpoint de download de plugins.
     * 
     * @param port Porta onde o servidor HTTP irá escutar
     * @param pluginsDirectory Diretório onde os arquivos .jar dos plugins estão armazenados
     * @throws IOException se houver erro ao criar o servidor
     */
    public PluginDownloadEndpoint(int port, String pluginsDirectory) throws IOException {
        this.pluginsDirectory = pluginsDirectory;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/plugins/", new PluginDownloadHandler());
        this.server.setExecutor(null); // Usa executor padrão
    }

    /**
     * Inicia o servidor HTTP.
     */
    public void start() {
        this.server.start();
    }

    /**
     * Para o servidor HTTP.
     * 
     * @param delay Delay em segundos antes de parar
     */
    public void stop(int delay) {
        this.server.stop(delay);
    }

    /**
     * Handler para requisições de download de plugins.
     */
    private class PluginDownloadHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            
            // Extrai o nome do plugin do path: /plugins/:name/download
            if (!path.startsWith("/plugins/") || !path.endsWith("/download")) {
                sendResponse(exchange, 404, "Not Found");
                return;
            }

            // Remove /plugins/ do início e /download do final
            String pluginName = path.substring("/plugins/".length(), path.length() - "/download".length());
            
            if (pluginName.isEmpty()) {
                sendResponse(exchange, 400, "Bad Request: Plugin name is required");
                return;
            }

            // Busca o arquivo do plugin
            Path pluginFile = Paths.get(pluginsDirectory, pluginName + ".jar");
            File file = pluginFile.toFile();

            if (!file.exists() || !file.isFile()) {
                sendResponse(exchange, 404, "Plugin not found: " + pluginName);
                return;
            }

            // Envia o arquivo
            try {
                exchange.getResponseHeaders().set("Content-Type", "application/java-archive");
                exchange.getResponseHeaders().set("Content-Disposition", 
                    "attachment; filename=\"" + pluginName + ".jar\"");
                exchange.sendResponseHeaders(200, file.length());

                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
            byte[] response = message.getBytes();
            exchange.sendResponseHeaders(statusCode, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }
}


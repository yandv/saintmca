package br.com.saintmc.plugin;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import br.com.saintmc.CommonConst;
import br.com.saintmc.plugin.Plugin;

/**
 * Gerenciador de plugins.
 * Permite buscar informações de plugins da API.
 */
public class PluginManager {

    /**
     * Busca informações de um plugin pelo nome.
     * 
     * @param pluginName Nome do plugin
     * @return Plugin com as informações obtidas da API, ou null se não encontrado
     * @throws Exception se houver erro ao buscar informações
     */
    public static Plugin getPluginInfo(String pluginName) throws Exception {
        String apiUrl = CommonConst.API_URL + "/plugins/" + pluginName;
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream in = conn.getInputStream()) {
            return CommonConst.GSON.fromJson(new String(in.readAllBytes(), StandardCharsets.UTF_8), Plugin.class);
        }
    }
}

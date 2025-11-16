package br.com.saintmc.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import br.com.saintmc.player.Player;
import br.com.saintmc.server.Server;
import br.com.saintmc.server.ServerType;
import br.com.saintmc.server.loadbalancer.LoadBalancer;

/**
 * Gerenciador central de servidores do sistema.
 * Responsável por manter o registro de todos os servidores e distribuir
 * jogadores entre eles usando os load balancers configurados.
 */
public class ServerManager {

    private final Map<ServerType, List<Server>> serversMap;
    private final Map<String, Server> serversBySlug;

    public ServerManager() {
        this.serversMap = new ConcurrentHashMap<>();
        this.serversBySlug = new ConcurrentHashMap<>();
    }

    /**
     * Registra um novo servidor no gerenciador.
     * 
     * @param server Servidor a ser registrado
     * @throws IllegalArgumentException se o servidor já estiver registrado
     */
    public void registerServer(Server server) {
        if (server == null) {
            throw new IllegalArgumentException("Server cannot be null");
        }

        if (this.serversBySlug.containsKey(server.getSlug())) {
            throw new IllegalArgumentException("Server with slug '" + server.getSlug() + "' is already registered");
        }

        this.serversMap.computeIfAbsent(server.getType(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(server);
        this.serversBySlug.put(server.getSlug(), server);
    }

    /**
     * Remove um servidor do gerenciador.
     * 
     * @param server Servidor a ser removido
     * @return true se o servidor foi removido, false caso contrário
     */
    public void unregisterServer(Server server) {
        if (server == null) {
            return;
        }

        List<Server> servers = this.serversMap.get(server.getType());
        if (servers != null) {
            servers.remove(server);
        }

        this.serversBySlug.remove(server.getSlug());
        this.serversMap.computeIfPresent(server.getType(), (k, v) -> {
            v.remove(server);
            return v.isEmpty() ? null : v;
        });
    }

    /**
     * Remove um servidor pelo seu slug.
     * 
     * @param slug Slug do servidor a ser removido
     * @return true se o servidor foi removido, false caso contrário
     */
    public void unregisterServer(String slug) {
        Server server = this.serversBySlug.get(slug);

        if (server != null) {
            this.unregisterServer(server);
        }
    }

    /**
     * Retorna o próximo servidor disponível para o jogador especificado.
     * 
     * @param type Tipo de servidor desejado
     * @param player Jogador que está solicitando o servidor
     * @return O próximo servidor disponível ou null se nenhum estiver disponível
     */
    public <T extends Server> T getNextAvailableServer(ServerType type, Player player) {
        List<Server> serverList = this.serversMap.get(type);

        if (serverList == null || serverList.isEmpty()) {
            return null;
        }

        LoadBalancer<Server> loadBalancer = type.getLoadBalancer();
        @SuppressWarnings("unchecked")
        T server = (T) loadBalancer.getNextAvailableServer(serverList, player);
        
        return server;
    }

    /**
     * Retorna um servidor pelo seu slug.
     * 
     * @param slug Slug do servidor
     * @return O servidor encontrado ou null se não existir
     */
    public Server getServerBySlug(String slug) {
        return this.serversBySlug.get(slug);
    }

    /**
     * Retorna todos os servidores de um tipo específico.
     * 
     * @param type Tipo de servidor
     * @return Lista imutável de servidores do tipo especificado
     */
    public List<Server> getServersByType(ServerType type) {
        List<Server> servers = this.serversMap.get(type);
        return servers != null ? List.copyOf(servers) : List.of();
    }

    /**
     * Retorna todos os servidores registrados.
     * 
     * @return Lista imutável de todos os servidores
     */
    public List<Server> getAllServers() {
        return this.serversBySlug.values().stream()
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Verifica se existe algum servidor disponível do tipo especificado.
     * 
     * @param type Tipo de servidor
     * @return true se houver pelo menos um servidor disponível
     */
    public boolean hasAvailableServer(ServerType type) {
        List<Server> servers = this.serversMap.get(type);
        if (servers == null || servers.isEmpty()) {
            return false;
        }
        return servers.stream().anyMatch(Server::isAvailable);
    }

    /**
     * Retorna a quantidade de servidores registrados de um tipo específico.
     * 
     * @param type Tipo de servidor
     * @return Quantidade de servidores do tipo especificado
     */
    public int getPlayerCount(ServerType type) {
        return this.serversMap.get(type).stream().mapToInt(Server::getPlayerCount).sum();
    }
}

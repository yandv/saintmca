package br.com.saintmc.server;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

import br.com.saintmc.server.loadbalancer.LoadBalancer;
import br.com.saintmc.server.loadbalancer.impl.LeastConnection;
import br.com.saintmc.server.loadbalancer.impl.RoundRobin;

/**
 * Enum que representa os tipos de servidor disponíveis no sistema.
 * Cada tipo possui uma instância única do load balancer associado.
 */
public enum ServerType {

    /**
     * Servidor proxy - usa Round Robin para distribuir conexões
     */
    PROXY(RoundRobin::new),
    
    /**
     * Servidor lobby - usa Least Connection para distribuir jogadores
     */
    LOBBY(LeastConnection::new);
    
    private final LoadBalancer<Server> loadBalancer;

    ServerType(Supplier<LoadBalancer<Server>> loadBalancerSupplier) {
        this.loadBalancer = loadBalancerSupplier.get();
    }

    /**
     * Retorna a instância única do load balancer associado a este tipo de servidor.
     * 
     * @return Instância única do load balancer
     */
    public LoadBalancer<Server> getLoadBalancer() {
        return this.loadBalancer;
    }

    private static final Map<String, ServerType> byName;

    static {
        byName = new HashMap<>();
        for (ServerType type : values()) {
            byName.put(type.name(), type);
        }
    }

    public static ServerType getByName(String name) {
        return byName.getOrDefault(name, null);
    }
}

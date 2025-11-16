package br.com.saintmc.server.loadbalancer.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import br.com.saintmc.player.Player;
import br.com.saintmc.server.Server;
import br.com.saintmc.server.loadbalancer.LoadBalancer;

/**
 * Implementação de load balancer usando o algoritmo Round Robin.
 * Distribui as requisições de forma circular entre os servidores disponíveis.
 */
public class RoundRobin<T extends Server> implements LoadBalancer<T> {

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public T getNextAvailableServer(List<T> servers, Player player) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        List<T> availableServers = servers.stream()
                .filter(Server::isAvailable)
                .toList();

        if (availableServers.isEmpty()) {
            return null;
        }

        int size = availableServers.size();
        int index = this.currentIndex.getAndUpdate(i -> (i + 1) % size) % size;
        return availableServers.get(index);
    }

}

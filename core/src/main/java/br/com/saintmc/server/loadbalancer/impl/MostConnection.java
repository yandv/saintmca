package br.com.saintmc.server.loadbalancer.impl;

import java.util.Comparator;
import java.util.List;

import br.com.saintmc.player.Player;
import br.com.saintmc.server.Server;
import br.com.saintmc.server.loadbalancer.LoadBalancer;

/**
 * Implementação de load balancer que seleciona o servidor com mais conexões.
 * Útil para casos específicos onde se deseja concentrar jogadores em poucos servidores.
 */
public class MostConnection<T extends Server> implements LoadBalancer<T> {

    @Override
    public T getNextAvailableServer(List<T> servers, Player player) {
        if (servers == null || servers.isEmpty()) {
            return null;
        }

        return servers.stream()
                .filter(Server::isAvailable)
                .max(Comparator.comparingInt(Server::getPlayerCount))
                .orElse(null);
    }
}

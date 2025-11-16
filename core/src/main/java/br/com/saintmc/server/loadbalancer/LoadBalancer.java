package br.com.saintmc.server.loadbalancer;

import java.util.List;

import br.com.saintmc.player.Player;
import br.com.saintmc.server.Server;

public interface LoadBalancer<T extends Server> {

    /**
     * Retorna o próximo servidor disponível para o jogador especificado.
     * 
     * @param servers Lista de servidores disponíveis
     * @param player Jogador que está solicitando o servidor (pode ser null)
     * @return O próximo servidor disponível ou null se nenhum estiver disponível
     */
    T getNextAvailableServer(List<T> servers, Player player);
    
}

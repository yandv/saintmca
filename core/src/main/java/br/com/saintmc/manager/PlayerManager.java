package br.com.saintmc.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import br.com.saintmc.player.Player;

public class PlayerManager {

    private final Map<UUID, Player> playersMap;

    public PlayerManager() {
        this.playersMap = new HashMap<>();
    }

    public void registerPlayer(Player player) {
        this.playersMap.put(player.getId(), player);
    }

    public void unregisterPlayer(UUID id) {
        this.playersMap.remove(id);
    }
    
}

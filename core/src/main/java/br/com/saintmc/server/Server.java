package br.com.saintmc.server;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.com.saintmc.CommonConst;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class Server {

    private final String slug;
    
    private final String address;
    private final int port;
    private final ServerType type;

    private int maxPlayers;

    @Getter(AccessLevel.NONE)
    private final Set<UUID> players;

    public Server(String slug, String address, int port, ServerType type) {
        this.slug = slug;
        this.address = address;
        this.port = port;
        this.type = type;
        this.maxPlayers = CommonConst.MAX_DEFAULT_SERVER_PLAYERS;
        this.players = new HashSet<>();
    }

    public void addPlayer(UUID player) {
        this.players.add(player);
    }

    public void removePlayer(UUID player) {
        this.players.remove(player);
    }

    public boolean hasPlayer(UUID player) {
        return this.players.contains(player);
    }

    public boolean isFull() {
        return this.players.size() >= this.maxPlayers;
    }

    public boolean isAvailable() {
        return !this.isFull();
    }

    public int getPlayerCount() {
        return this.players.size();
    }
    
}

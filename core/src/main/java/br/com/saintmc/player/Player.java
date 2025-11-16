package br.com.saintmc.player;

import java.util.UUID;

import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;

@Getter
public abstract class Player {

    private final UUID id;
    
    private final String name;

    private long firstSeen;
    private long lastSeen;
    private long totalOnlineTime;
    private boolean online;

    private String serverId;

    public Player(UUID id, String name) {
        this.id = id;
        this.name = name;
        this.serverId = null;
    }

    private long getCurrentOnlineTime() {
        if (this.online) {
            return System.currentTimeMillis() - this.firstSeen;
        }
        return 0L;
    }

    public long getTotalOnlineTime() {
        return this.totalOnlineTime + this.getCurrentOnlineTime();
    }
    
    public abstract void sendMessage(String message);

    public abstract void sendMessage(TextComponent message);
}

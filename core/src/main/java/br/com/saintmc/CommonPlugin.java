package br.com.saintmc;

import br.com.saintmc.manager.PlayerManager;
import br.com.saintmc.manager.PluginManager;
import br.com.saintmc.manager.ServerManager;
import lombok.Getter;

@Getter
public class CommonPlugin {

    protected final String serverSlug;
    protected final String serverAddress; 
    protected final int serverPort;

    protected final String pluginName;
    protected final String pluginVersion;

    protected final PlayerManager playerManager;
    protected final PluginManager pluginManager;
    protected final ServerManager serverManager;

    public CommonPlugin(String serverSlug, String serverAddress, int serverPort, String pluginName, String pluginVersion) {
        this.serverSlug = serverSlug;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.pluginName = pluginName;
        this.pluginVersion = pluginVersion;

        this.playerManager = new PlayerManager();
        this.pluginManager = new PluginManager();
        this.serverManager = new ServerManager();
    }
    
}

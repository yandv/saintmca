package br.com.saintmc;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.saintmc.plugin.Plugin;

public class BukkitMain extends JavaPlugin {

    private CommonPlugin commonPlugin;

    private boolean enabledSuccess = true;

    @Override
    public void onLoad() {
        this.commonPlugin = new CommonPlugin(
            this.getDescription().getName().toLowerCase(),
            this.getServer().getIp(),
            this.getServer().getPort(),
            this.getDescription().getName().toLowerCase(),
            this.getDescription().getVersion()
        );

        try {
            this.checkForUpdatesAsync();
        } catch (Exception e) {
            this.getLogger().severe("Erro ao verificar atualizações: " + e.getMessage());
            Bukkit.shutdown();
            enabledSuccess = false;
        }
        super.onLoad();
    }

    @Override
    public void onEnable() {
        if (!enabledSuccess) {
            return;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (!enabledSuccess) {
            return;
        }
        super.onDisable();
    }

    private void checkForUpdatesAsync() throws Exception {
        File currentFile = this.getFile();

        Plugin plugin = this.commonPlugin.getPluginManager().getPluginInfo(this.getDescription().getName().toLowerCase());

        if (plugin == null || plugin.getVersion() == null) {
            this.getLogger().info("Não foi possível verificar atualizações.");
            Bukkit.shutdown();
            return;
        }

        if (!plugin.isNewerVersionThan(this.getDescription().getVersion())) {
            this.getLogger().info("Plugin já está na versão mais recente.");
            return;
        }

        this.commonPlugin.getPluginManager().downloadAndReplace(this.getDescription().getName().toLowerCase(), currentFile);
    }
}
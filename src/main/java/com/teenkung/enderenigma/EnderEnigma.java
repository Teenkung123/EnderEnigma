package com.teenkung.enderenigma;

import com.teenkung.enderenigma.config.ConfigLoader;
import com.teenkung.enderenigma.manager.PlayerManager;
import com.teenkung.enderenigma.manager.SQLManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderEnigma extends JavaPlugin {

    private ConfigLoader configLoader;
    private SQLManager sqlManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
       this.configLoader = new ConfigLoader(this);
       this.sqlManager = new SQLManager(configLoader, this);
       this.playerManager = new PlayerManager(this);
    }

    @Override
    public void onDisable() {
        sqlManager.disconnect();
    }

    public SQLManager getSqlManager() { return sqlManager; }
    public ConfigLoader getConfigLoader() { return configLoader; }
    public PlayerManager getPlayerManager() { return playerManager; }
}

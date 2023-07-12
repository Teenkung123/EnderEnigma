package com.teenkung.enderenigma;

import com.teenkung.enderenigma.config.ConfigLoader;
import com.teenkung.enderenigma.manager.SQLManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderEnigma extends JavaPlugin {

    private ConfigLoader configLoader;
    private SQLManager sqlManager;

    @Override
    public void onEnable() {
       this.configLoader = new ConfigLoader(this);
       this.sqlManager = new SQLManager(configLoader, this);

    }

    @Override
    public void onDisable() {
        sqlManager.disconnect();
    }
}

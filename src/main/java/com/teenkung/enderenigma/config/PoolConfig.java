package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import org.bukkit.configuration.file.FileConfiguration;

public class PoolConfig {

    private final FileConfiguration config;
    private final EnderEnigma plugin;


    public PoolConfig(FileConfiguration config, EnderEnigma plugin) {
        this.config = config;
        this.plugin = plugin;
    }

}

package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.ItemBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class BannerConfig {

    private final EnderEnigma plugin;
    private final FileConfiguration config;
    private ItemBuilder key;

    public BannerConfig(FileConfiguration config, EnderEnigma plugin) {
        this.config = config;
        this.plugin = plugin;
        loadKey();
    }

    private void loadKey() {
        if (config.getBoolean("key.enable", false)) {
            ConfigurationSection keySection = config.getConfigurationSection("key");
            if (keySection != null) {
                try {
                    key = new ItemBuilder(keySection);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Could not load key item in file " + config.getName() + ". Disabling key System");
                    key = null;
                }
            } else {
                key = null;
            }
        } else {
            key = null;
        }
    }

}

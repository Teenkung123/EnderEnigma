package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PoolConfig {

    private final FileConfiguration config;
    private final EnderEnigma plugin;
    private final Map<String, SubPoolConfig> subPools = new HashMap<>();


    public PoolConfig(FileConfiguration config, EnderEnigma plugin) {
        this.config = config;
        this.plugin = plugin;

        ConfigurationSection section = config.getConfigurationSection("items");
        if (section == null) {
            try {
                throw new InvalidConfigurationException("File " + config.getName() + " does not valid!");
            } catch (InvalidConfigurationException e) {
                throw new RuntimeException(e);
            }
        }
        for (String id : section.getKeys(false)) {
            ConfigurationSection idSection = section.getConfigurationSection(id);
            if (idSection == null) {
                try {
                    throw new InvalidConfigurationException("File " + config.getName() + " does not valid!");
                } catch (InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }
            }
            SubPoolConfig subPoolConfig = new SubPoolConfig(idSection, plugin);
            subPools.put(id, subPoolConfig);
        }

    }


    public Set<String> getSubPoolList() { return subPools.keySet(); }
    public SubPoolConfig getSubPool(String id) { return subPools.get(id); }

}

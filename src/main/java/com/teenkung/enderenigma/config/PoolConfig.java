package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.PlayerDataManager;
import com.teenkung.enderenigma.utils.WeightedRandomSelector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PoolConfig {

    private final EnderEnigma plugin;
    private String id;
    private Map<String, SubPoolConfig> subPools = new HashMap<>();


    public PoolConfig(FileConfiguration config, EnderEnigma plugin) {
        this.plugin = plugin;
        this.id = config.getName().replace(".yml", "");
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

    public PoolConfig(EnderEnigma plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public Set<String> getSubPoolList() { return subPools.keySet(); }
    public SubPoolConfig getSubPool(String id) { return subPools.get(id); }
    public String getId() { return id; }
    public void addSubPool(SubPoolConfig subPool, String id) { this.subPools.put(id, subPool); }
    public void removeSubPool(String id) { this.subPools.remove(id); }
    public void setSubPool(Map<String, SubPoolConfig> map) { this.subPools = map; }

    public CompletableFuture<SubPoolConfig> requestPull(PlayerDataManager manager) {
        CompletableFuture<SubPoolConfig> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String selected = WeightedRandomSelector.select(subPools);
            future.complete(getSubPool(selected));
        });
        return future;
    }



}

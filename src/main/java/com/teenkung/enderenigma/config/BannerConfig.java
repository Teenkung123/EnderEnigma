package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.ItemBuilder;
import com.teenkung.enderenigma.manager.PlayerDataManager;
import com.teenkung.enderenigma.utils.ChanceRandomSelector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * This class is a Configuration Class for banner.yml file
 */
public class BannerConfig {

    private final EnderEnigma plugin;
    private final FileConfiguration config;
    private ItemBuilder key;
    private String uprate_pool;
    private Double uprate_increase;
    private Integer uprate_start;
    private final Map<String, Double> poolChance = new HashMap<>();

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
                }
            }
        }
    }

    private void loadUprate() {
        if (config.getBoolean("general.uprate.enable", false)) {
            uprate_pool = String.valueOf(Optional.ofNullable(config.getString("general.uprate.uprate-pool")));
            uprate_increase = config.getDouble("general.uprate.uprate-increase-chance", 0);
            uprate_start = config.getInt("general.uprate.uprate-start", 10000);
        } else {
            uprate_start = 0;
            uprate_increase = 0D;
        }
    }


    private void loadPools() {
        ConfigurationSection section = config.getConfigurationSection("pools");
        if (section != null) {
            for (String id : section.getKeys(false)) {
                poolChance.putIfAbsent(id, section.getDouble("", 0D));
                if (plugin.getConfigLoader().getPoolList().contains(id)) {
                    plugin.getLogger().info("Loaded pool " + id + " in " + config.getName() + ".");
                } else {
                    plugin.getLogger().warning("Could not find pool with ID " + id + "in " + config.getName() + ".");
                }
            }
        }
    }


    public ItemBuilder getKey() { return key; }

    public CompletableFuture<ArrayList<PoolConfig>> requestPulling(Integer amount, PlayerDataManager manager) {
        CompletableFuture<ArrayList<PoolConfig>> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String bannerID = config.getName().replace(".yml", "");
            ArrayList<PoolConfig> result = new ArrayList<>();

            for (int i = 1 ; i <= amount ; i++) {
                Map<String, Double> chance = new HashMap<>(poolChance);
                chance.put(uprate_pool, chance.get(uprate_pool) + Math.max(0, (manager.getCurrentPulls(bannerID)-uprate_start)*uprate_increase));

                String selected = ChanceRandomSelector.selectByChance(chance);

                if (selected != null) {
                    if (selected.equals(uprate_pool)) {
                        manager.setCurrentRolls(bannerID, 0);
                    }
                }
                manager.setTotalRolls(bannerID, manager.getTotalPulls(bannerID)+1);
                result.add(plugin.getConfigLoader().getPoolConfig(selected));
            }

            manager.saveData();
            future.complete(result);
        });
        return future;
    }

}

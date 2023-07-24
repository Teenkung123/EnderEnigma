package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.manager.ItemBuilder;
import com.teenkung.enderenigma.manager.PlayerDataManager;
import com.teenkung.enderenigma.utils.ChanceRandomSelector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.io.IOException;
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
    private Boolean uprate;
    private String uprate_pool;
    private Double uprate_increase;
    private Integer uprate_start;
    private Map<String, Double> poolChance = new HashMap<>();

    public BannerConfig(FileConfiguration config, EnderEnigma plugin) {
        this.config = config;
        this.plugin = plugin;
        loadKey();
        loadPools();
        loadUprate();
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
            uprate = true;
            uprate_pool = String.valueOf(Optional.ofNullable(config.getString("general.uprate.uprate-pool")));
            uprate_increase = config.getDouble("general.uprate.uprate-increase-chance", 0);
            uprate_start = config.getInt("general.uprate.uprate-start", 10000);
        } else {
            uprate = false;
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
    public Boolean getUprate() { return uprate; }
    public String getUpratePool() { return uprate_pool; }
    public Double getUprateIncrease() { return uprate_increase; }
    public Integer getUprateStart() { return uprate_start; }

    public void setKey(ItemBuilder key) { this.key = key; }
    public void setUprate(Boolean status) { this.uprate = status; }
    public void setUpratePool(String upratePool) { this.uprate_pool = upratePool; }
    public void setUprateIncrease(Double chance) { this.uprate_increase = chance; }
    public void setUprateStart(Integer start) { this.uprate_start = start; }

    public void addPool(String id, Double chance) { this.poolChance.put(id, chance); }
    public void removePool(String id) { this.poolChance.remove(id); }
    public void setPool(Map<String, Double> map) { this.poolChance = map; }

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

    public void save() throws IOException {
        // Save key related data
        ConfigurationSection keySection = config.getConfigurationSection("key");
        if (keySection != null && key != null) {
            keySection.set("type", key.getType());
            keySection.set("id", key.getId());
            keySection.set("amount", key.getAmount());
            keySection.set("modelData", key.getModelData() != 0 ? key.getModelData() : null);
            keySection.set("displayName", key.getDisplayName() != null ? key.getDisplayName() : null);
            if (key.getLore() != null && !key.getLore().isEmpty()) {
                keySection.set("lore", key.getLore());
            }
            if (key.getEnchants() != null && !key.getEnchants().isEmpty()) {
                ConfigurationSection enchantsSection = keySection.getConfigurationSection("enchants");
                if (enchantsSection == null) {
                    enchantsSection = keySection.createSection("enchants");
                }
                for (Map.Entry<Enchantment, Integer> entry : key.getEnchants().entrySet()) {
                    enchantsSection.set(entry.getKey().getKey().toString(), entry.getValue());
                }
            }
            keySection.set("enable", true);
        }

        // Save uprate related data
        ConfigurationSection uprateSection = config.getConfigurationSection("general.uprate");
        if (uprateSection != null) {
            uprateSection.set("enable", uprate);
            uprateSection.set("uprate-pool", uprate_pool);
            uprateSection.set("uprate-increase-chance", uprate_increase);
            uprateSection.set("uprate-start", uprate_start);
        }

        // Save pool chances
        ConfigurationSection poolsSection = config.getConfigurationSection("pools");
        if (poolsSection != null) {
            for (Map.Entry<String, Double> entry : poolChance.entrySet()) {
                poolsSection.set(entry.getKey(), entry.getValue());
            }
        }

        // Save the config to file
        config.save(plugin.getDataFolder() + "/banners/" + config.getName());
    }
}

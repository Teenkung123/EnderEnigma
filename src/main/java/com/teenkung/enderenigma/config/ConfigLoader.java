package com.teenkung.enderenigma.config;

import com.teenkung.enderenigma.EnderEnigma;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigLoader {

    private final EnderEnigma plugin;
    private final Map<String, BannerConfig> bannerList;
    private final Map<String, PoolConfig> poolList;
    private final DatabaseConfig databaseConfig;

    public ConfigLoader(EnderEnigma plugin) {
        this.plugin = plugin;
        checkConfig();
        this.databaseConfig = loadDatabaseConfig();
        this.poolList = loadConfigMap("pools", PoolConfig::new);
        this.bannerList = loadConfigMap("banners", BannerConfig::new);
    }

    private void checkConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
    }

    private DatabaseConfig loadDatabaseConfig() {
        FileConfiguration config = plugin.getConfig();
        return new DatabaseConfig(
                config.getString("Database.Host"),
                config.getString("Database.Username"),
                config.getString("Database.Password"),
                config.getString("Database.Port"),
                config.getString("Database.Table_prefix"),
                config.getString("Database.Database")
        );
    }

    private <T> Map<String, T> loadConfigMap(String folderName, ConfigConstructor<T> constructor) {
        File folder = new File(plugin.getDataFolder(), folderName);
        if (!folder.exists() && !folder.mkdir()) {
            plugin.getLogger().severe("Could not create " + folderName + " folder, This may cause huge effect on the plugin!");
            return new HashMap<>();
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        Map<String, T> configMap = new HashMap<>();
        if (files != null) {
            for (File file : files) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String id = config.getName().replace(".yml", "");
                configMap.put(id, constructor.construct(config, plugin));
            }
        } else {
            plugin.getLogger().warning("No .yml found in " + folderName + " folder");
        }

        return configMap;
    }

    public DatabaseConfig getDatabaseConfig() { return databaseConfig; }
    public Set<String> getBannerList() { return bannerList.keySet(); }
    public Set<String> getPoolList() { return poolList.keySet(); }
    public BannerConfig getBannerConfig(String bannerID) { return bannerList.get(bannerID); }
    public PoolConfig getPoolConfig(String poolID) { return poolList.get(poolID); }


}

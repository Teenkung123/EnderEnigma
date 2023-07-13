package com.teenkung.enderenigma.manager;

import com.teenkung.enderenigma.EnderEnigma;
import com.teenkung.enderenigma.config.SubPoolConfig;
import com.teenkung.enderenigma.utils.ItemSerialization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlayerDataManager {

    private Player player;
    private EnderEnigma plugin;
    private Map<String, Integer> totalRolls = new HashMap<>();
    private Map<String, Integer> currentRolls = new HashMap<>();


    public PlayerDataManager(Player player, EnderEnigma plugin) {
        this.player = player;
        this.plugin = plugin;

        loadPlayerData();
    }

    public void loadPlayerData() {
        SQLManager manager = plugin.getSqlManager();
        Connection connection = manager.getConnection();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT TotalRolls, CurrentRolls FROM " + manager.getDataTableName() + " WHERE UUID = ? AND BannerID = ?;");
                for (String id : plugin.getConfigLoader().getBannerList()) {
                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, id);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            this.totalRolls.put(id, rs.getInt("TotalRolls"));
                            this.currentRolls.put(id, rs.getInt("CurrentRolls"));
                        } else {
                            this.totalRolls.put(id, 0);
                            this.currentRolls.put(id, 0);
                            // Insert new player data into the database with 0 values
                            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO " + manager.getDataTableName() + " (UUID, BannerID, CurrentRoll, TotalRolls) VALUES (?, ?, ?, ?)");
                            insertStatement.setString(1, player.getUniqueId().toString());
                            insertStatement.setString(2, id);
                            insertStatement.setInt(3, 0);
                            insertStatement.setInt(4, 0);
                            insertStatement.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /**
     * Adds multiple ItemStack into the logs without executing multiple sql command
     * @param bannerID Banner ID of the target logs
     * @param stacks ItemStack
     */
    public void addLogs(String bannerID, ArrayList<ItemStack> stacks) {
        SQLManager manager = plugin.getSqlManager();
        Connection connection = manager.getConnection();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String query = "INSERT INTO " + manager.getLogsTableName() + "_logs (UUID, BannerID, Item, Date) VALUES (?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);

                for (ItemStack stack : stacks) {
                    String itemSerialized = ItemSerialization.serializeItemStack(stack);

                    statement.setString(1, player.getUniqueId().toString());
                    statement.setString(2, bannerID);
                    statement.setString(3, itemSerialized);
                    statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                    statement.addBatch();
                }

                statement.executeBatch();

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Add a data to logs table
     * @param bannerID bannerID
     * @param stack ItemStack
     */
    public void addLog(String bannerID, ItemStack stack) {
        SQLManager manager = plugin.getSqlManager();
        Connection connection = manager.getConnection();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String query = "INSERT INTO " + manager.getLogsTableName() + "_logs (UUID, BannerID, Item, Date) VALUES (?, ?, ?, ?);";
                PreparedStatement statement = connection.prepareStatement(query);

                String itemSerialized = ItemSerialization.serializeItemStack(stack);

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, bannerID);
                statement.setString(3, itemSerialized);
                statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

                statement.execute();

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }



    public Integer getCurrentPulls(String bannerID) { return currentRolls.get(bannerID); }
    public Integer getTotalPulls(String bannerID) { return totalRolls.get(bannerID); }
    public void setCurrentRolls(String bannerID, Integer amount) { currentRolls.put(bannerID, amount); }
    public void setTotalRolls(String bannerID, Integer amount) { totalRolls.put(bannerID, amount); }

    public void saveData() {
        SQLManager manager = plugin.getSqlManager();
        Connection connection = manager.getConnection();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String query = "UPDATE " + manager.getDataTableName() + " SET CurrentRolls = ?, TotalRolls = ? WHERE UUID = ? AND BannerID = ?;";
                PreparedStatement statement = connection.prepareStatement(query);

                for (String id : plugin.getConfigLoader().getBannerList()) {
                    if (this.totalRolls.containsKey(id) && this.currentRolls.containsKey(id)) {
                        statement.setInt(1, this.currentRolls.get(id));
                        statement.setInt(2, this.totalRolls.get(id));
                        statement.setString(3, player.getUniqueId().toString());
                        statement.setString(4, id);
                        statement.executeUpdate();
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


}

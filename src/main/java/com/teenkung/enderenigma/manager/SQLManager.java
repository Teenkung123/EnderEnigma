package com.teenkung.enderenigma.manager;

import com.teenkung.enderenigma.config.ConfigLoader;
import com.teenkung.enderenigma.EnderEnigma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLManager {

    private Connection connection;
    private final ConfigLoader config;
    private final EnderEnigma plugin;

    public SQLManager(ConfigLoader config, EnderEnigma plugin) {
        this.config = config;
        this.plugin = plugin;
        plugin.getLogger().info("Connecting to MySQL Database. . .");
        connect();
        createDataTable();
        createLogsTable();
    }

    /**
     * Connect to MySQL database.
     */
    private void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" +
                            config.getDatabaseConfig().getHost()+":" +
                            config.getDatabaseConfig().getPort()+"/" +
                            config.getDatabaseConfig().getDatabase()+"?useSSL=false&autoReconnect=true",
                    config.getDatabaseConfig().getUsername(),
                    config.getDatabaseConfig().getPassword()
            );
            plugin.getLogger().info("Connected to MySQL Database.");
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to connect to database. Please check your configuration.");
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDataTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + config.getDatabaseConfig().getTablePrefix() + "_data (" +
                "  ID INT AUTO_INCREMENT," +
                "  UUID VARCHAR(100)," +
                "  BannerID VARCHAR(100)," +
                "  CurrentRoll INT," +
                "  TotalRolls INT," +
                "  PRIMARY KEY (ID)" +
                ");";
        executeSQL(sql);
    }

    public void createLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + config.getDatabaseConfig().getTablePrefix() + "_logs (" +
                "  ID INT AUTO_INCREMENT," +
                "  UUID VARCHAR(100)," +
                "  BannerID VARCHAR(100)," +
                "  Item LONGTEXT," +
                "  Date DATETIME," +
                "  PRIMARY KEY (ID)" +
                ");";
        executeSQL(sql);
    }

    private void executeSQL(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL", e);
        }
    }

    public String getDataTableName() { return config.getDatabaseConfig().getTablePrefix() + "_data"; }
    public String getLogsTableName() { return config.getDatabaseConfig().getTablePrefix() + "_logs"; }
}

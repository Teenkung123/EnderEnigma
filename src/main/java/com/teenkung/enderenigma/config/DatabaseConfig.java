package com.teenkung.enderenigma.config;

public class DatabaseConfig {

    private String host;
    private String username;
    private String password;
    private String port;
    private String tablePrefix;
    private String database;

    public DatabaseConfig(String host, String username, String password, String port, String tablePrefix, String database) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.tablePrefix = tablePrefix;
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPort() {
        return port;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getDatabase() {
        return database;
    }
}


package org.example.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseFactory {
    private String driverName;
    private String serverName;
    private String database;
    private String url;
    private String username;
    private String password;
    private static DatabaseFactory instance = null;
    private DatabaseFactory() {
        driverName = "com.mysql.cj.jdbc.Driver";
        serverName = "localhost";
        database = "garden_city";
        url = "jdbc:mysql://" + serverName + "/" + database;
        username = "root";
        password = "lynn148388";
    }

    public static DatabaseFactory getInstance() {
        if(instance == null) {
            instance = new DatabaseFactory();
            return instance;
        }
        return instance;
    }
    public Connection getConnection() throws Exception {
        Class.forName(driverName);
        return DriverManager.getConnection(url, username, password);
    }
}

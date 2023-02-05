package me.chaotisch3r.lobby.mysql;

import me.chaotisch3r.lobby.Lobby;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:15 - 20.04.2022
 **/

public class MySQL {

    private final FileConfiguration configuration = Lobby.getInstance().getConfig();
    private final String prefix = Lobby.getInstance().getPrefix();
    private Connection connection;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private boolean useDafaultPort;

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() {
        if(isConnected())
            return;
        String url = "jdbc:mysql://";
        String end_url = "?autoReconnect=true&useSSL=false";
        readInput();
        if(useDafaultPort) {
            url = url + host + ":3306/" + database + end_url;
        } else url = url + host + ":" + port + "/" + database + end_url;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void disconnect() {
        if(!isConnected())
            return;
        try {
            connection.close();
        } catch (SQLException ex) {
            System.out.println(prefix + "§cCouldn't close connection from Database: Reason='" + ex.getMessage() + "'");
        }
    }

    public PreparedStatement getStatement(String sql) {
        if(!isConnected())
            return null;
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ps;
    }

    public void readInput() {
        useDafaultPort = configuration.getBoolean("MySQL.useDefaultPort");
        host = configuration.getString("MySQL.host");
        port = configuration.getInt("MySQL.port");
        database = configuration.getString("MySQL.database");
        user = configuration.getString("MySQL.user");
        password = configuration.getString("MySQL.password");
    }

}

package me.chaotisch3r.lobby.database;

import lombok.SneakyThrows;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.LobbyData;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 12:05 - 20.04.2022
 **/

public class LobbyDataManager {

    private final MySQL mySQL = Lobby.getInstance().getMySQL();
    private Map<UUID, LobbyData> lobbyCache = new HashMap<>();

    public void registerLobby() {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            if (!mySQL.isConnected()) mySQL.connect();
            try (PreparedStatement ps = mySQL.getStatement("CREATE TABLE IF NOT EXISTS lobby_data(`id` int NOT NULL AUTO_INCREMENT," +
                    " `uuid` varchar(64) NOT NULL, `lastConnected` varchar(32) DEFAULT NULL, `color` varchar(16) NOT NULL DEFAULT 'LIME'," +
                    " `respawnType` enum('SPAWN','LOGOUT') NOT NULL DEFAULT 'SPAWN', `playtime` int NOT NULL DEFAULT '0',PRIMARY KEY (`id`)," +
                    " UNIQUE KEY `uuid_UNIQUE` (`uuid`))")) {
                ps.executeUpdate();
            }catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @SneakyThrows
    public void loadLobby(UUID uuid) {
        if(!mySQL.isConnected())
            mySQL.connect();
        PreparedStatement preparedStatement = mySQL.getStatement("SELECT * FROM lobby_data WHERE uuid=?");
        ResultSet resultSet = null;
        try {
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();

            LobbyData lobbyData;
            if(resultSet.next()) {
                lobbyCache.put(uuid, (lobbyData = new LobbyData(uuid, resultSet.getString("lastConnected"), DyeColor.valueOf(resultSet.getString("color")),
                        resultSet.getString("respawnType").equals("SPAWN"), resultSet.getLong("playtime"), System.currentTimeMillis())));
            }else {
                lobbyCache.put(uuid, (lobbyData = new LobbyData(uuid, "LOBBY", DyeColor.LIME,
                        true, 0, System.currentTimeMillis())));
                updateAsync(lobbyData);
            }
            if (lobbyData.getPlaytime() != 0)
                updateAsync(lobbyData);
        }catch(SQLException ex) {
            ex.printStackTrace();
        }finally {
            if(resultSet != null)
                resultSet.close();
            preparedStatement.close();
        }
    }

    public void unloadPlayer(UUID uuid) {
        lobbyCache.remove(uuid);
    }

    public void updateAsync(LobbyData lobbyData) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> update(lobbyData));
    }

    private void update(LobbyData lobbyData) {
        if (!mySQL.isConnected()) mySQL.connect();
        try (PreparedStatement preparedStatement = mySQL.getStatement("INSERT INTO lobby_data(`uuid`, `lastConnected`, `color`, `respawnType`, `playtime`) " +
                "VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE `lastConnected`=?, `color`=?, `respawnType`=?, `playtime`=?")) {
            preparedStatement.setString(1, lobbyData.getUuid().toString());
            preparedStatement.setString(2, lobbyData.getLastConnectedServer());
            preparedStatement.setString(3, lobbyData.getColor().toString());
            preparedStatement.setString(4, lobbyData.isRespawnOnSpawn() ? "SPAWN" : "LOBBY");
            preparedStatement.setLong(5, lobbyData.getPlaytime());

            preparedStatement.setString(6, lobbyData.getLastConnectedServer());
            preparedStatement.setString(7, lobbyData.getColor().toString());
            preparedStatement.setString(8, lobbyData.isRespawnOnSpawn() ? "SPAWN" : "LOBBY");
            preparedStatement.setLong(9, lobbyData.getPlaytime());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public LobbyData getLobby(UUID uuid) {
        return lobbyCache.get(uuid);
    }

}

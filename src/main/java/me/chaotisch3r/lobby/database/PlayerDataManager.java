package me.chaotisch3r.lobby.database;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:31 - 20.04.2022
 **/

@RequiredArgsConstructor
public class PlayerDataManager {

    private final MySQL mySQL;
    private final Map<UUID, PlayerData> playerCache;

    private final RankDataManager rankDataManager;

    public PlayerDataManager(MySQL mySQl, RankDataManager rankDataManager) {
        this.mySQL = mySQl;
        this.playerCache = new HashMap<>();
        this.rankDataManager = rankDataManager;
    }

    public void registerPlayer() {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            if(!mySQL.isConnected()) mySQL.connect();
            try (PreparedStatement preparedStatement = mySQL.getStatement("CREATE TABLE IF NOT EXISTS player_data(" +
                    "`id` int NOT NULL AUTO_INCREMENT, `uuid` varchar(64) NOT NULL, `name` varchar(16) NOT NULL," +
                    " `ipAddress` varchar(45) DEFAULT NULL, `rank` varchar(32) NOT NULL DEFAULT 'Player'," +
                    " `locale` varchar(4) NOT NULL DEFAULT 'en', `coins` int NOT NULL DEFAULT '0', PRIMARY KEY (`id`)," +
                    " UNIQUE KEY `uuid_UNIQUE` (`uuid`))")) {
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @SneakyThrows
    public void loadPlayer(UUID uuid, String name, String address) {
        if(!mySQL.isConnected()) mySQL.connect();
        PreparedStatement preparedStatement = mySQL.getStatement("SELECT * FROM player_data WHERE `uuid`=?");
        ResultSet resultSet = null;
        try {
            preparedStatement.setString(1, uuid.toString());
            resultSet = preparedStatement.executeQuery();
            PlayerData playerData;
            if(resultSet.next()) {
                playerCache.put(uuid, (playerData = new PlayerData(uuid, name, address,
                        rankDataManager.getRank(resultSet.getString("rank")),
                        Locale.forLanguageTag(resultSet.getString("locale")),
                        resultSet.getInt("coins"))));
            } else {
                playerCache.put(uuid, (playerData = new PlayerData(uuid, name, address,
                        rankDataManager.getRank("player"), Locale.ENGLISH, 0)));
                updateAsync(playerData);
            }
            if(!playerData.getUuid().equals(uuid) || !playerData.getName().equals(name) || !playerData.getIpAddress().equals(address))
                updateAsync(playerData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if(resultSet != null) resultSet.close();
            preparedStatement.close();
        }
    }

    public void setRank(UUID uuid, String rankName) {
        if(!mySQL.isConnected()) mySQL.connect();
        PlayerData playerData = getOfflinePlayer(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
           try (PreparedStatement ps = mySQL.getStatement("UPDATE `player_data` SET `rank`=? WHERE `uuid`=?")) {
               ps.setString(1, rankName);
               ps.setString(2, uuid.toString());

               ps.executeUpdate();
           } catch (SQLException e) {
               throw new RuntimeException(e);
           }
        });
        if(playerCache.containsKey(uuid)) playerCache.replace(uuid, new PlayerData(uuid, playerData.getName(), playerData.getIpAddress(),
                rankDataManager.getRank(rankName), playerData.getLocale(), playerData.getCoins()));
    }

    public void unloadPlayer(UUID uuid) {
        playerCache.remove(uuid);
    }

    public void updateAsync(PlayerData playerData) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> update(playerData));
    }

    public void update(PlayerData playerData) {
        if(!mySQL.isConnected()) mySQL.connect();
        try (PreparedStatement preparedStatement = mySQL.getStatement("INSERT INTO player_data(`uuid`, `name`, `ipAddress`," +
                " `rank`, `locale`, `coins`) VALUES(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `name`=?, `ipAddress`=?, `rank`=?," +
                " `locale`=?, `coins`=?")) {
            preparedStatement.setString(1, playerData.getUuid().toString());
            preparedStatement.setString(2, playerData.getName());
            preparedStatement.setString(3, playerData.getIpAddress());
            preparedStatement.setString(4, playerData.getRank().getRankName());
            preparedStatement.setString(5, playerData.getLocale().toLanguageTag());
            preparedStatement.setInt(6, playerData.getCoins());

            preparedStatement.setString(7, playerData.getName());
            preparedStatement.setString(8, playerData.getIpAddress());
            preparedStatement.setString(9, playerData.getRank().getRankName());
            preparedStatement.setString(10, playerData.getLocale().toLanguageTag());
            preparedStatement.setInt(11, playerData.getCoins());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(!playerCache.containsKey(playerData.getUuid())) return;
        playerCache.replace(playerData.getUuid(), playerData);
    }

    public PlayerData getPlayer(UUID uuid) {
        return playerCache.get(uuid);
    }

    @SneakyThrows
    public PlayerData getOfflinePlayer(UUID uuid) {
        if(!mySQL.isConnected()) mySQL.connect();
        PlayerData playerData;
        PreparedStatement ps = mySQL.getStatement("SELECT * FROM player_data WHERE `uuid`=?");
        ResultSet rs;
        try {
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if(!rs.next()) return null;
            playerData = new PlayerData(uuid, rs.getString("name"), rs.getString("ipAddress"),
                    rankDataManager.getRank(rs.getString("rank")), Locale.forLanguageTag(rs.getString("locale")),
                    rs.getInt("coins"));
            return playerData;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

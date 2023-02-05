package me.chaotisch3r.lobby.database;

import lombok.SneakyThrows;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.SettingsData;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;

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
 * Created for Lobby-System, 18:31 28.05.2022
 **/

public class SettingsDataManager {
    
    private final Map<UUID, SettingsData> settingsCache = new HashMap<>();
    private final MySQL mySQL = Lobby.getInstance().getMySQL();
    
    public void registerSettings() {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            if(!mySQL.isConnected()) mySQL.connect();
            try (PreparedStatement ps = mySQL.getStatement("CREATE TABLE IF NOT EXISTS `settings_data` ( `id` INT NOT NULL AUTO_INCREMENT," +
                    " `uuid` VARCHAR(36) NOT NULL, `hiderStatus` VARCHAR(10) NOT NULL DEFAULT 'ALL', `getCoins` TINYINT NOT NULL DEFAULT 1, `friendRequest` TINYINT NOT NULL DEFAULT 1," +
                    " `friendJoin` TINYINT NOT NULL DEFAULT 1, `pm` TINYINT NOT NULL DEFAULT 1, `partyRequest` TINYINT NOT NULL DEFAULT 1," +
                    " `partyJump` TINYINT NOT NULL DEFAULT 1, `clanRequest` TINYINT NOT NULL DEFAULT 1, `clanJoin` TINYINT NOT NULL DEFAULT 1," +
                    " PRIMARY KEY (`id`), UNIQUE INDEX `uuid_UNIQUE` (`uuid`))")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    public void loadSetting(UUID uuid) {
        if(!mySQL.isConnected()) mySQL.connect();
        PreparedStatement ps = mySQL.getStatement("SELECT * FROM `settings_data` WHERE `uuid`=?");
        ResultSet rs = null;
        try {
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            
            SettingsData settingsData;
            if(rs.next()) {
                settingsCache.put(uuid, (settingsData = new SettingsData(uuid, rs.getString("hiderStatus"),
                        rs.getBoolean("getCoins"), rs.getBoolean("friendRequest"),
                        rs.getBoolean("friendJoin"), rs.getBoolean("pm"),
                        rs.getBoolean("partyRequest"), rs.getBoolean("partyJump"),
                        rs.getBoolean("clanRequest"), rs.getBoolean("clanJoin"))));
            }
            else {
                settingsCache.put(uuid, (settingsData = new SettingsData(uuid, "ALL", true, true,
                        true, true, true, true, true, true)));
                updateAsync(settingsData);
            }
            if(!settingsData.getUuid().equals(uuid)) updateAsync(settingsData);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if(rs != null) rs.close();
            ps.close();
        }
    }
    
    public void updateAsync(SettingsData settingsData) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> update(settingsData));
    }
    
    public void update(SettingsData settingsData) {
        if(!mySQL.isConnected()) mySQL.connect();
        try (PreparedStatement ps = mySQL.getStatement("INSERT INTO `settings_data`(`uuid`, `hiderStatus`, `getCoins`, `friendRequest`," +
                " `friendJoin`, `pm`, `partyRequest`, `partyJump`, `clanRequest`, `clanJoin`)" +
                " VALUES(?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `hiderStatus`=?, `getCoins`=?, `friendRequest`=?, `friendJoin`=?, " +
                "`pm`=?, `partyRequest`=?, `partyJump`=?, `clanRequest`=?, `clanJoin`=?")) {
            ps.setString(1, settingsData.getUuid().toString());
            ps.setString(2, settingsData.getHiderStatus());
            ps.setBoolean(3, settingsData.isGetCoins());
            ps.setBoolean(4, settingsData.isFriendRequest());
            ps.setBoolean(5, settingsData.isFriendJoin());
            ps.setBoolean(6, settingsData.isPm());
            ps.setBoolean(7, settingsData.isPartyRequest());
            ps.setBoolean(8, settingsData.isPartyJump());
            ps.setBoolean(9, settingsData.isClanRequest());
            ps.setBoolean(10, settingsData.isClanJoin());

            ps.setString(11, settingsData.getHiderStatus());
            ps.setBoolean(12, settingsData.isGetCoins());
            ps.setBoolean(13, settingsData.isFriendRequest());
            ps.setBoolean(14, settingsData.isFriendJoin());
            ps.setBoolean(15, settingsData.isPm());
            ps.setBoolean(16, settingsData.isPartyRequest());
            ps.setBoolean(17, settingsData.isPartyJump());
            ps.setBoolean(18, settingsData.isClanRequest());
            ps.setBoolean(19, settingsData.isClanJoin());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SettingsData getSettings(UUID uuid) {
        return settingsCache.get(uuid);
    }

}

package me.chaotisch3r.lobby.database;

import lombok.SneakyThrows;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.WarpData;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 14:14 02.05.2022
 **/

public class WarpDataManager {

    private final MySQL mySQL = Lobby.getInstance().getMySQL();
    private final Map<String, WarpData> warpCache = new HashMap<>();

    public void registerWarp() {
        if (mySQL.isConnected())
            mySQL.connect();
        try (PreparedStatement ps = mySQL.getStatement("CREATE TABLE IF NOT EXISTS warp_data(" +
                "`id` INT NOT NULL AUTO_INCREMENT, `warpName` VARCHAR(32) NOT NULL, `worldUID` VARCHAR(64) NOT NULL, " +
                "`x` DOUBLE NOT NULL, `y` DOUBLE NOT NULL, `z` DOUBLE NOT NULL, `yaw` FLOAT NOT NULL, " +
                "`pitch` FLOAT NOT NULL, PRIMARY KEY (`id`), UNIQUE INDEX `warpName_UNIQUE` (`warpName`))")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void loadWarp(String warpName, World world, double x, double y, double z, float yaw, float pitch) {
        if (!mySQL.isConnected()) mySQL.connect();
        PreparedStatement ps = mySQL.getStatement("SELECT * FROM warp_data WHERE warpName=?");
        ResultSet rs = null;
        try {
            ps.setString(1, warpName);
            rs = ps.executeQuery();

            WarpData warpData;
            if (rs.next()) {
                warpCache.put(warpName, (warpData = new WarpData(warpName, UUID.fromString(rs.getString("worldUID")),
                        rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"),
                        rs.getFloat("yaw"), rs.getFloat("pitch"))));
            } else {
                warpCache.put(warpName, (warpData = new WarpData(warpName, world.getUID(), x, y, z, yaw, pitch)));
                updateAsync(warpData);
            }
            if (!warpData.getWarpName().equals(warpName)) updateAsync(warpData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    public void loadWarp(String warpName, Location location) {
        loadWarp(warpName, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public void removeWarp(String warpName) {
        if (!warpCache.containsKey(warpName)) return;
        unloadWarp(warpName);
        try (PreparedStatement ps = mySQL.getStatement("DELETE FROM warp_Data WHERE warpName=?")) {
            ps.setString(1, warpName);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unloadWarp(String warpName) {
        warpCache.remove(warpName);
    }

    public void updateAsync(WarpData warpData) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> update(warpData));
    }

    public void update(WarpData warpData) {
        if (!mySQL.isConnected()) mySQL.connect();
        try (PreparedStatement preparedStatement = mySQL.getStatement("INSERT INTO warp_data(`warpName`, `worldUID`, `X`, `Y`, `Z`, `Yaw`, `Pitch`) " +
                "VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `X`=?, `Y`=?, `Z`=?, `Yaw`=?, `Pitch`=?")) {
            preparedStatement.setString(1, warpData.getWarpName());
            preparedStatement.setString(2, warpData.getWorldUID().toString());
            preparedStatement.setDouble(3, warpData.getX());
            preparedStatement.setDouble(4, warpData.getY());
            preparedStatement.setDouble(5, warpData.getZ());
            preparedStatement.setFloat(6, warpData.getYaw());
            preparedStatement.setFloat(7, warpData.getPitch());

            preparedStatement.setDouble(8, warpData.getX());
            preparedStatement.setDouble(9, warpData.getY());
            preparedStatement.setDouble(10, warpData.getZ());
            preparedStatement.setFloat(11, warpData.getYaw());
            preparedStatement.setFloat(12, warpData.getPitch());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void renameWarp(String oldWarpName, String newWarpName) {
        if (!warpCache.containsKey(oldWarpName)) return;
        WarpData oldWarpData = warpCache.get(oldWarpName);
        Location location = new Location(Bukkit.getWorld(oldWarpData.getWorldUID()), oldWarpData.getX(), oldWarpData.getY(),
                oldWarpData.getZ(), oldWarpData.getYaw(), oldWarpData.getPitch());
        removeWarp(oldWarpName);
        loadWarp(newWarpName, location);
    }

    public WarpData getWarp(String warpName) {
        return warpCache.get(warpName);
    }

    public Location getWarpLocation(String warpName) {
        if(!warpCache.containsKey(warpName)) return null;
        WarpData warpData = warpCache.get(warpName);
        return new Location(Bukkit.getWorld(warpData.getWorldUID()), warpData.getX(), warpData.getY(), warpData.getZ(),
                warpData.getYaw(), warpData.getPitch());
    }

    public List<WarpData> getWarps() {
        return warpCache.values().stream().toList();
    }

}

package me.chaotisch3r.lobby.database;

import lombok.Getter;
import lombok.SneakyThrows;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.WorldData;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
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
 * Created for Lobby-System, 13:23 23.04.2022
 **/

@Getter
public class WorldDataManager {

    private final MySQL mySQL = new MySQL();
    private final File file;
    private final Map<UUID, WorldData> worldCache = new HashMap<>();

    public WorldDataManager() {
        file = new File(Lobby.getInstance().getDataFolder(), "DeletedWorlds");
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void addWorldToDeletedList(World world) {
        File worldFile = new File(Lobby.getInstance().getDataFolder() + "/DeletedWorlds", world.getName());
        if (!worldFile.exists()) {
            world.getWorldFolder().renameTo(worldFile);
        }
    }

    public void registerWorld() {
        if (!mySQL.isConnected()) mySQL.connect();
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            try (PreparedStatement ps = mySQL.getStatement("CREATE TABLE IF NOT EXISTS world_data(`id` int NOT NULL AUTO_INCREMENT, `uid` varchar(64) NOT NULL," +
                    " `worldName` varchar(16) NOT NULL, `X` double NOT NULL, `Y` double NOT NULL, `Z` double NOT NULL," +
                    " `yaw` float NOT NULL, `pitch` float NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `uid_UNIQUE` (`uid`))")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    private void worldLoading(World world) {
        if (!mySQL.isConnected()) mySQL.connect();
        PreparedStatement ps = mySQL.getStatement("SELECT * FROM world_data WHERE uid=?");
        ResultSet rs = null;
        try {
            ps.setString(1, world.getUID().toString());
            rs = ps.executeQuery();

            WorldData worldData;
            if (rs.next()) {
                worldCache.put(world.getUID(), (worldData = new WorldData(world.getUID(), rs.getString("worldName"),
                        rs.getDouble("X"), rs.getDouble("Y"), rs.getDouble("Z"),
                        rs.getFloat("Yaw"), rs.getFloat("Pitch"))));
            } else {
                worldCache.put(world.getUID(), (worldData = new WorldData(world.getUID(), world.getName(),
                        world.getSpawnLocation().getX(), world.getSpawnLocation().getY(), world.getSpawnLocation().getZ(),
                        world.getSpawnLocation().getYaw(), world.getSpawnLocation().getPitch())));
                updateAsync(worldData);
            }
            if (!worldData.getUid().equals(world.getUID())) updateAsync(worldData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) rs.close();
            ps.close();
        }
    }

    public void loadWorld(World world) {
        worldLoading(world);
    }

    public void loadWorld(List<World> worlds) {
        worlds.forEach(this::loadWorld);
    }

    public void removeWorld(World world) {
        if (worldCache.containsKey(world.getUID())) unloadWorld(world);
        try (PreparedStatement ps = mySQL.getStatement("DELETE FROM world_data WHERE uid=?")) {
            ps.setString(1, world.getUID().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unloadWorld(World world) {
        worldCache.remove(world.getUID());
    }

    public void updateAsync(WorldData worldData) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> update(worldData));
    }

    public void update(WorldData worldData) {
        if (!mySQL.isConnected()) mySQL.connect();
        try (PreparedStatement preparedStatement = mySQL.getStatement("INSERT INTO world_data(`uid`, `worldName`, `X`, `Y`, `Z`, `Yaw`, `Pitch`) " +
                "VALUES(?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE `worldName`=?, `X`=?, `Y`=?, `Z`=?, `Yaw`=?, `Pitch`=?")) {
            preparedStatement.setString(1, worldData.getUid().toString());
            preparedStatement.setString(2, worldData.getWorldName());
            preparedStatement.setDouble(3, worldData.getX());
            preparedStatement.setDouble(4, worldData.getY());
            preparedStatement.setDouble(5, worldData.getZ());
            preparedStatement.setFloat(6, worldData.getYaw());
            preparedStatement.setFloat(7, worldData.getPitch());

            preparedStatement.setString(8, worldData.getWorldName());
            preparedStatement.setDouble(9, worldData.getX());
            preparedStatement.setDouble(10, worldData.getY());
            preparedStatement.setDouble(11, worldData.getZ());
            preparedStatement.setFloat(12, worldData.getYaw());
            preparedStatement.setFloat(13, worldData.getPitch());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public WorldData getWorld(World world) {
        return worldCache.get(world.getUID());
    }

    public List<WorldData> getWorlds() {
        return worldCache.values().stream().toList();
    }

}

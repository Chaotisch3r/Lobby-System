package me.chaotisch3r.lobby.database;

import lombok.SneakyThrows;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:47 13.05.2022
 **/

public class RankDataManager {

    private final MySQL mySQL = Lobby.getInstance().getMySQL();
    private final Map<String, RankData> rankCache = new HashMap<>();

    public void registerRank() {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            if(!mySQL.isConnected()) mySQL.connect();
            try (PreparedStatement ps = mySQL.getStatement("CREATE TABLE IF NOT EXISTS `rank_data`(" +
                    "`rankName` VARCHAR(36) NOT NULL, `rankID` INT NOT NULL, `rankListName` VARCHAR(36) NOT NULL, `rankDisplayName` VARCHAR(36) NOT NULL, " +
                    "`rankPermissions` TEXT NOT NULL, PRIMARY KEY (`rankID`), UNIQUE INDEX `rankName_UNIQUE` (`rankName`))")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @SneakyThrows
    public void loadRank(String rankName, int rankID, String rankListName, String rankDisplayName, String[] rankPermissions) {
        if(!mySQL.isConnected()) mySQL.connect();
        PreparedStatement ps = mySQL.getStatement("SELECT * FROM rank_data WHERE rankName=?");
        ResultSet rs = null;
        try {
            ps.setString(1, rankName);
            rs = ps.executeQuery();

            RankData rankData;
            if(rs.next()) {
                rankCache.put(rankName, (rankData = new RankData(rankName, rs.getInt("rankID"),
                        rs.getString("rankListName"), rs.getString("rankDisplayName"),
                        rs.getString("rankPermissions").split(";"))));
            } else {
                rankCache.put(rankName, (rankData = new RankData(rankName, rankID, rankListName, rankDisplayName, rankPermissions)));
                updateAsync(rankData);
            }
            if(!rankData.getRankName().equals(rankName)) updateAsync(rankData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if(rs != null) rs.close();
            ps.close();
        }
    }

    public void renameRank(String oldRankName, String newRankName) {
        if(!rankCache.containsKey(oldRankName)) return;
        RankData oldRankData = rankCache.get(oldRankName);
        deleteRank(oldRankName);
        loadRank(newRankName, oldRankData.getRankID(), oldRankData.getRankListName(), oldRankData.getRankDisplayName(), oldRankData.getRankPermissions());
    }

    public void renameListName(String rankName, String newRankListName) {
        if(!rankCache.containsKey(rankName)) return;
        RankData oldRankData = rankCache.get(rankName);
        deleteRank(rankName);
        loadRank(oldRankData.getRankName(), oldRankData.getRankID(), newRankListName, oldRankData.getRankDisplayName(), oldRankData.getRankPermissions());
    }

    public void renameDisplayName(String rankName, String newRankDisplayName) {
        if(!rankCache.containsKey(rankName)) return;
        RankData oldRankData = rankCache.get(rankName);
        deleteRank(rankName);
        loadRank(oldRankData.getRankName(), oldRankData.getRankID(), oldRankData.getRankListName(), newRankDisplayName, oldRankData.getRankPermissions());
    }

    public void readjustRandID(String rankName, int newRankID) {
        if(!rankCache.containsKey(rankName)) return;
        RankData oldRankData = rankCache.get(rankName);
        if(getRankIDs().contains(newRankID)) return;
        deleteRank(rankName);
        loadRank(oldRankData.getRankName(), oldRankData.getRankID(), oldRankData.getRankListName(), oldRankData.getRankDisplayName(), oldRankData.getRankPermissions());
    }

    public void deleteRank(String rankName) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> {
            if(!rankCache.containsKey(rankName)) return;
            unloadRank(rankName);
            try (PreparedStatement ps = mySQL.getStatement("DELTE FROM rank_data WHERE rankName=?")) {
                ps.setString(1, rankName);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void updateAsync(RankData rankData) {
        Bukkit.getScheduler().runTaskAsynchronously(Lobby.getInstance(), () -> update(rankData));
    }

    public void unloadRank(String rankName) { rankCache.remove(rankName); }

    private void update(RankData rankData) {
        if(!mySQL.isConnected()) mySQL.connect();
        try (PreparedStatement preparedStatement = mySQL.getStatement("INSERT INTO rank_data(`rankName`, `rankID`, `rankListName`, `rankDisplayName`, `rankPermissions`) " +
                "VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE `rankListName`=?, `rankDisplayName`=?, `rankPermissions`=?")) {
            preparedStatement.setString(1, rankData.getRankName());
            preparedStatement.setInt(2, rankData.getRankID());
            preparedStatement.setString(3, rankData.getRankListName());
            preparedStatement.setString(4, rankData.getRankDisplayName());
            preparedStatement.setString(5, Arrays.toString(rankData.getRankPermissions())
                    .replaceAll(",", ";").replaceAll(" ", "")
                    .replace("[", "").replace("]", ""));

            preparedStatement.setString(6, rankData.getRankListName());
            preparedStatement.setString(7, rankData.getRankDisplayName());
            preparedStatement.setString(8, Arrays.toString(rankData.getRankPermissions())
                    .replaceAll(",", ";").replaceAll(" ", "")
                    .replace("[", "").replace("]", ""));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RankData> getRanks() {
        return rankCache.values().stream().toList();
    }

    public List<Integer> getRankIDs() {
        List<Integer> ids = new ArrayList<>();
        getRanks().forEach(rankData -> ids.add(rankData.getRankID()));
        return ids;
    }

    public List<String> getRankNames() {
        List<String> names = new ArrayList<>();
        getRanks().forEach(rankData -> names.add(rankData.getRankName()));
        return names;
    }

    public RankData getRank(String rankName) {
        return rankCache.get(rankName);
    }

}

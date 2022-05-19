package me.chaotisch3r.lobby.util;

import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.text.NumberFormat;
import java.util.*;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:07 11.05.2022
 **/

public class UIManager {

    private Calendar calendar;
    private final Language language = Lobby.getInstance().getLanguage();

    public UIManager(Player player) {
        calendar = Calendar.getInstance(Locale.GERMAN);
        FileConfiguration config = Lobby.getInstance().getConfig();
        player.setLevel(config.getBoolean("XP.useYearXP") ? Math.addExact(calendar.getTime().getYear(), 1900) :
                config.getInt("XP.Level"));
        sendTimeActionBar(player);
        setSideBar(player);
    }

    private void sendTimeActionBar(Player player) {
        ActionBar actionBar = new ActionBar();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Lobby.getInstance(), () -> {
            calendar = Calendar.getInstance(Locale.GERMAN);
            String message;
            int minutes = calendar.getTime().getMinutes();
            int hours = calendar.getTime().getHours();
            message = "§b" + calendar.getTime().getDate() + "§8.§b" + Math.addExact(calendar.getTime().getMonth(), 1) + "§8.§b"
                    + Math.addExact(calendar.getTime().getYear(), 1900);
            if(hours < 10 && minutes < 10) {
                message = message + "§8 - " + "§60" + hours + "§8:§60"
                        + minutes;
            }
            else if(hours < 10 && minutes > 10) {
                message = message + "§8 - " + "§60" + hours + "§8:§6"
                        + minutes;
            }
            else if(hours > 10 && minutes < 10) {
                message = message + "§8 - " + "§6" + hours + "§8:§60"
                        + minutes;
            }
            else message = message + "§8 - " + "§6" + hours + "§8:§6"
                        + minutes;

            actionBar.sendActionBar(player, message);
        }, 0, 40);
    }

    private void setSideBar(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        PlayerData playerData = Lobby.getInstance().getPlayerDataManager().getPlayer(player.getUniqueId());
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("stats", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6Lobby");
        for (Map.Entry<String, Integer> s : getScores(player.getUniqueId()).entrySet()) {
            if (objective.getScore(s.getKey()).isScoreSet()) continue;
            if (s.getValue() == 15) continue;
            objective.getScore(s.getKey().replace('&', '§')
                            .replace("%COINS%", NumberFormat.getInstance(Locale.GERMAN).format(playerData.getCoins()))
                            .replace("%RANK%", playerData.getRank().getRankName())
                            .replace("%SERVER%", player.getWorld().getName()))
                    .setScore(s.getValue());
        }
        player.setScoreboard(scoreboard);
    }

    private Map<String, Integer> getScores(UUID uuid) {
        Map<String, Integer> scoreMap = new HashMap<>();
        for (int i = 15; i > 0; i--) {
            String score = language.getColoredString(uuid, "UI.SideBar." + i);
            if (score == null) continue;
            if (scoreMap.containsKey(score)) continue;
            scoreMap.put(score, i);
        }
        return scoreMap;
    }

}

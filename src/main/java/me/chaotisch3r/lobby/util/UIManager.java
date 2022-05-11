package me.chaotisch3r.lobby.util;

import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.database.Language;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Calendar;
import java.util.Locale;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:07 11.05.2022
 **/

public class UIManager {

    private Calendar calendar;
    private Language language;

    public UIManager(Player player) {
        calendar = Calendar.getInstance(Locale.GERMAN);
        FileConfiguration config = Lobby.getInstance().getConfig();
        player.setLevel(config.getBoolean("XP.useYearXP") ? Math.addExact(calendar.getTime().getYear(), 1900) :
                config.getInt("XP.Level"));
        sendTimeActionBar(player);
        setSideBar(player);
    }

    public UIManager(Language language) {
        this.language = language;
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
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("stats", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6Lobby");
        objective.getScore("§b§l ").setScore(0);
        objective.getScore("§6Coins§7: ").setScore(1);
        objective.getScore("§7» §4Not avaiable").setScore(2);
        objective.getScore("§a§l ").setScore(3);
        objective.getScore("§bRank§7: ").setScore(4);
        objective.getScore("§7» §4Not avaiable").setScore(5);
        objective.getScore("§1§l ").setScore(6);
        objective.getScore("§aServer§7: ").setScore(7);
        objective.getScore("§7» §a" + player.getWorld().getName()).setScore(8);
        objective.getScore("§1§l ").setScore(9);
        player.setScoreboard(scoreboard);
    }

}

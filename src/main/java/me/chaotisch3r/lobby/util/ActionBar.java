package me.chaotisch3r.lobby.util;

import me.chaotisch3r.lobby.Lobby;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 16:53 07.05.2022
 **/

public class ActionBar {

    final HashMap<String, Integer> Count = new HashMap<>();

    public void sendActionBar(Player player, String message) {
        final String newMessage = message.replace("_", " ");
        String s = ChatColor.translateAlternateColorCodes('&', newMessage);
        TextComponent component = new TextComponent(s);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public void sendActionBarTime(final Player player, final String message, final Integer time) {
        final String newMessage = message.replace("_", " ");
        if (!Count.containsKey(player.getName())) {
            String s = ChatColor.translateAlternateColorCodes('&', newMessage);
            TextComponent component = new TextComponent(s);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(Lobby.getInstance(), () -> {
            String s = ChatColor.translateAlternateColorCodes('&', newMessage);
            TextComponent component = new TextComponent(s);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);

            if (!Count.containsKey(player.getName())) {
                Count.put(player.getName(), 0);
            }
            int count = Count.get(player.getName());
            int newCount = count + 20;
            Count.put(player.getName(), newCount);

            if (newCount < time - 20) {
                wait(player, message, time);
            } else {
                Count.remove(player.getName());
            }
        }, 10);
    }

    private void wait(final Player player, final String message, final Integer time) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Lobby.getInstance(), () -> sendActionBarTime(player, message, time), 10);
    }
}
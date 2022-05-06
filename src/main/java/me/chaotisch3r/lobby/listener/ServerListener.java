package me.chaotisch3r.lobby.listener;

import me.chaotisch3r.lobby.Lobby;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 21:26 20.04.2022
 **/

public class ServerListener implements Listener {

    final FileConfiguration config = Lobby.getInstance().getConfig();

    @EventHandler
    public void onServerListPingEvent(ServerListPingEvent event) {
        event.setMotd(ChatColor.translateAlternateColorCodes('&', config.getString("MOTD")));
    }

    @EventHandler
    public void onWeatherChangeEvent(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}

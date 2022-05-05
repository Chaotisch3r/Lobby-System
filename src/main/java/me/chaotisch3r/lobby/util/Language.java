package me.chaotisch3r.lobby.util;

import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.filemanagement.MessageConfig;
import me.chaotisch3r.lobby.mysql.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:18 20.04.2022
 **/

public class Language {

    private final MySQL mySQL = new MySQL();
    private final MessageConfig messageConfig = new MessageConfig();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();
    private final Map<UUID, Locale> localeMap = new HashMap<>();
    private final List<Locale> locales;

    public Language() {
        locales = getlanguages();
    }

    public void loadLocale(UUID uuid) {
        localeMap.put(uuid, getLanguage(uuid));
    }

    public void unloadLocale(UUID uuid) {
        localeMap.remove(uuid);
    }

    public void setLocale(UUID uuid, Locale locale) {
        localeMap.replace(uuid, locale);
        Player player = Bukkit.getPlayer(uuid);
        playerDataManager.updateAsync(new PlayerData(playerDataManager.getPlayer(player.getUniqueId()).getUuid(), playerDataManager.getPlayer(player.getUniqueId()).getName(),
                playerDataManager.getPlayer(player.getUniqueId()).getIpAddress(), playerDataManager.getPlayer(player.getUniqueId()).getRank(), locale));
    }

    public Locale getLanguage(UUID uuid) {
        if (!mySQL.isConnected()) {
            mySQL.connect();
        }
        PreparedStatement ps = mySQL.getStatement("SELECT * FROM player_data WHERE uuid=?");
        ResultSet rs;
        try {
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();
            if (!rs.next()) {
                return Locale.ENGLISH;
            }
            return Locale.forLanguageTag(rs.getString("locale"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Locale getLocale(UUID uuid) {
        Locale locale = localeMap.getOrDefault(uuid, Locale.ENGLISH);
        if (!locales.contains(locale))
            return Locale.ENGLISH;
        return locale;
    }

    public String getColoredString(UUID uuid, String path) {
        return ChatColor.translateAlternateColorCodes('&', getString(uuid, path));
    }

    public String getString(UUID uuid, String path) {
        return messageConfig.getConfig().getString(getLocale(uuid).toLanguageTag() + "." + path);
    }

    public List<String> getStringList(UUID uuid, String path) {
        return messageConfig.getConfig().getStringList(getLocale(uuid).toLanguageTag() + "." + path).stream().map(s ->
                ChatColor.translateAlternateColorCodes('&', s)).toList();
    }

    private List<Locale> getlanguages() {
        List<Locale> locales = new ArrayList<>();
        if (!mySQL.isConnected()) {
            mySQL.connect();
        }
        try (PreparedStatement ps = mySQL.getStatement("SELECT locale FROM player_data"); ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                locales.add(Locale.forLanguageTag(rs.getString("locale")));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return locales;
    }

    public List<Locale> getLocales() {
        return locales;
    }


}

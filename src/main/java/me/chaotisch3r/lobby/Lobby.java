package me.chaotisch3r.lobby;

import lombok.Getter;
import me.chaotisch3r.lobby.command.BuildCommand;
import me.chaotisch3r.lobby.database.LobbyDataManager;
import me.chaotisch3r.lobby.database.MySQL;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.filemanagement.MessageConfig;
import me.chaotisch3r.lobby.listener.PlayerListener;
import me.chaotisch3r.lobby.listener.ServerListener;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.Langauge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:13 - 20.04.2022
 **/

@Getter
public class Lobby extends JavaPlugin {

    private final String testUUID = "12a061c5-cdc5-4bbf-baa4-30606209567f";

    private static Lobby instance;
    private String prefix;
    private MySQL mySQL;
    private PlayerDataManager playerDataManager;
    private LobbyDataManager lobbyDataManager;
    private Langauge langauge;
    private MessageConfig messageConfig;
    private CommandUtil commandUtil;
    private PluginManager pluginManager;

    public static Lobby getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("Prefix"));
        registerClasses();
        registerListeners(this);
        registerDatabase();
        registerCommands();
    }

    @Override
    public void onDisable() {
        instance = null;
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(langauge.getColoredString(player.getUniqueId(), "Overall.KickMessage.1")
                    + "\n" + langauge.getColoredString(player.getUniqueId(), "Overall.KickMessage.2"));
        });
        this.mySQL.disconnect();
    }

    private void registerListeners(Plugin plugin) {
        this.pluginManager.registerEvents(new PlayerListener(playerDataManager, lobbyDataManager, langauge, commandUtil), plugin);
        this.pluginManager.registerEvents(new ServerListener(), this);
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(langauge, commandUtil));
    }

    private void registerClasses() {
        this.mySQL = new MySQL();
        this.playerDataManager = new PlayerDataManager();
        this.lobbyDataManager = new LobbyDataManager();
        this.pluginManager = Bukkit.getPluginManager();
        this.langauge = new Langauge();
        this.messageConfig = new MessageConfig();
        this.commandUtil = new CommandUtil();
    }

    private void registerDatabase() {
        this.mySQL.readInput();
        this.mySQL.connect();
        this.playerDataManager.registerPlayer();
        this.lobbyDataManager.registerLobby();
    }

}

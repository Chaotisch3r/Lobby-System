package me.chaotisch3r.lobby;

import lombok.Getter;
import me.chaotisch3r.lobby.command.BuildCommand;
import me.chaotisch3r.lobby.command.LanguageCommand;
import me.chaotisch3r.lobby.command.util.TabComplete;
import me.chaotisch3r.lobby.database.LobbyDataManager;
import me.chaotisch3r.lobby.database.MySQL;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.filemanagement.MessageConfig;
import me.chaotisch3r.lobby.listener.BlockListener;
import me.chaotisch3r.lobby.listener.EntityListener;
import me.chaotisch3r.lobby.listener.PlayerListener;
import me.chaotisch3r.lobby.listener.ServerListener;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.Langauge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:13 - 20.04.2022
 **/

@Getter
public class Lobby extends JavaPlugin {

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
        registerDatabase();
        registerCommands();
        registerTabComplete();
        registerListeners(this);
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
        this.pluginManager.registerEvents(new ServerListener(), plugin);
        this.pluginManager.registerEvents(new BlockListener(), plugin);
        this.pluginManager.registerEvents(new EntityListener(), plugin);
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(langauge, commandUtil));
        getCommand("language").setExecutor(new LanguageCommand(langauge, playerDataManager));
    }

    private void registerTabComplete() {
        getCommand("build").setTabCompleter(new TabComplete());
        getCommand("language").setTabCompleter(new TabComplete());
    }

    private void registerClasses() {
        this.mySQL = new MySQL();
        this.playerDataManager = new PlayerDataManager();
        this.lobbyDataManager = new LobbyDataManager();
        this.pluginManager = Bukkit.getPluginManager();
        this.messageConfig = new MessageConfig();
        this.langauge = new Langauge();
        this.commandUtil = new CommandUtil();
    }

    private void registerDatabase() {
        this.mySQL.connect();
        this.playerDataManager.registerPlayer();
        this.lobbyDataManager.registerLobby();
    }

}

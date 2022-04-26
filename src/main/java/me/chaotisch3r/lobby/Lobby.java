package me.chaotisch3r.lobby;

import lombok.Getter;
import me.chaotisch3r.lobby.command.BuildCommand;
import me.chaotisch3r.lobby.command.LanguageCommand;
import me.chaotisch3r.lobby.command.WorldCommand;
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
import me.chaotisch3r.lobby.util.ItemManager;
import me.chaotisch3r.lobby.util.Language;
import me.chaotisch3r.lobby.database.WorldDataManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
    private Language language;
    private MessageConfig messageConfig;
    private CommandUtil commandUtil;
    private ItemManager itemManager;
    private WorldDataManager worldDataManager;
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
        setupWorld(this);
    }

    @Override
    public void onDisable() {
        instance = null;
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.kickPlayer(language.getColoredString(player.getUniqueId(), "Overall.KickMessage.1")
                    + "\n" + language.getColoredString(player.getUniqueId(), "Overall.KickMessage.2"));
        });
        this.mySQL.disconnect();
    }

    private void registerListeners(Plugin plugin) {
        this.pluginManager.registerEvents(new PlayerListener(playerDataManager, lobbyDataManager, language, commandUtil), plugin);
        this.pluginManager.registerEvents(new ServerListener(), plugin);
        this.pluginManager.registerEvents(new BlockListener(), plugin);
        this.pluginManager.registerEvents(new EntityListener(), plugin);
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(language, commandUtil));
        getCommand("language").setExecutor(new LanguageCommand(language, messageConfig));
        getCommand("world").setExecutor(new WorldCommand(language, worldDataManager));
    }

    private void registerTabComplete() {
        getCommand("build").setTabCompleter(new TabComplete(worldDataManager));
        getCommand("language").setTabCompleter(new TabComplete(worldDataManager));
        getCommand("world").setTabCompleter(new TabComplete(worldDataManager));
    }

    private void registerClasses() {
        this.mySQL = new MySQL();
        this.playerDataManager = new PlayerDataManager();
        this.lobbyDataManager = new LobbyDataManager();
        this.pluginManager = Bukkit.getPluginManager();
        this.messageConfig = new MessageConfig();
        this.language = new Language();
        this.commandUtil = new CommandUtil();
        this.itemManager = new ItemManager();
        this.worldDataManager = new WorldDataManager();
    }

    private void registerDatabase() {
        this.mySQL.connect();
        this.playerDataManager.registerPlayer();
        this.lobbyDataManager.registerLobby();
        this.worldDataManager.registerWorld();
    }

    private void setupWorld(Plugin plugin) {
        World world = Bukkit.getWorld(this.getConfig().getString("World"));
        if(world == null)
            return;
        for (Entity e : world.getEntities()) {
            for (String str : this.getConfig().getStringList("RemovedEntity")) {
                if (!(e.getType() == EntityType.valueOf(str.toUpperCase()))) e.remove();
            }
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> world.setTime(6000), 20, 20 * 30);
        world.setThundering(false);
        world.setStorm(false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            this.worldDataManager.loadWorld(Bukkit.getWorlds());
            this.worldDataManager.getWorlds().forEach(worldData -> {
                if(Bukkit.getWorld(worldData.getUid()) == null) {
                    new WorldCreator(worldData.getWorldName());
                }
            });
        });
    }

}

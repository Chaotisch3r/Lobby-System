package me.chaotisch3r.lobby;

import lombok.Getter;
import me.chaotisch3r.lobby.command.*;
import me.chaotisch3r.lobby.command.util.TabComplete;
import me.chaotisch3r.lobby.database.*;
import me.chaotisch3r.lobby.filemanagement.ItemConfig;
import me.chaotisch3r.lobby.filemanagement.MessageConfig;
import me.chaotisch3r.lobby.listener.*;
import me.chaotisch3r.lobby.mysql.MySQL;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.ItemManager;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:13 - 20.04.2022
 **/

/*
    TODO:
     - Settings Databse:
       - /coin <Dein Name> ein/auschaltbar machen (perms ignore this)
       - Glass farbe in Inv
       - ...
 */

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
    private ItemConfig itemConfig;
    private ItemManager itemManager;
    private WorldDataManager worldDataManager;
    private WarpDataManager warpDataManager;
    private RankDataManager rankDataManager;
    private SettingsDataManager settingsDataManager;

    private PluginManager pluginManager;

    public static Lobby getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        prefix = getColoredString("Prefix");
        registerClasses();
        registerDatabase();
        registerCommands();
        registerTabComplete();
        registerListeners(this);
        setupWorld(this);
        setupRanks();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player ->
                player.kickPlayer(language.getColoredString(player.getUniqueId(), "Overall.KickMessage.1")
                + "\n" + language.getColoredString(player.getUniqueId(), "Overall.KickMessage.2")));
        this.mySQL.disconnect();
        instance = null;
    }

    private void registerListeners(Plugin plugin) {
        this.pluginManager.registerEvents(new PlayerListener(language, commandUtil, itemManager, playerDataManager,
                lobbyDataManager, settingsDataManager), plugin);
        this.pluginManager.registerEvents(new ServerListener(), plugin);
        this.pluginManager.registerEvents(new BlockListener(), plugin);
        this.pluginManager.registerEvents(new EntityListener(), plugin);
        this.pluginManager.registerEvents(new InventoryListener(language, itemConfig, itemManager, playerDataManager,
                lobbyDataManager), plugin);
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(language, commandUtil, itemManager, playerDataManager));
        getCommand("language").setExecutor(new LanguageCommand(language, messageConfig, playerDataManager));
        getCommand("world").setExecutor(new WorldCommand(language, itemManager, worldDataManager, playerDataManager));
        getCommand("warp").setExecutor(new WarpCommand(language, itemManager, warpDataManager, playerDataManager));
        getCommand("lobby").setExecutor(new LobbyCommand(language, warpDataManager, playerDataManager));
        getCommand("ping").setExecutor(new PingCommand(language, playerDataManager));
        getCommand("rank").setExecutor(new RankCommand(language, commandUtil, itemManager, playerDataManager, rankDataManager));
        getCommand("coins").setExecutor(new CoinsCommand(language, commandUtil, playerDataManager, settingsDataManager));
    }

    private void registerTabComplete() {
        List<String> commands = new ArrayList<>();
        commands.add("build");
        commands.add("language");
        commands.add("lobby");
        commands.add("ping");
        commands.add("warp");
        commands.add("world");
        commands.add("rank");
        commands.forEach(cmd -> getCommand(cmd).setTabCompleter(new TabComplete(worldDataManager, warpDataManager, rankDataManager, playerDataManager)));
    }

    private void registerClasses() {
        this.mySQL = new MySQL();
        this.rankDataManager = new RankDataManager();
        this.playerDataManager = new PlayerDataManager(rankDataManager);
        this.playerDataManager.registerPlayer();
        this.lobbyDataManager = new LobbyDataManager();
        this.pluginManager = Bukkit.getPluginManager();
        this.messageConfig = new MessageConfig();
        this.language = new Language(messageConfig, playerDataManager);
        this.itemConfig = new ItemConfig();
        this.worldDataManager = new WorldDataManager();
        this.warpDataManager = new WarpDataManager();
        this.settingsDataManager = new SettingsDataManager();
        this.commandUtil = new CommandUtil(language, playerDataManager);
        this.itemManager = new ItemManager(itemConfig, language, playerDataManager, worldDataManager, warpDataManager,
                lobbyDataManager, settingsDataManager);
    }

    private void registerDatabase() {
        this.mySQL.connect();
        this.lobbyDataManager.registerLobby();
        this.worldDataManager.registerWorld();
        this.warpDataManager.registerWarp();
        this.rankDataManager.registerRank();
        this.settingsDataManager.registerSettings();
    }

    private void setupWorld(Plugin plugin) {
        this.worldDataManager.loadWorlds();
        this.worldDataManager.loadWorld(Bukkit.getWorlds());
        this.worldDataManager.getWorlds().forEach(worldData -> {
            if (Bukkit.getWorld(worldData.getUid()) == null) {
                Bukkit.createWorld(new WorldCreator(worldData.getWorldName()));
            }
        });
        World world = Bukkit.getWorld(plugin.getConfig().getString("World"));
        if (world == null)
            return;
        world.getEntities().forEach(entity -> plugin.getConfig().getStringList("RemovedEntity").forEach(s -> {
            if (!(entity.getType() == EntityType.valueOf(s.toUpperCase()))) entity.remove();
        }));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> world.setTime(6000), 20, 20 * 30);
        world.setThundering(false);
        world.setStorm(false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
    }

    private void setupRanks() {
        if(this.rankDataManager.getRank("owner") == null)
            this.rankDataManager.loadRank("owner", 1, "&4Owner&7 |&4 ",
                    "&4Owner&7 » ", new String[]{ "lobby.*" });
        if(this.rankDataManager.getRank("player") == null)
            this.rankDataManager.loadRank("player", 30, "&7Player | ",
                    "&7Player » ", new String[]{ "" });
    }

    public String getColoredString(String path) {
        return ChatColor.translateAlternateColorCodes('&', getConfig().getString(path));
    }
}

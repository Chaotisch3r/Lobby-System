package me.chaotisch3r.lobby;

import lombok.Getter;
import me.chaotisch3r.lobby.command.*;
import me.chaotisch3r.lobby.command.util.TabComplete;
import me.chaotisch3r.lobby.database.*;
import me.chaotisch3r.lobby.filemanagement.ItemConfig;
import me.chaotisch3r.lobby.filemanagement.MessageConfig;
import me.chaotisch3r.lobby.listener.BlockListener;
import me.chaotisch3r.lobby.listener.EntityListener;
import me.chaotisch3r.lobby.listener.PlayerListener;
import me.chaotisch3r.lobby.listener.ServerListener;
import me.chaotisch3r.lobby.mysql.MySQL;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.ItemManager;
import org.bukkit.*;
import org.bukkit.entity.Entity;
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
     - Warp List Inv
     - World List chnagen zum Inv -> Overwold: Grass; Nether: Netherrack; End: End Stone
     - Alle Help Messages Hoverbar machen mit einer genaueren Beschreibung
     - Alle Help Messages Klickbar machen, der den geklickten Befehl Suggested
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
        setupRanks();
    }

    @Override
    public void onDisable() {
        instance = null;
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(language.getColoredString(player.getUniqueId(), "Overall.KickMessage.1")
                + "\n" + language.getColoredString(player.getUniqueId(), "Overall.KickMessage.2")));
        this.mySQL.disconnect();
    }

    private void registerListeners(Plugin plugin) {
        this.pluginManager.registerEvents(new PlayerListener(playerDataManager, lobbyDataManager,
                language, commandUtil, itemManager), plugin);
        this.pluginManager.registerEvents(new ServerListener(), plugin);
        this.pluginManager.registerEvents(new BlockListener(), plugin);
        this.pluginManager.registerEvents(new EntityListener(), plugin);
    }

    private void registerCommands() {
        getCommand("build").setExecutor(new BuildCommand(language, commandUtil, playerDataManager));
        getCommand("language").setExecutor(new LanguageCommand(language, messageConfig));
        getCommand("world").setExecutor(new WorldCommand(language, worldDataManager, itemManager));
        getCommand("warp").setExecutor(new WarpCommand(language, warpDataManager));
        getCommand("lobby").setExecutor(new LobbyCommand(language, warpDataManager));
        getCommand("ping").setExecutor(new PingCommand(language));
    }

    private void registerTabComplete() {
        List<String> commands = new ArrayList<>();
        commands.add("build");
        commands.add("language");
        commands.add("lobby");
        commands.add("ping");
        commands.add("warp");
        commands.add("world");
        commands.forEach(cmd -> getCommand(cmd).setTabCompleter(new TabComplete(worldDataManager, warpDataManager)));
    }

    private void registerClasses() {
        this.mySQL = new MySQL();
        this.playerDataManager = new PlayerDataManager();
        this.playerDataManager.registerPlayer();
        this.lobbyDataManager = new LobbyDataManager();
        this.pluginManager = Bukkit.getPluginManager();
        this.messageConfig = new MessageConfig();
        this.language = new Language(messageConfig, playerDataManager);
        this.commandUtil = new CommandUtil(language);
        this.itemConfig = new ItemConfig();
        this.itemManager = new ItemManager(itemConfig, language, playerDataManager, worldDataManager);
        this.worldDataManager = new WorldDataManager();
        this.warpDataManager = new WarpDataManager();
        this.rankDataManager = new RankDataManager();
    }

    private void registerDatabase() {
        this.mySQL.connect();
        this.lobbyDataManager.registerLobby();
        this.worldDataManager.registerWorld();
        this.warpDataManager.registerWarp();
        this.rankDataManager.registerRank();
    }

    private void setupWorld(Plugin plugin) {
        World world = Bukkit.getWorld(this.getConfig().getString("World"));
        if (world == null)
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
                if (Bukkit.getWorld(worldData.getUid()) == null) {
                    new WorldCreator(worldData.getWorldName());
                }
            });
        });
    }

    private void setupRanks() {
        if(this.rankDataManager.getRank("owner") == null)
            this.rankDataManager.loadRank("owner", 1, "&4Owner&7 |&4 ", "&4Owner&7 » ", new String[]{ "lobby.*" });
        if(this.rankDataManager.getRank("player") == null)
            this.rankDataManager.loadRank("player", 30, "&7Player | ", "&7Player » ", new String[]{ "" });
    }

}

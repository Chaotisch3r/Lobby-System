package me.chaotisch3r.lobby.command.util;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.RankDataManager;
import me.chaotisch3r.lobby.database.WarpDataManager;
import me.chaotisch3r.lobby.database.WorldDataManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 22:49 20.04.2022
 **/

@RequiredArgsConstructor
public class TabComplete implements TabCompleter {

    private final WorldDataManager worldDataManager;
    private final WarpDataManager warpDataManager;
    private final RankDataManager rankDataManager;
    private final PlayerDataManager playerDataManager;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (!(sender instanceof Player player)) return tabComplete;
        Bukkit.getOnlinePlayers().forEach(players -> tabComplete.remove(player.getName()));
        UUID uuid = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayer(uuid);
        if (command.getName().equalsIgnoreCase("build")) {
            if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.build")))
                return tabComplete;
            if (args.length == 1) {
                tabComplete.add("help");
                Bukkit.getOnlinePlayers().forEach(players -> tabComplete.add(players.getName()));
            }
        }
        if (command.getName().equalsIgnoreCase("language") || command.getName().equalsIgnoreCase("lang")) {
            if (args.length == 1) {
                if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.language")))
                    tabComplete.add("reload");
                tabComplete.add("help");
                tabComplete.add("change");
                tabComplete.add("list");
                Bukkit.getOnlinePlayers().forEach(players -> tabComplete.add(players.getName()));
            } else if (args.length == 2) {
                if (!args[0].equalsIgnoreCase("change")) return tabComplete;
                tabComplete.add("de");
                tabComplete.add("en");
            }
        }
        if (command.getName().equalsIgnoreCase("world")) {
            if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.world")))
                return tabComplete;
            if (args.length == 1) {
                tabComplete.add("information");
                tabComplete.add("join");
                tabComplete.add("create");
                tabComplete.add("delete");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("delete")) {
                    worldDataManager.getWorlds().forEach(worldData -> tabComplete.add(worldData.getWorldName()));
                }
            }
            else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("create")) {
                    tabComplete.add("NORMAL");
                    tabComplete.add("NETHER");
                    tabComplete.add("THE_END");
                }
            }
        }
        if (command.getName().equalsIgnoreCase("warp")) {
            if (args.length == 1) {
                tabComplete.add("list");
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("remove")) {
                    warpDataManager.getWarps().forEach(warpData -> tabComplete.add(warpData.getWarpName()));
                }
                if (args[0].equalsIgnoreCase("rename")) {
                    warpDataManager.getWarps().forEach(warpData -> tabComplete.add(warpData.getWarpName()));
                }
            }
        }
        if (command.getName().equalsIgnoreCase("lobby")) {
            if(args.length == 1) {
                tabComplete.add("help");
                if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.setup")))
                    return tabComplete;
                tabComplete.add("setup");
            }
            else if(args.length == 2) {
                tabComplete.add("setlobby");
            }
        }
        if (command.getName().equalsIgnoreCase("ping")) {
            if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.ping")))
                return tabComplete;
            if(args.length == 1) {
                Bukkit.getOnlinePlayers().forEach(players -> tabComplete.add(players.getName()));
            }
        }
        if (command.getName().equalsIgnoreCase("rank")) {
            if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.build")))
                return tabComplete;
            if (args.length == 1) {
                tabComplete.add("create");
                tabComplete.add("delete");
                tabComplete.add("set");
                tabComplete.add("remove");
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("delete")) {
                    tabComplete.addAll(rankDataManager.getRankNames());
                }
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove")) {
                    Bukkit.getOnlinePlayers().forEach(players -> tabComplete.add(players.getName()));
                }
                if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("set")
                        || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("create")) {
                    return tabComplete;
                }
                tabComplete.add("information");
                tabComplete.add("readjustID");
                tabComplete.add("rename");
                tabComplete.add("renameList");
                tabComplete.add("renameDisplay");
            }
            if (args.length == 3) {
                if(!(args[0].equalsIgnoreCase("set"))) return tabComplete;
                tabComplete.addAll(rankDataManager.getRankNames());
            }
        }
        return tabComplete;
    }
}

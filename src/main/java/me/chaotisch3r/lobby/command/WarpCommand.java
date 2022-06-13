package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.WarpDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.ItemManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 19:31 26.04.2022
 **/

@RequiredArgsConstructor
public class WarpCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil;
    private final ItemManager itemManager;

    private final WarpDataManager warpDataManager;
    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§7This won't work, until you are a Player.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        Location location = player.getLocation();
        if (args.length == 0) {
            sendHelp(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove")) {
                if (player.isOp() || player.hasPermission("lobby.warp") || player.hasPermission("lobby.*")) {
                    sendHelp(player);
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                itemManager.openWarpListInventory(player);
            }
            String warpName = args[0];
            if (warpDataManager.getWarp(warpName) == null) {
                player.sendMessage(prefix, language.getColoredString(uuid, "Command.Warp.Error.WarpNotExisting")
                        .replace("%WARP", warpName));
            }
            player.teleport(warpDataManager.getWarp(warpName).toLocation());
            player.sendMessage(prefix, language.getColoredString(uuid, "Command.Warp.Use"));
        } else if (args.length == 2) {
            PlayerData playerData = playerDataManager.getPlayer(uuid);
            if (!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.warp"))) {
                sendHelp(player);
                return true;
            }
            String warpName = args[1];
            if (args[0].equalsIgnoreCase("set")) {
                if (warpDataManager.getWarp(warpName) != null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Error.WarpAlreadyExisting"));
                    return true;
                }
                warpDataManager.loadWarp(warpName, location.getWorld(), location.getX(), location.getY(), location.getZ(),
                        location.getYaw(), location.getPitch());
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Set").replace("%WARP%", warpName));
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (warpDataManager.getWarp(warpName) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Error.WarpNotExisting"));
                    return true;
                }
                warpDataManager.removeWarp(warpName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Remove").replace("%WARP%", warpName));
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (warpDataManager.getWarp(warpName) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Error.WarpNotExisting"));
                    return true;
                }
                warpDataManager.loadWarp(warpName, location.getWorld(), location.getX(), location.getY(), location.getZ(),
                        location.getYaw(), location.getPitch());
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Update").replace("%WARP%", warpName));
            }
        } else if (args.length == 3) {
            if (!(player.isOp() || player.hasPermission("lobby.warp") || player.hasPermission("lobby.*"))) {
                sendHelp(player);
                return true;
            }
            String oldWarpName = args[1];
            String newWarpName = args[2];
            if (args[0].equalsIgnoreCase("rename")) {
                if (warpDataManager.getWarp(oldWarpName) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Error.WarpNotExisting"));
                    return true;
                }
                warpDataManager.renameWarp(oldWarpName, newWarpName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Warp.Rename")
                        .replace("%OLDWARPNAME%", oldWarpName).replace("%NEWWARPNAME%", newWarpName));
            }
        } else {
            sendHelp(player);
            return true;
        }
        return false;
    }

    private void sendHelp(Player player) {
        commandUtil.sendWarpHelp(player);
    }

}

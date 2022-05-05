package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 21:34 20.04.2022
 **/

@RequiredArgsConstructor
public class BuildCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();
    private final Language language;
    private final CommandUtil commandUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "Dieses Feature ist nur als Spieler möglich.");
            return true;
        }
        if (!(player.isOp() || player.hasPermission("lobby.*") || player.hasPermission("lobby.build"))) {
            player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.NoPermission"));
            return true;
        }
        if (args.length == 0) {
            player.setGameMode(commandUtil.build.contains(player) ? GameMode.SURVIVAL : GameMode.CREATIVE);
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Build.Add").replace("%PLAYER%", player.getName()));
                commandUtil.build.add(player);
            }
            if (player.getGameMode() == GameMode.SURVIVAL) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Build.Remove").replace("%PLAYER%", player.getName()));
                commandUtil.build.remove(player);
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                sendHelp(player);
                return true;
            }
            String targetName = args[0];
            if (Bukkit.getPlayer(targetName) == null) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.UknownPlayer").replace("%TARGET%", targetName));
                return true;
            }
            Player target = Bukkit.getPlayer(targetName);
            target.setGameMode(commandUtil.build.contains(target) ? GameMode.SURVIVAL : GameMode.CREATIVE);
            if (target.getGameMode() == GameMode.CREATIVE) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Build.Add").replace("%PLAYER%", target.getName()));
                target.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Build.Add").replace("%PLAYER%", target.getName()));
                commandUtil.build.add(target);
            }
            if (target.getGameMode() == GameMode.SURVIVAL) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Build.Remove").replace("%PLAYER%", target.getName()));
                target.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Build.Remove").replace("%PLAYER%", target.getName()));
                commandUtil.build.remove(target);
            }
        } else {
            sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(prefix + "§7----------[§6 Build§7-§6Help§7]----------");
        player.sendMessage("§6/build§7 - " + language.getColoredString(player.getUniqueId(), "Command.Build.Usage.Single"));
        player.sendMessage("§6/build§7 <§bplayername§7> - " + language.getColoredString(player.getUniqueId(), "Command.Build.Usage.Multi"));
        player.sendMessage(prefix + "§7----------[§6 Build§7-§6Help§7]----------");
    }

}
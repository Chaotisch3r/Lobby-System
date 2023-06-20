package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
import org.bukkit.Bukkit;
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
 * Created for Lobby-System, 15:50 06.05.2022
 **/

@RequiredArgsConstructor
public class PingCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil;

    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§bThis will only work, if you are a player.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        if(args.length == 0) {
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Ping")
                    .replace("%PLAYER%", player.getName())
                    .replace("%PING%", String.valueOf(player.getPing())));
        }
        else if(args.length == 1) {
            PlayerData playerData = playerDataManager.getPlayer(uuid);
            if(!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.ping"))) {
               player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.NoPermission"));
               return true;
           }
           String targetName = args[0];
            Player target;
            if((target = Bukkit.getPlayer(targetName)) == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer")
                        .replace("%TARGET%", targetName));
                return true;
            }
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Ping")
                    .replace("%PLAYER%", target.getName())
                    .replace("%PING%", String.valueOf(target.getPing())));
        }
        else {
            sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        commandUtil.sendPingHelp(player);
    }
}
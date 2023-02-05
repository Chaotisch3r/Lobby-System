package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.WarpDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
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
 * Created for Lobby-System, 14:13 06.05.2022
 **/

@RequiredArgsConstructor
public class LobbyCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil;

    private final WarpDataManager warpDataManager;
    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§7This will only work, if you are a player.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        if(args.length >= 1) {
            PlayerData playerData = playerDataManager.getPlayer(uuid);
            if(!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.setup"))) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.NoPermission"));
                return true;
            }
        }
        if(args.length == 0) {
            if(warpDataManager.getWarp("lobby") == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Lobby.Error.LobbyWarpNotSet"));
                return true;
            }
            Location lobby = warpDataManager.getWarpLocation("lobby");
            player.teleport(lobby);
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Lobby.Teleport"));
        }
        else if(args.length == 1) {
            sendHelp(player);
        }
        else if(args.length == 2) {
            if(!args[0].equalsIgnoreCase("setup")) {
                sendHelp(player);
                return true;
            }
            if(args[1].equalsIgnoreCase("setlobby")) {
                warpDataManager.loadWarp("lobby", player.getLocation());
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Lobby.Setup.SetLobby"));
                return true;
            }
            sendHelp(player);
        }
        else {
            sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        commandUtil.sendLobbyHelp(player);
    }

}
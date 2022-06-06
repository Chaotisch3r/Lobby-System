package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.SettingsDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.UIManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 19:11 27.05.2022
 **/

@RequiredArgsConstructor
public class CoinsCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil;

    private final PlayerDataManager playerDataManager;
    private final SettingsDataManager settingsDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§7Dieser Befehl kann nur als Spieler ausgeführt werden.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        RankData playerRankData = playerDataManager.getPlayer(uuid).getRank();
        PlayerData playerData = playerDataManager.getPlayer(uuid);
        if (args.length == 0) {
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.GetCoins")
                    .replace("%PLAYER%", player.getName())
                    .replace("%COINS%", NumberFormat.getInstance(playerData.getLocale()).format(playerData.getCoins())));
        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("add")
                    || args[0].equalsIgnoreCase("remove")) {
                sendHelp(player, "");
                return true;
            }
            String targetName = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
            PlayerData targetData = playerDataManager.getOfflinePlayer(target.getUniqueId());
            if (targetData == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer")
                        .replace("%TARGET%", targetName));
                return true;
            }
            if (!settingsDataManager.getSettings(target.getUniqueId()).isGetCoins()) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.Error.GetCoinsNotVisible"));
                return true;
            }
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.GetCoins")
                    .replace("%PLAYER%", targetData.getName())
                    .replace("%COINS%", NumberFormat.getInstance(playerData.getLocale()).format(targetData.getCoins())));
        }
        else if (args.length == 2) {
            sendHelp(player, "");
        }
        else if (args.length == 3) {
            if (!(player.isOp() || playerRankData.hasPermission("lobby.*") || playerRankData.hasPermission("lobby.coins"))) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.NoPermission"));
                return true;
            }
            String targetName = args[1];
            int coins;
            try {
                coins = Integer.parseInt(args[2]);
            }catch (Exception exception) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.Error.UnknownCoins"));
                return true;
            }
            if (args[0].equalsIgnoreCase("set")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                PlayerData targetData = playerDataManager.getOfflinePlayer(target.getUniqueId());
                if (targetData == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer")
                            .replace("%TARGET%", targetName));
                    return true;
                }
                if (coins < 0) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.Error.CoinsBelowOne"));
                    return true;
                }
                PlayerData newTargetData = new PlayerData(target.getUniqueId(), target.getName(), targetData.getIpAddress(),
                        targetData.getRank(), targetData.getLocale(), coins);
                playerDataManager.updateAsync(newTargetData);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.SetCoins")
                        .replace("%TARGET%", targetData.getName())
                        .replace("%COINS%", NumberFormat.getInstance(playerData.getLocale()).format(coins)));
                if (!target.isOnline()) return true;
                if (target == player) {
                    new UIManager(player);
                    return true;
                }
                Player t = (Player) target;
                UUID uuidT = t.getUniqueId();
                PlayerData tData = playerDataManager.getPlayer(uuidT);
                t.sendMessage(prefix + language.getColoredString(uuidT, "Command.Coins.SetCoins")
                        .replace("%TARGET%", targetData.getName())
                        .replace("%COINS%", NumberFormat.getInstance(tData.getLocale()).format(coins)));
                new UIManager(t);
                return true;
            }
            if (args[0].equalsIgnoreCase("add")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                PlayerData oldTargetData = playerDataManager.getOfflinePlayer(target.getUniqueId());
                if (oldTargetData == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer")
                            .replace("%TARGET%", targetName));
                    return true;
                }
                if (coins <= 0) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.Error.CoinsBelowOne"));
                    return true;
                }
                int addedCoins = Math.addExact(oldTargetData.getCoins(), coins);
                PlayerData newTargetData = new PlayerData(target.getUniqueId(), target.getName(), oldTargetData.getIpAddress(),
                        oldTargetData.getRank(), oldTargetData.getLocale(), addedCoins);
                playerDataManager.updateAsync(newTargetData);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.AddCoins")
                        .replace("%TARGET%", newTargetData.getName())
                        .replace("%COINS%", NumberFormat.getInstance(playerData.getLocale()).format(coins)));
                if (!target.isOnline()) return true;
                if (target == player) {
                    new UIManager(player);
                    return true;
                }
                Player t = (Player) target;
                UUID uuidT = t.getUniqueId();
                PlayerData tData = playerDataManager.getPlayer(uuidT);
                t.sendMessage(prefix + language.getColoredString(uuidT, "Command.Coins.AddCoins")
                        .replace("%TARGET%", newTargetData.getName())
                        .replace("%COINS%", NumberFormat.getInstance(tData.getLocale()).format(coins)));
                new UIManager(t);
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                PlayerData targetData = playerDataManager.getOfflinePlayer(target.getUniqueId());
                if (targetData == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer")
                            .replace("%TARGET%", targetName));
                    return true;
                }
                if (coins <= 0) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.Error.CoinsBelowOne"));
                    return true;
                }
                PlayerData newTargetData;
                int newAmount = Math.subtractExact(targetData.getCoins(), coins);
                if (newAmount < 0) {
                    newTargetData = new PlayerData(target.getUniqueId(), target.getName(), targetData.getIpAddress(),
                            targetData.getRank(), targetData.getLocale(), 0);
                }
                else newTargetData = new PlayerData(target.getUniqueId(), target.getName(), targetData.getIpAddress(),
                        targetData.getRank(), targetData.getLocale(), newAmount);
                playerDataManager.updateAsync(newTargetData);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Coins.RemoveCoins")
                        .replace("%TARGET%", targetData.getName())
                        .replace("%COINS%", NumberFormat.getInstance(playerData.getLocale()).format(coins)));
                if (!target.isOnline()) return true;
                if (target == player) {
                    new UIManager(player);
                    return true;
                }
                Player t = (Player) target;
                UUID uuidT = t.getUniqueId();
                PlayerData tData = playerDataManager.getPlayer(uuidT);
                t.sendMessage(prefix + language.getColoredString(uuidT, "Command.Coins.RemoveCoins")
                        .replace("%TARGET%", t.getName())
                        .replace("%COINS%", NumberFormat.getInstance(tData.getLocale()).format(coins)));
                new UIManager(t);
                return true;
            }
            sendHelp(player, targetName);
        }else sendHelp(player, "");
        return false;
    }

    private void sendHelp(Player player, String targetName) {
        commandUtil.sendCoinsHelp(player, targetName);
    }
}
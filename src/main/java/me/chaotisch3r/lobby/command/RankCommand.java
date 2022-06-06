package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.RankDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.ItemManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 20:40 15.05.2022
 **/

@RequiredArgsConstructor
public class RankCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil;
    private final ItemManager itemManager;

    private final PlayerDataManager playerDataManager;
    private final RankDataManager rankDataManager;
    /*
        - /rank create <rankName> <rankID> <rankListName> <rankDisplayName> <permissions>   ✔
        - /rank delete <rankName/rankID>                                                    ✔
        - /rank set <playername> <rankName>                                                 ✔
        - /rank remove <playername> <rankName>                                              ✔
        - /rank <rankName/rankID> info / information                                        ✔
        - /rank <rankName/rankID> readjustID <newRankID>                                    ✔
        - /rank <rankName/rankID> rename <newRankName>                                      ✔
        - /rank <rankName/rankID> renameList <newListName>                                  ✔
        - /rank <rankName/rankID> renameDisplayName <newDisplayName>                        ✔

        `player` is a default rank, no changes should be done to this rank.
        The only thing that can chnage is Display- & ListName
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "Dieses Feature ist nur als Spieler möglich.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        RankData playerRankData = playerDataManager.getPlayer(uuid).getRank();
        if (args.length == 0) {
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.GetRank")
                    .replace("%RANK_NAME%", playerRankData.getRankName()));
        }
        else if(args.length == 1) {
            if (!(player.isOp() || playerRankData.hasPermission("lobby.*") || playerRankData.hasPermission("lobby.rank"))) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.NoPermission"));
                return true;
            }
            if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete")
                || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("remove")) {
                sendCompleteHelp(player);
            }
            String rankName = args[0];
            if (rankDataManager.getRank(rankName) == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                return true;
            }
            sendRankHelp(player, rankName);
        }
        else if(args.length == 2) {
            if (!(player.isOp() || playerRankData.hasPermission("lobby.*") || playerRankData.hasPermission("lobby.rank"))) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.NoPermission"));
                return true;
            }
            if (args[0].equalsIgnoreCase("openRankPermissions")) {
               String rankName = args[1];
                if (rankDataManager.getRank(rankName) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                    return true;
                }
                RankData rankData1 = rankDataManager.getRank(rankName);
                itemManager.openRankPermissionInventory(player, rankData1);
                return true;
            }
            if (args[0].equalsIgnoreCase("delete")) {
                String rankName = args[1];
                if (rankDataManager.getRank(rankName) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                    return true;
                }
                if (rankDataManager.getRank(rankName).getRankName().equals("player")) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.ConnotDeleteDefaultRank"));
                    return true;
                }
                rankDataManager.deleteRank(rankName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Delete")
                        .replace("%RANK_NAME%", rankName));
            }
            if(args[0].equalsIgnoreCase("remove")) {
                String playerName = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                PlayerData targetData = playerDataManager.getOfflinePlayer(target.getUniqueId());
                if (targetData == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                    return true;
                }
                if (playerDataManager.getOfflinePlayer(target.getUniqueId()) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer"));
                    return true;
                }
                playerDataManager.setRank(target.getUniqueId(), "player");
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Set")
                        .replace("%TARGET%", targetData.getName()).replace("%RANK_NAME%", "player"));
                if (target.isOnline()) {
                    UUID uuid1 = target.getUniqueId();
                    Player t = (Player) target;
                    t.kickPlayer(language.getColoredString(uuid1, "Overall.KickMessage.1" + "\n"
                            + language.getColoredString(uuid1, "Command.Rank.Kick")));
                }
                return true;
            }
            String rankName = args[0];
            if (rankDataManager.getRank(rankName) == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                return true;
            }
            if(args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("information")) {
                sendRankInformation(player, rankName);
            }
        }
        else if (args.length == 3) {
            if (!(player.isOp() || playerRankData.hasPermission("lobby.*") || playerRankData.hasPermission("lobby.rank"))) {
                player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.NoPermission"));
                return true;
            }
            if(args[0].equalsIgnoreCase("set")) {
                String playerName = args[1];
                String rankName = args[2];
                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
                PlayerData targetData = playerDataManager.getOfflinePlayer(target.getUniqueId());
                if (targetData == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer"));
                    return true;
                }
                if (rankDataManager.getRank(rankName) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                    return true;
                }
                playerDataManager.setRank(target.getUniqueId(), rankName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Set")
                        .replace("%TARGET%", targetData.getName()).replace("%RANK_NAME%", rankName));
                if (target.isOnline()) {
                    UUID uuid1 = target.getUniqueId();
                    Player t = (Player) target;
                    t.kickPlayer(language.getColoredString(uuid1, "Overall.KickMessage.1")
                            + "\n" + language.getColoredString(uuid1, "Command.Rank.Kick"));
                }
                return true;
            }
            String rankName = args[0];
            if (rankDataManager.getRank(rankName) == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
                return true;
            }
            RankData rankData = rankDataManager.getRank(rankName);
            if (args[1].equalsIgnoreCase("readjustID")) {
                int newRankID = Integer.parseInt(args[2]);
                if (rankDataManager.getRank(rankName).getRankID() == newRankID) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.CannotReadjustSameID"));
                    return true;
                }
                rankDataManager.readjustRandID(rankData.getRankName(), newRankID);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.ReadjustID")
                        .replace("%RANK_NAME%", rankData.getRankName())
                        .replace("%RANK_ID%", String.valueOf(newRankID)));
            }
            if (args[1].equalsIgnoreCase("rename")) {
                if (rankDataManager.getRank(rankName).getRankName().equals("player")) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.ConnotRenameDefaultRank"));
                    return true;
                }
                String newRankName = args[2];
                if (rankDataManager.getRank(rankName).getRankName().equals(newRankName)) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.CannotRenameSameName"));
                    return true;
                }
                if (rankDataManager.getRankNames().contains(newRankName)) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.CannotRenameExistingName"));
                    return true;
                }
                rankDataManager.renameRank(rankData.getRankName(), newRankName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Rename")
                        .replace("%OLD_RANK_NAME%", rankName)
                        .replace("%NEW_RANK_NAME%", newRankName));
            }
            if (args[1].equalsIgnoreCase("renameListName") || args[1].equalsIgnoreCase("renameList")) {
                String newRankListName = args[2];
                if (rankDataManager.getRank(rankName).getRankListName().equals(newRankListName)) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.CannotRenameSameListName"));
                    return true;
                }
                rankDataManager.renameListName(rankData.getRankListName(), newRankListName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.RenameListName")
                        .replace("%RANK_NAME%", rankData.getRankName())
                        .replace("%RANK_LIST_NAME%", newRankListName));
            }
            if (args[1].equalsIgnoreCase("renameDislpayName") || args[1].equalsIgnoreCase("renameDisplay")) {
                String newRankDisplayName = args[2];
                if (rankDataManager.getRank(rankName).getRankDisplayName().equals(newRankDisplayName)) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.CannotRenameSameDisplayName"));
                    return true;
                }
                rankDataManager.renameDisplayName(rankData.getRankDisplayName(), newRankDisplayName);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.RenameDisplayName")
                        .replace("%RANK_NAME%", rankData.getRankName())
                        .replace("%RANK_LIST_NAME%", newRankDisplayName));
            }
        }
        else  {
            if(!(args[0].equalsIgnoreCase("create"))) {
                sendCompleteHelp(player);
                return true;
            }
            String rankName = args[1];
            int rankID = Integer.parseInt(args[2]);
            String rankListName = args[3];
            String rankDisplayName = args[4];
            String[] argsCollctor = Arrays.toString(args).split(";");
            String readjustPermissions = Arrays.toString(argsCollctor).replace(rankName + ";", "")
                    .replace(rankID + ";", "").replace(rankListName + ";", "")
                    .replace(rankDisplayName + ";", "");
            String[] rankPermissions = readjustPermissions.split(";");
            if (rankDataManager.getRank(rankName) != null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankAlreadyExisting"));
                return true;
            }
            if (rankDataManager.getRankIDs().contains(rankID)) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.IDAlreadyTaken"));
                return true;
            }
            rankDataManager.loadRank(rankName, rankID, rankListName, rankDisplayName, rankPermissions);
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Create")
                    .replace("%RANK_NAME%", rankName));
        }
        return false;
    }

    private void sendRankInformation(Player player, String rankName) {
        UUID uuid = player.getUniqueId();
        if (rankDataManager.getRank(rankName) == null) {
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Rank.Error.RankNotExisting"));
            return;
        }
        RankData playerRankData = rankDataManager.getRank(rankName);
        player.sendMessage(prefix + "§7----------[§6 Rank§7-§6Info§7]----------");
        player.sendMessage(language.getColoredString(uuid, "Command.Rank.Information.Name").replace("%RANK_NAME%", playerRankData.getRankName()));
        player.sendMessage(language.getColoredString(uuid, "Command.Rank.Information.ID").replace("%RANK_ID%", String.valueOf(playerRankData.getRankID())));
        player.sendMessage(language.getColoredString(uuid, "Command.Rank.Information.List").replace("%RANK_LIST_NAME%", playerRankData.getRankListName()));
        player.sendMessage(language.getColoredString(uuid, "Command.Rank.Information.Display").replace("%RANK_DISPLAY_NAME%", playerRankData.getRankDisplayName()));
        TextComponent component = new TextComponent(language.getColoredString(uuid, "Command.Rank.Information.Permissions"));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank openRankPermissions " + rankName));
        player.spigot().sendMessage(component);
        player.sendMessage(prefix + "§7----------[§6 Rank§7-§6Info§7]----------");
    }

    private void sendCompleteHelp(Player player) {
        commandUtil.sendRankCompleteHelp(player);
    }

    private void sendRankHelp(Player player, String rankName) {
        commandUtil.sendRankHelp(player, rankName);
    }

}
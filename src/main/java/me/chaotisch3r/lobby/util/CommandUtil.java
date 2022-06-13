package me.chaotisch3r.lobby.util;

import lombok.Getter;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 21:49 20.04.2022
 **/

@Getter
public class CommandUtil {

    public final List<Player> build = new ArrayList<>();

    private final String prefix = Lobby.getInstance().getPrefix();

    private UUID uuid;

    private Language language;

    private PlayerDataManager playerDataManager;
    private RankData rankData;

    public CommandUtil(Language language, PlayerDataManager playerDataManager) {
        this.language = language;
        this.playerDataManager = playerDataManager;
    }

    public CommandUtil(UUID uuid) {
        this.uuid = uuid;
        this.rankData = this.playerDataManager.getPlayer(uuid).getRank();
    }

    public void sendWarpHelp(Player player) {
        TextComponent warp = new TextComponent("-§6 /warp§7 <§bwarpName§7> - "
                + language.getColoredString(uuid, "Command.Warp.Usage.Warp"));
        TextComponent list = new TextComponent("-§6 /warp list§7 - "
                + language.getColoredString(uuid, "Command.Warp.Usage.List"));
        TextComponent set = new TextComponent("-§6 /warp set§7 <§bwarpName§7> - "
                + language.getColoredString(uuid, "Command.Warp.Usage.Set"));
        TextComponent remove = new TextComponent("-§6 /warp remove§7 <§bwarpName§7> - "
                + language.getColoredString(uuid, "Command.Warp.Usage.Remove"));
        TextComponent update = new TextComponent("-§6 /warp update§7 <§bwarpName§7> - "
                + language.getColoredString(uuid, "Command.Warp.Usage.Update"));
        TextComponent rename = new TextComponent("-§6 /warp rename§7 <§5oldWarpnNme§7> <§bnewWarpName§7> - "
                + language.getColoredString(uuid, "Command.Warp.Usage.Rename"));

        warp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Warp.Hover.Warp"))));
        list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Warp.Hover.List"))));
        set.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Warp.Hover.Set"))));
        remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Warp.Hover.Remove"))));
        update.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Warp.Hover.Update"))));
        rename.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Warp.Hover.Rename"))));

        warp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp "));
        list.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp list"));
        set.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp set "));
        remove.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp remove "));
        update.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp update "));
        rename.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp rename "));

        if (!(player.isOp() || rankData.hasPermission("lobby.*") || rankData.hasPermission("lobby.warp"))) {
            player.sendMessage(prefix + language.getColoredString(player.getUniqueId(), "Command.Overall.NoPermission"));
            player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
            player.spigot().sendMessage(list);
            player.spigot().sendMessage(warp);
            player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
            return;
        }

        player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
        player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
        player.spigot().sendMessage(list);
        player.spigot().sendMessage(warp);
        player.spigot().sendMessage(set);
        player.spigot().sendMessage(remove);
        player.spigot().sendMessage(update);
        player.spigot().sendMessage(rename);
        player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
    }

    public void sendWorldHelp(Player player) {
        TextComponent world = new TextComponent("§6/world§7 - "
                + language.getColoredString(uuid, "Command.World.Usage.Information"));
        TextComponent info = new TextComponent("§6/world information§7 - "
                + language.getColoredString(uuid, "Command.World.Usage.Information"));
        TextComponent join = new TextComponent("§6/world join§7 <§bworldName§7> - "
                + language.getColoredString(uuid, "Command.World.Usage.Join"));
        TextComponent create = new TextComponent("§6/world create§7 <§bname§7> - "
                + language.getColoredString(uuid, "Command.World.Usage.Create"));
        TextComponent create2 = new TextComponent("§6/world create§7 <§bname§7> <§ddimension>§7 - "
                + language.getColoredString(uuid, "Command.World.Usage.CreateDimension"));
        TextComponent delete = new TextComponent("§6/world delete§7 <§bworldName§7> - "
                + language.getColoredString(uuid, "Command.World.Usage.Delete"));

        world.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Wold.Hover.World"))));
        info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Wold.Hover.Join"))));
        join.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Wold.Hover.Information"))));
        create.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Wold.Hover.Create"))));
        create2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.World.Hover.CreateDimension"))));
        delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Wold.Hover.Delete"))));

        world.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world "));
        info.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world information"));
        join.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world join "));
        create.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world crteate "));
        create2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world create "));
        delete.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world delete "));

        player.sendMessage(prefix + "§7----------[§6 World§7-§6Help§7]----------");
        player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
        player.spigot().sendMessage(world);
        player.spigot().sendMessage(info);
        player.spigot().sendMessage(join);
        player.spigot().sendMessage(create);
        player.spigot().sendMessage(create2);
        player.spigot().sendMessage(delete);
        player.sendMessage(prefix + "§7----------[§6 World§7-§6Help§7]----------");
    }

    public void sendLobbyHelp(Player player) {
        TextComponent lobby = new TextComponent("-§6 /lobby§7 - "
                + language.getColoredString(uuid, "Command.Lobby.Usage.Teleport"));
        TextComponent setup = new TextComponent("-§6 /lobby setup setlobby§7 - "
                + language.getColoredString(uuid, "Command.Lobby.Usage.SetLobby"));

        lobby.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Comman.Lobby.Hover.Lobby"))));
        setup.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Lobby.Hover.Setup"))));

        lobby.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lobby"));
        setup.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lobby setup setlobby"));

        if (!(player.isOp() || rankData.hasPermission("lobby.*") || rankData.hasPermission("lobby.lobby"))) {
            player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
            player.spigot().sendMessage(lobby);
            player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
            return;
        }

        player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
        player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
        player.spigot().sendMessage(lobby);
        player.spigot().sendMessage(setup);
        player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
    }

    public void sendPingHelp(Player player) {
        TextComponent pingOwn = new TextComponent("-§6 /ping§7 - "
                + language.getColoredString(uuid, "Command.Ping.Usage.Own"));
        TextComponent pingAnother = new TextComponent("-§6 /ping <§bplayerName§7> - "
                + language.getColoredString(uuid, "Command.Ping.Usage.Another"));

        pingOwn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Ping.Hover.Own"))));
        pingAnother.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Ping.Hover.Another"))));

        pingAnother.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ping "));
        pingOwn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ping"));

        if (!(player.isOp() || rankData.hasPermission("lobby.*") || rankData.hasPermission("lobby.ping"))) {
            player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
            player.spigot().sendMessage(pingOwn);
            player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
            return;
        }

        player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
        player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
        player.spigot().sendMessage(pingOwn);
        player.spigot().sendMessage(pingAnother);
        player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
    }

    public void sendRankCompleteHelp(Player player) {
        // /rank create/...
        TextComponent c1 = new TextComponent("-§6 /rank create§7 <§brankName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.General.Create"));
        TextComponent c2 = new TextComponent("-§6 /rank delete§7 <§brankName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.General.Delete"));
        TextComponent c3 = new TextComponent("-§6 /rank set§7 <§bplayerName§7> <§drankName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.General.Set"));
        TextComponent c4 = new TextComponent("-§6 /rank remove§7 <§bplayerName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.General.Remove"));

        c1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.General.Create"))));
        c2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.General.Delete"))));
        c3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.General.Set"))));
        c4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.General.Remove"))));

        c1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank create "));
        c2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank delete "));
        c3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank set "));
        c4.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank remove "));

        player.sendMessage(prefix + "§7----------[§6 Rank§7-§6Help§7]----------");
        player.spigot().sendMessage(c1);
        player.spigot().sendMessage(c2);
        player.spigot().sendMessage(c3);
        player.spigot().sendMessage(c4);
        player.sendMessage(prefix + "§7----------[§6 Rank§7-§6Help§7]----------");
    }

    public void sendRankHelp(Player player, String rankName) {
        // /rank <rankname> ...
        TextComponent c1 = new TextComponent("-§6 /rank " + rankName + " information§7 - "
                + language.getColoredString(uuid, "Command.Rank.Usage.Rank.Information"));
        TextComponent c2 = new TextComponent("-§6 /rank " + rankName + " readjustID§7 <§bnewID§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.Rank.ReadjustID"));
        TextComponent c3 = new TextComponent("-§6 /rank " + rankName + " rename§7 <§bnewRankName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.Rank.Rename"));
        TextComponent c4 = new TextComponent("-§6 /rank " + rankName + " renameList§7 <§bnewListName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.Rank.RenameList"));
        TextComponent c5 = new TextComponent("-§6 /rank " + rankName + " renameDisplay§7 <§bnewListName§7> - "
                + language.getColoredString(uuid, "Command.Rank.Usage.Rank.RenameDisplay"));

        c1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.Rank.Information"))));
        c2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.Rank.ReadjustID"))));
        c3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.Rank.Rename"))));
        c4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.Rank.RenameList"))));
        c5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Rank.Hover.Rank.RenameDisplay"))));

        c1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank " + rankName + " information"));
        c2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank " + rankName + " redajustID "));
        c3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank " + rankName + " rename "));
        c4.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank " + rankName + " renameList "));
        c5.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/rank " + rankName + " renameDisplay "));

        player.sendMessage(prefix + "§7----------[§6 Rank§7-§6Help§7]----------");
        player.spigot().sendMessage(c1);
        player.spigot().sendMessage(c2);
        player.spigot().sendMessage(c3);
        player.spigot().sendMessage(c4);
        player.spigot().sendMessage(c5);
        player.sendMessage(prefix + "§7----------[§6 Rank§7-§6Help§7]----------");
    }

    public void sendCoinsHelp(Player player, String targetName) {
        TextComponent c1 = new TextComponent("-§6 /coins§7 - "
                + language.getColoredString(uuid, "Command.Coins.Usage.GetCoins"));
        TextComponent c2 = new TextComponent("-§6 /coins§7 <§bplayerName§7> - "
                + language.getColoredString(uuid, "Command.Coins.Usage.GetCoins"));
        TextComponent c3 = new TextComponent("-§6 /coins set <§bplayerName§7> <§dcoins§7> - "
                + language.getColoredString(uuid, "Command.Coins.Usage.SetCoins"));
        TextComponent c4 = new TextComponent("-§6 /coins add <§bplayerName§7> <§dcoins§7> - "
                + language.getColoredString(uuid, "Command.Coins.Usage.AddCoins"));
        TextComponent c5 = new TextComponent("-§6 /coins remove <§bplayerName§7> <§dcoins§7> - "
                + language.getColoredString(uuid, "Command.Coins.Usage.RemoveCoins"));

        c1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Coins.Hover.GetCoins"))));
        c2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Coins.Hover.GetCoins"))));
        c3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Coins.Hover.SetCoins"))));
        c4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Coins.Hover.AddCoins"))));
        c5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(language.getColoredString(uuid, "Command.Coins.Hover.RemoveCoins"))));

        c1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins"));
        c2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins "));
        c3.setClickEvent(!targetName.equals("") ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins set " + targetName)
                : new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins set"));
        c4.setClickEvent(!targetName.equals("") ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins add " + targetName)
                : new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins add"));
        c5.setClickEvent(!targetName.equals("") ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins remove " + targetName)
                : new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/coins remove"));

        if (!(player.isOp() || rankData.hasPermission("lobby.*") || rankData.hasPermission("lobby.coins"))) {
            player.sendMessage(prefix + "§7----------[§6 Coins§7-§6Help§7]----------");
            player.spigot().sendMessage(c1);
            player.spigot().sendMessage(c2);
            player.sendMessage(prefix + "§7----------[§6 Coins§7-§6Help§7]----------");
            return;
        }
        player.sendMessage(prefix + "§7----------[§6 Coins§7-§6Help§7]----------");
        player.spigot().sendMessage(c1);
        player.spigot().sendMessage(c2);
        player.spigot().sendMessage(c3);
        player.spigot().sendMessage(c4);
        player.spigot().sendMessage(c5);
        player.sendMessage(prefix + "§7----------[§6 Coins§7-§6Help§7]----------");
    }

    public void sendLanguageHelp(Player player) {
        TextComponent c1 = new TextComponent("§6/language§7 - "
                + language.getColoredString(uuid, "Command.Language.Usage.Use"));
        TextComponent c2 = new TextComponent("§6/language list§7 - "
                + language.getColoredString(uuid, "Command.Language.Usage.List"));
        TextComponent c3 = new TextComponent("§6/language change§7 <§blanguage§7> - " + language.getColoredString(uuid, "Command.Language.Usage.Change"));

        c1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Language.Hover.Use"))));
        c2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Language.Hover.List"))));
        c3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Language.Hover.Change"))));

        c1.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/language"));
        c2.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/language list"));
        c3.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/language change "));
        player.sendMessage(prefix + "§7----------[§6 Language§7-§6Help§7]----------");
        player.spigot().sendMessage(c1);
        player.spigot().sendMessage(c2);
        player.spigot().sendMessage(c3);
        player.sendMessage(prefix + "§7----------[§6 Language§7-§6Help§7]----------");
    }

}

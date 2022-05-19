package me.chaotisch3r.lobby.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.database.Language;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.awt.*;
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
@RequiredArgsConstructor
public class CommandUtil {

    public final List<Player> build = new ArrayList<>();
    private final String prefix = Lobby.getInstance().getPrefix();
    private Language language;
    private UUID uuid;

    public CommandUtil(UUID uuid) {
        this.uuid = uuid;
    }

    public CommandUtil(Language language) {
        this.language = language;
    }

    public void sendWarpHelp(Player player) {
        TextComponent warp = new TextComponent("-§6 /warp§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Warp"));
        TextComponent list = new TextComponent("-§6 /warp list§7 - " + language.getColoredString(uuid, "Command.Warp.Usage.List"));
        TextComponent set = new TextComponent("-§6 /warp set§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Set"));
        TextComponent remove = new TextComponent("-§6 /warp remove§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Remove"));
        TextComponent update = new TextComponent("-§6 /warp update§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Update"));
        TextComponent rename = new TextComponent("-§6 /warp rename§7 <§5oldwarpname§7> <§bnewwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Rename"));
        warp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Warp.Hover.Warp"))));
        list.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Warp.Hover.List"))));
        set.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Warp.Hover.Set"))));
        remove.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Warp.Hover.Remove"))));
        update.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Warp.Hover.Update"))));
        rename.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Warp.Hover.Rename"))));
        warp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp "));
        list.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp list"));
        set.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp set "));
        remove.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp remove "));
        update.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp update "));
        rename.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/warp rename "));
        if (!(player.isOp() || player.hasPermission("lobbyq.*") || player.hasPermission("lobby.warp"))) {
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
        TextComponent world = new TextComponent("§6/world§7 - " + language.getColoredString(uuid, "Command.World.Usage.Information"));
        TextComponent info = new TextComponent("§6/world information§7 - " + language.getColoredString(uuid, "Command.World.Usage.Information"));
        TextComponent join = new TextComponent("§6/world join§7 <§bworldname§7> - " + language.getColoredString(uuid, "Command.World.Usage.Join"));
        TextComponent create = new TextComponent("§6/world create§7 <§bname§7> - " + language.getColoredString(uuid, "Command.World.Usage.Create"));
        TextComponent delete = new TextComponent("§6/world delete§7 <§bworldname§7> - " + language.getColoredString(uuid, "Command.World.Usage.Delete"));
        world.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Wold.Hover.World"))));
        info.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Wold.Hover.Join"))));
        join.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Wold.Hover.Information"))));
        create.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Wold.Hover.Create"))));
        delete.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Wold.Hover.Delete"))));
        world.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world "));
        info.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world information"));
        join.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world join "));
        create.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world crteate "));
        delete.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/world delete "));
        player.sendMessage(prefix + "§7----------[§6 World§7-§6Help§7]----------");
        player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.HelpInformation"));
        player.spigot().sendMessage(world);
        player.spigot().sendMessage(info);
        player.spigot().sendMessage(join);
        player.spigot().sendMessage(create);
        player.spigot().sendMessage(delete);
        player.sendMessage(prefix + "§7----------[§6 World§7-§6Help§7]----------");
    }

    public void sendLobbyHelp(Player player) {
        TextComponent lobby = new TextComponent("-§6 /lobby§7 - " + language.getColoredString(uuid, "Command.Lobby.Usage.Teleport"));
        TextComponent setup = new TextComponent("-§6 /lobby setup setlobby§7 - " + language.getColoredString(uuid, "Command.Lobby.Usage.SetLobby"));
        lobby.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Comman.Lobby.Hover.Lobby"))));
        lobby.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lobby"));
        setup.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Lobby.Hover.Setup"))));
        setup.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lobby setup setlobby"));
        if (!(player.isOp() || player.hasPermission("lobbyq.*") || player.hasPermission("lobby.warp"))) {
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
        TextComponent pingOwn = new TextComponent("-§6 /ping§7 - " + language.getColoredString(uuid, "Command.Ping.Usage.Own"));
        TextComponent pingAnother = new TextComponent("-§6 /ping <§bplayername§7> - " + language.getColoredString(uuid, "Command.Ping.Usage.Another"));
        pingOwn.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Ping.Hover.Own"))));
        pingOwn.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ping"));
        pingAnother.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(uuid, "Command.Ping.Hover.Another"))));
        pingAnother.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ping "));
        if (!(player.isOp() || player.hasPermission("lobby.*") || player.hasPermission("lobby.warp"))) {
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
    }

    public void sendRankHelp(Player player) {

    }

}

package me.chaotisch3r.lobby.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.database.WarpDataManager;
import org.bukkit.entity.Player;

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
    private final Language language;
    private UUID uuid;

    public void sendWarpHelp(Player player) {
        uuid = player.getUniqueId();
        if (!(player.isOp() || player.hasPermission("lobbyq.*") || player.hasPermission("lobby.warp"))) {
            player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
            player.sendMessage("-§6 /warp list§7 - " + language.getColoredString(uuid, "Command.Warp.Usage.List"));
            player.sendMessage("-§6 /warp§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Warp"));
            player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
            return;
        }
        player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
        player.sendMessage("-§6 /warp list§7 - " + language.getColoredString(uuid, "Command.Warp.Usage.List"));
        player.sendMessage("-§6 /warp§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Warp"));
        player.sendMessage("-§6 /warp set§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Set"));
        player.sendMessage("-§6 /warp remove§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Remove"));
        player.sendMessage("-§6 /warp update§7 <§bwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Update"));
        player.sendMessage("-§6 /warp rename§7 <§5oldwarpname§7> <§bnewwarpname§7> - " + language.getColoredString(uuid, "Command.Warp.Usage.Rename"));
        player.sendMessage(prefix + "§7----------[§6 Warp§7-§6Help§7]----------");
    }

    public void sendWorldHelp(Player player) {
        uuid = player.getUniqueId();
        player.sendMessage(prefix + "§7----------[§6 World§7-§6Help§7]----------");
        player.sendMessage("§6/world§7 - " + language.getColoredString(uuid, "Command.World.Usage.Information"));
        player.sendMessage("§6/world information§7 - " + language.getColoredString(uuid, "Command.World.Usage.Information"));
        player.sendMessage("§6/world join§7 <§bworldname§7> - " + language.getColoredString(uuid, "Command.World.Usage.Join"));
        player.sendMessage("§6/world create§7 <§bname§7> - " + language.getColoredString(uuid, "Command.World.Usage.Create"));
        player.sendMessage("§6/world delete§7 <§bworldname§7> - " + language.getColoredString(uuid, "Command.World.Usage.Delete"));
        player.sendMessage(prefix + "§7----------[§6 World§7-§6Help§7]----------");
    }

    public void sendLobbyHelp(Player player) {
    }

    public void sendPingHelp(Player player) {
        uuid = player.getUniqueId();
        if (!(player.isOp() || player.hasPermission("lobbyq.*") || player.hasPermission("lobby.warp"))) {
            player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
            player.sendMessage("-§6 /ping§7 - " + language.getColoredString(uuid, "Command.Ping.Usage.Own"));
            player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
            return;
        }
        player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
        player.sendMessage("-§6 /ping§7 - " + language.getColoredString(uuid, "Command.Ping.Usage.Own"));
        player.sendMessage("-§6 /ping <§bplayername§7> - " + language.getColoredString(uuid, "Command.Ping.Usage.Another"));
        player.sendMessage(prefix + "§7----------[§6 Ping§7-§6Help§7]----------");
    }
}

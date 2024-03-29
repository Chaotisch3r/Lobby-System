package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.WorldDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.ItemManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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
 * Created for Lobby-System, 12:44 23.04.2022
 **/

@RequiredArgsConstructor
public class WorldCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil;
    private final ItemManager itemManager;

    private final WorldDataManager worldDataManager;
    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§7Du kannst diesen Befehl nur als Spieler ausführen.");
            return true;
        }
        UUID uuid = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayer(uuid);
        if(!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.world"))) {
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.NoPermission"));
            return true;
        }
        if(args.length == 0) {
            sendWorldInformation(player);
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                itemManager.openWorldListInventory(player);
            }
            if(args[0].equalsIgnoreCase("information") || args[0].equalsIgnoreCase("info")) {
                sendWorldInformation(player);
                return true;
            }
            sendHelp(player);
        } else if(args.length == 2) {
            String worldName = args[1];
            if(args[0].equalsIgnoreCase("join")) {
                World world;
                if((world = Bukkit.getWorld(worldName)) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Error.WorldNotExisting"));
                    return true;
                }
                if(player.getWorld() == world) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Error.WorldAlreadyJoined"));
                    return true;
                }
                player.teleport(world.getSpawnLocation());
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Join")
                        .replace("%WORLD%", world.getName()));
                return true;
            }
            if(args[0].equalsIgnoreCase("create")) {
                if(Bukkit.getWorld(worldName) != null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Error.WorldAlreadyExisting"));
                    return true;
                }
                World world = Bukkit.createWorld(new WorldCreator(worldName));
                Bukkit.getWorlds().add(world);
                worldDataManager.loadWorld(world);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Create.1")
                        .replace("%WORLD%", worldName));
                TextComponent component = new TextComponent(language.getColoredString(uuid, "Command.World.Create.2"));
                component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world join " + worldName));
                player.spigot().sendMessage(component);
                return true;
            }
            if(args[0].equalsIgnoreCase("delete")) {
                World world;
                if((world = Bukkit.getWorld(worldName)) == null) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Error.WorldNotExisting"));
                    return true;
                }
                worldDataManager.addWorldToDeletedList(world);
                worldDataManager.removeWorld(world);
                Bukkit.unloadWorld(world, false);
                Bukkit.getWorlds().remove(world);
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Delete")
                        .replace("%WORLD%", world.getName()));
                return true;
            }
            sendHelp(player);
        }
        else if(args.length == 3) {
            if(!(args[0].equalsIgnoreCase("create"))) {
                sendHelp(player);
                return true;
            }
            String worldName = args[1];
            String destination = args[2];
            World.Environment environment = World.Environment.valueOf(destination);
            if(!(Arrays.stream(World.Environment.values()).toList().contains(environment))) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Error.EnvironmentNotExisting"));
                return true;
            }
            World world = Bukkit.createWorld(new WorldCreator(worldName).environment(environment));
            Bukkit.getWorlds().add(world);
            worldDataManager.loadWorld(world);
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.World.Create.1")
                    .replace("%WORLD%", worldName));
            TextComponent component = new TextComponent(language.getColoredString(uuid, "Command.World.Create.2"));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/world join " + worldName));
            player.spigot().sendMessage(component);
        }
        else sendHelp(player);
        return false;
    }

    private void sendWorldInformation(Player player) {
        World world = player.getWorld();
        player.sendMessage(language.getColoredString(player.getUniqueId(), "Command.World.Information.World")
                + world.getName());
        player.sendMessage();
        TextComponent component = new TextComponent(language.getColoredString(player.getUniqueId(), "Command.World.Information.Spawn")
                + " §6X§7= " + world.getSpawnLocation().getX() + " §6Y§7= " + world.getSpawnLocation().getY() + " §6Z§7= " + world.getSpawnLocation().getZ() + " §6Yaw§7= "
                + world.getSpawnLocation().getYaw() + " §6Pitch§7= " + world.getSpawnLocation().getPitch());
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(language.getColoredString(player.getUniqueId(), "Command.World.Hover.TP"))));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + player.getName() + " " + world.getSpawnLocation().getX()
                + " " + world.getSpawnLocation().getY() + " " + world.getSpawnLocation().getZ() + " " + world.getSpawnLocation().getYaw() + " " + world.getSpawnLocation().getPitch() + " "));
        player.spigot().sendMessage(component);
        player.sendMessage(language.getColoredString(player.getUniqueId(), "Command.World.Information.Players") + world.getPlayers().size());
        player.sendMessage(language.getColoredString(player.getUniqueId(), "Command.World.Information.Seed") + world.getSeed());
    }

    private void sendHelp(Player player) {
        commandUtil.sendWorldHelp(player);
    }

}

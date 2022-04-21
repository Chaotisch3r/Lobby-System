package me.chaotisch3r.lobby.command.util;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 22:49 20.04.2022
 **/

public class TabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if(!(sender instanceof Player player)) return tabComplete;
        if(command.getName().equalsIgnoreCase("build")) {
            if(!(player.isOp() || player.hasPermission("lobby.*") || player.hasPermission("lobby.build")))
                return tabComplete;
            if(args.length == 1) {
                tabComplete.add("help");
                Bukkit.getOnlinePlayers().forEach(players -> tabComplete.add(players.getName()));
            }
        }
        if(command.getName().equalsIgnoreCase("language") || command.getName().equalsIgnoreCase("lang")) {
            if(args.length == 1) {
                tabComplete.add("help");
                tabComplete.add("change");
                tabComplete.add("list");
                Bukkit.getOnlinePlayers().forEach(players -> tabComplete.add(players.getName()));
            }
            else if (args.length == 2) {
                if(!args[0].equalsIgnoreCase("change")) return tabComplete;
                tabComplete.add("de");
                tabComplete.add("en");
            }
        }
        return tabComplete;
    }
}

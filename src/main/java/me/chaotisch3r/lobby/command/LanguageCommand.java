package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.PlayerData;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.util.Langauge;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 22:59 20.04.2022
 **/

@RequiredArgsConstructor
public class LanguageCommand implements CommandExecutor {

    private final String prefix = Lobby.getInstance().getPrefix();
    private final Langauge langauge;
    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§7Dieser Befehl kann nur als Spieler ausgeführt werden,");
            return true;
        }
        if(args.length == 0) {
            player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Language.Use")
                    .replace("%PLAYER%", player.getName())
                    .replace("%LANGUAGE%", langauge.getLocale(player.getUniqueId()).getDisplayName()));
        }
        else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("help")) {
                sendHelp(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("list")) {
                player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Language.List"));
                player.sendMessage("§7-§6 de§7 (Deutsch)");
                player.sendMessage("§7-§6 en§7 (English)");
                System.out.println(langauge.getLocales());
                return true;
            }
            if(args[0].equalsIgnoreCase("change")) {
                sendHelp(player);
                return true;
            }
            String targetName = args[0];
            if (Bukkit.getPlayer(targetName) == null) {
                player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Overall.UknownPlayer")
                        .replace("%TARGET%", targetName));
                return true;
            }
            Player target = Bukkit.getPlayer(targetName);
            player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Language.Use")
                    .replace("%PLAYER%", target.getName())
                    .replace("%LANGUAGE%", langauge.getLocale(target.getUniqueId()).getLanguage()));
        }
        else if(args.length == 2) {
            String lang = args[1];
            if(!(args[0].equalsIgnoreCase("change"))) {
                sendHelp(player);
                return true;
            }
            if(Locale.forLanguageTag(lang) == null) {
                player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Language.Error.UknownLanguage"));
                return true;
            }
            Locale locale = Locale.forLanguageTag(lang);
            if(langauge.getLocale(player.getUniqueId()).equals(locale)) {
                player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Language.Error.SameLanguage"));
                return true;
            }
            langauge.setLocale(player.getUniqueId(), locale);
            player.sendMessage(prefix + langauge.getColoredString(player.getUniqueId(), "Command.Language.Change")
                    .replace("%LANGUAGE%", locale.getDisplayName()));
        }else {
            sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(prefix + "§7----------[§6 Language§7-§6Help§7]----------");
        player.sendMessage("§6/language§7 - " + langauge.getColoredString(player.getUniqueId(), "Command.Language.Usage.Use"));
        player.sendMessage("§6/language list§7 - " + langauge.getColoredString(player.getUniqueId(), "Command.Language.Usage.List"));
        player.sendMessage("§6/language change§7 <§blanguage§7> - " + langauge.getColoredString(player.getUniqueId(), "Command.Language.Usage.Change"));
        player.sendMessage(prefix + "§7----------[§6 Language§7-§6Help§7]----------");
    }

}
package me.chaotisch3r.lobby.command;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.filemanagement.MessageConfig;
import me.chaotisch3r.lobby.util.CommandUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.UUID;

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
    
    private final Language language;
    private final CommandUtil commandUtil;
    private final MessageConfig messageConfig;
    
    private final PlayerDataManager playerDataManager;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(prefix + "§7Dieser Befehl kann nur als Spieler ausgeführt werden,");
            return true;
        }
        UUID uuid = player.getUniqueId();
        if(args.length == 0) {
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.Use")
                    .replace("%PLAYER%", player.getName())
                    .replace("%LANGUAGE%", language.getLocale(uuid).getDisplayName()));
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("reload")) {
                PlayerData playerData = playerDataManager.getPlayer(uuid);
                if(!(player.isOp() || playerData.getRank().hasPermission("lobby.*") || playerData.getRank().hasPermission("lobby.language"))) {
                    player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.NoPermission"));
                    return true;
                }
                messageConfig.reloadMessageConfig();
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.Reload"));
            }
            if(args[0].equalsIgnoreCase("help")) {
                sendHelp(player);
                return true;
            }
            if(args[0].equalsIgnoreCase("list")) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.List"));
                player.sendMessage("§7-§6 de§7 (Deutsch)");
                player.sendMessage("§7-§6 en§7 (English)");
                System.out.println(language.getLocales());
                return true;
            }
            if(args[0].equalsIgnoreCase("change")) {
                sendHelp(player);
                return true;
            }
            String targetName = args[0];
            Player target;
            if((target = Bukkit.getPlayer(targetName)) == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Overall.UnknownPlayer")
                        .replace("%TARGET%", targetName));
                return true;
            }
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.Use")
                    .replace("%PLAYER%", target.getName())
                    .replace("%LANGUAGE%", language.getLocale(target.getUniqueId()).getLanguage()));
        } else if(args.length == 2) {
            String lang = args[1];
            if(!(args[0].equalsIgnoreCase("change"))) {
                sendHelp(player);
                return true;
            }
            if(Locale.forLanguageTag(lang) == null) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.Error.UnknownLanguage"));
                return true;
            }
            Locale locale = Locale.forLanguageTag(lang);
            if(language.getLocale(player.getUniqueId()).equals(locale)) {
                player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.Error.SameLanguage"));
                return true;
            }
            language.setLocale(uuid, locale);
            player.sendMessage(prefix + language.getColoredString(uuid, "Command.Language.Change")
                    .replace("%LANGUAGE%", locale.getDisplayName()));
        } else {
            sendHelp(player);
        }
        return false;
    }

    private void sendHelp(Player player) {
        commandUtil.sendLanguageHelp(player);
    }

}
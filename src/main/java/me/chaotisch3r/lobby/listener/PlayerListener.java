package me.chaotisch3r.lobby.listener;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.LobbyData;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.LobbyDataManager;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.SettingsDataManager;
import me.chaotisch3r.lobby.util.CommandUtil;
import me.chaotisch3r.lobby.util.ItemManager;
import me.chaotisch3r.lobby.util.UIManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Arrays;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:54 - 20.04.2022
 **/

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final CommandUtil commandUtil = Lobby.getInstance().getCommandUtil();
    private final ItemManager itemManager;

    private final PlayerDataManager playerDataManager;
    private final LobbyDataManager lobbyDataManager;
    private final SettingsDataManager settingsDataManager;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(AsyncPlayerPreLoginEvent event) {
        playerDataManager.loadPlayer(event.getUniqueId(), event.getName(), event.getAddress().getHostAddress());
        language.loadLocale(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerQuitEvent event) {
        PlayerData playerData = playerDataManager.getPlayer(event.getPlayer().getUniqueId());
        playerDataManager.unloadPlayer(playerData.getUuid());
        LobbyData lobbyData = lobbyDataManager.getLobby(event.getPlayer().getUniqueId());
        lobbyData.setPlaytime(lobbyData.getPlaytime() + (System.currentTimeMillis() - lobbyData.getLogin()));
        lobbyDataManager.updateAsync(lobbyData);
        lobbyDataManager.unloadPlayer(lobbyData.getUuid());
        language.unloadLocale(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        lobbyDataManager.loadLobby(player.getUniqueId());
        settingsDataManager.loadSetting(player.getUniqueId());
        new UIManager(player);
        new CommandUtil(player.getUniqueId());
        event.setJoinMessage(null);
        Bukkit.getOnlinePlayers().forEach(players ->
                players.sendMessage(prefix + language.getColoredString(players.getUniqueId(), "Overall.JoinMessage")
                        .replace("%PLAYER%", player.getName())));
        player.setHealth(20);
        player.setFoodLevel(20);
        PermissionAttachment attachment = player.addAttachment(Lobby.getInstance());
        RankData rankData = playerDataManager.getPlayer(player.getUniqueId()).getRank();
        Arrays.stream(rankData.getRankPermissions()).forEach(permission -> {
            attachment.setPermission(permission, true);
        });
        commandUtil.permissions.put(player.getUniqueId(), attachment);
        if(player.getGameMode() == GameMode.CREATIVE && (player.isOp() || rankData.hasPermission("lobby.*")
                || rankData.hasPermission("lobby.build"))) {
            commandUtil.build.add(player);
            return;
        }
        itemManager.setStartEquip(player);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);
        Bukkit.getOnlinePlayers().forEach(players ->
                players.sendMessage(prefix + language.getColoredString(players.getUniqueId(), "Overall.QuitMessage")
                        .replace("%PLAYER%", player.getName())));
        commandUtil.build.remove(player);
        player.removeAttachment(commandUtil.permissions.get(player.getUniqueId()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR ||event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if(event.getItem() == null) return;
        if(!event.getItem().hasItemMeta()) return;
        if(event.getItem().getItemMeta().getDisplayName().equals(itemManager.getItemConfig().
                getItem(language.getLocale(event.getPlayer().getUniqueId()), "StartItem.Hider").getItemMeta().getDisplayName())) {
            itemManager.openHiderInventory(event.getPlayer());
        }
        if(event.getItem().getItemMeta().getDisplayName().equals(itemManager.getItemConfig().
                getItem(language.getLocale(event.getPlayer().getUniqueId()), "StartItem.Profile").getItemMeta().getDisplayName())) {
            itemManager.openProfileInventory(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        new UIManager(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
    }

}

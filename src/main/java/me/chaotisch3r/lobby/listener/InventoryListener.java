package me.chaotisch3r.lobby.listener;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.PlayerData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.LobbyDataManager;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.filemanagement.ItemConfig;
import me.chaotisch3r.lobby.util.ItemManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Locale;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 18:51 31.05.2022
 **/

@RequiredArgsConstructor
public class InventoryListener implements Listener {
    private final String prefix = Lobby.getInstance().getPrefix();

    private final Language language;
    private final ItemConfig itemConfig;
    private final ItemManager itemManager;

    private final PlayerDataManager playerDataManager;
    private final LobbyDataManager lobbyDataManager;

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player))
            return;
        //Setup
        UUID uuid = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayer(uuid);
        Locale locale = playerData.getLocale();
        if(event.getCurrentItem() == null)
            return;
        if(event.getCurrentItem().getItemMeta() == null)
            return;
        if(event.getAction() == InventoryAction.UNKNOWN)
            return;
        if(player.getGameMode() == GameMode.CREATIVE)
            return;
        try {
            //Inventory
            if(event.getClickedInventory() == player.getInventory()) {
                event.setCancelled(player.getGameMode() != GameMode.CREATIVE);
                return;
            }
            //Inventory: Profile
            if(event.getClickedInventory() == itemManager.getProfileInventory()) {
                if(!(event.getCurrentItem().getType() ==  itemConfig.getGlassPane(uuid).getType()))
                    player.sendMessage(prefix + "§4This is currently under construction§7.");
                event.setCancelled(true);
                return;
            }
            //Inventory: Hider
            if(event.getClickedInventory().getType() == itemManager.getHiderInventory().getType()) {
                player.sendMessage(prefix + "§4This is currently under construction§7.");
                event.setCancelled(true);
                return;
            }
            //Open Profile-Settings
            if(event.getCurrentItem().getItemMeta().getDisplayName().equals
                    (itemConfig.getItem(locale, "Profile.Settings").getItemMeta().getDisplayName())) {
                //openProfileSettings
            }
        }catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}

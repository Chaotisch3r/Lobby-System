package me.chaotisch3r.lobby.util;

import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.filemanagement.ItemConfig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 15:59 08.05.2022
 **/

@RequiredArgsConstructor
public class ItemManager {

    private final ItemConfig itemConfig;
    private final PlayerDataManager playerDataManager;
    private Locale locale;

    public void setStartEquip(Player player) {
        locale = playerDataManager.getPlayer(player.getUniqueId()).getLocale();
        ItemStack compass = itemConfig.getItem(locale, "StartItem.Compass");
        ItemStack hider = itemConfig.getItem(locale, "StartItem.Hider");
        ItemStack noGadget = itemConfig.getItem(locale, "StartItem.NoGadget");
        ItemStack gadget = itemConfig.getItem(locale, "StartItem.Gadget");
        ItemStack profile = itemConfig.getHead(locale, "StartItem.Profile", player);
        player.getInventory().setItem(0, compass);
        player.getInventory().setItem(1, hider);
        player.getInventory().setItem(4, noGadget);
        player.getInventory().setItem(7, gadget);
        player.getInventory().setItem(8, profile);
    }

}

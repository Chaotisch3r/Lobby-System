package me.chaotisch3r.lobby.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.database.Language;
import me.chaotisch3r.lobby.database.PlayerDataManager;
import me.chaotisch3r.lobby.database.WorldDataManager;
import me.chaotisch3r.lobby.filemanagement.ItemConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 15:59 08.05.2022
 **/

@Getter
@RequiredArgsConstructor
public class ItemManager {

    private final ItemConfig itemConfig;
    private final Language language;
    private Locale locale;
    private final PlayerDataManager playerDataManager;
    private final WorldDataManager worldDataManager;

    private final FileConfiguration config = Lobby.getInstance().getConfig();

    private Inventory hiderInventory;

    private Inventory rankPermissionsInventory;
    private Inventory worldListInventory;

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

    public void openHiderInventory(Player player) {
        //Item management
        locale = playerDataManager.getPlayer(player.getUniqueId()).getLocale();
        ItemStack setting = itemConfig.getItem(locale, "Hider.Setting");
        ItemStack all = itemConfig.getItem(locale, "Hider.All");
        ItemStack vip = itemConfig.getItem(locale, "Hider.VIP");
        ItemStack none = itemConfig.getItem(locale, "Hider.None");
        //Inventory management
        if(config.contains("Inventory.Hider.Slot")) {
            hiderInventory = Bukkit.createInventory(player, config.getInt("Inventory.Hider.Slot"));
        }
        else if(config.contains("Inventory.Hider.Type")) {
            InventoryType inventoryType = InventoryType.valueOf(config.getString("Inventory.Hider.Type"));
            hiderInventory = Bukkit.createInventory(player, inventoryType);
        }
        else {
            hiderInventory = Bukkit.createInventory(player, InventoryType.BREWING);
            hiderInventory.setItem(0, all);
            hiderInventory.setItem(1, vip);
            hiderInventory.setItem(2, none);
            hiderInventory.setItem(3, setting);
        }
        player.openInventory(hiderInventory);
    }

    // Command Inventories

    public void openRankPermissionInventory(Player player, RankData rankData) {
        UUID uuid = player.getUniqueId();
        rankPermissionsInventory = Bukkit.createInventory(player, 5*9, language.getColoredString(uuid, "Inventory.RankPermissions.Name")
                .replace("%RANK_NAME%", rankData.getRankName())
                .replace("%PERMISSIONS%", String.valueOf(rankData.getRankPermissions().length)));
        Arrays.stream(rankData.getRankPermissions()).forEach(permission -> {
            rankPermissionsInventory.addItem(new ItemBuilder(Material.PAPER, permission).get());
        });
        player.openInventory(rankPermissionsInventory);
    }

    public void openWorldListInventory(Player player) {
        UUID uuid = player.getUniqueId();
        worldListInventory = Bukkit.createInventory(player, 5*9, language.getColoredString(uuid, "Inventory.WorldList.Name")
                .replace("%WORLD_COUNT%", String.valueOf(worldDataManager.getWorlds().size())));
        worldDataManager.getWorlds().forEach(worldData -> {
            if (worldData.getEnvironment() == World.Environment.NORMAL)
                worldListInventory.addItem(new ItemBuilder(Material.GRASS, "§a" + worldData.getWorldName()).get());
            else if (worldData.getEnvironment() == World.Environment.NETHER)
                worldListInventory.addItem(new ItemBuilder(Material.NETHERRACK, "§c" + worldData.getWorldName()).get());
            else if (worldData.getEnvironment() == World.Environment.THE_END)
                worldListInventory.addItem(new ItemBuilder(Material.END_STONE, "&6" + worldData.getWorldName()).get());
        });
    }

}

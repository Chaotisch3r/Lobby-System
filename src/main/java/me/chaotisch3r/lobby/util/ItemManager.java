package me.chaotisch3r.lobby.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.data.RankData;
import me.chaotisch3r.lobby.database.*;
import me.chaotisch3r.lobby.filemanagement.ItemConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
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

    private Locale locale;

    private final ItemConfig itemConfig;
    private final Language language;
    private final PlayerDataManager playerDataManager;
    private final WorldDataManager worldDataManager;
    private final WarpDataManager warpDataManager;
    private final LobbyDataManager lobbyDataManager;
    private final SettingsDataManager settingsDataManager;

    private final FileConfiguration config = Lobby.getInstance().getConfig();

    private Inventory hiderInventory;
    private Inventory profileInventory;

    private Inventory rankPermissionsInventory;
    private Inventory worldListInventory;
    private Inventory warpListInventory;

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
        UUID uuid = player.getUniqueId();
        //Item management
        locale = playerDataManager.getPlayer(player.getUniqueId()).getLocale();
        ItemStack setting = itemConfig.getItem(locale, "Hider.Setting");
        ItemStack all = itemConfig.getItem(locale, "Hider.All");
        ItemStack vip = itemConfig.getItem(locale, "Hider.VIP");
        ItemStack none = itemConfig.getItem(locale, "Hider.None");
        ItemMeta settingMeta = setting.getItemMeta();
        List<String> lore = settingMeta.getLore();
        lore.forEach(line -> line.replace("%HIDER_STATUS%", settingsDataManager.getSettings(uuid).getHiderStatus()));
        settingMeta.setLore(lore);
        setting.setItemMeta(settingMeta);
        //Inventory management
        hiderInventory = Bukkit.createInventory(player, InventoryType.BREWING, language.getColoredString(uuid, "Inventory.Hider"));
        hiderInventory.setItem(0, all);
        hiderInventory.setItem(1, vip);
        hiderInventory.setItem(2, none);
        hiderInventory.setItem(3, setting);
        player.openInventory(hiderInventory);
    }

    public void openProfileInventory(Player player) {
        UUID uuid = player.getUniqueId();
        locale = playerDataManager.getPlayer(uuid).getLocale();
        //Item management
        ItemStack back = itemConfig.getItem(locale, "Profile.Back");
        ItemStack friends = itemConfig.getHead(locale, "Profile.Friends", player);
        ItemStack party = itemConfig.getItem(locale, "Profile.Party");
        ItemStack clan = itemConfig.getItem(locale, "Profile.Clan");
        ItemStack settings = itemConfig.getItem(locale, "Profile.Settings");
        ItemStack forward = itemConfig.getItem(locale, "Profile.Forward");
        //Inventory management
        profileInventory = Bukkit.createInventory(player, 6*9, language.getColoredString(uuid, "Inventory.Profile"));
        setGlassPane(uuid, profileInventory, 4);
        new ItemBuilder(back).build(profileInventory, 5, 0);
        new ItemBuilder(friends).build(profileInventory, 5, 2);
        new ItemBuilder(party).build(profileInventory, 5, 3);
        new ItemBuilder(clan).build(profileInventory, 5, 5);
        new ItemBuilder(settings).build(profileInventory, 5, 6);
        new ItemBuilder(forward).build(profileInventory, 5, 8);
        player.openInventory(profileInventory);
    }

    // Command Inventories

    public void openRankPermissionInventory(Player player, RankData rankData) {
        UUID uuid = player.getUniqueId();
        rankPermissionsInventory = Bukkit.createInventory(player, 5*9, language.getColoredString(uuid, "Inventory.RankPermissions.Name")
                .replace("%RANK_NAME%", rankData.getRankName())
                .replace("%PERMISSIONS%", String.valueOf(rankData.getRankPermissions().length)));
        Arrays.stream(rankData.getRankPermissions()).forEach(permission -> rankPermissionsInventory.addItem(new ItemBuilder(Material.PAPER, permission).get()));
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
        player.openInventory(worldListInventory);
    }

    public void openWarpListInventory(Player player) {
        UUID uuid = player.getUniqueId();
        warpListInventory = Bukkit.createInventory(player, 5*9, language.getColoredString(uuid, "Inventory.WarpList.Name")
                .replace("%WARP_COUNT%", String.valueOf(warpDataManager.getWarps().size())));
        warpDataManager.getWarps().forEach(warpData -> warpListInventory.addItem(new ItemBuilder(Material.NETHER_STAR, "§d" + warpData.getWarpName()).get()));
        player.openInventory(warpListInventory);
    }

    private void setGlassPane(UUID uuid, Inventory inventory, int line) {
        int i = switch (line) {
            case 1 -> 9;
            case 2 -> 18;
            case 3 -> 27;
            case 4 -> 36;
            case 5 -> 45;
            default -> -1;
        };
        int newI = i+9;
        for (; i < newI; i++) {
            if (inventory.getItem(i) == null)
                inventory.setItem(i, itemConfig.getGlassPane(uuid));
        }
    }
}

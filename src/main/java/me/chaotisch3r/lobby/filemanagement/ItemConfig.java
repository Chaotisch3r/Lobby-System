package me.chaotisch3r.lobby.filemanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 15:03 21.04.2022
 **/

@Getter
@RequiredArgsConstructor
public class ItemConfig {

    private final File file;
    private YamlConfiguration config;
    private Gson gson;


    public ItemConfig() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        file = new File(Lobby.getInstance().getDataFolder(), "items.yml");
        loadConfig();
        config.options().copyDefaults(true);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        addEnglishItems();
        addGermanItems();
        saveConfig();
    }

    public void addEnglishItems() {
        String prefix = "en.";
        //Hotbar
        ItemStack startItemCompass = new ItemBuilder(Material.COMPASS)
                .setDisplayName("§7Teleporter")
                .get();
        ItemStack startItemHider = new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName("§6Hider")
                .get();
        ItemStack startItemNoGadget = new ItemBuilder(Material.BARRIER)
                .setDisplayName("§4No active Gadget")
                .get();
        ItemStack startItemGadget = new ItemBuilder(Material.CHEST)
                .setDisplayName("§eGadget")
                .get();
        ItemStack startItemprofile = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§bProfile")
                .get();
        //Inventory: Hider
        ItemStack hiderAll = new ItemBuilder(Material.LIME_CONCRETE)
                .setDisplayName("§aShow all players")
                .addLore("§7Shows you all players")
                .get();
        ItemStack hiderVip = new ItemBuilder(Material.PURPLE_CONCRETE)
                .setDisplayName("§5Show only VIPs")
                .addLore("§7Shows only VIPs")
                .get();
        ItemStack hiderNone = new ItemBuilder(Material.RED_CONCRETE)
                .setDisplayName("§cHide everyone")
                .addLore("§7Hides every player")
                .get();
        ItemStack hiderSetting = new ItemBuilder(Material.REPEATER)
                .setDisplayName("§4Setting")
                .addLore("§7Currently: ")
                .addLore("%HIDER_STATUS%")
                .get();
        //Hotbar-set
        config.addDefault(prefix + "StartItem.Compass",  startItemCompass);
        config.addDefault(prefix + "StartItem.Hider", startItemHider);
        config.addDefault(prefix + "StartItem.NoGadget",  startItemNoGadget);
        config.addDefault(prefix + "StartItem.Gadget",  startItemGadget);
        config.addDefault(prefix + "StartItem.Profile", startItemprofile);
        //Inventory: Hider-set
        config.addDefault(prefix + "Hider.All", hiderAll);
        config.addDefault(prefix + "Hider.VIP", hiderVip);
        config.addDefault(prefix + "Hider.None", hiderNone);
        config.addDefault(prefix + "Hider.Setting", hiderSetting);
    }

    public void addGermanItems() {
        String prefix = "de.";
        //Hotbar
        ItemStack startItemCompass = new ItemBuilder(Material.COMPASS)
                .setDisplayName("§7Teleporter")
                .get();
        ItemStack startItemHider = new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName("§6Verstecker")
                .get();
        ItemStack startItemNoGadget = new ItemBuilder(Material.BARRIER)
                .setDisplayName("§4Kein aktives Gadget")
                .get();
        ItemStack startItemGadget = new ItemBuilder(Material.CHEST)
                .setDisplayName("§eGadget")
                .get();
        ItemStack startItemprofile = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§bProfil")
                .get();
        //Inventory: Hider
        ItemStack hiderAll = new ItemBuilder(Material.LIME_CONCRETE)
                .setDisplayName("§aZeige alle Spieler")
                .addLore("§7Zeigt dir alle Spieler")
                .get();
        ItemStack hiderVip = new ItemBuilder(Material.PURPLE_CONCRETE)
                .setDisplayName("§5Zeige nur VIPs")
                .addLore("§7Zeigt dir nur noch VIPs")
                .get();
        ItemStack hiderNone = new ItemBuilder(Material.RED_CONCRETE)
                .setDisplayName("§cZeige niemanden")
                .addLore("§7Zeigt dir keine Spieler mehr")
                .get();
        ItemStack hiderSetting = new ItemBuilder(Material.REPEATER)
                .setDisplayName("§4Einstellung")
                .addLore("§7Aktuell: ")
                .addLore("%HIDER_STATUS%")
                .get();
        //Hotbar-set
        config.addDefault(prefix + "StartItem.Compass",  startItemCompass);
        config.addDefault(prefix + "StartItem.Hider", startItemHider);
        config.addDefault(prefix + "StartItem.NoGadget",  startItemNoGadget);
        config.addDefault(prefix + "StartItem.Gadget",  startItemGadget);
        config.addDefault(prefix + "StartItem.Profile", startItemprofile);
        //Inventory: Hider-set
        config.addDefault(prefix + "Hider.All", hiderAll);
        config.addDefault(prefix + "Hider.VIP", hiderVip);
        config.addDefault(prefix + "Hider.None", hiderNone);
        config.addDefault(prefix + "Hider.Setting", hiderSetting);
    }

    public ItemStack getItem(Locale locale, String path) {
        relaodConfig();
        if (config.get(locale.toLanguageTag() + "." + path) == null) return null;
        return config.getItemStack(locale.toLanguageTag() + "." + path);
    }

    public ItemStack getHead(Locale locale, String path, OfflinePlayer owner) {
        relaodConfig();
        if (config.get(locale.toLanguageTag() + "." + path) == null) return null;
        ItemStack is = config.getItemStack(locale.toLanguageTag() + "." + path);
        SkullMeta isMeta = (SkullMeta) is.getItemMeta();
        isMeta.setOwningPlayer(owner);
        is.setItemMeta(isMeta);
        return is;
    }

    public void loadConfig() {
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public void saveConfig() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void relaodConfig() {
        config = null;
        config = YamlConfiguration.loadConfiguration(file);
    }

}

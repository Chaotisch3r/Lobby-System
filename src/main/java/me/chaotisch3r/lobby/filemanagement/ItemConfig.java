package me.chaotisch3r.lobby.filemanagement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.stream.JsonReader;
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
        ItemStack compass = new ItemBuilder(Material.COMPASS)
                .setDisplayName("§7Teleporter")
                .get();
        ItemStack hider = new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName("§6Hider")
                .get();
        ItemStack noGadget = new ItemBuilder(Material.BARRIER)
                .setDisplayName("§4No active Gadget")
                .get();
        ItemStack gadget = new ItemBuilder(Material.CHEST)
                .setDisplayName("§eGadget")
                .get();
        ItemStack profile = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§bProfile")
                .get();
        config.addDefault("en.StartItem.Compass",  compass);
        config.addDefault("en.StartItem.Hider", hider);
        config.addDefault("en.StartItem.NoGadget",  noGadget);
        config.addDefault("en.StartItem.Gadget",  gadget);
        config.addDefault("en.StartItem.Profile", profile);
    }

    public void addGermanItems() {
        ItemStack compass = new ItemBuilder(Material.COMPASS)
                .setDisplayName("§7Teleporter")
                .get();
        ItemStack hider = new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName("§6Verstecker")
                .get();
        ItemStack noGadget = new ItemBuilder(Material.BARRIER)
                .setDisplayName("§4Kein aktives Gadget")
                .get();
        ItemStack gadget = new ItemBuilder(Material.CHEST)
                .setDisplayName("§eGadget")
                .get();
        ItemStack profile = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("§bProfil")
                .get();
        config.addDefault("de.StartItem.Compass",  compass);
        config.addDefault("de.StartItem.Hider", hider);
        config.addDefault("de.StartItem.NoGadget",  noGadget);
        config.addDefault("de.StartItem.Gadget",  gadget);
        config.addDefault("de.StartItem.Profile", profile);
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

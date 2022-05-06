package me.chaotisch3r.lobby.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.chaotisch3r.lobby.Lobby;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 15:03 21.04.2022
 **/

@Getter
public class ItemManager {

    private final File file;
    private YamlConfiguration config;
    private Gson gson;

    public ItemManager() {
        file = new File(Lobby.getInstance().getDataFolder(), "items.yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.options().copyDefaults(true);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //addItems();
        saveConfig();
    }

    public void addItems() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        ItemStack is1 = new ItemBuilder(Material.COMPASS)
                .setDisplayName("§7Compass")
                .get();
        gson.toJson(is1);
        config.addDefault("StartItems.Compass", gson);
    }

    public ItemStack getItem(String path) {
        gson = new GsonBuilder().setPrettyPrinting().create();
        if (config.get(path) == null) return null;
        return null;
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
        config = YamlConfiguration.loadConfiguration(file);
    }

}

package me.chaotisch3r.lobby.filemanagement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.chaotisch3r.lobby.Lobby;
import me.chaotisch3r.lobby.util.Langauge;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:29 20.04.2022
 **/

@Getter
@RequiredArgsConstructor
public class MessageConfig {

    private final File file;
    private YamlConfiguration config;

    private final Langauge langauge = Lobby.getInstance().getLangauge();

    public MessageConfig() {
        this.file = new File("plugins", "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);
        InputStream inputStream = Lobby.getInstance().getResource("messages.yml");
        if (inputStream != null) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
            this.config.setDefaults(cfg);
        }
        saveDefaultMessageConfig();
    }

    public void saveDefaultMessageConfig() {
        Lobby.getInstance().saveResource("messages.yml", true);
    }

    public void reloadMessageConfig() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

}

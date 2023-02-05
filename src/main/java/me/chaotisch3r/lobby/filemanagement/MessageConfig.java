package me.chaotisch3r.lobby.filemanagement;

import lombok.Getter;
import me.chaotisch3r.lobby.Lobby;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:29 20.04.2022
 **/

@Getter
public class MessageConfig {

    private final File file;
    private YamlConfiguration config;

    public MessageConfig() {
        this.file = new File("plugins", "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);
        InputStream inputStream = Lobby.getInstance().getResource("messages.yml");
        if(inputStream != null) {
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

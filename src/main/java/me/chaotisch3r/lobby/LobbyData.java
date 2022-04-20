package me.chaotisch3r.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.DyeColor;

import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:15 - 20.04.2022
 **/

@AllArgsConstructor
@Data
public class LobbyData {

    private UUID uuid;
    private String lastConnectedServer;
    private DyeColor color;
    private boolean respawnOnSpawn;
    private long playtime;

    private transient long login;

}

package me.chaotisch3r.lobby.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;
import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * <p>
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 11:14 - 20.04.2022
 **/

@AllArgsConstructor
@Data
public class PlayerData {

    private UUID uuid;
    private String name;
    private String ipAddress;
    private String rank;
    private Locale locale;

}

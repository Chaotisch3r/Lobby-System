package me.chaotisch3r.lobby.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 18:23 28.05.2022
 **/

@Data
@AllArgsConstructor
public class SettingsData {

    private UUID uuid;
    private String hiderStatus;
    private boolean getCoins;
    private boolean friendRequest;
    private boolean friendJoin;
    private boolean pm;
    private boolean partyRequest;
    private boolean partyJump;
    private boolean clanRequest;
    private boolean clanJoin;

}

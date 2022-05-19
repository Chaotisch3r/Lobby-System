package me.chaotisch3r.lobby.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 17:45 13.05.2022
 **/

@Data
@AllArgsConstructor
public class RankData {

    private String rankName;
    private int rankID;
    private String rankListName;
    private String rankDisplayName;
    private String[] rankPermissions;

    public boolean hasPermission(String permission) {
        return Arrays.stream(rankPermissions).toList().contains(permission);
    }

}

package me.chaotisch3r.lobby;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 15:12 25.04.2022
 **/

@AllArgsConstructor
@Data
public class WorldData {

    private UUID uid;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

}

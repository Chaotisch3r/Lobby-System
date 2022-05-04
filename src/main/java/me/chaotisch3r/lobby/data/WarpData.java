package me.chaotisch3r.lobby.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

/**
 * Copyright © Chaotisch3r, All Rights Reserved
 * If there are any problems with the class, please contact Chaotisch3r.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Created for Lobby-System, 14:12 02.05.2022
 **/

@AllArgsConstructor
@Data
public class WarpData {

    private String warpName;
    private UUID worldUID;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldUID), x, y, z, yaw, pitch);
    }

}

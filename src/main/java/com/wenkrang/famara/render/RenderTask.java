package com.wenkrang.famara.render;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.File;

public record RenderTask (
        int x,
        int y,
        Location eyes,
        double pitch,
        double yaw,
        double fieldOfView,
        String uuid,
        BufferedImage image,
        Player player,
        File picture
) {

}

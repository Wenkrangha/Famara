package com.wenkrang.famara.render;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 渲染上下文的内容
 */
public class RenderContext {
    public final Location eyes;
    public final double eyeX;
    public final double eyeY;
    public final double eyeZ;
    public final double pitch;
    public final double yaw;
    public final double fieldOfView;
    public final String id;
    public final BufferedImage image;
    public final Player player;
    public final File picture;
    public final MapView map;

    public RenderContext(String id, BufferedImage image, Player player, File picture, MapView map) {
        this.player = player;

        eyes = player.getEyeLocation();
        eyeX = eyes.getX();
        eyeY = eyes.getY();
        eyeZ = eyes.getZ();
        pitch = Math.toRadians(-eyes.getPitch());
        yaw = Math.toRadians(eyes.getYaw() + 90);
        fieldOfView = 1.0 / 128.0;

        this.id = id;
        this.image = image;
        this.picture = picture;
        this.map = map;
    }
}

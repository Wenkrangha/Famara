package com.wenkrang.famara.Render;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

public class RenderTask {
    int x;
    int y;
    Location eyes;
    double pitchRad;
    double yawRad;
    double fieldOfView;
    UUID uuid;
    BufferedImage image;
    Player player;
    File picture;
}

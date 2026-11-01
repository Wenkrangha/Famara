package com.wenkrang.famara.render;

import com.wenkrang.famara.render.rota.Camara;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 渲染上下文的内容
 */
public class RenderContext {
    /**
     * 竖直方向的完整视野角(弧度)。
     * 与替换前的球面计算保持一致的取景范围(128 像素 * 1/128 弧度)。
     */
    public static final double FOV = 1.0;

    /**
     * 焦距。方向向量归一化后焦距不影响结果, 仅用于定义视平面尺度。
     */
    public static final double FOCAL_LENGTH = 1.0;

    public final Location eyes;
    public final double eyeX;
    public final double eyeY;
    public final double eyeZ;
    /**
     * 针孔相机模型, 已预计算每个像素的视线方向
     */
    public final Camara camara;
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
        camara = new Camara(eyes, FOV, FOCAL_LENGTH, 128, 128);

        this.id = id;
        this.image = image;
        this.picture = picture;
        this.map = map;
    }
}

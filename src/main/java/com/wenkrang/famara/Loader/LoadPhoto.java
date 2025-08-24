package com.wenkrang.famara.Loader;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public class LoadPhoto {
    public static void loadPhoto(File dataFolder) {
        File pictureDir = new File(dataFolder, "pictures");
        for (File file : Objects.requireNonNull(pictureDir.listFiles())) {
            String name = file.getName();
            String substring = name.substring(0, name.lastIndexOf('.'));
            File picture = new File(dataFolder, "pictures/" + substring + ".png");

            try {
                BufferedImage image = ImageIO.read(picture);
                MapView map = Bukkit.getMap(Integer.parseInt(substring));
                if (map != null) {
                    map.setLocked(true);
                }
                MapRenderer mapRenderer = new MapRenderer() {
                    @Override
                    public void render(@NotNull MapView mapView, MapCanvas mapCanvas,@NotNull Player player) {
                        //渲染照片
                        mapCanvas.drawImage(0, 0, image);
                    }
                };
                map.addRenderer(mapRenderer);
            } catch (Exception e) {
                Logger.getGlobal().warning("§c§l[-] §r图片加载失败：" + substring);
            }
        }
    }
}

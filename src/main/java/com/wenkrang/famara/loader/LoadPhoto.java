package com.wenkrang.famara.loader;

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

/**
 * 照片加载器类
 * 用于加载和渲染地图上的图片
 */
public class LoadPhoto {
    /**
     * 加载照片到地图渲染器中
     * 该方法会遍历指定数据文件夹中的pictures目录，读取所有图片文件，
     * 并将它们渲染到对应ID的地图上
     *
     * @param dataFolder 插件的数据文件夹，用于定位pictures目录
     */
    public static void loadPhoto(File dataFolder) {
        // 创建pictures目录并遍历其中的所有文件
        File pictureDir = new File(dataFolder, "pictures");
        for (File file : Objects.requireNonNull(pictureDir.listFiles())) {
            String name = file.getName();
            String substring = name.substring(0, name.lastIndexOf('.'));
            File picture = new File(dataFolder, "pictures/" + substring + ".png");

            try {
                // 读取图片文件并获取对应的地图视图
                BufferedImage image = ImageIO.read(picture);
                MapView map = Bukkit.getMap(Integer.parseInt(substring));
                if (map != null) {
                    map.setLocked(true);
                }

                // 创建自定义地图渲染器来显示图片
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


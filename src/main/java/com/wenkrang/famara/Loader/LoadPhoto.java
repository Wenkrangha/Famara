package com.wenkrang.famara.Loader;

import com.wenkrang.famara.render.PhotoRender;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class LoadPhoto {
    public static void loadPhoto() {
        File pictureDir = new File("./plugins/Famara/pictures/");
        for (File file : Objects.requireNonNull(pictureDir.listFiles())) {
            String name = file.getName();
            String substring = name.substring(0, name.lastIndexOf('.'));
            File picture = new File("./plugins/Famara/pictures/" + substring + ".png");

            try {
                BufferedImage image = ImageIO.read(picture);
                MapView map = Bukkit.getMap(Integer.parseInt(substring));
                map.setLocked(true);
                MapRenderer mapRenderer = new MapRenderer() {
                    @Override
                    public void render(@NotNull MapView mapView, MapCanvas mapCanvas,@NotNull Player player) {
                        //渲染照片
                        mapCanvas.drawImage(0, 0, image);
                    }
                };
                map.addRenderer(mapRenderer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

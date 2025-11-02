package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class PhotoRender {

    public static ItemStack TakePhoto(Player player, File pictureFolder) throws IOException {

        MapView map = Bukkit.createMap(player.getWorld());

        //生成mapID
        String id = String.valueOf(map.getId());

        //初始化照片
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        File picture = new File(pictureFolder, id + ".png");
        try {
            boolean newFile = picture.createNewFile();
            if (!newFile) {
                throw new RuntimeException("照片文件创建失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        //初始化数据
        Location eyes = player.getEyeLocation();

        double pitch = Math.toRadians(-eyes.getPitch());
        double yaw = Math.toRadians(eyes.getYaw() + 90);
        final double fieldOfView = 1.0 / 128.0;

        Famara.progress.put(id, 0);

        //为照片每一个像素进行渲染
        RenderTask renderTask = new RenderTask(
                0,
                0,
                eyes,
                pitch,
                yaw,
                fieldOfView,
                id,
                image,
                player,
                picture
        );
        Famara.tasks.add(renderTask);

        return RenderLib.getPhoto(image, map);
    }
}
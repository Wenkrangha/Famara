package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import static com.wenkrang.famara.render.RenderLib.ShowProgress;

public class PhotoRender {

    
    // 添加边缘阴影检测方法
//    public static Color addEdgeShadow(Block block, BlockFace face, Color color) {
//        if (block == null || face != BlockFace.UP) return color;
//
//        Location location = block.getLocation();
//        Location xa = location.add(1, 1, 0);
//        Location xb = location.add(-1, 1, 0);
//        Location za = location.add(0, 1, 1);
//        Location zb = location.add(0, 1, -1);
//
//        if (!xa.getBlock().getType().isAir() | !xb.getBlock().getType().isAir() | !za.getBlock().getType().isAir() | !zb.getBlock().getType().isAir()) {
//            if (color.getRed() < 5 || color.getGreen() < 5 || color.getBlue() < 5) {
//                return Color.BLACK;
//            }
//            return new Color(color.getRed() - 5, color.getGreen() - 5, color.getBlue() - 5);
//        }
//
//        return color;
//    }
    


    public static void TakePhoto(Player player) {
        //生成UUID
        UUID uuid = UUID.randomUUID();

        //初始化照片
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        File picture = new File("./plugins/Famara/pictures/" + uuid + ".png");
        try {
            boolean newFile = picture.createNewFile();
            if (!newFile) {
                throw new RuntimeException("照片文件创建失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        player.getInventory().addItem(RenderLib.getPhoto(image, player.getWorld()));

        //初始化数据
        Location eyes = player.getEyeLocation();
        double pitchRad = Math.toRadians(-eyes.getPitch());
        double yawRad = Math.toRadians(eyes.getYaw() + 90);
        final double fieldOfView = 1.0 / 128.0;

        Famara.progress.put(uuid, 0);

        ShowProgress(player, uuid);


        //为照片每一个像素进行渲染
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                RenderTask renderTask = new RenderTask();
                renderTask.x = x;
                renderTask.y = y;
                renderTask.eyes = eyes;
                renderTask.pitchRad = pitchRad;
                renderTask.yawRad = yawRad;
                renderTask.fieldOfView = fieldOfView;
                renderTask.uuid = uuid;
                renderTask.image = image;
                renderTask.player = player;
                renderTask.picture = picture;
                Famara.tasks.add(renderTask);
            }
        }
    }
}

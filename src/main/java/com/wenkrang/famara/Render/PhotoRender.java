package com.wenkrang.famara.Render;

import com.wenkrang.famara.Famara;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static com.wenkrang.famara.Famara.yamlConfiguration;

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
    
    public static void ShowProgress(Player player,UUID uuid) {
        BossBar progress = Bukkit.createBossBar("曝光进度", BarColor.WHITE, BarStyle.SOLID);
        progress.addPlayer(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                Integer i = Famara.progress.get(uuid);

                try {
                    progress.setProgress((double) i / 16384);
                } catch (Exception e) {
                    progress.removeAll();
                    Famara.progress.remove(uuid);
                    cancel();
                }

                if (i == 16384) {
                    progress.removeAll();
                    Famara.progress.remove(uuid);
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(Famara.class), 0, 20);
    }

    public static void TakePhoto(Player player) {
        //生成UUID
        UUID uuid = UUID.randomUUID();

        //初始化照片
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        File picture = new File("./plugins/Famara/pictures/" + uuid.toString() + ".png");
        try {
            picture.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MapView map = Bukkit.createMap(player.getWorld());
        map.setLocked(true);
        MapRenderer mapRenderer = new MapRenderer() {
            @Override
            public void render(MapView mapView, MapCanvas mapCanvas, Player player) {
                //渲染照片
                mapCanvas.drawImage(0, 0, image);
            }
        };
        map.addRenderer(mapRenderer);

        //发放照片
        ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
        ItemMeta itemMeta = itemStack.getItemMeta();
        MapMeta mapMeta = (MapMeta) itemMeta;
        if (mapMeta != null) {
            mapMeta.setMapView(map);
        }
        itemStack.setItemMeta(mapMeta);
        player.getInventory().addItem(itemStack);

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
                int x1 = x;
                int y1 = y;
                RenderTask renderTask = new RenderTask();
                renderTask.x = x1;
                renderTask.y = y1;
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

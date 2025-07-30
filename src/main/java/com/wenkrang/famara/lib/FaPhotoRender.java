package com.wenkrang.famara.lib;

import com.wenkrang.famara.Famara;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
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
import java.util.function.Predicate;

import static com.wenkrang.famara.Famara.yamlConfiguration;

public class FaPhotoRender {
    public static boolean isBlock(Block block) {
        return block != null && block.getType().isBlock() && !block.getType().isAir();
    }
    public static Color BlockColorMatcher(Block block) throws IOException, InvalidConfigurationException {
        if (isBlock(block)) {
            Material material = block.getType();

            String name = material.name();

            if (!yamlConfiguration.contains(name + ".r")) {
                return Color.MAGENTA;
            }

            int r = yamlConfiguration.getInt(name + ".r");
            int g = yamlConfiguration.getInt(name + ".g");
            int b = yamlConfiguration.getInt(name + ".b");

            return new Color(r, g, b);
        }
        return new Color(143,173,241);
    }
    public static Color PhotoColorMatcher(RayTraceResult result, Location start) throws IOException, InvalidConfigurationException {
        if (result != null) {
            if (isBlock(result.getHitBlock()) && result.getHitEntity() != null) {
                if (result.getHitBlock().getLocation().distance(start) < result.getHitEntity().getLocation().distance(start)) {
                    return BlockColorMatcher(result.getHitBlock());
                }else {
                    return Color.YELLOW;
                }
            } else {
                if (isBlock(result.getHitBlock())) {
                    return BlockColorMatcher(result.getHitBlock());
                } else if (result.getHitEntity() != null) {
                    return Color.YELLOW;
                }
            }
        }

        return null;
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
        mapMeta.setMapView(map);
        itemStack.setItemMeta(mapMeta);
        player.getInventory().addItem(itemStack);

        //初始化数据
        Location eyes = player.getEyeLocation();
        double pitchRad = Math.toRadians(-eyes.getPitch());
        double yawRad = Math.toRadians(eyes.getYaw() + 90);
        final double fieldOfView = 1.0 / 128.0;

        //为照片每一个像素进行渲染
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                int x1 = x;
                int y1 = y;

                //由于渲染要消耗大量算力，如果没有进行限速，服务器TPS将会崩溃
                //这里使用随机数，将每一个像素的渲染随机延迟来达到限速的目的
                Random random = new Random();
                //这里将渲染任务限制在50秒之内（大约需要50秒来完成渲染，但由于一些原因，实际大约需要1分钟）
                int i = random.nextInt(20 * 50);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int finalX = x1;
                        int finalY = y1;


                        double cos = Math.cos(pitchRad - (finalY - 64) * fieldOfView);
                        Vector direction = new Vector(
                                Math.cos(yawRad + (finalX - 64) * fieldOfView) * cos,
                                Math.sin(pitchRad - (finalY - 64) * fieldOfView),
                                Math.sin(yawRad + (finalX - 64) * fieldOfView) * cos
                        );

                        Predicate<Entity> excludePlayers = entity -> !(entity instanceof Player);
                        //光线追踪
                        RayTraceResult result = player.getWorld().rayTrace(eyes, direction, 180, FluidCollisionMode.ALWAYS, false, 1, excludePlayers);

                        try {
                            if (result != null) {
                                image.setRGB(finalX, finalY, PhotoColorMatcher(result, eyes).getRGB());
                            } else {
                                image.setRGB(finalX, finalY, (new Color(143,173,241)).getRGB());
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidConfigurationException e) {
                            throw new RuntimeException(e);
                        }

                        //写入照片
                        try {
                            ImageIO.write(image, "png", picture);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.runTaskLater(Famara.getPlugin(Famara.class), i);
            }
        }
    }
}

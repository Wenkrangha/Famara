package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.itemSystem.ItemSystem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

import static com.wenkrang.famara.Famara.renderSpeeds;
import static com.wenkrang.famara.Famara.yamlConfiguration;

public class RenderLib {
    public static Color getSkyColor(World world) {
        long time = world.getTime();
        boolean isRaining = world.hasStorm();


        if (world.getEnvironment() == World.Environment.NETHER) {
            return new Color(132, 22, 22);
        }
        if (world.getEnvironment() == World.Environment.THE_END){
            return Color.BLACK;
        }
        // 根据时间计算基础颜色（简化逻辑）
        int red = 0, green = 0, blue = 0;

        // 白天（6000-12000 ticks）
        if (time >= 0 && time < 6000 || time >= 23300 && time < 24000) {
            red = 100; green = 150; blue = 255; // 黄昏/黎明
        } else if (time >= 6000 && time < 12000) {
            red = 135; green = 206; blue = 250; // 正午
        }
        // 晚上（12000-23000 ticks）
        else if (time >= 12000 && time < 23000) {
            red = 20; green = 20; blue = 40; // 夜晚
        }

        // 根据天气调整颜色
        if (isRaining) {
            red -= 30; green -= 30; blue -= 50; // 下雨时调暗
        }

        // 限制颜色范围
        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return new Color(red, green, blue);
    }


    public static ItemStack getPhoto(BufferedImage image, MapView map) {
        map.setLocked(true);
        MapRenderer mapRenderer = new MapRenderer() {
            @Override
            public void render(@NotNull MapView mapView, MapCanvas mapCanvas,@NotNull Player player) {
                //渲染照片
                mapCanvas.drawImage(0, 0, image);
            }
        };
        map.addRenderer(mapRenderer);

        //发放照片
        ItemStack itemStack = ItemSystem.itemMap.get("photo");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setItemModel(new NamespacedKey("famara", "photo"));
        MapMeta mapMeta = (MapMeta) itemMeta;

        mapMeta.setMapView(map);

        itemStack.setItemMeta(mapMeta);
        return itemStack;
    }

    public static void render(int x, int y, Location eyes, double pitchRad, double yawRad, double fieldOfView,String id, BufferedImage image, Player player, File picture){
        //TODO:1.添加液体渲染 2.添加UP阴影
                double cos = Math.cos(pitchRad - (y - 64) * fieldOfView);
        Vector direction = new Vector(
                Math.cos(yawRad + (x - 64) * fieldOfView) * cos,
                Math.sin(pitchRad - (y - 64) * fieldOfView),
                Math.sin(yawRad + (x - 64) * fieldOfView) * cos
        );


        //光线追踪
        RayTraceResult result = player.getWorld().rayTraceBlocks(eyes, direction, 300, FluidCollisionMode.ALWAYS, false);

        if (result != null) {
            Color color = PhotoColorMatcher(result, eyes, direction, player);
            color = BlockFaceColorMatcher(result.getHitBlockFace(), color);
            color = LightColorMatcher(color, getBlockLightLevel(result));
            // 添加边缘阴影效果
//                                if (result.getHitBlock() != null && result.getHitBlockFace() != null) {
//                                    color = addEdgeShadow(result.getHitBlock(), result.getHitBlockFace(), color);
//                                }
            color = MaskColor(x, y, color);
            image.setRGB(x, y, color.getRGB());
        } else {
            image.setRGB(x, y, getSkyColor(player.getWorld()).getRGB());

        }

        //写入照片
        try {
            ImageIO.write(image, "png", picture);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Famara.progress.put(id, Famara.progress.get(id) + 1);

        renderSpeeds.put(id, Famara.renderSpeeds.containsKey(id) ? Famara.renderSpeeds.get(id) + 1 : 1);
    }

    public static boolean isBlock(Block block) {
        return block != null && block.getType().isBlock() && !block.getType().isAir();
    }
    public static Color BlockColorMatcher(Block block, Player player, Location eyes, Vector direction) {
        if (isBlock(block)) {
            Material material = block.getType();

//            if (material == Material.WATER) {
//                Predicate<Entity> excludePlayers = entity -> !(entity instanceof Player);
//                //光线追踪
//                RayTraceResult result = player.getWorld().rayTrace(eyes, direction, 180, FluidCollisionMode.NEVER, false, 1, excludePlayers);
//
//                if (result != null) {
//                    Color color = PhotoColorMatcher(result, eyes, direction, player);
//                    color = BlockFaceColorMatcher(result.getHitBlockFace(), color);
//                    color = LightColorMatcher(color, getBlockLightLevel(result));
//
//                    Color Water = new Color(65, 109, 204);
//
//                    //混合Water和color这两个颜色
//                    return new Color(Water.getRed() * 0.5f + color.getRed() * 0.5f, Water.getGreen() * 0.5f + color.getGreen() * 0.5f, Water.getBlue() * 0.5f + color.getBlue() * 0.5f);
//                }
//            }

            String name = material.name();
            if (Famara.colorCache.containsKey(name)) {
                return Famara.colorCache.get(name);
            }

            if (!yamlConfiguration.contains(name + ".r")) {
                player.sendMessage("§c§l[-] §r无法找到颜色：" + name);
                return Color.MAGENTA;
            }

            int r = yamlConfiguration.getInt(name + ".r");
            int g = yamlConfiguration.getInt(name + ".g");
            int b = yamlConfiguration.getInt(name + ".b");

            Famara.colorCache.put(name, new Color(r, g, b));
            return new Color(r, g, b);
        }
//        return new Color(143,173,241);
        return getSkyColor(player.getWorld());
    }
    public static Color PhotoColorMatcher(RayTraceResult result, Location start, Vector direction, Player player) {
        if (result != null) {
            if (isBlock(result.getHitBlock()) && result.getHitEntity() != null) {
                if (result.getHitBlock().getLocation().distance(start) < result.getHitEntity().getLocation().distance(start)) {
                    return BlockColorMatcher(result.getHitBlock(), player, start, direction);
                }else {
                    return Color.YELLOW;
                }
            } else {
                if (isBlock(result.getHitBlock())) {
                    return BlockColorMatcher(result.getHitBlock(), player, start, direction);
                } else if (result.getHitEntity() != null) {
                    return Color.YELLOW;
                }
            }
        }

        return null;
    }

    public static Color BlockFaceColorMatcher(BlockFace blockFace, Color color) {
        try {
            return switch (blockFace) {
                case NORTH -> new Color(color.getRed() - 5, color.getGreen() - 5, color.getBlue() - 5);
                case SOUTH -> new Color(color.getRed() + 5, color.getGreen() + 5, color.getBlue() + 5);
                case UP -> new Color(color.getRed() + 20, color.getGreen() + 20, color.getBlue() + 20);
                case DOWN -> new Color(color.getRed() - 15, color.getGreen() - 15, color.getBlue() - 15);
                case EAST -> new Color(color.getRed() + 10, color.getGreen() + 10, color.getBlue() + 10);
                case WEST -> new Color(color.getRed() - 10, color.getGreen() - 10, color.getBlue() - 10);
                default -> color;
            };
        } catch (Exception e) {
            if (color.getRed() >= 100 || color.getGreen() >= 100 || color.getBlue() >= 100) {
                return Color.WHITE;
            }
            if (color.getRed() < 100 || color.getGreen() < 100 || color.getBlue() < 100) {
                return Color.black;
            }
            Logger.getGlobal().warning("颜色遮罩设置失败");
            return Color.black;
        }
    }

    public static int getBlockLightLevel(RayTraceResult result) {
        if (isBlock(result.getHitBlock())) {
            // 获取方块的亮度等级 (0-15)
            if (result.getHitBlockFace() != null) {
                return result.getHitBlock().getRelative(result.getHitBlockFace()).getLightLevel();
            }
        }
        return 0; // 默认返回0亮度
    }

    public static Color LightColorMatcher(Color color, int lightLevel) {
        // 改进光照计算，增加对比度
        float lightFactor = (float) lightLevel / 15.0f;
        // 使用更明显的光照变化
        int red = Math.min(255, Math.max(0, (int)(color.getRed() * (0.3f + 0.7f * lightFactor))));
        int green = Math.min(255, Math.max(0, (int)(color.getGreen() * (0.3f + 0.7f * lightFactor))));
        int blue = Math.min(255, Math.max(0, (int)(color.getBlue() * (0.3f + 0.7f * lightFactor))));
        return new Color(red, green, blue);
    }

    public static Color MaskColor(int x,int y,Color color) {
        boolean xEven = (x % 2 == 0);
        boolean yEven = (y % 2 == 0);

        int delta = (xEven == yEven) ? -2 : 2;

        int newRed = color.getRed() + delta;
        int newGreen = color.getGreen() + delta;
        int newBlue = color.getBlue() + delta;

        // 边界检查
        if (newRed < 0 || newRed > 255 ||
                newGreen < 0 || newGreen > 255 ||
                newBlue < 0 || newBlue > 255) {
            return (color.getRed() >= 100 || color.getGreen() >= 100 || color.getBlue() >= 100)
                    ? Color.WHITE : Color.BLACK;
        }

        return new Color(newRed, newGreen, newBlue);
    }
}

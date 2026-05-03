package com.wenkrang.famara.lib;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.render.RenderTemp;
import com.wenkrang.famara.render.lib.FaColor;
import com.wenkrang.famara.render.stage.RayTraceStage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 颜色管理类
 */
public class ColorManager {
    /**
     * 颜色配置文件的YAML配置对象。
     */
    public static YamlConfiguration yamlConfiguration = new YamlConfiguration();
    /**
     * 排除的方块列表，表示没有上色的方块。
     */
    public static CopyOnWriteArrayList<String> excludingBlocks = new CopyOnWriteArrayList<>();
    /**
     * 颜色缓存，用于快速查找颜色值。
     */
    public static ConcurrentHashMap<String, Color> colorCache = new ConcurrentHashMap<>();

    /**
     * 颜色缓存，用于快速查找颜色值。
     */
    public static ConcurrentHashMap<String, FaColor> faColorCache = new ConcurrentHashMap<>();


    /**
     * 加载颜色配置文件。
     */
    public static void loadColor() {
        File file = new File(Famara.dataFolder, "colors.yml");
        try {
            ColorManager.yamlConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置颜色。
     *
     * @param name  颜色名称
     * @param red   红色值
     * @param green 绿色值
     * @param blue  蓝色值
     */
    public static void setColor(String name, int red, int green, int blue) {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new NullPointerException(Translation.CURRENT.of("setError1"));
        }

        ColorManager.yamlConfiguration.set(name + ".r", red);
        ColorManager.yamlConfiguration.set(name + ".g", green);
        ColorManager.yamlConfiguration.set(name + ".b", blue);

        try {
            ColorManager.yamlConfiguration.save(new File(Famara.dataFolder, "colors.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置颜色。
     *
     * @param name  颜色名称
     * @param s     红色值
     * @param s1    绿色值
     * @param s2    蓝色值
     */
    public static void setColor(String name, String s, String s1, String s2) {
        try {
            int red = Integer.parseInt(s);
            int green = Integer.parseInt(s1);
            int blue = Integer.parseInt(s2);
            setColor(name, red, green, blue);

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid color value format");
        }
    }



    /**
     * 枚举方块颜色
     * @param block 要查询的方块
     * @param rt 渲染临时变量
     */
    public static void getBlockColor(Block block, RenderTemp rt) {
        // 先检测是不是方块(不是空气)
        if (RayTraceStage.isBlock(block)) {
            // 获取材质
            Material material = block.getType();
    
            // 获取方块名字
            String name = material.name();
                
            // 查询FaColor缓存
            FaColor cached = faColorCache.get(name);
            if (cached != null) {
                rt.color.set(cached);
                return;
            }
    
            // 查询配置是否存在
            if (!yamlConfiguration.contains(name + ".r")) {
                //TODO:返回找不到的
                rt.color.set(Color.MAGENTA);
                if (!rt.failedBlocks.contains(name)) rt.failedBlocks.add(name);
                return;
            }
    
            // 获取rgb
            int r = yamlConfiguration.getInt(name + ".r");
            int g = yamlConfiguration.getInt(name + ".g");
            int b = yamlConfiguration.getInt(name + ".b");
    
            // 存缓存(只创建一次)
            FaColor newColor = new FaColor(r, g, b);
            faColorCache.put(name, newColor);
                
            // 赋值
            rt.color.set(newColor);
        }else {
            // 赋空
            rt.color = null;
        }
    }
}

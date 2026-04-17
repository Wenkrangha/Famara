package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;
import org.bukkit.World;

import java.awt.*;

public class SkyColorStage implements RenderStage{
    /**
     * 获取天空颜色
     * @param world 世界
     * @param rt 渲染临时变量
     */
    public static void getSkyColor(World world,RenderTemp rt) {
        long time = world.getTime();
        boolean isRaining = world.hasStorm();


        if (world.getEnvironment() == World.Environment.NETHER) {
            rt.color.set(132, 22, 22);
        }
        if (world.getEnvironment() == World.Environment.THE_END){
            rt.color.set(Color.BLACK);
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

        rt.color.set(red, green, blue);
    }
    @Override
    public void render(RenderContext renderContext, RenderTemp rt) {
        if (rt.hit == null) {
            getSkyColor(renderContext.player.getWorld(), rt);

            rt.isSkipped = true;
        }
    }
}

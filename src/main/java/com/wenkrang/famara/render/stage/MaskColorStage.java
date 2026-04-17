package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;

import java.awt.*;

/**
 * 颜色遮罩
 */
public class MaskColorStage implements RenderStage{
    @Override
    public void render(RenderContext renderContext, RenderTemp rt) {
        boolean xEven = (rt.x % 2 == 0);
        boolean yEven = (rt.y % 2 == 0);

        int delta = (xEven == yEven) ? -2 : 2;

        // 使用零GC优化的批量调整
        int newRed = rt.color.getR() + delta;
        int newGreen = rt.color.getG() + delta;
        int newBlue = rt.color.getB() + delta;

        // 边界检查
        if (newRed < 0 || newRed > 255 ||
                newGreen < 0 || newGreen > 255 ||
                newBlue < 0 || newBlue > 255) {
            // 边界溢出时使用黑白，直接设置字段避免 Color 对象
            boolean isBright = (rt.color.getR() >= 100 || rt.color.getG() >= 100 || rt.color.getB() >= 100);
            rt.color.setR(isBright ? 255 : 0);
            rt.color.setG(isBright ? 255 : 0);
            rt.color.setB(isBright ? 255 : 0);
            return;
        }

        rt.color.set(newRed, newGreen, newBlue);
    }
}

package com.wenkrang.famara.render;

import com.wenkrang.famara.render.lib.FaColor;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * 渲染临时数据类
 */
public class RenderTemp {
    public int x;
    public int y;
    public Vector direction;
    public double cos;
    public RayTraceResult hit;
    public FaColor color;
    public ArrayList<String> failedBlocks;
    public boolean isSkipped = false;

    public RenderTemp() {
        direction = new Vector();
        failedBlocks = new ArrayList<>();
        color = new FaColor(0, 0, 0);
    }
}

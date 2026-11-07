package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.lib.ColorManager;
import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.awt.*;


/**
 * 射线追踪部分
 */
public class RayTraceStage implements RenderStage{
    /**
     * 判断是否为方块
     * @param block 要检测的方块
     * @return 是否为方块
     */
    public static boolean isBlock(Block block) {
        return block != null && block.getType().isBlock() && !block.getType().isAir();
    }

    /**
     * 获取方块颜色
     * @param rt 渲染临时变量
     * @param ctx 渲染上下文
     */
    public static void getBlockColor(RenderTemp rt,RenderContext ctx) {
        // 检测结果是否位null
        if (rt.hit != null) {
            // 判断遮挡关系
            if (isBlock(rt.hit.getHitBlock()) && rt.hit.getHitEntity() != null) {
                // 检测距离 - 使用预计算坐标避免 getLocation() 创建对象
                double blockX = rt.hit.getHitBlock().getX();
                double blockY = rt.hit.getHitBlock().getY();
                double blockZ = rt.hit.getHitBlock().getZ();
                Location location = rt.hit.getHitBlock().getLocation();
                double entityX = location.getX();
                double entityY = location.getY();
                double entityZ = location.getZ();
                
                // 使用距离平方比较，避免 sqrt 开销
                double blockDistSq = (blockX - ctx.eyeX) * (blockX - ctx.eyeX) +
                                    (blockY - ctx.eyeY) * (blockY - ctx.eyeY) +
                                    (blockZ - ctx.eyeZ) * (blockZ - ctx.eyeZ);
                double entityDistSq = (entityX - ctx.eyeX) * (entityX - ctx.eyeX) +
                                     (entityY - ctx.eyeY) * (entityY - ctx.eyeY) +
                                     (entityZ - ctx.eyeZ) * (entityZ - ctx.eyeZ);
                
                if (blockDistSq < entityDistSq) {
                    ColorManager.getBlockColor(rt.hit.getHitBlock(), rt);
                }else {
                    rt.color.setR(255);
                    rt.color.setG(255);
                    rt.color.setB(0);
                }
            } else {
                if (isBlock(rt.hit.getHitBlock())) {
                    ColorManager.getBlockColor(rt.hit.getHitBlock(), rt);
                } else if (rt.hit.getHitEntity() != null) {
                    rt.color.setR(255);
                    rt.color.setG(255);
                    rt.color.setB(0);
                }
            }
        }

    }

    @Override
    public void render(RenderContext ctx, RenderTemp rt) {
        // 从预计算的视角矩阵取出当前像素的视线方向, 避免逐像素三角函数与球面畸变
        Vector direction = ctx.camara.getDirection(rt.x, rt.y);
        rt.direction.setX(direction.getX());
        rt.direction.setY(direction.getY());
        rt.direction.setZ(direction.getZ());

        // 光线追踪
        rt.hit = ctx.player.getWorld().rayTraceBlocks(ctx.eyes, rt.direction, 256, FluidCollisionMode.ALWAYS, false);



        if (rt.hit != null) {
            // 引用修改
            getBlockColor(rt, ctx);
        }
    }
}

package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.lib.ColorManager;
import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;

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
                double entityX = rt.hit.getHitEntity().getLocation().getX();
                double entityY = rt.hit.getHitEntity().getLocation().getY();
                double entityZ = rt.hit.getHitEntity().getLocation().getZ();
                
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
        // 初始化渲染向量
        rt.cos = Math.cos(ctx.pitch - (rt.y - 64) * ctx.fieldOfView);
        rt.direction.setX(Math.cos(ctx.yaw + (rt.x - 64) * ctx.fieldOfView) * rt.cos);
        rt.direction.setY(Math.sin(ctx.pitch - (rt.y - 64) * ctx.fieldOfView));
        rt.direction.setZ(Math.sin(ctx.yaw + (rt.x - 64) * ctx.fieldOfView) * rt.cos);

        // 光线追踪
        rt.hit = ctx.player.getWorld().rayTraceBlocks(ctx.eyes, rt.direction, 256, FluidCollisionMode.ALWAYS, false);



        if (rt.hit != null) {
            // 引用修改
            getBlockColor(rt, ctx);
        }
    }
}

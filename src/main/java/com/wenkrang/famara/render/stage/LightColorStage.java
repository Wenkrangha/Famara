package com.wenkrang.famara.render.stage;

import com.wenkrang.famara.render.RenderContext;
import com.wenkrang.famara.render.RenderTemp;
import org.bukkit.util.RayTraceResult;

public class LightColorStage implements RenderStage{
    /**
     * 获取方块的亮度等级
     * @param result 射线结果
     * @return 亮度等级
     */
    public static int getBlockLightLevel(RayTraceResult result) {
        if (RayTraceStage.isBlock(result.getHitBlock())) {
            // 获取方块的亮度等级 (0-15)
            if (result.getHitBlockFace() != null) {
                return result.getHitBlock().getRelative(result.getHitBlockFace()).getLightLevel();
            }
        }
        return 0; // 默认返回0亮度
    }
    @Override
    public void render(RenderContext renderContext, RenderTemp rt) {
        // 改进光照计算，增加对比度
        float lightFactor = (float) getBlockLightLevel(rt.hit) / 15.0f;
        // 使用零GC优化的批量光照应用
        rt.color.applyLightFactor(0.3f, lightFactor);
    }
}

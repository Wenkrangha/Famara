package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.Translation;
import com.wenkrang.famara.render.stage.*;
import org.bukkit.scheduler.BukkitRunnable;

import javax.imageio.ImageIO;
import java.io.IOException;

import static com.wenkrang.famara.Famara.renderService;

/**
 * 渲染任务
 */
public class RenderTask {
    public final RenderPipe pipe;
    public final RenderContext ctx;
    private final RenderTemp rt;
    public volatile boolean handled = false;

    public boolean isHandled() {
        return handled;
    }

    /**
     * 完成渲染任务
     */
    public void finish() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //写入照片
                try {
                    ImageIO.write(ctx.image, "png", ctx.picture);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskAsynchronously(Famara.getPlugin());

        // 输出错误信息
        if (!rt.failedBlocks.isEmpty()) {
            Famara.plugin.getLogger().warning(Translation.CURRENT.of("renderError1") + " " + rt.failedBlocks.toString());
        }
    }

    public RenderTask(RenderContext ctx) {
        this.ctx = ctx;

        // 初始化渲染临时变量
        rt = new RenderTemp();

        rt.x = 0;
        rt.y = 0;

        pipe = new RenderPipe(rt);

        // 添加渲染阶段
        pipe.addStage(new RayTraceStage())
                .addStage(new SkyColorStage())
                .addStage(new BlockFaceLightStage())
                .addStage(new LightColorStage())
                .addStage(new MaskColorStage());
    }

    public void updateTraceData() {
        // progress
        renderService.progress.put(ctx.id, renderService.progress.get(ctx.id) + 1);

        // render speed
        renderService.renderSpeeds.put(
                ctx.id, renderService.renderSpeeds.containsKey(ctx.id)
                        ?
                        renderService.renderSpeeds.get(ctx.id) + 1
                        :
                        1
        );
    }

    public void step() {
        rt.isSkipped = false;

        pipe.render(ctx);

        rt.x++;
        // 当x坐标超出范围时，重置x坐标并增加y坐标
        if (rt.x >= 128) {
            rt.x = 0;
            rt.y++;
        }

        updateTraceData();

        handled = true;
    }

    /**
     * 检查渲染任务是否完成
     *
     * @return 当y坐标达到或超过128时返回true，否则返回false
     */
    public boolean isFinished(){
        return rt.y >= 128;
    }


}

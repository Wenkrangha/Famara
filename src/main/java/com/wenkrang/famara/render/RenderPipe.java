package com.wenkrang.famara.render;

import com.wenkrang.famara.render.stage.RenderStage;

import java.util.ArrayList;

import static com.wenkrang.famara.Famara.renderService;

public class RenderPipe {
    private final ArrayList<RenderStage> stages;

    private final RenderTemp rt;

    public RenderPipe(RenderTemp rt) {
        this.rt = rt;
        stages = new ArrayList<>();
    }

    public RenderPipe addStage(RenderStage stage) {
        stages.add(stage);
        return this;
    }

    public void render(RenderContext ctx) {
        // 流水线
        for (RenderStage stage : stages) {
            // 流水线操作
            stage.render(ctx, rt);

            // 如果流水线要求跳过
            if (rt.isSkipped) {
                break;
            }
        }

        // 写进Buffer
        ctx.image.setRGB(rt.x, rt.y, rt.color.toARGB());

    }
}

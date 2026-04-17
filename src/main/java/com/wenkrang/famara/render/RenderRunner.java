package com.wenkrang.famara.render;


import com.wenkrang.famara.Famara;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import static com.wenkrang.famara.Famara.renderService;

public class RenderRunner {
    public RenderService renderService;

    public RenderRunner(RenderService renderService) {
        this.renderService = renderService;
    }

    public void Runner() {
        //TODO:取消竞态渲染
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // 检查任务队列是否为空
                    if (!renderService.tasks.isEmpty()) {
                        Random random = new Random();
                        // 根据配置的速度参数，执行相应数量的任务步骤
                        for (int i = 0; i < renderService.speed; i++) {
                            // 随机选择一个渲染任务并执行一步
                            if (!renderService.tasks.isEmpty()) {
                                RenderTask renderTask = renderService.tasks.get(random.nextInt(renderService.tasks.size()));
                                renderTask.step();
                                // 如果任务已完成，则从任务队列中移除
                                if (renderTask.isFinished()) {
                                    renderTask.finish();
                                    renderService.tasks.remove(renderTask);
                                }
                            }
                        }
                    }
                }catch (Exception e) {
//                    Logger.getGlobal().warning(Arrays.toString(e.getStackTrace()));
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(Famara.getPlugin(), 0, 1);
    }

}

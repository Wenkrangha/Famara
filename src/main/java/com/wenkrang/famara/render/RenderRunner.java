package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Logger;

/**
 * 渲染任务执行器类
 * 负责定时执行渲染任务队列中的任务
 */
public class RenderRunner {

    /**
     * 启动渲染任务执行器
     * 创建一个Bukkit定时任务，周期性地从任务队列中随机选择并执行渲染任务
     * 该方法会立即启动并以1tick的间隔持续执行
     */
    public static void Runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // 检查任务队列是否为空
                    if (!Famara.tasks.isEmpty()) {
                        Random random = new Random();
                        // 根据配置的速度参数，执行相应数量的任务步骤
                        for (int i = 0;i < Famara.speed;i++) {
                            // 随机选择一个渲染任务并执行一步
                            RenderTask renderTask = Famara.tasks.get(random.nextInt(Famara.tasks.size()));

                            renderTask.step();
                            // 如果任务已完成，则从任务队列中移除
                            if (renderTask.isFinished()) Famara.tasks.remove(renderTask);
                        }
                    }
                }catch (Exception e) {
                    Logger.getGlobal().warning(e.getMessage());
                }
            }
        }.runTaskTimer(Famara.getPlugin(), 0, 1);
    }
}


package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Logger;

import static com.wenkrang.famara.render.RenderLib.render;

public class RenderRunner {

    public static void Runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!Famara.tasks.isEmpty()) {
                        Random random = new Random();
                        for (int i = 0;i < Famara.speed;i++) {
                            RenderTaskNew renderTask = Famara.tasks.get(random.nextInt(Famara.tasks.size()));

                            renderTask.step();
                            if (renderTask.isFinished()) Famara.tasks.remove(renderTask);
                        }
                    }
                }catch (Exception e) {
                    Logger.getGlobal().warning(e.getMessage());
                }
            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 1);
    }
}

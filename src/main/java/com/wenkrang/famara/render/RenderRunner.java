package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.Loader.LoadItem;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Logger;

import static com.wenkrang.famara.render.RenderLib.*;
import static org.bukkit.Bukkit.getServer;

public class RenderRunner {

    public static void Runner() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!Famara.tasks.isEmpty()) {
                        Random random = new Random();
                        for (int i = 0;i < Famara.speed;i++) {
                            RenderTask renderTask = Famara.tasks.get(random.nextInt(Famara.tasks.size()));

                            render(renderTask.x, renderTask.y
                                    , renderTask.eyes,
                                    renderTask.pitchRad, renderTask.yawRad,
                                    renderTask.fieldOfView,
                                    renderTask.id,
                                    renderTask.image,
                                    renderTask.player,
                                    renderTask.picture);

                            Famara.tasks.remove(renderTask);
                        }
                    }
                }catch (Exception e) {
                    Logger.getGlobal().warning(e.getMessage());
                }
            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 1);
    }
}

package com.wenkrang.famara.Render;

import com.wenkrang.famara.Famara;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static com.wenkrang.famara.Render.Renderlib.*;

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
                                    renderTask.uuid,
                                    renderTask.image,
                                    renderTask.player,
                                    renderTask.picture);

                            Famara.tasks.remove(renderTask);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 1);
    }
}

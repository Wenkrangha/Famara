package com.wenkrang.famara;

import com.wenkrang.famara.event.OpenBookE;
import com.wenkrang.famara.itemSystem.BookPage;
import com.wenkrang.famara.Loader.LoadItem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.render.RenderRunner;
import com.wenkrang.famara.render.RenderTask;
import com.wenkrang.famara.command.fa;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static com.wenkrang.famara.Loader.LoadPhoto.loadPhoto;

public final class Famara extends JavaPlugin {


    public static Map<String, Integer> progress = new HashMap<>();

    public static YamlConfiguration yamlConfiguration = new YamlConfiguration();

    public static ArrayList<RenderTask> tasks = new ArrayList<>();

    public static int speed = 7;

    public static Map<String, Color> colorCache = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        //注册命令
        Objects.requireNonNull(this.getCommand("fa")).setExecutor(new fa());

        getServer().getPluginManager().registerEvents(new OpenBookE(), this);

        try {
            yamlConfiguration.load("./plugins/Famara/colors.yml");
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        //初始化照片文件夹
        File pictureDir = new File("./plugins/Famara/pictures");
        if (!pictureDir.exists()) {
            boolean mkdir = pictureDir.mkdirs();
            if (!mkdir) {
                Logger.getGlobal().warning("文件夹创建失败");
            }
        }


        RenderRunner.Runner();

        new BukkitRunnable() {
            @Override
            public void run() {
                colorCache.clear();
            }
        }.runTaskTimer(this, 0, 200);

        LoadItem.loadItem();

        RecipeBook.mainPage = new BookPage("相机配方", null, null, null);

        loadPhoto();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}

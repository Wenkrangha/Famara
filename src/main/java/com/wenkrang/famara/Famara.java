package com.wenkrang.famara;

import com.wenkrang.famara.Loader.LoadRecipe;
import com.wenkrang.famara.event.*;
import com.wenkrang.famara.itemSystem.BookPage;
import com.wenkrang.famara.Loader.LoadItem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.render.RenderRunner;
import com.wenkrang.famara.render.RenderTask;
import com.wenkrang.famara.command.fa;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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

    public static Map<Integer, ItemStack> results = new HashMap<>();

    public static Map<String,Integer> renderSpeeds = new HashMap<>();

    public static Map<String,Integer> renderRealSpeeds = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        //注册命令
        Objects.requireNonNull(this.getCommand("fa")).setExecutor(new fa());

        getServer().getPluginManager().registerEvents(new OpenBookE(), this);
        getServer().getPluginManager().registerEvents(new BookClickE(), this);
        getServer().getPluginManager().registerEvents(new OnUseCameraE(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoinE(), this);
        getServer().getPluginManager().registerEvents(new OnLoadFilm(), this);

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


        /*TODO:在进行多次拍摄后，我发现拉出相片时（好像要多拍几次才会出发这个bug），配方书中的相机胶卷数也会变成与手中相机的一样，
         * 比如说原本配方书中的相机应该是没有装胶卷的，我用一台由14张胶卷的相机拍照，拉出胶卷后，打开配方书，配方书中的相机
         * 居然也变成14张胶卷的了，我检查了与RecipeBook.MainPage的相关调用，除了LoadItem的合法调用，没有发现其他的异常
         * 使用，十分神奇，我修了半天，也不知道问题在哪里，但用下面的东西可以很好的修复问题，但我也很担心gc和内存会不会爆炸，毕竟
         * 是往Map写重复的东西，但愿有人看到这篇文字，并且来修复它
         * 2025.8.19    --Wenkrang
         */
        new BukkitRunnable() {
            @Override
            public void run() {
                LoadItem.loadItem();
            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 8);

        new BukkitRunnable() {
            @Override
            public void run() {
                Famara.renderRealSpeeds.clear();
                Famara.renderRealSpeeds.putAll(Famara.renderSpeeds);
                Famara.renderSpeeds.clear();

            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(Famara.class), 0, 20);


        RecipeBook.mainPage = new BookPage("相机配方", new HashMap<>(), new HashMap<>(), new HashMap<>());

        LoadItem.loadItem();

        loadPhoto();

        LoadRecipe.loadRecipe();

        getServer().getOnlinePlayers().forEach(OnPlayerJoinE::startCheck);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}

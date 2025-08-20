package com.wenkrang.famara;

import com.google.common.cache.RemovalListener;
import com.wenkrang.famara.Loader.LoadRecipe;
import com.wenkrang.famara.command.faTabComplete;
import com.wenkrang.famara.event.*;
import com.wenkrang.famara.itemSystem.BookPage;
import com.wenkrang.famara.Loader.LoadItem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.render.RenderRunner;
import com.wenkrang.famara.render.RenderTask;
import com.wenkrang.famara.command.fa;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.logging.Logger;

import static com.wenkrang.famara.Loader.LoadPhoto.loadPhoto;

/**
 * 主插件类，负责插件的初始化和关闭逻辑。
 * 包括命令注册、事件监听、资源加载、渲染器启动等。
 */
public final class Famara extends JavaPlugin {

    /**
     * 存储渲染进度的映射表，键为任务标识符，值为当前进度。
     */
    public static Map<String, Integer> progress = new HashMap<>();

    /**
     * 颜色配置文件的YAML配置对象。
     */
    public static YamlConfiguration yamlConfiguration = new YamlConfiguration();

    /**
     * 渲染任务列表。
     */
    public static ArrayList<RenderTask> tasks = new ArrayList<>();

    /**
     * 渲染速度设置，默认为7。
     */
    public static int speed = 7;

    /**
     * 颜色缓存，用于快速查找颜色值。
     */
    public static Map<String, Color> colorCache = new HashMap<>();

    /**
     * 渲染结果缓存，键为任务ID，值为对应的物品堆。
     */
    public static Map<Integer, ItemStack> results = new HashMap<>();

    /**
     * 存储每个图片的渲染速度。
     */
    public static Map<String,Integer> renderSpeeds = new HashMap<>();

    /**
     * 实际使用的渲染速度映射。
     */
    public static Map<String,Integer> renderRealSpeeds = new HashMap<>();

    /**
     * 排除的方块列表，表示没有上色的方块。
     */
    public static ArrayList<String> excludingBlocks = new ArrayList<>();

    /**
     * 当前颜色配置版本号。
     */
    public static int colorVersion = 1;

    /**
     * 插件启用时调用的方法。
     * 负责初始化插件的各种功能模块，包括命令、事件、资源加载等。
     */
    @Override
    public void onEnable() {
        // 注册命令执行器和补全器
        Objects.requireNonNull(this.getCommand("fa")).setExecutor(new fa());
        Objects.requireNonNull(this.getCommand("fa")).setTabCompleter(new faTabComplete());

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new OpenBookE(), this);
        getServer().getPluginManager().registerEvents(new BookClickE(), this);
        getServer().getPluginManager().registerEvents(new OnUseCameraE(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerJoinE(), this);
        getServer().getPluginManager().registerEvents(new OnLoadFilm(), this);
        getServer().getPluginManager().registerEvents(new PlayerDownloadResPackE(), this);

        // 初始化照片存储目录
        File pictureDir = new File("./plugins/Famara/pictures");
        if (!pictureDir.exists()) {
            boolean mkdir = pictureDir.mkdirs();
            if (!mkdir) {
                Logger.getGlobal().warning("文件夹创建失败");
            }
        }

        // 加载颜色配置文件
        try {
            File file = new File("./plugins/Famara/colors.yml");
            Runnable unpackColor = () -> {
                ClassLoader classLoader = Famara.class.getClassLoader();
                URL resource = classLoader.getResource("colors.yml");

                try {
                    InputStream inputStream = resource.openStream();
                    Files.copy(inputStream, file.toPath());
                    inputStream.close();
                    yamlConfiguration.load("./plugins/Famara/colors.yml");
                } catch (IOException | InvalidConfigurationException e) {
                    throw new RuntimeException(e);
                }

            };
            if (file.exists()) {
                yamlConfiguration.load("./plugins/Famara/colors.yml");
                int version = yamlConfiguration.getInt("version");
                if (colorVersion > version) {
                    if (file.delete()) {
                        unpackColor.run();
                    }else {
                        Logger.getGlobal().warning("Color.yml文件更新失败，请尝试手动删除");
                    }
                }
            }else {
                unpackColor.run();
            }

        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        // 启动渲染器
        RenderRunner.Runner();

        // 定时清理颜色缓存
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

        // 定时重新加载物品缓存
        new BukkitRunnable() {
            @Override
            public void run() {
                LoadItem.loadItem();
            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 8);

        // 定时同步渲染速度数据
        new BukkitRunnable() {
            @Override
            public void run() {
                Famara.renderRealSpeeds.clear();
                Famara.renderRealSpeeds.putAll(Famara.renderSpeeds);
                Famara.renderSpeeds.clear();

            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(Famara.class), 0, 20);

        // 初始化配方书主页面
        RecipeBook.mainPage = new BookPage("相机配方", new HashMap<>(), new HashMap<>(), new HashMap<>());

        // 加载物品、照片和配方
        LoadItem.loadItem();
        loadPhoto();
        LoadRecipe.loadRecipe();

        // 对在线玩家执行加入检查
        getServer().getOnlinePlayers().forEach(OnPlayerJoinE::startCheck);

        // 构建排除方块列表
        List<Material> itemStacks = new ArrayList<>(Arrays.stream(Material.values()).toList());
        itemStacks.removeIf(i -> !i.isBlock());
        itemStacks.forEach(i -> excludingBlocks.add(i.name().toUpperCase()));
        excludingBlocks.removeIf(Famara.yamlConfiguration::contains);
    }

    /**
     * 插件禁用时调用的方法。
     * 可用于执行清理工作。
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

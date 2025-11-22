package com.wenkrang.famara;

import com.wenkrang.famara.event.*;
import com.wenkrang.famara.itemSystem.BookPage;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.*;
import com.wenkrang.famara.loader.LoadCmd;
import com.wenkrang.famara.loader.LoadItem;
import com.wenkrang.famara.loader.LoadRecipe;
import com.wenkrang.famara.render.RenderRunner;
import com.wenkrang.famara.render.RenderTask;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static com.wenkrang.famara.loader.LoadPhoto.loadPhoto;
import static org.bukkit.Bukkit.getServer;

/**
 * 主插件类，负责插件的初始化和关闭逻辑。
 * 包括命令注册、事件监听、资源加载、渲染器启动等。
 */
public final class Famara {

    /**
     * 存储渲染进度的映射表，键为任务标识符，值为当前进度。
     */
    public static ConcurrentHashMap<String, Integer> progress = new ConcurrentHashMap<>();

    /**
     * 渲染任务列表。
     */
    public static CopyOnWriteArrayList<RenderTask> tasks = new CopyOnWriteArrayList<>();

    /**
     * 渲染速度设置，默认为7。
     */
    public static int speed = 7;

    /**
     * 渲染结果缓存，键为任务ID，值为对应的物品堆。
     */
    public static ConcurrentHashMap<Integer, ItemStack> results = new ConcurrentHashMap<>();

    /**
     * 存储每个图片的渲染速度。
     */
    public static ConcurrentHashMap<String,Integer> renderSpeeds = new ConcurrentHashMap<>();

    /**
     * 实际使用的渲染速度映射。
     */
    public static ConcurrentHashMap<String,Integer> renderRealSpeeds = new ConcurrentHashMap<>();

    /**
     * 当前颜色配置版本号。
     */
    public static int Version = 4;

    /**
     * 插件启用时调用的方法。
     * 负责初始化插件的各种功能模块，包括命令、事件、资源加载等。
     */

    public static UUID resPack = UUID.fromString("4cc63921-99d7-40d7-bc00-f44b5ecb2437");

    public static Inventory excludingBlocksInv = Bukkit.createInventory(null, 54,"54");

    public static JavaPlugin plugin;

    /**
     * 插件数据文件夹路径。
     */
    public static File dataFolder;

    /**
     * 将jar内的文件解压至指定文件夹
     *
     * @param name 文件名
     * @param file 指定目录
     */
    public static void unpackFile(String name, File file) {
        try {
            Runnable runnable = () -> {
                try {
                    InputStream inputStream = Famara.getPlugin().getResource(name);
                    Files.copy(inputStream, file.toPath());
                    inputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            if (file.exists()) {
                YamlConfiguration tmp = YamlConfiguration.loadConfiguration(file);
                tmp.load(file);
                int version = tmp.getInt("version");
                if (Version > version) {
                    if (file.delete()) {
                        runnable.run();
                    }else {
                        Logger.getGlobal().warning(name + "文件更新失败，请尝试手动删除");
                    }
                }
            } else {
                runnable.run();
            }
        }catch (Exception e) {
            Logger.getGlobal().warning("§c§l[-] §r文件加载失败：" + name);
            e.printStackTrace();
        }

    }
    public static void mkdir(File file) {
        if (!file.exists()) {
            boolean mkdir = file.mkdirs();
            if (!mkdir) {
                Logger.getGlobal().warning("Failed to create directory");
            }
        }
    }

    public static JavaPlugin getPlugin() {
        try {
            // 使用完整类名
            Class<?> bootLoaderClass = Class.forName("com.wenkrang.famara.BootLoader");
            Method getPluginMethod = JavaPlugin.class.getMethod("getPlugin", Class.class);
            return (JavaPlugin) getPluginMethod.invoke(null, bootLoaderClass);
        } catch (Exception ignored) {
            // 备用方案
            return (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("Famara");
        }
    }

    public void onEnable() {
        //设置插件实例
        plugin = getPlugin();

        new BukkitRunnable() {
            @Override
            public void run() {
                unpackFile("language.yml", new File("./plugins/Famara/language.yml"));
                Translation.setCurrent(YamlConfiguration.loadConfiguration(new File("./plugins/Famara/language.yml")));
            }
        }.runTaskLater(getPlugin(), 5);

        //加载配置
        unpackFile("config.yml", new File("./plugins/Famara/config.yml"));
        Config.setCurrent(YamlConfiguration.loadConfiguration(new File("./plugins/Famara/config.yml")));

        //Bstats统计
        int pluginId = 27448;
        Metrics metrics = new Metrics(getPlugin(), pluginId);

        metrics.addCustomChart(new SimplePie("chart_id", () -> "My value"));

        ConsoleCommandSender consoleSender = getServer().getConsoleSender();
        consoleSender.sendMessage("    ____                                ");
        consoleSender.sendMessage("   / __/___ _____ ___  ____ __________ _");
        consoleSender.sendMessage("  / /_/ __ `/ __ `__ \\/ __ `/ ___/ __ `/");
        consoleSender.sendMessage(" / __/ /_/ / / / / / / /_/ / /  / /_/ / ");
        consoleSender.sendMessage("/_/  \\__,_/_/ /_/ /_/\\__,_/_/   \\__,_/  \n");
        ConsoleLogger.info("Server version: " + VersionChecker.getVersion());

        ConsoleLogger.info("Registering commands");
        // 注册命令执行器和 completer
        LoadCmd.registerCommands();

        ConsoleLogger.info("Registering event listeners");
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new BookClickE(), getPlugin());
        getServer().getPluginManager().registerEvents(new OnLoadFilm(), getPlugin());
        getServer().getPluginManager().registerEvents(new OnPlayerJoinE(), getPlugin());
        getServer().getPluginManager().registerEvents(new OnUseCameraE(), getPlugin());
        getServer().getPluginManager().registerEvents(new OpenBookE(), getPlugin());
        getServer().getPluginManager().registerEvents(new PlayerDownloadResPackE(), getPlugin());

        ConsoleLogger.info("Initializing photo storage directory");
        // 初始化照片存储目录
        dataFolder = plugin.getDataFolder();

        mkdir(new File(plugin.getDataFolder(), "pictures"));

        mkdir(new File(plugin.getDataFolder(), "players"));

        mkdir(new File(plugin.getDataFolder(), "update"));
        // 加载颜色配置文件
        File file = new File(plugin.getDataFolder(), "colors.yml");
        unpackFile("colors.yml", file);
        ColorManager.loadColor();

        ConsoleLogger.info("Starting renderer");
        // 启动渲染器
        RenderRunner.Runner();

        ConsoleLogger.info("Starting scheduled tasks");
        // 定时清理颜色缓存
        new BukkitRunnable() {
            @Override
            public void run() {
                ColorManager.colorCache.clear();
            }
        }.runTaskTimer(getPlugin(), 0, 200);

        // 定时重新加载物品缓存
        new BukkitRunnable() {
            @Override
            public void run() {
                ColorManager.excludingBlocks.clear();
                List<Material> itemStacks = new ArrayList<>(Arrays.stream(Material.values()).toList());
                itemStacks.removeIf(t -> ColorManager.yamlConfiguration.contains(t.name().toUpperCase()));
                itemStacks.removeIf(t -> !t.isBlock());
                itemStacks.removeIf(t -> !t.isItem());
                itemStacks.removeIf(Material::isAir);
                itemStacks.forEach(t -> ColorManager.excludingBlocks.add(t.name().toUpperCase()));
                List<String> ShownBlocks = ColorManager.excludingBlocks.stream().limit(54).toList();
                for (int i = 0;i < ShownBlocks.size();i++) {
                    ItemStack itemStack = new ItemStack(itemStacks.get(i));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(itemStack.getType().name());
                    itemStack.setItemMeta(itemMeta);
                    excludingBlocksInv.setItem(i, itemStack);
                }
                LoadItem.loadItem();
            }
        }.runTaskTimer(Famara.getPlugin(), 0, 8);

        // 定时同步渲染速度数据
        new BukkitRunnable() {
            @Override
            public void run() {
                Famara.renderRealSpeeds.clear();
                Famara.renderRealSpeeds.putAll(Famara.renderSpeeds);
                Famara.renderSpeeds.clear();
            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(), 0, 20);

        // 定时更新Colors.yml
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File file = new File(plugin.getDataFolder(), "colors.yml");
                    File updateFile = new File(plugin.getDataFolder(), "update/colors.yml");
                    UnsafeDownloader.downloadFile("https://gitee.com/wenkrang/Famara/raw/master/colors_new.yml", updateFile);
                    if (updateFile.exists()) {
                        YamlConfiguration updateYaml = YamlConfiguration.loadConfiguration(updateFile);
                        if (updateYaml.getInt("version") > ColorManager.yamlConfiguration.getInt("version")) {

                            file.delete();
                            Files.copy(updateFile.toPath(), file.toPath());
                            ColorManager.yamlConfiguration.load(file);
                            ConsoleLogger.info("Colors.yml updated");

                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(), 0, 6000);

        // 完成照片渲染
        new BukkitRunnable() {
            @Override
            public void run() {
                Famara.progress.forEach((id, progress) -> {
                    if (progress >= 16384) Famara.progress.remove(id);
                });
            }
        }.runTaskTimer(Famara.getPlugin(), 0 , 20);


        ConsoleLogger.info("Initializing recipe book");
        // 初始化配方书主页面
        RecipeBook.mainPage = new BookPage("相机配方", new HashMap<>(), new HashMap<>(), new HashMap<>());

        ConsoleLogger.info("Loading items, photos and recipes");
        // 加载物品、照片和配方
        LoadItem.loadItem();
        loadPhoto(plugin.getDataFolder());
        LoadRecipe.loadRecipe();


        // 对在线玩家执行加入检查
        getServer().getOnlinePlayers().forEach(OnPlayerJoinE::startCheck);

        ConsoleLogger.info("Loading complete, current version: 1.2");
    }

    /**
     * 插件禁用时调用的方法。
     * 可用于执行清理工作。
     */
    public void onDisable() {
        // Plugin shutdown logic
    }
}

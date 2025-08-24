package com.wenkrang.famara;

import com.wenkrang.famara.Loader.LoadRecipe;
import com.wenkrang.famara.command.faTabComplete;
import com.wenkrang.famara.event.*;
import com.wenkrang.famara.itemSystem.BookPage;
import com.wenkrang.famara.Loader.LoadItem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ConsoleLoger;
import com.wenkrang.famara.lib.UnsafeDownloader;
import com.wenkrang.famara.lib.VersionChecker;
import com.wenkrang.famara.lib.text;
import com.wenkrang.famara.render.RenderRunner;
import com.wenkrang.famara.render.RenderTask;
import com.wenkrang.famara.command.fa;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
    public static ConcurrentHashMap<String, Integer> progress = new ConcurrentHashMap<>();

    /**
     * 颜色配置文件的YAML配置对象。
     */
    public static YamlConfiguration yamlConfiguration = new YamlConfiguration();

    /**
     * 渲染任务列表。
     */
    public static CopyOnWriteArrayList<RenderTask> tasks = new CopyOnWriteArrayList<>();

    /**
     * 渲染速度设置，默认为7。
     */
    public static int speed = 7;

    /**
     * 颜色缓存，用于快速查找颜色值。
     */
    public static ConcurrentHashMap<String, Color> colorCache = new ConcurrentHashMap<>();

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
     * 排除的方块列表，表示没有上色的方块。
     */
    public static CopyOnWriteArrayList<String> excludingBlocks = new CopyOnWriteArrayList<>();

    /**
     * 当前颜色配置版本号。
     */
    public static int Version = 3;

    /**
     * 插件启用时调用的方法。
     * 负责初始化插件的各种功能模块，包括命令、事件、资源加载等。
     */

    public static UUID resPack = UUID.fromString("4cc63921-99d7-40d7-bc00-f44b5ecb2437");

    public static Inventory excludingBlocksInv = Bukkit.createInventory(null, 54,"54");

    public static void loadPack(String name, File file) {
        try {
            Runnable runnable = () -> {
                ClassLoader classLoader = Famara.class.getClassLoader();
                URL resource = classLoader.getResource(name);

                try {
                    InputStream inputStream = resource.openStream();
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
    @Override
    public void onEnable() {



        ConsoleCommandSender consoleSender = getServer().getConsoleSender();
        consoleSender.sendMessage("    ____                                ");
        consoleSender.sendMessage("   / __/___ _____ ___  ____ __________ _");
        consoleSender.sendMessage("  / /_/ __ `/ __ `__ \\/ __ `/ ___/ __ `/");
        consoleSender.sendMessage(" / __/ /_/ / / / / / / /_/ / /  / /_/ / ");
        consoleSender.sendMessage("/_/  \\__,_/_/ /_/ /_/\\__,_/_/   \\__,_/  \n");
        ConsoleLoger.info("Server version: " + VersionChecker.getVersion());

        ConsoleLoger.info("Registering commands");
        // 注册命令执行器和补全器
        Objects.requireNonNull(this.getCommand("fa")).setExecutor(new fa());
        Objects.requireNonNull(this.getCommand("fa")).setTabCompleter(new faTabComplete());

        ConsoleLoger.info("Registering event listeners");


        // 注册事件监听器
        new OpenBookE(this);
        new BookClickE(this);
        new OnUseCameraE(this);
        OnPlayerJoinE playerJoinEvent = new OnPlayerJoinE(this);
        new OnLoadFilm(this);
        new PlayerDownloadResPackE(this);

        ConsoleLoger.info("Initializing photo storage directory");
        // 初始化照片存储目录
        mkdir(new File(getDataFolder(), "pictures"));

        mkdir(new File(getDataFolder(), "players"));

        mkdir(new File(getDataFolder(), "update"));
        loadPack("language.yml", new File(getDataFolder(), "language.yml"));
        text.config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "language.yml"));
        ConsoleLoger.info("Loading color configuration file");
        // 加载颜色配置文件
        File file = new File(getDataFolder(), "colors.yml");
        loadPack("colors.yml", file);
        try {
            yamlConfiguration.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        ConsoleLoger.info("Starting renderer");
        // 启动渲染器
        RenderRunner.Runner();

        ConsoleLoger.info("Starting scheduled tasks");
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
                excludingBlocks.clear();
                List<Material> itemStacks = new ArrayList<>(Arrays.stream(Material.values()).toList());
                itemStacks.removeIf(t -> yamlConfiguration.contains(t.name().toUpperCase()));
                itemStacks.removeIf(t -> !t.isBlock());
                itemStacks.removeIf(t -> !t.isItem());
                itemStacks.removeIf(Material::isAir);
                itemStacks.forEach(t -> excludingBlocks.add(t.name().toUpperCase()));
                for (int i = 0;i < excludingBlocksInv.getSize();i++) {
                    ItemStack itemStack = new ItemStack(itemStacks.get(i));
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setDisplayName(itemStack.getType().name());
                    itemStack.setItemMeta(itemMeta);
                    excludingBlocksInv.setItem(i, itemStack);
                }
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

        // 定时更新Colors.yml
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    File file = new File(getDataFolder(), "colors.yml");
                    File updateFile = new File(getDataFolder(), "update/colors.yml");
                    UnsafeDownloader.downloadFile("https://gitee.com/wenkrang/Famara/raw/master/colors.yml", updateFile);
                    if (updateFile.exists()) {
                        YamlConfiguration updateYaml = YamlConfiguration.loadConfiguration(updateFile);
                        if (updateYaml.getInt("version") > yamlConfiguration.getInt("version")) {
                            file.delete();
                            Files.copy(updateFile.toPath(), file.toPath());
                            yamlConfiguration.load(file);
                            ConsoleLoger.info("Colors.yml updated");
                        }
                    }
                } catch (Exception e) {

                }
            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(Famara.class), 0, 6000);

        // 完成照片渲染
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!Famara.progress.isEmpty()) {
                        Famara.progress.forEach((id, progress) -> {
                            if (Famara.progress.containsKey(id) && progress >= 16384) Famara.progress.remove(id);
                        });}
                }catch (Exception e) {
                    //Ignore
                }

            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0 , 20);

        // 对在线玩家执行加入检查
        new BukkitRunnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().forEach(playerJoinEvent::startCheck);
            }
        }.runTaskLater(Famara.getPlugin(Famara.class), 20);




        ConsoleLoger.info("Initializing recipe book");
        // 初始化配方书主页面
        RecipeBook.mainPage = new BookPage("相机配方", new HashMap<>(), new HashMap<>(), new HashMap<>());

        ConsoleLoger.info("Loading items, photos and recipes");
        // 加载物品、照片和配方
        LoadItem.loadItem();
        loadPhoto(getDataFolder());
        LoadRecipe.loadRecipe();

        ConsoleLoger.info("Loading complete, current version: alpine 1.0");

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

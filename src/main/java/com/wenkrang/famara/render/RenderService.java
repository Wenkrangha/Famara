package com.wenkrang.famara.render;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.render.lib.LoadPhoto;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class RenderService {
    /**
     * 存储渲染进度的映射表，键为任务标识符，值为当前进度。
     */
    public ConcurrentHashMap<String, Integer> progress = new ConcurrentHashMap<>();
    /**
     * 渲染任务列表。
     */
    @NotNull
    public CopyOnWriteArrayList<RenderTask> tasks = new CopyOnWriteArrayList<>();
    /**
     * 渲染速度设置，默认为300。
     */
    public int speed = 400;
    /**
     * 渲染结果缓存，键为任务ID，值为对应的物品堆。
     */
    public ConcurrentHashMap<Integer, ItemStack> results = new ConcurrentHashMap<>();
    /**
     * 存储每个图片的渲染速度。
     */
    public ConcurrentHashMap<String,Integer> renderSpeeds = new ConcurrentHashMap<>();
    /**
     * 实际使用的渲染速度映射。
     */
    public ConcurrentHashMap<String,Integer> renderRealSpeeds = new ConcurrentHashMap<>();

    public RenderRunner runner;

    public File pictureFolder;

    public RenderService(File pictureFolder) {
        runner = new RenderRunner(this);
        this.pictureFolder = pictureFolder;
    }

    /**
     * 启动服务
     */
    public void start() {
        LoadPhoto.loadPhoto(pictureFolder);

        runner.Runner();

        // 定时同步渲染速度数据
        new BukkitRunnable() {
            @Override
            public void run() {
                renderRealSpeeds.clear();
                renderRealSpeeds.putAll(renderSpeeds);
                renderSpeeds.clear();
            }
        }.runTaskTimerAsynchronously(Famara.getPlugin(), 0, 20);
    }

    /**
     * 添加渲染任务
     * @param task 渲染任务
     */
    public void addTask(RenderTask task) {
        progress.put(task.ctx.id, 0);
        tasks.add(task);
    }

    /**
     * 创建照片文件
     * @param id 任务ID
     * @return 照片
     */
    public BufferedImage createPhotoFile(String id, File picture) {
        //初始化照片
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);

        try {
            boolean newFile = picture.createNewFile();
            if (!newFile) {
                picture.delete();
                throw new RuntimeException("照片文件创建失败:" + picture.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return image;
    }

    public static ItemStack getPhoto(BufferedImage image, MapView map) {
        map.setLocked(true);
        MapRenderer mapRenderer = new MapRenderer() {
            @Override
            public void render(@NotNull MapView mapView, MapCanvas mapCanvas, @NotNull Player player) {
                //渲染照片
                mapCanvas.drawImage(0, 0, image);
            }
        };
        map.addRenderer(mapRenderer);

        //发放照片
        ItemStack itemStack = ItemSystem.get("photo");
        ItemMeta itemMeta = itemStack.getItemMeta();
        MapMeta mapMeta = (MapMeta) itemMeta;

        mapMeta.setMapView(map);

        itemStack.setItemMeta(mapMeta);
        return itemStack;
    }

    public ItemStack takePhoto(Player player) {
        MapView map = Bukkit.createMap(player.getWorld());

        //生成mapID
        String id = String.valueOf(map.getId());

        File picture = new File(pictureFolder, id + ".png");
        BufferedImage image = createPhotoFile(id, picture);

        // 新建渲染上下文
        RenderContext ctx = new RenderContext(id, image, player, picture, map);

        RenderTask task = new RenderTask(ctx);

        addTask(task);

        return getPhoto(image, map);
    }

}

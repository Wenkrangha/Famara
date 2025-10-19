package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.Loader.LoadResourcePack;
import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.lib.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

import static com.wenkrang.famara.event.OnUseCameraE.getId;
import static com.wenkrang.famara.lib.ItemUtils.setModelSafely;

public class OnPlayerJoinE implements Listener {
    Famara plugin;
    public OnPlayerJoinE(Famara plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getScoreboardTags().contains("FamaraResPackIncluded")) {
            event.getPlayer().removeScoreboardTag("FamaraResPackIncluded");
            LoadResourcePack.load(event.getPlayer());
        }
        //进行相关检测
        startCheck(event.getPlayer());
    }

    public void startCheck(Player player) {
        BossBar progress = Bukkit.createBossBar("冲洗进度", BarColor.WHITE, BarStyle.SOLID);
        File PlayerFile = new File(plugin.getDataFolder(), "players/" + player.getName() + ".yml");
        
        if(!PlayerFile.exists()) {
            try {
                if (!PlayerFile.createNewFile()) {
                    Logger.getGlobal().warning("Failed to create PlayerFile");
                }
            }catch (Exception ignore){
            }
            player.getInventory().addItem(ItemSystem.get("recipeBook"));
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                }
                if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
                    if (player.getInventory().getItemInMainHand()
                            .getItemMeta()
                            .getDisplayName()
                            .equalsIgnoreCase("§f照片（按右键撕开拉片）")) {
                        int id = getId(player.getInventory().getItemInMainHand(), 3);

                        if (!Famara.progress.containsKey(String.valueOf(id))) {
                            progress.removeAll();
                            return;
                        }
                        Integer i = Famara.progress.get(String.valueOf(id));

                        if (i != 16384) progress.addPlayer(player);

                        try {
                            progress.setProgress((double) i / 16384);
                            if (Famara.renderRealSpeeds.get(String.valueOf(id)) == null) return;
                            progress.setTitle("冲洗进度 "
                                    + Math.round((double) i / 16384 * 100) + "% "
                                    + "s:" + Famara.renderRealSpeeds.get(String.valueOf(id)) + "p/s "
                                    + "ETA:" + (16384 - Famara.progress.get(String.valueOf(id))) /
                                    Math.max(1, Famara.renderRealSpeeds.get(String.valueOf(id))) + "s");

                        } catch (Exception e) {
                            progress.removeAll();
                            Famara.progress.remove(String.valueOf(id));
                        }

                        if (i == 16384) {
                            progress.removeAll();
                        }
                    }else {
                        progress.removeAll();
                    }
                }else {
                    progress.removeAll();
                }
                //TODO:更优雅的开镜放大
                ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

                PotionEffectType type = PotionEffectType.getByName("slowness");
                if (type == null) type = PotionEffectType.SLOW;
                //检测手中是否拿相机
                if (ItemUtils.checkName(itemInMainHand, "§f相机") && ItemUtils.matchKey(itemInMainHand.getItemMeta(),"famara_open",10)) {
                    ItemMeta itemMeta = itemInMainHand.getItemMeta();
                    if (player.isSneaking()) {
                        // 玩家潜行时关闭相机
                        itemMeta = setModelSafely(itemMeta,new NamespacedKey("famara", "famara_close"), 20);
                        itemInMainHand.setItemMeta(itemMeta);
                        player.getInventory().setItemInMainHand(itemInMainHand);
                        player.getWorld().playSound(player.getLocation(), "famara:famara.viewfinder", 1, 1);
                    } else {
                        // 相机开启时给玩家添加缓慢效果
                        player.addPotionEffect(new PotionEffect(type, 9999999, 4));
                    }
                } else {
                    //如果手中不是相机，就去掉缓慢效果
                    //TODO:检测是否是相机添加的效果，目前不是相机添加的缓慢也会被去除
                    if (player.hasPotionEffect(type) &&
                            Objects.requireNonNull(player.getPotionEffect(type)).getAmplifier() == 4) {
                        player.removePotionEffect(type);
                    }
                }
            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 3);
    }
}

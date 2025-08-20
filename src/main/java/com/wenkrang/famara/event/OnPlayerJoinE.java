package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
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

import java.util.Objects;

import static com.wenkrang.famara.event.OnUseCameraE.getId;

public class OnPlayerJoinE implements Listener {

    @EventHandler
    public static void onHoldFilm(PlayerJoinEvent event) {
        startCheck(event.getPlayer());
    }

    public static void startCheck(Player player) {
        BossBar progress = Bukkit.createBossBar("冲洗进度", BarColor.WHITE, BarStyle.SOLID);

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

                        if (!Famara.progress.containsKey(String.valueOf(id))) return;
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
                            Famara.progress.remove(String.valueOf(id));
                        }
                    }else {
                        progress.removeAll();
                    }
                }else {
                    progress.removeAll();
                }
                //TODO:更优雅的开镜放大
                if (player.getInventory().getItemInMainHand().getItemMeta() != null) {
                    if (Objects.requireNonNull(player.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equalsIgnoreCase("§f相机")) {
                        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                        ItemMeta itemMeta = itemInMainHand.getItemMeta();
                        NamespacedKey itemModel = itemInMainHand.getItemMeta().getItemModel();
                        if (player.isSneaking()) {
                            if (!itemModel.getKey().equalsIgnoreCase("famara_open")) return;
                            itemMeta.setItemModel(new NamespacedKey("famara", "famara_close"));
                            itemInMainHand.setItemMeta(itemMeta);
                            player.getInventory().setItemInMainHand(itemInMainHand);
                        }
                        if (itemModel.getKey().equalsIgnoreCase("famara_open")) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 9999999, 4));
                        } else {
                            player.removePotionEffect(PotionEffectType.SLOWNESS);
                        }
                    } else {
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                    }
                } else {
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                }

            }
        }.runTaskTimer(Famara.getPlugin(Famara.class), 0, 3);
    }
}

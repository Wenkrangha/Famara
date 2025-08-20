package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ItemUtils;
import com.wenkrang.famara.render.PhotoRender;
import com.wenkrang.famara.render.RenderLib;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class OnUseCameraE implements Listener {
    public static int getId(ItemStack itemStack, int index) {
        String s = itemStack.getItemMeta().getLore().get(index);
        return Integer.parseInt(s.replace("§7照片编号：", ""));
    }
    @EventHandler
    public static void onUseCamera(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND &&
                (event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                        event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) return;
            //TODO:这里有大问题，会修改ItemMap
            if (!event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§f相机")) {
                ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
                NamespacedKey itemModel = itemInMainHand.getItemMeta().getItemModel();
                if (itemModel.getKey().equalsIgnoreCase("famara_close")) {
                    ItemMeta itemMeta = itemInMainHand.getItemMeta();
                    itemMeta.setItemModel(new NamespacedKey("famara", "famara_open"));
                    itemInMainHand.setItemMeta(itemMeta);
                    event.getPlayer().getInventory().setItemInMainHand(itemInMainHand);
                    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara:famara.viewfinder", 1, 1);
                    return;
                }
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara:famara.shutter", 1, 1);
                try {
                    if (ItemUtils.getFilmAmount(itemInMainHand) <= 0)
                        return;
                    else {
                        int filmAmount = ItemUtils.getFilmAmount(itemInMainHand);
                        ItemStack itemInMainHand1 = ItemUtils.setFilmAmount(itemInMainHand, filmAmount - 1);
                        event.getPlayer().getInventory().setItemInMainHand(itemInMainHand1);
                    }
                    ItemStack itemStack = PhotoRender.TakePhoto(event.getPlayer());
                    MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                    int mapId = mapMeta.getMapId();
                    ItemStack cameraFilmed = ItemSystem.itemMap.get("camera_filmed");
                    List<String> lore = cameraFilmed.getItemMeta().getLore();
                    lore.set(3, "§7照片编号：" + mapId);
                    ItemMeta itemMeta = cameraFilmed.getItemMeta();
                    itemMeta.setLore(lore);
                    cameraFilmed.setItemMeta(itemMeta);
                    Famara.results.put(mapId, itemStack);
                    int filmAmount = ItemUtils.getFilmAmount(itemInMainHand);
                    ItemStack itemInMainHand1 = ItemUtils.setFilmAmount(cameraFilmed, filmAmount);
                    if (filmAmount == 0) {
                        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara:famara.remove.film.box", 1, 1);
                    }
                    event.getPlayer().getInventory().setItemInMainHand(itemInMainHand1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (event.getPlayer().isSneaking() && event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§f相机（Shift + 右键拉出撕拉片）")) {
                int i = getId(event.getPlayer().getInventory().getItemInMainHand(), 3);
                try {
                    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara:famara.pull.film", 1, 1);
                    ItemStack itemStack = ItemSystem.itemMap.get("photo_unPull");
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    itemMeta.setItemModel(new NamespacedKey("famara", "photo"));
                    List<String> lore = itemMeta.getLore();

                    if (lore.size() >= 4) {
                        lore.set(3,"§7照片编号：" + i);
                    } else lore.add(3,"§7照片编号：" + i);

                    itemMeta.setLore(lore);
                    itemStack.setItemMeta(itemMeta);
                    event.getPlayer().getInventory().addItem(itemStack);
                    ItemStack itemStack1 = ItemSystem.itemMap.get("camera");
                    ItemStack itemStack2 = ItemUtils.setFilmAmount(itemStack1, ItemUtils.getFilmAmount(event.getPlayer().getInventory().getItemInMainHand()));
                    event.getPlayer().getInventory().setItemInMainHand(itemStack2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equalsIgnoreCase("§f照片（按右键撕开拉片）")) {
                int i = getId(event.getPlayer().getInventory().getItemInMainHand(), 3);
                if (Famara.progress.containsKey(String.valueOf(i))) {
                    event.getPlayer().sendMessage("§c§l[-] §r照片冲洗未完成，请等待渲染再撕开拉片");
                    return;
                }
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara:famara.film", 1, 1);

                try {
                    ItemStack itemStack = null;
                    if (Famara.results.containsKey(i)) {
                        itemStack = Famara.results.get(i);
                    }else {
                        if (Bukkit.getMap(i) == null) return;
                        //TODO：重启服务器后，没有撕开的照片没法获取（加急！！！！！）
                        RenderLib.getPhoto(ImageIO.read(new File("./plugins/Famara/pictures/" + i + ".png")), Bukkit.getMap(i));
                    }
                    event.getPlayer().getInventory().setItemInMainHand(itemStack);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

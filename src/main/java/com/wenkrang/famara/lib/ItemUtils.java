package com.wenkrang.famara.lib;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.List;

public class ItemUtils {
    public static boolean checkModel(ItemStack itemStack, String id) {
        if (itemStack == null ||
                itemStack.getItemMeta() == null ||
                getModel(itemStack.getItemMeta()) == null) return false;
        return getModel(itemStack.getItemMeta()).getKey().equalsIgnoreCase(id);
    }

    public static boolean checkName(ItemStack itemStack, String s) {
        if (itemStack == null || itemStack.getItemMeta() == null) return false;
        return itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(s);
    }

    public static void setCamera(Player player) {
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        if (itemInMainHand.getItemMeta() != null) {
            ItemStack itemStack = setFilmAmount(itemInMainHand, 16);
            player.getInventory().setItemInMainHand(itemStack);
        }
    }

    public static int getFilmAmount(ItemStack itemStack) {
        List<String> lore = itemStack.getItemMeta().getLore();
        String s = lore.get(2);
        if (s.equalsIgnoreCase("§7未装填胶卷")) return 0;
        return Integer.parseInt(s.replace("§7已装填彩色胶卷（16 / ", "").replace("）", ""));
    }

    public static ItemStack setFilmAmount(ItemStack itemStack, int i) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (i <= 0) {
            lore.set(2, "§7未装填胶卷");
        } else lore.set(2, "§7已装填彩色胶卷（16 / " + i + "）");
        itemMeta.setLore(lore);

        final var newItem = itemStack.clone();
        newItem.setItemMeta(itemMeta);
        return newItem;
    }

    /**
     * 安全地设置物品模型，由于在1.21.4以下没有关于Model的方法，必须进行区别处理
     *
     * @param itemMeta 需要修改的ItemMeta
     * @param namespacedKey 模型的命名空间
     * @param i 兼容性的物品模型id
     *
     * @return 修改后的ItemMeta
     **/
    public static ItemMeta setModelSafely(ItemMeta itemMeta, NamespacedKey namespacedKey,Integer i) {
        try {
            if (!VersionChecker.isVersionBelow("1.21.4")) {
                Method setItemModel = itemMeta.getClass().getMethod("setItemModel", NamespacedKey.class);
                setItemModel.setAccessible(true);
                setItemModel.invoke(itemMeta, namespacedKey);
            } else {
                itemMeta.setCustomModelData(i);
                return itemMeta;
            }
        }catch (Exception ignored) {}
        return itemMeta;
    }

    /**
     * 匹配物品模型
     *
     * @param itemMeta 需要匹配的ItemMeta
     * @param s 模型的命名空间
     * @param i 兼容性的物品模型id
     *
     * @return 匹配结果
     **/
    public static boolean matchKey(ItemMeta itemMeta,String s,Integer i){
        try {
            if (!VersionChecker.isVersionBelow("1.21.4")) {
                Method getItemModel = itemMeta.getClass().getMethod("getItemModel");
                getItemModel.setAccessible(true);
                NamespacedKey namespacedKey = (NamespacedKey) getItemModel.invoke(itemMeta);
                return namespacedKey != null && namespacedKey.getKey().equalsIgnoreCase(s);
            } else {
                return itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == i;
            }
        }catch (Exception ignored){
            ignored.printStackTrace();
            return false;
        }
    }
    /**
     * 获取物品模型
     *
     * @param itemMeta 需要获取的ItemMeta
     * @return 获取到的模型
     **/
    public static NamespacedKey getModel(ItemMeta itemMeta){
        try {
            if (!VersionChecker.isVersionBelow("1.21.4")) {
                Method getItemModel = itemMeta.getClass().getMethod("getItemModel");
                getItemModel.setAccessible(true);
                return (NamespacedKey) getItemModel.invoke(itemMeta);
            } else {
                return null;
            }
        }catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 获取物品模型id
     *
     * @param itemMeta 需要获取的ItemMeta
     * @return 获取到的模型id
     **/
    public static Integer getModelId(ItemMeta itemMeta){
        try {
            if (!VersionChecker.isVersionBelow("1.21.4")) {
                return null;
            } else {
                return itemMeta.hasCustomModelData() ? itemMeta.getCustomModelData() : null;
            }
        }catch (Exception ignored) {
            return null;
        }
    }
}

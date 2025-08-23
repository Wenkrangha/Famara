package com.wenkrang.famara.lib;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemUtils {
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
        }
        lore.set(2, "§7已装填彩色胶卷（16 / " + i + "）");
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}

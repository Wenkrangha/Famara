package com.wenkrang.famara.itemSystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class RecipeBook {
    public static BookPage mainPage;
    public static ItemStack RecipeBookItem;

    public static void openBook(Player player) {
        if (mainPage == null) {
            throw new RuntimeException("mainPage未初始化");
        }
        Inventory inventory = Bukkit.createInventory(null, 27, mainPage.title());
        ItemStack itemStack0 = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta itemMeta0 = itemStack0.getItemMeta();
        itemMeta0.setDisplayName("");
        itemStack0.setItemMeta(itemMeta0);
        ItemStack itemStack1 = new ItemStack(Material.COMPASS);
        ItemMeta itemMeta1 = itemStack1.getItemMeta();
        itemMeta1.setDisplayName("§f相机§9§l配方");
        itemStack1.setItemMeta(itemMeta1);
        inventory.setItem(0, itemStack0);
        inventory.setItem(1, itemStack1);
        inventory.setItem(2, itemStack0);
        inventory.setItem(3, itemStack0);
        inventory.setItem(4, itemStack0);
        inventory.setItem(5, itemStack0);
        inventory.setItem(6, itemStack0);
        inventory.setItem(7, itemStack0);
        inventory.setItem(8, itemStack0);

        player.openInventory(inventory);

        Map<Integer, ItemStack> items = mainPage.items();
        items.forEach((index, itemStack) -> {
            inventory.setItem(index + 9, itemStack.clone());
        });
    }
}

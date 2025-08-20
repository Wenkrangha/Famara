package com.wenkrang.famara.event;

import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

public class BookClickE implements Listener {
    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("相机具体配方")) {
            if (event.getRawSlot() == 1) {
                RecipeBook.openBook((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
        if (event.getView().getTitle().equalsIgnoreCase("相机配方")) {
            Map<Integer, ArrayList<ItemStack>> recipes = RecipeBook.mainPage.Recipes();
            if (event.getRawSlot() >= 9 && event.getRawSlot() <= 26) {
                if (event.isRightClick() && event.getView().getPlayer().isOp()
                        && event.getView().getPlayer().getGameMode().equals(GameMode.CREATIVE)
                        && event.getCurrentItem() != null) {
                    event.getView().getPlayer().getInventory().addItem(event.getCurrentItem());
                }
                if (event.isLeftClick()) {
                    if (recipes.containsKey(event.getRawSlot() - 9)) {
                        Inventory inventory = Bukkit.createInventory(null, 27, "相机具体配方");
                        ItemStack itemStack0 = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                        ItemMeta itemMeta0 = itemStack0.getItemMeta();
                        itemMeta0.setDisplayName(" ");
                        itemStack0.setItemMeta(itemMeta0);
                        ItemStack itemStack1 = new ItemStack(Material.WRITABLE_BOOK);
                        ItemMeta itemMeta1 = itemStack1.getItemMeta();
                        itemMeta1.setDisplayName("§f返回");
                        itemStack1.setItemMeta(itemMeta1);
                        ItemStack itemStack3 = new ItemStack(Material.CRAFTING_TABLE);
                        ItemMeta itemMeta3 = itemStack3.getItemMeta();
                        itemMeta3.setDisplayName("§f工作台§9§l合成");
                        ArrayList<String> lore3 = new ArrayList<>();
                        lore3.add("§7这个物品由§7§l工作台§7进行合成，按照左侧");
                        lore3.add("§7§l3x3方格内§7物品的示意，在§7§l工作台§7内");
                        lore3.add("§7摆放所示物品以§7§l进行合成");
                        itemMeta3.setLore(lore3);
                        itemStack3.setItemMeta(itemMeta3);
                        inventory.setItem(0, itemStack0);
                        inventory.setItem(1, itemStack1);
                        inventory.setItem(2, itemStack0);
                        inventory.setItem(6, itemStack0);
                        inventory.setItem(7, itemStack0);
                        inventory.setItem(8, itemStack0);
                        inventory.setItem(10, itemStack3);

                        inventory.setItem(16, event.getCurrentItem());

                        inventory.setItem(3, recipes.get(event.getRawSlot() - 9).get(0));
                        inventory.setItem(4, recipes.get(event.getRawSlot() - 9).get(1));
                        inventory.setItem(5, recipes.get(event.getRawSlot() - 9).get(2));
                        inventory.setItem(12, recipes.get(event.getRawSlot() - 9).get(3));
                        inventory.setItem(13, recipes.get(event.getRawSlot() - 9).get(4));
                        inventory.setItem(14, recipes.get(event.getRawSlot() - 9).get(5));
                        inventory.setItem(21, recipes.get(event.getRawSlot() - 9).get(6));
                        inventory.setItem(22, recipes.get(event.getRawSlot() - 9).get(7));
                        inventory.setItem(23, recipes.get(event.getRawSlot() - 9).get(8));

                        event.getView().getPlayer().openInventory(inventory);
                    }
                }
            }
            event.setCancelled(true);
        }
    }
}

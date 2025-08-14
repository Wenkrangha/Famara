package com.wenkrang.famara.event;

import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingRecipe;

import java.util.Map;

public class BookClickE implements Listener {
    @EventHandler
    public static void onClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("相机配方")) {
            Map<Integer, CraftingRecipe> recipes = RecipeBook.mainPage.Recipes();
            if (event.getRawSlot() >= 9 && event.getRawSlot() <= 26) {
                if (event.getView().getPlayer().isOp()
                        && event.getView().getPlayer().getGameMode().equals(GameMode.CREATIVE)
                        && event.getCurrentItem() != null) {
                    event.getView().getPlayer().getInventory().addItem(event.getCurrentItem());
                }
            }
            event.setCancelled(true);
        }
    }
}

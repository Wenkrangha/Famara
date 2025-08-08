package com.wenkrang.famara.event;

import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

public class OpenBookE implements Listener {
    @EventHandler
    public static void onOpen(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
                if (event.getPlayer()
                        .getInventory().getItemInMainHand()
                        .getItemMeta()
                        .getDisplayName()
                        .equals("§9§lFamara§f-法玛拉相机配方")) {
                    RecipeBook.openBook(event.getPlayer());
                }
            }
        }
    }
}

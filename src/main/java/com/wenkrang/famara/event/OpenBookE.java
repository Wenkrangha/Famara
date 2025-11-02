package com.wenkrang.famara.event;

import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.loader.LoadResourcePack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OpenBookE implements Listener {
    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
                if (event.getPlayer()
                        .getInventory().getItemInMainHand()
                        .getItemMeta()
                        .getDisplayName()
                        .equals("§9§lFamara§f-相机配方")) {
                    LoadResourcePack.load(event.getPlayer());
                    RecipeBook.openBook(event.getPlayer());
                }
            }
        }
    }
}

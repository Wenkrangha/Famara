package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.Loader.LoadResourcePack;
import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OpenBookE implements Listener {
    Famara plugin;
    public OpenBookE(Famara plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onOpen(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
                if (event.getPlayer()
                        .getInventory().getItemInMainHand()
                        .getItemMeta()
                        .getDisplayName()
                        .equals("§9§lFamara§f-相机配方")) {
                    LoadResourcePack.load(event.getPlayer(),false);
                    RecipeBook.openBook(event.getPlayer());
                }
            }
        }
    }
}

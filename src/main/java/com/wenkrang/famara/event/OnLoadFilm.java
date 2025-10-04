package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.ItemUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class OnLoadFilm implements Listener {
    Famara plugin;
    public OnLoadFilm(Famara plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void LoadFilm(PlayerSwapHandItemsEvent event) {
        if (event.getOffHandItem() != null && event.getOffHandItem().getItemMeta() != null) {
            if (ItemUtils.matchKey(event.getOffHandItem().getItemMeta(), "famara_close", 20)) {
                if (ItemUtils.getFilmAmount(event.getOffHandItem()) != 0) return;
                PlayerInventory inventory = event.getPlayer().getInventory();
                for (int i = 0;i < inventory.getSize();i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item != null && item.getItemMeta() != null) {
                        if (ItemUtils.matchKey(item.getItemMeta(), "film_box", 30)) {
                            if (item.getAmount() > 1) {
                                item.setAmount(item.getAmount() - 1);
                                inventory.setItem(i, item);
                            }else {
                                inventory.remove(item);
                            }
                            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara:famara.attach.film.box", 1, 1);
                            ItemUtils.setCamera(event.getPlayer());
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}

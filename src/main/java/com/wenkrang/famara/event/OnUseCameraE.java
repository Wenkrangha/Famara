package com.wenkrang.famara.event;

import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.render.PhotoRender;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;

import java.io.IOException;

public class OnUseCameraE implements Listener {
    @EventHandler
    public static void onUseCamera(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.HAND &&
                (event.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                        event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            if (event.getPlayer().getInventory().getItemInMainHand().equals(ItemSystem.itemMap.get("camera"))) {
                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), "famara.shutter", 1, 1);
                try {
                    PhotoRender.TakePhoto(event.getPlayer());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

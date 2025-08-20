package com.wenkrang.famara.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class PlayerDownloadResPackE implements Listener {
    @EventHandler
    public static void onPlayerDownloadResPack(org.bukkit.event.player.PlayerResourcePackStatusEvent event) {
        PlayerResourcePackStatusEvent.Status status = event.getStatus();
        
    }
}

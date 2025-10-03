package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.Translation;
import com.wenkrang.famara.lib.VersionChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.lang.reflect.Method;
import java.util.UUID;

public class PlayerDownloadResPackE implements Listener {
    Famara plugin;
    public PlayerDownloadResPackE(Famara plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static UUID getID (PlayerResourcePackStatusEvent playerResourcePackStatusEvent) {
        try {
            if (VersionChecker.isVersionBelow("1.20.3")) {
                return null;
            }else {
                Method getID = playerResourcePackStatusEvent.getClass().getMethod("getID");
                getID.setAccessible(true);
                return (UUID) getID.invoke(playerResourcePackStatusEvent);
            }
        }catch (Exception ignored){
            return null;
        }
    }

    @EventHandler
    public static void onPlayerDownloadResPack(org.bukkit.event.player.PlayerResourcePackStatusEvent event) {
        if (event.getStatus().toString().equalsIgnoreCase("SUCCESSFULLY_LOADED")) {
            event.getPlayer().addScoreboardTag("FamaraResPackIncluded");
        }
        switch (event.getStatus().toString().toUpperCase()) {
            case "DISCARDED",
                 "INVALID_URL",
                 "DECLINED",
                 "FAILED_RELOAD",
                 "FAILED_DOWNLOAD":
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError1"));
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError2"));
                break;
            case "SUCCESSFULLY_LOADED":
                event.getPlayer().sendMessage(Translation.CURRENT.of("resSuccessfully"));
                break;
            default:
                break;
        }
    }
}

package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDownloadResPackE implements Listener {
    Famara plugin;
    public PlayerDownloadResPackE(Famara plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public static void onPlayerDownloadResPack(org.bukkit.event.player.PlayerResourcePackStatusEvent event) {
        if (!Famara.resPack.equals(event.getID())) return;
        switch (event.getStatus()) {
            case DISCARDED,
                 INVALID_URL,
                 DECLINED,
                 FAILED_RELOAD,
                 FAILED_DOWNLOAD:
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError1"));
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError2"));
                break;
            case SUCCESSFULLY_LOADED:
                event.getPlayer().addScoreboardTag("FamaraResPackIncluded");
                event.getPlayer().sendMessage(Translation.CURRENT.of("resSuccessfully"));
                break;
            default:
                break;
        }
    }
}

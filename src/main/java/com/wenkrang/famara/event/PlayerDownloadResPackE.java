package com.wenkrang.famara.event;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerDownloadResPackE implements Listener {
    @EventHandler
    public static void onPlayerDownloadResPack(org.bukkit.event.player.PlayerResourcePackStatusEvent event) {
        if (!Famara.resPack.equals(event.getID())) return;
        switch (event.getStatus()) {
            case DISCARDED,
                 FAILED_RELOAD,
                 INVALID_URL,
                 DECLINED:
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError1"));
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError2"));
                break;
            case FAILED_DOWNLOAD:
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError1"));
                event.getPlayer().sendMessage(Translation.CURRENT.of("resError2"));
                event.getPlayer().sendMessage("§e§l[!]§r 如果您位于中国，请输入/fa resource china来使用加速镜像");
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

package com.wenkrang.famara.event;

import com.wenkrang.famara.lib.text;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class PlayerDownloadResPackE implements Listener {
    @EventHandler
    public static void onPlayerDownloadResPack(org.bukkit.event.player.PlayerResourcePackStatusEvent event) {
        PlayerResourcePackStatusEvent.Status status = event.getStatus();
        if (status.equals(PlayerResourcePackStatusEvent.Status.DISCARDED) |
            status.equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) |
            status.equals(PlayerResourcePackStatusEvent.Status.FAILED_RELOAD) |
            status.equals(PlayerResourcePackStatusEvent.Status.INVALID_URL) |
            status.equals(PlayerResourcePackStatusEvent.Status.DECLINED)) {
            event.getPlayer().sendMessage(text.get("resError1"));
            event.getPlayer().sendMessage(text.get("resError2"));
        }
        if (status.equals(PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)) {
            event.getPlayer().sendMessage("§e§l[!]§r 如果您位于中国，请输入/fa resource china来使用加速镜像");
        }
        if (status.equals(PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED)) {
            event.getPlayer().addScoreboardTag("FamaraResPackIncluded");
            event.getPlayer().sendMessage(text.get("resSuccessfully"));
        }
    }
}

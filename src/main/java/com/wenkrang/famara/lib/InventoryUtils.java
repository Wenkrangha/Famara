package com.wenkrang.famara.lib;

import java.lang.reflect.Method;

public class InventoryUtils {
    /**
     * 获取标题（兼容性）
     * @param view 需要查看的InventoryView
     * @return 标题
     */
    public static String getTitle(Object view) {
        try {
            Method getTitle = view.getClass().getMethod("getTitle");
            getTitle.setAccessible(true);
            return (String) getTitle.invoke(view);
        }catch (Exception ignored){
            return null;
        }
    }

    /**
     * 获取玩家（兼容性）
     * @param view 需要查看的InventoryView
     * @return 玩家
     */
    public static Object getPlayer(Object view) {
        try {
            Method getPlayer = view.getClass().getMethod("getPlayer");
            getPlayer.setAccessible(true);
            return getPlayer.invoke(view);
        }catch (Exception ignored){
            return null;
        }
    }
}

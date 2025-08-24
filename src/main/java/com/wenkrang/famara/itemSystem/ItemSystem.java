package com.wenkrang.famara.itemSystem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public class ItemSystem {
    private static final ConcurrentHashMap<String, ItemStack> itemMap = new ConcurrentHashMap<>();
    public static void put(@NotNull String id, @Nullable ItemStack sampleItem) {
        if (sampleItem == null) {
            itemMap.remove(id);
        } else {
            itemMap.put(id, sampleItem);
        }
    }
    public static ItemStack get(@NotNull String id) {
        ItemStack item = itemMap.get(id);
        if (item == null) {
            throw new IllegalArgumentException("找不到物品 " + id);
        }
        return item.clone();
    }
    @Nullable
    public static ItemStack getOrNull(@NotNull String id) {
        ItemStack item = itemMap.get(id);
        return item == null ? null : item.clone();
    }
}

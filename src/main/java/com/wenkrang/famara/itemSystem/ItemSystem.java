package com.wenkrang.famara.itemSystem;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public class ItemSystem {
    public static @NotNull ConcurrentHashMap<String, ItemStack> itemMap = new ConcurrentHashMap<>();
}

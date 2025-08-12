package com.wenkrang.famara.Loader;

import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class LoadItem {
    public static void loadItem() {
        {
            ItemStack itemStack = new ItemStack(Material.WRITABLE_BOOK);
            @NotNull ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
            itemMeta.setDisplayName("§9§lFamara§f-法玛拉相机配方");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7你有没有一些想记录下来的风景呢？");
            lore.add("§7快用相机拍下来吧！");
            lore.add("");
            lore.add("§6§l右键§6打开");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.itemMap.put("recipeBook", itemStack);
            RecipeBook.RecipeBookItem = itemStack;
        }
        {
            ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
            @NotNull ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
            itemMeta.setDisplayName("§f照片");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7一张看起来§7§l平平无奇的照片§7，");
            lore.add("§7也许你早已经忘却了照片上的§7§l人与事物§r，");
            lore.add("§7但它一直在为你记录着§7§l那一切§7......");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.itemMap.put("photo", itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.NETHERITE_HOE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§f相机");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7这是一台老式的撕拉片相机，看起来十分破旧，");
            lore.add("§7但还可以用，它会为你留下怎样的记忆呢？");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.itemMap.put("camera", itemStack);
            RecipeBook.mainPage.items().put(0, itemStack);
        }
    }
}

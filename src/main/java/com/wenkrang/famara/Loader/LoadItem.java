package com.wenkrang.famara.Loader;

import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
            ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
            @NotNull ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
            itemMeta.setDisplayName("§f照片（按右键撕开拉片）");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7一张看起来§7§l平平无奇的照片§7，");
            lore.add("§7也许你早已经忘却了照片上的§7§l人与事物§r，");
            lore.add("§7但它一直在为你记录着§7§l那一切§7......");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.itemMap.put("photo_unPull", itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.STICK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§f相机");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7这是一台老式的撕拉片相机，外观虽显陈旧，");
            lore.add("§7却依然能够捕捉时光的痕迹，它将为你定格怎样的回忆呢？");
            lore.add("§7未装填胶卷");
            lore.add("");
            lore.add("§f『你看，这小小的胶片就像一片会呼吸的画布——光与影的私语");
            lore.add("§f被封存其中，，等待显影时才揭晓答案，多浪漫呢？』");
            lore.add("§f『原理上是利用银盐感光…但你说的“浪漫”，是指它无法预览");
            lore.add("§f成像的不可控？』");
            lore.add("§f『不止哦~它的宽容度像黄昏的云霞，能吞下刺眼的高光...");
            lore.add("§f要试试吗♪』");
            lore.add("§f『…若是为了“美好回忆”，倒是可以浪费片刻光阴。』");
            lore.add("§6§lShift + 右键§r§6将拉片拉出");
            itemMeta.setItemModel(new NamespacedKey("famara", "famara_close"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            ItemSystem.itemMap.put("camera", itemStack);
            RecipeBook.mainPage.items().put(0, itemStack);
            ArrayList<ItemStack> objects = new ArrayList<>();
            objects.add(new ItemStack(Material.LEVER));
            objects.add(new ItemStack(Material.IRON_INGOT));
            objects.add(new ItemStack(Material.OAK_BUTTON));
            objects.add(new ItemStack(Material.IRON_INGOT));
            objects.add(new ItemStack(Material.GLASS_PANE));
            objects.add(new ItemStack(Material.IRON_INGOT));
            objects.add(new ItemStack(Material.IRON_INGOT));
            objects.add(new ItemStack(Material.IRON_INGOT));
            objects.add(new ItemStack(Material.IRON_INGOT));
            RecipeBook.mainPage.Recipes().put(0, objects);
        }
        {
            ItemStack itemStack = new ItemStack(Material.STICK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§f相机（Shift + 右键拉出撕拉片）");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7这是一台老式的撕拉片相机，外观虽显陈旧，");
            lore.add("§7却依然能够捕捉时光的痕迹，它将为你定格怎样的回忆呢？");
            //§7已装填彩色胶卷（16 / 0）
            lore.add("§7未装填胶卷");
            lore.add("§7照片编号：");
            lore.add("");
            lore.add("§f『你看，这小小的胶片就像一片会呼吸的画布——光与影的私语");
            lore.add("§f被封存其中，，等待显影时才揭晓答案，多浪漫呢？』");
            lore.add("§f『原理上是利用银盐感光…但你说的“浪漫”，是指它无法预览");
            lore.add("§f成像的不可控？』");
            lore.add("§f『不止哦~它的宽容度像黄昏的云霞，能吞下刺眼的高光...");
            lore.add("§f要试试吗♪』");
            lore.add("§f『…若是为了“美好回忆”，倒是可以浪费片刻光阴。』");
            lore.add("§6§lShift + 右键§r§6将拉片拉出");
            itemMeta.setItemModel(new NamespacedKey("famara", "famara_close"));
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            ItemSystem.itemMap.put("camera_filmed", itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("§f胶卷盒");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7这是一个装有胶卷的盒子，不可以见光，");
            lore.add("§7属于相机耗材，相机需要胶卷来拍摄");
            lore.add("");
            lore.add("§f『欸兄弟，你这盒子装的什么啊』");
            lore.add("§f『是胶卷，胶卷啦！』");
            lore.add("§f『嘻，让我打开来看看长啥样』");
            lore.add("§f『别！别！胶卷不可以见光哒！！！』");
            itemMeta.setLore(lore);
            itemMeta.setItemModel(new NamespacedKey("famara", "film_box"));
            itemStack.setItemMeta(itemMeta);
            ItemSystem.itemMap.put("filmBox", itemStack);
            RecipeBook.mainPage.items().put(1, itemStack);
            ArrayList<ItemStack> objects = new ArrayList<>();
            //按照上面的写配方，配方写为：青金石，萤石，绿宝石，火药，火药，火药，纸，纸，纸
            objects.add(new ItemStack(Material.LIGHT_BLUE_DYE));
            objects.add(new ItemStack(Material.GLOWSTONE_DUST));
            objects.add(new ItemStack(Material.GREEN_DYE));
            objects.add(new ItemStack(Material.GUNPOWDER));
            objects.add(new ItemStack(Material.GUNPOWDER));
            objects.add(new ItemStack(Material.GUNPOWDER));
            objects.add(new ItemStack(Material.PAPER));
            objects.add(new ItemStack(Material.PAPER));
            objects.add(new ItemStack(Material.PAPER));
            RecipeBook.mainPage.Recipes().put(1, objects);
        }
    }
}

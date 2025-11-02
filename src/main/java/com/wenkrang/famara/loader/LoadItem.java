package com.wenkrang.famara.loader;

import com.wenkrang.famara.itemSystem.ItemSystem;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ItemUtils;
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
            itemMeta.setDisplayName("§9§lFamara§f-相机配方");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7你有没有一些想记录下来的风景呢？");
            lore.add("§7快用相机拍下来吧！");
            lore.add("");
            lore.add("§6§l右键§6打开");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.put("recipeBook", itemStack);
            RecipeBook.RecipeBookItem = itemStack;
        }
        {
            ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
            @NotNull ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
            itemMeta.setDisplayName("§f照片");
            itemMeta = ItemUtils.setModelSafely(itemMeta, new NamespacedKey("famara", "photo"), 40);
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7一张看起来§7§l平平无奇的照片§7，");
            lore.add("§7也许你早已经忘却了照片上的§7§l人与事物§r，");
            lore.add("§7但它一直在为你记录着§7§l那一切§7......");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.put("photo", itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.FILLED_MAP);
            @NotNull ItemMeta itemMeta = Objects.requireNonNull(itemStack.getItemMeta());
            itemMeta = ItemUtils.setModelSafely(itemMeta, new NamespacedKey("famara", "photo"), 40);
            itemMeta.setDisplayName("§f照片（按右键撕开拉片）");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7一张看起来§7§l平平无奇的照片§7，");
            lore.add("§7也许你早已经忘却了照片上的§7§l人与事物，");
            lore.add("§7但它一直在为你记录着§7§l那一切§7......");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            ItemSystem.put("photo_unPull", itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.STICK);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta = ItemUtils.setModelSafely(itemMeta, new NamespacedKey("famara", "famara_close"), 20);
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
            lore.add("");
            lore.add("§9§l-> §r§f打开取景器前");
            lore.add("§6§l按f§r§6装填胶卷（将胶卷放在背包）");
            lore.add("§6§l右键§r§6打开取景器");
            lore.add("§9§l-> §r§f打开取景器后");
            lore.add("§6§l右键§r§6拍照");
            lore.add("§6§l按Shift§r§6关闭取景器");
            lore.add("§9§l-> §r§f拍摄后");
            lore.add("§6§lShift + 右键§r§6将拉片拉出");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            ItemSystem.put("camera", itemStack);
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
            itemMeta = ItemUtils.setModelSafely(itemMeta, new NamespacedKey("famara", "famara_close"), 20);
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
            lore.add("");
            lore.add("§9§l-> §r§f打开取景器前");
            lore.add("§6§l按f§r§6装填胶卷（将胶卷放在背包）");
            lore.add("§6§l右键§r§6打开取景器");
            lore.add("§9§l-> §r§f打开取景器后");
            lore.add("§6§l右键§r§6拍照");
            lore.add("§6§l按Shift§r§6关闭取景器");
            lore.add("§9§l-> §r§f拍摄后");
            lore.add("§6§lShift + 右键§r§6将拉片拉出");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            ItemSystem.put("camera_filmed", itemStack);
        }
        {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta = ItemUtils.setModelSafely(itemMeta, new NamespacedKey("famara", "film_box"), 30);
            itemMeta.setDisplayName("§f胶卷盒");
            ArrayList<String> lore = new ArrayList<>();
            lore.add("§7当手指抚过这卷泛黄的胶片，银盐颗粒在指腹间沙沙作响——");
            lore.add("§7这是属于旧时代的情书载体，每一格35mm画幅都暗藏时光的密语。");
            lore.add("");
            lore.add("§f『咦？你没见过胶卷吗？这可是记录颜色的魔法纸哦！』");
            lore.add("§f『（伸手触碰）这是...塑料膜？』");
            lore.add("§f『不对不对！要装进相机里，拍照冲洗后就能看到封存的风景啦！』");
            lore.add("§f『（突然撕开胶卷外盒）嘻，那我打开看看内部结构！』");
            lore.add("§f『等、等下！这会让胶卷彻底曝光的！（扑上去按住胶卷）我的胶卷啊！』");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            ItemSystem.put("filmBox", itemStack);
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

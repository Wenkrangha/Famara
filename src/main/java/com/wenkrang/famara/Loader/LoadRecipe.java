package com.wenkrang.famara.Loader;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.itemSystem.BookPage;
import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Map;

/**
 * LoadRecipe 类用于加载自定义配方到服务器中。
 * 它通过读取 RecipeBook 中的配方数据，构建并注册 ShapedRecipe。
 */
public class LoadRecipe {

    /**
     * 加载所有自定义配方到 Bukkit 服务器中。
     * 该方法会遍历 RecipeBook 中的配方数据，为每个配方创建一个 ShapedRecipe 实例，
     * 并将其注册到服务器中。
     *
     * 注意：该方法不会返回任何值，也不会抛出异常（异常被捕获但未处理）。
     */
    public static void loadRecipe() {
        // 获取主页面的配方数据
        BookPage mainPage = RecipeBook.mainPage;
        Map<Integer, ArrayList<ItemStack>> recipes = mainPage.Recipes();

        // 遍历所有配方并注册
        recipes.forEach((index, itemStacks) -> {
            // 创建带命名空间的配方对象
            ShapedRecipe shapedRecipe = new ShapedRecipe(
                    new NamespacedKey(Famara.getPlugin(Famara.class), String.valueOf(index)),
                    mainPage.items().get(index).clone()
            );

            // 设置配方形状为 3x3 网格
            shapedRecipe.shape("qwe", "rty", "uio");

            // 设置每个字符对应的材料（使用精确匹配）
            shapedRecipe.setIngredient('q', new RecipeChoice.ExactChoice(itemStacks.get(0).clone()));
            shapedRecipe.setIngredient('w', new RecipeChoice.ExactChoice(itemStacks.get(1).clone()));
            shapedRecipe.setIngredient('e', new RecipeChoice.ExactChoice(itemStacks.get(2).clone()));
            shapedRecipe.setIngredient('r', new RecipeChoice.ExactChoice(itemStacks.get(3).clone()));
            shapedRecipe.setIngredient('t', new RecipeChoice.ExactChoice(itemStacks.get(4).clone()));
            shapedRecipe.setIngredient('y', new RecipeChoice.ExactChoice(itemStacks.get(5).clone()));
            shapedRecipe.setIngredient('u', new RecipeChoice.ExactChoice(itemStacks.get(6).clone()));
            shapedRecipe.setIngredient('i', new RecipeChoice.ExactChoice(itemStacks.get(7).clone()));
            shapedRecipe.setIngredient('o', new RecipeChoice.ExactChoice(itemStacks.get(8).clone()));

            // 尝试将配方添加到服务器中
            try {
                Bukkit.getServer().addRecipe(shapedRecipe);
            }catch (Exception ignored) {
            }
        });
    }
}


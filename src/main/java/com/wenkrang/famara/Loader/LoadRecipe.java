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

public class LoadRecipe {
    public static void loadRecipe() {
        BookPage mainPage = RecipeBook.mainPage;
        Map<Integer, ArrayList<ItemStack>> recipes = mainPage.Recipes();
        recipes.forEach((index, itemStacks) -> {
            ShapedRecipe shapedRecipe = new ShapedRecipe(new NamespacedKey(Famara.getPlugin(Famara.class), String.valueOf(index)),mainPage.items().get(index))
                    .shape("qwe","rty","uio");
            shapedRecipe.setIngredient('q', new RecipeChoice.ExactChoice(itemStacks.get(0)));
            shapedRecipe.setIngredient('w', new RecipeChoice.ExactChoice(itemStacks.get(1)));
            shapedRecipe.setIngredient('e', new RecipeChoice.ExactChoice(itemStacks.get(2)));
            shapedRecipe.setIngredient('r', new RecipeChoice.ExactChoice(itemStacks.get(3)));
            shapedRecipe.setIngredient('t', new RecipeChoice.ExactChoice(itemStacks.get(4)));
            shapedRecipe.setIngredient('y', new RecipeChoice.ExactChoice(itemStacks.get(5)));
            shapedRecipe.setIngredient('u', new RecipeChoice.ExactChoice(itemStacks.get(6)));
            shapedRecipe.setIngredient('i', new RecipeChoice.ExactChoice(itemStacks.get(7)));
            shapedRecipe.setIngredient('o', new RecipeChoice.ExactChoice(itemStacks.get(8)));

            try {
                Bukkit.getServer().addRecipe(shapedRecipe);
            } catch (Exception e) {
            }
        });
    }
}

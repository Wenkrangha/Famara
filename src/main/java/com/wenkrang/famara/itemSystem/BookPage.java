package com.wenkrang.famara.itemSystem;

import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public record BookPage(String title, Map<Integer, ItemStack> items, Map<Integer, BookPage> References, Map<Integer, CraftingRecipe> Recipes) {

}

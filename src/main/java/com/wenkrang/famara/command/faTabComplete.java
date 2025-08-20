package com.wenkrang.famara.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class faTabComplete implements TabCompleter{
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<Material> itemStacks = Arrays.stream(Material.values()).toList();
        ArrayList<String> names = new ArrayList<>();
        itemStacks.forEach(i -> {
            names.add(i.name().toUpperCase());
        });

        return switch (strings.length) {
            case 1 -> Arrays.asList("help", "guide", "set", "speed");
            case 2 -> switch (strings[0].toLowerCase()) {
                case "set" -> names;
                case "speed" -> List.of("Integer");
                default -> Collections.emptyList();
            };
            case 3 -> switch (strings[0].toLowerCase()) {
                case "set" -> List.of("RED");
                default -> Collections.emptyList();
            };
            case 4 -> switch (strings[0].toLowerCase()) {
                case "set" -> List.of("GREEN");
                default -> Collections.emptyList();
            };
            case 5 -> switch (strings[0].toLowerCase()) {
                case "set" -> List.of("BLUE");
                default -> Collections.emptyList();
            };
            default -> Collections.emptyList();
        };
    }
}

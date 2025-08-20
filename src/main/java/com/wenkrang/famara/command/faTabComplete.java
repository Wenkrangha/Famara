package com.wenkrang.famara.command;

import com.wenkrang.famara.Famara;
import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class faTabComplete implements TabCompleter{
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player player) {
            player.playEffect(player.getLocation(), Effect.CLICK1, 1);
        }
        return switch (strings.length) {
            case 1 -> Arrays.asList("help", "guide", "set", "speed");
            case 2 -> switch (strings[0].toLowerCase()) {

                case "set" -> Famara.excludingBlocks;
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

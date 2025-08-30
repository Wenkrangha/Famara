package com.wenkrang.famara.Loader;

import com.google.common.collect.Range;
import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.command.CommandArgument;
import com.wenkrang.famara.lib.command.FaCommand;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ConsoleLogger;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.wenkrang.famara.Famara.*;
import static com.wenkrang.famara.Famara.excludingBlocks;
import static com.wenkrang.famara.Famara.yamlConfiguration;

public class LoadCommand {
    public static void registerCommands() {

        FaCommand.register(new FaCommand("help", new CommandArgument[]{},
                (i, j) -> FaCommand.getHelp(i)));

        FaCommand.register(new FaCommand("color", new CommandArgument[]{},
                (i, j) -> {
                    if (i.isOp()) ConsoleLogger.info("缺失方块颜色数量： " + excludingBlocks.size());
                }));

        FaCommand.register(new FaCommand("inv", new CommandArgument[]{},
                (i, j) -> {if (i.isOp() && i instanceof Player)
                    ((Player) i).openInventory(excludingBlocksInv);}));

        FaCommand.register(new FaCommand(
                "version", new CommandArgument[]{
                new CommandArgument.IntArgument("int", false)
        }, (i, j) -> {
            if (i.isOp()) {
                yamlConfiguration.set("version", Integer.parseInt(j.getFirst()));
                try {
                    yamlConfiguration.save("./plugins/Famara/colors.yml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ));

        FaCommand.register(new FaCommand(
                "set", new CommandArgument[]{
                new CommandArgument.WeakFixedArgument(excludingBlocks, false),
                new CommandArgument.IntArgument("RED", false),
                new CommandArgument.IntArgument("GREEN", false),
                new CommandArgument.IntArgument("BLUE", false)
        }, (i, j) -> {

            if (!i.isOp()) return;

            if (!Range.closed(0, 255)
                    .containsAll(Set.of(
                            Integer.parseInt(j.get(1)),
                            Integer.parseInt(j.get(2)),
                            Integer.parseInt(j.get(3))
                    ))) i.sendMessage(Translation.CURRENT.of("setError1"));

            yamlConfiguration.set(j.getFirst() + ".r", Integer.parseInt(j.get(1)));
            yamlConfiguration.set(j.getFirst() + ".g", Integer.parseInt(j.get(2)));
            yamlConfiguration.set(j.getFirst() + ".b", Integer.parseInt(j.get(3)));

            try {
                yamlConfiguration.save("./plugins/Famara/colors.yml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            i.sendMessage(Translation.CURRENT.of("setSuccessfully"));
        }
        ));

        FaCommand.register(new FaCommand(
                "speed", new CommandArgument[]{
                new CommandArgument.IntArgument("speed", false)
        }, (i, j) -> {
            if (!i.isOp()) return;

            Famara.speed = Integer.parseInt(j.getFirst());

            if (Famara.speed > 50)
                i.sendMessage(Translation.CURRENT.of("warning1"));

            i.sendMessage(String.format(
                    Translation.CURRENT.of("speedSetSuccessfully"), Famara.speed));
        }));

        FaCommand.register(new FaCommand(
                "resource", new CommandArgument[]{
                new CommandArgument.FixedArgument(List.of("china"), true)
        }, (i, j) -> {
            if (i instanceof Player) {
                LoadResourcePack.load(
                        (Player) i,
                        // boolean “&&” 符号如果前面为 false 会停止计算
                        !j.isEmpty() && "china".equals(j.getFirst())
                );
            } else {
                i.sendMessage(Translation.CURRENT.of("useInGame"));
            }
        }));

        FaCommand.register(new FaCommand(
                "guide", new CommandArgument[]{}, (i, j) -> {
            if (i instanceof Player player) {
                player.getWorld().dropItem(player.getLocation(), RecipeBook.RecipeBookItem);
            } else {
                i.sendMessage(Translation.CURRENT.of("useInGame"));
            }
        }));

        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("fa"))
                .setExecutor(FaCommand.exec);
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("fa"))
                .setTabCompleter(FaCommand.tabCompleter);

    }
}

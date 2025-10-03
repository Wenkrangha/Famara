package com.wenkrang.famara.Loader;

import com.google.common.collect.Range;
import com.wenkrang.famara.Famara;
import com.wenkrang.famara.command.CmdArgs;
import com.wenkrang.famara.command.FaCmd;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ConsoleLogger;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.wenkrang.famara.Famara.*;
import static com.wenkrang.famara.Famara.excludingBlocks;
import static com.wenkrang.famara.Famara.yamlConfiguration;

public class LoadCmd {
    public static void registerCommands() {

        FaCmd.register(new FaCmd("help", new CmdArgs[]{},
                (i, j) -> FaCmd.getHelp(i)));

        FaCmd.register(new FaCmd("color", new CmdArgs[]{},
                (i, j) -> {
                    if (i.isOp()) ConsoleLogger.info("缺失方块颜色数量： " + excludingBlocks.size());
                }));

        FaCmd.register(new FaCmd("inv", new CmdArgs[]{},
                (i, j) -> {if (i.isOp() && i instanceof Player)
                    ((Player) i).openInventory(excludingBlocksInv);}));

        FaCmd.register(new FaCmd(
                "version", new CmdArgs[]{
                new CmdArgs.IntArgument("int", false)
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

        FaCmd.register(new FaCmd(
                "set", new CmdArgs[]{
                new CmdArgs.WeakFixedArgument(excludingBlocks, false),
                new CmdArgs.IntArgument("RED", false),
                new CmdArgs.IntArgument("GREEN", false),
                new CmdArgs.IntArgument("BLUE", false)
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

        FaCmd.register(new FaCmd(
                "speed", new CmdArgs[]{
                new CmdArgs.IntArgument("speed", false)
        }, (i, j) -> {
            if (!i.isOp()) return;

            Famara.speed = Integer.parseInt(j.getFirst());

            if (Famara.speed > 50)
                i.sendMessage(Translation.CURRENT.of("warning1"));

            i.sendMessage(String.format(
                    Translation.CURRENT.of("speedSetSuccessfully"), Famara.speed));
        }));

        FaCmd.register(new FaCmd(
                "resource", new CmdArgs[]{
        }, (i, j) -> {
            if (i instanceof Player) {
                LoadResourcePack.load((Player) i);
            } else {
                i.sendMessage(Translation.CURRENT.of("useInGame"));
            }
        }));

        FaCmd.register(new FaCmd(
                "guide", new CmdArgs[]{}, (i, j) -> {
            if (i instanceof Player player) {
                player.getWorld().dropItem(player.getLocation(), RecipeBook.RecipeBookItem);
            } else {
                i.sendMessage(Translation.CURRENT.of("useInGame"));
            }
        }));


        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("fa"))
                .setExecutor(FaCmd.exec);
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("fa"))
                .setTabCompleter(FaCmd.tabCompleter);

    }
}

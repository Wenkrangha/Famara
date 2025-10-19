package com.wenkrang.famara.Loader;

import com.google.common.collect.Range;
import com.wenkrang.famara.Famara;
import com.wenkrang.famara.command.CmdArgs;
import com.wenkrang.famara.command.FaCmd;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ColorManager;
import com.wenkrang.famara.lib.ConsoleLogger;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static com.wenkrang.famara.Famara.*;

public class LoadCmd {
    /**
     * 注册命令触发器至Spigot
     */
    public static void spigotRegister() {
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("fa"))
                .setExecutor(FaCmd.exec);
        Objects.requireNonNull(Bukkit.getServer().getPluginCommand("fa"))
                .setTabCompleter(FaCmd.tabCompleter);
    }

    public static void registerCommands() {

        FaCmd.register(new FaCmd("help", new CmdArgs[]{},
                (i, j) -> FaCmd.getHelp(i)));

        FaCmd.register(new FaCmd("color", new CmdArgs[]{},
                (i, j) -> {
                    if (i.isOp()) ConsoleLogger.info("缺失方块颜色数量： " + ColorManager.excludingBlocks.size());
                }));

        FaCmd.register(new FaCmd("inv", new CmdArgs[]{},
                (i, j) -> {if (i.isOp() && i instanceof Player)
                    ((Player) i).openInventory(excludingBlocksInv);}));

        FaCmd.register(new FaCmd(
                "set", new CmdArgs[]{
                new CmdArgs.WeakFixedArgument(ColorManager.excludingBlocks, false),
                new CmdArgs.IntArgument("RED", false),
                new CmdArgs.IntArgument("GREEN", false),
                new CmdArgs.IntArgument("BLUE", false)
        }, (i, j) -> {

            if (!i.isOp()) return;

            try {
                ColorManager.setColor(j.get(0), j.get(1),j.get(2),j.get(3));
            }catch (NullPointerException e) {
                i.sendMessage(Translation.CURRENT.of("setError1"));
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
        FaCmd.register(new FaCmd(
                        "step", new CmdArgs[]{
                        new CmdArgs.IntArgument("RED", false),
                        new CmdArgs.IntArgument("GREEN", false),
                        new CmdArgs.IntArgument("BLUE", false)
                }, (i, j) -> {
                    if (!i.isOp()) return;
                    try {
                        ColorManager.setColor(ColorManager.excludingBlocks.get(0), j.get(0), j.get(1), j.get(2));
                        ConsoleLogger.info("已设置颜色：" + ColorManager.excludingBlocks.get(0));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
//                    }catch (NullPointerException e) {
//                        i.sendMessage(Translation.CURRENT.of("setError1"));
//                    }
                })
        );

        spigotRegister();

    }
}

package com.wenkrang.famara.command;

import com.google.common.collect.Range;
import com.wenkrang.famara.Famara;
import com.wenkrang.famara.Loader.LoadResourcePack;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ConsoleLogger;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


import static com.wenkrang.famara.Famara.*;
import static com.wenkrang.famara.command.FaCommand.getHelp;

public class fa implements CommandExecutor {

    /**
     * 获取图片中出现最多的颜色
     * @param imageFile PNG图片文件
     * @return 出现最多的颜色
     * @throws IOException 当读取图片文件失败时抛出
     */
    private Color getMostFrequentColor(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        Map<Integer, Integer> colorCountMap = new HashMap<>();

        // 遍历图片的每个像素
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                colorCountMap.put(rgb, colorCountMap.getOrDefault(rgb, 0) + 1);
            }
        }

        // 找出出现次数最多的颜色
        int maxCount = 0;
        int mostFrequentRGB = 0;
        for (Map.Entry<Integer, Integer> entry : colorCountMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentRGB = entry.getKey();
            }
        }

        return new Color(mostFrequentRGB);
    }

    @Deprecated(forRemoval = true)
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,@NotNull Command command,@NotNull String s, String[] strings) {
        //Famara命令处理

        boolean help = true;
        try {
            if (strings[0].equalsIgnoreCase("help")) {
                getHelp(commandSender);
                help = false;
            }
            if (commandSender.isOp()) {
                if (strings[0].equalsIgnoreCase("color")) {
                    ConsoleLogger.info("缺失方块颜色数量： " + (long) excludingBlocks.size());
                    help = false;
                }
                if (strings[0].equalsIgnoreCase("inv")) {
                    if (commandSender instanceof Player player) {
                        player.openInventory(excludingBlocksInv);
                        help = false;
                    }
                }
                if (strings[0].equalsIgnoreCase("version")) {
                    yamlConfiguration.set("version", Integer.parseInt(strings[1]));
                    try {
                        yamlConfiguration.save("./plugins/Famara/colors.yml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (strings[0].equalsIgnoreCase("set")) {
                    try {
                        if (strings.length < 5) {
                            commandSender.sendMessage(Translation.CURRENT.of("lostArguments"));
                            return true;
                        }

                        if (Range.closed(0, 255)
                                .containsAll(Set.of(
                                        Integer.parseInt(strings[2]),
                                        Integer.parseInt(strings[3]),
                                        Integer.parseInt(strings[4])
                                ))) {
                            commandSender.sendMessage(Translation.CURRENT.of("setError1"));
                            return true;
                        }
                        yamlConfiguration.set(strings[1] + ".r", Integer.parseInt(strings[2]));
                        yamlConfiguration.set(strings[1] + ".g", Integer.parseInt(strings[3]));
                        yamlConfiguration.set(strings[1] + ".b", Integer.parseInt(strings[4]));
                    }catch (NumberFormatException e){
                        commandSender.sendMessage(Translation.CURRENT.of("setError2"));
                        return true;
                    }

                    try {
                        yamlConfiguration.save("./plugins/Famara/colors.yml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    commandSender.sendMessage(Translation.CURRENT.of("setSuccessfully"));
                    help = false;
                }

                if (strings[0].equalsIgnoreCase("speed")) {
                    try {
                        if (Famara.speed > 50) {
                            commandSender.sendMessage(Translation.CURRENT.of("warning1"));
                        }
                        Famara.speed = Integer.parseInt(strings[1]);
                    }catch (NumberFormatException e) {
                        commandSender.sendMessage(Translation.CURRENT.of("warning2"));
                        return true;
                    }

                    commandSender.sendMessage(Translation.CURRENT.of("speedSetSuccessfully") + Famara.speed);
                    help = false;
                }
            }

            //检测对象是否为玩家
            if (strings[0].equalsIgnoreCase("resource")) {
                if (commandSender instanceof Player player) {
                    if (strings.length > 2) {
                        if (strings[1].equalsIgnoreCase("china")){
                            LoadResourcePack.load(player, true);
                            return true;
                        }
                        LoadResourcePack.load(player, false);
                    }
                    help = false;
                } else {
                    commandSender.sendMessage(Translation.CURRENT.of("useInGame"));
                    help = false;
                }
            }
            if (strings[0].equalsIgnoreCase("guide")) {
                if (commandSender instanceof Player player) {
                    player.getWorld().dropItem(player.getLocation(), RecipeBook.RecipeBookItem);
                    help = false;
                } else {
                    commandSender.sendMessage(Translation.CURRENT.of("useInGame"));
                    help = false;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
//            getHelp(commandSender);
            help = false;
        }

        if (help) {
            getHelp(commandSender);
        }
        return true;
    }

    public static void registerNewCommand() {

        FaCommand.register(new FaCommand("help", new CommandArgument[]{},
                (i, j) -> getHelp(i)));

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

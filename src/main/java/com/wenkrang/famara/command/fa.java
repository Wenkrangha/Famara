package com.wenkrang.famara.command;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.Loader.LoadResourcePack;
import com.wenkrang.famara.itemSystem.RecipeBook;
import com.wenkrang.famara.lib.ConsoleLoger;
import com.wenkrang.famara.lib.text;
import org.apache.commons.lang3.Range;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,@NotNull Command command,@NotNull String s, String[] strings) {
        //Famara命令处理
        //TODO:改为Switch处理
        boolean help = true;
        if (strings.length == 0) {
            getHelp(commandSender);
            return true;
        }
        try {
            if (strings[0].equalsIgnoreCase("help")) {
                getHelp(commandSender);
                help = false;
            }
            if (commandSender.isOp()) {
                if (strings[0].equalsIgnoreCase("color")) {
                    ConsoleLoger.info("缺失方块颜色数量： " + (long) excludingBlocks.size());
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
                            commandSender.sendMessage(text.get("lostArguments"));
                            return true;
                        }

                        Range<Integer> range = Range.of(0, 255);
                        if (!range.contains(Integer.parseInt(strings[2])) ||
                                !range.contains(Integer.parseInt(strings[3])) ||
                                !range.contains(Integer.parseInt(strings[4]))) {
                            commandSender.sendMessage(text.get("setError1"));
                            return true;
                        }
                        yamlConfiguration.set(strings[1] + ".r", Integer.parseInt(strings[2]));
                        yamlConfiguration.set(strings[1] + ".g", Integer.parseInt(strings[3]));
                        yamlConfiguration.set(strings[1] + ".b", Integer.parseInt(strings[4]));
                    }catch (NumberFormatException e){
                        commandSender.sendMessage(text.get("setError2"));
                        return true;
                    }

                    try {
                        yamlConfiguration.save("./plugins/Famara/colors.yml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    commandSender.sendMessage(text.get("setSuccessfully"));
                    help = false;
                }

                if (strings[0].equalsIgnoreCase("speed")) {
                    try {
                        if (Famara.speed > 50) {
                            commandSender.sendMessage(text.get("warning1"));
                        }
                        Famara.speed = Integer.parseInt(strings[1]);
                    }catch (NumberFormatException e) {
                        commandSender.sendMessage(text.get("warning2"));
                        return true;
                    }

                    commandSender.sendMessage(text.get("speedSetSuccessfully") + Famara.speed);
                    help = false;
                }
            }

            //检测对象是否为玩家
            if (strings[0].equalsIgnoreCase("resource")) {
                if (commandSender instanceof Player player) {
                    if (strings.length >= 2) {
                        if (strings[1].equalsIgnoreCase("china")){
                            player.addScoreboardTag("FromChina");
                            LoadResourcePack.load(player, true);
                            return true;
                        }
                        LoadResourcePack.load(player, false);
                    }
                } else {
                    commandSender.sendMessage(text.get("useInGame"));
                }
                help = false;
            }
            if (strings[0].equalsIgnoreCase("guide")) {
                if (commandSender instanceof Player player) {
                    player.getWorld().dropItem(player.getLocation(), RecipeBook.RecipeBookItem);
                    help = false;
                } else {
                    commandSender.sendMessage(text.get("useInGame"));
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

    private static void getHelp(@NotNull CommandSender commandSender) {
        commandSender.sendMessage(text.get("help1"));
        commandSender.sendMessage(text.get("help2"));
        commandSender.sendMessage(text.get("help3"));
        commandSender.sendMessage(text.get("help4"));
        commandSender.sendMessage(text.get("help5"));
        commandSender.sendMessage(text.get("help6"));
        commandSender.sendMessage(text.get("help7"));
    }
}

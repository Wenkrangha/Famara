package com.wenkrang.famara.command;

import com.wenkrang.famara.Famara;
import com.wenkrang.famara.itemSystem.RecipeBook;
import org.bukkit.Material;
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

import static com.wenkrang.famara.Famara.excludingBlocks;
import static com.wenkrang.famara.Famara.yamlConfiguration;

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

        boolean help = true;
        try {
            if (strings[0].equalsIgnoreCase("help")) {
                getHelp(commandSender);
                help = false;
            }
            if (commandSender.isOp()) {
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
                            commandSender.sendMessage("§c§l[-]§r 参数缺少，请根据命令补全来填写");
                            return true;
                        }
                        if (Integer.parseInt(strings[2]) > 255 | Integer.parseInt(strings[3]) > 255 | Integer.parseInt(strings[4]) > 255
                        | Integer.parseInt(strings[2]) < 0 | Integer.parseInt(strings[3]) < 0 | Integer.parseInt(strings[4]) < 0) {
                            commandSender.sendMessage("§c§l[-]§r \"RED\",\"BLUE\",\"GREEN\"必须在0 ~ 255之间");
                            return true;
                        }
                        yamlConfiguration.set(strings[1] + ".r", Integer.parseInt(strings[2]));
                        yamlConfiguration.set(strings[1] + ".g", Integer.parseInt(strings[3]));
                        yamlConfiguration.set(strings[1] + ".b", Integer.parseInt(strings[4]));
                    }catch (NumberFormatException e){
                        commandSender.sendMessage("§c§l[-]§r \"RED\",\"BLUE\",\"GREEN\"必须为数字");
                        return true;
                    }

                    try {
                        yamlConfiguration.save("./plugins/Famara/colors.yml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    commandSender.sendMessage("§9§l[*]§r 方块颜色设置成功");
                    //刷新列表
                    List<Material> itemStacks = new ArrayList<>(Arrays.stream(Material.values()).toList());
                    itemStacks.removeIf(i -> !i.isBlock());
                    itemStacks.forEach(i -> excludingBlocks.add(i.name().toUpperCase()));
                    excludingBlocks.removeIf(Famara.yamlConfiguration::contains);
                    help = false;
                }

                if (strings[0].equalsIgnoreCase("speed")) {
                    try {
                        if (Famara.speed > 50) {
                            commandSender.sendMessage("§e§l[!]§r Integer过大可能会导致服务器卡顿!!!");
                        }
                        Famara.speed = Integer.parseInt(strings[1]);
                    }catch (NumberFormatException e) {
                        commandSender.sendMessage("§c§l[-]§r Integer必须为数字");
                        return true;
                    }

                    commandSender.sendMessage("§9§l[*]§r 当前渲染速度设置为" + Famara.speed);
                    help = false;
                }
            }

            //检测对象是否为玩家

            if (strings[0].equalsIgnoreCase("guide")) {
                if (commandSender instanceof Player player) {
                    player.getWorld().dropItem(player.getLocation(), RecipeBook.RecipeBookItem);
                    help = false;
                } else {
                    commandSender.sendMessage("§c§l[-]§r 请在游戏中使用该命令");
                    help = false;
                }
            }


        } catch (Exception e) {
            getHelp(commandSender);
            help = false;
        }

        if (help) {
            getHelp(commandSender);
        }
        return true;
    }

    private static void getHelp(@NotNull CommandSender commandSender) {
        commandSender.sendMessage("§7[!]  §4寄枪 - FakeGun §7正在运行");
        commandSender.sendMessage(" §4| §7help  帮助列表");
        commandSender.sendMessage(" §4| §7speed  设置渲染速度（默认为7）");
        commandSender.sendMessage(" §4| §7set 设置物品对应颜色");
        commandSender.sendMessage(" §4| §7guide  获取指南");
        commandSender.sendMessage(" §4| §7- 创造下，右键配方可以将直接获取物品");
    }
}

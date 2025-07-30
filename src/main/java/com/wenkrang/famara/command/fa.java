package com.wenkrang.famara.command;

import com.wenkrang.famara.lib.FaPhotoRender;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        //Famara命令处理

        //检测对象是否为玩家
        if (commandSender instanceof Player) {
            //防止重复获取
            Player player = (Player) commandSender;

            //供测试使用的命令
            if (strings[0].equalsIgnoreCase("test")) {
                FaPhotoRender.TakePhoto(player);
            }

            if (strings[0].equalsIgnoreCase("color")) {
                YamlConfiguration colors = new YamlConfiguration();

                File textures = new File("./textures/");
                List<String> list = Arrays.stream(textures.list()).toList();

                Material[] values = Material.values();

                String name = Arrays.stream(values).filter(Material::isBlock).findFirst().get().name().toLowerCase();
                player.sendMessage(name);

                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("./exclude.yml"));

                    for (Material material : values) {
                        if (material.isBlock()) {
                            if (list.contains(material.name().toLowerCase() + ".png")) {
                                File file = new File("./textures/" + material.name().toLowerCase() + ".png");
                                try {
                                    Color mostFrequentColor = getMostFrequentColor(file);

                                    player.sendMessage(material.name() + ": " + mostFrequentColor);


                                    colors.set(material.name() + ".r", mostFrequentColor.getRed());
                                    colors.set(material.name() + ".g", mostFrequentColor.getGreen());
                                    colors.set(material.name() + ".b", mostFrequentColor.getBlue());

//                                    colors.set(material.name() + ".include", true);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }else {
                                bufferedWriter.write(material.name() + "\n");
                            }
                        }
                    }

                    bufferedWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    colors.save("./plugins/Famara/colors.yml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }



        return true;
    }
}

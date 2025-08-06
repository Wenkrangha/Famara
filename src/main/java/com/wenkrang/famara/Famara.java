package com.wenkrang.famara;

import com.wenkrang.famara.command.fa;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapCanvas;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Famara extends JavaPlugin {


    public static Map<UUID, Integer> progress = new HashMap<>();

    public static YamlConfiguration yamlConfiguration = new YamlConfiguration();
    @Override
    public void onEnable() {
        // Plugin startup logic
        //注册命令
        this.getCommand("fa").setExecutor(new fa());

        try {
            yamlConfiguration.load("./plugins/Famara/colors.yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        //初始化照片文件夹
        File pictureDir = new File("./plugins/Famara/pictures");
        if (!pictureDir.exists()) {
            pictureDir.mkdirs();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

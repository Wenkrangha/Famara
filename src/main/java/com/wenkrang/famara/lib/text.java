package com.wenkrang.famara.lib;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class text {
    public static YamlConfiguration config = null;
    public static String get(String key){
        return config.getString(key);
    }
}

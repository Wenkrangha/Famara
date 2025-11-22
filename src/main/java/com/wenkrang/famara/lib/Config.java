package com.wenkrang.famara.lib;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Config(YamlConfiguration ref) {
    public static Config CURRENT = new Config(null);
    @NotNull
    public String of(@NotNull String key) {
        if (Objects.isNull(ref)) {
            return key;
        } else {
            return Objects.requireNonNullElse(ref.getString(key), key);
        }
    }

    public static void setCurrent(YamlConfiguration ref) {
        CURRENT = new Config(ref);
    }
}

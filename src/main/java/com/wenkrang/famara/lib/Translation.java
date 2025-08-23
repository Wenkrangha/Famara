package com.wenkrang.famara.lib;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Translation {
    public static Translation CURRENT = new Translation(null);
    private final YamlConfiguration ref;

    public Translation(@Nullable YamlConfiguration ref) {
        this.ref = ref;
    }

    @NotNull
    public String of(@NotNull String key) {
        if (Objects.isNull(ref)) {
            return key;
        } else {
            return Objects.requireNonNullElse(ref.getString(key), key);
        }
    }

    public static void setCurrent(YamlConfiguration ref) {
        CURRENT = new Translation(ref);
    }
}

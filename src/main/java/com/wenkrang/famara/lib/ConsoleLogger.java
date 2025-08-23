package com.wenkrang.famara.lib;

import static org.bukkit.Bukkit.getServer;

public class ConsoleLogger {
    public static void info(String msg) {getServer().getConsoleSender().sendMessage(SpigotConsoleColors.BLUE + "[*] " + SpigotConsoleColors.RESET + msg);}
    public static void error(String msg) {getServer().getConsoleSender().sendMessage(SpigotConsoleColors.RED + "[-] " + SpigotConsoleColors.RESET + msg);}
}

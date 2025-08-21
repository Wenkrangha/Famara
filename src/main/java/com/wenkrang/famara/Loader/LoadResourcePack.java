package com.wenkrang.famara.Loader;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.wenkrang.famara.Famara;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;

public class LoadResourcePack {
    private static DatabaseReader reader;
    public static String getOutIP() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

            String ip = in.readLine();
            return ip;

        } catch (Exception e) {}

        return "";
    }

    public LoadResourcePack(String geoipDatabasePath) throws Exception {
        File database = new File(geoipDatabasePath);
        reader = new DatabaseReader.Builder(database).build();
    }
    public static boolean isIPFromChina(String ipAddress) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CountryResponse response = reader.country(inetAddress);
            String countryCode = response.getCountry().getIsoCode();
            return "CN".equals(countryCode);
        } catch (Exception e) {
            return false;
        }
    }
    public static void load(Player player, boolean isFromChina) {
        if (player.getScoreboardTags().contains("FamaraResPackIncluded")){
            return;
        }
        player.sendMessage("§9§l[*]§r 正在下载材质包");
        byte[] hash;
        String hashString = "51214ab019075b983f5689a0cc87be2577efbdae";
        hash = new BigInteger(hashString, 16).toByteArray();
        if (isIPFromChina(getOutIP()) | isFromChina) {
            player.addResourcePack(Famara.resPack, "https://gitee.com/wenkrang/Famara/raw/master/famara_textures.zip", hash, "Famara十分建议推荐搭配材质包使用，如果您不添加，使用将十分糟糕！！！", false);
            player.sendMessage("§9§l[*]§r 正在从中国镜像下载材质包");
        }else {
            player.addResourcePack(Famara.resPack, "https://github.com/Wenkrangha/Famara/raw/refs/heads/master/famara_textures.zip", hash, "Famara十分建议推荐搭配材质包使用，如果您不添加，使用将十分糟糕！！！", false);
        }

    }
}

package com.wenkrang.famara.Loader;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.wenkrang.famara.Famara;
import com.wenkrang.famara.lib.Translation;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;

public class LoadResourcePack {
    private static DatabaseReader reader;
    public static String getOutIP() {
        try {
            URL whatismyip = URI.create("http://checkip.amazonaws.com").toURL();
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
    public static byte[] stringToHash(String string) {
        return new BigInteger(string, 16).toByteArray();
    }
    public static void load(Player player) {
        String mirrorURL = "https://gitee.com/wenkrang/Famara/raw/master/famara_compatible_textures.zip";
        if (player.getScoreboardTags().contains("FamaraResPackIncluded")){
            return;
        }
        player.sendMessage("§9§l[*]§r 正在下载材质包");
        byte[] hash = stringToHash("d8148fb023c11a5d2e11696a9ad6bf1d4acae94c");
        if (player.getScoreboardTags().contains("FromChina") || isIPFromChina(getOutIP()) || player.getLocale().equalsIgnoreCase("zh_cn")) {
            player.addResourcePack(Famara.resPack, mirrorURL, hash, Translation.CURRENT.of("resourceInformation"), false);
            player.sendMessage("§9§l[*]§r 正在从中国镜像下载材质包");
        } else {
            //https://github.com/Wenkrangha/Famara/raw/refs/heads/master/famara_textures.zip
            //目前只在minebbs发布该插件，因此直接使用gitee
            player.addResourcePack(Famara.resPack, mirrorURL, hash, Translation.CURRENT.of("resourceInformation"), false);
        }

    }
}

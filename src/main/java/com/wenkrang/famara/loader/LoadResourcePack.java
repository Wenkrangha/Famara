package com.wenkrang.famara.loader;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.wenkrang.famara.lib.Config;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;

/**
 * 资源包加载器类，用于根据玩家所在地区自动选择合适的资源包下载地址。
 */
public class LoadResourcePack {
    private static DatabaseReader reader;

    /**
     * 获取当前服务器的公网 IP 地址。
     *
     * @return 返回获取到的公网 IP 字符串；若发生异常则返回空字符串。
     */
    public static String getOutIP() {
        try {
            URL whatismyip = URI.create("http://checkip.amazonaws.com").toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

            String ip = in.readLine();
            return ip;

        } catch (Exception e) {}

        return "";
    }

    /**
     * 构造方法，初始化 GeoIP 数据库读取器。
     *
     * @param geoipDatabasePath GeoIP 数据库文件路径
     * @throws Exception 初始化过程中可能抛出的异常
     */
    public LoadResourcePack(String geoipDatabasePath) throws Exception {
        File database = new File(geoipDatabasePath);
        reader = new DatabaseReader.Builder(database).build();
    }

    /**
     * 判断指定 IP 是否来自中国。
     *
     * @param ipAddress 需要判断的 IP 地址
     * @return 如果是来自中国返回 true，否则返回 false
     */
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

    /**
     * 将十六进制字符串转换为字节数组（SHA1 哈希值）。
     *
     * @param hex 输入的十六进制字符串
     * @return 对应的字节数组
     * @throws IllegalArgumentException 当输入字符串格式非法时抛出此异常
     */
    public static byte[] stringToHash(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length: " + hex);
        }
        byte[] data = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            int high = Character.digit(hex.charAt(i), 16);
            int low = Character.digit(hex.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character in: " + hex);
            }
            data[i / 2] = (byte) ((high << 4) | low);
        }
        return data;
    }

    /**
     * 根据玩家信息为其设置并加载对应的资源包。
     * 若玩家已包含特定标签，则跳过加载过程。
     * 同时会检测玩家是否来自中国大陆，并据此决定使用哪个镜像地址进行下载。
     *
     * @param player 目标玩家对象
     */
    public static void load(Player player) {
        // 检查是否强制使用资源包
        if (!Boolean.parseBoolean(Config.CURRENT.of("force-resource"))) return;

        String mirrorURL = "https://gitee.com/wenkrang/Famara/raw/master/famara_compatible_textures.zip";

        // 检查玩家是否已经包含了资源包标记
        if (player.getScoreboardTags().contains("FamaraResPackIncluded")){
            return;
        }

        player.sendMessage("§9§l[*]§r 正在下载材质包，如果下载失败，请使用/fa resource下载");

        byte[] hash = stringToHash("d8148fb023c11a5d2e11696a9ad6bf1d4acae94c");

        // 判断是否应该使用中国镜像：如果玩家有 FromChina 标签、IP 来自中国或语言环境为中国大陆
        if (player.getScoreboardTags().contains("FromChina") || isIPFromChina(getOutIP()) || player.getLocale().equalsIgnoreCase("zh_cn")) {
            player.setResourcePack(mirrorURL, hash);
            player.sendMessage("§9§l[*]§r 正在从中国镜像下载材质包");
        } else {
            // 使用默认镜像（目前与国内一致）
            player.setResourcePack(mirrorURL, hash);
        }
    }
}

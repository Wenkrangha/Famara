package com.wenkrang.famara.lib;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * 不安全的文件下载器类
 * 该类提供了一个可以绕过SSL证书验证的HTTPS文件下载功能
 * 注意：由于绕过了SSL验证，存在安全风险，仅应用于测试环境或特殊场景
 */
public class UnsafeDownloader {
    /**
     * 从指定URL下载文件并保存到目标位置
     * 该方法会绕过SSL证书验证，允许下载自签名证书或无效证书的HTTPS资源
     *
     * @param urlString 文件下载地址URL字符串
     * @param destinationFile 目标文件对象，指定文件保存的位置
     * @throws Exception 当网络连接失败、IO操作异常或其他错误时抛出
     */
    public static void downloadFile(String urlString, File destinationFile) throws Exception {
        // 创建URL对象
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // 绕过SSL验证
        try {
            connection.setHostnameVerifier((hostname, session) -> true);
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, new javax.net.ssl.TrustManager[]{new MyTrustManager()}, null);
            connection.setSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 执行文件下载，将网络流数据写入本地文件
        InputStream in = connection.getInputStream();
        FileOutputStream out = new FileOutputStream(destinationFile);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        in.close();
        out.close();
    }

    /**
     * 自定义信任管理器实现类
     * 该类通过接受所有证书来绕过SSL证书验证
     */
    public static class MyTrustManager implements javax.net.ssl.X509TrustManager {
        /**
         * 获取可接受的证书颁发机构列表
         *
         * @return 返回null表示接受所有CA证书
         */
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        /**
         * 检查客户端证书是否可信
         *
         * @param certs 客户端证书数组
         * @param authType 认证类型
         */
        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        /**
         * 检查服务器端证书是否可信
         *
         * @param certs 服务器端证书数组
         * @param authType 认证类型
         */
        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
    }
}


package com.wenkrang.famara.lib;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

public class UnsafeDownloader {
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

    public static class MyTrustManager implements javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
        }
    }
}

package com.may.routeplansystem.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetWorkUtil {

    /**
     * 用于访问网址的工具方法
     * @param urlStr
     * @return
     * @throws IOException
     */
    public static String visitUrl(String urlStr) throws IOException {
        StringBuilder result = new StringBuilder();
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.connect();

            if (connection.getResponseCode() == 200) {
                if (connection.getContentLength() > 0) {
                    BufferedInputStream bufferedInputStream =
                            new BufferedInputStream(connection.getInputStream());
                    int len;
                    byte[] bytes = new byte[1024];

                    while ((len = bufferedInputStream.read(bytes)) > 0) {
                        String str = new String(bytes, 0, len);
                        result.append(str);
                    }
                }
            }
        return result.toString();
    }
}

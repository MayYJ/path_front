package com.may.routeplansystem.util;

import java.io.IOException;
import java.util.Objects;

public class BaiduMapApiUtil {

    /**
     * 得到两个点之间的距离时间的json数据
     * @param startAddressLat 起始地点纬度
     * @param startAddressLng 起始地点经度
     * @param endAddressLat 终点纬度
     * @param endAddressLng 终点经度
     * @param ak 百度API秘钥
     * @return
     * @throws IOException
     */
    public static String getJsonOfTwoNode(double startAddressLat, double startAddressLng,
                                          double endAddressLat, double endAddressLng, String ak) throws IOException {
        Objects.requireNonNull(ak);

        String sb = "http://api.map.baidu.com/routematrix/v2/driving?output=json&origins=" + startAddressLat +
                "," +
                startAddressLng +
                "&destinations=" +
                endAddressLat +
                "," +
                endAddressLng +
                "&ak=" +
                ak;
        return NetWorkUtil.visitUrl(sb);
    }

    /**
     * 得到一个地点的经纬度的json数据
     * @param address 地址
     * @param ak 百度应用秘钥
     * @return
     * @throws IOException
     */
    public static String getJsonOfOneNode(String address, String ak) throws IOException {
        Objects.requireNonNull(address);
        Objects.requireNonNull(ak);
        String sb = "http://api.map.baidu.com/geocoder/v2/?address=" + address +
                "&output=json&ak=" +
                ak;
        return NetWorkUtil.visitUrl(sb);
    }
}

package com.lenwotion.travel.global;

import com.amap.api.location.AMapLocation;
import com.lenwotion.travel.bean.bybus.StationInfoBean;

import java.util.ArrayList;

/**
 * 全局变量
 */
public class GlobalVariables {

    /**
     * 是否工厂测试版本
     */
    public static boolean IS_TEST_FACTORY = false;
    /**
     * 是否泰国版演示测试版本
     */
    public static boolean IS_TEST_SHOW = false;
    /**
     * 是否GPS测试版本
     */
    public static boolean IS_TEST_GPS = false;

    public static String WIFI_AP_SSID = "network";
    public static String WIFI_AP_PASSWORD = "sgy123456";
    /**
     * AMapLocation 高德定位结果实体类
     */
    public static AMapLocation A_MAP_LOCATION;
    /**
     * 选择站台实体类
     */
    public static StationInfoBean STATION_INFO_BEAN;
    /**
     * 路线名集合
     */
    public static ArrayList<String> LINE_NAME_LIST = new ArrayList<>();

    // 深圳
    // 坪洲地铁站      22.569537 113.869958
    // 海滨新村        22.564416 113.869711

    // 定南
    // 太湖            24.756991 115.043160
    // 大圆盘          24.773491 115.036656
    // 行政中心        24.783588 115.026950
    // 国土局          24.773734 115.035257
    // 职业中专        24.775791 115.045812

    public static String TEST_LAT = "0";
    public static String TEST_LNG = "0";

}

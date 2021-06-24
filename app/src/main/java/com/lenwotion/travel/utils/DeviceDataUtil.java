//package com.lenwotion.travel.utils;
//
//import com.google.gson.Gson;
//import com.lenwotion.travel.bean.NettyCommunicationBean;
//import com.lenwotion.travel.global.GlobalConstant;
//
///**
// * 设备（车载机）数据处理工具类
// * Created by John on 2017/8/25.
// */
//
//public class DeviceDataUtil {
//
//    /**
//     * 单例模式实例
//     */
//    private static DeviceDataUtil instance;
//
//    private DeviceDataUtil() {
//
//    }
//
//    /**
//     * 获取实例
//     */
//    public static synchronized DeviceDataUtil getInstance() {
//        if (instance == null) {
//            instance = new DeviceDataUtil();
//        }
//        return instance;
//    }
//
//    public String getLineBroadcastData() {
//        NettyCommunicationBean bean = new NettyCommunicationBean();
//        bean.setUserToken("");
//        bean.setOperationCode(GlobalConstant.ORDER_LINE_BROADCAST);
//        bean.setOperationData("");
//        String order = new Gson().toJson(bean);
//        return order;
//    }
//
//}

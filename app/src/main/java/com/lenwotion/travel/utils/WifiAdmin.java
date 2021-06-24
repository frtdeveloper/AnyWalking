package com.lenwotion.travel.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.lenwotion.travel.global.GlobalConstants;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Wifi 工具类
 * 封装了Wifi的基础操作方法，方便获取Wifi连接信息以及操作Wifi
 */
public class WifiAdmin {

    // wifi 操作事件
//    /**
//     * 连接wifi
//     */
//    public static final int WIFI_CONNECT = 0;
//    /**
//     * 其他wifi事件
//     */
//    public static final int WIFI_EVENT = 1;
//    /**
//     * Wi-Fi AP 获取状态未知
//     */
//    private static final int WIFI_AP_STATE_UNKNOWN = -1;
//    /**
//     * Wi-Fi AP 目前正在被禁用
//     */
//    public static final int WIFI_AP_STATE_DISABLING = 10;
//    /**
//     * Wi-Fi AP 禁用.
//     */
//    public static final int WIFI_AP_STATE_DISABLED = 11;
//    /**
//     * Wi-Fi AP 目前正在启用
//     */
//    public static final int WIFI_AP_STATE_ENABLING = 12;
//    /**
//     * Wi-Fi AP 启用
//     */
//    public static final int WIFI_AP_STATE_ENABLED = 13;
//    /**
//     * Wi-Fi AP 获取状态失败
//     */
//    public static final int WIFI_AP_STATE_FAILED = 14;
    /**
     * wifi相关信息
     */
    private static WifiInfo mWifiInfo;
    /**
     * WifiManager 对象
     */
    private static WifiManager mWifiManager;
    /**
     * 扫描出的网络连接列表
     */
    private List<ScanResult> mWifiList;

    public WifiAdmin(Context context) {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
    }

    /**
     * 根据ssid获取wifi配置
     */
    private WifiConfiguration getWifiConfigurationWithSsid(String ssid) {
        List<WifiConfiguration> wifiConfigurationList = mWifiManager.getConfiguredNetworks();
        if (wifiConfigurationList != null && !wifiConfigurationList.isEmpty()) {
            for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
                if (wifiConfiguration.SSID.equals("\"" + ssid + "\"")) {
                    return wifiConfiguration;
                }
            }
        }
        return null;
    }

    /**
     * 建立WIFI配置
     */
    private WifiConfiguration createWifiConfig(String ssid, String password, boolean isApServer) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        // 此配置支持的身份验证协议集。
        wifiConfiguration.allowedAuthAlgorithms.clear();
        // 分组密码支持的配置设置。
        wifiConfiguration.allowedGroupCiphers.clear();
        // 此配置支持的密钥管理协议集。
        wifiConfiguration.allowedKeyManagement.clear();
        // 该配置支持WPA两两密码设置。
        wifiConfiguration.allowedPairwiseCiphers.clear();
        // 此配置支持的安全协议集。
        wifiConfiguration.allowedProtocols.clear();

        if (isApServer) {
            wifiConfiguration.SSID = ssid;
        } else {
            wifiConfiguration.SSID = "\"" + ssid + "\"";
        }
        WifiConfiguration tempConfig = getWifiConfigurationWithSsid(ssid);
        // 如果已有热点存在
        if (tempConfig != null) {
            // 删除旧的热点
            removeNetwork(tempConfig.networkId);
        }

        // 预共享密钥
        if (isApServer) {
            wifiConfiguration.preSharedKey = password;
        } else {
            wifiConfiguration.preSharedKey = "\"" + password + "\"";
        }
        // 隐藏SSID
        wifiConfiguration.hiddenSSID = false;
        // 认证算法
        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        // 允许的协议
//        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        // 允许的密钥管理
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        // 允许的配对密码
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        // 允许的密码群组
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
        return wifiConfiguration;
    }

    /**
     * 开启wifi ap
     */
    public void createWifiAp(String ssid, String password) {
        try {
            WifiConfiguration wifiConfiguration = createWifiConfig(ssid, password, true);
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, boolean.class);
            method.invoke(mWifiManager, wifiConfiguration, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭wifi AP
     */
    public void closeWifiAp() {
        if (isWifiApEnabled()) {
            try {
                Method methodConfig = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                methodConfig.setAccessible(true);
                WifiConfiguration wifiConfiguration = (WifiConfiguration) methodConfig.invoke(mWifiManager);
                Method methodEnabled = mWifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);
                methodEnabled.invoke(mWifiManager, wifiConfiguration, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查WIFI网络是否可用
     */
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 检测AP是否可用
     */
    private boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取热点的开启状态
     */
    public boolean isWifiApOpen() {
        try {
            // 通过放射获取 getWifiApState()方法
            Method method = mWifiManager.getClass().getDeclaredMethod("getWifiApState");
            // 调用getWifiApState() ，获取返回值
            int state = (int) method.invoke(mWifiManager);
            // 通过放射获取 WIFI_AP的开启状态属性
            Field field = mWifiManager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            // 获取属性值
            int value = (int) field.get(mWifiManager);
            // 判断是否开启
            return state == value;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打开WIFI
     */
    public void openWifi() {
        Log.v(GlobalConstants.LOG_TAG, "openWifi");
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 删除已存在网络
     */
    private void removeNetwork(int netId) {
        if (mWifiManager != null) {
            mWifiManager.removeNetwork(netId);
        }
    }

    /**
     * wifi是否可用
     */
    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    /**
     * 连接Wifi
     */
    public boolean connectWifi(String ssid, String password) {
        Log.v(GlobalConstants.LOG_TAG, "ssid:" + ssid + "-password:" + password);
        // 不要断开，假如附近有能连接得WiFi，会先自动连接别的wifi
        WifiConfiguration wifiConfiguration = createWifiConfig(ssid, password, false);
        int netId = mWifiManager.addNetwork(wifiConfiguration);
        //Log.v(GlobalConstants.LOG_TAG, "netId:" + netId);
        if (netId == -1) {
            return false;
        }
        // 设置为true,使其他的连接断开
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.saveConfiguration();
        return mWifiManager.reconnect();
    }

    /**
     * 根据ssid删除wifi配置
     */
    public void removeNetwork(String ssidContains) {
        if (mWifiManager == null) {
            return;
        }
        List<WifiConfiguration> wifiConfigurationList = mWifiManager.getConfiguredNetworks();
        if (wifiConfigurationList != null && !wifiConfigurationList.isEmpty()) {
            for (WifiConfiguration wifiConfiguration : wifiConfigurationList) {
                String savedSsid = wifiConfiguration.SSID;
                savedSsid.replace("\"", "");
                if (savedSsid.contains(ssidContains)) {
                    removeNetwork(wifiConfiguration.networkId);
                }
            }
        }
    }

    /**
     * 获取wifi SSID
     */
    public String getSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null) {
            return "NULL";
        }
        return mWifiInfo.getSSID().replace("\"", "");
    }

    /**
     * 获取wifi信号强度 Rssi
     * 得到的值是一个0到-100的区间值，是一个int型数据
     * 无线信号强度：正常的范围是-90dBm~-60dBm。
     * 当信号有阀值，小于-100之后就无法在wifi列表中找到，系统开发无法识别，大于-55就显示最大值。
     * android系统开发的wifi信号图标有4个状态，根据网上猜测,如果等分之间的值的话那么就是：
     * 0 — (-55)dbm  满格(4格)信号
     * (-55) —— (-70)dbm  3格信号
     * (-70) —— (-85)dbm　2格信号
     * (-85) —— (-100)dbm 1格信号
     */
    public int getRssi() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null) {
            return -200;
        }
        return mWifiInfo.getRssi();
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
    }

    /**
     * 得到网络列表
     */
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    /**
     * 查看扫描结果
     */
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    /**
     * 得到连接的ID
     */
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**
     * 得到wifiInfo的所有信息
     */
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    /**
     * 检查网络连接类型
     */
    public static int getNetworkType(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return -1;
            }
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }
        return -1;
    }

    /**
     * int值转换成ip地址
     */
    public String intToIp(int address) {
        return (address & 0xFF) + "." + ((address >> 8) & 0xFF) + "."
                + ((address >> 16) & 0xFF) + "." + ((address >> 24) & 0xFF);
    }

}
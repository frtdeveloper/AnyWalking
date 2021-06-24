package com.lenwotion.travel.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * Created by fq on 2017/8/10.
 * 常用工具类
 */

public class CommonUtil {

    private static final String PATTERN_PHONE = "^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$";
    private static long mLastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - mLastClickTime;
        mLastClickTime = time;
        return 0 < timeD && timeD < 1000;
    }

    /**
     * 检测是否为手机号
     */
    public static boolean isPhoneNum(String str) {
        return Pattern.compile(PATTERN_PHONE).matcher(str).matches();
    }

    /**
     * 获取assest下的资源文件
     */
    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String result = "";
            while ((line = bufReader.readLine()) != null)
                result += line;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前时间戳
     */
    public static long getTime() {
        return System.currentTimeMillis();//获取系统时间的10位的时间戳
    }

    /**
     * 获取当前时间
     */
    public static String getCurrTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 获取当前时分
     */
    public static String getCurrMinuteTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    /**
     * 比较两个时间差
     */
    public static int getTimeDifference(String currTime, String dataTime) {
        int size = 0;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        try {
            Date cur = formatter.parse(currTime);
            Date result = formatter.parse(dataTime);
            long diff = cur.getTime() - result.getTime();
            String value = Long.toString(diff / 1000);
            size = Integer.valueOf(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 对比时分时间, 当前时间与所选起始时间对比,判断如果当前时间小于选择时间
     */
    public static boolean compareCurrTimeAndOriginTime(String currTime, String originTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        try {
            Date currparse = dateFormat.parse(currTime);
            Date originparse = dateFormat.parse(originTime);
            long diff = currparse.getTime() - originparse.getTime();
            return diff <= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 对比时分时间, 当前时间与所选末班车时间对比，判断如果当前时间大于选择时间
     */
    public static boolean compareCurrTimeAndEndTime(String currTime, String endTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.CHINESE);
        try {
            Date currparse = dateFormat.parse(currTime);
            Date endparse = dateFormat.parse(endTime);
            long diff = endparse.getTime() - currparse.getTime();
            return diff <= 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) {
            return false;
        }
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    /**
     * String 计算 MD5
     */
    public static String getMD5Encode(String string) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (md5 != null) {
            return new BigInteger(1, md5.digest()).toString(16);
        } else {
            return "";
        }
    }

    /**
     * 获取两个经纬度的距离
     */
    public static int getDistance(double cuttentLat, double cuttentLng, double stationLat, double stationLng) {
        LatLng mCuttentLatLng = new LatLng(cuttentLat, cuttentLng);
        LatLng mStationLatLng = new LatLng(stationLat, stationLng);
        return (int) AMapUtils.calculateLineDistance(mCuttentLatLng, mStationLatLng);
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @return true 表示开启
     */
    public static boolean checkGPSIsOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return gps || network;
    }

    /**
     * 强制帮用户打开GPS
     */
    public static void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取屏幕的宽
     */
    public static int getScreenWidth(Activity context) {
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获取屏幕的宽
     */
    public static int getScreenHeight(Activity context) {
        WindowManager manager = context.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        int versionCode = 0;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本名称
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionCode = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * Base64 加密
     */
    public static String encodeString(String string) {
        try {
            byte[] encodeBytes = Base64.encode(string.getBytes("UTF-8"), Base64.DEFAULT);
            return new String(encodeBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Base64 解密
     */
    public static String decodeString(String string) {
        try {
            byte[] encodeBytes = Base64.decode(string.getBytes("UTF-8"), Base64.DEFAULT);
            return new String(encodeBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

}

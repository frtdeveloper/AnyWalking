package com.lenwotion.travel.utils;

import android.content.Context;

import com.lenwotion.travel.global.GlobalConstants;

/**
 * 用户信息读写工具类
 */
public class UserInfoCacheUtil {

    /**
     * 保存用户token
     */
    public static void saveUserToken(Context context, String userToken) {
        SharedPreferencesUtil.setString(context, GlobalConstants.SP_USER_TOKEN, userToken);
    }

    /**
     * 获取用户token(默认为"")
     */
    public static String getUserToken(Context context) {
        return SharedPreferencesUtil.getString(context, GlobalConstants.SP_USER_TOKEN);
    }

    /**
     * 保存用户手机号
     */
    public static void saveUserPhoneNum(Context context, String phoneNum) {
        SharedPreferencesUtil.setString(context,
                GlobalConstants.SP_USER_PHONE_NUMBER, phoneNum);
    }

    /**
     * 获取用户手机号(默认为"")
     */
    public static String getUserPhoneNum(Context context) {
        return SharedPreferencesUtil.getString(context,
                GlobalConstants.SP_USER_PHONE_NUMBER);
    }

    /**
     * 清除用户个人资料信息
     */
    public static void clearUserInfo(Context context) {
        saveUserToken(context, "");
        saveUserPhoneNum(context, "");
    }

}

package com.lenwotion.travel.activity.bybus.contract;

import android.content.Context;

import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.interfaces.ICallBackListener;

/**
 * Created by fq on 2017/10/14.
 */

public interface WaitingBusContract {

    interface ShowWaitResultView {
        /**
         * 显示错误信息
         */
        void showError(String errorMsg);

        void showUploadGPSData(UploadGPSBean data);
    }

    interface WaitingBusModel {
        /**
         * 实时上传经纬度
         */
        void realTimeLatLng(Context context, String userToken, boolean isNettyConnect, String busImei, ICallBackListener listener);
    }

    interface WaitingBusPresenter {
        /**
         * 请求实时经纬接口
         */
        void requestRealTimeLatLng(Context context, String userToken, boolean isNettyConnect, String busImei);
    }

}

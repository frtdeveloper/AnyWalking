package com.lenwotion.travel.activity.bybus.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.interfaces.IByBusService;
import com.lenwotion.travel.interfaces.ICallBackListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fq on 2017/10/14.
 */

public class WaitingBusModel implements WaitingBusContract.WaitingBusModel {

    @Override
    public void realTimeLatLng(final Context context, String userToken, boolean isNettyConnect, String busImei, final ICallBackListener listener) {
        AMapLocation location = GlobalVariables.A_MAP_LOCATION;
        if (location != null) {
            String lng = String.valueOf(location.getLongitude());
            String lat = String.valueOf(location.getLatitude());
            //Log.v(GlobalConstants.LOG_TAG, "uploadGPS:" + lat + "-" + lng);
            IByBusService waitingService = AnyWalkingApplication.getInstance().getRetrofit().create(IByBusService.class);
            Call<UploadGPSBean> call = waitingService.requestWaitingGPSData(userToken, lng, lat, isNettyConnect, busImei);
            call.enqueue(new Callback<UploadGPSBean>() {
                @Override
                public void onResponse(@NonNull Call<UploadGPSBean> call, @NonNull Response<UploadGPSBean> response) {
                    if (response.isSuccessful()) {
                        UploadGPSBean bean = response.body();
                        if (bean != null) {
                            if (bean.isFlag()) {
                                listener.onSuccess(bean);
                            } else {
                                listener.onFailure(bean.getMsg());
                            }
                        } else {
                            // http 错误
                            listener.onFailure(context.getString(R.string.http_error));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UploadGPSBean> call, @NonNull Throwable throwable) {
                    // 超时
                    listener.onFailure(context.getString(R.string.http_error));
                }
            });
        }
    }

}

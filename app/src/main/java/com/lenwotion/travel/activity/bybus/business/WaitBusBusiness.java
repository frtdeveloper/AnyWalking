package com.lenwotion.travel.activity.bybus.business;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lenwotion.travel.activity.bybus.interfaces.IWaitBusService;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.GeneralResponseBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaitBusBusiness {

    /**
     * 上传WIFI连接信息
     */
    public void sendConnectInfo(final Context context, String station, String ssid) {
        IWaitBusService service = AnyWalkingApplication.getInstance().getRetrofit().create(IWaitBusService.class);
        Call<GeneralResponseBean> call = service.sendConnectInfo(UserInfoCacheUtil.getUserToken(context), station, ssid);
        call.enqueue(new Callback<GeneralResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBean> call, @NonNull Response<GeneralResponseBean> response) {
                if (response.isSuccessful()) {
                    GeneralResponseBean bean = response.body();
                    if (bean == null) {
                        Log.v(GlobalConstants.LOG_TAG, "sendConnectInfo 数据错误");
                        return;
                    }
                    if (bean.isFlag()) {
                        Log.v(GlobalConstants.LOG_TAG, "sendConnectInfo 成功");
                    } else {
                        Log.v(GlobalConstants.LOG_TAG, "sendConnectInfo " + bean.getMsg());
                    }
                } else {
                    Log.v(GlobalConstants.LOG_TAG, "sendConnectInfo 网络错误");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBean> call, @NonNull Throwable throwable) {
                Log.v(GlobalConstants.LOG_TAG, "sendConnectInfo 网络错误");
            }
        });
    }


}

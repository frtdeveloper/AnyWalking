package com.lenwotion.travel.test.business;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lenwotion.travel.R;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.test.bean.GetWifiInfoResponseBean;
import com.lenwotion.travel.test.interfaces.IGetWifiInfoCallback;
import com.lenwotion.travel.test.interfaces.IGetWifiInfoService;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetWifiInfoBusiness {

    public void getWifiInfo(final Context context, String imei, final IGetWifiInfoCallback callback) {
        IGetWifiInfoService service = AnyWalkingApplication.getInstance().getRetrofit().create(IGetWifiInfoService.class);
        Call<GetWifiInfoResponseBean> call = service.getWifiInfo(UserInfoCacheUtil.getUserToken(context), imei);
        call.enqueue(new Callback<GetWifiInfoResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<GetWifiInfoResponseBean> call, @NonNull Response<GetWifiInfoResponseBean> response) {
                if (response.isSuccessful()) {
                    GetWifiInfoResponseBean bean = response.body();
                    if (bean == null) {
                        callback.getWifiInfoFail(context.getString(R.string.fail));
                        return;
                    }
                    if (bean.isFlag()) {
                        callback.getWifiInfoSuccess(bean.getData());
                    } else {
                        // 获取服务器信息失败
                        callback.getWifiInfoFail(context.getString(R.string.fail) + ":" + bean.getMsg());
                    }
                } else {
                    // http 错误
                    callback.getWifiInfoFail(context.getString(R.string.fail));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetWifiInfoResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                callback.getWifiInfoFail(context.getString(R.string.fail));
            }
        });
    }

}

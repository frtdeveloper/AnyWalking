package com.lenwotion.travel.activity.bybus.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.bybus.contract.SelectLineContract;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.bybus.AffirmWaitBusBean;
import com.lenwotion.travel.bean.bybus.StationInfoBean;
import com.lenwotion.travel.bean.bybus.StationLineBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.interfaces.IByBusService;
import com.lenwotion.travel.interfaces.ICallBackListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fq on 2017/10/14.
 */

public class SelectLineModel implements SelectLineContract.SelectLineModel {

    @Override
    public void stationLineListData(final Context context, String userToken, String flag, String station, final ICallBackListener listener) {
        IByBusService waitingService = AnyWalkingApplication.getInstance().getRetrofit().create(IByBusService.class);
        Call<StationLineBean> call = waitingService.getStationLineListData(userToken, flag, station);
        call.enqueue(new Callback<StationLineBean>() {
            @Override
            public void onResponse(@NonNull Call<StationLineBean> call, @NonNull Response<StationLineBean> response) {
                if (response.isSuccessful()) {
                    StationLineBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            listener.onSuccess(bean);
                        } else {
                            // 获取服务器信息失败
                            listener.onFailure(bean.getMsg());
                        }
                    } else {
                        // http 错误
                        listener.onFailure(context.getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StationLineBean> call, @NonNull Throwable throwable) {
                // 超时
                listener.onFailure(context.getString(R.string.http_error));
            }
        });
    }

    @Override
    public void waitBusState(final Context context, String userToken, StationInfoBean stationInfoBean,
                             ArrayList<String> lineName, final ICallBackListener listener) {
        // 站台标识
        String flag = stationInfoBean.getFlag();
        //站台名称
        String station = stationInfoBean.getStation();

        Log.v(GlobalConstants.LOG_TAG, "waitBusState.getFlag()==" + flag);
        Log.v(GlobalConstants.LOG_TAG, "waitBusState.getStation()==" + station);
        Log.v(GlobalConstants.LOG_TAG, "waitBusState.lineName==" + lineName);

        // 测试参数，获取测试车载机信息,1-测试 0-正常
        String type = "0";
        if (GlobalVariables.IS_TEST_SHOW) {
            type = "1";
        }

        IByBusService waitingService = AnyWalkingApplication.getInstance().getRetrofit().create(IByBusService.class);
        Call<AffirmWaitBusBean> call = waitingService.getWaitAffirmData(userToken, flag, station, lineName, type);
        call.enqueue(new Callback<AffirmWaitBusBean>() {
            @Override
            public void onResponse(@NonNull Call<AffirmWaitBusBean> call, @NonNull Response<AffirmWaitBusBean> response) {
                if (response.isSuccessful()) {
                    AffirmWaitBusBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            listener.onSuccess(bean);
                        } else {
                            // 获取服务器信息失败
                            listener.onFailure("获取服务器信息失败" + bean.getMsg());
                        }
                    } else {
                        // http 错误
                        listener.onFailure(context.getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AffirmWaitBusBean> call, @NonNull Throwable throwable) {
                // 超时
                listener.onFailure(context.getString(R.string.http_error));
            }
        });
    }

}

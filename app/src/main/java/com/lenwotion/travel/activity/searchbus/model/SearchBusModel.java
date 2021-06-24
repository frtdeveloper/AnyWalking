package com.lenwotion.travel.activity.searchbus.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.searchbus.contract.SearchBusContract;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.search.SearchLineResponseBean;
import com.lenwotion.travel.bean.search.SearchStationResponseBean;
import com.lenwotion.travel.interfaces.ICallBackListener;
import com.lenwotion.travel.interfaces.ISearchService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fq on 2017/11/25.
 */
public class SearchBusModel implements SearchBusContract.SearchBusModel {

    /**
     * 模糊查询站台
     */
    @Override
    public void getSearchStationData(final Context context, String userToken, String filed, String city, final ICallBackListener listener) {
        ISearchService searchService = AnyWalkingApplication.getInstance().getRetrofit().create(ISearchService.class);
        Call<SearchStationResponseBean> call = searchService.getSearchStationMatchData(userToken, filed, city);
        call.enqueue(new Callback<SearchStationResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<SearchStationResponseBean> call, @NonNull Response<SearchStationResponseBean> response) {
                if (response.isSuccessful()) {
                    SearchStationResponseBean bean = response.body();
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
            public void onFailure(@NonNull Call<SearchStationResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                listener.onFailure(context.getString(R.string.http_error));
            }
        });
    }

    /**
     * 模糊查询线路
     */
    @Override
    public void getSearchLineData(final Context context, String userToken, String filed, String city, final ICallBackListener listener) {
        ISearchService searchService = AnyWalkingApplication.getInstance().getRetrofit().create(ISearchService.class);
        Call<SearchLineResponseBean> call = searchService.getSearchLineMatchData(userToken, filed, city);
        call.enqueue(new Callback<SearchLineResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<SearchLineResponseBean> call, @NonNull Response<SearchLineResponseBean> response) {
                if (response.isSuccessful()) {
                    SearchLineResponseBean bean = response.body();
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
            public void onFailure(@NonNull Call<SearchLineResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                listener.onFailure(context.getString(R.string.http_error));
            }
        });
    }

}

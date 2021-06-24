package com.lenwotion.travel.activity.searchbus.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.searchbus.contract.SearchLineDirectionContract;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.search.SearchLineDirectionResponseBean;
import com.lenwotion.travel.interfaces.ICallBackListener;
import com.lenwotion.travel.interfaces.ISearchService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fq on 2017/11/25.
 */

public class SearchLineDirectionModle implements SearchLineDirectionContract.SearchLineDirectionModle {

    /**
     * 获取查询线路上下行
     */
    @Override
    public void getSearchLineDirectionData(final Context context, String userToken, String line, String city, final ICallBackListener listener) {
        ISearchService searchService = AnyWalkingApplication.getInstance().getRetrofit().create(ISearchService.class);
        Call<SearchLineDirectionResponseBean> call = searchService.getSearchLineDirectionData(userToken, line, city);
        call.enqueue(new Callback<SearchLineDirectionResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<SearchLineDirectionResponseBean> call, @NonNull Response<SearchLineDirectionResponseBean> response) {
                if (response.isSuccessful()) {
                    SearchLineDirectionResponseBean bean = response.body();
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
            public void onFailure(@NonNull Call<SearchLineDirectionResponseBean> call, @NonNull Throwable throwable) {
                // 超时
                listener.onFailure(context.getString(R.string.http_error));
            }
        });
    }

}

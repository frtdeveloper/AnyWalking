package com.lenwotion.travel.activity.searchbus.presenter;

import android.content.Context;

import com.lenwotion.travel.activity.searchbus.contract.SearchBusContract;
import com.lenwotion.travel.activity.searchbus.model.SearchBusModel;
import com.lenwotion.travel.bean.search.SearchLineResponseBean;
import com.lenwotion.travel.bean.search.SearchStationResponseBean;
import com.lenwotion.travel.interfaces.ICallBackListener;

/**
 * Created by fq on 2017/11/25.
 */

public class SearchBusPresenter implements SearchBusContract.SearchBusPresenter {

    private SearchBusContract.ShowSearchLineView mLineView;
    private SearchBusContract.ShowSearchStationView mStationView;
    private SearchBusModel mModel;

    public SearchBusPresenter(SearchBusContract.ShowSearchLineView view) {
        mLineView = view;
        mModel = new SearchBusModel();
    }

    public SearchBusPresenter(SearchBusContract.ShowSearchStationView view) {
        mStationView = view;
        mModel = new SearchBusModel();
    }

    /**
     * 模糊站台匹配
     */
    @Override
    public void requestSearchStationData(Context context, String userToken, String filed, String city) {
        if (mStationView == null) {
            return;
        }
        if (mModel != null) {
            mModel.getSearchStationData(context, userToken, filed, city, new ICallBackListener<SearchStationResponseBean>() {
                @Override
                public void onSuccess(SearchStationResponseBean result) {
                    mStationView.showSearchStationData(result.getData());
                }

                @Override
                public void onFailure(String errorMsg) {
                    mStationView.showError("模糊站台匹配失败：" + errorMsg);
                }
            });
        }
    }

    /**
     * 模糊线路匹配
     */
    @Override
    public void requestSearchLineData(Context context, String userToken, String filed, String city) {
        if (mLineView == null) {
            return;
        }
        if (mModel != null) {
            mModel.getSearchLineData(context, userToken, filed, city, new ICallBackListener<SearchLineResponseBean>() {
                @Override
                public void onSuccess(SearchLineResponseBean result) {
                    mLineView.showSearchLineData(result.getData());
                }

                @Override
                public void onFailure(String errorMsg) {
                    mLineView.showError("模糊线路匹配失败：" + errorMsg);
                }
            });
        }
    }

}

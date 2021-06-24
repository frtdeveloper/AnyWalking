package com.lenwotion.travel.activity.searchbus.contract;

import android.content.Context;

import com.lenwotion.travel.bean.search.SearchLineInfoBean;
import com.lenwotion.travel.bean.search.SearchStationInfoBean;
import com.lenwotion.travel.interfaces.ICallBackListener;

import java.util.List;

/**
 * Created by fq on 2017/11/25.
 */

public interface SearchBusContract {

    interface ShowSearchLineView {
        /**
         * 显示错误信息
         */
        void showError(String errorMsg);

        /**
         * 显示模糊查询线路
         */
        void showSearchLineData(List<SearchLineInfoBean> lineData);
    }

    interface ShowSearchStationView {
        /**
         * 显示错误信息
         */
        void showError(String errorMsg);

        /**
         * 显示模糊查询站台
         */
        void showSearchStationData(List<SearchStationInfoBean> stationData);
    }


    interface SearchBusModel {
        /**
         * 获取模糊查询线路
         */
        void getSearchLineData(Context context, String userToken, String filed, String city, ICallBackListener listener);

        /**
         * 获取模糊查询站台
         */
        void getSearchStationData(Context context, String userToken, String filed, String city, ICallBackListener listener);
    }

    interface SearchBusPresenter {
        /**
         * 请求模糊查询线路
         */
        void requestSearchLineData(Context context, String userToken, String filed, String city);

        /**
         * 请求模糊查询站台
         */
        void requestSearchStationData(Context context, String userToken, String line, String city);
    }

}

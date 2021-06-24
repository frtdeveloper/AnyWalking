package com.lenwotion.travel.activity.bybus.contract;

import android.content.Context;

import com.lenwotion.travel.bean.bybus.AffirmWaitBusBean;
import com.lenwotion.travel.bean.bybus.StationInfoBean;
import com.lenwotion.travel.bean.bybus.StationLineBean;
import com.lenwotion.travel.interfaces.ICallBackListener;
import com.lenwotion.travel.view.BaseView;

import java.util.ArrayList;

/**
 * Created by fq on 2017/10/14.
 */

public interface SelectLineContract {

    interface ShowLineResultView extends BaseView {
        /**
         * 显示路线列表
         */
        void showStationLineListData(StationLineBean stationLineBean);

        /**
         * 显示候车状态
         */
        void showAffirmWaitSucceed(AffirmWaitBusBean result);
    }

    interface SelectLineModel {
        /**
         * 获取站台线路
         */
        void stationLineListData(Context context, String userToken, String flag, String station, ICallBackListener listener);

        /**
         * 请求开始候车
         */
        void waitBusState(Context context, String userToken, StationInfoBean stationInfoBean, ArrayList<String> lineName, ICallBackListener listener);
    }

    interface SelectLinePresenter {
        /**
         * 请求获取站台线路
         */
        void requestStationLineListData(Context context, String userToken, String flag, String station);

        /**
         * 请求开始候车
         */
        void requestWaitBus(Context context, String userToken, StationInfoBean stationInfoBean, ArrayList<String> lineName);
    }

}

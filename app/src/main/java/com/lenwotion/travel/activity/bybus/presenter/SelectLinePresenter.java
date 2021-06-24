package com.lenwotion.travel.activity.bybus.presenter;

import android.content.Context;

import com.lenwotion.travel.activity.bybus.contract.SelectLineContract;
import com.lenwotion.travel.activity.bybus.model.SelectLineModel;
import com.lenwotion.travel.bean.bybus.AffirmWaitBusBean;
import com.lenwotion.travel.bean.bybus.StationInfoBean;
import com.lenwotion.travel.bean.bybus.StationLineBean;
import com.lenwotion.travel.interfaces.ICallBackListener;

import java.util.ArrayList;

/**
 * Created by fq on 2017/10/14.
 */
public class SelectLinePresenter implements SelectLineContract.SelectLinePresenter {

    private SelectLineContract.ShowLineResultView mView;
    private SelectLineModel mModel;

    public SelectLinePresenter(SelectLineContract.ShowLineResultView view) {
        mView = view;
        mModel = new SelectLineModel();
    }

    @Override
    public void requestStationLineListData(Context context, String userToken, String flag, String station) {
        if (mView == null) {
            return;
        }
        //mView.showProgress();
        mModel.stationLineListData(context, userToken, flag, station, new ICallBackListener<StationLineBean>() {
            @Override
            public void onSuccess(StationLineBean result) {
                //mView.hideProgress();
                mView.showStationLineListData(result);
            }

            @Override
            public void onFailure(String errorMsg) {
                //mView.hideProgress();
                mView.showError("请求线路列表失败:" + errorMsg);
            }
        });
    }

    @Override
    public void requestWaitBus(Context context, String userToken, StationInfoBean stationInfoBean, ArrayList<String> lineName) {
        if (mView == null) {
            return;
        }
        //mView.showProgress();
        mModel.waitBusState(context, userToken, stationInfoBean, lineName, new ICallBackListener<AffirmWaitBusBean>() {
            @Override
            public void onSuccess(AffirmWaitBusBean result) {
                //mView.hideProgress();
                mView.showAffirmWaitSucceed(result);
            }

            @Override
            public void onFailure(String errorMsg) {
                //mView.hideProgress();
                mView.showError("请求候车失败:" + errorMsg);
            }
        });
    }

}

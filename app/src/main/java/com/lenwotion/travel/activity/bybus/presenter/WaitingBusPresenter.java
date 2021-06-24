package com.lenwotion.travel.activity.bybus.presenter;

import android.content.Context;

import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
import com.lenwotion.travel.activity.bybus.model.WaitingBusModel;
import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.interfaces.ICallBackListener;

/**
 * Created by fq on 2017/10/14.
 */

public class WaitingBusPresenter implements WaitingBusContract.WaitingBusPresenter {

    private WaitingBusContract.ShowWaitResultView mView;
    private WaitingBusModel mModel;

    public WaitingBusPresenter(WaitingBusContract.ShowWaitResultView view) {
        mView = view;
        mModel = new WaitingBusModel();
    }

    /**
     * 实时上传GPS
     */
    @Override
    public void requestRealTimeLatLng(final Context context, String userToken, boolean isNettyConnect, String busImei) {
        if (mView == null) {
            return;
        }
        mModel.realTimeLatLng(context, userToken, isNettyConnect, busImei, new ICallBackListener<UploadGPSBean>() {
            @Override
            public void onSuccess(UploadGPSBean result) {
                mView.showUploadGPSData(result);
            }

            @Override
            public void onFailure(String errorMsg) {
                //mView.showError(context.getString(R.string.fail) + errorMsg);
            }
        });
    }

}

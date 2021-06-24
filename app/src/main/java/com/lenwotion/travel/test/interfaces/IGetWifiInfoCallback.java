package com.lenwotion.travel.test.interfaces;

import com.lenwotion.travel.test.bean.GetWifiInfoBean;

public interface IGetWifiInfoCallback {

    void getWifiInfoSuccess(GetWifiInfoBean bean);

    void getWifiInfoFail(String errMsg);

}

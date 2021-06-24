package com.lenwotion.travel.test.bean;

import com.lenwotion.travel.bean.GeneralResponseBean;

public class GetWifiInfoResponseBean extends GeneralResponseBean {

    private GetWifiInfoBean data;

    public GetWifiInfoBean getData() {
        return data;
    }

    public void setData(GetWifiInfoBean data) {
        this.data = data;
    }

}

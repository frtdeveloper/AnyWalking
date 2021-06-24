package com.lenwotion.travel.bean.bybus;

import com.lenwotion.travel.bean.GeneralResponseBean;

import java.util.List;

/**
 * 站台列表
 * Created by fq on 2017/8/31.
 */

public class StationListBean extends GeneralResponseBean {

    private List<StationInfoBean> data;
    public List<StationInfoBean> getData() {
        return data;
    }
    public void setData(List<StationInfoBean> data){
        this.data = data;
    }
}

package com.lenwotion.travel.bean.bybus;

import com.lenwotion.travel.bean.GeneralResponseBean;

import java.util.List;

/**
 * 确定候车
 * Created by fq on 2017/11/13.
 */

public class AffirmWaitBusBean extends GeneralResponseBean{
    private AffirmWaitInfoBean data;

    public AffirmWaitInfoBean getData() {
        return data;
    }

    public void setData(AffirmWaitInfoBean data) {
        this.data = data;
    }
}

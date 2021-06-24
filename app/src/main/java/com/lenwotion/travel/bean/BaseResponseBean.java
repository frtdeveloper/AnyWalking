package com.lenwotion.travel.bean;

/**
 * 公共返回数据
 * Created by fq on 2017/8/25.
 */

public class BaseResponseBean extends GeneralResponseBean {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

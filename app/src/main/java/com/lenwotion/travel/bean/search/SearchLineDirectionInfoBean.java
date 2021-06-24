package com.lenwotion.travel.bean.search;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fq on 2017/11/25.
 */

public class SearchLineDirectionInfoBean {

    /**
     * 上行
     */
    @SerializedName("upWay")
    private List<StationInfoBean> upDirection;
    /**
     * 下行
     */
    @SerializedName("downWay")
    private List<StationInfoBean> downDirection;

    public List<StationInfoBean> getUpDirection() {
        return upDirection;
    }

    public void setUpDirection(List<StationInfoBean> upDirection) {
        this.upDirection = upDirection;
    }

    public List<StationInfoBean> getDownDirection() {
        return downDirection;
    }

    public void setDownDirection(List<StationInfoBean> downDirection) {
        this.downDirection = downDirection;
    }

}

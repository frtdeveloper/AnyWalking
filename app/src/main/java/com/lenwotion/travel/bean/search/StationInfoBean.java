package com.lenwotion.travel.bean.search;

/**
 * Created by fq on 2017/12/5.
 */

public class StationInfoBean {
    /**
     *  站台名
     */
    private String stationName;
    /**
     *  经纬度
     */
    private String lng;
    private String lat;

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
}

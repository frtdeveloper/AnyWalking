package com.lenwotion.travel.bean.bybus;

/**
 * 站台具体信息
 * Created by fq on 2017/9/7.
 */

public class StationInfoBean {

    /**
     * 站台标识
     */
    private String flag;
    /**
     * 站台名称
     */
    private String station;
    /**
     * 站台经度
     */
    private String lng;
    /**
     * 站台维度
     */
    private String lat;
    /**
     * 是否开通(1开通，0未开通)
     */
    private int enabled;
    /**
     * 与站台相距多少米
     */
    private double distance;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "StationInfoBean{" +
                "flag='" + flag + '\'' +
                ", station='" + station + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", enabled=" + enabled +
                ", distance=" + distance +
                '}';
    }
}

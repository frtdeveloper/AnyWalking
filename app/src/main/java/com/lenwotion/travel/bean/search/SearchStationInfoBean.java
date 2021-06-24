package com.lenwotion.travel.bean.search;

/**
 * Created by fq on 2017/11/25.
 */

public class SearchStationInfoBean {
    /**
     * lat1 : 22.621205
     * lat2 : 22.621121
     * lng1 : 113.869661
     * lng2 : 113.869639
     * station1 : 西乡华佳工业园-A
     * station2 : 西乡华佳工业园-B
     */
    private String lat1;
    private String lat2;
    private String lng1;
    private String lng2;
    private String station1;
    private String station2;

    public String getLat1() {
        return lat1;
    }

    public void setLat1(String lat1) {
        this.lat1 = lat1;
    }

    public String getLat2() {
        return lat2;
    }

    public void setLat2(String lat2) {
        this.lat2 = lat2;
    }

    public String getLng1() {
        return lng1;
    }

    public void setLng1(String lng1) {
        this.lng1 = lng1;
    }

    public String getLng2() {
        return lng2;
    }

    public void setLng2(String lng2) {
        this.lng2 = lng2;
    }

    public String getStation1() {
        return station1;
    }

    public void setStation1(String station1) {
        this.station1 = station1;
    }

    public String getStation2() {
        return station2;
    }

    public void setStation2(String station2) {
        this.station2 = station2;
    }

    @Override
    public String toString() {
        return "SearchStationInfoBean{" +
                "lat1='" + lat1 + '\'' +
                ", lat2='" + lat2 + '\'' +
                ", lng1='" + lng1 + '\'' +
                ", lng2='" + lng2 + '\'' +
                ", station1='" + station1 + '\'' +
                ", station2='" + station2 + '\'' +
                '}';
    }

}

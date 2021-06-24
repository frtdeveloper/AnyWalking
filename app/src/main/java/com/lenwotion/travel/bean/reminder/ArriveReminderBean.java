package com.lenwotion.travel.bean.reminder;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by fq on 2017/12/06.
 */
@Entity
public class ArriveReminderBean {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 区分是线路还是站台
     */
    private int type;
    private int stationId;
    /**
     * 站台名
     */
    private String stationName;
    /**
     * 经度
     */
    private String lng;
    /**
     * 维度
     */
    private String lat;
    /**
     * 距离
     */
    private int direction;
    /**
     * 区分上下行
     */
    private int wayType;
    /**
     * 是否提醒
     */
    private boolean isRemind;
    /**
     * 是否到达
     */
    private boolean isArrive;

    @Generated(hash = 1598333827)
    public ArriveReminderBean(Long id, int type, int stationId, String stationName,
                              String lng, String lat, int direction, int wayType, boolean isRemind,
                              boolean isArrive) {
        this.id = id;
        this.type = type;
        this.stationId = stationId;
        this.stationName = stationName;
        this.lng = lng;
        this.lat = lat;
        this.direction = direction;
        this.wayType = wayType;
        this.isRemind = isRemind;
        this.isArrive = isArrive;
    }

    @Generated(hash = 836127364)
    public ArriveReminderBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStationId() {
        return this.stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return this.stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLng() {
        return this.lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getWayType() {
        return this.wayType;
    }

    public void setWayType(int wayType) {
        this.wayType = wayType;
    }

    public boolean getIsRemind() {
        return this.isRemind;
    }

    public void setIsRemind(boolean isRemind) {
        this.isRemind = isRemind;
    }

    public boolean getIsArrive() {
        return this.isArrive;
    }

    public void setIsArrive(boolean isArrive) {
        this.isArrive = isArrive;
    }

}

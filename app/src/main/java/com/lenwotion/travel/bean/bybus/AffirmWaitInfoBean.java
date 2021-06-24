package com.lenwotion.travel.bean.bybus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 确定候车车载机状态、信息
 * Created by fq on 2017/9/12.
 */

public class AffirmWaitInfoBean implements Parcelable {
    /**
     * 操作返回码{200=正常 201=推送车载机超过三次 202=选中线路无回调信息 203=查不到该站台信息 204=系统异常 302=netty连接中}
     */
    private int operateCode;
    /**
     * 车载机IMEI
     */
    private String imei;
    /**
     * 车载机wifi ssid
     */
    private String ssid;
    /**
     * 车载机wifi密码
     */
    private String password;
    /**
     * 距离
     */
    private int distance;
    /**
     * 车载机路线名
     */
    private String lineName;
    /**
     * 车载机车牌号
     */
    private String plateNumber;
    /**
     * 当前站台
     */
    private String currentStation;
    /**
     * 距离几个站
     */
    private int frontStations;

    public int getOperateCode() {
        return operateCode;
    }

    public void setOperateCode(int operateCode) {
        this.operateCode = operateCode;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getCurrentStation() {
        return currentStation;
    }

    public void setCurrentStation(String currentStation) {
        this.currentStation = currentStation;
    }

    public int getFrontStations() {
        return frontStations;
    }

    public void setFrontStations(int frontStations) {
        this.frontStations = frontStations;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.operateCode);
        dest.writeString(this.imei);
        dest.writeString(this.ssid);
        dest.writeString(this.password);
        dest.writeInt(this.distance);
        dest.writeString(this.lineName);
        dest.writeString(this.plateNumber);
        dest.writeString(this.currentStation);
        dest.writeInt(this.frontStations);
    }

    public AffirmWaitInfoBean() {
    }

    protected AffirmWaitInfoBean(Parcel in) {
        this.operateCode = in.readInt();
        this.imei = in.readString();
        this.ssid = in.readString();
        this.password = in.readString();
        this.distance = in.readInt();
        this.lineName = in.readString();
        this.plateNumber = in.readString();
        this.currentStation = in.readString();
        this.frontStations = in.readInt();
    }

    public static final Creator<AffirmWaitInfoBean> CREATOR = new Creator<AffirmWaitInfoBean>() {
        @Override
        public AffirmWaitInfoBean createFromParcel(Parcel source) {
            return new AffirmWaitInfoBean(source);
        }

        @Override
        public AffirmWaitInfoBean[] newArray(int size) {
            return new AffirmWaitInfoBean[size];
        }
    };

}

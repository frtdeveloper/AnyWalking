package com.lenwotion.travel.bean.bybus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fq on 2017/9/1.
 */

public class StationLineInfoBean implements Parcelable {

    /**
     * 线路名称
     */
    private String lineName;
    /**
     * 起始站
     */
    private String origin;
    /**
     * 终点站
     */
    private String terminal;
    /**
     * 服务开始时间
     */
    private String serverStartTm;
    /**
     * 服务结束时间
     */
    private String serverEndTm;

    private boolean isChecked ;

    protected StationLineInfoBean(Parcel in) {
        lineName = in.readString();
        origin = in.readString();
        terminal = in.readString();
        serverStartTm = in.readString();
        serverEndTm = in.readString();
    }

    public static final Creator<StationLineInfoBean> CREATOR = new Creator<StationLineInfoBean>() {
        @Override
        public StationLineInfoBean createFromParcel(Parcel in) {
            return new StationLineInfoBean(in);
        }

        @Override
        public StationLineInfoBean[] newArray(int size) {
            return new StationLineInfoBean[size];
        }
    };

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getServerStartTm() {
        return serverStartTm;
    }

    public void setServerStartTm(String serverStartTm) {
        this.serverStartTm = serverStartTm;
    }

    public String getServerEndTm() {
        return serverEndTm;
    }

    public void setServerEndTm(String serverEndTm) {
        this.serverEndTm = serverEndTm;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(lineName);
        out.writeString(origin);
        out.writeString(terminal);
        out.writeString(serverStartTm);
        out.writeString(serverEndTm);
    }

}

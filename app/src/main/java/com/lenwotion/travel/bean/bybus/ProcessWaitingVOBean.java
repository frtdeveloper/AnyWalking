package com.lenwotion.travel.bean.bybus;

/**
 * 候车界面：上传GPS返回的车载机具体业务数据
 * Created by fq on 2017/11/16.
 */

public class ProcessWaitingVOBean {

    /**
     * 200 正常等车（distance）
     */
    public static final int CODE_WAITTING_BUS = 200;
    /**
     * 201 用户远离站台
     */
    public static final int CODE_WAITTING_BUS_201 = 201;
    /**
     * 202 车辆已出站，没有新的车辆分配，候车退出
     */
    public static final int CODE_WAITTING_BUS_202 = 202;
    /**
     * 203 用户等待超过15分钟重新叫车
     */
    public static final int CODE_WAITTING_BUS_203 = 203;
    /**
     * 204 用户不存在
     */
    public static final int CODE_WAITTING_BUS_204 = 204;
    /**
     * 302 WIFI连接中，需求要求不做任何操作
     */
    public static final int CODE_NETTY_CONNECT = 302;

    private int code;
    private double distance;
    private AffirmWaitInfoBean affirm;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public AffirmWaitInfoBean getAffirm() {
        return affirm;
    }

    public void setAffirm(AffirmWaitInfoBean affirm) {
        this.affirm = affirm;
    }

    @Override
    public String toString() {
        return "ProcessWaitingVOBean{" +
                "code=" + code +
                ", distance=" + distance +
                ", affirm=" + affirm +
                '}';
    }
}

package com.lenwotion.travel.bean;


/**
 * 大部分接口通用服务端返回数据bean(其他接口返回bean基本上继承的父类)
 * Created by John on 2016/7/4.
 */
public class GeneralResponseBean {
    /**
     * 操作标识
     */
    private boolean flag;
    /**
     * 操作返回码
     */
    private int code;
    /**
     * 操作信息
     */
    private String msg;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

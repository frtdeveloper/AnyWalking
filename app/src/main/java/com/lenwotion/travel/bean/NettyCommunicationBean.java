package com.lenwotion.travel.bean;

/**
 * netty通讯实体类
 * Created by John on 2016/12/23.
 */

public class NettyCommunicationBean {

    /**
     * 用户token
     */
    private String userToken;
    /**
     * 操作码
     */
    private int operationCode;
    /**
     * 操作数据
     */
    private String operationData;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public int getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(int operationCode) {
        this.operationCode = operationCode;
    }

    public String getOperationData() {
        return operationData;
    }

    public void setOperationData(String operationData) {
        this.operationData = operationData;
    }
}

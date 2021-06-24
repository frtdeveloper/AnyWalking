package com.lenwotion.travel.interfaces;

/**
 * 回调请求结果的结果
 * Created by fq on 2017/09/13.
 */
public interface ICallBackListener<T> {

    void onSuccess(T result);

    void onFailure(String errorMsg);

}

package com.lenwotion.travel.test.interfaces;

import com.lenwotion.travel.test.bean.GetWifiInfoResponseBean;
import com.lenwotion.travel.global.GlobalConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IGetWifiInfoService {

    /**
     * 获取wifi信息，工厂测试用
     */
    @FormUrlEncoded
    @POST(GlobalConstants.APP_GENERAL_FETCH_WIFI_INFO)
    Call<GetWifiInfoResponseBean> getWifiInfo(@Field("userToken") String userToken,
                                              @Field("imei") String imei);

}

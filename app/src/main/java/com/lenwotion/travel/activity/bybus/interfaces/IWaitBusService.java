package com.lenwotion.travel.activity.bybus.interfaces;

import com.lenwotion.travel.bean.GeneralResponseBean;
import com.lenwotion.travel.global.GlobalConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IWaitBusService {

    /**
     * 上传WIFI连接信息
     */
    @FormUrlEncoded
    @POST(GlobalConstants.SEND_CONNECT_INFO)
    Call<GeneralResponseBean> sendConnectInfo(@Field("ucid") String userToken,
                                              @Field("station") String station,
                                              @Field("wifiName") String wifiName);

}

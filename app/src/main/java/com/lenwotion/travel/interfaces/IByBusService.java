package com.lenwotion.travel.interfaces;

import com.lenwotion.travel.bean.GeneralResponseBean;
import com.lenwotion.travel.bean.bybus.AffirmWaitBusBean;
import com.lenwotion.travel.bean.bybus.StationLineBean;
import com.lenwotion.travel.bean.bybus.StationListBean;
import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.global.GlobalConstants;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 坐车模块
 * Created by fq on 2017/8/31.
 */
public interface IByBusService {

    /**
     * 上传当前用户Token、GPS，获取站台列表数据
     */
    @FormUrlEncoded
    @POST(GlobalConstants.WAITING_GET_STATION_LIE)
    Call<StationListBean> getStationListData(@Field("userToken") String userToken,
                                             @Field("lat") String lat,
                                             @Field("lng") String lng);

    /**
     * 根据站台查询线路
     */
    @FormUrlEncoded
    @POST(GlobalConstants.WAITING_GET_STATION_LINE_LIE)
    Call<StationLineBean> getStationLineListData(@Field("userToken") String userToken,
                                                 @Field("flag") String flag,
                                                 @Field("station") String station);

    /**
     * 确认候车
     */
    @FormUrlEncoded
    @POST(GlobalConstants.WAITING_GET_WAIT_AFFIRM_LINE)
    Call<AffirmWaitBusBean> getWaitAffirmData(@Field("userToken") String userToken,
                                              @Field("flag") String flag,
                                              @Field("station") String station,
                                              @Field("lineList") ArrayList<String> lineList,
                                              @Field("type") String type);//type 1 测试 0 正常

    /**
     * 用户GPS上传判断
     */
    @FormUrlEncoded
    @POST(GlobalConstants.WAITING_REQUEST_GPS)
    Call<UploadGPSBean> requestWaitingGPSData(@Field("userToken") String userToken,
                                              @Field("lng") String lng,
                                              @Field("lat") String lat,
                                              @Field("linkWiFi") boolean linkWiFi,
                                              @Field("imei") String busImei);

    /**
     * 取消候车
     */
    @FormUrlEncoded
    @POST(GlobalConstants.WAITING_CANCEL)
    Call<GeneralResponseBean> cancelWaitingBus(@Field("userToken") String userToken);

}

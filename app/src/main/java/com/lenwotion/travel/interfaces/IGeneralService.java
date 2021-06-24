package com.lenwotion.travel.interfaces;

import com.lenwotion.travel.bean.BaseResponseBean;
import com.lenwotion.travel.bean.general.AppVersionResponseBean;
import com.lenwotion.travel.global.GlobalConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * APP常规通用网络服务
 * Created by John on 2017/4/12.
 */
public interface IGeneralService {

    /**
     * 获取最新版本信息
     * appType = 1 用户端
     */
    @FormUrlEncoded
    @POST(GlobalConstants.APP_GENERAL_FETCH_VERSION_INFO)
    Call<AppVersionResponseBean> fetchAppLatestVersionInfo(@Field("appType") int appType);

    /**
     * 提交意见建议信息
     */
    @FormUrlEncoded
    @POST(GlobalConstants.APP_GENERAL_SEND_ADVICE_INFO)
    Call<BaseResponseBean> sendAdviceInfo(@Field("userToken") String userToken,
                                          @Field("content") String content);

//    /**
//     * 上传错误信息
//     */
//    @FormUrlEncoded
//    @POST(GlobalConstant.APP_GENERAL_UPLOAD_CRASH_REPORTS)
//    Call<BaseResponseBean> uploadCrashReportsInfo(@Field("imei") String imei,
//                                                  @Field("content") String crash);

}

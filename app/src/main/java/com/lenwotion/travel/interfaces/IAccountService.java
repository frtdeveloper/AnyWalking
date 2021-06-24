package com.lenwotion.travel.interfaces;

import com.lenwotion.travel.bean.BaseResponseBean;
import com.lenwotion.travel.global.GlobalConstants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 用户账号模块接口
 * Created by fq on 2017/08/17.
 */
public interface IAccountService {

    int TYPE_REGISTER = 1;
    int TYPE_RESET_PASSWORD = 2;

    /**
     * 登录的接口
     */
    @FormUrlEncoded
    @POST(GlobalConstants.ACCOUNT_LOGIN_BY_PHONE_NUMBER)
    Call<BaseResponseBean> login(@Field("phoneNum") String phoneNum,
                                 @Field("password") String password);

    /**
     * 注册的接口
     */
    @FormUrlEncoded
    @POST(GlobalConstants.ACCOUNT_REGISTER)
    Call<BaseResponseBean> register(@Field("phoneNum") String phoneNum,
                                    @Field("password") String password,
                                    @Field("verifyCode") String verifyCode,
                                    @Field("type") int type);

    /**
     * 获取手机验证码的接口
     */
    @FormUrlEncoded
    @POST(GlobalConstants.ACCOUNT_FETCH_PHONE_VERIFY_CODE)
    Call<BaseResponseBean> fetchPhoneVerifyCode(@Field("phoneNum") String phoneNum);

    /**
     * 用户是否使用APP
     */
    @FormUrlEncoded
    @POST(GlobalConstants.ACCOUNT_USER_ISUSE_APP)
    Call<BaseResponseBean> checkIsUseApp(@Field("phoneNum") String phoneNum);

}

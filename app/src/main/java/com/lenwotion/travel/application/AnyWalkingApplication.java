package com.lenwotion.travel.application;

import android.app.Application;

import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.greendao.DaoMaster;
import com.lenwotion.travel.greendao.DaoSession;
import com.lenwotion.travel.utils.CrashHandler;
import com.lenwotion.travel.utils.SharedPreferencesUtil;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * APP的application
 * Created by John on 2016/6/6.
 */
public class AnyWalkingApplication extends Application {

    /**
     * 单例模式实例
     */
    private static AnyWalkingApplication mInstance;
    /**
     * greendao数据库操作对象
     */
    private DaoSession mDaoSession;
    /**
     * Retrofit 操作对象
     */
    private Retrofit mRetrofit;

    /**
     * 获取实例
     */
    public static synchronized AnyWalkingApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
    }

    /**
     * 初始化一些东西
     */
    private void init() {
        // 异常崩溃处理
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
        // 初始化Retrofit
        initRetrofit();
        // 初始化数据库
        initDatabase();
    }

    /**
     * 初始化Retrofit
     */
    private void initRetrofit() {
        //是否本地测试版本
        boolean isLocal = SharedPreferencesUtil.getBoolean(getApplicationContext(), "LocalModel");
        String baseUrl = GlobalConstants.BASE_SERVICE_URL;
        if (isLocal) {
            baseUrl = GlobalConstants.BASE_SERVICE_URL_LOCAL;
        }
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 获取Retrofit对象
     */
    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 初始化数据库
     */
    private void initDatabase() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), GlobalConstants.DATABASE_NAME, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDaoSession = daoMaster.newSession();
    }

    /**
     * 获取数据库DAO对象
     */
    public DaoSession getDaoSession() {
        return mDaoSession;
    }

}

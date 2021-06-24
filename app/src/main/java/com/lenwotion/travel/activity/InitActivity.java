package com.lenwotion.travel.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.login.LoginActivity;
import com.lenwotion.travel.activity.me.AboutActivity;
import com.lenwotion.travel.activity.me.InfoActivity;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.SharedPreferencesUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import java.util.ArrayList;

/**
 * 初始化页面
 */
public class InitActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_init);
        initData();
        initFunction();
    }

    private void initData() {
        mContext = this;
        getInitData();
    }

    private void getInitData() {
        boolean isFactory = SharedPreferencesUtil.getBoolean(mContext, "FactoryModel");
        boolean isShow = SharedPreferencesUtil.getBoolean(mContext, "ShowModel");
        boolean isGps = SharedPreferencesUtil.getBoolean(mContext, "GpsModel");
        String testLat = SharedPreferencesUtil.getString(mContext, "TestLat");
        String testLng = SharedPreferencesUtil.getString(mContext, "TestLng");

        GlobalVariables.IS_TEST_FACTORY = isFactory;
        GlobalVariables.IS_TEST_SHOW = isShow;
        GlobalVariables.IS_TEST_GPS = isGps;
        GlobalVariables.TEST_LAT = testLat;
        GlobalVariables.TEST_LNG = testLng;
    }

    /**
     * 初始化一些功能及数据
     */
    private void initFunction() {
        boolean isAgree = SharedPreferencesUtil.getBoolean(mContext, "Agreement");
        if (isAgree) {
            initPermssions();
        } else {
            showAgreement();
        }
    }

    private void showAgreement() {
        findViewById(R.id.ll_agreement).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_agreement).setOnClickListener(v -> {
            Intent intent = new Intent(mContext, InfoActivity.class);
            intent.putExtra(AboutActivity.ABOUT_INFO_TYPE_KEY, AboutActivity.ABOUT_INFO_TYPE_AGREEMENT);
            startActivity(intent);
            //startActivity(new Intent(InitActivity.this, AgreementActivity.class))
        });
        findViewById(R.id.bt_init_agree).setOnClickListener(v -> {
            findViewById(R.id.ll_agreement).setVisibility(View.GONE);
            SharedPreferencesUtil.setBoolean(mContext, "Agreement", true);
            initPermssions();
        });
        findViewById(R.id.bt_init_disagree).setOnClickListener(v -> finish());
    }

    /**
     * 初始化权限请求
     */
    private void initPermssions() {
        // 首先判断安卓版本，6.0以下没有权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 需要权限申请
            checkPermissions();
        } else {
            // 不需要权限申请
            // 继续初始化其他数据
            initOtherData();
        }
    }

    /**
     * 检查缺少权限
     */
    private void checkPermissions() {
        ArrayList<String> permissionList = new ArrayList<>();
        // 权限请求
        // APP写入权限(只需设置可写，因为可写必定可读)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        // 获取手机状态信息（数位定位需要用到）
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        // 定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionList.isEmpty()) {
            // 还有权限未申请，加入申请列表
            String[] permissions = new String[permissionList.size()];
            for (int i = 0; i < permissionList.size(); i++) {
                permissions[i] = permissionList.get(i);
            }
            // 开始申请权限
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            // 已有所有权限
            // 继续初始化其他数据
            initOtherData();
        }
    }

    /**
     * 权限申请回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 需要再次检查，因为没有区分哪个通过，所以在某些通过的情况下会漏掉没有通过的
        checkPermissions();
    }

    /**
     * 检测拥有所有权限后,继续初始化其他数据
     */
    private void initOtherData() {
        startApp();
    }

    /**
     * 启动APP
     */
    private void startApp() {
        new Handler().postDelayed(this::checkUserLogin, 1000);
    }

    /**
     * 检查是否已登录
     */
    public void checkUserLogin() {
        if (TextUtils.isEmpty(UserInfoCacheUtil.getUserToken(mContext))) {
            // 没有用户token
            // 跳转登录页面
            startActivity(new Intent(mContext, LoginActivity.class));
        } else {
            // 打开主页
            startActivity(new Intent(mContext, MainActivity.class));
        }
        finish();
    }

}
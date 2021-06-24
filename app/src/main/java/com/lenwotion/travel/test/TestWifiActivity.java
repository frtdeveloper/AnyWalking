package com.lenwotion.travel.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.WifiAdmin;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * APP主页
 */
public class TestWifiActivity extends BaseActivity {

    private static WifiAdmin mWifiAdmin;

    private ActivityBroadcastReceiver mActivityBroadcastReceiver;

    /**
     * 广播接收
     */
    private class ActivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {

                case "android.net.conn.CONNECTIVITY_CHANGE"://网络状态变化
                    int networkType = WifiAdmin.getNetworkType(mContext);
                    switch (networkType) {

                        case ConnectivityManager.TYPE_WIFI:
                            Log.v(GlobalConstants.LOG_TAG, "网络状态变化 TYPE_WIFI");
                            checkIsConnectTheWifi();
                            break;

                        default:

                            break;
                    }
                    break;

                default:
                    break;
            }
        }

    }

    static final int IS_HAVE_CONNECT_WIFI = 0;
    static final int IS_HAVE_NOT_CONNECT_WIFI = 1;

    private static ActivityHandler mActivityHandler;

    private static class ActivityHandler extends Handler {

        WeakReference<TestWifiActivity> weakActivity;

        public ActivityHandler(TestWifiActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);
            final TestWifiActivity activity = weakActivity.get();
            if (activity != null) {
                switch (message.what) {
                    case IS_HAVE_CONNECT_WIFI:
                        connectWifi();
                        break;

                    case IS_HAVE_NOT_CONNECT_WIFI:
                        searchConnectWifi();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_wifi);
        initData();
        initView();
        initResultView();
        initListener();
        initFunction();
    }

    private void initData() {
        mContext = this;
        mWifiAdmin = new WifiAdmin(mContext);
        mActivityHandler = new ActivityHandler(this);
        initFilter();
    }

    /**
     * 广播过滤设定
     */
    private void initFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态变化
        mActivityBroadcastReceiver = new ActivityBroadcastReceiver();
        mContext.registerReceiver(mActivityBroadcastReceiver, intentFilter);
    }

    private void initView() {

    }

    private void initResultView() {

    }

    private void initListener() {

    }

    private void initFunction() {
        searchConnectWifi();
    }

    private void openWifi() {
        if (!mWifiAdmin.isWifiEnabled()) {
            mWifiAdmin.openWifi();
        }
    }

    /**
     * 扫描WIFI
     */
    private static void searchConnectWifi() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mWifiAdmin.startScan();
                List<ScanResult> scanResultList = mWifiAdmin.getWifiList();
                Message message = mActivityHandler.obtainMessage();
                for (ScanResult scanResult : scanResultList) {
                    if (scanResult.SSID.equals(GlobalVariables.WIFI_AP_SSID)) {
                        message.what = IS_HAVE_CONNECT_WIFI;
                        Log.v(GlobalConstants.LOG_TAG, "IS_HAVE_CONNECT_WIFI");
                        mActivityHandler.handleMessage(message);
                        return;
                    }
                }
                message.what = IS_HAVE_NOT_CONNECT_WIFI;
                Log.v(GlobalConstants.LOG_TAG, "IS_HAVE_NOT_CONNECT_WIFI");
                mActivityHandler.handleMessage(message);
            }
        }).start();
    }

    private static void connectWifi() {
        boolean isConnect = mWifiAdmin.connectWifi(GlobalVariables.WIFI_AP_SSID, GlobalVariables.WIFI_AP_PASSWORD);
        Log.v(GlobalConstants.LOG_TAG, "isConnect:" + isConnect);
    }

    private void checkIsConnectTheWifi() {
        if (mWifiAdmin.getSSID().equals(GlobalVariables.WIFI_AP_SSID)) {
            Log.v(GlobalConstants.LOG_TAG, "checkIsConnectTheWifi true");
        } else {
            Log.v(GlobalConstants.LOG_TAG, "checkIsConnectTheWifi false");
        }
    }

    @Override
    protected void onDestroy() {
        removeBroadcastReceiver();
        removeWifi();
        super.onDestroy();
    }

    private void removeBroadcastReceiver() {
        if (mActivityBroadcastReceiver != null) {
            unregisterReceiver(mActivityBroadcastReceiver);
            mActivityBroadcastReceiver = null;
        }
    }

    private void removeWifi() {
        mWifiAdmin.removeNetwork(GlobalVariables.WIFI_AP_SSID);
        mWifiAdmin = null;
    }

}

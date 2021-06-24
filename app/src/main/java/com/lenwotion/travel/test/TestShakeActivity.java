package com.lenwotion.travel.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.bean.NettyCommunicationBean;
import com.lenwotion.travel.test.bean.GetWifiInfoBean;
import com.lenwotion.travel.test.business.GetWifiInfoBusiness;
import com.lenwotion.travel.test.interfaces.IGetWifiInfoCallback;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.netty.NettyClient;
import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;
import com.lenwotion.travel.utils.ShakeManager;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;
import com.lenwotion.travel.utils.WifiAdmin;

public class TestShakeActivity extends BaseActivity implements INettyMessageCallback, IGetWifiInfoCallback {

    private Context mContext;

    private WifiAdmin mWifiAdmin;
    /**
     * 页面运行标记
     */
    private boolean mIsActivityRunning = true;
    /**
     * 页面广播
     */
    private ActivityBroadcastReceiver mActivityBroadcastReceiver;

    private EditText mNameEt;
    private EditText mPasswordEt;
    private ImageView mDeviceConnectedIv;
    private EditText mImeiEt;
    private ProgressBar mRecallPb;
    /**
     * 摇动管理器
     */
    private ShakeManager mShakeManager;
    /**
     * 功能摇一摇振动器
     */
    private Vibrator mVibrator;

    private boolean mIsNettyConnect;

    /**
     * 广播接收器
     */
    public class ActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {

                case "android.net.conn.CONNECTIVITY_CHANGE"://网络状态变化
                    if (WifiAdmin.getNetworkType(mContext) == ConnectivityManager.TYPE_WIFI) {
                        connectNetty();//连接车载机
                    }
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_test);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        mWifiAdmin = new WifiAdmin(mContext);
        mShakeManager = new ShakeManager(this);
        // 获取Vibrator震动服务
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        registerReceiver();
    }

    /**
     * 注册页面监听广播
     */
    private void registerReceiver() {
        mActivityBroadcastReceiver = new ActivityBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态变化
        registerReceiver(mActivityBroadcastReceiver, intentFilter);
    }

    private void initView() {
        mNameEt = findViewById(R.id.et_shake_test_name);
        mPasswordEt = findViewById(R.id.et_shake_test_password);
        mDeviceConnectedIv = findViewById(R.id.iv_shake_test_device_connected);
        mDeviceConnectedIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectNetty();
            }
        });
        findViewById(R.id.bt_shake_test_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectWifi();
            }
        });
        mImeiEt = findViewById(R.id.et_shake_test_imei);
        //mImeiEt.setText("869966029687151");
        findViewById(R.id.bt_shake_test_get_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceInfo();
            }
        });
        mRecallPb = findViewById(R.id.pb_shake_test_recall);
        findViewById(R.id.bt_shake_test_recall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recall();
            }
        });
    }

    private void initListener() {
        mShakeManager.setonShakeListener(new ShakeManager.onShakeListener() {//摇一摇监听
            @Override
            public void shake() {//震动监听
                sendOrderToDevice();//发送播报命令
            }
        });
    }

    private void getDeviceInfo() {
        String imei = mImeiEt.getText().toString();
        GetWifiInfoBusiness business = new GetWifiInfoBusiness();
        business.getWifiInfo(mContext, imei, this);
    }

    @Override
    public void getWifiInfoSuccess(GetWifiInfoBean bean) {
        GlobalVariables.WIFI_AP_SSID = bean.getWifiName();
        GlobalVariables.WIFI_AP_PASSWORD = bean.getWifiPassword();
        mNameEt.setText(bean.getWifiName());
        mPasswordEt.setText(bean.getWifiPassword());
        ToastUtil.showToast(mContext, getString(R.string.success));
    }

    @Override
    public void getWifiInfoFail(String errMsg) {
        ToastUtil.showToast(mContext, errMsg);
    }

    private void connectWifi() {
        // 如果wifi没有打开，打开WiFi
        if (!mWifiAdmin.isWifiEnabled()) {
            mWifiAdmin.openWifi();
        }
        String name = mNameEt.getText().toString();
        String password = mPasswordEt.getText().toString();
        mWifiAdmin.connectWifi(name, password);
    }

    /**
     * 启动netty客户端连接
     */
    private void connectNetty() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new NettyClient(TestShakeActivity.this).run();
            }
        }).start();
    }

    @Override
    public void channelRead(String message) {

    }

    @Override
    public void channelActive() {
        if (mIsActivityRunning) {
            notifyClientConnected(true);
        }
    }

    /**
     * 有客户端连接刷新页面提示信息
     */
    private void notifyClientConnected(final boolean isConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    mIsNettyConnect = true;
                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.green));
                    // 连接上发送一次命令
                    sendOrderToDevice();
                } else { //断开连接
                    mIsNettyConnect = false;
                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.red));
                }
            }
        });
    }

    /**
     * 發送命令到车载机，发广播喇叭
     */
    private void sendOrderToDevice() {
        mVibrator.vibrate(200);//震动
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
                nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
                nettyCommunicationBean.setOperationCode(GlobalConstants.ORDER_LINE_BROADCAST);
                nettyCommunicationBean.setOperationData("no data");
                String order = new Gson().toJson(nettyCommunicationBean);
                NettyClient.sendOrder(order);
            }
        }).start();
    }

    @Override
    public void channelInactive() {
        notifyClientConnected(false);
    }

    /**
     * 重复播报
     */
    private void recall() {
        if (!mIsNettyConnect) {
            return;
        }
        findViewById(R.id.bt_shake_test_recall).setEnabled(false);
        mRecallPb.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsNettyConnect && mIsActivityRunning) {
                    sendOrderToDevice();
                    SystemClock.sleep(5000);
                }
            }
        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        //注册管理传感器
        mShakeManager.registerListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        //注销管理传感器
        mShakeManager.unregisterListener();
    }

    @Override
    public void onDestroy() {
        mIsActivityRunning = false;
        //注销候车页面监听广播
        if (mActivityBroadcastReceiver != null) {
            unregisterReceiver(mActivityBroadcastReceiver);
        }
        NettyClient.shutdownGracefully();
        //删除WIFI信息
        mWifiAdmin.removeNetwork(GlobalConstants.WIFI_START_SIGN);
        super.onDestroy();
    }

}

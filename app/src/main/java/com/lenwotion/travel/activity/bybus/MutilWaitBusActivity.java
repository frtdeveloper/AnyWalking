package com.lenwotion.travel.activity.bybus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
import com.lenwotion.travel.activity.bybus.presenter.WaitingBusPresenter;
import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;
import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.netty.NettyClient;
import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;
import com.lenwotion.travel.utils.ShakeManager;
import com.lenwotion.travel.utils.WifiAdmin;

import java.util.ArrayList;

public class MutilWaitBusActivity extends BaseActivity
                                  implements INettyMessageCallback,
                                             WaitingBusContract.ShowWaitResultView {

    private static final int MESSAGE_BEGIN_HANDLE = 0;
    private static final int MESSAGE_ONE_BUS_HANDLE = MESSAGE_BEGIN_HANDLE + 1;
    private static final int MESSAGE_END_HANDLE = MESSAGE_ONE_BUS_HANDLE + 1;


    private WifiAdmin mWifiAdmin;
    private ShakeManager mShakeManager;
    private Vibrator mVibrator;
    private ActivityBroadcastReceiver mActivityBroadcastReceiver;

    private WaitingBusPresenter mPresenter;

    private Button mCancelWaitingBt;
    private ImageView mDeviceConnectedIv;
    private ListView  mWaitBusesListView;

    private ArrayList<AffirmWaitInfoBean> mHandleBuesInfoBean;

    private boolean mIsActivityRunning;
    private boolean mIsNettyConnected;
    boolean mIsNettyConnecting;
    boolean mIsNotifyBusArrive;

    private Handler mBusUpdateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_BEGIN_HANDLE:
                    break;
                case MESSAGE_ONE_BUS_HANDLE:
                    break;
                case MESSAGE_END_HANDLE:
                    break;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.wait_bus_arrive_station));
        setContentView(R.layout.activity_mutil_wait_bus);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initData();
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActivityRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActivityRunning = false;
    }

    @Override
    public void showError(String errorMsg) {

    }

    @Override
    public void showUploadGPSData(UploadGPSBean data) {

    }

    @Override
    public void channelRead(String message) {

    }

    @Override
    public void channelActive() {

    }

    @Override
    public void channelInactive() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }

    private void initData(){
        mWifiAdmin = new WifiAdmin(this);
        mShakeManager = new ShakeManager(this);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mPresenter = new WaitingBusPresenter(this);
        registerReceiver();
    }

    private void registerReceiver() {
        mActivityBroadcastReceiver = new ActivityBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态变化
        registerReceiver(mActivityBroadcastReceiver, intentFilter);
    }

    private void initView() {
        mCancelWaitingBt = findViewById(R.id.m_wait_bus_connected_btn);
        mDeviceConnectedIv = findViewById(R.id.m_wait_bus_connected_iv);
        mWaitBusesListView = findViewById(R.id.m_wait_bus_list);
    }

    private void initListener() {
        mCancelWaitingBt.setOnClickListener(this);
        mShakeManager.setonShakeListener(new ShakeManager.onShakeListener() {//摇一摇监听
            @Override
            public void shake() {//震动监听
                //sendOrderToDevice();//发送播报命令
            }
        });
        mDeviceConnectedIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private boolean checkIsConnectDeviceWifi() {
        //是否连接上车载机WiFi
        return mWifiAdmin.getSSID().equals(GlobalVariables.WIFI_AP_SSID);
    }

    private void checkWifiRssi() {
        Log.v(GlobalConstants.LOG_TAG, "检查WIFI强度");
        new Thread(new Runnable() {

            //信号强度是否足够
            boolean rssiIsStrong = false;

            @Override
            public void run() {
                while (!rssiIsStrong && mIsActivityRunning) {
                    if (mWifiAdmin.getRssi() > -80) {
                        Log.v(GlobalConstants.LOG_TAG, "WIFI强度:" + mWifiAdmin.getRssi());
                        rssiIsStrong = true;
                        Log.v(GlobalConstants.LOG_TAG, "connectNetty");
                        connectNetty();
                    }
                    SystemClock.sleep(200);
                }
            }
        }).start();
    }

    private void connectNetty() {
        //防止网络状态回调过多netty多次连接
        if (!mIsNettyConnecting && !mIsNettyConnected && mIsActivityRunning) {//netty未开始连接且netty未连接
            mIsNettyConnecting = true;
            Log.v(GlobalConstants.LOG_TAG, "启动netty客户端连接");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new NettyClient(MutilWaitBusActivity.this).run();
                }
            }).start();
            //N秒后重置开始连接状态
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsNettyConnecting = false;
                        }
                    }, 500);
                }
            });
        }
    }



    private static String MY_INTENT = "com.lenwotion.travel.activity.bybus.START_M_WAIT_BUSES";
    private static ArrayList<AffirmWaitInfoBean> WAITING_BUSES_INFO;

    /**
     * 设置需要等待的线路信息
     * @param wait_bean_info
     * @param need_clean
     */
    public static void addWaittingBuses(AffirmWaitInfoBean wait_bean_info, boolean need_clean) {
        if(null == WAITING_BUSES_INFO) {
            WAITING_BUSES_INFO = new ArrayList<>();
        }

        if (need_clean){
            WAITING_BUSES_INFO.clear();
            WAITING_BUSES_INFO = null;
            WAITING_BUSES_INFO = new ArrayList<>();
        }

        WAITING_BUSES_INFO.add(wait_bean_info);
    }

    /**
     * 启动这个Activity
     * @param ctx
     * @param need_finish
     */
    public static void startMySelf(Activity ctx, boolean need_finish){
        Intent start_intent = new Intent();
        start_intent.setAction(MY_INTENT);
        start_intent.addCategory(Intent.CATEGORY_DEFAULT);
        ctx.startActivity(start_intent);

        if (need_finish)
            ctx.finish();
    }

    /**
     * 广播接收器
     */
    public class ActivityBroadcastReceiver extends BroadcastReceiver {

        boolean isPost;
        boolean isFirst = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {

                case "android.net.conn.CONNECTIVITY_CHANGE"://网络状态变化
                    if (WifiAdmin.getNetworkType(mContext) == ConnectivityManager.TYPE_WIFI) {
                        if (isFirst) {
                            isFirst = false;
                            return;
                        }
                        if (!isPost) {
                            isPost = true;
                            Log.v(GlobalConstants.LOG_TAG, "网络状态变化:已经是WIFI连接状态");
                            if (checkIsConnectDeviceWifi()) {
                                Log.v(GlobalConstants.LOG_TAG, "连接的是车载机WIFI");
                                checkWifiRssi();//连接车载机
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isPost = false;
                                }
                            }, 500);
                        }
                    }
                    break;

                default:
                    break;
            }
        }

    }
}
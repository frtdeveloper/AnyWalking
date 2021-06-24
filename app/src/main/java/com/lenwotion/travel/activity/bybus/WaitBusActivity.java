package com.lenwotion.travel.activity.bybus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.bybus.business.WaitBusBusiness;
import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
import com.lenwotion.travel.activity.bybus.presenter.WaitingBusPresenter;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.GeneralResponseBean;
import com.lenwotion.travel.bean.NettyCommunicationBean;
import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;
import com.lenwotion.travel.bean.bybus.ProcessWaitingVOBean;
import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.interfaces.IByBusService;
import com.lenwotion.travel.netty.NettyClient;
import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;
import com.lenwotion.travel.utils.ShakeManager;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;
import com.lenwotion.travel.utils.WifiAdmin;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 候车页面
 */
public class WaitBusActivity extends BaseActivity implements INettyMessageCallback, WaitingBusContract.ShowWaitResultView {

    /**
     * wifi 工具类
     */
    private WifiAdmin mWifiAdmin;
    /**
     * 页面广播
     */
    private ActivityBroadcastReceiver mActivityBroadcastReceiver;
    /**
     * 页面控件
     */
    private Button mCancelWaitingBt;
    private ImageView mDeviceConnectedIv;
    //private TextView mWaitInfoTv;
    private TextView mWaitBusName;
    private TextView mWaitBusInfo;
    //private TextView mWifiTv;
    /**
     * 候车的信息
     */
    private AffirmWaitInfoBean mAffirmInfoBean;
    /**
     * 摇动管理器
     */
    private ShakeManager mShakeManager;
    /**
     * 功能摇一摇振动器
     */
    private Vibrator mVibrator;
    /**
     * 页面运行标记
     */
    private boolean mIsActivityRunning = true;

    private WaitingBusPresenter mPresenter;
    /**
     * netty是否已连接
     */
    private boolean mIsNettyConnected;
    /**
     * netty是否连接中
     */
    boolean mIsNettyConnecting;
    /**
     * 是否已通知用户车辆到达
     */
    boolean mIsNotifyBusArrive;

    //private TextView mLogTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.wait_bus_arrive_station));
        setContentView(R.layout.activity_waiting_bus);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕保持唤醒
        initData();
        initView();
        initListener();
        initFunction();
    }

    private void initData() {
        mContext = this;
        mAffirmInfoBean = getIntent().getParcelableExtra("affirmInfoBean");
        mWifiAdmin = new WifiAdmin(mContext);
        mShakeManager = new ShakeManager(this);
        // 获取Vibrator震动服务
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        // 初始化presenter
        mPresenter = new WaitingBusPresenter(this);
        // 注册监听广播
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
        mCancelWaitingBt = findViewById(R.id.bt_waiting_bus_cancel_waiting);
        mDeviceConnectedIv = findViewById(R.id.iv_wait_bus_device_connected);
        //mWaitInfoTv = findViewById(R.id.tv_watiInfo);
        mWaitBusName = findViewById(R.id.tv_wait_bus_name);
        mWaitBusInfo = findViewById(R.id.tv_wait_bus_info);
        //mWifiTv = findViewById(R.id.tv_wifi);
        //mLogTv = findViewById(R.id.tv_log);
        //mLogTv.setMovementMethod(ScrollingMovementMethod.getInstance());
        //显示候车信息
        showWaitBusData(mAffirmInfoBean);
    }

    /**
     * 显示候车的信息
     */
    private void showWaitBusData(AffirmWaitInfoBean bean) {
        if (bean == null) {
            return;
        }
        // 显示候车信息
        String busName = bean.getLineName() + " 路车";
        mWaitBusName.setText(busName);
        //mWaitInfoTv.setText(bean.getLineName() + "正在开往本站");
        //mWaitBusInfo.setText("距离 " + bean.getDistance() + " 米");

        //更新WIFI信息
        GlobalVariables.WIFI_AP_SSID = bean.getSsid();
        GlobalVariables.WIFI_AP_PASSWORD = bean.getPassword();

        String busInfo = "";

        // 获取候车Wifi账号跟密码
        if (GlobalVariables.IS_TEST_FACTORY) {
            busInfo = "WIFI名:" + GlobalVariables.WIFI_AP_SSID + "\n" +
                    "WIFI密码:" + GlobalVariables.WIFI_AP_PASSWORD + "\n" +
                    "车牌号:" + bean.getPlateNumber() + "\n";
        }
        busInfo += "距离站台:" + bean.getDistance() + " 米" + "\n" +
                "当前到达: " + bean.getCurrentStation() + "\n";
        String frontStations;
        if (bean.getFrontStations() > 0) {
            frontStations = "距离你还有 " + bean.getFrontStations() + " 个站";
        } else if (bean.getFrontStations() == 0) {
            frontStations = "车辆已到站";
        } else if (bean.getFrontStations() == -999) {
            frontStations = "暂无到站信息";
        } else if (bean.getFrontStations() < 0) {
            frontStations = "车辆已过站，系统将重新预约车辆";
        } else {
            frontStations = "暂无到站信息";
        }
        busInfo += frontStations;
        mWaitBusInfo.setText(busInfo);
    }

    private void initListener() {
        mCancelWaitingBt.setOnClickListener(this);
        mShakeManager.setonShakeListener(new ShakeManager.onShakeListener() {//摇一摇监听
            @Override
            public void shake() {//震动监听
                sendOrderToDevice();//发送播报命令
            }
        });
        mDeviceConnectedIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NettyClient.shutdownGracefully();

//                if (!GlobalVariables.IS_TEST_FACTORY) {
//                    return;
//                }
//                //sendConnectInfo();
//                if (mIsNettyConnected) {
//                    mIsNettyConnected = false;
//                } else {
//                    mIsNettyConnected = true;
//                }
//                ToastUtil.showToast(mContext, "mIsNettyConnected:" + mIsNettyConnected);
            }
        });
    }

    //private long fTime;

    private void initFunction() {
        //fTime = System.currentTimeMillis();
        //检查当前网络
        checkCurrentNetwork();
        //上传当前经纬度
        uploadGPS();
    }

    /**
     * 检查当前网络
     */
    private void checkCurrentNetwork() {
        if (WifiAdmin.getNetworkType(mContext) == ConnectivityManager.TYPE_WIFI) {//当前是WIFI状态
            Log.v(GlobalConstants.LOG_TAG, "检查当前网络，当前是WIFI状态");
            if (checkIsConnectDeviceWifi()) {//连接的是车载机WIFI
                Log.v(GlobalConstants.LOG_TAG, "连接的是车载机WIFI");
                checkWifiRssi();//连接车载机
            } else {//连接的不是车载机
                Log.v(GlobalConstants.LOG_TAG, "连接的不是车载机");
                // 扫描WIFI
                scanWifi();
            }
        } else if (WifiAdmin.getNetworkType(mContext) == ConnectivityManager.TYPE_MOBILE) {//移动网络状态
            // 扫描WIFI
            Log.v(GlobalConstants.LOG_TAG, "检查当前网络，移动网络状态");
            scanWifi();
        } else {//网络异常
            Log.v(GlobalConstants.LOG_TAG, "检查当前网络，网络异常");
            //关闭页面
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(mContext, getString(R.string.http_error));
                }
            });
            //finish();
        }
    }

    /**
     * 判断连接的是车载机WIFI
     */
    private boolean checkIsConnectDeviceWifi() {
        //是否连接上车载机WiFi
        return mWifiAdmin.getSSID().equals(GlobalVariables.WIFI_AP_SSID);
    }

    /**
     * 扫描WIFI
     */
    private void scanWifi() {
        Log.v(GlobalConstants.LOG_TAG, "start scanWifi");
        new Thread(new Runnable() {

            boolean isFindWifi;//是否已经找到目标WIFI信号

            @Override
            public void run() {
                while (!isFindWifi && mIsActivityRunning) {//找到则停止
                    //如果wifi没有打开，打开Wifi
                    if (!mWifiAdmin.isWifiEnabled()) {
                        Log.v(GlobalConstants.LOG_TAG, "wifi is close,openWifi");
                        mWifiAdmin.openWifi();
                    }

                    //开始扫描
                    Log.v(GlobalConstants.LOG_TAG, "开始扫描");
                    mWifiAdmin.startScan();

                    //扫描结果处理
                    List<ScanResult> scanResultList = mWifiAdmin.getWifiList();
                    for (ScanResult scanResult : scanResultList) {
                        if (scanResult.SSID.equals(GlobalVariables.WIFI_AP_SSID)) {//如果扫描到的SSID有目标车辆SSID
                            Log.v(GlobalConstants.LOG_TAG, "扫描到的SSID有目标车辆SSID");
                            isFindWifi = true;//停止扫描
                            //开始连接车载机WIFI
                            connectWifi();
                        }
                    }
                    SystemClock.sleep(500);
                }
            }
        }).start();
    }

    /**
     * 连接wifi
     */
    private void connectWifi() {
        Log.v(GlobalConstants.LOG_TAG, "连接wifi");
        new Thread(new Runnable() {

            boolean isConnectWifi;

            @Override
            public void run() {
                while (!isConnectWifi && mIsActivityRunning) {
                    isConnectWifi = mWifiAdmin.connectWifi(GlobalVariables.WIFI_AP_SSID, GlobalVariables.WIFI_AP_PASSWORD);//连接结果在广播监听回调
                    Log.v(GlobalConstants.LOG_TAG, "连接wifi:" + isConnectWifi);
                    SystemClock.sleep(200);
                }
            }
        }).start();
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

    /**
     * 检查WIFI强度
     */
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

    /**
     * 启动netty客户端连接
     */
    private void connectNetty() {
        //防止网络状态回调过多netty多次连接
        if (!mIsNettyConnecting && !mIsNettyConnected && mIsActivityRunning) {//netty未开始连接且netty未连接
            mIsNettyConnecting = true;
            Log.v(GlobalConstants.LOG_TAG, "启动netty客户端连接");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new NettyClient(WaitBusActivity.this).run();
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

    @Override
    public void channelRead(final String message) {

    }

    /**
     * netty连接上
     */
    @Override
    public void channelActive() {
        Log.v(GlobalConstants.LOG_TAG, "netty连接上channelActive");
        if (mIsActivityRunning) {
            notifyClientConnected(true);
            sendConnectInfo();
        }
    }

    /**
     * 上传WIFI连接信息
     */
    private void sendConnectInfo() {
        WaitBusBusiness business = new WaitBusBusiness();
        business.sendConnectInfo(mContext, GlobalVariables.STATION_INFO_BEAN.getStation(), mWifiAdmin.getSSID());
    }

    /**
     * netty断开连接
     */
    @Override
    public void channelInactive() {
        Log.v(GlobalConstants.LOG_TAG, "netty断开连接channelInactive");
        if (mIsActivityRunning) {
            notifyClientConnected(false);
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
//                    long aTime = System.currentTimeMillis();
//                    long cTime = aTime - fTime;
                    //cTime = cTime / 1000;
                    //ToastUtil.showToast(mContext, "连接时间:" + cTime + "毫秒");
                    mIsNettyConnected = true;//netty连接状态标记
                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.green));
                    mDeviceConnectedIv.setContentDescription("设备已连接");
                    // 连接上发送一次命令
                    sendOrderToDevice();
                    // 连接上发送用户候车信息
                    sendUserInfo();
                } else { //断开连接
                    mIsNettyConnected = false;//netty连接状态标记
                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.red));
                    mDeviceConnectedIv.setContentDescription("设备未连接");
                    mVibrator.vibrate(500);
                    // netty断开重连
                    scanWifi();
                }
            }
        });
    }

    /**
     * 發送命令到车载机，发广播喇叭
     */
    private void sendOrderToDevice() {
        if (!mIsNettyConnected) {
            return;
        }
        mVibrator.vibrate(200);//震动
        Log.v(GlobalConstants.LOG_TAG, "發送广播命令到车载机");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
                nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
                nettyCommunicationBean.setOperationCode(GlobalConstants.ORDER_LINE_BROADCAST);
                nettyCommunicationBean.setOperationData("no data");
                String order = new Gson().toJson(nettyCommunicationBean);
                NettyClient.sendOrder(order);
                Log.v(GlobalConstants.LOG_TAG, order);
            }
        }).start();
    }

    /**
     * 发送用户候车信息
     */
    private void sendUserInfo() {
        if (!mIsNettyConnected) {
            return;
        }
        Log.v(GlobalConstants.LOG_TAG, "发送用户候车信息到车载机");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
                nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
                nettyCommunicationBean.setOperationCode(GlobalConstants.ORDER_SEND_USER_INFO);
                nettyCommunicationBean.setOperationData(GlobalVariables.STATION_INFO_BEAN.getStation());
                String order = new Gson().toJson(nettyCommunicationBean);
                NettyClient.sendOrder(order);
                Log.v(GlobalConstants.LOG_TAG, order);
            }
        }).start();
    }

    /**
     * 每隔N秒上传当前GPS
     */
    private void uploadGPS() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsActivityRunning) {
                    if (UserInfoCacheUtil.getUserToken(mContext).isEmpty()) {
                        Log.v(GlobalConstants.LOG_TAG, "uploadGPS:当前用户不存在");
                        return;
                    }
                    if (mAffirmInfoBean == null) {
                        return;
                    }
                    mPresenter.requestRealTimeLatLng(mContext, UserInfoCacheUtil.getUserToken(mContext),
                            mIsNettyConnected, mAffirmInfoBean.getImei());
                    SystemClock.sleep(2000);
                }
            }
        }).start();
    }

    @Override
    public void showError(String errorMsg) {
        Log.v(GlobalConstants.LOG_TAG, "uploadGPS:" + errorMsg);
    }

    @Override
    public void showUploadGPSData(UploadGPSBean data) {
        ProcessWaitingVOBean voBean = data.getData();
        //Log.v(GlobalConstant.LOG_TAG, "候车信息:" + voBean.toString());
        switch (voBean.getCode()) {

            case ProcessWaitingVOBean.CODE_WAITTING_BUS: //200.正常等车，更新候车信息
                //Log.v(GlobalConstants.LOG_TAG, "正常等车，更新候车信息:" + voBean.getDistance());
                mAffirmInfoBean = voBean.getAffirm();
                showWaitBusData(mAffirmInfoBean);
                //车辆距离小于N米,未通知过的情况下,震动提醒
                if (!mIsNotifyBusArrive && voBean.getDistance() < 200) {
                    mIsNotifyBusArrive = true;
                    mVibrator.vibrate(1000);
                    ToastUtil.showToast(mContext, getString(R.string.bus_will_arrive_station));
                }
                //因为有可能会安排新的车辆，需要再次提醒
                //如果震动提醒过，那么重置提醒状态/WIFI连接功能
                if (mIsNotifyBusArrive && voBean.getDistance() > 300) {
                    mIsNotifyBusArrive = false;
                    //重新开始WIFI连接功能逻辑
                    checkCurrentNetwork();
                }
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_201: //201.用户远离站台
                Log.v(GlobalConstants.LOG_TAG, "用户远离站台");
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_201);
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_202: //202.车过了用户重新叫车:车过了，用户还在等,后台重新分配车辆，继续进入候车界面
                Log.v(GlobalConstants.LOG_TAG, "车过了用户重新叫车");
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_202);
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_203: //203.用户等待超过15分钟重新叫车
                Log.v(GlobalConstants.LOG_TAG, "用户等待超过15分钟重新叫车");
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_203);
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_204: //204.用户/系统异常
                Log.v(GlobalConstants.LOG_TAG, "用户/系统异常");
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_204);
                break;

            case ProcessWaitingVOBean.CODE_NETTY_CONNECT: //302.WIFI连接中，需求要求不做任何操作
                //Log.v(GlobalConstants.LOG_TAG, "WIFI连接中，需求要求不做任何操作");
                mWaitBusInfo.setText(getString(R.string.bus_arrive_station));
                break;

            default:
                break;
        }
    }

    /**
     * 候车状态改变
     */
    public void waitingStateChange(int code) {
        if (!mIsActivityRunning) {
            return;
        }
        Log.v(GlobalConstants.LOG_TAG, "waitingStateChange");
        Intent intent = new Intent(mContext, WaitBusStateActivity.class);
        intent.putExtra("module", "wait");
        intent.putExtra("operateCode", code);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_waiting_bus_cancel_waiting:
                cancelWaitBus();
                break;
        }
    }

    /**
     * 停止候车，关闭页面
     */
    private void cancelWaitBus() {
        new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.confirm_cancel_wait_bus))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendCancelInfo();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    /**
     * 发送停止候车
     */
    private void sendCancelInfo() {
        Log.v(GlobalConstants.LOG_TAG, "发送停止候车");
        IByBusService byBusService = AnyWalkingApplication.getInstance().getRetrofit().create(IByBusService.class);
        Call<GeneralResponseBean> call = byBusService.cancelWaitingBus(UserInfoCacheUtil.getUserToken(mContext));
        call.enqueue(new Callback<GeneralResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBean> call, @NonNull Response<GeneralResponseBean> response) {
                if (response.isSuccessful()) {
                    GeneralResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            sendCancelInfoSuccess();
                        } else {
                            sendCancelInfoFail(bean.getMsg());
                        }
                    } else {
                        sendCancelInfoFail(getString(R.string.http_error));
                    }
                } else {
                    sendCancelInfoFail(getString(R.string.http_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeneralResponseBean> call, @NonNull Throwable throwable) {
                sendCancelInfoFail(getString(R.string.http_error));
            }
        });
    }

    /**
     * 发送取消候车成功
     */
    private void sendCancelInfoSuccess() {
        Log.v(GlobalConstants.LOG_TAG, "取消候车成功");
    }

    /**
     * 发送取消候车失败
     */
    private void sendCancelInfoFail(String errMsg) {
        Log.v(GlobalConstants.LOG_TAG, "取消候车:" + errMsg);
    }

    @Override
    public void onBackPressed() {
        cancelWaitBus();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        //注册管理传感器
        mShakeManager.registerListener();
    }

    /**
     * 方法必须重写
     */
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
        Log.v(GlobalConstants.LOG_TAG, "候车页面关闭");
        super.onDestroy();
    }

}

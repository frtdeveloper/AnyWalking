//package com.lenwotion.travel.activity.bybus;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.media.MediaPlayer;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.wifi.ScanResult;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Vibrator;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.amap.api.location.AMapLocation;
//import com.google.gson.Gson;
//import com.lenwotion.travel.R;
//import com.lenwotion.travel.activity.BaseActivity;
//import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
//import com.lenwotion.travel.activity.bybus.presenter.WaitingBusPresenter;
//import com.lenwotion.travel.bean.NettyCommunicationBean;
//import com.lenwotion.travel.bean.NettyUserInfoBean;
//import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;
//import com.lenwotion.travel.bean.bybus.ProcessWaitingVOBean;
//import com.lenwotion.travel.bean.bybus.StationInfoBean;
//import com.lenwotion.travel.bean.bybus.UploadGPSBean;
//import com.lenwotion.travel.global.GlobalConstant;
//import com.lenwotion.travel.global.GlobalVariable;
//import com.lenwotion.travel.netty.NettyClient;
//import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;
//import com.lenwotion.travel.utils.CommUtil;
//import com.lenwotion.travel.utils.ShakeManager;
//import com.lenwotion.travel.utils.ToastUtil;
//import com.lenwotion.travel.utils.UserInfoCacheUtil;
//import com.lenwotion.travel.utils.WifiAdmin;
//import com.lenwotion.travel.view.ReturnDialog;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * 等候班车到站
// */
//public class WaitingBusActivity extends BaseActivity implements WaitingBusContract.ShowWaitResultView, INettyMessageCallback {
//
//    /**
//     * 取消坐车按钮
//     */
//    private Button mCancelWaitingBt;
//    /**
//     * 车载机WIFI是否连接提示ImageView
//     */
//    private ImageView mDeviceConnectedIv;
//    /**
//     * 候车信息控件
//     */
//    private TextView mWaitInfoTv;
//    private TextView mWaitBusName;
//    private TextView mWaitBusLineDistance;
//    private TextView mWifiTv;
//    /**
//     * 功能摇一摇控件
//     */
////    private RelativeLayout mShakeRl;
////    private LinearLayout mTopLayout;
////    private LinearLayout mBottomLayout;
////    private ImageView mTopLine;
////    private ImageView mBottomLine;
//    /**
//     * 功能摇一摇振动器
//     */
//    private Vibrator mVibrator;
//    /**
//     * 页面广播
//     */
//    private ActivityBroadcastReceiver mActivityBroadcastReceiver;
//    /**
//     * wifi 工具类
//     */
//    private WifiAdmin mWifiAdmin;
//    /**
//     * 声音播放器
//     */
//    private MediaPlayer mMediaPlayer;
//    /**
//     * 候车的信息
//     */
//    private AffirmWaitInfoBean mAffirmInfoBean;
//
//    private WaitingBusPresenter mPresenter;
//    /**
//     * 是否和车载机wifi ap连接上
//     */
//    private boolean mIsDeviceApConnected = false;
//    /**
//     * 是否和车载机netty连接上
//     */
//    private boolean mIsDeviceNettyConnected = false;
//    /**
//     * 开始开始连接车载机wifi状态标记
//     */
//    boolean mIsStartConnectAp = false;
//    /**
//     * 页面运行标记
//     */
//    boolean mIsActivityRunning = true;
//    /**
//     * 摇动管理器
//     */
//    private ShakeManager mShakeManager;
//    /**
//     * 定时上传GPS
//     */
//    private Timer mUploadGpsTimer;
//
//    // private DeferWorkHandler mDeferHandler = new DeferWorkHandler(this);
//
////    private static class DeferWorkHandler extends Handler {
////
////        private WeakReference<WaitingBusActivity> mActivityReference;
////        private WaitingBusActivity mActivity;
////
////        public DeferWorkHandler(WaitingBusActivity activity) {
////            mActivityReference = new WeakReference<>(activity);
////        }
////
////        @Override
////        public void handleMessage(Message msg) {
////            super.handleMessage(msg);
////            if (mActivityReference != null && mActivityReference.get() != null) {
////                mActivity = mActivityReference.get();
////                switch (msg.what) {
////
////                    case GlobalConstant.START_SHAKE://开始震动
////                        mActivity.mShakeRl.setVisibility(View.VISIBLE);
////                        mActivity.mVibrator.vibrate(200);
////                        //发出提示音
////                        mActivity.mTopLine.setVisibility(View.VISIBLE);
////                        mActivity.mBottomLine.setVisibility(View.VISIBLE);
////                        // mActivity.startAnimation(false);
////                        break;
////
////                    case GlobalConstant.AGAIN_SHAKE://再次震动
////                        mActivity.mVibrator.vibrate(200);
////                        break;
////
////                    case GlobalConstant.END_SHAKE://震动效果结束
////                        //震动效果结束, 将震动设置为false,开始搜索候车的Wifi，如果连接上，就发送命令
////                        // mActivity.startAnimation(true);
////                        break;
////
////                    default:
////                        break;
////                }
////            }
////        }
////    }
//
//    public class ActivityBroadcastReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction() == null) {
//                return;
//            }
//            switch (intent.getAction()) {
//                case WifiManager.NETWORK_STATE_CHANGED_ACTION://监听wifi的连接状态即是否连上了一个有效网络
//                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
//                        if (mIsDeviceApConnected) {
//                            // 是原来就连接上过的
//                            // 重新开始连接
//                            mIsDeviceApConnected = false;
//                            // 开始连接设备
//                            Log.v(GlobalConstant.LOG_TAG, "原来就连接上过的,开始连接设备");
//                            judgeCurrentWifiStatus();
//                        } else {
//                            // 原来没有连接过的
//                            if (!mIsStartConnectAp) {
//                                mIsStartConnectAp = true;
//                                // 开始连接设备
//                                Log.v(GlobalConstant.LOG_TAG, "原来没有连接过的,开始连接设备");
//                                judgeCurrentWifiStatus();
//                            }
//                        }
//                    } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
//                        // 判断当前WiFi状态
//                        Log.v(GlobalConstant.LOG_TAG, "判断当前WiFi状态");
//                        judgeCurrentWifiStatus();
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_waiting_bus);
//        initData();
//        initView();
//        initListener();
//        initFunction();
//    }
//
//    private void initData() {
//        mContext = this;
//
//        mWifiAdmin = new WifiAdmin(mContext);
//        mWifiAdmin.closeWifi();
//
//        mMediaPlayer = MediaPlayer.create(mContext, R.raw.winxp);
//        // 初始化presenter
//        mPresenter = new WaitingBusPresenter(this);
//
//        mAffirmInfoBean = getIntent().getParcelableExtra("affirmInfoBean");
//        // 注册监听广播
//        registerReceiver();
//        // 初始化振动器
//        initVibrator();
//        //推送候车信息
////        if (null != getIntent()) {
////            Bundle bundle = getIntent().getExtras();
////            if (bundle != null) {
////                mAffirmInfoBean = bundle.getParcelable("affirmInfo");
////            }
////        }
//    }
//
//    private void initView() {
//        mCancelWaitingBt = (Button) findViewById(R.id.bt_waiting_bus_cancel_waiting);
//        mDeviceConnectedIv = (ImageView) findViewById(R.id.iv_wait_bus_device_connected);
//        mWaitInfoTv = (TextView) findViewById(R.id.tv_watiInfo);
//        mWaitBusName = (TextView) findViewById(R.id.tv_wait_bus_name);
//        mWaitBusLineDistance = (TextView) findViewById(R.id.tv_wait_bus_distance);
//        mWifiTv = (TextView) findViewById(R.id.tv_wifi);
//        //摇一摇控件
////        mShakeRl = (RelativeLayout) findViewById(R.id.rl_shake);
////        mTopLayout = (LinearLayout) findViewById(R.id.main_linear_top);
////        mBottomLayout = ((LinearLayout) findViewById(R.id.main_linear_bottom));
////        mTopLine = (ImageView) findViewById(R.id.main_shake_top_line);
////        mBottomLine = (ImageView) findViewById(R.id.main_shake_bottom_line);
////        mTopLine.setVisibility(View.GONE);
////        mBottomLine.setVisibility(View.GONE);
//        //显示候车信息
//        showWaitBusData(mAffirmInfoBean);
//    }
//
//    private void initListener() {
//        mCancelWaitingBt.setOnClickListener(this);
//    }
//
//    private void initFunction() {
//        // 判断当前WiFi状态
//        judgeCurrentWifiStatus();
//        // 开始对比用户当前位置是否远离站台
//        compareUserLocation();
//        //上传当前经纬度
//        uploadGPS();
//    }
//
//    /**
//     * 判断当前WiFi状态
//     */
//    private void judgeCurrentWifiStatus() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (!mWifiAdmin.isWifiEnabled()) {
//                    mWifiAdmin.openWifi();
//                }
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                // 判断是否已经连接WiFi
//                if (mWifiAdmin.isWifiEnabled()) {
//                    // WiFi已经连接
//                    // 判断是否车载机WiFi
//                    if (isConnectedDeviceAp()) {
//                        // 是车载机WiFi
//                        mIsDeviceApConnected = true;
//                        Log.v(GlobalConstant.LOG_TAG, "是车载机WiFi");
//                        // 判断是否连接上netty
//                        if (mIsDeviceNettyConnected) {
//                            // 已经连接netty
//                            Log.v(GlobalConstant.LOG_TAG, "已经连接netty");
//                        } else {
//                            // 未连接上netty,则连接netty
//                            Log.v(GlobalConstant.LOG_TAG, "未连接上netty,则连接netty");
//                            connectNettyClient();
//                        }
//                    } else {
//                        // 不是车载机WiFi,扫描是否有车载机WiFi
//                        Log.v(GlobalConstant.LOG_TAG, "不是车载机WiFi,扫描是否有车载机WiFi");
//                        startScan();
//                    }
//                } else {
//                    // WiFi未连接,打开WiFi
//                    Log.v(GlobalConstant.LOG_TAG, "WiFi未连接,打开WiFi");
//                    mWifiAdmin.openWifi();
//                    // 扫描是否有车载机WiFi
//                    Log.v(GlobalConstant.LOG_TAG, "扫描是否有车载机WiFi");
//                    startScan();
//                }
//            }
//        }).start();
//    }
//
//    /**
//     * 没有连接上车载机wifi，扫描是否有车载机WiFi
//     */
//    private void startScan() {
//        Log.v(GlobalConstant.LOG_TAG, "扫描是否有车载机WiFi");
//        mWifiAdmin.startScan();
//        // 扫描结果
//        List<ScanResult> wifiList = mWifiAdmin.getWifiList();
//        // 扫描ssid结果
//        ArrayList<String> wifiSSIDList = new ArrayList<>();
//        if (wifiList != null && !wifiList.isEmpty()) {
//            for (ScanResult scanResult : wifiList) {
//                wifiSSIDList.add(scanResult.SSID);
//            }
//            // 判断ssid列表是否包含车载机wifi
//            if (wifiSSIDList.contains(WifiAdmin.WIFI_AP_SSID)) {
//                // 包含，开始连接
//                connectDeviceAp();
//            } else {
//                // 没有就马上再次扫描
//                judgeCurrentWifiStatus();
//            }
//        } else {
//            // 没有就马上再次扫描
//            judgeCurrentWifiStatus();
//        }
//    }
//
//    /**
//     * 开始连接车载机wifi
//     */
//    private void connectDeviceAp() {
//        mWifiAdmin.connectWifi(WifiAdmin.WIFI_AP_SSID, WifiAdmin.WIFI_AP_PASSWORD);
//        if (!mIsDeviceNettyConnected) {
//            // wifi ap 连接完成
//            mIsStartConnectAp = false;
//            connectNettyClient();
//        }
//    }
//
//    /**
//     * 判断连接的wifi是否指定的车载机WiFi
//     */
//    private boolean isConnectedDeviceAp() {
//        return WifiAdmin.WIFI_AP_SSID.equals(mWifiAdmin.getSSID());
//    }
//
//    /**
//     * netty客户端连接
//     */
//    private void connectNettyClient() {
//        Log.v(GlobalConstant.LOG_TAG, "connectNettyClient");
//        if (!mIsDeviceNettyConnected) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    // 启动netty客户端连接
//                    new NettyClient(WaitingBusActivity.this).run();
//                }
//            }).start();
//        }
//    }
//
//    @Override
//    public void channelRead(String message) {
//
//    }
//
//    @Override
//    public void channelActive() {
//        if (mIsActivityRunning) {
//            notifyClientConnected(true);
//        }
//    }
//
//    @Override
//    public void channelInactive() {
//        if (mIsActivityRunning) {
//            notifyClientConnected(false);
//        }
//    }
//
//    /**
//     * 有客户端连接刷新页面提示信息
//     */
//    private void notifyClientConnected(final boolean isConnected) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (isConnected) {
//                    mIsDeviceNettyConnected = true;
//                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.green));
//                    mVibrator.vibrate(200);
//                    if (mSendUserInfoTimer != null) {
//                        mSendUserInfoTimer.start();
//                    }
//                } else { //断开连接
//                    mIsDeviceNettyConnected = false;
//                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.red));
//                    mVibrator.vibrate(1000);
//                    // 重新连接
//                    connectNettyClient();
//                }
//            }
//        });
//    }
//
//    /**
//     * 注册页面监听广播
//     */
//    private void registerReceiver() {
//        mActivityBroadcastReceiver = new ActivityBroadcastReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        // 监听WIFI网络的连接状态
//        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
////        // 这个监听wifi的打开与关闭，与wifi的连接无关
////        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
////        // 这个是监听网络状态的，包括了wifi和移动网络。
////        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
////        // 注册接收定位改变的广播，刷新页面（广播来自MainActivity）
////        intentFilter.addAction(GlobalConstant.INTENT_MAP_LOCATION_CHANGED);
//        registerReceiver(mActivityBroadcastReceiver, intentFilter);
//    }
//
//    private void initVibrator() {
//        //获取Vibrator震动服务
//        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        mShakeManager = new ShakeManager(this);
//        mShakeManager.setonShakeListener(new ShakeManager.onShakeListener() {
//            @Override
//            public void shake() {
//                if (mIsDeviceNettyConnected) {
//                    sendOrderToDevice();
//                    mVibrator.vibrate(200);
//                } else {
//                    if (mMediaPlayer != null) {
//                        mMediaPlayer.start();
//                    }
//                    mVibrator.vibrate(1000);
//                }
//
////                Thread thread = new Thread() {
////                    @Override
////                    public void run() {
////                        super.run();
////                        try {
////                            //开始震动 发出提示音 展示动画效果
////                            mDeferHandler.obtainMessage(GlobalConstant.START_SHAKE).sendToTarget();
////                            Thread.sleep(200);
////                            //再来一次震动提示
////                            mDeferHandler.obtainMessage(GlobalConstant.AGAIN_SHAKE).sendToTarget();
////                            Thread.sleep(200);
////                            mDeferHandler.obtainMessage(GlobalConstant.END_SHAKE).sendToTarget();
////                        } catch (InterruptedException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                };
////                thread.start();
//            }
//        });
//    }
//
//    /**
//     * 显示候车的信息
//     */
//    private void showWaitBusData(AffirmWaitInfoBean bean) {
//        //显示候车信息
//        if (bean != null) {
//            mWaitBusName.setText(bean.getLineName());
//            mWaitBusLineDistance.setText("距离您" + bean.getDistance() + "米");
//            mWaitInfoTv.setText("预约成功，" + bean.getLineName() + "正在开往本站");
//            //获取候车Wifi账号跟密码
//            mWifiTv.setText("wifi名称==" + WifiAdmin.WIFI_AP_SSID + "\n" + "wifi密码==" + WifiAdmin.WIFI_AP_PASSWORD + "\n" + "车牌号==" + bean.getPlateNumber());
//        } else {  //如果为空，默认信息
//            mWaitBusName.setText("M382路");
//            mWaitBusLineDistance.setText("开往 " + "恒生医院" + " 方向");
//            mWifiTv.setText("wifi名称==" + WifiAdmin.WIFI_AP_SSID + "\n" + "wifi密码==" + WifiAdmin.WIFI_AP_PASSWORD);
//        }
//    }
//
//    @Override
//    public void showError(String errorMsg) {
//        ToastUtil.showToast(mContext, "error==" + errorMsg);
//    }
//
//    /**
//     * 上传GPS，返回数据业务
//     */
//    @Override
//    public void showUploadGPSData(UploadGPSBean data) {
//        ProcessWaitingVOBean voBean = data.getData();
//        //根据code判断
//        if (voBean == null) {
//            return;
//        }
//        int code = voBean.getCode();
//        switch (code) {
//
//            case 200: //正常等车，更新候车信息
//                mWaitBusLineDistance.setText("距离您" + voBean.getDistance() + "米" + "\n" + " 状态码：" + code);
//                break;
//
//            case 201: //用户远离站台
//                WaitBusStateActivity.luncher((Activity) mContext, GlobalConstant.WAITING_BUS_DEVIATION_STATION);
//                finish();
//                break;
//
//            case 202: //用户状态异常重新叫车:车过了，用户还在等,后台重新分配车辆，继续进入候车界面
//                AffirmWaitInfoBean infoBean = voBean.getAffirm();
//                Intent intent = new Intent(mContext, WaitingBusActivity.class);
//                intent.putExtra("affirmInfoBean", infoBean);
//                startActivity(intent);
//                finish();
//                break;
//
//            case 203: //用户等待超过15分钟重新叫车
//                WaitBusStateActivity.luncher((Activity) mContext, GlobalConstant.WAITING_BUS_NOT_CALLBACK);
//                finish();
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.bt_waiting_bus_cancel_waiting:
//                cancelWaitBus();
//                break;
//        }
//    }
//
//    /**
//     * 每隔3秒上传当前GPS
//     */
//    private void uploadGPS() {
//        mUploadGpsTimer = new Timer();
//        mUploadGpsTimer.schedule(mUploadGpsTask, 0, 3 * 1000);
//    }
//
//    /**
//     * 上传传当前GPS任务
//     */
//    private TimerTask mUploadGpsTask = new TimerTask() {
//        @Override
//        public void run() {
//            if (UserInfoCacheUtil.getUserToken(mContext).isEmpty()) {
//                ToastUtil.showToast(mContext, "当前用户不存在");
//                return;
//            }
//            mPresenter.requestRealTimeLatLng(UserInfoCacheUtil.getUserToken(mContext));
//        }
//    };
//
//    /**
//     * 每隔10秒对比用户位置是否偏移远离站台
//     */
//    private void compareUserLocation() {
//        // 当前位置经纬度
//        AMapLocation location = GlobalVariable.A_MAP_LOCATION;
//        // 所选站台经纬度
//        StationInfoBean stationInfoBean = GlobalVariable.STATION_INFO_BEAN;
//        if (location == null || stationInfoBean == null) {
//            ToastUtil.showToast(mContext, R.string.no_location);
//            return;
//        }
//        double lat = Double.parseDouble(stationInfoBean.getLat());
//        double lng = Double.parseDouble(stationInfoBean.getLng());
//
//        //对比两个经纬度差，偏移大于N米，弹出提示框已偏移站台
//        int distance = CommUtil.getDistance(location.getLatitude(), location.getLongitude(), lat, lng);
//        if (distance > 100) {
//            Intent intentExceed = new Intent(mContext, WaitBusStateActivity.class);
//            intentExceed.putExtra("operateCode", GlobalConstant.WAITING_BUS_DEVIATION_STATION);
//            startActivity(intentExceed);
//            finish();
//        }
//    }
//
//    /**
//     * 發送命令到车载机，发广播喇叭
//     */
//    private void sendOrderToDevice() {
//        NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
//        nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
//        nettyCommunicationBean.setOperationCode(GlobalConstant.ORDER_LINE_BROADCAST);
//        nettyCommunicationBean.setOperationData("");
//        String order = new Gson().toJson(nettyCommunicationBean);
//        NettyClient.sendOrder(order);
//        //mShakeRl.setVisibility(View.GONE);
//    }
//
//    /**
//     * 连上netty的时候，每隔5秒，发送一次用户候车信息
//     */
//    private CountDownTimer mSendUserInfoTimer = new CountDownTimer(20 * 1000, 5 * 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
//            sendUserInfo();
//        }
//
//        @Override
//        public void onFinish() {
//
//        }
//    };
//
//    /**
//     * 发送用户候车信息
//     */
//    private void sendUserInfo() {
//        NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
//        nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
//        nettyCommunicationBean.setOperationCode(GlobalConstant.ORDER_SEND_USER_INFO);
//        NettyUserInfoBean infoBean = new NettyUserInfoBean();
//        infoBean.setStationName("noStation");
//        String data = new Gson().toJson(infoBean);
//        nettyCommunicationBean.setOperationData(data);
//        String order = new Gson().toJson(nettyCommunicationBean);
//        NettyClient.sendOrder(order);
//    }
//
////    /**
////     * 开启 摇一摇动画
////     *
////     * @param isBack 是否是返回初识状态
////     */
////    private void startAnimation(boolean isBack) {
////        ShakeAnimationUtil.getInstance().setBack(isBack);
////        //上面图片的动画效果
////        TranslateAnimation topAnim = ShakeAnimationUtil.getInstance().getUpAnim();
////        TranslateAnimation bottomAnim = ShakeAnimationUtil.getInstance().getDownAnim(new ShakeAnimationUtil.onAnimationCompleteListener() {
////            @Override
////            public void onAnimEnd() {
////                //当动画结束后 , 将中间两条横线GONE掉, 不让其占位
////                mTopLine.setVisibility(View.GONE);
////                mBottomLine.setVisibility(View.GONE);
////                // 发送命令到车载机，发广播喇叭
////                sendOrderToDevice();
////                if (mIsDeviceNettyConnected) {
////                    ToastUtil.showToast(mContext, "已连接上");
////                } else {
////                    if (mMediaPlayer != null) {
////                        mMediaPlayer.start();
////                    }
////                }
////            }
////        });
////        //设置动画
////        mTopLayout.startAnimation(topAnim);
////        mBottomLayout.startAnimation(bottomAnim);
////    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        //注册管理传感器
//        mShakeManager.registerListener();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        //注销管理传感器
//        mShakeManager.unregisterListener();
//    }
//
////    public static void luncher(Activity activity, AffirmWaitInfoBean affirmInfoBean) {
////        Intent mIntent = new Intent(activity, WaitingBusActivity.class);
////        Bundle bundle = new Bundle();
////        bundle.putParcelable("affirmInfo", affirmInfoBean);
////        mIntent.putExtras(bundle);
////        activity.startActivity(mIntent);
////    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onDestroy() {
//        mIsActivityRunning = false;
//        //返回主页,需要全部退出已打开Activity
//        // finishActivity(new Class[]{SelectStationActivity.class, SelectLineActivity.class, WaitingBusActivity.class});
//        //注销候车页面监听广播
//        if (mActivityBroadcastReceiver != null) {
//            unregisterReceiver(mActivityBroadcastReceiver);
//        }
//        NettyClient.shutdownGracefully();
////        if (mDeferHandler != null) {
////            mDeferHandler.removeCallbacksAndMessages(null);
////            mDeferHandler = null;
////        }
//        if (mMediaPlayer != null) {
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//        if (mUploadGpsTimer != null) {
//            mUploadGpsTimer.cancel();//停止上传GPS
//            mUploadGpsTimer = null;
//        }
//        if (mSendUserInfoTimer != null) {
//            mSendUserInfoTimer.cancel();//停止上传用户信息
//            mSendUserInfoTimer = null;
//        }
//        super.onDestroy();
//    }
//
//    @Override
//    public void onBackPressed() {
//        cancelWaitBus();
//    }
//
//    /**
//     * 停止候车，关闭页面
//     */
//    private void cancelWaitBus() {
//        final ReturnDialog dialog = new ReturnDialog(mContext);
//        dialog.setOnItemClickListener(new ReturnDialog.onItemClickListener() {
//            @Override
//            public void onItem() {
//                ToastUtil.showToast(mContext, "取消候车成功");
//                dialog.dismiss();
//                finish();
//            }
//        });
//        dialog.show();
//    }
//
//}

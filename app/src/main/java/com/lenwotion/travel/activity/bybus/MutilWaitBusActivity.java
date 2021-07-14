package com.lenwotion.travel.activity.bybus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.bybus.business.WaitBusBusiness;
import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
import com.lenwotion.travel.activity.bybus.presenter.WaitingBusPresenter;
import com.lenwotion.travel.adapter.WaitBusesAdapter;
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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MutilWaitBusActivity extends BaseActivity
                                  implements INettyMessageCallback,
                                             WaitingBusContract.ShowWaitResultView,
                                             AdapterView.OnItemClickListener {

    private static final String MY_LOG_TAG = "slashinfo";
    private static final String MY_CHECK_LOG_TAG = "slashinfo_check";

    private static final int MESSAGE_BEGIN_HANDLE = 0;
    private static final int MESSAGE_ONE_BUS_HANDLE = MESSAGE_BEGIN_HANDLE + 1;
    private static final int MESSAGE_END_HANDLE = MESSAGE_ONE_BUS_HANDLE + 2;


    private WifiAdmin mWifiAdmin;
    private ShakeManager mShakeManager;
    private Vibrator mVibrator;
    private ActivityBroadcastReceiver mActivityBroadcastReceiver;

    private WaitingBusPresenter mPresenter;

    private Button mCancelWaitingBt;
    private ImageView mDeviceConnectedIv;
    private ListView  mWaitBusesListView;
    private WaitBusesAdapter mBusesAdapter;

    private ArrayList<AffirmWaitInfoBean> mHandleBusesInfoBean;

    private boolean mIsActivityRunning;
    private boolean mIsNettyConnected;
    private boolean mIsNettyConnecting;
    private boolean mIsNotifyBusArrive;

    private BusesHandler mBusUpdateHandler = new BusesHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.wait_bus_arrive_station));
        setContentView(R.layout.activity_mutil_wait_bus);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initData();
        initView();
        initListener();
        initFunction();
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::onCreate= " + mIsActivityRunning);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsActivityRunning = true;
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::onResume= " + mIsActivityRunning);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsActivityRunning = false;
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::onPause= " + mIsActivityRunning);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIsActivityRunning = false;
        mIsNettyConnected = false;
        mIsNettyConnecting = false;
        mIsNotifyBusArrive = false;
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::onDestroy= " + mIsActivityRunning);
    }

    @Override
    public void showError(String errorMsg) {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::showError= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex() + " msg= " + errorMsg);
    }

    @Override
    public void showUploadGPSData(UploadGPSBean data) {
        ProcessWaitingVOBean voBean = data.getData();
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::showUploadGPSData= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex()  + " voBean= " + voBean);
        int handle_index = mBusUpdateHandler.getIndex();
        switch (voBean.getCode()) {
            case ProcessWaitingVOBean.CODE_WAITTING_BUS: //200.正常等车，更新候车信息
                AffirmWaitInfoBean mAffirmInfoBean = voBean.getAffirm();
                mBusUpdateHandler.insertToList(mAffirmInfoBean);

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
                Log.v(MY_LOG_TAG, "用户远离站台");
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_201);
                mBusUpdateHandler.insertToList(mHandleBusesInfoBean.get(handle_index));
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_202: //202.车过了用户重新叫车:车过了，用户还在等,后台重新分配车辆，继续进入候车界面
                Log.v(MY_LOG_TAG, "车过了用户重新叫车");
                mBusUpdateHandler.insertToList(mHandleBusesInfoBean.get(handle_index));
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_202);
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_203: //203.用户等待超过15分钟重新叫车
                Log.v(MY_LOG_TAG, "用户等待超过15分钟重新叫车");
                mBusUpdateHandler.insertToList(mHandleBusesInfoBean.get(handle_index));
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_203);
                break;

            case ProcessWaitingVOBean.CODE_WAITTING_BUS_204: //204.用户/系统异常
                Log.v(MY_LOG_TAG, "用户/系统异常");
                mBusUpdateHandler.insertToList(mHandleBusesInfoBean.get(handle_index));
                waitingStateChange(ProcessWaitingVOBean.CODE_WAITTING_BUS_204);
                break;

            case ProcessWaitingVOBean.CODE_NETTY_CONNECT: //302.WIFI连接中，需求要求不做任何操作
                //Log.v(MY_LOG_TAG, "WIFI连接中，需求要求不做任何操作");
                //mWaitBusInfo.setText(getString(R.string.bus_arrive_station));
                break;

            default:
                break;
        }
        mBusUpdateHandler.sendEmptyMessage(MESSAGE_ONE_BUS_HANDLE);
    }

    public void waitingStateChange(int code) {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::waitingStateChange= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex() + " code= " + code);
        if (!mIsActivityRunning) {
            return;
        }
        /**
        Intent intent = new Intent(mContext, WaitBusStateActivity.class);
        intent.putExtra("module", "wait");
        intent.putExtra("operateCode", code);
        startActivity(intent);
        finish();
         **/
    }

    @Override
    public void channelRead(String message) {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::channelRead= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex());
    }

    @Override
    public void channelActive() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::channelRead= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex() + " netty连接上channelActive");

        if (mIsActivityRunning) {
            notifyClientConnected(true);
            sendConnectInfo();
        }
    }

    @Override
    public void channelInactive() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::channelInactive= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex() + " netty断开连接channelInactive");

        if (mIsActivityRunning) {
            notifyClientConnected(false);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.m_wait_bus_connected_btn:
                cancelWaitBus();
                break;
        }
    }

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

    private void sendCancelInfo() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendCancelInfo= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex() + " 发送停止候车");

        IByBusService byBusService = AnyWalkingApplication.getInstance().getRetrofit().create(IByBusService.class);
        Call<GeneralResponseBean> call = byBusService.cancelWaitingBus(UserInfoCacheUtil.getUserToken(mContext));
        call.enqueue(new Callback<GeneralResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<GeneralResponseBean> call, @NonNull Response<GeneralResponseBean> response) {
                Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendCancelInfo::onResponse= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex() + " result= " + response.isSuccessful());
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
                Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendCancelInfo::onFailure= " + mIsActivityRunning + " index= " + mBusUpdateHandler.getIndex());
                sendCancelInfoFail(getString(R.string.http_error));
            }
        });
    }

    /**
     * 发送取消候车成功
     */
    private void sendCancelInfoSuccess() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendCancelInfoSuccess= " + mIsActivityRunning + " 取消候车成功" + " index= " + mBusUpdateHandler.getIndex());
    }

    /**
     * 发送取消候车失败
     */
    private void sendCancelInfoFail(String errMsg) {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendCancelInfoFail= " + mIsActivityRunning + " msg= " + errMsg + " index= " + mBusUpdateHandler.getIndex());
    }

    private void initData(){
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::initData======= ");

        mWifiAdmin = new WifiAdmin(this);
        mShakeManager = new ShakeManager(this);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mPresenter = new WaitingBusPresenter(this);
        mHandleBusesInfoBean = MutilWaitBusActivity.WAITING_BUSES_INFO;
        registerReceiver();
    }

    private void registerReceiver() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::registerReceiver======= "  + " index= " + mBusUpdateHandler.getIndex());
        mActivityBroadcastReceiver = new ActivityBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态变化
        registerReceiver(mActivityBroadcastReceiver, intentFilter);
    }

    private void initView() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::initView======= " + " index= " + mBusUpdateHandler.getIndex());
        mCancelWaitingBt = findViewById(R.id.m_wait_bus_connected_btn);
        mDeviceConnectedIv = findViewById(R.id.m_wait_bus_connected_iv);
        mWaitBusesListView = findViewById(R.id.m_wait_bus_list);
        mWaitBusesListView.setOnItemClickListener(this);
        showWaitBusData();
    }

    private void initListener() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::initListener======= " + " index= " + mBusUpdateHandler.getIndex());
        mCancelWaitingBt.setOnClickListener(this);
        mShakeManager.setonShakeListener(new ShakeManager.onShakeListener() {//摇一摇监听
            @Override
            public void shake() {//震动监听
                Log.v(MY_LOG_TAG, "MutilWaitBusActivity::initListener::shake======= " + " index= " + mBusUpdateHandler.getIndex());
                sendOrderToDevice();//发送播报命令
            }
        });
        mDeviceConnectedIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void initFunction() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::initFunction======= " + " index= " + mBusUpdateHandler.getIndex());
        checkCurrentNetwork();
        uploadGPS();
    }

    private void uploadGPS() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::uploadGPS======= " + " index= " + mBusUpdateHandler.getIndex());
        mBusUpdateHandler.sendEmptyMessage(MESSAGE_BEGIN_HANDLE);
    }

    private void endProcessGPS() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::endProcessGPS======= " + " index= " + mBusUpdateHandler.getIndex());
        mBusUpdateHandler.sendEmptyMessage(MESSAGE_END_HANDLE);
    }

    private void showWaitBusData(){
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::showWaitBusData======= " + " index= " + mBusUpdateHandler.getIndex());
        mBusesAdapter = new WaitBusesAdapter(this, mHandleBusesInfoBean);
        mWaitBusesListView.setAdapter(mBusesAdapter);
    }

    private boolean checkIsConnectDeviceWifi() {
        boolean is_connect = mWifiAdmin.getSSID().equals(GlobalVariables.WIFI_AP_SSID);
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::showWaitBusData::is_connect " + is_connect + " index= " + mBusUpdateHandler.getIndex());
        return is_connect;
    }

    private void checkWifiRssi() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::checkWifiRssi:: index= " + mBusUpdateHandler.getIndex() + " 检查WIFI强度");
        new Thread(new Runnable() {
            boolean rssiIsStrong = false;
            @Override
            public void run() {
                while (!rssiIsStrong && mIsActivityRunning) {
                    Log.v(MY_CHECK_LOG_TAG, "MutilWaitBusActivity::checkWifiRssi:: index= " + mBusUpdateHandler.getIndex() + "WIFI强度: " + mWifiAdmin.getRssi());
                    if (mWifiAdmin.getRssi() > -80) {
                        Log.v(MY_CHECK_LOG_TAG, "WIFI强度:" + mWifiAdmin.getRssi());
                        rssiIsStrong = true;
                        Log.v(MY_CHECK_LOG_TAG, "connectNetty");
                        connectNetty();
                    }
                    SystemClock.sleep(200);
                }
            }
        }).start();
    }

    private void connectNetty() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::connectNetty:: index= " + mBusUpdateHandler.getIndex() + " 检查WIFI强度");
        if (!mIsNettyConnecting && !mIsNettyConnected && mIsActivityRunning) {//netty未开始连接且netty未连接
            mIsNettyConnecting = true;
            Log.v(MY_LOG_TAG, "启动netty客户端连接");
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

    private void notifyClientConnected(final boolean isConnected) {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::notifyClientConnected:: index= " +
                mBusUpdateHandler.getIndex() + " isConnected= " + isConnected);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    mIsNettyConnected = true;//netty连接状态标记
                    mDeviceConnectedIv.setBackgroundColor(getResources().getColor(R.color.green));
                    mDeviceConnectedIv.setContentDescription("设备已连接");
                    sendOrderToDevice();
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

    private void sendConnectInfo() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendConnectInfo:: index= " + mBusUpdateHandler.getIndex() + " ===============");
        WaitBusBusiness business = new WaitBusBusiness();
        business.sendConnectInfo(mContext, GlobalVariables.STATION_INFO_BEAN.getStation(), mWifiAdmin.getSSID());
    }

    private void sendOrderToDevice() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendConnectInfo:: index= " +
                mBusUpdateHandler.getIndex() + " mIsNettyConnected= " + mIsNettyConnected + " 發送广播命令到车载机");
        if (!mIsNettyConnected) {
            return;
        }
        mVibrator.vibrate(200);//震动
        Log.v(MY_LOG_TAG, "發送广播命令到车载机");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
                nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
                nettyCommunicationBean.setOperationCode(GlobalConstants.ORDER_LINE_BROADCAST);
                nettyCommunicationBean.setOperationData("no data");
                String order = new Gson().toJson(nettyCommunicationBean);
                NettyClient.sendOrder(order);
                Log.v(MY_LOG_TAG, order);
            }
        }).start();
    }

    private void sendUserInfo() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendUserInfo:: index= " + mBusUpdateHandler.getIndex() +
                " mIsNettyConnected= " + mIsNettyConnected + " 发送用户候车信息到车载机");
        if (!mIsNettyConnected) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                NettyCommunicationBean nettyCommunicationBean = new NettyCommunicationBean();
                nettyCommunicationBean.setUserToken(UserInfoCacheUtil.getUserToken(mContext));
                nettyCommunicationBean.setOperationCode(GlobalConstants.ORDER_SEND_USER_INFO);
                nettyCommunicationBean.setOperationData(GlobalVariables.STATION_INFO_BEAN.getStation());
                String order = new Gson().toJson(nettyCommunicationBean);
                NettyClient.sendOrder(order);
                Log.v(MY_LOG_TAG, order);
                Log.v(MY_LOG_TAG, "MutilWaitBusActivity::sendUserInfo::run::index= " + mBusUpdateHandler.getIndex() +
                        " mIsNettyConnected= " + mIsNettyConnected + " order= " + order);
            }
        }).start();
    }

    private void checkCurrentNetwork() {
        int wifi_type = WifiAdmin.getNetworkType(mContext);
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::checkCurrentNetwork:: index= " + mBusUpdateHandler.getIndex() +
                " mIsNettyConnected= " + mIsNettyConnected + " type= " + wifi_type);

        if (wifi_type == ConnectivityManager.TYPE_WIFI) {//当前是WIFI状态
            Log.v(MY_LOG_TAG, "检查当前网络，当前是WIFI状态");
            if (checkIsConnectDeviceWifi()) {//连接的是车载机WIFI
                Log.v(MY_LOG_TAG, "连接的是车载机WIFI");
                checkWifiRssi();//连接车载机
            } else {//连接的不是车载机
                Log.v(MY_LOG_TAG, "连接的不是车载机");
                // 扫描WIFI
                scanWifi();
            }
        } else if (WifiAdmin.getNetworkType(mContext) == ConnectivityManager.TYPE_MOBILE) {//移动网络状态
            // 扫描WIFI
            Log.v(MY_LOG_TAG, "检查当前网络，移动网络状态");
            scanWifi();
        } else {//网络异常
            Log.v(MY_LOG_TAG, "检查当前网络，网络异常");
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

    private void scanWifi() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::scanWifi:: index= " + mBusUpdateHandler.getIndex() +
                " mIsActivityRunning= " + mIsActivityRunning);
        new Thread(new Runnable() {

            boolean isFindWifi;//是否已经找到目标WIFI信号

            @Override
            public void run() {
                while (!isFindWifi && mIsActivityRunning) {//找到则停止
                    Log.v(MY_CHECK_LOG_TAG, "MutilWaitBusActivity::scanWifi:: index= " + mBusUpdateHandler.getIndex() +
                            " mIsActivityRunning= " + mIsActivityRunning + " isFindWifi= " + isFindWifi);
                    //如果wifi没有打开，打开Wifi
                    if (!mWifiAdmin.isWifiEnabled()) {
                        Log.v(MY_LOG_TAG, "wifi is close,openWifi");
                        mWifiAdmin.openWifi();
                    }

                    //开始扫描
                    Log.v(MY_LOG_TAG, "开始扫描");
                    mWifiAdmin.startScan();

                    //扫描结果处理
                    List<ScanResult> scanResultList = mWifiAdmin.getWifiList();
                    for (ScanResult scanResult : scanResultList) {
                        if (scanResult.SSID.equals(GlobalVariables.WIFI_AP_SSID)) {//如果扫描到的SSID有目标车辆SSID
                            Log.v(MY_CHECK_LOG_TAG, "扫描到的SSID有目标车辆SSID");
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

    private void connectWifi() {
        Log.v(MY_LOG_TAG, "MutilWaitBusActivity::connectWifi:: index= " + mBusUpdateHandler.getIndex() +
                " mIsActivityRunning= " + mIsActivityRunning + " 连接wifi");

        new Thread(new Runnable() {
            boolean isConnectWifi;

            @Override
            public void run() {
                while (!isConnectWifi && mIsActivityRunning) {
                    isConnectWifi = mWifiAdmin.connectWifi(GlobalVariables.WIFI_AP_SSID, GlobalVariables.WIFI_AP_PASSWORD);//连接结果在广播监听回调
                    Log.v(MY_LOG_TAG, "连接wifi:" + isConnectWifi);
                    SystemClock.sleep(200);
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WaitBusDetailActivity.startMySelf(MutilWaitBusActivity.this, false, mHandleBusesInfoBean.get(position));
    }

    private static String MY_INTENT = "com.lenwotion.travel.activity.bybus.START_M_WAIT_BUSES";
    private static ArrayList<AffirmWaitInfoBean> WAITING_BUSES_INFO;


    /**
     * 启动这个Activity
     * @param ctx
     * @param buses_list
     * @param need_finish 是否需要结束启动者
     */
    public static void startMySelf(Activity ctx, ArrayList<AffirmWaitInfoBean> buses_list, boolean need_finish){

        if(null != WAITING_BUSES_INFO) {
            WAITING_BUSES_INFO = null;
        }

        WAITING_BUSES_INFO = new ArrayList<>();
        WAITING_BUSES_INFO = buses_list;

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

            if (null == intent || null == intent.getAction()) {
                return;
            }

            switch (intent.getAction()) {

                case "android.net.conn.CONNECTIVITY_CHANGE"://网络状态变化
                    if (ConnectivityManager.TYPE_WIFI == WifiAdmin.getNetworkType(mContext)) {
                        if (isFirst) {
                            isFirst = false;
                            return;
                        }
                        if (!isPost) {
                            isPost = true;
                            Log.v(MY_LOG_TAG, "网络状态变化:已经是WIFI连接状态");
                            if (checkIsConnectDeviceWifi()) {
                                Log.v(MY_LOG_TAG, "连接的是车载机WIFI");
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

    public class BusesHandler extends Handler{
        private int mCurrentIndex;
        private ArrayList<AffirmWaitInfoBean> mMyBeans;

        public int getIndex(){
            Log.v(MY_LOG_TAG, "MutilWaitBusActivity::BusesHandler::getIndex= " + mCurrentIndex);
            return mCurrentIndex;
        }

        public void insertToList(AffirmWaitInfoBean bean){
            Log.v(MY_LOG_TAG, "MutilWaitBusActivity::BusesHandler::insertToList= " + bean);
            mMyBeans.add(bean);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what){
                case MESSAGE_BEGIN_HANDLE:
                    mCurrentIndex = 0;
                    Log.v(MY_LOG_TAG, "BusesHandler::1 MESSAGE_BEGIN_HANDLE::index= " + mCurrentIndex);
                    mMyBeans = null;
                    mMyBeans = new ArrayList<AffirmWaitInfoBean>();
                    Log.v(MY_LOG_TAG, "BusesHandler::2 MESSAGE_BEGIN_HANDLE::index= " + mCurrentIndex + " send request====");
                    mPresenter.requestRealTimeLatLng(mContext, UserInfoCacheUtil.getUserToken(mContext),
                            mIsNettyConnected,
                            mHandleBusesInfoBean.get(mCurrentIndex).getImei());
                    break;
                case MESSAGE_ONE_BUS_HANDLE:
                    mCurrentIndex = mCurrentIndex + 1;
                    int count = mHandleBusesInfoBean.size();
                    Log.v(MY_LOG_TAG, "BusesHandler::1 MESSAGE_ONE_BUS_HANDLE::index= " + mCurrentIndex + " total= " + count);
                    if(mCurrentIndex < count){
                        Log.v(MY_LOG_TAG, "BusesHandler::2-1 MESSAGE_ONE_BUS_HANDLE::index= " + mCurrentIndex + " total= " + count + " send request============");
                        mPresenter.requestRealTimeLatLng(mContext, UserInfoCacheUtil.getUserToken(mContext),
                                mIsNettyConnected,
                                mHandleBusesInfoBean.get(mCurrentIndex).getImei());
                    } else {
                        Log.v(MY_LOG_TAG, "BusesHandler::2-2 MESSAGE_ONE_BUS_HANDLE::index= " + mCurrentIndex + " total= " + count + " all bus info finish============");
                        mHandleBusesInfoBean = mMyBeans;
                        endProcessGPS();
                    }
                    break;
                case MESSAGE_END_HANDLE:
                    int size = mHandleBusesInfoBean.size();
                    Log.v(MY_LOG_TAG, "BusesHandler::1 MESSAGE_END_HANDLE::index= " + mCurrentIndex + " total= " + size);
                    mHandleBusesInfoBean = mMyBeans;
                    showWaitBusData();
                    sendEmptyMessageDelayed(MESSAGE_BEGIN_HANDLE, 4000);
                    break;
                default:
                    break;
            }
        }
    }
}
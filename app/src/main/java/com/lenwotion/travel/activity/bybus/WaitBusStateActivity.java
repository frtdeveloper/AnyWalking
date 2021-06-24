package com.lenwotion.travel.activity.bybus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.bean.bybus.ProcessWaitingVOBean;
import com.lenwotion.travel.global.GlobalConstants;

/**
 * 候车状态界面
 * Created by fq on 2017/9/14.
 */
public class WaitBusStateActivity extends BaseActivity {

    private TextView mWaitStateMsgTv;
    //private Button mReelectLineBt;
    private Button mReelectStationBt;
    private Button mFinishBt;
    /**
     * 模块
     */
    private String mModule;
    /**
     * 状态码
     */
    private int mOperateCode;
//    /**
//     * 操作请求接口和显示结果
//     */
//    private SelectLinePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.wait_bus_state));
        setContentView(R.layout.activity_waiting_state);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        mModule = getIntent().getStringExtra("module");
        mOperateCode = getIntent().getIntExtra("operateCode", 0);
        //mPresenter = new SelectLinePresenter(this);
    }

    private void initView() {
        mWaitStateMsgTv = findViewById(R.id.tv_wait_msg);
        //mReelectLineBt = findViewById(R.id.bt_state_reelect_line);
        mReelectStationBt = findViewById(R.id.bt_state_reelect_station);
        mFinishBt = findViewById(R.id.bt_state_finish);

        //mReelectLineBt.setVisibility(View.GONE);
        mReelectStationBt.setVisibility(View.GONE);
        mFinishBt.setVisibility(View.GONE);
        showStateInfo(mModule, mOperateCode);
    }

    private void initListener() {
        //mReelectLineBt.setOnClickListener(this);
        mReelectStationBt.setOnClickListener(this);
        mFinishBt.setOnClickListener(this);
    }

    private void showStateInfo(String module, int operateCode) {
        switch (module) {
            case "select":
                switch (operateCode) {
                    case GlobalConstants.WAITING_BUS_201://201 推送车载机超过三次
                        showSelect201();
                        break;
                    case GlobalConstants.WAITING_BUS_202://202 选中线路无回调信息
                        showSelect202();
                        break;
                    case GlobalConstants.WAITING_BUS_203://203 查不到该站台信息
                        showSelect203();
                        break;
                    case GlobalConstants.WAITING_BUS_204://204 系统异常
                        showSelect204();
                        break;
                    case GlobalConstants.WAITING_BUS_205://205 筛选之后无合适车辆
                        showSelect205();
                        break;
                    default:
                        showErrorMsg();
                        break;
                }
                break;

            case "wait":
                switch (operateCode) {
                    case ProcessWaitingVOBean.CODE_WAITTING_BUS_201://201 用户远离站台
                        showWait201();
                        break;
                    case ProcessWaitingVOBean.CODE_WAITTING_BUS_202://202 用户状态异常重新叫车
                        showWait202();
                        break;
                    case ProcessWaitingVOBean.CODE_WAITTING_BUS_203://203 用户等待超过15分钟重新叫车
                        showWait203();
                        break;
                    case ProcessWaitingVOBean.CODE_WAITTING_BUS_204://204 用户不存在
                        showWait204();
                        break;
                    default:
                        showErrorMsg();
                        break;
                }
                break;

            default:
                showErrorMsg();
                break;
        }
    }

    /**
     * 201 用户远离站台
     * 页面提示:1 用户远离站台，候车退出  返回主页
     */
    private void showWait201() {
        mWaitStateMsgTv.setText(getString(R.string.wait_code_201));
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * 202 用户状态异常重新叫车
     * 页面提示:2 车辆已出站，没有新的车辆分配，候车退出  返回主页
     */
    private void showWait202() {
        mWaitStateMsgTv.setText(getString(R.string.wait_code_202));
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * 203 用户等待超过15分钟重新叫车
     * 页面提示:3 用户等候太久，没有合适的车辆经过，候车退出  返回主页
     */
    private void showWait203() {
        mWaitStateMsgTv.setText(getString(R.string.wait_code_203));
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * 204 用户不存在
     * 页面提示:4 用户或系统异常，候车退出  返回主页
     */
    private void showWait204() {
        mWaitStateMsgTv.setText(getString(R.string.wait_code_204));
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * operateCode = 201
     * 推送车载机超过三次
     * 页面提示:1 预约车辆失败，是否重新候车  重选站台/返回主页
     */
    private void showSelect201() {
        mWaitStateMsgTv.setText(getString(R.string.response_code_201));
        mReelectStationBt.setVisibility(View.VISIBLE);
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * operateCode = 202
     * 选中线路无回调信息
     * 页面提示:2 没有合适的车辆经过，是否重新候车  重选站台/返回主页
     */
    private void showSelect202() {
        mWaitStateMsgTv.setText(getString(R.string.response_code_202));
        mReelectStationBt.setVisibility(View.VISIBLE);
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * operateCode = 203
     * 查不到该站台信息
     * 页面提示:203 查不到该站台信息  返回主页
     */
    private void showSelect203() {
        mWaitStateMsgTv.setText(getString(R.string.response_code_203));
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * operateCode = 204
     * 系统异常
     * 页面提示:204 系统异常  返回主页
     */
    private void showSelect204() {
        mWaitStateMsgTv.setText(getString(R.string.response_code_204));
        mFinishBt.setVisibility(View.VISIBLE);
    }

    /**
     * operateCode = 205
     * 筛选之后无合适车辆
     * 页面提示:5 没有合适的车辆经过，是否重新候车  重选站台/返回主页
     */
    private void showSelect205() {
        mWaitStateMsgTv.setText(getString(R.string.response_code_205));
        mReelectStationBt.setVisibility(View.VISIBLE);
        mFinishBt.setVisibility(View.VISIBLE);
    }

    private void showErrorMsg() {
        mWaitStateMsgTv.setText(getString(R.string.system_error));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bt_state_reelect_station://重选站台
                reelectStation();
                break;

//            case R.id.bt_state_reelect_line://重选线路
//                reelectLine();
//                break;

            case R.id.bt_state_finish:
                finish();
                break;
        }
    }

//    /**
//     * 重新请求候车
//     */
//    private void requestWaitBus() {
//        String userToken = UserInfoCacheUtil.getUserToken(mContext);
//        //站台信息
//        StationInfoBean stationInfoBean = GlobalVariables.STATION_INFO_BEAN;
//        //线路信息
//        ArrayList<String> lineList = GlobalVariables.LINE_NAME_LIST;
//        if (!lineList.isEmpty()) {
//            mPresenter.requestWaitBus(mContext, userToken, stationInfoBean, lineList);
//        }
//    }

    /**
     * 重选站台
     */
    private void reelectStation() {
        startActivity(new Intent(mContext, SelectStationActivity.class));
        finish();
    }

//    /**
//     * 重选线路
//     */
//    private void reelectLine() {
//        startActivity(new Intent(mContext, SelectStationActivity.class));
//        finish();
//    }

//    @Override
//    public void showError(String errorMsg) {
//        ToastUtil.showToast(mContext, getString(R.string.fail) + ":" + errorMsg);
//        Log.v(GlobalConstants.LOG_TAG, errorMsg);
//    }
//
//    @Override
//    public void showStationLineListData(StationLineBean stationLineBean) {
//
//    }
//
//    @Override
//    public void showAffirmWaitSucceed(AffirmWaitBusBean result) {
//        Log.v(GlobalConstants.LOG_TAG, result.getMsg());
//        //如果状态码为200，则就跳转候车界面
//        //其它状态码，直接在这个界面更新数据
//        AffirmWaitInfoBean infoBean = result.getData();
//        showAffirmWaitInfoData(infoBean);
//    }
//
//    /**
//     * 候车数据解析，根据OperateCode状态码操作
//     */
//    public void showAffirmWaitInfoData(AffirmWaitInfoBean affirmInfoBean) {
//        if (affirmInfoBean != null) {
//            //WifiAdmin.WIFI_AP_SSID = affirmInfoBean.getSsid();
//            //WifiAdmin.WIFI_AP_PASSWORD = affirmInfoBean.getPassword();
//            int code = affirmInfoBean.getOperateCode();
//            if (code == GlobalConstants.WAITING_BUS_SUCCESS) {
//                //进入候车之前，先要判断是否有GPS
//                //有GPS，直接进入候车界面
//                if (CommonUtil.checkGPSIsOpen(mContext)) {
//                    Intent intent = new Intent(mContext, WaitBusActivity.class);
//                    intent.putExtra("affirmInfoBean", affirmInfoBean);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    //如果没有GPS，提示打开GPS
//                    ToastUtil.showToast(mContext, getString(R.string.gps_not_open));
//                }
//            } else if (code == GlobalConstants.WAITING_BUS_JPSU_EXCEED || code == GlobalConstants.WAITING_BUS_NOT_CALLBACK || code == GlobalConstants.WAITING_BUS_NOT_SUITABLE) {
//                //直接更新数据
//                mOperateCode = code;
//                showStateInfo("select", code);
//            } else if (code == GlobalConstants.WAITING_BUS_NOT_STATION || code == GlobalConstants.WAITING_BUS_SYSTEM_ERROR) {
//                String msg = code == GlobalConstants.WAITING_BUS_NOT_STATION ?
//                        getString(R.string.can_not_find_station_info) : getString(R.string.system_error);
//                ToastUtil.showToast(mContext, msg);
//                finish();
//            }
//        }
//    }

}

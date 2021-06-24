package com.lenwotion.travel.activity.bybus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.bybus.contract.SelectLineContract;
import com.lenwotion.travel.activity.bybus.presenter.SelectLinePresenter;
import com.lenwotion.travel.adapter.SelectLineAdapter;
import com.lenwotion.travel.bean.bybus.AffirmWaitBusBean;
import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;
import com.lenwotion.travel.bean.bybus.StationInfoBean;
import com.lenwotion.travel.bean.bybus.StationLineBean;
import com.lenwotion.travel.bean.bybus.StationLineInfoBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.CommonUtil;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择路线
 * Created by fq on 2017/9/01.
 */
public class SelectLineActivity extends BaseActivity implements SelectLineContract.ShowLineResultView {

    private TextView mLineStateTv;
    private SelectLineAdapter mSelectLineAdapter;
    private List<StationLineInfoBean> mLineBeanList;
    /**
     * 候车信息
     */
    private AffirmWaitInfoBean mWaitInfoBean;
    /**
     * 用户Token
     */
    private String mUserToken;
    /**
     * 线路名列表
     */
    private ArrayList<String> mLineNameList;
    /**
     * 操作请求接口和显示结果
     */
    private SelectLinePresenter mPresenter;

    private int GPS_REQUEST_CODE = 10;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.select_line));
        setContentView(R.layout.activity_select_line);
        initData();
        initView();
    }

    /**
     * 获取站台路线信息
     */
    private void initData() {
        mContext = this;
        mLineBeanList = new ArrayList<>();
        mPresenter = new SelectLinePresenter(this);
        mUserToken = UserInfoCacheUtil.getUserToken(mContext);
        if (TextUtils.isEmpty(mUserToken)) {
            ToastUtil.showToast(mContext, getString(R.string.user_info_error));
            finish();
        }
    }

    private void initView() {
        mLineStateTv = findViewById(R.id.tv_lineState);
        Button selectBt = findViewById(R.id.bt_select);
        selectBt.setOnClickListener(this);
        mSelectLineAdapter = new SelectLineAdapter(mContext, mLineBeanList);
        mSelectLineAdapter.setOnSelctLineListener(new SelectLineAdapter.onSelctLineListener() {
            @Override
            public void onSelect(ArrayList<String> lineNameList) {
                mLineNameList = lineNameList;
                GlobalVariables.LINE_NAME_LIST.clear();
                GlobalVariables.LINE_NAME_LIST.addAll(mLineNameList);
            }
        });
        ListView selectLineLv = findViewById(R.id.lv_select_line);
        selectLineLv.setAdapter(mSelectLineAdapter);

        //本地数据测试
//        String stationData = CommUtil.getFromAssets(mContext, "StationLineListData");
//        StationLineBean bean = new Gson().fromJson(stationData, StationLineBean.class);
//        lineBeanList = bean.getData();
//        adapter.setData(bean.getData());

        //请求路线列表
        StationInfoBean infoBean = GlobalVariables.STATION_INFO_BEAN;
        if (infoBean != null) {
            //站台标识
            String flag = infoBean.getFlag();
            //站台名称
            String station = infoBean.getStation();
            mPresenter.requestStationLineListData(mContext, mUserToken, flag, station);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            //确定候车
            case R.id.bt_select:
                //站台信息
                StationInfoBean stationInfoBean = GlobalVariables.STATION_INFO_BEAN;
                if (mLineNameList == null || mLineNameList.isEmpty()) {
                    ToastUtil.showToast(mContext, getString(R.string.please_select_line));
                    return;
                }
                //开始请求候车
                mPresenter.requestWaitBus(mContext, mUserToken, stationInfoBean, mLineNameList);
                break;

            default:
                break;
        }
    }

//    @Override
//    public void showProgress() {
//        getBaseProgressDialog().show();
//    }
//
//    @Override
//    public void hideProgress() {
//        getBaseProgressDialog().dismiss();
//    }

    /**
     * 显示错误
     */
    @Override
    public void showError(String errorMsg) {
        ToastUtil.showToast(mContext, getString(R.string.fail) + ":" + errorMsg);
        Log.v(GlobalConstants.LOG_TAG, errorMsg);
    }

    /**
     * 显示路线列表
     */
    @Override
    public void showStationLineListData(StationLineBean stationLineBean) {
        if (stationLineBean.getData().size() == 0 || stationLineBean.getData() == null) {
            mLineStateTv.setVisibility(View.VISIBLE);
            return;
        }
        mLineStateTv.setVisibility(View.GONE);
        mLineBeanList = stationLineBean.getData();
        mSelectLineAdapter.refresh(mLineBeanList);
    }

    /**
     * 确认候车成功
     */
    @Override
    public void showAffirmWaitSucceed(AffirmWaitBusBean result) {
        //根据候车返回的结果，跳转相应的界面
        mWaitInfoBean = result.getData();
        Log.v(GlobalConstants.LOG_TAG, "showAffirmWaitSucceed getOperateCode():" + String.valueOf(mWaitInfoBean.getOperateCode()));
        Log.v(GlobalConstants.LOG_TAG, "showAffirmWaitSucceed getLineName():" + mWaitInfoBean.getLineName());
        showAffirmWaitInfoData();
    }

    /**
     * 候车数据解析，根据OperateCode状态码操作
     */
    public void showAffirmWaitInfoData() {
        if (mWaitInfoBean != null) {
//            WifiAdmin.WIFI_AP_SSID = mWaitInfoBean.getSsid();
//            WifiAdmin.WIFI_AP_PASSWORD = mWaitInfoBean.getPassword();
            int code = mWaitInfoBean.getOperateCode();
            if (code == GlobalConstants.WAITING_BUS_SUCCESS) {
                //进入候车之前，先要判断是否有GPS
                //有GPS，直接进入候车界面
                if (CommonUtil.checkGPSIsOpen(mContext)) {
                    startWaitingBus();
                } else {
                    //如果没有GPS，提示强制打开GPS
                    ToastUtil.showToast(mContext, getString(R.string.gps_not_open));
                    openGPSSettings();
                }
            } else if (code == GlobalConstants.WAITING_BUS_201 || code == GlobalConstants.WAITING_BUS_202 || code == GlobalConstants.WAITING_BUS_205) {
                Intent intent = new Intent(mContext, WaitBusStateActivity.class);
                intent.putExtra("module", "select");
                intent.putExtra("operateCode", code);
                startActivity(intent);
                finish();
                //ToastUtil.showToast(mContext, "没有合适的车辆");
            } else if (code == GlobalConstants.WAITING_BUS_203 || code == GlobalConstants.WAITING_BUS_204) {
                String msg = code == GlobalConstants.WAITING_BUS_203 ?
                        getString(R.string.can_not_find_station_info) : getString(R.string.system_error);
                ToastUtil.showToast(mContext, msg);
                finish();
            }
        }
    }

    /**
     * 打开候车页面开始候车
     */
    private void startWaitingBus() {
        Intent intent = new Intent(mContext, WaitBusActivity.class);
        intent.putExtra("affirmInfoBean", mWaitInfoBean);
        startActivity(intent);
        finish();
    }

    /**
     * 打开设置GPS
     */
    private void openGPSSettings() {
        new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.need_open_gps))
                // 拒绝, 退出应用
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                .setPositiveButton(R.string.setting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //跳转GPS设置界面
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, GPS_REQUEST_CODE);
                            }
                        })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {//GPS设置成功，直接进入候车
            startWaitingBus();
        }
    }

}

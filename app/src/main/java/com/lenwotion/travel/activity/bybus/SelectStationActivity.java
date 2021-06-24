package com.lenwotion.travel.activity.bybus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.adapter.SelectStationAdapter;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.bybus.StationInfoBean;
import com.lenwotion.travel.bean.bybus.StationListBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.interfaces.IByBusService;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 选择站台
 * Created by fq on 2017/8/26.
 */
public class SelectStationActivity extends BaseActivity {

    private SelectStationAdapter mSelectStationAdapter;
    private List<StationInfoBean> mStationInfoBeanList;
    private TextView mResultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.select_station));
        setContentView(R.layout.activity_select_station);
        initData();
        initView();
        initFunction();
    }

    private void initData() {
        mContext = this;
        mStationInfoBeanList = new ArrayList<>();
    }

    private void initView() {
        mResultTv = findViewById(R.id.tv_result);
        mSelectStationAdapter = new SelectStationAdapter(mContext, mStationInfoBeanList);
        final ListView selectStationLv = findViewById(R.id.lv_select_station);
        selectStationLv.setAdapter(mSelectStationAdapter);
        selectStationLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                StationInfoBean infoBean = mStationInfoBeanList.get(position);
                if (infoBean.getEnabled() == 0) {
                    ToastUtil.showToast(mContext, getString(R.string.station_no_function));
//                    mResultTv.setVisibility(View.VISIBLE);
//                    mResultTv.setText("此站台经过的公交车辆未安装公交导盲系统");
                    return;
                }
                // 保存地址到全局变量
                GlobalVariables.STATION_INFO_BEAN = infoBean;
                Log.v(GlobalConstants.LOG_TAG, infoBean.toString());
                startActivity(new Intent(mContext, SelectLineActivity.class));
                finish();
            }
        });
    }

    private void initFunction() {
        // 获取附近站台信息
        requestStationListData();
    }

    /**
     * 上传用户Token，经纬度，后台返回附近站台信息
     */
    private void requestStationListData() {
        String userToken = UserInfoCacheUtil.getUserToken(mContext);
        if (TextUtils.isEmpty(userToken)) {
            ToastUtil.showToast(mContext, getString(R.string.user_info_error));
            return;
        }
        //当前的经纬度
        AMapLocation aMapLocation = GlobalVariables.A_MAP_LOCATION;
        if (aMapLocation == null) {
            ToastUtil.showToast(mContext, getString(R.string.no_location));
            return;
        }
        String lat = String.valueOf(aMapLocation.getLatitude());
        String lng = String.valueOf(aMapLocation.getLongitude());
        //getBaseProgressDialog().show();
        IByBusService waitingService = AnyWalkingApplication.getInstance().getRetrofit().create(IByBusService.class);
        Call<StationListBean> call = waitingService.getStationListData(userToken, lat, lng);
        call.enqueue(new Callback<StationListBean>() {
            @Override
            public void onResponse(@NonNull Call<StationListBean> call, @NonNull Response<StationListBean> response) {
                //getBaseProgressDialog().dismiss();
                if (response.isSuccessful()) {
                    StationListBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            fetchStationSuccess(bean);
                        } else {
                            // 获取服务器信息失败
                            fetchStationFail(bean.getMsg());
                        }
                    } else {
                        // http 错误
                        fetchStationFail(getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<StationListBean> call, @NonNull Throwable throwable) {
                // 超时
                fetchStationFail(getString(R.string.http_error));
            }
        });
    }

    /**
     * 获取到站台列表,返回此经纬度有站台的列表
     */
    public void fetchStationSuccess(StationListBean bean) {
        if (bean.getData().size() == 0 || bean.getData() == null) {
            mResultTv.setVisibility(View.VISIBLE);
            mResultTv.setText(getString(R.string.far_from_station));
            return;
        }
        mResultTv.setVisibility(View.GONE);
        mStationInfoBeanList = bean.getData();
        mSelectStationAdapter.refresh(mStationInfoBeanList);
    }

    /**
     * 获取到站台列表失败提示
     */
    public void fetchStationFail(String errorMessage) {
        //getBaseProgressDialog().dismiss();
        ToastUtil.showToast(mContext, getString(R.string.fail) + ":" + errorMessage);
    }

}

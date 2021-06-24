package com.lenwotion.travel.fragment.homepage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lenwotion.travel.R;
import com.lenwotion.travel.adapter.reminder.ArrivalStationAdapter;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.reminder.ArriveReminderBean;
import com.lenwotion.travel.fragment.BaseFragment;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;

import java.util.ArrayList;
import java.util.List;

/**
 * 到站提醒页面
 * Created by fq on 2017/12/7.
 */
public class ArrivalStationFragment extends BaseFragment {

    private View mView;
    private TextView mNoArriveStationInfoTv;
    private ListView mArriveStationLv;
    private ArrivalStationAdapter mArrivalStationAdapter;
    /**
     * 站台列表数据
     */
    private List<ArriveReminderBean> mBeanList;
    /**
     * 接收开始计算距离的广播
     */
    private ReminderReceiver mReminderReceiver;

    public static ArrivalStationFragment newInstance() {
        return new ArrivalStationFragment();
    }

    public class ReminderReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {

                case GlobalConstants.INTENT_MAP_LOCATION_CHANGED:
                    getRemindDataByDb();
                    break;

                default:
                    break;
            }

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_arrive_station, container, false);
        initData();
        initView();
        return mView;
    }

    private void initData() {
        mContext = getActivity();
        mBeanList = new ArrayList<>();
        registerReceiver();
    }

    private void initView() {
        mNoArriveStationInfoTv = mView.findViewById(R.id.tv_arrive_station_no_info);
        mArriveStationLv = mView.findViewById(R.id.lv_arrive_station);
        mArrivalStationAdapter = new ArrivalStationAdapter(mBeanList, mContext);
        mArriveStationLv.setAdapter(mArrivalStationAdapter);
        getRemindDataByDb();
    }

    /**
     * 注册接收
     */
    private void registerReceiver() {
        mReminderReceiver = new ReminderReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstants.INTENT_MAP_LOCATION_CHANGED);//开始计算经纬度距离
        mContext.registerReceiver(mReminderReceiver, intentFilter);
    }

    /**
     * 从数据库里获取提醒数据
     */
    private void getRemindDataByDb() {
        if (GlobalVariables.A_MAP_LOCATION == null) {
            return;
        }
        List<ArriveReminderBean> infoList = AnyWalkingApplication.getInstance().getDaoSession().loadAll(ArriveReminderBean.class);
        if (infoList.isEmpty()) {
            mNoArriveStationInfoTv.setVisibility(View.VISIBLE);
            mArriveStationLv.setVisibility(View.GONE);
        } else {
            mNoArriveStationInfoTv.setVisibility(View.GONE);
            mArriveStationLv.setVisibility(View.VISIBLE);
            mBeanList.clear();
            for (ArriveReminderBean bean : infoList) {
                bean.setDirection((int) AMapUtils.calculateLineDistance(
                        new LatLng(GlobalVariables.A_MAP_LOCATION.getLatitude(), GlobalVariables.A_MAP_LOCATION.getLongitude()),
                        new LatLng(Double.valueOf(bean.getLat()), Double.valueOf(bean.getLng()))));
                mBeanList.add(bean);
            }
        }
        mArrivalStationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mReminderReceiver);
        super.onDestroy();
    }

}

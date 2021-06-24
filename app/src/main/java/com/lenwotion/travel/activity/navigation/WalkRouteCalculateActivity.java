package com.lenwotion.travel.activity.navigation;

import android.os.Bundle;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.NaviLatLng;
import com.lenwotion.travel.R;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.DataSaveUtil;

public class WalkRouteCalculateActivity extends BaseNavigationActivity {

    /**
     * 终点GPS
     */
    private LatLng mLatLng;
    /**
     * 导航类型
     */
    private int mNaviType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.walking_navigation));
        setContentView(R.layout.activity_basic_navi);
        initData();
        initView(savedInstanceState);
    }

    private void initData() {
        mContext = this;
        mNaviType = getIntent().getIntExtra("naviType", NaviType.GPS);
        mLatLng = getIntent().getParcelableExtra("latLng");
        if (mNaviType == NaviType.EMULATOR) {
            DataSaveUtil.clearNavigationPlanningList();
        }
    }

    private void initView(Bundle savedInstanceState) {
        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);
    }

    @Override
    public void onInitNaviSuccess() {
        super.onInitNaviSuccess();
        if (GlobalVariables.A_MAP_LOCATION == null) {
            return;
        }
        mAMapNavi.calculateWalkRoute(
                new NaviLatLng(GlobalVariables.A_MAP_LOCATION.getLatitude(),
                        GlobalVariables.A_MAP_LOCATION.getLongitude()),
                new NaviLatLng(mLatLng.latitude, mLatLng.longitude));
    }

    @Override
    public void onCalculateRouteSuccess(int[] ids) {
        super.onCalculateRouteSuccess(ids);
        mAMapNavi.startNavi(mNaviType);
    }

    @Override
    public void onGetNavigationText(String text) {
        super.onGetNavigationText(text);
        // 模拟导航才需要记录导航列表
        if (mNaviType == NaviType.EMULATOR) {
            DataSaveUtil.saveNavigationPlanningList(text);
        }
    }

}

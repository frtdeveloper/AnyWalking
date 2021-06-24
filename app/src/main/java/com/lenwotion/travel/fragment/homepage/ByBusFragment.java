package com.lenwotion.travel.fragment.homepage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.lenwotion.travel.R;
import com.lenwotion.travel.test.TestGPSActivity;
import com.lenwotion.travel.activity.bybus.SelectStationActivity;
import com.lenwotion.travel.test.TestShakeActivity;
import com.lenwotion.travel.fragment.BaseFragment;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.WifiAdmin;

/**
 * 乘车页面
 * Created by john on 2016/3/17.
 */
public class ByBusFragment extends BaseFragment {

    /**
     * fragment布局
     */
    private View mView;
    /**
     * AMap对象
     */
    private AMap mAMap;
    /**
     * MapView对象
     */
    private MapView mMapView;
    /**
     * 我的位置按钮
     */
    private Button mLocationBt;
    /**
     * 地图定位标记
     */
    private Marker mMarker;
    /**
     * 我要坐车按钮
     */
    private Button mWantToByBusBt;
//    /**
//     * 步行导航按钮
//     */
//    private Button mNavigationBt;
    /**
     * 车载机WIFI是否连接提示ImageView
     */
    private ImageView mDeviceConnectedIv;
    /**
     * 接收定位改变的广播，刷新页面（广播来自MainActivity）
     */
    private LocationChangedReceiver mLocationChangedReceiver;
    private WifiAdmin mWifiAdmin;

    private TextView mGPSInfoTv;
    private Button mFactoryTestBt;

    public static ByBusFragment newInstance() {
        return new ByBusFragment();
    }

    public class LocationChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            if (intent.getAction().equals(GlobalConstants.INTENT_MAP_LOCATION_CHANGED)) {
                // 接收定位改变的广播，刷新页面
                refreshMapViewLocationMark(GlobalVariables.A_MAP_LOCATION.getLatitude(),
                        GlobalVariables.A_MAP_LOCATION.getLongitude());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_by_bus, container, false);
        initData();
        initView(savedInstanceState);
        initListener();
        return mView;
    }

    private void initData() {
        mContext = getActivity();
        mWifiAdmin = new WifiAdmin(mContext);
        // 注册接收定位改变的广播，刷新页面（广播来自MainActivity）
        registerLocationChangedReceiver();
    }

    /**
     * 注册接收定位改变的广播，刷新页面（广播来自MainActivity）
     */
    private void registerLocationChangedReceiver() {
        mLocationChangedReceiver = new LocationChangedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstants.INTENT_MAP_LOCATION_CHANGED);//地图定位数据更新
//        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//DownloadManager下载完成
//        intentFilter.addAction(GlobalConstants.SEND_COMPUTATIONS_DISTANCE);//开始计算经纬度距离
        mContext.registerReceiver(mLocationChangedReceiver, intentFilter);
    }

    private void initView(Bundle savedInstanceState) {
        mMapView = mView.findViewById(R.id.mv_homepage);
        mLocationBt = mView.findViewById(R.id.bt_my_location);
        mWantToByBusBt = mView.findViewById(R.id.bt_want_to_by_bus);
        //mNavigationBt = mView.findViewById(R.id.bt_navigation);

        mDeviceConnectedIv = mView.findViewById(R.id.iv_device_connected);
        mGPSInfoTv = mView.findViewById(R.id.tv_gps_info);
        mFactoryTestBt = mView.findViewById(R.id.bt_factory_test);

        initMap(savedInstanceState);
    }

    private void initMap(Bundle savedInstanceState) {
        // 此方法必须重写
        mMapView.onCreate(savedInstanceState);
        mAMap = mMapView.getMap();
        mMarker = mAMap.addMarker(new MarkerOptions().anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

//        // 如果要设置定位的默认状态，可以在此处进行设置
//        mMyLocationStyle = new MyLocationStyle();
//        // 设置定位频次方法，单位：毫秒，默认值：1000毫秒，如果传小于1000的任何值将按照1000计算。该方法只会作用在会执行连续定位的工作模式上。
//        mMyLocationStyle.interval(2000);
//        // 设置定位蓝点精度圆圈的填充颜色(不知道如何设置颜色，无论什么数值都是透明)
//        mMyLocationStyle.radiusFillColor(0);
//        // 精度圈边框宽度自定义方法
//        mMyLocationStyle.strokeWidth(0);
//        // 初始化定位蓝点样式类。连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
//        // 设置定位蓝点的Style
//        mAMap.setMyLocationStyle(mMyLocationStyle);
//
        // 实例化UiSettings类对象
        UiSettings mUiSettings = mAMap.getUiSettings();
        // 设置默认定位按钮是否显示
        mUiSettings.setMyLocationButtonEnabled(false);
        // 缩放按钮是提供给 App 端用户控制地图缩放级别的交换按钮，每次点击改变1个级别，此控件默认打开
        mUiSettings.setZoomControlsEnabled(false);
        // 比例尺控件。位于地图右下角，可控制其显示与隐藏
        mUiSettings.setScaleControlsEnabled(false);
        // 禁用所有手势
        mUiSettings.setAllGesturesEnabled(false);
//        // 禁用缩放手势
//        mUiSettings.setZoomGesturesEnabled(false);
//        // 禁用滑动手势
//        mUiSettings.setScrollGesturesEnabled(false);
//        // 禁用旋转手势
//        mUiSettings.setRotateGesturesEnabled(false);
//        // 禁用倾斜手势
//        mUiSettings.setTiltGesturesEnabled(false);

//        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        mAMap.setMyLocationEnabled(true);
//        // 设置默认显示的缩放比例
//        mAMap.moveCamera(CameraUpdateFactory.zoomTo(16));
//        // 设置SDK 自带定位消息监听
//        mAMap.setOnMyLocationChangeListener(this);
    }

//定位蓝点提供8种模式：
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//以下三种模式从5.1.0版本开始提供
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。

    private void initListener() {
        mLocationBt.setOnClickListener(this);
        mWantToByBusBt.setOnClickListener(this);
        //mNavigationBt.setOnClickListener(this);

        if (GlobalVariables.IS_TEST_GPS) {
            mDeviceConnectedIv.setOnClickListener(this);
            mDeviceConnectedIv.setVisibility(View.VISIBLE);
        }
        if (GlobalVariables.IS_TEST_FACTORY) {
            mFactoryTestBt.setOnClickListener(this);
            mFactoryTestBt.setVisibility(View.VISIBLE);
            mGPSInfoTv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (GlobalVariables.A_MAP_LOCATION == null) {
            ToastUtil.showToast(mContext, getString(R.string.no_location));
            return;
        }

        switch (v.getId()) {
            case R.id.bt_my_location:
                //ToastUtil.showToast(mContext, "rssi:" + mWifiAdmin.getRssi());
                queryLocation();
                //startActivity(new Intent(mContext, TestWifiActivity.class));
                break;

            case R.id.bt_want_to_by_bus:
                wantToByBus();
                break;

//            case R.id.bt_navigation:
//                startActivity(new Intent(mContext, PoiSearchActivity.class));
//                break;

            case R.id.bt_factory_test:
                startActivity(new Intent(mContext, TestShakeActivity.class));
                break;

            case R.id.iv_device_connected:
                startActivity(new Intent(mContext, TestGPSActivity.class));
                break;
        }
    }

    /**
     * 查找我的位置
     */
    private void queryLocation() {
        if (GlobalVariables.A_MAP_LOCATION == null || TextUtils.isEmpty(GlobalVariables.A_MAP_LOCATION.getAddress())) {
            ToastUtil.showToast(mContext, mContext.getString(R.string.no_location));
            return;
        }
        ToastUtil.showToast(mContext, GlobalVariables.A_MAP_LOCATION.getAddress());

        mGPSInfoTv.setText(GlobalVariables.A_MAP_LOCATION.getLatitude() + "\n" +
                GlobalVariables.A_MAP_LOCATION.getLongitude());
    }

    /**
     * 刷新地图定位标记
     */
    private void refreshMapViewLocationMark(double latitude, double longitude) {
        if (mAMap != null) {
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), GlobalConstants.MAP_ZOOM));
            mMarker.setPosition(new LatLng(latitude, longitude));
        }
    }

    /**
     * 我要坐车
     */
    private void wantToByBus() {
        // 如果wifi没有打开，打开WiFi
        if (!mWifiAdmin.isWifiEnabled()) {
            ToastUtil.showToast(mContext, "请打开WIFI后再使用坐车功能");
            Log.v(GlobalConstants.LOG_TAG, "wifi is close,openWifi");
            mWifiAdmin.openWifi();
            return;
        }
        startActivity(new Intent(mContext, SelectStationActivity.class));
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mLocationChangedReceiver);
        mMapView.onDestroy();
        super.onDestroy();
    }

}

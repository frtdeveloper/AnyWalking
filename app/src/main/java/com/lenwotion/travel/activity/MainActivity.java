package com.lenwotion.travel.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lenwotion.travel.R;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.BaseResponseBean;
import com.lenwotion.travel.fragment.homepage.ArrivalStationFragment;
import com.lenwotion.travel.fragment.homepage.ByBusFragment;
import com.lenwotion.travel.fragment.homepage.MeFragment;
import com.lenwotion.travel.fragment.homepage.SearchBusFragment;
import com.lenwotion.travel.fragment.homepage.WhereGoFragment;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.interfaces.IAccountService;
import com.lenwotion.travel.utils.DownloadApkUtil;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;
import com.lenwotion.travel.utils.WifiAdmin;
import com.lenwotion.travel.view.CustomerViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * APP主页
 */
public class MainActivity extends BaseActivity implements AMapLocationListener {

    /**
     * 底部导航栏view
     */
    private BottomNavigationView mNavigationView;
    /**
     * 模块ViewPager
     */
    private CustomerViewPager mHomeViewPager;
    /**
     * 模块Fragment列表
     */
    private ArrayList<Fragment> mFragmentList;
    /**
     * 返回键按下计数
     */
    private int mBackPressedCount;
    private WifiAdmin mWifiAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDisplayHomeAsUpNotEnabled();
        setActionBarTitle(getString(R.string.app_name));
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initResultView();
        initListener();
        initFunction();
    }

    private void initData() {
        mContext = this;
        // 初始化ViewPager的数据集
        mFragmentList = new ArrayList<>();
        mFragmentList.add(ByBusFragment.newInstance());
        mFragmentList.add(SearchBusFragment.newInstance());
        mFragmentList.add(ArrivalStationFragment.newInstance());
        mFragmentList.add(WhereGoFragment.newInstance());
        mFragmentList.add(MeFragment.newInstance());

        mWifiAdmin = new WifiAdmin(mContext);
    }

    private void initFunction() {
        //删除已存在的Apk
        DownloadApkUtil.removeFile(mContext);
        // 初始化高德地图定位
        initAMapLocation();
        // 上传手机号码,作为用户使用记录
        checkIsUse();
        // 打开WIFI
        openWifi();
    }

    /**
     * 初始化高德地图定位
     */
    private void initAMapLocation() {
        //初始化定位
        AMapLocationClient mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(2 * 1000);
        //设置是否返回地址信息（默认返回地址信息）
        //mLocationOption.setNeedAddress(true);
        //设置是否强制刷新WIFI，默认为true，强制刷新。
        //mLocationOption.setWifiActiveScan(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        //mLocationOption.setHttpTimeOut(30 * 1000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (GlobalVariables.IS_TEST_GPS) {
                if (!TextUtils.isEmpty(GlobalVariables.TEST_LAT) && !TextUtils.isEmpty(GlobalVariables.TEST_LNG)) {
                    aMapLocation.setLatitude(Double.valueOf(GlobalVariables.TEST_LAT));
                    aMapLocation.setLongitude(Double.valueOf(GlobalVariables.TEST_LNG));
                }
            }
            // 保存地址到全局变量
            GlobalVariables.A_MAP_LOCATION = aMapLocation;
            // 发送广播给各个需要的地方
            sendBroadcast(new Intent(GlobalConstants.INTENT_MAP_LOCATION_CHANGED));
        }
    }

    private void initView() {
        mHomeViewPager = findViewById(R.id.home_viewpager);
        mNavigationView = findViewById(R.id.home_navigation);
        // 禁用BottomNavigationView ShiftMode
        disableShiftMode(mNavigationView);
    }

    private void initResultView() {
        // Fragment模块适配器
        FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }
        };

        mHomeViewPager.setAdapter(mFragmentPagerAdapter);

        // 设置在空闲状态的视图层次中，应保留当前页的任何一方的页数。
        mHomeViewPager.setOffscreenPageLimit(mFragmentList.size());
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    /**
     * 底部导航栏监听
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.navigation_by_bus:
                            mHomeViewPager.setCurrentItem(0, false);
                            return true;
                        case R.id.navigation_search_bus:
                            mHomeViewPager.setCurrentItem(1, false);
                            return true;
                        case R.id.navigation_arrive_station:
                            mHomeViewPager.setCurrentItem(2, false);
                            return true;
                        case R.id.navigation_where_go:
                            mHomeViewPager.setCurrentItem(3, false);
                            return true;
                        case R.id.navigation_me:
                            mHomeViewPager.setCurrentItem(4, false);
                            return true;

                    }
                    return false;

                }

            };

    /**
     * BottomNavigationView当menu item大于3个的时候，会出现缩放动画
     * 谷歌设计是遵循设计规范。当BottomNavigationMenuView item大于3的时候，使用shift mode
     * 改变这种默认的行为，官方没有提供，在stackoverflow上找到的最佳方案是使用反射
     */
    @SuppressWarnings("all")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                // noinspection RestrictedApi
                // item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                // noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传手机号码,作为用户使用记录
     */
    private void checkIsUse() {
        String phoneNum = UserInfoCacheUtil.getUserPhoneNum(mContext);
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        // 请求接口
        IAccountService service = AnyWalkingApplication.getInstance().getRetrofit().create(IAccountService.class);
        Call<BaseResponseBean> call = service.checkIsUseApp(phoneNum);
        call.enqueue(new Callback<BaseResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponseBean> call, @NonNull Response<BaseResponseBean> response) {
                if (response.isSuccessful()) {
                    BaseResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            Log.v(GlobalConstants.LOG_TAG, "checkIsUse成功");
                        } else {
                            Log.v(GlobalConstants.LOG_TAG, "checkIsUse失败:" + bean.getMsg());
                        }
                    } else {
                        Log.v(GlobalConstants.LOG_TAG, "checkIsUse失败:" + response.code());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponseBean> call, @NonNull Throwable throwable) {
                Log.v(GlobalConstants.LOG_TAG, "checkIsUse失败:" + throwable.getMessage());
            }
        });
    }

    /**
     * 打开WIFI
     */
    private void openWifi() {
        if (!mWifiAdmin.isWifiEnabled()) {
            Log.v(GlobalConstants.LOG_TAG, "wifi is close,openWifi");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWifiAdmin.openWifi();
                }
            }, 1000);
        }
    }

    @Override
    public void onBackPressed() {
        // 设置主页按下返回键提示再按一次退出
        mBackPressedCount++;
        ToastUtil.showToast(MainActivity.this, getString(R.string.press_again_to_exit));

        // 2秒没有按下，计数重置
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackPressedCount = 0;
            }
        }, 2 * 1000);

        if (mBackPressedCount > 1) {
            // 2秒内再次按下，退出程序
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mFragmentList != null) {
            mFragmentList.clear();
            mFragmentList = null;
        }
        super.onDestroy();
    }

}

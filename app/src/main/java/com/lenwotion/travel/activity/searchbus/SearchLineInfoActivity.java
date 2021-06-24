package com.lenwotion.travel.activity.searchbus;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.searchbus.contract.SearchLineDirectionContract;
import com.lenwotion.travel.activity.searchbus.presenter.SearchLineDirectionPresenter;
import com.lenwotion.travel.adapter.search.SearchLineDirectionAdapter;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.reminder.ArriveReminderBean;
import com.lenwotion.travel.bean.search.StationInfoBean;
import com.lenwotion.travel.bean.search.SearchLineDirectionInfoBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.greendao.ArriveReminderBeanDao;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询线路详情：上下行
 * Created by fq on 2017/11/25.
 */

public class SearchLineInfoActivity extends BaseActivity implements SearchLineDirectionContract.ShowSearchLineDirectionResultView {

    /**
     * 线路方向
     */
    private TextView mLineDirectionTv;
    /**
     * 切换方向
     */
    private Button mChangeDirectionBt;

    private SearchLineDirectionAdapter mAdapter;
    /**
     * 线路上下行实体类
     */
    private SearchLineDirectionInfoBean mLineDirectionInfoBean;
    private List<StationInfoBean> mStationList;
    /**
     * 请求数据
     */
    private SearchLineDirectionPresenter mPresenter;
    /**
     * 区分上下行:1表示上行，2表示下行
     */
    private int mLineDirectionType = 0;
    private String mLineName;
    private String mLineDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.table_search_line_details));
        setContentView(R.layout.activity_search_lineway);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        if (getIntent() != null) {
            mLineName = getIntent().getStringExtra("lineName");
            mLineDirection = getIntent().getStringExtra("direction");
        }
        mPresenter = new SearchLineDirectionPresenter(this);
        mStationList = new ArrayList<>();
    }

    private void initView() {
        TextView lineWayNameTv = findViewById(R.id.tv_line_name);
        lineWayNameTv.setText(mLineName);

        mLineDirectionTv = findViewById(R.id.tv_line_direction);
        mLineDirectionTv.setText(getString(R.string.go_to) + mLineDirection);

        mChangeDirectionBt = findViewById(R.id.bt_change_direction);

        mAdapter = new SearchLineDirectionAdapter(mContext, mStationList);
        mAdapter.setOnItemSetListener(new SearchLineDirectionAdapter.OnItemSetListener() {
            @Override
            public void onItemSet(StationInfoBean item) {
                if (!TextUtils.isEmpty(item.getLat()) && !TextUtils.isEmpty(item.getLng())) {
                    saveSelectStationToDB(mLineDirectionType, item);
                }
            }
        });
        ListView lineWayLv = findViewById(R.id.lv_line_way);
        lineWayLv.setAdapter(mAdapter);

        requestLineWayListData();
    }

    private void initListener() {
        mChangeDirectionBt.setOnClickListener(this);
    }

    /**
     * 获取线路详情：上下行
     */
    private void requestLineWayListData() {
        if (GlobalVariables.A_MAP_LOCATION == null) {
            return;
        }
        mPresenter.requestSearchLineDirectionData(mContext, UserInfoCacheUtil.getUserToken(mContext),
                mLineName, GlobalVariables.A_MAP_LOCATION.getCity());
    }

    /**
     * 保存选项到数据库
     */
    private void saveSelectStationToDB(int wayType, StationInfoBean item) {
        //先查询数据库中是否包含这站台
        ArriveReminderBean findBean = AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(ArriveReminderBean.class)
                .where(ArriveReminderBeanDao.Properties.StationName.eq(item.getStationName()))
                .where(ArriveReminderBeanDao.Properties.WayType.eq(wayType)).build().unique();
        // 数据库中没有此站台，才添加到数据库
        if (findBean == null) {
            ArriveReminderBean bean = new ArriveReminderBean();
            bean.setType(GlobalConstants.TYPE_SEARCH_LINE);
            bean.setWayType(mLineDirectionType);
            bean.setStationName(item.getStationName());
            bean.setLat(item.getLat());
            bean.setLng(item.getLng());
            bean.setIsRemind(true);
            AnyWalkingApplication.getInstance().getDaoSession().insert(bean);
            Log.v(GlobalConstants.LOG_TAG, "添加站台数据成功");
        } else {
            Log.v(GlobalConstants.LOG_TAG, " 数据库中已包含此站台");
        }
        //sendBroadcast(new Intent(GlobalConstants.SEND_COMPUTATIONS_DISTANCE));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_change_direction:
                if (mStationList != null) {
                    // 如果是上行，切换为下行
                    if (mLineDirectionType == 1) {
                        mLineDirectionType = 2;
                        mStationList = mLineDirectionInfoBean.getDownDirection();
                        mLineDirectionTv.setText(getString(R.string.go_to) + mStationList.get(mLineDirectionInfoBean.getDownDirection().size() - 1).getStationName());
                    }// 如果是下行，切换为上行
                    else if (mLineDirectionType == 2) {
                        mLineDirectionType = 1;
                        mStationList = mLineDirectionInfoBean.getUpDirection();
                        mLineDirectionTv.setText(getString(R.string.go_to) + mStationList.get(mLineDirectionInfoBean.getUpDirection().size() - 1).getStationName());
                    }
                    if (!mStationList.isEmpty()) {
                        mAdapter.refresh(mStationList);
                    }
                }
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

    @Override
    public void showError(String errorMsg) {

    }

    /**
     * 线路上下行数据
     */
    @Override
    public void showSearchLineDirectionData(SearchLineDirectionInfoBean data) {
        mLineDirectionInfoBean = data;
        if (!data.getUpDirection().isEmpty()) {
            //拿开往方向跟上行的最后一个站对比，如果相等，即上行，否则下行
            if (mLineDirection.contentEquals(data.getUpDirection().get(data.getUpDirection().size() - 1).getStationName())) {
                //上行
                mLineDirectionType = 1;
                mStationList = data.getUpDirection();
            } else {
                //下行
                mLineDirectionType = 2;
                mStationList = data.getDownDirection();
            }
            mAdapter.refresh(mStationList);
        }
    }

}

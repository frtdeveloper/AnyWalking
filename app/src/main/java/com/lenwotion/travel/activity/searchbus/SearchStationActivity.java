package com.lenwotion.travel.activity.searchbus;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.searchbus.contract.SearchBusContract;
import com.lenwotion.travel.activity.searchbus.interfaces.IStationSelectCallback;
import com.lenwotion.travel.activity.searchbus.presenter.SearchBusPresenter;
import com.lenwotion.travel.adapter.search.SearchHistoryAdapter;
import com.lenwotion.travel.adapter.search.SearchStationAdapter;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.search.SearchStationInfoBean;
import com.lenwotion.travel.bean.search.db.SearchBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.greendao.SearchBeanDao;
import com.lenwotion.travel.utils.CommonUtil;
import com.lenwotion.travel.utils.ToastUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;
import com.lenwotion.travel.view.ClearEditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询站台界面
 * Created by fq on 2017/11/30.
 */

public class SearchStationActivity extends BaseActivity implements SearchBusContract.ShowSearchStationView, IStationSelectCallback {

    private ClearEditText mSearchEt;
    private ListView mSearchLv;
    private Button mClearHistoryBt;
    private LinearLayout mHistoryLl;
    private LinearLayout mSearchLl;
    /**
     * 请求数据操作
     */
    private SearchBusPresenter mPresenter;
    private SearchStationAdapter mStationAdapter;
    /**
     * 查询站台数据
     */
    private List<SearchStationInfoBean> mStationData;
    /**
     * 历史记录
     */
    private SearchHistoryAdapter mHistoryAdapter;
    /**
     * 搜索历史记录
     */
    private List<SearchBean> mHistoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.search_station));
        setContentView(R.layout.activity_search_bus);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        mPresenter = new SearchBusPresenter(this);
        mStationData = new ArrayList<>();
        mHistoryList = new ArrayList<>();
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);
        mSearchLv = findViewById(R.id.lv_search);

        mClearHistoryBt = findViewById(R.id.bt_clear);
        mHistoryLl = findViewById(R.id.ll_history);
        mSearchLl = findViewById(R.id.ll_search);
        // 历史记录
        mHistoryAdapter = new SearchHistoryAdapter(mContext, mHistoryList);

        ListView historyLv = findViewById(R.id.lv_history);
        historyLv.setAdapter(mHistoryAdapter);
        historyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mHistoryList.isEmpty()) {
                    String content = mHistoryList.get(position).getHistoryword();
                    mSearchEt.setText(content);
                }
            }
        });
        // 查询
        mStationAdapter = new SearchStationAdapter(mContext, mStationData, this);
        mSearchLv.setAdapter(mStationAdapter);
        mSearchEt.setHint(getString(R.string.input_bus_station));

        getLineHistoryByDb();
        initSearch();
    }

    private void initListener() {
        mClearHistoryBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_clear:
                deleteHistoryByDb();
                break;
        }
    }

    /**
     * 查询部分:监听输入、点击
     */
    private void initSearch() {
        mSearchEt.setOnTextCompleteListener(new ClearEditText.OnTextCompleteListener() {
            @Override
            public void onText(String content) {
                //监听输入完成
                if (!TextUtils.isEmpty(content)) {
                    requestSearchDataByType(content);
                } else {
                    //输入框的数据为空
                    if (!mStationData.isEmpty()) {
                        mStationData.clear();
                        mStationAdapter.refresh(mStationData);
                        if (!mHistoryList.isEmpty()) {
                            mHistoryLl.setVisibility(View.VISIBLE);
                            mSearchLl.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
        // 监听搜索键、确定键
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String content = mSearchEt.getText().toString().trim();
                    if (!TextUtils.isEmpty(content)) {
                        requestSearchDataByType(content);
                    }
                    return true;
                }
                return false;
            }
        });
//        mSearchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //保存数据库：选择的线路或者站台
//                String searchValue;
//                int searchType;
//                searchType = GlobalConstants.TYPE_SEARCH_STATION;
//                searchValue = mStationData.get(position).getStation1();
//                if (!TextUtils.isEmpty(searchValue)) {
//                    saveSearchDataToDb(searchValue, searchType);
//                }
//            }
//        });
    }

    @Override
    public void onItemSelect(int position) {
        String searchValue;
        int searchType;
        searchType = GlobalConstants.TYPE_SEARCH_STATION;
        searchValue = mStationData.get(position).getStation1();
        if (!TextUtils.isEmpty(searchValue)) {
            saveSearchDataToDb(searchValue, searchType);
        }
    }

    /**
     * 保存选择线路或站台到数据库
     */
    private void saveSearchDataToDb(String data, int type) {
        // 先数据库查询是否有这个线路或站台
        SearchBean findBean = AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(SearchBean.class).where(SearchBeanDao.Properties.Historyword.eq(data)).build().unique();
        if (findBean != null) {
            findBean.setType(type);
            findBean.setHistoryword(data);
            findBean.setUpdatetime(CommonUtil.getTime());
            AnyWalkingApplication.getInstance().getDaoSession().update(findBean);
        } else {
            SearchBean searchBean = new SearchBean();
            searchBean.setType(type);
            searchBean.setHistoryword(data);
            searchBean.setUpdatetime(CommonUtil.getTime());
            AnyWalkingApplication.getInstance().getDaoSession().insert(searchBean);
        }
    }

    /**
     * 从数据库获取查询线路历史记录
     * 按照时间升序
     */
    private void getLineHistoryByDb() {
        mHistoryList = AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(SearchBean.class).where(SearchBeanDao.Properties.Type.eq(2)).orderDesc(SearchBeanDao.Properties.Updatetime).list();
        if (!mHistoryList.isEmpty()) {
            mHistoryLl.setVisibility(View.VISIBLE);
            mSearchLl.setVisibility(View.GONE);
            mHistoryAdapter.refresh(mHistoryList);
        }
    }

    /**
     * 清空数据路查询线路记录
     */
    private void deleteHistoryByDb() {
        AnyWalkingApplication.getInstance().getDaoSession().getSearchBeanDao().queryBuilder().where(SearchBeanDao.Properties.Type.eq(2)).buildDelete().executeDeleteWithoutDetachingEntities();
        if (!mHistoryList.isEmpty()) {
            mHistoryList.clear();
            mHistoryAdapter.refresh(mHistoryList);
            mHistoryLl.setVisibility(View.GONE);
            mSearchLl.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 根据类型请求数据
     */
    private void requestSearchDataByType(String filed) {
        if (TextUtils.isEmpty(UserInfoCacheUtil.getUserToken(mContext))) {
            ToastUtil.showToast(mContext, getString(R.string.user_info_error));
            return;
        }
        //所在城市
        if (GlobalVariables.A_MAP_LOCATION == null) {
            ToastUtil.showToast(mContext, getString(R.string.no_location));
            return;
        }
        if (TextUtils.isEmpty(GlobalVariables.A_MAP_LOCATION.getCity())) {
            ToastUtil.showToast(mContext, getString(R.string.no_location));
            return;
        }
        mHistoryLl.setVisibility(View.GONE);
        mSearchLl.setVisibility(View.VISIBLE);
        mPresenter.requestSearchStationData(mContext, UserInfoCacheUtil.getUserToken(mContext),
                filed, GlobalVariables.A_MAP_LOCATION.getCity());
    }

    @Override
    public void showError(String errorMsg) {
        Log.v(GlobalConstants.LOG_TAG, errorMsg);
    }

    /**
     * 显示查询模糊站台数据
     */
    @Override
    public void showSearchStationData(List<SearchStationInfoBean> stationData) {
        if (stationData != null && stationData.size() > 0) {
            mStationData = stationData;
            mStationAdapter.refresh(stationData);
        }
    }

}

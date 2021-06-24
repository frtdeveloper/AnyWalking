package com.lenwotion.travel.activity.searchbus;

import android.content.Intent;
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
import com.lenwotion.travel.activity.searchbus.presenter.SearchBusPresenter;
import com.lenwotion.travel.adapter.search.SearchHistoryAdapter;
import com.lenwotion.travel.adapter.search.SearchLineAdapter;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.search.SearchLineInfoBean;
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
 * 查询线路界面
 * Created by fq on 2017/11/30.
 */
public class SearchLineActivity extends BaseActivity implements SearchBusContract.ShowSearchLineView {

    private ClearEditText mSearchEt;
    private ListView mSearchLv;
    private Button mClearHistoryBt;
    private LinearLayout mHistoryLl;
    private SearchBusPresenter mPresenter;
    /**
     * 查询线路列表适配器
     */
    private SearchLineAdapter mSearchAdapter;
    /**
     * 历史记录列表适配器
     */
    private SearchHistoryAdapter mHistorysAdapter;
    /**
     * 搜索历史记录数据
     */
    private List<SearchBean> mHistorysList;
    /**
     * 查询线路数据
     */
    private List<SearchLineInfoBean> mSearchList;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.search_line));
        setContentView(R.layout.activity_search_bus);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        mPresenter = new SearchBusPresenter(this);
        mSearchList = new ArrayList<>();
        mHistorysList = new ArrayList<>();
    }

    private void initView() {
        mSearchEt = findViewById(R.id.et_search);
        mSearchLv = findViewById(R.id.lv_search);
        mClearHistoryBt = findViewById(R.id.bt_clear);
        mHistoryLl = findViewById(R.id.ll_history);
        // 历史记录
        mHistorysAdapter = new SearchHistoryAdapter(mContext, mHistorysList);
        ListView historyLv = findViewById(R.id.lv_history);
        historyLv.setAdapter(mHistorysAdapter);
        historyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mHistorysList.isEmpty()) {
                    String content = mHistorysList.get(position).getHistoryword();
                    mSearchEt.setText(content);
                }
            }
        });
        // 查询
        mSearchAdapter = new SearchLineAdapter(mContext, mSearchList);
        mSearchLv.setAdapter(mSearchAdapter);
        mSearchEt.setHint("请输入线路名称");
        // 数据库获取历史记录
        getLineHistoryByDb();
        initSearch();
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
                    if (!mSearchList.isEmpty()) {
                        mSearchList.clear();
                        mSearchAdapter.refresh(mSearchList);
                        if (!mHistorysList.isEmpty()) {
                            mHistoryLl.setVisibility(View.VISIBLE);
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
        mSearchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //保存数据库：选择的线路或者站台
                SearchLineInfoBean bean = mSearchList.get(position);
                String searchValue = bean.getLineName();
                if (!TextUtils.isEmpty(searchValue)) {
                    saveSearchDataToDb(searchValue);
                }
                //跳转线路详情界面
                Intent intent = new Intent(mContext, SearchLineInfoActivity.class);
                intent.putExtra("lineName", searchValue);
                intent.putExtra("direction", bean.getDirection());
                startActivity(intent);
                finish();
            }
        });
    }

    private void initListener() {
        mClearHistoryBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_clear:
                delectHistoryByDb();
                break;
        }
    }

    /**
     * 从数据库获取查询线路历史记录
     */
    private void getLineHistoryByDb() {
        // 按照:1、类别；2、时间升序
        mHistorysList = AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(SearchBean.class).where(SearchBeanDao.Properties.Type.eq(1)).orderDesc(SearchBeanDao.Properties.Updatetime).list();
        if (!mHistorysList.isEmpty()) {
            Log.v(GlobalConstants.LOG_TAG, "历史记录" + mHistorysList.size());
            for (int i = 0; i < mHistorysList.size(); i++) {
                String title = mHistorysList.get(i).getHistoryword();
                Log.v(GlobalConstants.LOG_TAG, "历史记录" + title);
            }
            mHistoryLl.setVisibility(View.VISIBLE);
            mHistorysAdapter.refresh(mHistorysList);
        }
    }

    /**
     * 清空数据路查询线路记录
     */
    private void delectHistoryByDb() {
        AnyWalkingApplication.getInstance().getDaoSession().getSearchBeanDao().queryBuilder().where(SearchBeanDao.Properties.Type.eq(1)).buildDelete().executeDeleteWithoutDetachingEntities();
        if (!mHistorysList.isEmpty()) {
            mHistorysList.clear();
            mHistorysAdapter.refresh(mHistorysList);
            mHistoryLl.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 请求数据
     */
    private void requestSearchDataByType(String filed) {
        String userToken = UserInfoCacheUtil.getUserToken(mContext);
        if (TextUtils.isEmpty(userToken)) {
            ToastUtil.showToast(mContext, getString(R.string.user_info_error));
            return;
        }
        //所在城市
        if (GlobalVariables.A_MAP_LOCATION == null) {
            return;
        }
        String city = GlobalVariables.A_MAP_LOCATION.getCity();
        if (TextUtils.isEmpty(city)) {
            city = "深圳";
        }
        Log.v(GlobalConstants.LOG_TAG, "requestSearchDataByType--filed===" + filed);
        mPresenter.requestSearchLineData(mContext, userToken, filed, city);
    }

    /**
     * 保存选择线路或站台到数据库
     */
    private void saveSearchDataToDb(String data) {
        // 先数据库查询是否有这个线路或站台
        SearchBean findBean = AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(SearchBean.class).where(SearchBeanDao.Properties.Historyword.eq(data)).build().unique();
        if (findBean != null) {
            findBean.setUpdatetime(CommonUtil.getTime());
            AnyWalkingApplication.getInstance().getDaoSession().update(findBean);
        } else {
            SearchBean searchBean = new SearchBean();
            searchBean.setType(GlobalConstants.TYPE_SEARCH_LINE);
            searchBean.setHistoryword(data);
            searchBean.setUpdatetime(CommonUtil.getTime());
            AnyWalkingApplication.getInstance().getDaoSession().insert(searchBean);
        }
    }

    @Override
    public void showError(String errorMsg) {
        Log.v(GlobalConstants.LOG_TAG, errorMsg);
    }

    @Override
    public void showSearchLineData(List<SearchLineInfoBean> lineData) {
        mHistoryLl.setVisibility(View.GONE);
        if (!lineData.isEmpty()) {
            mSearchList = lineData;
            mSearchAdapter.refresh(lineData);
        }
    }

}

package com.lenwotion.travel.activity.navigation;

import android.os.Bundle;
import android.widget.ListView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.adapter.SimpleSelectAdapter;
import com.lenwotion.travel.utils.DataSaveUtil;

import java.util.List;

/**
 * 模拟导航文字列表页面
 * Created by john on 2017/11/27
 */
public class NavigationPlanningActivity extends BaseActivity {

    /**
     * 模拟导航文字列表
     */
    private List<String> mNavigationTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.emulator_navigation_planning));
        setContentView(R.layout.activity_navigation_planning);
        initData();
        initView();
    }

    private void initData() {
        mContext = this;
        mNavigationTextList = DataSaveUtil.getNavigationPlanningList();
    }

    private void initView() {
        SimpleSelectAdapter simpleSelectAdapter = new SimpleSelectAdapter(mContext, mNavigationTextList);
        ListView navigationTextLv = findViewById(R.id.lv_navigation_text);
        navigationTextLv.setAdapter(simpleSelectAdapter);
    }

}

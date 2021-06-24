package com.lenwotion.travel.activity.navigation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.services.core.PoiItem;
import com.lenwotion.travel.R;
import com.lenwotion.travel.adapter.PoiResultAdapter;
import com.lenwotion.travel.adapter.SimpleSelectAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 周边搜索页面
 * Created by john on 2017/11/27
 */
public class NearbySearchActivity extends BaseSearchActivity {

    /**
     * 周边搜索选项列表LV (POI类别)
     */
    private ListView mNearbySearchLv;

    private List<String> mPoiTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.nearby_search));
        setContentView(R.layout.activity_nearby_search);
        initData();
        initView();
    }

    private void initData() {
        mContext = this;
        mPoiTypeList = new ArrayList<>();
        mPoiTypeList.add("推拿");
        mPoiTypeList.add("美食");
        mPoiTypeList.add("酒店");
        mPoiTypeList.add("银行");
        mPoiTypeList.add("公交");
        mPoiTypeList.add("地铁");
        mPoiTypeList.add("超市");
        mPoiTypeList.add("商场");
        mPoiTypeList.add("KTV");
        mPoiTypeList.add("医院");
        mPoiTypeList.add("药店");
        mPoiTypeList.add("厕所");
    }

    private void initView() {
        mNearbySearchLv = findViewById(R.id.lv_nearby_search);
        SimpleSelectAdapter simpleSelectAdapter = new SimpleSelectAdapter(mContext, mPoiTypeList);
        mNearbySearchLv.setAdapter(simpleSelectAdapter);

        mNearbySearchLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                poiSearch(mPoiTypeList.get(position), true);
            }
        });
    }

    @Override
    public void hasPoiResult(List<PoiItem> poiItems) {
        PoiResultAdapter poiResultAdapter = new PoiResultAdapter(mContext, poiItems);
        mNearbySearchLv.setAdapter(poiResultAdapter);
        poiResultAdapter.notifyDataSetChanged();
    }

}

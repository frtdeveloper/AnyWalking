//package com.lenwotion.travel.activity.navigation;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ListView;
//
//import com.amap.api.services.core.PoiItem;
//import com.lenwotion.travel.R;
//import com.lenwotion.travel.adapter.PoiResultAdapter;
//import com.lenwotion.travel.utils.AMapUtil;
//
//import java.util.List;
//
///**
// * POI搜索界面
// * Created by john on 2017/11/27
// */
//public class PoiSearchActivity extends BaseSearchActivity implements TextWatcher {
//
//    /**
//     * POI搜索结果列表LV
//     */
//    private ListView mPoiSearchResultLv;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_poi_search);
//        initData();
//        initView();
//    }
//
//    private void initData() {
//        mContext = this;
//    }
//
//    private void initView() {
//        EditText poiSearchEt = findViewById(R.id.et_poi_search);
//        Button nearbySearchBt = findViewById(R.id.bt_nearby_search);
//        mPoiSearchResultLv = findViewById(R.id.lv_poi_search_result);
//
//        poiSearchEt.addTextChangedListener(this);
//        nearbySearchBt.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//
//            // 周边搜索
//            case R.id.bt_nearby_search:
//                startActivity(new Intent(mContext, NearbySearchActivity.class));
//                break;
//
//            default:
//                break;
//
//        }
//    }
//
//    @Override
//    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//    }
//
//    @Override
//    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//        String newText = charSequence.toString().trim();
//        if (!AMapUtil.IsEmptyOrNullString(newText)) {
//            poiSearch(newText);
//        }
//    }
//
//    @Override
//    public void afterTextChanged(Editable s) {
//
//    }
//
//    @Override
//    public void hasPoiResult(List<PoiItem> poiItems) {
//        PoiResultAdapter poiResultAdapter = new PoiResultAdapter(mContext, poiItems);
//        mPoiSearchResultLv.setAdapter(poiResultAdapter);
//        poiResultAdapter.notifyDataSetChanged();
//    }
//
//}

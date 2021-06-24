package com.lenwotion.travel.fragment.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.amap.api.services.core.PoiItem;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.navigation.BaseSearchFragment;
import com.lenwotion.travel.activity.navigation.NearbySearchActivity;
import com.lenwotion.travel.adapter.PoiResultAdapter;
import com.lenwotion.travel.utils.AMapUtil;

import java.util.List;

/**
 * 去哪儿页面
 * Created by john on 2018/12/21.
 */
public class WhereGoFragment extends BaseSearchFragment implements TextWatcher {

    /**
     * fragment布局
     */
    private View mView;
    /**
     * POI搜索结果列表LV
     */
    private ListView mPoiSearchResultLv;

    private Button mNearbySearchBt;

    public static WhereGoFragment newInstance() {
        return new WhereGoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_poi_search, container, false);
        initData();
        initView();
        return mView;
    }

    private void initData() {
        mContext = getActivity();
    }

    private void initView() {
        EditText poiSearchEt = mView.findViewById(R.id.et_poi_search);
        mNearbySearchBt = mView.findViewById(R.id.bt_nearby_search);
        mPoiSearchResultLv = mView.findViewById(R.id.lv_poi_search_result);

        poiSearchEt.addTextChangedListener(this);
        mNearbySearchBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 周边搜索
            case R.id.bt_nearby_search:
                startActivity(new Intent(mContext, NearbySearchActivity.class));
                break;

            default:
                break;

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        String newText = charSequence.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            mNearbySearchBt.setVisibility(View.GONE);
            mPoiSearchResultLv.setVisibility(View.VISIBLE);
            poiSearch(newText, false);
        } else {
            mNearbySearchBt.setVisibility(View.VISIBLE);
            mPoiSearchResultLv.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void hasPoiResult(List<PoiItem> poiItems) {
        PoiResultAdapter poiResultAdapter = new PoiResultAdapter(mContext, poiItems);
        mPoiSearchResultLv.setAdapter(poiResultAdapter);
        poiResultAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

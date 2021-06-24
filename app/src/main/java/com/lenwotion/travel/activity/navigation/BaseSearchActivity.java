package com.lenwotion.travel.activity.navigation;

import android.os.Bundle;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.ToastUtil;

import java.util.List;

public abstract class BaseSearchActivity extends BaseActivity implements PoiSearch.OnPoiSearchListener {

    /**
     * Poi查询条件类
     */
    private PoiSearch.Query mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    /**
     * 开始搜索
     */
    public void poiSearch(String keyWord, boolean isNear) {
        if ("".equals(keyWord)) {
            ToastUtil.showToast(mContext, getString(R.string.input_null));
        } else {
            doSearchQuery(keyWord, isNear);
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keyWord, boolean isNear) {
        if (GlobalVariables.A_MAP_LOCATION.getCity() == null) {
            ToastUtil.showToast(mContext, getString(R.string.no_location));
            return;
        }
        //getBaseProgressDialog().show();
        int currentPage = 0;
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        mQuery = new PoiSearch.Query(keyWord, "", GlobalVariables.A_MAP_LOCATION.getCity());
        // 设置每页最多返回多少条poiitem
        mQuery.setPageSize(30);
        // 设置查第一页
        mQuery.setPageNum(currentPage);

        PoiSearch poiSearch = new PoiSearch(mContext, mQuery);
        poiSearch.setOnPoiSearchListener(this);
        // 设置周边搜索的中心点以及半径
        // 5000M的半径，高德推荐范围
        // 与关键字检索的唯一区别需要通过 PoiSearch 的 setBound 方法设置圆形查询范围
        if (isNear) {
            poiSearch.setBound(
                    new PoiSearch.SearchBound(
                            new LatLonPoint(
                                    GlobalVariables.A_MAP_LOCATION.getLatitude(),
                                    GlobalVariables.A_MAP_LOCATION.getLongitude()),
                            5 * 1000));
        }
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        //getBaseProgressDialog().dismiss();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            // 搜索poi的结果
            if (result != null && result.getQuery() != null) {
                // 是否是同一条
                if (result.getQuery().equals(mQuery)) {
                    // 取得搜索到的poiitems有多少页
                    // 取得第一页的poiitem数据，页数从数字0开始
                    List<PoiItem> poiItems = result.getPois();

                    if (poiItems != null && poiItems.size() > 0) {
                        // 搜索有结果
                        hasPoiResult(poiItems);
                    } else {
                        ToastUtil.showToast(mContext, getString(R.string.no_search_result));
                    }
                }
            } else {
                ToastUtil.showToast(mContext, getString(R.string.no_search_result));
            }
        } else {
            // code 见 AMapException
            ToastUtil.showToast(mContext, getString(R.string.no_search_result) + rCode);
        }
    }

    public abstract void hasPoiResult(List<PoiItem> poiItems);

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}

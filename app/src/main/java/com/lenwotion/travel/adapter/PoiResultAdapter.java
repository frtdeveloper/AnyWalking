package com.lenwotion.travel.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.navigation.NavigationTypeActivity;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.global.GlobalVariables;
import com.lenwotion.travel.utils.ToastUtil;

import java.util.List;

/**
 * Created by john on 2017/11/27.
 */
public class PoiResultAdapter extends BaseAdapter {

    private Context mContext;
    private List<PoiItem> mPoiItemList;

    public PoiResultAdapter(Context context, List<PoiItem> list) {
        mContext = context;
        mPoiItemList = list;
    }

    @Override
    public int getCount() {
        return mPoiItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPoiItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHoler;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_poi_result, viewGroup, false);
            viewHoler = new ViewHolder();
            viewHoler.poiItemLl = view.findViewById(R.id.ll_poi_item);
            viewHoler.poiNameTv = view.findViewById(R.id.tv_poi_title);
            viewHoler.poiDistanceTv = view.findViewById(R.id.tv_poi_distance);
            view.setTag(viewHoler);
        } else {
            viewHoler = (ViewHolder) view.getTag();
        }

        final PoiItem poiItem = mPoiItemList.get(position);
        viewHoler.poiNameTv.setText(poiItem.getTitle());
        if (poiItem.getDistance() > 0) {//POI搜索不会返回距离，周边搜索才会返回距离
            viewHoler.poiDistanceTv.setText(poiItem.getDistance() + " 米");
        }

        // 开始导航监听
        viewHoler.poiItemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude());
                if (isDistanceTooLong(latLng)) {//检查步行距离
                    ToastUtil.showToast(mContext, mContext.getString(R.string.walk_distance_too_long));
                    return;
                }
                Intent intent = new Intent(mContext, NavigationTypeActivity.class);
                intent.putExtra("latLng", latLng);
                intent.putExtra("positionTitle", poiItem.getTitle());
                mContext.startActivity(intent);
            }
        });

        return view;
    }

    /**
     * 检查步行距离，是否太远
     */
    private boolean isDistanceTooLong(LatLng latLng) {
        float distance = AMapUtils.calculateLineDistance(
                new LatLng(GlobalVariables.A_MAP_LOCATION.getLatitude(), GlobalVariables.A_MAP_LOCATION.getLongitude()),
                latLng);
        Log.v(GlobalConstants.LOG_TAG, "步行导航直线距离:" + distance / 1000 + "KM");
        return distance > 50 * 1000;//直线距离50KM，西乡到深圳北17KM
    }

    public class ViewHolder {
        TextView poiNameTv;
        TextView poiDistanceTv;
        LinearLayout poiItemLl;
    }

}

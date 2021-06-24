package com.lenwotion.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.bean.bybus.StationInfoBean;

import java.util.List;

/**
 * 站台选择适配器
 * Created by fq on 2017/8/26.
 */
public class SelectStationAdapter extends BaseAdapter {

    private Context mContext;
    private List<StationInfoBean> mStationInfoBeanList;

    public SelectStationAdapter(Context context, List<StationInfoBean> stationList) {
        mContext = context;
        mStationInfoBeanList = stationList;
    }

    public void refresh(List<StationInfoBean> stationList) {
        mStationInfoBeanList = stationList;
        notifyDataSetChanged();
    }

//    public List<StationInfoBean> getData() {
//        return mStationInfoBeanList;
//    }

    @Override
    public int getCount() {
        return mStationInfoBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return mStationInfoBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        StationInfoBean infoBean = mStationInfoBeanList.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_station, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.stationNameTv = view.findViewById(R.id.tv_station_name);
            viewHolder.stationDistanceTv = view.findViewById(R.id.tv_station_distance);
            viewHolder.setRemindTv = view.findViewById(R.id.tv_set_remind);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.stationDistanceTv.setVisibility(View.GONE);
        viewHolder.setRemindTv.setVisibility(View.GONE);
        String stationName = infoBean.getStation() + " " + mContext.getString(R.string.bus_station) + " " + infoBean.getFlag();
        viewHolder.stationNameTv.setText(stationName);
        return view;
    }

    private class ViewHolder {
        TextView stationNameTv;
        TextView stationDistanceTv;
        TextView setRemindTv;
    }

}

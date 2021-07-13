package com.lenwotion.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.adapter.search.SearchLineAdapter;
import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;

import java.util.ArrayList;

public class WaitBusesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AffirmWaitInfoBean> mWaitBusesList;

    public WaitBusesAdapter(Context ctx, ArrayList<AffirmWaitInfoBean> buses_list) {
        mContext = ctx;
        mWaitBusesList = buses_list;
    }

    public void refresh(ArrayList<AffirmWaitInfoBean> data) {
        mWaitBusesList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (null == mWaitBusesList ? 0 : mWaitBusesList.size());
    }

    @Override
    public Object getItem(int position) {
        return mWaitBusesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AffirmWaitInfoBean current_bean = mWaitBusesList.get(position);
        ViewHoler viewHoler;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wait_bus_info_layout, parent, false);
            viewHoler = new ViewHoler();
            viewHoler.mLineNameTV = convertView.findViewById(R.id.bus_info_linename_tv);
            viewHoler.mLineNameTV.setText(current_bean.getLineName());

            viewHoler.mStateCountTV = convertView.findViewById(R.id.bus_info_statecount_tv);
            String frontStations = "距离 " + current_bean.getFrontStations() + " 站";
            viewHoler.mStateCountTV.setText(frontStations);

            viewHoler.mCurrentStopTV = convertView.findViewById(R.id.bus_info_current_stop_tv);
            String current_stop = "当前到达: " + current_bean.getCurrentStation();
            viewHoler.mCurrentStopTV.setText(current_stop);

            viewHoler.mNextStopTV = convertView.findViewById(R.id.bus_info_distance_tv);
            String current_distance;
            if (current_bean.getFrontStations() > 0) {
                current_distance = "距离你还有 " + current_bean.getFrontStations() + " 个站";
            } else if (current_bean.getFrontStations() == 0) {
                current_distance = "车辆已到站";
            } else if (current_bean.getFrontStations() == -999) {
                current_distance = "暂无到站信息";
            } else if (current_bean.getFrontStations() < 0) {
                current_distance = "车辆已过站，系统将重新预约车辆";
            } else {
                current_distance = "暂无到站信息";
            }
            viewHoler.mCurrentStopTV.setText(current_distance);

            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHoler {
        TextView mLineNameTV;
        TextView mStateCountTV;
        TextView mCurrentStopTV;
        TextView mNextStopTV;
    }
}

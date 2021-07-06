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
            viewHoler.mStateCountTV = convertView.findViewById(R.id.bus_info_statecount_tv);
            viewHoler.mCurrentStopTV = convertView.findViewById(R.id.bus_info_currentstop_tv);
            viewHoler.mNextStopTV = convertView.findViewById(R.id.bus_info_nextstop_tv);
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

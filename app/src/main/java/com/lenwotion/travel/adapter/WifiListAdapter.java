//package com.lenwotion.travel.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.lenwotion.travel.R;
//
//import java.util.List;
//
//public class WifiListAdapter extends BaseAdapter {
//
//    private Context mContext;
//    private List<String> wifiList;
//
//    public WifiListAdapter(Context context, List<String> wifiList) {
//        mContext = context;
//        this.wifiList = wifiList;
//    }
//
//    @Override
//    public int getCount() {
//        return wifiList.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return wifiList.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int position, View view, ViewGroup viewGroup) {
//        ViewHolder viewHolder;
//        if (view == null) {
//            view = LayoutInflater.from(mContext).inflate(R.layout.adapter_sle_station, viewGroup, false);
//            viewHolder = new ViewHolder();
//            viewHolder.tv_station_name = view.findViewById(R.id.tv_station_name);
//            view.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) view.getTag();
//        }
//
//        String wifiSSID = wifiList.get(position);
//        viewHolder.tv_station_name.setText(wifiSSID);
//        return view;
//    }
//
//    private class ViewHolder {
//        TextView tv_station_name;
//    }
//
//}

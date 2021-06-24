package com.lenwotion.travel.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.reminder.ArriveReminderBean;
import com.lenwotion.travel.bean.search.StationInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fq on 2017/11/28.
 */
public class SearchLineDirectionAdapter extends BaseAdapter {

    private Context mContext;
    private List<StationInfoBean> mData;
    // 用来控制的选中状况
    //private static HashMap<Integer, Boolean> isSelected;
    private OnItemSetListener mListener;
    private List<String> mStationNameList;

    public SearchLineDirectionAdapter(Context context, List<StationInfoBean> data) {
        mContext = context;
        mData = data;
        //isSelected = new HashMap<>();
    }

    public void refresh(List<StationInfoBean> data) {
        mData = data;
        //initDate();
        notifyDataSetChanged();
    }

//    // 初始化isSelected的数据
//    private void initDate() {
//        for (int i = 0; i < mData.size(); i++) {
//            getIsSelected().put(i, false);
//        }
//    }

//    private static HashMap<Integer, Boolean> getIsSelected() {
//        return isSelected;
//    }
//
//    private static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
//        SearchLineDirectionAdapter.isSelected = isSelected;
//    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        //获取当前实体类对象
        final StationInfoBean item = mData.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_station, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.setRemindersRl = convertView.findViewById(R.id.rl_remind);
            viewHolder.lineWayNameTv = convertView.findViewById(R.id.tv_station_name);
            viewHolder.setRemindersTv = convertView.findViewById(R.id.tv_set_remind);
            viewHolder.distanceTv = convertView.findViewById(R.id.tv_station_distance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mStationNameList.contains(item.getStationName())) {
            viewHolder.setRemindersTv.setText(mContext.getString(R.string.is_set_remind));
            viewHolder.setRemindersTv.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
        } else {
            viewHolder.setRemindersTv.setText(mContext.getString(R.string.set_remind));
            viewHolder.setRemindersTv.setTextColor(mContext.getResources().getColor(R.color.blue));
        }
        viewHolder.distanceTv.setVisibility(View.GONE);
        viewHolder.lineWayNameTv.setText(item.getStationName());
        viewHolder.setRemindersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isSelected.put(position, true);
//                setIsSelected(isSelected);
//                viewHolder.setRemindersTv.setText(mContext.getString(R.string.is_set_remind));
//                viewHolder.setRemindersTv.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
                if (mListener != null) {
                    mListener.onItemSet(item);
                }
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    class ViewHolder {
        RelativeLayout setRemindersRl;
        TextView lineWayNameTv;
        TextView setRemindersTv;
        TextView distanceTv;
    }

    public void setOnItemSetListener(OnItemSetListener listener) {
        mListener = listener;
    }

    public interface OnItemSetListener {
        void onItemSet(StationInfoBean bean);
    }

    @Override
    public void notifyDataSetChanged() {
        mStationNameList = getRemindDataByDb();
        super.notifyDataSetChanged();
    }

    /**
     * 从数据库里获取提醒数据
     */
    private List<String> getRemindDataByDb() {
        List<String> stationList = new ArrayList<>();
        List<ArriveReminderBean> infoList = AnyWalkingApplication.getInstance().getDaoSession().loadAll(ArriveReminderBean.class);
        if (infoList.isEmpty()) {
            return stationList;
        } else {
            for (ArriveReminderBean bean : infoList) {
                stationList.add(bean.getStationName());
            }
            return stationList;
        }
    }

}

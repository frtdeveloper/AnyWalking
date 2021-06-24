package com.lenwotion.travel.adapter.search;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.searchbus.interfaces.IStationSelectCallback;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.reminder.ArriveReminderBean;
import com.lenwotion.travel.bean.search.SearchStationInfoBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.greendao.ArriveReminderBeanDao;
import com.lenwotion.travel.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 模糊搜索站台
 * Created by fq on 2017/11/24.
 */

public class SearchStationAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<SearchStationInfoBean> mData;
    private List<String> mStationNameList;
    private IStationSelectCallback mCallback;

    public SearchStationAdapter(Context context, List<SearchStationInfoBean> data, IStationSelectCallback callback) {
        mContext = context;
        mData = data;
        mCallback = callback;
        inflater = LayoutInflater.from(mContext);
    }

    public void refresh(List<SearchStationInfoBean> data) {
        mData = data;
        notifyDataSetChanged();
    }

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
        SearchStationInfoBean stationInfoBean = mData.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_station, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.setRemindersRl = convertView.findViewById(R.id.rl_remind);
            viewHolder.searchContentTv = convertView.findViewById(R.id.tv_station_name);
            viewHolder.stationDistanceTv = convertView.findViewById(R.id.tv_station_distance);
            viewHolder.setRemindersTv = convertView.findViewById(R.id.tv_set_remind);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mStationNameList.contains(stationInfoBean.getStation1())) {
            viewHolder.setRemindersTv.setText(mContext.getString(R.string.is_set_remind));
            viewHolder.setRemindersTv.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
        } else {
            viewHolder.setRemindersTv.setText(mContext.getString(R.string.set_remind));
            viewHolder.setRemindersTv.setTextColor(mContext.getResources().getColor(R.color.blue));
        }
        viewHolder.stationDistanceTv.setVisibility(View.GONE);
        viewHolder.searchContentTv.setText(stationInfoBean.getStation1());
        viewHolder.setRemindersRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onItemSelect(position);
                //设置提醒
                SearchStationInfoBean item = mData.get(position);
                //插入数据库
                saveReminderStationToDB(item);
                ToastUtil.showToast(mContext, mContext.getString(R.string.set_remind_success));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private void saveReminderStationToDB(SearchStationInfoBean item) {
        if (!TextUtils.isEmpty(item.getLat1()) && !TextUtils.isEmpty(item.getLng1())) {
            //先查询数据库中是否包含此站台
            ArriveReminderBean findStation1 = findStationByDB(1, item.getStation1());
            if (findStation1 == null) {
                ArriveReminderBean bean1 = new ArriveReminderBean();
                bean1.setType(GlobalConstants.TYPE_SEARCH_STATION);
                bean1.setWayType(1);
                bean1.setStationName(item.getStation1());
                bean1.setLat(item.getLat1());
                bean1.setLng(item.getLng1());
                bean1.setIsRemind(true);
                AnyWalkingApplication.getInstance().getDaoSession().insert(bean1);
                Log.v(GlobalConstants.LOG_TAG, "站台1插入成功");
            } else {
                Log.v(GlobalConstants.LOG_TAG, "站台1已存在");
            }
        }
        if (!TextUtils.isEmpty(item.getLat2()) && !TextUtils.isEmpty(item.getLng2())) {
            ArriveReminderBean findStation2 = findStationByDB(2, item.getStation2());
            if (findStation2 == null) {
                ArriveReminderBean bean2 = new ArriveReminderBean();
                bean2.setType(GlobalConstants.TYPE_SEARCH_STATION);
                bean2.setWayType(2);
                bean2.setStationName(item.getStation2());
                bean2.setLat(item.getLat2());
                bean2.setLng(item.getLng2());
                bean2.setIsRemind(true);
                AnyWalkingApplication.getInstance().getDaoSession().insert(bean2);
                Log.v(GlobalConstants.LOG_TAG, "站台2插入成功");
            } else {
                Log.v(GlobalConstants.LOG_TAG, "站台2已存在");
            }
        }
        // 发送广播给到提醒界面
        //mContext.sendBroadcast(new Intent(GlobalConstants.SEND_COMPUTATIONS_DISTANCE));
    }

    private ArriveReminderBean findStationByDB(int type, String stationName) {
        return AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(ArriveReminderBean.class)
                .where(ArriveReminderBeanDao.Properties.StationName.eq(stationName))
                .where(ArriveReminderBeanDao.Properties.WayType.eq(type)).build().unique();
    }

    static class ViewHolder {
        RelativeLayout setRemindersRl;
        TextView searchContentTv;
        TextView stationDistanceTv;
        TextView setRemindersTv;
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
        List<String> stationNameList = new ArrayList<>();
        List<ArriveReminderBean> infoList = AnyWalkingApplication.getInstance().getDaoSession().loadAll(ArriveReminderBean.class);
        if (infoList.isEmpty()) {
            return stationNameList;
        } else {
            for (ArriveReminderBean bean : infoList) {
                stationNameList.add(bean.getStationName());
            }
            return stationNameList;
        }
    }

}

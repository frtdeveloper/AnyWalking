package com.lenwotion.travel.adapter.reminder;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.reminder.ArriveReminderBean;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.utils.ToastUtil;

import java.util.List;

/**
 * Created by fq on 2017/12/7.
 */
public class ArrivalStationAdapter extends BaseAdapter {

    private Context mContext;
    private List<ArriveReminderBean> mList;
    // 用来控制的选中状况
    //private static HashMap<Integer, Boolean> mIsSelected;
    // private int mCancelNum = 0; // 记录选中的条目数量
    // onReminderItemListener mListener;
    private Vibrator mVibrator;

    public ArrivalStationAdapter(List<ArriveReminderBean> list, Context context) {
        mList = list;
        mContext = context;
        //mIsSelected = new HashMap<>();
        //initDate();
        // 获取Vibrator震动服务
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

//    public void setRemindData(List<ArriveReminderBean> data) {
//        mData = data;
//        // 初始化数据
//        initDate();
//        //notifyDataSetChanged();
//    }

//    // 初始化isSelected的数据
//    private void initDate() {
//        for (int i = 0; i < mList.size(); i++) {
//            getIsSelected().put(i, false);
//        }
//    }

//    private static HashMap<Integer, Boolean> getIsSelected() {
//        return mIsSelected;
//    }
//
//    private static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
//        mIsSelected = isSelected;
//    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHoler viewHoler;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_station, parent, false);
            viewHoler = new ViewHoler();
            viewHoler.reminderNameTv = convertView.findViewById(R.id.tv_station_name);
            viewHoler.reminderDistanceTv = convertView.findViewById(R.id.tv_station_distance);
            viewHoler.reminderCancelTv = convertView.findViewById(R.id.tv_set_remind);
            viewHoler.reminderRl = convertView.findViewById(R.id.rl_remind);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) convertView.getTag();
        }

        final ArriveReminderBean bean = mList.get(position);

        viewHoler.reminderNameTv.setText(bean.getStationName());

        // 如果距离小于N米，则到站提醒
        if (bean.getDirection() < 100) {//到站提醒
            mVibrator.vibrate(5 * 1000);//震动提醒
            cancelNotification(position);
            //viewHoler.reminderDirectionTv.setText("距离：" + bean.getDirection() + "米");
            //bean.setIsArrive(true);
            //saveArrivalDataToDb(bean.getStationName(), bean.getWayType());
            ToastUtil.showToast(mContext, "已到达站台：" + bean.getStationName());
        } else {
            viewHoler.reminderDistanceTv.setText("距离:" + bean.getDirection() + "米");
            //bean.setIsArrive(false);
        }

        viewHoler.reminderCancelTv.setText(mContext.getString(R.string.cancel_remind));

//        //根据状态设置还是取消
//        if (!bean.getIsRemind()) {//设置提醒
//            viewHoler.reminderNotTv.setText("设置提醒");
//            mIsSelected.put(position, true);
//            setIsSelected(mIsSelected);
//        } else {//取消提醒
//            viewHoler.reminderNotTv.setText("取消提醒");
//            mIsSelected.put(position, false);
//            setIsSelected(mIsSelected);
//        }

        viewHoler.reminderRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNotification(position);
//                if (mIsSelected.get(position)) {
//                    mIsSelected.put(position, false);
//                    setIsSelected(mIsSelected);
//                    bean.setIsRemind(true);
////                    if (mCancelNum != 0) {
////                        mCancelNum--;
////                    }
//                    viewHoler.reminderNotTv.setText("取消提醒");
//                } else {
//                    mIsSelected.put(position, true);
//                    setIsSelected(mIsSelected);
//                    bean.setIsRemind(false);
//                    //mCancelNum++;
//                    viewHoler.reminderNotTv.setText("设置提醒");
//                }
//                if (mListener != null) {
//                    mListener.onReminder(position, mCancelNum, item);
//                }
            }
        });
        return convertView;
    }

    private void cancelNotification(int position) {
        AnyWalkingApplication.getInstance().getDaoSession().delete(mList.get(position));
        mContext.sendBroadcast(new Intent(GlobalConstants.INTENT_MAP_LOCATION_CHANGED));
//        if (mList.size() > position) {
//            AnyWalkingApplication.getInstance().getDaoSession().delete(mList.get(position));
//            mContext.sendBroadcast(new Intent(GlobalConstants.INTENT_MAP_LOCATION_CHANGED));
////            mList.remove(mList.get(position));
////            notifyDataSetChanged();
//        }
//        if (mList.size() == 0) {
//            notifyDataSetChanged();
//        }
    }

    class ViewHoler {
        TextView reminderNameTv;
        TextView reminderDistanceTv;
        TextView reminderCancelTv;
        RelativeLayout reminderRl;
    }

//    public void setOnReminderItemListener(onReminderItemListener listener) {
//        mListener = listener;
//    }

//    public interface onReminderItemListener {
//        void onReminder(int position, int cancelNum, ArriveReminderBean item);
//    }

//    /**
//     * 保存已到达的站台到数据库
//     */
//    private void saveArrivalDataToDb(String stationName, int wayType) {
//        Log.v(GlobalConstants.LOG_TAG, "stationName:" + stationName + "--wayType:" + wayType);
//        // 先检查是否包含了这条数据
//        ArriveReminderBean findBean = AnyWalkingApplication.getInstance().getDaoSession().queryBuilder(ArriveReminderBean.class)
//                .where(ArriveReminderBeanDao.Properties.StationName.eq(stationName),
//                        ArriveReminderBeanDao.Properties.WayType.eq(wayType),
//                        ArriveReminderBeanDao.Properties.IsRemind.eq(false)).build().unique();
//        if (findBean != null) {
//            findBean.setIsArrive(true);
//            findBean.setIsRemind(true);
//            AnyWalkingApplication.getInstance().getDaoSession().getArriveReminderBeanDao().update(findBean);
//        } else {
//            Log.v(GlobalConstants.LOG_TAG, "ArriveReminderBean==null");
//        }
//    }

}

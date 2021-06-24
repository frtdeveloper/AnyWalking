package com.lenwotion.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.bean.bybus.StationLineInfoBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fq on 2017/8/26.
 */

public class SelectLineAdapter extends BaseAdapter {

    private Context mContext;
    private List<StationLineInfoBean> mData;
    private onSelctLineListener mListener;
    private ArrayList<String> mLineNameList = new ArrayList<>();
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> mIsSelected;

    public SelectLineAdapter(Context context, List<StationLineInfoBean> data) {
        mContext = context;
        mData = data;
        mIsSelected = new HashMap<>();
    }

    public void refresh(List<StationLineInfoBean> data) {
        mData = data;
        // 初始化数据
        initDate();
        notifyDataSetChanged();
    }

//    public List<StationLineInfoBean> getData() {
//        return mData;
//    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < mData.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    private static HashMap<Integer, Boolean> getIsSelected() {
        return mIsSelected;
    }

    private static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        mIsSelected = isSelected;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHoler viewHoler;
        final StationLineInfoBean infoBean = mData.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_select_line, viewGroup, false);
            viewHoler = new ViewHoler();
            viewHoler.lineNameTv = view.findViewById(R.id.tv_line_name);
            viewHoler.lineEndTv = view.findViewById(R.id.tv_line_end);
            viewHoler.selectCb = view.findViewById(R.id.cb_select);
            viewHoler.lineRl = view.findViewById(R.id.rl_line);
            view.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) view.getTag();
        }
        viewHoler.lineNameTv.setText(infoBean.getLineName() + " 路车");
        if (infoBean.getTerminal().contains("-")) {
            String index = infoBean.getTerminal().substring(0, infoBean.getTerminal().indexOf("-"));
            viewHoler.lineEndTv.setText(index);
        } else {
            viewHoler.lineEndTv.setText(infoBean.getTerminal());
        }

        // 根据isSelected来设置checkbox的选中状况
        viewHoler.lineRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsSelected.get(position)) {
                    mIsSelected.put(position, false);
                    setIsSelected(mIsSelected);
                    if (mLineNameList.contains(infoBean.getLineName())) {
                        mLineNameList.remove(infoBean.getLineName());
                    }
                } else {
                    mIsSelected.put(position, true);
                    setIsSelected(mIsSelected);
                    if (mLineNameList.contains(infoBean.getLineName())) {
                        mLineNameList.set(position, infoBean.getLineName());
                    } else {
                        mLineNameList.add(infoBean.getLineName());
                    }
                }
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onSelect(mLineNameList);
                }
            }
        });
        // 根据isSelected来设置checkbox的选中状况
        viewHoler.selectCb.setChecked(getIsSelected().get(position));
        return view;
    }

    private class ViewHoler {
        TextView lineNameTv;
        TextView lineEndTv;
        CheckBox selectCb;
        RelativeLayout lineRl;
    }

    public void setOnSelctLineListener(onSelctLineListener listener) {
        mListener = listener;
    }

    public interface onSelctLineListener {
        void onSelect(ArrayList<String> lineNameList);
    }

//    /**
//     * 对比当前当前时间与选择路线起时间、末时间：如果超过选择路线时间，提示
//     */
//    private boolean comparisonTime(StationLineInfoBean infoBean) {
//        String curr = CommonUtil.getCurrMinuteTime();
//        String origin = infoBean.getServerStartTm();
//        String end = infoBean.getServerEndTm();
//        if (CommonUtil.compareCurrTimeAndOriginTime(curr, origin)) {
//            ToastUtil.showToast(mContext, "早于起始班车时间，暂时无班车乘坐");
//            return false;
//        }
//        if (CommonUtil.compareCurrTimeAndEndTime(curr, end)) {
//            ToastUtil.showToast(mContext, "超过末班车时间，无班车乘坐");
//            return false;
//        }
//        return true;
//    }

}

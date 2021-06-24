package com.lenwotion.travel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenwotion.travel.R;

import java.util.List;

/**
 * 简单选择类型列表适配器
 */
public class SimpleSelectAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mSelectItemTextList;

    public SimpleSelectAdapter(Context context, List<String> list) {
        mContext = context;
        mSelectItemTextList = list;
    }

    @Override
    public int getCount() {
        return mSelectItemTextList.size();
    }

    @Override
    public Object getItem(int i) {
        return mSelectItemTextList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_select, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.selectItemTextTv = view.findViewById(R.id.tv_select_item_text);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String selectItemText = mSelectItemTextList.get(position);
        viewHolder.selectItemTextTv.setText(selectItemText);
        return view;
    }

    private class ViewHolder {
        TextView selectItemTextTv;
    }

}

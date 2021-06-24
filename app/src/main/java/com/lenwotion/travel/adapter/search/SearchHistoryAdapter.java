package com.lenwotion.travel.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.bean.search.db.SearchBean;

import java.util.List;

/**
 * 搜索历史记录列表适配器
 * Created by fq on 2017/11/25.
 */
public class SearchHistoryAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchBean> mData;

    public SearchHistoryAdapter(Context context, List<SearchBean> data) {
        mContext = context;
        mData = data;
    }

    public void refresh(List<SearchBean> data) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchBean bean = mData.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_simple_select, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.historyTv = convertView.findViewById(R.id.tv_select_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.historyTv.setText(bean.getHistoryword());
        return convertView;
    }

    private static class ViewHolder {
        private TextView historyTv;
    }

}

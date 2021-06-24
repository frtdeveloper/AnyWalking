package com.lenwotion.travel.adapter.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.bean.search.SearchLineInfoBean;

import java.util.List;

/**
 * 模糊搜索线路列表适配器
 * Created by fq on 2017/11/24.
 */
public class SearchLineAdapter extends BaseAdapter {

    private Context mContext;
    private List<SearchLineInfoBean> mData;

    public SearchLineAdapter(Context context, List<SearchLineInfoBean> mData) {
        mContext = context;
        this.mData = mData;
    }

    public void refresh(List<SearchLineInfoBean> data) {
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
        SearchLineInfoBean bean = mData.get(position);
        ViewHoler viewHoler;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_bus, parent, false);
            viewHoler = new ViewHoler();
            viewHoler.mSearchContentTv = convertView.findViewById(R.id.tv_searchContent);
            viewHoler.mLineDirectionTv = convertView.findViewById(R.id.tv_line_direction);
            convertView.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) convertView.getTag();
        }
        viewHoler.mSearchContentTv.setText(bean.getLineName());
        viewHoler.mLineDirectionTv.setText(bean.getDirection());
        return convertView;
    }

    static class ViewHoler {
        TextView mSearchContentTv;
        TextView mLineDirectionTv;
    }
}

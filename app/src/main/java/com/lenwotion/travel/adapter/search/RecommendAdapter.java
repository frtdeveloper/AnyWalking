//package com.lenwotion.travel.adapter.search;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.lenwotion.travel.R;
//import com.lenwotion.travel.bean.search.RecommendInfoBean;
//import com.lenwotion.travel.view.RemoteImageView;
//
//import java.util.ArrayList;
//
///**
// * Created by fq on 2017/12/1.
// */
//public class RecommendAdapter extends BaseAdapter {
//
//    private Context mContext;
//    private ArrayList<RecommendInfoBean> mData;
//    private LayoutInflater inflater;
//    private onItemRecommendListener mListener;
//
//    public RecommendAdapter(Context context, ArrayList<RecommendInfoBean> data) {
//        mContext = context;
//        mData = data;
//        inflater = LayoutInflater.from(mContext);
//    }
//
//    public void setData(ArrayList<RecommendInfoBean> data) {
//        mData = data;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return mData.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        final RecommendInfoBean bean = mData.get(position);
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.adapter_recommend, null, false);
//            viewHolder = new ViewHolder();
//            viewHolder.iv_recommend = convertView.findViewById(R.id.iv_recommend);
//            viewHolder.tv_recommend_title = convertView.findViewById(R.id.tv_recommend_title);
//            viewHolder.tv_recommend_described = convertView.findViewById(R.id.tv_recommend_described);
//            viewHolder.btn_install = convertView.findViewById(R.id.btn_install);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        viewHolder.iv_recommend.setImageUri(R.mipmap.ic_launcher, bean.getPicture());
//        viewHolder.tv_recommend_title.setText(bean.getVersionName() == null ? "标题" : bean.getVersionName());
//        viewHolder.tv_recommend_described.setText(bean.getDescribed() == null ? "描述" : bean.getDescribed());
//        viewHolder.btn_install.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mListener != null) {
//                    mListener.onRecommend(bean);
//                }
//            }
//        });
//        return convertView;
//    }
//
//
//    class ViewHolder {
//        RemoteImageView iv_recommend;
//        TextView tv_recommend_title;
//        TextView tv_recommend_described;
//        TextView btn_install;
//    }
//
//    public void setOnItemRecommendListener(onItemRecommendListener listener) {
//        mListener = listener;
//    }
//
//    public interface onItemRecommendListener {
//        void onRecommend(RecommendInfoBean bean);
//    }
//
//}

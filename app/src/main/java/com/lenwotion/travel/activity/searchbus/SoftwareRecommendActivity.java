//package com.lenwotion.travel.activity.searchbus;
//
//import android.os.Bundle;
//import android.widget.ListView;
//
//import com.lenwotion.travel.R;
//import com.lenwotion.travel.activity.BaseActivity;
//import com.lenwotion.travel.adapter.search.RecommendAdapter;
//import com.lenwotion.travel.bean.search.RecommendInfoBean;
//
//import java.util.ArrayList;
//
///**
// * 精品软件推荐
// * Created by fq on 2017/12/1.
// */
//
//public class SoftwareRecommendActivity extends BaseActivity {
//
//    private ListView lv_software_recommend;
//    private RecommendAdapter adapter;
//    private ArrayList<RecommendInfoBean> reminderBeanList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_recommend);
//        initData();
//        initView();
//    }
//
//    private void initData() {
//        mContext = this;
//        reminderBeanList = new ArrayList<>();
//        if (getIntent() != null) {
//            reminderBeanList = getIntent().getParcelableArrayListExtra("reminderList");
//        }
//    }
//
//    private void initView() {
//        lv_software_recommend = (ListView) findViewById(R.id.lv_software_recommend);
//        if (!reminderBeanList.isEmpty()) {
//            adapter = new RecommendAdapter(mContext, reminderBeanList);
//            lv_software_recommend.setAdapter(adapter);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (!reminderBeanList.isEmpty()) {
//            reminderBeanList.clear();
//            reminderBeanList = null;
//        }
//    }
//
//}

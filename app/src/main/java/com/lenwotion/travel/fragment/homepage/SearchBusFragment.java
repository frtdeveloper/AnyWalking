package com.lenwotion.travel.fragment.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.searchbus.SearchLineActivity;
import com.lenwotion.travel.activity.searchbus.SearchStationActivity;
import com.lenwotion.travel.fragment.BaseFragment;

/**
 * 查询页面
 * Created by john on 2016/3/17.
 */
public class SearchBusFragment extends BaseFragment {

    /**
     * fragment布局
     */
    private View mView;
    private Button mSearchLineBt;
    private Button mSearchStationBt;
//    private TextView mMoreTv;
//    /**
//     * 精品软件推荐模块  recommend
//     */
//    private LinearLayout mRecommendLl;
//    private RecommendAdapter mRecommendAdapter;
//    private ArrayList<RecommendInfoBean> mReminderBeanList;

    public static SearchBusFragment newInstance() {
        return new SearchBusFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_search_bus, container, false);
        initData();
        initView();
        initListener();
        initFunction();
        return mView;
    }

    private void initData() {
        mContext = getActivity();
        //mReminderBeanList = new ArrayList<>();
    }

    private void initView() {
        mSearchLineBt = mView.findViewById(R.id.btn_searchLine);
        mSearchStationBt = mView.findViewById(R.id.btn_searchStation);
//        mMoreTv = mView.findViewById(R.id.tv_more);

        // mRecommendLl = mView.findViewById(R.id.ll_recommend);
//        mRecommendAdapter = new RecommendAdapter(mContext, mReminderBeanList);
//        mRecommendAdapter.setOnItemRecommendListener(new RecommendAdapter.onItemRecommendListener() {
//            @Override
//            public void onRecommend(RecommendInfoBean bean) {
//                if (bean != null) {
//                    appUpdate(bean.getUrl(), bean.getVersionName());
//                }
//            }
//        });

//        ListView mRecommendLv = mView.findViewById(R.id.lv_recommend);
//        mRecommendLv.setAdapter(mRecommendAdapter);
    }

    private void initListener() {
        mSearchLineBt.setOnClickListener(this);
        mSearchStationBt.setOnClickListener(this);
//        mMoreTv.setOnClickListener(this);
    }

    private void initFunction() {
        // getReminderListData();
    }

//    private void showLog(String msg) {
//        Log.v(GlobalConstants.LOG_TAG, msg);
//    }

//    /**
//     * 开始下载apk
//     */
//    private void appUpdate(String downloadUrl, String versionName) {
//        //如果手机已经启动下载程序，执行downloadApk。否则跳转到设置界面
//        if (DownLoadUtil.getInstance(mContext.getApplicationContext()).canDownload()) {
//            DownloadApkUtil.downloadApk(mContext.getApplicationContext(), downloadUrl, versionName);
//        } else {
//            DownLoadUtil.getInstance(mContext.getApplicationContext()).skipToDownloadManager();
//        }
//    }

//    /**
//     * 获取精品软件推荐数据
//     */
//    private void getReminderListData() {
//        ISearchService searchService = AnyWalkingApplication.getInstance().getRetrofit().create(ISearchService.class);
//        Call<RecommendBean> call = searchService.getReminderSoftwareData();
//        call.enqueue(new Callback<RecommendBean>() {
//            @Override
//            public void onResponse(Call<RecommendBean> call, Response<RecommendBean> response) {
//                if (response.isSuccessful()) {
//                    RecommendBean bean = response.body();
//                    if (bean != null) {
//                        if (bean.isFlag()) {
//                            fetchRecommendListSuccess(bean);
//                        } else {
//                            // 获取服务器信息失败
//                            fetchRecommendListFail(bean.getMsg());
//                        }
//                    } else {
//                        // http 错误
//                        fetchRecommendListFail(String.valueOf(response.code()));
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<RecommendBean> call, Throwable throwable) {
//                // 超时
//                fetchRecommendListFail("超时==" + throwable.toString());
//            }
//        });
//    }
//
//    /**
//     * 获取精品软件数据成功
//     */
//    private void fetchRecommendListSuccess(RecommendBean bean) {
//        showLog("检查精品软件成功：" + bean.getMsg());
//        mReminderBeanList = bean.getData();
//        if (!mReminderBeanList.isEmpty()) {
//            mRecommendLl.setVisibility(View.VISIBLE);
//            mRecommendAdapter.setData(mReminderBeanList);
//        }
//    }
//
//    /**
//     * 获取精品软件数据失败
//     */
//    private void fetchRecommendListFail(String errMsg) {
//        showLog("超时：" + errMsg);
//        ToastUtil.showToast(mContext, "获取精品软件失败:" + errMsg);
//    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // 查询线路
            case R.id.btn_searchLine:
                intent = new Intent(mContext, SearchLineActivity.class);
                startActivity(intent);
                break;
            // 查询站台
            case R.id.btn_searchStation:
                intent = new Intent(mContext, SearchStationActivity.class);
                startActivity(intent);
                break;
//            // 查看更多
//            case R.id.tv_more:
//                Intent intent = new Intent(mContext, SoftwareRecommendActivity.class);
//                intent.putParcelableArrayListExtra("reminderList", mReminderBeanList);
//                startActivity(intent);
//                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (mReminderBeanList != null) {
//            mReminderBeanList.clear();
//            mReminderBeanList = null;
//        }
    }

}

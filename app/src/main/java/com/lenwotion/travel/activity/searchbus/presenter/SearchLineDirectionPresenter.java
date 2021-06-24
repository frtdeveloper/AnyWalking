package com.lenwotion.travel.activity.searchbus.presenter;

import android.content.Context;

import com.lenwotion.travel.activity.searchbus.contract.SearchLineDirectionContract;
import com.lenwotion.travel.activity.searchbus.model.SearchLineDirectionModle;
import com.lenwotion.travel.bean.search.SearchLineDirectionResponseBean;
import com.lenwotion.travel.interfaces.ICallBackListener;

/**
 * Created by fq on 2017/11/25.
 */

public class SearchLineDirectionPresenter implements SearchLineDirectionContract.SearchLineDirectionPresenter {

    private SearchLineDirectionContract.ShowSearchLineDirectionResultView mView;
    private SearchLineDirectionModle mModle;

    public SearchLineDirectionPresenter(SearchLineDirectionContract.ShowSearchLineDirectionResultView view) {
        mView = view;
        mModle = new SearchLineDirectionModle();
    }

    @Override
    public void requestSearchLineDirectionData(Context context, String userToken, String line, String city) {
        if (mView == null) {
            return;
        }
        //mView.showProgress();
        mModle.getSearchLineDirectionData(context, userToken, line, city, new ICallBackListener<SearchLineDirectionResponseBean>() {
            @Override
            public void onSuccess(SearchLineDirectionResponseBean result) {
                //mView.hideProgress();
                mView.showSearchLineDirectionData(result.getData());
            }

            @Override
            public void onFailure(String errorMsg) {
                //mView.hideProgress();
                mView.showError("请求查询线路上下行失败：" + errorMsg);
            }
        });
    }

}

package com.lenwotion.travel.activity.searchbus.contract;

import android.content.Context;

import com.lenwotion.travel.bean.search.SearchLineDirectionInfoBean;
import com.lenwotion.travel.interfaces.ICallBackListener;
import com.lenwotion.travel.view.BaseView;

/**
 * 查询线路上下行
 * Created by fq on 2017/11/25.
 */

public interface SearchLineDirectionContract {

    interface ShowSearchLineDirectionResultView extends BaseView {
        /**
         * 显示查询线路上下行数据
         */
        void showSearchLineDirectionData(SearchLineDirectionInfoBean data);
    }

    interface SearchLineDirectionModle {
        /**
         * 获取查询线路上下行数据
         */
        void getSearchLineDirectionData(Context context, String userToken, String line, String city, ICallBackListener listener);
    }

    interface SearchLineDirectionPresenter {
        /**
         * 请求查询线路上下行数据
         */
        void requestSearchLineDirectionData(Context context, String userToken, String line, String city);
    }

}

package com.lenwotion.travel.bean.search;
import com.lenwotion.travel.bean.GeneralResponseBean;
import java.util.List;

/**
 *  模糊搜索线路
 * Created by fq on 2017/11/24.
 */

public class SearchLineResponseBean extends GeneralResponseBean {

    /**
     * code : 1001
     * data : ["M330","M363","M344","345"]
     * flag : true
     * msg : 操作成功
     */
    private List<SearchLineInfoBean> data;

    public List<SearchLineInfoBean> getData() {
        return data;
    }

    public void setData(List<SearchLineInfoBean> data) {
        this.data = data;
    }
}

package com.lenwotion.travel.bean.search;

import com.lenwotion.travel.bean.GeneralResponseBean;

import java.util.List;

/**
 * 模糊搜索站台
 * Created by fq on 2017/11/24.
 */

public class SearchStationResponseBean extends GeneralResponseBean{

    /**
     * code : 1001
     * data : [{"lat1":"22.621205","lat2":"22.621121","lng1":"113.869661","lng2":"113.869639","station":"西乡华佳工业园"},{"lat1":"22.539955","lat2":"22.540333","lng1":"114.086717","lng2":"114.085313","station":"华强北地铁接驳站"}]
     * flag : true
     * msg : 操作成功
     */

    private List<SearchStationInfoBean> data;

    public List<SearchStationInfoBean> getData() {
        return data;
    }

    public void setData(List<SearchStationInfoBean> data) {
        this.data = data;
    }
}

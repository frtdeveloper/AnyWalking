package com.lenwotion.travel.bean.bybus;
import com.lenwotion.travel.bean.GeneralResponseBean;

import java.util.List;

/**
 * 站台路线
 * Created by fq on 2017/91.
 */

public class StationLineBean extends GeneralResponseBean{
    private List<StationLineInfoBean> data;

    public List<StationLineInfoBean> getData() {
        return data;
    }

    public void setData(List<StationLineInfoBean> data) {
        this.data = data;
    }
}

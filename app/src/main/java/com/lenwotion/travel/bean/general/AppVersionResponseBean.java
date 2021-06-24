package com.lenwotion.travel.bean.general;
import com.lenwotion.travel.bean.GeneralResponseBean;

/**
 * app版本检测实体类
 * Created by fq on 2017/11/30.
 */
public class AppVersionResponseBean extends GeneralResponseBean {

    // 版本信息
    private AppVersionBean data;

    public AppVersionBean getData() {
        return data;
    }

    public void setData(AppVersionBean data) {
        this.data = data;
    }
}

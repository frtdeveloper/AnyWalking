package com.lenwotion.travel.bean.bybus;

import com.lenwotion.travel.bean.GeneralResponseBean;

/**
 * 上传GPS
 * Created by fq on 2017/11/16.
 */

public class UploadGPSBean extends GeneralResponseBean{
    private ProcessWaitingVOBean data;

    public ProcessWaitingVOBean getData() {
        return data;
    }

    public void setData(ProcessWaitingVOBean data) {
        this.data = data;
    }
}

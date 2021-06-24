package com.lenwotion.travel.bean.search;

import com.lenwotion.travel.bean.GeneralResponseBean;

/**
 * Created by fq on 2017/11/25.
 */

public class SearchLineDirectionResponseBean extends GeneralResponseBean{

    /**
     * code : 1001
     * data : {"upWay":["恒生医院-A","玉湖湾花园-A","碧海公馆-A","圣淘沙-A","永丰路口-A","坪洲地铁站-A"],"downWay":["深圳北站-B","民治花场-B","南贤广场-B","民治邮局-B","塘水围村-B","华富路1-B","华强北地铁接驳站-B"]}
     * flag : true
     * msg : 操作成功
     */

    private SearchLineDirectionInfoBean data;

    public SearchLineDirectionInfoBean getData() {
        return data;
    }

    public void setData(SearchLineDirectionInfoBean data) {
        this.data = data;
    }
}

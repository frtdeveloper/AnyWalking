package com.lenwotion.travel.bean.search.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by fq on 2017/11/27.
 */
@Entity
public class SearchBean {

    @Id(autoincrement = true)
    private Long id;
    /**
     * 区分线路、站台
     */
    private int type;
    /**
     * 历史记录字段
     */
    private String historyword;
    /**
     * 更新的时间，用于排序
     */
    private long updatetime;

    @Generated(hash = 1377017843)
    public SearchBean(Long id, int type, String historyword, long updatetime) {
        this.id = id;
        this.type = type;
        this.historyword = historyword;
        this.updatetime = updatetime;
    }

    @Generated(hash = 562045751)
    public SearchBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHistoryword() {
        return this.historyword;
    }

    public void setHistoryword(String historyword) {
        this.historyword = historyword;
    }

    public long getUpdatetime() {
        return this.updatetime;
    }

    public void setUpdatetime(long updatetime) {
        this.updatetime = updatetime;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

}

package com.lenwotion.travel.bean.general;

/**
 * 获取app版本返回实体类
 * Created by John on 2016/4/25.
 */
public class AppVersionBean {
    /**
     * 新版本版本号，如3
     */
    private int versionCode;
    /**
     * 新版本版本名，如1.0.1
     */
    private String versionName;
    /**
     * 新版本APP下载链接
     */
    private String downloadUrl;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

}

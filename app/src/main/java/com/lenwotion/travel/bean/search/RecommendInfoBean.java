package com.lenwotion.travel.bean.search;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  软件推荐信息实体类
 * Created by fq on 2017/12/1.
 */

public class RecommendInfoBean implements Parcelable {
    /**
     * 内容描述
     */
    private String described;
    /**
     * 图片链接
     */
    private String picture;
    /**
     * 软件名
     */
    private String versionName;
    /**
     * Apk链接
     */
    private String url;

    protected RecommendInfoBean(Parcel in) {
        described = in.readString();
        picture = in.readString();
        versionName = in.readString();
        url = in.readString();
    }

    public static final Creator<RecommendInfoBean> CREATOR = new Creator<RecommendInfoBean>() {
        @Override
        public RecommendInfoBean createFromParcel(Parcel in) {
            return new RecommendInfoBean(in);
        }

        @Override
        public RecommendInfoBean[] newArray(int size) {
            return new RecommendInfoBean[size];
        }
    };

    public String getDescribed() {
        return described;
    }

    public void setDescribed(String described) {
        this.described = described;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(described);
        parcel.writeString(picture);
        parcel.writeString(versionName);
        parcel.writeString(url);
    }
}

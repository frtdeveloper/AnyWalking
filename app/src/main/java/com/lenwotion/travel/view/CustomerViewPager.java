package com.lenwotion.travel.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义ViewPager，主要是禁用了触摸左右滑动事件
 * Created by John on 2017/4/6.
 */

public class CustomerViewPager extends ViewPager {

    public CustomerViewPager(Context context) {
        super(context);
    }

    public CustomerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 不消耗触摸滑动的事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    /**
     * 不消耗触摸滑动的事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

}

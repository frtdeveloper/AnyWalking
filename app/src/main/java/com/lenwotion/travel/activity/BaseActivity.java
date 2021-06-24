package com.lenwotion.travel.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.lenwotion.travel.R;

/**
 * 基类Activity
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Context mContext;
    protected ActionBar mActionBar;
    protected TextView mActionBarTv;
//    /**
//     * 全局基类ProgressDialog
//     */
//    private ProgressDialog mBaseProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        // 使用竖屏显示，高度大于宽度
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 默认软键盘不弹出|键盘覆盖屏幕
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // 自定义ActionBar
        setCustomActionBar();
    }

    /**
     * 自定义ActionBar
     */
    private void setCustomActionBar() {
        mActionBar = this.getSupportActionBar();
        if (mActionBar == null) {
            return;
        }
        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.item_action_bar, null);
        mActionBarTv = view.findViewById(R.id.tv_action_bar);
        mActionBar.setCustomView(view, layoutParams);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);
        //显示返回箭头
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 显示标题
     */
    protected void setActionBarTitle(String title) {
        if (mActionBar != null && mActionBarTv != null) {
            mActionBarTv.setText(title);
        }
    }

    /**
     * 禁用返回箭头
     */
    protected void setDisplayHomeAsUpNotEnabled() {
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * 返回箭头功能
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {

    }

//    /**
//     * 获取ProgressDialog对象
//     */
//    public ProgressDialog getBaseProgressDialog() {
//        if (mBaseProgressDialog == null) {
//            mBaseProgressDialog = new ProgressDialog(this);
//            mBaseProgressDialog.setCancelable(true);
//            mBaseProgressDialog.setCanceledOnTouchOutside(false);
//        }
//        return mBaseProgressDialog;
//    }

    /**
     * 隐藏软键盘
     */
    protected void hideSoftInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager == null) {
                return;
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        hideSoftInput();
        super.onDestroy();
    }

}

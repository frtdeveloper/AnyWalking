package com.lenwotion.travel.fragment.homepage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.login.LoginActivity;
import com.lenwotion.travel.activity.login.RegisterActivity;
import com.lenwotion.travel.activity.me.AboutActivity;
import com.lenwotion.travel.activity.me.SuggestionActivity;
import com.lenwotion.travel.fragment.BaseFragment;
import com.lenwotion.travel.interfaces.IAccountService;
import com.lenwotion.travel.utils.SharedPreferencesUtil;
import com.lenwotion.travel.utils.UserInfoCacheUtil;

/**
 * 我的页面
 * Created by john on 2016/3/17.
 */
public class MeFragment extends BaseFragment {

    /**
     * fragment布局
     */
    private View mView;
    /**
     * 修改密码
     */
    private LinearLayout mReviseLl;
    /**
     * 建议Ll
     */
    private LinearLayout mSuggestionLl;
    /**
     * 关于Ll
     */
    private LinearLayout mAboutLl;
    /**
     * 注销Ll
     */
    private LinearLayout mLogoutLl;
    /**
     * 用户名Tv
     */
    private TextView mUserNameTv;

    public static MeFragment newInstance() {
        return new MeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_me, container, false);
        initData();
        initView();
        initResultView();
        initListener();
        return mView;
    }

    private void initData() {
        mContext = getActivity();
    }

    private void initView() {
        mUserNameTv = mView.findViewById(R.id.tv_me_username);
        mSuggestionLl = mView.findViewById(R.id.ll_me_suggestion);
        mAboutLl = mView.findViewById(R.id.ll_me_about);
        mLogoutLl = mView.findViewById(R.id.ll_me_logout);
        mReviseLl = mView.findViewById(R.id.ll_me_revise);
    }

    private void initResultView() {
        mUserNameTv.setText(UserInfoCacheUtil.getUserPhoneNum(mContext));
    }

    private void initListener() {
        mSuggestionLl.setOnClickListener(this);
        mAboutLl.setOnClickListener(this);
        mLogoutLl.setOnClickListener(this);
        mReviseLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_me_suggestion:
                startActivity(new Intent(mContext, SuggestionActivity.class));
                break;
            case R.id.ll_me_about:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
            case R.id.ll_me_logout:
                showLogoutDialog();
                break;
            case R.id.ll_me_revise:
                //修改密码
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                intent.putExtra("type", IAccountService.TYPE_RESET_PASSWORD);
                startActivity(intent);
                break;
        }
    }

    /**
     * 注销确认dialog
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.confirm_logout))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    /**
     * 注销
     */
    private void logout() {
        // 清除用户信息
        SharedPreferencesUtil.clearSharedPreferences(mContext);
        startActivity(new Intent(mContext, LoginActivity.class));
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

}

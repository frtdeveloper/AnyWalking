package com.lenwotion.travel.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * fragment基类
 * Created by john on 2016/3/17.
 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    /**
//     * 获取ProgressDialog对象
//     */
//    public ProgressDialog getBaseProgressDialog() {
//        if (getActivity() == null) {
//            return null;
//        }
//        return ((BaseActivity) getActivity()).getBaseProgressDialog();
//    }

    @Override
    public void onClick(View v) {

    }

}

package com.lenwotion.travel.activity.bybus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.activity.bybus.contract.WaitingBusContract;
import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;
import com.lenwotion.travel.bean.bybus.UploadGPSBean;
import com.lenwotion.travel.netty.interfaces.INettyMessageCallback;

import java.util.ArrayList;

public class MutilWaitBusActivity extends BaseActivity
                                  implements INettyMessageCallback,
                                             WaitingBusContract.ShowWaitResultView {

    private Button mCancelWaitingBt;
    private ImageView mDeviceConnectedIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.wait_bus_arrive_station));
        setContentView(R.layout.activity_mutil_wait_bus);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void showError(String errorMsg) {

    }

    @Override
    public void showUploadGPSData(UploadGPSBean data) {

    }

    @Override
    public void channelRead(String message) {

    }

    @Override
    public void channelActive() {

    }

    @Override
    public void channelInactive() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }



    private static String MY_INTENT = "com.lenwotion.travel.activity.bybus.START_M_WAIT_BUSES";
    private static ArrayList<AffirmWaitInfoBean> WAITING_BUSES_INFO;

    /**
     * 设置需要等待的线路信息
     * @param wait_bean_info
     * @param need_clean
     */
    public static void addWaittingBuses(AffirmWaitInfoBean wait_bean_info, boolean need_clean) {
        if(null == WAITING_BUSES_INFO) {
            WAITING_BUSES_INFO = new ArrayList<>();
        }

        if (need_clean){
            WAITING_BUSES_INFO.clear();
            WAITING_BUSES_INFO = null;
            WAITING_BUSES_INFO = new ArrayList<>();
        }

        WAITING_BUSES_INFO.add(wait_bean_info);
    }

    /**
     * 启动这个Activity
     * @param ctx
     * @param need_finish
     */
    public static void startMySelf(Activity ctx, boolean need_finish){
        Intent start_intent = new Intent();
        start_intent.setAction(MY_INTENT);
        start_intent.addCategory(Intent.CATEGORY_DEFAULT);
        ctx.startActivity(start_intent);

        if (need_finish)
            ctx.finish();
    }

}
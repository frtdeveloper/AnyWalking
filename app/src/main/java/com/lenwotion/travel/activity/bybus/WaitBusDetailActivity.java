package com.lenwotion.travel.activity.bybus;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lenwotion.travel.R;
import com.lenwotion.travel.bean.bybus.AffirmWaitInfoBean;

public class WaitBusDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_bus_detail);
    }

    private static String MY_ACTION = "com.lenwotion.travel.activity.bybus.START_WAIT_BUS_DETAIL";
    /**
     * 启动这个Activity
     * @param ctx
     * @param need_finish
     */
    public static void startMySelf(Activity ctx, boolean need_finish, AffirmWaitInfoBean bus_bean){
        Intent start_intent = new Intent();
        start_intent.setAction(MY_ACTION);
        start_intent.addCategory(Intent.CATEGORY_DEFAULT);
        ctx.startActivity(start_intent);

        if (need_finish)
            ctx.finish();
    }
}
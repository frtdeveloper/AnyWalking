//package com.lenwotion.travel.view;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.lenwotion.travel.R;
//
///**
// * 首页公告
// * Created by fq on 2017/8/21.
// */
//
//public class NoticeDialog extends Dialog {
//
//    private Activity mContext;
//    private TextView tv_content;
//    private Button bt_sure;
//    private String noticeText;
//    private onClickSureListener mListener;
//
//    public NoticeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//        init();
//    }
//
//    public NoticeDialog(Context context, int theme) {
//        super(context, theme);
//        init();
//    }
//
//    public NoticeDialog(Context context, String noticeText) {
//        super(context);
//        this.noticeText = noticeText;
//        init();
//    }
//
//    public void init() {
//        setContentView(R.layout.dialog_notice);
//        tv_content = findViewById(R.id.tv_content);
//        bt_sure = findViewById(R.id.bt_sure);
//        tv_content.setText(noticeText);
//        bt_sure.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mListener != null)
//                    mListener.onSure();
//            }
//        });
//    }
//
//    public interface onClickSureListener {
//        void onSure();
//    }
//
//    public void setOnClickSureListener(onClickSureListener listener) {
//        mListener = listener;
//    }
//
//    @Override
//    public void show() {
//        if (isActivityActive()) {
//            try {
//                super.show();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void dismiss() {
//        if (isActivityActive()) {
//            try {
//                super.dismiss();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private boolean isActivityActive() {
//        return mContext == null || !mContext.isFinishing();
//    }
//}

//package com.lenwotion.travel.view;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.widget.TextView;
//
//import com.lenwotion.travel.R;
//
///**
// * 返回询问框
// * Created by fq on 2017/10/12.
// */
//
//public class ReturnDialog extends Dialog {
//
//    private onItemClickListener mListener;
//
////    public ReturnDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
////        super(context, cancelable, cancelListener);
////        init();
////    }
////
////    public ReturnDialog(Context context, int theme) {
////        super(context, theme);
////        init();
////    }
//
//    public ReturnDialog(Context context) {
//        super(context);
//        init();
//    }
//
//    private void init() {
//        setContentView(R.layout.dialog_return);
//        TextView cancelTv = findViewById(R.id.dialog_cancel);
//        TextView confirmTv = findViewById(R.id.dialog_confirm);
//        cancelTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dismiss();
//            }
//        });
//        confirmTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mListener != null)
//                    mListener.onItem();
//            }
//        });
//    }
//
//    public interface onItemClickListener {
//        void onItem();
//    }
//
//    public void setOnItemClickListener(onItemClickListener listener) {
//        mListener = listener;
//    }
//
//}

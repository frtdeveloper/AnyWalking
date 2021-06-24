//package com.lenwotion.travel.utils;
//
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//
///**
// * 摇一摇显示动画效果
// * Created by fq on 2017/12/11.
// */
//
//public class ShakeAnimationUtil {
//    private static ShakeAnimationUtil instance = null;
//    private boolean isBack;
//    public static ShakeAnimationUtil getInstance() {
//        if (instance == null) {
//            instance = new ShakeAnimationUtil();
//        }
//        return instance;
//    }
//
//    private boolean getIsBack() {
//        return isBack;
//    }
//
//    public void setBack(boolean back) {
//        isBack = back;
//    }
//
//    /** 摇一摇手掌上部分动画 */
//    public TranslateAnimation getUpAnim() {
//        int type = Animation.RELATIVE_TO_SELF;
//        float topFromY;
//        float topToY ;
//        if (getIsBack()) {
//            topFromY = -0.5f;
//            topToY = 0;
//        } else {
//            topFromY = 0;
//            topToY = -0.5f;
//        }
//        //上面图片的动画效果
//        TranslateAnimation topAnim = new TranslateAnimation(
//                type, 0, type, 0, type, topFromY, type, topToY
//        );
//        topAnim.setDuration(200);
//        //动画终止时停留在最后一帧~不然会回到没有执行之前的状态
//        topAnim.setFillAfter(true);
//        return topAnim;
//    }
//    /** 摇一摇手掌下部分动画 */
//    public TranslateAnimation getDownAnim(final onAnimationCompleteListener listener) {
//        //动画坐标移动的位置的类型是相对自己的
//        int type = Animation.RELATIVE_TO_SELF;
//        float bottomFromY;
//        float bottomToY;
//        if (getIsBack()) {
//            bottomFromY = 0.5f;
//            bottomToY = 0;
//        } else {
//            bottomFromY = 0;
//            bottomToY = 0.5f;
//        }
//        //底部的动画效果
//        TranslateAnimation bottomAnim = new TranslateAnimation(
//                type, 0, type, 0, type, bottomFromY, type, bottomToY
//        );
//        bottomAnim.setDuration(200);
//        bottomAnim.setFillAfter(true);
//        // 监听动画结束
//        if (getIsBack()) {
//            bottomAnim.setAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    //动画结束
//                     if(listener != null){
//                         listener.onAnimEnd();
//                     }
//                }
//            });
//        }
//        return bottomAnim;
//    }
//
//   public interface onAnimationCompleteListener{
//      void onAnimEnd();
//   }
//
//
//}

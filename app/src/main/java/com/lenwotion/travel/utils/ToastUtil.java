package com.lenwotion.travel.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author John
 * @ClassName: ToastUtils
 * @Description: Toast工具类
 * @date 2015年6月25日 下午3:30:12
 */
public class ToastUtil {

    private static Toast mToast;

    public static void showToast(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

}

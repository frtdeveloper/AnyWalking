package com.lenwotion.travel.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.lenwotion.travel.global.GlobalConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fq on 2017/4/28.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String CRASH_FILE_PATH = "/sdcard/LenwotionApp/";
    private static final String CRASH_FILE_NAME = "crash.log";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler mInstance = new CrashHandler();
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return mInstance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, final Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            Log.v(GlobalConstants.LOG_TAG, "uncaughtException--if==" + ex.getMessage());
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            Log.v(GlobalConstants.LOG_TAG, "uncaughtException--else==" + ex.getMessage());
            SystemClock.sleep(1000);
            // 用于再次启动APP，没有效果，暂时放在这
//            Intent intent = new Intent(mContext.getApplicationContext(), InitActivity.class);
//            PendingIntent restartIntent = PendingIntent.getActivity(
//                    mContext.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            // 1秒钟后重启应用
//            AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Log.v(GlobalConstants.LOG_TAG, "handleException---ex.getMessage()==" + ex.getMessage());
                // Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     */
    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.v(GlobalConstants.LOG_TAG, "an error occured when collect package info");
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.v(GlobalConstants.LOG_TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.v(GlobalConstants.LOG_TAG, "an error occured when collect crash info");
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
           /* long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";*/
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //  String path = "/sdcard/crash/";
                File dir = new File(CRASH_FILE_PATH);
                if (dir.exists()) {
                    dir.delete();
                }
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(CRASH_FILE_PATH + CRASH_FILE_NAME);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return CRASH_FILE_NAME;
        } catch (Exception e) {
            Log.v(GlobalConstants.LOG_TAG, "an error occured while writing file...");
        }
        return null;
    }

//    /**
//     * 选择采用PendingIntent的方式
//     */
//    private void restart() {
//        try {
//            Thread.sleep(2 * 1000);
//        } catch (InterruptedException e) {
//            Log.v(GlobalConstant.LOG_TAG, "error:" + e);
//        }
//        Intent mStartActivity = new Intent(mContext, InitActivity.class);
//        int mPendingIntentId = 123456;
//        PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }

//    /**
//     * 读取崩溃日记
//     */
//    public String readCrashLog() {
//        String str = "";
//        File file = new File(CRASH_FILE_PATH + CRASH_FILE_NAME);
//        if (!file.exists()) {
//            // 没有文件
//            return "";
//        }
//        try {
//            FileInputStream in = new FileInputStream(file);
//            // size为字串的长度,这里一次性读完
//            int size = in.available();
//            byte[] buffer = new byte[size];
//            in.read(buffer);
//            in.close();
//            str = new String(buffer, "utf-8");
//            return str;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    /**
//     * 删除崩溃日记
//     */
//    public void deleteCrashLog() {
//        File file = new File(CRASH_FILE_PATH + CRASH_FILE_NAME);
//        if (file.exists()) {
//            file.delete();
//        }
//    }

}

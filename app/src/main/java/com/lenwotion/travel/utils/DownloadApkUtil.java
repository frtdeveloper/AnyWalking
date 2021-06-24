package com.lenwotion.travel.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lenwotion.travel.global.GlobalConstants;

import java.io.File;

/**
 * Apk下载
 * Created by fq on 2017/11/30
 */
public class DownloadApkUtil {

    //private static ApkInstallReceiver apkInstallReceiver;

//    /**
//     * 下载APK文件
//     */
//    public static void downloadApk(Context context, String url, final String appName) {
//        //获取存储的下载ID
//        long downloadId = SharedPreferencesUtil.getLong(context, DownloadManager.EXTRA_DOWNLOAD_ID);
//        if (downloadId != 0) {
//            //存在downloadId
//            DownLoadUtil downLoadUtils = DownLoadUtil.getInstance(context);
//            //获取当前状态
//            int status = downLoadUtils.getDownloadStatus(downloadId);
//            if (DownloadManager.STATUS_SUCCESSFUL == status) {
//                //状态为下载成功
//                //获取下载路径URI
//                Uri downloadUri = downLoadUtils.getDownloadUri(downloadId);
//                if (null != downloadUri) {
//                    //存在下载的APK，如果两个APK相同，启动更新界面。否之则删除，重新下载。
//                    if (compare(getApkInfo(context, downloadUri.getPath()), context)) {
//                        //startInstall(context, downloadUri);
//                        start(context, url, appName);
//                        return;
//                    } else {
//                        //删除下载任务以及文件
//                        downLoadUtils.getDownloadManager().remove(downloadId);
//                    }
//                }
//                start(context, url, appName);
//            } else if (DownloadManager.STATUS_FAILED == status) {
//                //下载失败,重新下载
//                start(context, url, appName);
//                Log.v(GlobalConstants.LOG_TAG, "apk下载失败,重新下载");
//            } else {
//                Log.v(GlobalConstants.LOG_TAG, "apk is already downloading");
//            }
//        } else {
//            //不存在downloadId，没有下载过APK
//            Log.v(GlobalConstants.LOG_TAG, "不存在downloadId，没有下载过APK");
//            start(context, url, appName);
//        }
//    }

//    /**
//     * 开始下载
//     */
//    private static void start(Context context, String url, String appName) {
//        if (hasSDKCard()) {
//            long id = DownLoadUtil.getInstance(context).download(url, appName);
//            // 把当前下载的ID保存起来
//            SharedPreferencesUtil.setLong(context, DownloadManager.EXTRA_DOWNLOAD_ID, id);
//        } else {
//            Toast.makeText(context, "手机未安装SD卡，下载失败", Toast.LENGTH_LONG).show();
//        }
//    }

//    public static void registerBroadcast(Context context) {
//        apkInstallReceiver = new ApkInstallReceiver();
//        context.registerReceiver(apkInstallReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//    }
//
//    public static void unregisterBroadcast(Context context) {
//        if (null != apkInstallReceiver) {
//            context.unregisterReceiver(apkInstallReceiver);
//        }
//    }

//    /**
//     * 检查APK安装权限，针对8.0以上
//     */
//    private void checkCanInstall(Context context, long downloadId) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0，需要检查安装APK的权限
//            Log.v(GlobalConstants.LOG_TAG, "android >= 8.0");
//            boolean canInstall = context.getPackageManager().canRequestPackageInstalls();
//            if (canInstall) {
//                Log.v(GlobalConstants.LOG_TAG, "canInstall");
//                DownloadApkBusiness.startInstall(context, downloadId);
//            } else {
//                Log.v(GlobalConstants.LOG_TAG, "请求安装未知应用来源的权限");
//                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
//                context.startActivity(intent);
//            }
//        } else {
//            Log.v(GlobalConstants.LOG_TAG, "android < 8.0");
//            DownloadApkBusiness.startInstall(context, downloadId);
//        }
//    }
//
//    /**
//     * 跳转到安装界面
//     */
//    private static void startInstall(Context context, Uri uri) {
//        Intent install = new Intent(Intent.ACTION_VIEW);
//        install.setDataAndType(uri, "application/vnd.android.package-archive");
//        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(install);
//    }

    /**
     * 获取APK程序信息
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (null != pi) {
            return pi;
        }
        return null;
    }

    /**
     * 比较两个APK的信息
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (null == apkInfo) {
            return false;
        }
        String localPackageName = context.getPackageName();
        if (localPackageName.equals(apkInfo.packageName)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackageName, 0);
                //比较当前APK和下载的APK版本号
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    //如果下载的APK版本号大于当前安装的APK版本号，返回true
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

//    /**
//     * 是否存在SD卡
//     */
//    private static boolean hasSDKCard() {
//        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
//    }

    /**
     * 删除已下载的文件
     */
    public static void removeFile(Context context) {
        String filePath = SharedPreferencesUtil.getString(context, "downloadApk");
        Log.v(GlobalConstants.LOG_TAG, "APK已删除");
        File downloadFile = new File(filePath);
        if (downloadFile.exists()) {
            //删除之前先判断用户是否已经安装了，安装了才删除。
            if (!compare(getApkInfo(context, filePath), context)) {
                downloadFile.delete();
                Log.v(GlobalConstants.LOG_TAG, "APK delete--已删除");
            }
        }
    }

}

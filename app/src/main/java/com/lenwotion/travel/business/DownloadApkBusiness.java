package com.lenwotion.travel.business;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.lenwotion.travel.R;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.utils.FileUtil;
import com.lenwotion.travel.utils.SharedPreferencesUtil;

import java.io.File;

public class DownloadApkBusiness {

    /**
     * 开始下载apk
     */
    public static void downloadAPK(Context context, String downloadLink) {
        // 设置下载路径和文件名
        String fileName = context.getString(R.string.app_name) + ".apk";
        // 调用DownloadManager来下载
        Uri uri = Uri.parse(downloadLink);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(FileUtil.DIR_TYPE, fileName);
        request.setDescription(fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置类型
        request.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        request.allowScanningByMediaScanner();
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            return;
        }
        long refernece = downloadManager.enqueue(request);
        // 把当前下载的ID保存起来
        SharedPreferencesUtil.setLong(context, GlobalConstants.SP_DOWNLOADING_ID, refernece);
        Log.v(GlobalConstants.LOG_TAG, "开始下载:" + fileName);
    }

    /**
     * 开始安装
     */
    public static void startInstall(Context context, long downloadId) {
        long enqueueId = SharedPreferencesUtil.getLong(context, GlobalConstants.SP_DOWNLOADING_ID);
        if (downloadId != enqueueId) {
            return;
        }
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(enqueueId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager == null) {
            return;
        }
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                String apkUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                File file = new File(Uri.parse(apkUri).getPath());
                String apkPath = file.getAbsolutePath();
                installAPK(context, apkPath);
            }
        }
    }

    /**
     * 自动安装
     */
    private static void installAPK(Context context, String apkPath) {
        Log.v(GlobalConstants.LOG_TAG, apkPath);
        if (TextUtils.isEmpty(apkPath)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//7.0以上
            Log.v(GlobalConstants.LOG_TAG, "android >= 7.0");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, GlobalConstants.PACKAGE_NAME, new File(apkPath));
        } else {//7.0以下
            Log.v(GlobalConstants.LOG_TAG, "android < 7.0");
            uri = Uri.fromFile(new File(apkPath));
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

}

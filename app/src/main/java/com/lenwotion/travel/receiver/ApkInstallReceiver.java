package com.lenwotion.travel.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.lenwotion.travel.utils.SharedPreferencesUtil;

import java.io.File;

/**
 * Created by fq on 2017/11/30
 */
public class ApkInstallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            installApk(context, downloadApkId);
        }
    }

    /**
     * 安装apk
     */
    private void installApk(Context context, long downloadId) {
        long downId = SharedPreferencesUtil.getLong(context, DownloadManager.EXTRA_DOWNLOAD_ID);
        if (downloadId == downId) {
            DownloadManager downManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (downManager == null) {
                return;
            }
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
            Cursor cursor = downManager.query(query);
            if (cursor.moveToFirst()) {
                String apk = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                File file = new File(Uri.parse(apk).getPath());
                String apkPath = file.getAbsolutePath();
                Log.e("ApkInstallReceiver", "apkPath==" + apkPath);
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
                    Uri apkUri = downManager.getUriForDownloadedFile(downId);//在AndroidManifest中的android:authorities值
                    SharedPreferencesUtil.setString(context, "downloadApk", apkUri.getPath());
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                } else {
                    install.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
                }
                context.startActivity(install);
            }
        }
    }

}

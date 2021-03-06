package com.lenwotion.travel.activity.me;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.application.AnyWalkingApplication;
import com.lenwotion.travel.bean.general.AppVersionBean;
import com.lenwotion.travel.bean.general.AppVersionResponseBean;
import com.lenwotion.travel.business.DownloadApkBusiness;
import com.lenwotion.travel.global.GlobalConstants;
import com.lenwotion.travel.interfaces.IGeneralService;
import com.lenwotion.travel.utils.CommonUtil;
import com.lenwotion.travel.utils.DownLoadUtil;
import com.lenwotion.travel.utils.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends BaseActivity {

    private LinearLayout mAboutCompanyCityLl;
    private LinearLayout mAboutCompanyLl;
    private LinearLayout mAboutAppLl;
    private LinearLayout mAboutAgreementLl;
    private LinearLayout mUseHelpLl;
    private LinearLayout mCheckUpdateLl;
    private ImageView mCompanyIv;

    private int mCompanyClickTimes;

    //public static final String ABOUT_INFO_TEXT = "ABOUT_INFO_TEXT";
    public static final String ABOUT_INFO_TYPE_KEY = "ABOUT_INFO_TYPE_KEY";
    public static final String ABOUT_INFO_TYPE_COMPANY_CITY = "ABOUT_INFO_TYPE_COMPANY_CITY";
    public static final String ABOUT_INFO_TYPE_COMPANY = "ABOUT_INFO_TYPE_COMPANY";
    public static final String ABOUT_INFO_TYPE_APP = "ABOUT_INFO_TYPE_APP";
    public static final String ABOUT_INFO_TYPE_AGREEMENT = "ABOUT_INFO_TYPE_AGREEMENT";

    /**
     * ????????????
     */
    private AppVersionBean mAppVersionBean;
    /**
     * ?????????APK??????ID
     */
    private long mDownloadId;
    /**
     * ????????????????????????????????????????????????
     */
    private final int ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE = 8;
    /**
     * ????????????????????????
     */
    private ActivityReceiver mActivityReceiver;

    public class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {//???????????????????????????????????????
                Log.v(GlobalConstants.LOG_TAG, "???????????????????????????????????????");
                mDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                checkCanInstall();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.about));
        setContentView(R.layout.activity_about);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        mContext = this;
        registerReceiver();
    }

    private void initView() {
        mAboutCompanyCityLl = findViewById(R.id.ll_about_about_company_city);
        mAboutCompanyLl = findViewById(R.id.ll_about_about_company);
        mAboutAppLl = findViewById(R.id.ll_about_about_app);
        mAboutAgreementLl = findViewById(R.id.ll_about_about_agreement);
        mUseHelpLl = findViewById(R.id.ll_about_use_help);
        mCheckUpdateLl = findViewById(R.id.ll_about_check_update);
        mCompanyIv = findViewById(R.id.iv_me_about_company);
        TextView appVersionTv = findViewById(R.id.tv_about_us_app_version);
        appVersionTv.setText(CommonUtil.getVersionName(mContext) == null ? "1.0" : CommonUtil.getVersionName(mContext));
    }

    private void initListener() {
        mAboutCompanyCityLl.setOnClickListener(this);
        mAboutCompanyLl.setOnClickListener(this);
        mAboutAppLl.setOnClickListener(this);
        mAboutAgreementLl.setOnClickListener(this);
        mUseHelpLl.setOnClickListener(this);
        mCheckUpdateLl.setOnClickListener(this);
        mCompanyIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, InfoActivity.class);
        switch (v.getId()) {
            case R.id.ll_about_about_company_city:
                intent.putExtra(ABOUT_INFO_TYPE_KEY, ABOUT_INFO_TYPE_COMPANY_CITY);
                startActivity(intent);
                break;

            case R.id.ll_about_about_company:
                intent.putExtra(ABOUT_INFO_TYPE_KEY, ABOUT_INFO_TYPE_COMPANY);
                startActivity(intent);
                break;

            case R.id.ll_about_about_app:
                intent.putExtra(ABOUT_INFO_TYPE_KEY, ABOUT_INFO_TYPE_APP);
                startActivity(intent);
                break;

            case R.id.ll_about_about_agreement:
                intent.putExtra(ABOUT_INFO_TYPE_KEY, ABOUT_INFO_TYPE_AGREEMENT);
                startActivity(intent);
                break;

            case R.id.ll_about_use_help:
                openUseHelp();
                break;

            case R.id.ll_about_check_update:
                getAppUpdateInfo();
                break;

            case R.id.iv_me_about_company:
                companyClick();
                break;
        }
    }

    /**
     * ??????????????????
     */
    private void registerReceiver() {
        mActivityReceiver = new ActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);//????????????
        mContext.registerReceiver(mActivityReceiver, intentFilter);
    }

    private void openUseHelp() {
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("type", "useHelp");
        startActivity(intent);
    }

    /**
     * ??????????????????????????????
     */
    private void showDialogUpdate() {
        new AlertDialog.Builder(mContext)
                .setMessage(getString(R.string.is_download_app_new_version))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appUpdate(mAppVersionBean.getDownloadUrl());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    /**
     * ????????????????????????
     */
    private void getAppUpdateInfo() {
        IGeneralService generalService = AnyWalkingApplication.getInstance().getRetrofit().create(IGeneralService.class);
        Call<AppVersionResponseBean> call = generalService.fetchAppLatestVersionInfo(1);
        call.enqueue(new Callback<AppVersionResponseBean>() {
            @Override
            public void onResponse(@NonNull Call<AppVersionResponseBean> call, @NonNull Response<AppVersionResponseBean> response) {
                if (response.isSuccessful()) {
                    AppVersionResponseBean bean = response.body();
                    if (bean != null) {
                        if (bean.isFlag()) {
                            fetchAppVersionInfoSuccess(bean);
                        } else {
                            // ???????????????????????????
                            fetchAppVersionInfoFail(mContext.getString(R.string.http_error) + bean.getMsg());
                        }
                    } else {
                        // http ??????
                        fetchAppVersionInfoFail(mContext.getString(R.string.http_error));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AppVersionResponseBean> call, @NonNull Throwable throwable) {
                // ??????
                fetchAppVersionInfoFail(mContext.getString(R.string.http_error));
            }
        });
    }

    /**
     * ????????????????????????
     */
    private void fetchAppVersionInfoSuccess(AppVersionResponseBean responseBean) {
        // showLog("???????????????????????????" + responseBean.getMsg());
        mAppVersionBean = responseBean.getData();
        if (mAppVersionBean.getVersionCode() > CommonUtil.getVersionCode(mContext)) {
            // ????????????????????????????????????
            // showLog("????????????:" + CommUtil.getVersionCode(mContext));
            // showLog("????????????:" + mAppVersionBean.getVersionCode());
            //??????????????????????????????
            showDialogUpdate();
        } else {
            // ???????????????
            // showLog("????????????:" + CommUtil.getVersionCode(mContext));
            // showLog("????????????:" + mAppVersionBean.getVersionCode());
            ToastUtil.showToast(mContext, getString(R.string.is_already_the_latest_version));
        }
    }

    /**
     * ????????????????????????
     */
    private void fetchAppVersionInfoFail(String errMsg) {
        // showLog("?????????" + errMsg);
        ToastUtil.showToast(mContext, errMsg);
    }

    /**
     * ????????????apk
     */
    private void appUpdate(String downloadUrl) {
        //?????????????????????????????????????????????downloadApk??????????????????????????????
        if (DownLoadUtil.getInstance(mContext.getApplicationContext()).canDownload()) {
            //DownloadApkUtil.downloadApk(mContext, downloadUrl, versionName);
            DownloadApkBusiness.downloadAPK(mContext, downloadUrl);//??????APK
        } else {
            DownLoadUtil.getInstance(mContext).skipToDownloadManager();
        }
    }

    /**
     * ??????APK?????????????????????8.0??????
     */
    private void checkCanInstall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0?????????????????????APK?????????
            Log.v(GlobalConstants.LOG_TAG, "android >= 8.0");
            boolean canInstall = mContext.getPackageManager().canRequestPackageInstalls();
            if (canInstall) {
                Log.v(GlobalConstants.LOG_TAG, "canInstall");
                DownloadApkBusiness.startInstall(mContext, mDownloadId);
            } else {
                Log.v(GlobalConstants.LOG_TAG, "???????????????????????????????????????");
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                startActivityForResult(intent, ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE);
            }
        } else {
            Log.v(GlobalConstants.LOG_TAG, "android < 8.0");
            DownloadApkBusiness.startInstall(mContext, mDownloadId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0
                boolean canInstall = mContext.getPackageManager().canRequestPackageInstalls();
                if (canInstall) {
                    Log.v(GlobalConstants.LOG_TAG, "canInstall");
                    DownloadApkBusiness.startInstall(mContext, mDownloadId);
                } else {
                    ToastUtil.showToast(mContext, "??????????????????");
                }
            }
        }
    }

    private void companyClick() {
        mCompanyClickTimes++;
        if (mCompanyClickTimes > 20) {
            startActivity(new Intent(mContext, SystemSettingActivity.class));
            finish();
        }
    }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(mActivityReceiver);
        super.onDestroy();
    }

}

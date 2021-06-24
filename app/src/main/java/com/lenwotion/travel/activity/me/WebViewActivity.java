package com.lenwotion.travel.activity.me;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lenwotion.travel.R;
import com.lenwotion.travel.activity.BaseActivity;
import com.lenwotion.travel.global.GlobalConstants;

public class WebViewActivity extends BaseActivity {

    private String mType;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarTitle(getString(R.string.app_name));
        setContentView(R.layout.activity_webview);
        initData();
        initView();
    }

    private void initData() {
        mContext = this;
        mType = getIntent().getStringExtra("type");
    }

    private void initView() {
        mWebView = findViewById(R.id.wv_webview);
        String url = "http://www.lenwotion.com/";
        switch (mType) {
            case "useHelp":
                url = GlobalConstants.APP_GENERAL_USE_HELP_URL;
                break;
        }
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setSupportZoom(true);//支持屏幕缩放
        webSettings.setBuiltInZoomControls(true);//设置出现缩放工具
        webSettings.setDisplayZoomControls(false);//不显示webview缩放按钮
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);//缩放至屏幕的大小
//        webSettings.setLoadsImagesAutomatically(true);//支持自动加载图片
//        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {//点击返回按钮的时候判断有没有上一页
            mWebView.goBack(); //goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }
}

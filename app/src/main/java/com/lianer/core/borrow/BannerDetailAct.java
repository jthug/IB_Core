package com.lianer.core.borrow;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.databinding.ActivityBannerDetailBinding;
import com.lianer.core.etherscan.SafeWebChromeClient;
import com.lianer.core.etherscan.SafeWebViewClient;
import com.lianer.core.widget.ProgressDialog;

public class BannerDetailAct extends BaseActivity {

    ActivityBannerDetailBinding bannerDetailBinding;
    ProgressDialog mDialog;

    @Override
    protected void initViews() {
        bannerDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_banner_detail);
        mDialog = new ProgressDialog();
        mDialog.setCancelable(true);
        if (this instanceof AppCompatActivity){
            AppCompatActivity activity = this;
            activity
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(mDialog, mDialog.getTag())
                    .commitAllowingStateLoss();
        }
        bannerDetailBinding.webview.setWebViewClient(new SafeWebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismiss();
            }
        });// 设置 WebViewClient
        bannerDetailBinding.webview.setWebChromeClient(new SafeWebChromeClient());// 设置 WebChromeClient

        WebSettings webSettings = bannerDetailBinding.webview.getSettings();
        if (webSettings == null) return;
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        // 开启DOM缓存
        webSettings.setDomStorageEnabled(true);
        // 开启数据库缓存
        webSettings.setDatabaseEnabled(true);
        // 设置 WebView 的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 支持启用缓存模式
        webSettings.setAppCacheEnabled(true);
        // 关闭密码保存提醒功能
        webSettings.setSavePassword(false);
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 设置 UserAgent 属性
        webSettings.setUserAgentString("");
        // 允许加载本地 html 文件/false
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }


    @Override
    protected void initData() {
        bannerDetailBinding.webview.loadUrl(getIntent().getStringExtra("webUrl"));
    }

    private void dismiss() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

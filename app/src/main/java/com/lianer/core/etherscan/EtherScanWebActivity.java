package com.lianer.core.etherscan;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.databinding.ActivityEtherScanWebBinding;
import com.lianer.core.widget.ProgressDialog;

import static com.lianer.core.app.Constants.BASE_ADDRESS_URL;
import static com.lianer.core.app.Constants.BASE_TX_URL;


public class EtherScanWebActivity extends BaseActivity implements View.OnClickListener {

    private ActivityEtherScanWebBinding mBinding;
    private ProgressDialog mDialog;


    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ether_scan_web);
        mDialog = new ProgressDialog();
        mDialog.setCancelable(true);
        if (this instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity) this;
            activity
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(mDialog, mDialog.getTag())
                    .commitAllowingStateLoss();
        }
        mBinding.webview.setWebViewClient(new SafeWebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dismiss();
            }
        });// 设置 WebViewClient
        mBinding.webview.setWebChromeClient(new SafeWebChromeClient());// 设置 WebChromeClient

        WebSettings webSettings = mBinding.webview.getSettings();
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

    }


    @Override
    protected void initData() {
        if(getIntent().getStringExtra("TxHash") == null || getIntent().getStringExtra("TxHash").equals("")){
            mBinding.webview.loadUrl(BASE_ADDRESS_URL+getIntent().getStringExtra("ContractAddress"));
        }else{
            mBinding.webview.loadUrl(BASE_TX_URL+getIntent().getStringExtra("TxHash"));
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }


    private static String getStackMsg(Exception e) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
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

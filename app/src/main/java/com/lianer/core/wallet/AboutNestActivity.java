package com.lianer.core.wallet;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityAboutNestBinding;
import com.lianer.core.utils.SnackbarUtil;

public class AboutNestActivity extends BaseActivity implements View.OnLongClickListener {

    private ActivityAboutNestBinding binding;
    private ClipboardManager cmb;

    @Override
    protected void initViews() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_about_nest);

        initTitleBar();
        String appVersion = getAppVersion();

        binding.ivIcon.setRound(20);
        binding.tvVersion.setText("V "+appVersion);

        binding.tvWebsite.setOnLongClickListener(this);
        binding.tvEmail.setOnLongClickListener(this);
        binding.tvTwitter.setOnLongClickListener(this);
        binding.tvTelegram.setOnLongClickListener(this);
        binding.tvWechat.setOnLongClickListener(this);

    }

    private String getAppVersion() {
        PackageManager packageManager = getPackageManager();
        String versionName="";
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName= packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionName;
    }

    private void initTitleBar() {
        binding.titlebar.showLeftDrawable();
        binding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                onBackPressed();
            }

            @Override
            public void rightTextClick() {

            }

            @Override
            public void rightImgClick() {

            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onLongClick(View v) {
        if (cmb==null){
            cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        }
        switch (v.getId()){
            case R.id.tv_website:
                cmb.setText(binding.tvWebsite.getText());
                break;
            case R.id.tv_email:
                cmb.setText(binding.tvEmail.getText());
                break;
            case R.id.tv_twitter:
                cmb.setText(binding.tvTwitter.getText());
                break;
            case R.id.tv_telegram:
                cmb.setText(binding.tvTelegram.getText());
                break;
            case R.id.tv_wechat:
                cmb.setText(binding.tvWechat.getText());
                break;
        }
        SnackbarUtil.DefaultSnackbar(binding.getRoot(),getString(R.string.copy_success)).show();
        return true;
    }
}

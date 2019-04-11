package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;

import com.lianer.core.base.BaseActivity;
import com.lianer.core.base.BaseFragment;
import com.lianer.core.R;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityExportKeystoreBinding;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.wallet.adapter.FragmentAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * 导出keyStore
 * @author bowen
 */
public class ExportKeystoreActivity extends BaseActivity {

    private ActivityExportKeystoreBinding mBinding;
    public String mWalletAddress;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_export_keystore);
        mBinding.titlebar.showLeftDrawable();
        mBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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

        Intent intent = getIntent();
        mWalletAddress = intent.getStringExtra("walletAddress");
        List<BaseFragment> fragments = Arrays.asList(
                new ExportKeystoreFileFragment(),
                new ExportKeystoreQRCodeFragment()
        );
        List<String> labels = Arrays.asList(getString(R.string.keystore_file),getString(R.string.QR_code));
        mBinding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragments,labels));
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);

        initDialog();
    }


    /**
     * 初始化截图警告弹出框
     */
    private void initDialog() {
        new KnowDialog(new CenterDialog(R.layout.dlg_dont_screenshot, ExportKeystoreActivity.this),
                () -> {
                },
                getResources().getString(R.string.dont_screenshot),
                getResources().getString(R.string.dont_screenshot_warn));
    }
    @Override
    protected void initData() {

    }
}

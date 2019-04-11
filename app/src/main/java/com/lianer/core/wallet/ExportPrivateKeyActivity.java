package com.lianer.core.wallet;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;

import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityExportPrivateKeyBinding;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.utils.SnackbarUtil;

public class ExportPrivateKeyActivity extends BaseActivity {

    private ActivityExportPrivateKeyBinding binding;
    private String privateKey;

    @Override
    protected void initViews() {
        binding = DataBindingUtil.setContentView(this,R.layout.activity_export_private_key);
        initTitleBar();
        initDialog();
        Intent intent = getIntent();
        privateKey = intent.getStringExtra("privateKey");
        binding.btnCopy.setOnClickListener(v->{
            ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(privateKey);
            SnackbarUtil.DefaultSnackbar(binding.getRoot(),getString(R.string.copy_success)).show();
        });


    }

    private void initTitleBar() {
        binding.titlebar.showLeftDrawable();
        binding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                finish();
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

    /**
     * 初始化截图警告弹出框
     */
    private void initDialog() {
        new KnowDialog(new CenterDialog(R.layout.dlg_dont_screenshot, ExportPrivateKeyActivity.this),
                () -> {
                    binding.showPrivateKey.setText(privateKey);
                },
                getResources().getString(R.string.dont_screenshot),
                getResources().getString(R.string.dont_screenshot_warn));
    }
}

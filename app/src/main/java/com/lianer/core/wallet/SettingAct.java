package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import com.lianer.common.utils.ACache;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.R;
import com.lianer.core.config.Tag;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityWalletManagerBinding;
import com.lianer.core.dialog.DeleteDialog;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.SnackbarUtil;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

public class SettingAct extends BaseActivity {

    private ActivityWalletManagerBinding walletManagerBinding;
    private InputWalletPswDialog inputWalletPswDialog;

    @Override
    protected void initViews() {
        walletManagerBinding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manager);

        initTitleBar();

        initLanguageType();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        walletManagerBinding.titlebar.showLeftDrawable();
        walletManagerBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
//        walletManagerBinding.updatePwd.setOnClickListener(v -> navigateToUpdataPsd());
        walletManagerBinding.languageSwitch.setOnClickListener(v -> navigateToLanguage());
        //删除钱包
//        walletManagerBinding.deleteWallet.setOnClickListener(v -> deleteWallet());
        //导出keystore
//        walletManagerBinding.exportKeystore.setOnClickListener(v -> invokePasswordInput());
        walletManagerBinding.rlAbout.setOnClickListener(v->navigateToAbout());
    }

    private void navigateToAbout() {
        Intent intent = new Intent(this, AboutNestActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到修改密码页面
     */
    private void navigateToUpdataPsd() {
        Intent intent = new Intent(SettingAct.this, UpdatePsdAct.class);
        startActivity(intent);
    }




    /**
     * 跳转到语言选择页面
     */
    private void navigateToLanguage() {
        Intent intent = new Intent(SettingAct.this, LanguageSwitchAct.class);
        startActivity(intent);
    }

    /**
     * 跳转到导出keystore页面
     */
    private void exportKeystore() {
        Intent intent = new Intent(SettingAct.this, ExportKeystoreActivity.class);
        startActivity(intent);
    }

    //输入密码
    private void invokePasswordInput() {
        inputWalletPswDialog = new InputWalletPswDialog(new CenterDialog(R.layout.dlg_input_wallet_psd, SettingAct.this), new InputWalletPswDialog.BtnListener() {
            @Override
            public void sure() {
                String password = inputWalletPswDialog.getWalletPsd();
                Credentials credentials = null;
                try {
                    credentials = Credentials.create(LWallet.decrypt(password, HLWalletManager.shared().getCurrentWallet(SettingAct.this).walletFile));
                } catch (CipherException e) {
                    SnackbarUtil.DefaultSnackbar(walletManagerBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    password = "";
                }
                if (credentials == null) {
                    SnackbarUtil.DefaultSnackbar(walletManagerBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }
                exportKeystore();

            }
        });

    }

    /**
     * 初始化语言类型
     */
    private void initLanguageType() {
        int languageType = MultiLanguageUtil.getInstance().getLanguageType();
        walletManagerBinding.languageType.setText(languageType == LanguageType.LANGUAGE_EN ? getResources().getString(R.string.english) : getResources().getString(R.string.simplified_chinese));
    }

    /**
     * 删除钱包
     */
    private void deleteWallet() {
        new DeleteDialog(new CenterDialog(R.layout.dlg_delete_wallet, this), "", "", () -> {
//                SPUtils.getInstance().clear();
            //删除nonce记录
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
            sharedPreferences.edit().remove(Constants.TRANSACTION_NONCE).apply();

            ACache.get(SettingAct.this).remove(Tag.IS_BACKUP);
            HLWalletManager.shared().deleteWallet(SettingAct.this);
            Intent intent = new Intent(SettingAct.this, CreateAndImportActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

    }

}

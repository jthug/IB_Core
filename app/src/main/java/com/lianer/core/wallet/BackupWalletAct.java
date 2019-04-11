package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.KeyEvent;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.core.app.NestApp;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.databinding.ActivityBackupWalletBinding;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.utils.SnackbarUtil;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import io.reactivex.Flowable;

/**
 * 备份钱包
 *
 * @author allison
 */
public class BackupWalletAct extends BaseActivity {

    private String password;
    private InputWalletPswDialog inputWalletPswDialog;
    private WalletFile walletFile;

    @Override
    protected void initViews() {
        ActivityBackupWalletBinding backupWalletBinding = DataBindingUtil.setContentView(this, R.layout.activity_backup_wallet);
        SecurityConfigure securityConfigure = new SecurityConfigure().setDefaultKeyboardType(KeyboardType.NUMBER);
        CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, BackupWalletAct.this);
        SecurityKeyboard securityKeyboard = new SecurityKeyboard(this, centerDialog.getContentView(), securityConfigure);
        backupWalletBinding.backupWallet.setOnClickListener(v -> inputWalletPswDialog = new InputWalletPswDialog(centerDialog, () -> Flowable.just(1)
                .map(integer -> {
                    password = inputWalletPswDialog.getWalletPsd();
//                    walletFile = HLWalletManager.shared().getCurrentWallet(BackupWalletAct.this).getWalletFile();

                    walletFile = NestApp.tempHlWallet.walletFile;
                    return checkPsdCorrect(password, walletFile);
                })
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        Intent intent = new Intent(BackupWalletAct.this, MnemonicAct.class);
                        startActivity(intent);
                    } else {
                        SnackbarUtil.DefaultSnackbar(backupWalletBinding.getRoot(), getString(R.string.password_error)).show();
                    }
                })));

    }

    @Override
    protected void initData() {
    }

    /**
     * 验证密码是否存在
     *
     * @param password   密码
     * @param walletFile 钱包文件
     * @return 是否存在状态
     */
    private boolean checkPsdCorrect(String password, WalletFile walletFile) {
        try {
            Wallet.decrypt(password, walletFile);
        } catch (CipherException e) {
            return false;
        }
        return true;
    }

    /**
     * 屏蔽返回按钮
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_BACK || super.dispatchKeyEvent(event);
    }
}

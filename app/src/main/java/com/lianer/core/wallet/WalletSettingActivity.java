package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.ACache;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.config.Tag;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityWalletSettingBinding;
import com.lianer.core.dialog.DeleteDialog;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import java.util.List;

public class WalletSettingActivity extends BaseActivity implements View.OnClickListener {

    private ActivityWalletSettingBinding mBinding;
    private String mWalletAddress;
    private String mWalletName;
    private InputWalletPswDialog inputWalletPswDialog;
    private Credentials credentials;
    private SecurityConfigure configure;
    private InputWalletPswDialog inputWalletPswDialog1;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_setting);
        configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
        initTitleBar();

        mBinding.tvDeleteWallet.setOnClickListener(this);
        mBinding.rlModifyPwd.setOnClickListener(this);
        mBinding.rlWalletName.setOnClickListener(this);
        mBinding.rlBackupKeystore.setOnClickListener(this);
        mBinding.rlExportPrivateKey.setOnClickListener(this);
    }

    private void initTitleBar() {
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
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mWalletAddress = intent.getStringExtra("walletAddress");
        mWalletName = intent.getStringExtra("walletName");

        mBinding.tvWalletAddress.setText(mWalletAddress);
        mBinding.tvWalletName.setText(mWalletName);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_delete_wallet:
                deleteWallet();
                break;
            case R.id.rl_wallet_name:
                modifyWalletName();
                break;
            case R.id.rl_modify_pwd:
                modifyPwd();
                break;
            case R.id.rl_backup_keystore:
                backupKeystore();
                break;
            case R.id.rl_export_private_key:
                exportPrivateKey();
                break;
        }
    }

    private void exportPrivateKey() {
        CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, this);
        SecurityKeyboard securityKeyboard = new SecurityKeyboard(this, centerDialog.getContentView(), configure);
        inputWalletPswDialog1 = new InputWalletPswDialog(centerDialog, new InputWalletPswDialog.BtnListener() {
            @Override
            public void sure() {
                String walletPsd = inputWalletPswDialog1.getWalletPsd();
                HLWalletManager hlWalletManager = HLWalletManager.shared();
                try {
                    HLWallet wallet = hlWalletManager.getWallet(mWalletAddress);
                    credentials = Credentials.create(LWallet.decrypt(walletPsd, wallet.walletFile));
                } catch (CipherException e) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }
                if (credentials == null) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }

                ECKeyPair ecKeyPair = credentials.getEcKeyPair();
                String privateKey = ecKeyPair.getPrivateKey().toString(16);
                Intent intent = new Intent(WalletSettingActivity.this, ExportPrivateKeyActivity.class);
                intent.putExtra("privateKey",privateKey);
                startActivity(intent);
            }
        });
    }


    private void backupKeystore() {
        CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, this);
        SecurityKeyboard securityKeyboard = new SecurityKeyboard(this, centerDialog.getContentView(), configure);
        inputWalletPswDialog = new InputWalletPswDialog(centerDialog, new InputWalletPswDialog.BtnListener() {
            @Override
            public void sure() {
                String walletPsd = inputWalletPswDialog.getWalletPsd();
                HLWalletManager hlWalletManager = HLWalletManager.shared();
                try {
                    HLWallet wallet = hlWalletManager.getWallet(mWalletAddress);
                    credentials = Credentials.create(LWallet.decrypt(walletPsd, wallet.walletFile));
                } catch (CipherException e) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                }
                if (credentials == null) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }
                Intent intent = new Intent(WalletSettingActivity.this, ExportKeystoreActivity.class);
                intent.putExtra("walletAddress",mWalletAddress);
                startActivity(intent);
            }
        });

    }

    private void modifyPwd() {
        Intent intent = new Intent(this, UpdatePsdAct.class);
        intent.putExtra("walletAddress",mWalletAddress);
        startActivity(intent);
    }

    private void modifyWalletName() {
        CenterDialog centerDialog = new CenterDialog(R.layout.dialog_modify_wallet_name, this);
        EditText etWalletName = centerDialog.findViewById(R.id.et_wallet_name);
        TextView tvCancel = centerDialog.findViewById(R.id.tv_cancel);
        TextView tvSure = centerDialog.findViewById(R.id.tv_sure);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerDialog.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String walletName = etWalletName.getText().toString().trim();
                if (TextUtils.isEmpty(walletName)){
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.wallet_name_tips)).show();
                    return;
                }
                if(!walletName.matches("^[A-Za-z0-9]+$")){
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.modify_wallet_name_tips)).show();
                    return;
                }
                List<HLWallet> wallets = HLWalletManager.shared().getWallets();
                for (HLWallet hlWallet:
                     wallets) {
                    if (TextUtils.equals(walletName,hlWallet.getWalletName())){
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.wallet_name_same_tips)).show();
                        return;
                    }
                }
                HLWallet wallet = HLWalletManager.shared().getWallet(mWalletAddress);
                wallet.setWalletName(walletName);
                HLWalletManager.shared().updateWallet(WalletSettingActivity.this,wallet);
                mBinding.tvWalletName.setText(walletName);
                centerDialog.dismiss();
            }
        });
        centerDialog.show();
    }

    private void deleteWallet() {
        new DeleteDialog(new CenterDialog(R.layout.dlg_delete_wallet, WalletSettingActivity.this), "", "", () -> {

            HLWallet currentWallet = HLWalletManager.shared().getCurrentWallet(WalletSettingActivity.this);
            String currentWalletAddress = currentWallet.getAddress();
            if (mWalletAddress.equals(currentWalletAddress)) {//删除的是当前的钱包
                HLWalletManager.shared().deleteSingleWallet(WalletSettingActivity.this, mWalletAddress);
                //将当前钱包列表的第一个钱包设置为当前钱包
                List<HLWallet> wallets = HLWalletManager.shared().getWallets();
                if (wallets.size() > 0) {
                    HLWallet hlWallet = wallets.get(0);
                    SharedPreferences sp = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
                    long nonce = 0;
                    try {
                        nonce = IBContractUtil.getNonce(TransferUtil.getWeb3j(), hlWallet.getAddress());
                    } catch (Exception e) {
                        e.printStackTrace();
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.nonce_error)).show();
                        return;
                    }
                    HLWalletManager.shared().switchCurrentWallet(WalletSettingActivity.this, hlWallet);
                    //重新缓存nonce
                    sp.edit().putLong(Constants.TRANSACTION_NONCE, nonce).apply();

                }else {
                    //如果当前钱包列表没有钱包
                    ACache.get(WalletSettingActivity.this).remove(Tag.IS_BACKUP);
                    HLWalletManager.shared().deleteWallet(WalletSettingActivity.this);
                    Intent intent = new Intent(WalletSettingActivity.this, CreateAndImportActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else {
                HLWalletManager.shared().deleteSingleWallet(WalletSettingActivity.this, mWalletAddress);
            }
            finish();
        });
    }
}

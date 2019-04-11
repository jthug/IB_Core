package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityUpdatePsdBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.utils.SnackbarUtil;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

/**
 * 修改密码
 * @author allison
 */
public class UpdatePsdAct extends BaseActivity {

    private ActivityUpdatePsdBinding updatePsdBinding;
    private String currentPsd, newPsd, confirmNewPsd;
    private WalletFile walletFile;
    private ECKeyPair ecKeyPair;
    boolean isPsd, isNewPsd, isConfirmNewPsd;
    private SecurityKeyboard mSecurityKeyboard;
    private HLWallet oldHlWallet;

    @Override
    protected void initViews() {
        updatePsdBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_psd);
        SecurityConfigure configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
                //.setLetterEnabled(false); 关闭字母
        mSecurityKeyboard = new SecurityKeyboard(updatePsdBinding.llPwd, configure);

        initTitleBar();

//        initSpannableString();
        initEditTextListen();

        initClick();
    }

    /**
     * 初始化输入框监听事件
     */
    private void initEditTextListen() {
        updatePsdBinding.currentPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPsd = !TextUtils.isEmpty(s) &&  s.length() >= 8;
                setUpdateWalletEnable(isPsd && isNewPsd && isConfirmNewPsd);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updatePsdBinding.newPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isNewPsd = !TextUtils.isEmpty(s) &&  s.length() >= 8;
                setUpdateWalletEnable(isPsd && isNewPsd && isConfirmNewPsd);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updatePsdBinding.confirmNewPsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isConfirmNewPsd = !TextUtils.isEmpty(s) &&  s.length() >= 8;
                setUpdateWalletEnable(isPsd && isNewPsd && isConfirmNewPsd);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUpdateWalletEnable(boolean isWalletCreate) {
        updatePsdBinding.confirmUpdate.setEnabled(isWalletCreate);
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        updatePsdBinding.titlebar.showLeftDrawable();
        updatePsdBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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

    /**
     * 初始化点击事件
     */
    private void initClick() {
        updatePsdBinding.confirmUpdate.setOnClickListener(v -> {
            currentPsd = updatePsdBinding.currentPsd.getText().toString().trim();
            newPsd = updatePsdBinding.newPsd.getText().toString().trim();
            confirmNewPsd = updatePsdBinding.confirmNewPsd.getText().toString().trim();
            Intent intent = getIntent();
            String walletAddress = intent.getStringExtra("walletAddress");
            oldHlWallet = HLWalletManager.shared().getWallet(walletAddress);
            walletFile = oldHlWallet.getWalletFile();
            if (validatePsd()) {
                updatePsd();
            }
        });
    }

    /**
     * 修改密码
     */
    private void updatePsd() {
        try {
            WalletFile walletFile = Wallet.createLight(newPsd, ecKeyPair);
            HLWallet hlWallet = new HLWallet(walletFile);
            hlWallet.setWalletName(oldHlWallet.getWalletName());
            HLWalletManager.shared().saveWallet(this, hlWallet);
            SnackbarUtil.DefaultSnackbar(updatePsdBinding.getRoot(), getString(R.string.update_psd_success)).show();
            finish();
        } catch (CipherException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证密码
     * @return 密码是否正确状态
     */
    private boolean validatePsd() {
        //判断输入的当前密码是否正确
        if (!checkPsdCorrect(currentPsd, walletFile)) {
            SnackbarUtil.DefaultSnackbar(updatePsdBinding.getRoot(), getString(R.string.old_password_error)).show();
            return false;
        }

        if (!newPsd.equals(confirmNewPsd)) {
            SnackbarUtil.DefaultSnackbar(updatePsdBinding.getRoot(), getString(R.string.second_psd_not_match)).show();
            return false;
        }

        return true;
    }

    /**
     * 初始化富文本
     */
    private void initSpannableString() {
        SpannableString spannableString = new SpannableString(getResources().getString(R.string.update_psd_warn));
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(R.color.clr_059EFF));
        updatePsdBinding.updatePsdWarn.setMovementMethod(LinkMovementMethod.getInstance());
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                navigateToImport();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(colorSpan, 12, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        updatePsdBinding.updatePsdWarn.setText(spannableString);
    }

    /**
     * 跳转到导入助记词或私钥的页面
     */
    private void navigateToImport() {
        Intent intent = new Intent(this, ImportWalletAct.class);
        startActivity(intent);
    }

    @Override
    protected void initData() {

    }

    /**
     * 验证密码是否存在
     * @param password  密码
     * @param walletFile  钱包文件
     * @return 是否存在状态
     */
    private boolean checkPsdCorrect(String password, WalletFile walletFile) {
        try {
            ecKeyPair = Wallet.decrypt(password, walletFile);
        } catch (CipherException e) {
            return false;
        }
        return true;
    }

}

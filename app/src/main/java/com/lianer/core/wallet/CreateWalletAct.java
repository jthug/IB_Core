package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.core.app.Constants;
import com.lianer.core.app.NestApp;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.utils.ACache;
import com.lianer.core.R;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.config.Tag;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityCreateWalletBinding;
import com.lianer.core.manager.InitWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.ScheduleCompat;
import com.lianer.core.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;

/**
 * 创建钱包
 *
 * @author allison
 */
public class CreateWalletAct extends BaseActivity {

    private ActivityCreateWalletBinding createWalletBinding;
    private boolean isPsd, isRepsd, isAgree = true;
    String mnemonics;

    @Override
    protected void initViews() {
        createWalletBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_wallet);
        SecurityConfigure configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
        SecurityKeyboard securityKeyboard = new SecurityKeyboard(createWalletBinding.llRoot, configure);
        initTitleBar();
        createWalletBinding.createWallet.setOnClickListener(v -> createWallet());
        createWalletBinding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPsd = !TextUtils.isEmpty(s) && s.length() >= 8;
                setCreateWalletEnable(isPsd && isRepsd && isAgree);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        createWalletBinding.repassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isRepsd = !TextUtils.isEmpty(s) && s.length() >= 8;
                setCreateWalletEnable(isPsd && isRepsd && isAgree);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        createWalletBinding.importWallet.setOnClickListener(v -> {
//            Intent intent2 = new Intent(CreateWalletAct.this, ImportWalletAct.class);
//            startActivity(intent2);
//        });

        createWalletBinding.agreeCreateWallet.setOnClickListener(v -> {
            isAgree = !isAgree;
            createWalletBinding.agreeCreateWallet.setCompoundDrawablesWithIntrinsicBounds(isAgree ? getResources().getDrawable(
                    R.drawable.ic_agreement_selected) : getResources().getDrawable(
                    R.drawable.ic_agreement_unselected),
                    null, null, null);
            setCreateWalletEnable(isPsd && isRepsd && isAgree);
        });
        //协议跳转事件
        createWalletBinding.userAgreement.setOnClickListener(v -> {
            Intent intent = new Intent(CreateWalletAct.this, BannerDetailAct.class);
//                if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_CHINESE);
//                } else {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_ENGLISH);
//                }
            intent.putExtra("webUrl", Constants.USER_AGREEMENT);
            startActivity(intent);
        });
    }

    private void initTitleBar() {
        createWalletBinding.titlebar.showLeftDrawable();
        createWalletBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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

    private void setCreateWalletEnable(boolean isWalletCreate) {
        createWalletBinding.createWallet.setEnabled(isWalletCreate);
    }

    private void createWallet() {
        String password = createWalletBinding.password.getText().toString().trim();
        String repassword = createWalletBinding.repassword.getText().toString().trim();


        Flowable
                .just(password)
                .filter(o -> validateInput(password, repassword, isAgree))
                .map(s -> {
                    mnemonics = checkMnemonics();
                    return mnemonics;
                })
                .flatMap(s -> InitWalletManager.shared().generateWallet(getApplication(), password, s))
                .compose(ScheduleCompat.apply())
                .subscribe(new HLSubscriber<HLWallet>(this, true) {
                    @Override
                    protected void success(HLWallet data) {
                        ACache.get(CreateWalletAct.this).put(Tag.MNEMONICS, mnemonics);
                        NestApp.tempHlWallet = data;
                        Intent intent = new Intent(CreateWalletAct.this, BackupWalletAct.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(createWalletBinding.getRoot(), error.getMessage()).show();
                    }
                });
    }

    private boolean validateInput(String password, String repassword, boolean isAgreeProtocol) {
        if (password.length() < 8) {
//            ScheduleCompat.snackInMain(createWalletBinding.getRoot(), getString(R.string.psd_length_not_match));
            return false;
        }
        if (!TextUtils.equals(password, repassword)) {
            ScheduleCompat.snackInMain(createWalletBinding.getRoot(), getString(R.string.second_psd_not_match));
            return false;
        }
        if (!isAgreeProtocol) {
            ScheduleCompat.snackInMain(createWalletBinding.getRoot(), getString(R.string.agree_agreement));
            return false;
        }
        return true;
    }


    @Override
    protected void initData() {
    }

    /**
     * 检查助记词是否包含相同的单词
     */
    private String checkMnemonics() {
        String generateMnemonics = InitWalletManager.shared().generateMnemonics();
        List<String> mnemonicData = new ArrayList<>();

        if (!TextUtils.isEmpty(generateMnemonics)) {
            String[] mnemonicArray = generateMnemonics.split(" ");
            Collections.addAll(mnemonicData, mnemonicArray);
        }

        //判断助记词是否有相同的单词
        String firstMnemonic = mnemonicData.get(0);
        for (int i = 1; i < mnemonicData.size(); i++) {
            if (firstMnemonic.equals(mnemonicData.get(i))) {
                return checkMnemonics();
            }
        }
        return generateMnemonics;
    }
}

package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.ACache;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseFragment;
import com.lianer.core.R;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.config.Tag;
import com.lianer.core.lauch.MainAct;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.manager.InitWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.ScheduleCompat;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

/**
 * <pre>
 * Create by  :    L
 * Create Time:    2018/6/22
 * Brief Desc :
 * </pre>
 */
public class UnlockKeystoreFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    EditText inputKeystore;
    EditText inputPassword;
    Button btnUnlock;
    TextView userAgreement;
    TextView readConfirm;
    //免责协议
    private boolean isAgree = true;
    private HLWallet currentWallet;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_unlock_keystore, null);
        inputKeystore = mView.findViewById(R.id.et_keystore);
        inputPassword = mView.findViewById(R.id.et_password);
        btnUnlock = mView.findViewById(R.id.btn_unlock);
        userAgreement =  mView.findViewById(R.id.user_agreement);
        readConfirm = mView.findViewById(R.id.read_confirm);
        LinearLayout llRoot = mView.findViewById(R.id.ll_root);

        SecurityConfigure configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
        SecurityKeyboard securityKeyboard = new SecurityKeyboard(llRoot, configure);

        btnUnlock.setOnClickListener(this);
        userAgreement.setOnClickListener(this);
        readConfirm.setOnClickListener(this);

        inputKeystore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0 && isAgree){
                    btnUnlock.setEnabled(true);
                }else{
                    btnUnlock.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return mView;
    }

    @Override
    protected void initData() {
        super.initData();

    }

    private void attemptUnlock() {
        String keystore = inputKeystore.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        Flowable
                .just(keystore)
                .filter(s -> validateInput(s,password))
                .flatMap(s -> InitWalletManager.shared().importKeystore(getActivity(),s,password))
                .compose(ScheduleCompat.apply())
                .subscribe(new HLSubscriber<HLWallet>(getActivity(),true) {
                    @Override
                    protected void success(HLWallet data) {
                        SnackbarUtil.DefaultSnackbar(mView,getString(R.string.import_success)).show();
                        Flowable.just(1)
                                .map(s->{
                                    currentWallet = HLWalletManager.shared().getCurrentWallet(getContext());
                                    long nonce = IBContractUtil.getNonce(TransferUtil.getWeb3j(), currentWallet.getAddress());
                                    return nonce;
                                })
                                .delay(2000, TimeUnit.MILLISECONDS)
                                .compose(ScheduleCompat.apply())
                                .subscribe(nonce -> {

                                    Log.w("wallet",currentWallet.getAddress());
                                    SharedPreferences sp = getContext().getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
                                    sp.edit().putLong(Constants.TRANSACTION_NONCE, nonce).apply();
                                    ACache.get(getContext()).put(Tag.IS_BACKUP, "true");
                                    Intent intent = new Intent(mActivity, MainAct.class);
                                    intent.putExtra(Tag.INDEX,1);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                });
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w(error.getMessage());
                        if(error.getMessage().contains("password")){
                            SnackbarUtil.DefaultSnackbar(mView,getString(R.string.keystore_password_incorrect)).show();
                        }else{
                            SnackbarUtil.DefaultSnackbar(mView,getString(R.string.keystore_error)).show();
                        }

                    }
                });
    }

    private boolean validateInput(String privateKey, String password) {
        // validate empty
        if (TextUtils.isEmpty(privateKey) || TextUtils.isEmpty(password)){
            ScheduleCompat.snackInMain(mView,getString(R.string.please_input_data));
            return false;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_unlock:
                btnUnlock.setEnabled(false);
                attemptUnlock();
                break;
            //免责协议阅读
            case R.id.read_confirm:
                isAgree = !isAgree;
                if(isAgree){
                    readConfirm.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.ic_agreement_selected),
                            null, null, null);
                    btnUnlock.setEnabled(true);
                }else{
                    readConfirm.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.ic_agreement_unselected),
                            null, null, null);
                    btnUnlock.setEnabled(false);
                }
                break;
            //免责协议详情
            case R.id.user_agreement:
                Intent intent = new Intent(getActivity(), BannerDetailAct.class);
//                if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_CHINESE);
//                } else {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_ENGLISH);
//                }
                intent.putExtra("webUrl", Constants.USER_AGREEMENT);
                startActivity(intent);
                break;
        }
    }
}

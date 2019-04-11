package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseFragment;
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

import org.web3j.crypto.WalletUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;

public class UnlockPrivateKeyFragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    EditText etPk;
    EditText inputPassword;
    EditText inputRePassword;
    Button btnUnlock;
    TextView userAgreement;
    TextView readConfirm;
    //免责协议
    private boolean isConsent = true;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_unlock_privatekey, container, false);
        etPk= mView.findViewById(R.id.et_pk);
        inputPassword = mView.findViewById(R.id.et_password);
        inputRePassword = mView.findViewById(R.id.et_re_password);
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

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etPk.getText().length() > 0 && inputPassword.getText().length() > 0 && inputRePassword.getText().length() > 0 ){
                    btnUnlock.setEnabled(true);
                }else{
                    btnUnlock.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        etPk.addTextChangedListener(watcher);
        inputPassword.addTextChangedListener(watcher);
        inputRePassword.addTextChangedListener(watcher);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_unlock:
                btnUnlock.setEnabled(false);
                attemptUnlock();
                break;
            case R.id.read_confirm:
                isConsent = !isConsent;
                if(isConsent){
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

    private void attemptUnlock() {
        String pk = etPk.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String repassword = inputRePassword.getText().toString().trim();

        Flowable.just(pk)
                .filter(s->validateInput(s,password,repassword))
                .flatMap(s -> InitWalletManager.shared().importPrivateKey(getContext(),pk,password))
                .compose(ScheduleCompat.apply())
                .subscribe(new HLSubscriber<HLWallet>(getActivity(),true) {
                    @Override
                    protected void success(HLWallet data) {
                        SnackbarUtil.DefaultSnackbar(mView,getString(R.string.mnemonic_success)).show();
                        Flowable.just(1)
                                .delay(2000, TimeUnit.MILLISECONDS)
                                .compose(ScheduleCompat.apply())
                                .subscribe(integer -> {
                                    HLWallet currentWallet = HLWalletManager.shared().getCurrentWallet(getContext());
                                    long nonce = IBContractUtil.getNonce(TransferUtil.getWeb3j(), currentWallet.getAddress());
                                    SharedPreferences sp = getContext().getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
                                    sp.edit().putLong(Constants.TRANSACTION_NONCE, nonce).apply();
                                    KLog.w("wallet",currentWallet.getAddress());
                                    ACache.get(getContext()).put(Tag.IS_BACKUP, "true");
                                    Intent intent = new Intent(mActivity, MainAct.class);
                                    intent.putExtra(Tag.INDEX,1);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                });
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(mView,error.getMessage()).show();
                    }
                });
    }

    private boolean validateInput(String pk, String password, String repassword) {
        // validate empty
        if (TextUtils.isEmpty(pk) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)){
            ScheduleCompat.snackInMain(mView,getString(R.string.please_input_data));
            return false;
        }
        // validate password
        if (!TextUtils.equals(password,repassword)){
            ScheduleCompat.snackInMain(mView,getString(R.string.password_does_not_match));
            return false;
        }
        if(password.length() < 8){
            ScheduleCompat.snackInMain(mView,getString(R.string.set_password));
            return false;
        }
        // validate privatekey
        if (!WalletUtils.isValidPrivateKey(pk)){
            ScheduleCompat.snackInMain(mView,getString(R.string.privatekey_error));
            return false;
        }
        return true;
    }
}

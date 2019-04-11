package com.lianer.core.dialog;


import android.app.Dialog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lianer.core.R;

/**
 * 请输入钱包密码
 * @author allison
 * 使用demo
 * new InputWalletPswDialog(new CenterDialog(R.layout.dlg_input_wallet_psd, MainAct.this), new InputWalletPswDialog.BtnListener() {
    @Override
    public void sure() {
    }
    });
 */
public class InputWalletPswDialog implements View.OnClickListener {
    private Dialog mDialog;
    private BtnListener mListener;
    private EditText inputPsd;

    public InputWalletPswDialog(Dialog mDialog, BtnListener listener) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init();
    }

    private void __init() {
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        inputPsd = mDialog.findViewById(R.id.dg_wallet_psd);
        TextView tvCancle = mDialog.findViewById(R.id.cancel);
        TextView tvSure = mDialog.findViewById(R.id.sure);

        tvCancle.setOnClickListener(this);
        tvSure.setOnClickListener(this);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mDialog.dismiss();
                break;
            case R.id.sure:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.sure();
                }
                break;
        }

    }

    /**
     * 获取输入框密码
     * @return
     */
    public String getWalletPsd() {
        return inputPsd.getText().toString().trim();
    }

    public interface BtnListener {
        void sure();
    }
}

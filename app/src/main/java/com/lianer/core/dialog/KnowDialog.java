package com.lianer.core.dialog;


import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.R;

/**
 * 提醒纯文字弹窗
 * @author allison
 * 使用demo
 * new InputWalletPswDialog(new CenterDialog(R.layout.dlg_input_wallet_psd, MainAct.this), new InputWalletPswDialog.BtnListener() {
@Override
public void iKnow() {
}
});
 */
public class KnowDialog implements View.OnClickListener {
    private Dialog mDialog;
    private BtnListener mListener;
    private int mGravity;

    /**
     * @param mDialog
     * @param listener
     * @param dialogTitle    标题
     * @param dialogContent  文本
     */
    public KnowDialog(Dialog mDialog, BtnListener listener, String dialogTitle, String dialogContent) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init(dialogTitle, dialogContent);
    }

    public KnowDialog(Dialog mDialog, BtnListener listener, String dialogTitle, String dialogContent, int gravity) {
        this.mListener = listener;
        this.mDialog = mDialog;
        this.mGravity = gravity;
        __init(dialogTitle, dialogContent);
    }

    private void __init(String dialogTitle, String dialogContent) {
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        TextView warnKnow = mDialog.findViewById(R.id.warn_know);
        TextView warnTitle = mDialog.findViewById(R.id.warn_title);
        TextView warnContent = mDialog.findViewById(R.id.warn_content);

        warnTitle.setText(dialogTitle);
        warnContent.setText(dialogContent);
        if (mGravity != 0) {
            warnContent.setGravity(mGravity);
        }
        warnKnow.setOnClickListener(this);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.warn_know:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.iKnow();
                }
                break;
        }

    }

    public interface BtnListener {
        void iKnow();
    }
}

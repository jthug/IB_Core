package com.lianer.core.dialog;


import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.R;

/**
 * 终止合约
 */
public class ContractTerminationDialog implements View.OnClickListener {
    private Dialog mDialog;
    private BtnListener mListener;
    private TextView confirm, cancel;

    public ContractTerminationDialog(Dialog mDialog, BtnListener listener) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init();
    }

    private void __init() {
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        confirm = mDialog.findViewById(R.id.confirm);
        cancel = mDialog.findViewById(R.id.cancel);

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mDialog.dismiss();
                break;
            case R.id.confirm:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.confirm();
                }
                break;
        }

    }

    public interface BtnListener {
        void confirm();
    }
}

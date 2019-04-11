package com.lianer.core.dialog;


import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.R;


public class SureAndCancelDialog implements View.OnClickListener {
    private Dialog mDialog;
    private BtnListener mListener;
    private TextView titleTextView, contentTextView, tvOk, tvCancel;

    public SureAndCancelDialog(Dialog mDialog, String title, String content, BtnListener listener) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init(title, content);
    }

    public SureAndCancelDialog(Dialog mDialog, SureAndCancelDialog.BtnListener listener) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init();
    }

    private void __init() {
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        tvOk = mDialog.findViewById(R.id.ok);
        tvCancel = mDialog.findViewById(R.id.cancel);

        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        mDialog.show();

    }

    private void __init(String title, String content) {
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        titleTextView = mDialog.findViewById(R.id.title);
        contentTextView = mDialog.findViewById(R.id.content);
        tvOk = mDialog.findViewById(R.id.ok);
        tvCancel = mDialog.findViewById(R.id.cancel);

        if (!TextUtils.isEmpty(title)) {
            titleTextView.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            contentTextView.setText(content);
        }

        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mDialog.dismiss();
                break;
            case R.id.ok:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.sure();
                }
                break;
        }

    }

    public interface BtnListener {
        void sure();
    }

}

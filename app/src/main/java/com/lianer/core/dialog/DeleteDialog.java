package com.lianer.core.dialog;


import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.R;

/**
 * 确定删除钱包
 * @author allison
 */
public class DeleteDialog implements View.OnClickListener {
    private Dialog mDialog;
    private BtnListener mListener;
    private TextView titleTextView, contentTextView, tvDelete, tvCancel;

    public DeleteDialog(Dialog mDialog, String title, String content, BtnListener listener) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init(title, content);
    }

    private void __init(String title, String content) {
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        titleTextView = mDialog.findViewById(R.id.title);
        contentTextView = mDialog.findViewById(R.id.content);
        tvDelete = mDialog.findViewById(R.id.delete);
        tvCancel = mDialog.findViewById(R.id.cancel);

        if (!TextUtils.isEmpty(title)) {
            titleTextView.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            contentTextView.setText(content);
        }

        tvDelete.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mDialog.dismiss();
                break;
            case R.id.delete:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.delete();
                }
                break;
        }

    }

    public interface BtnListener {
        void delete();
    }

}

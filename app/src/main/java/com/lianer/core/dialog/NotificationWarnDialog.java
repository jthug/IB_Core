package com.lianer.core.dialog;


import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.R;

/**
 * 通知权限弹窗
 * @author allison
 */
public class NotificationWarnDialog implements View.OnClickListener {
    private Dialog mDialog;
    private BtnListener mListener;
    private TextView tvRefuse, tvAllow;

    public NotificationWarnDialog(Dialog mDialog, BtnListener listener) {
        this.mListener = listener;
        this.mDialog = mDialog;
        __init();
    }

    private void __init() {
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        tvRefuse = mDialog.findViewById(R.id.refuse);
        tvAllow = mDialog.findViewById(R.id.allow);

        tvRefuse.setOnClickListener(this);
        tvAllow.setOnClickListener(this);
        mDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refuse:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.refuse();
                }
                break;
            case R.id.allow:
                mDialog.dismiss();
                if (mListener != null) {
                    mListener.allow();
                }
                break;
        }

    }

    public interface BtnListener {
        void allow();
        void refuse();
    }
}

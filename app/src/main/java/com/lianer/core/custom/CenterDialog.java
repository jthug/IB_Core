package com.lianer.core.custom;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.lianer.core.R;


/**
 * dialog自定义
 * @author allison
 */
public class CenterDialog extends Dialog {
    private Context mContext;
    private ViewGroup view;

    public CenterDialog(int layout, Context context) {
        super(context, R.style.MyDialog);
        mContext = context;
        __initDialog(layout);
    }

    private void __initDialog(int layout) {
        view = (ViewGroup) LayoutInflater.from(mContext).inflate(layout,null);
        setContentView(view);
        Window window = getWindow();
        setCanceledOnTouchOutside(true);
        if (window != null) {
//            window.setBackgroundDrawableResource(R.drawable.dialog_circle_oval_bg);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
            attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(attributes);
        }
    }

    public ViewGroup getContentView(){
        return view;
    }
}

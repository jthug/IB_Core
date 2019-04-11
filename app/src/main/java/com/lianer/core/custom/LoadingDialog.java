package com.lianer.core.custom;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.lianer.core.R;


/**
 * 缓冲进度显示
 * @author allison
 */
public class LoadingDialog extends Dialog {
    private Activity mActivity;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        this.mActivity = (Activity) context;
        init(context);
    }

    public LoadingDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mActivity = (Activity) context;
        init(context);
    }

    protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mActivity = (Activity) context;
        init(context);
    }

    @Override
    public void show() {
        if (!this.isShowing() && mActivity != null && !mActivity.isFinishing()) {
                super.show();
        }
    }

    @Override
    public void dismiss() {
        if (this.isShowing() && mActivity != null && !mActivity.isFinishing()) {
            super.dismiss();
        }
    }

    private void init(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_loading);
//        Window window = getWindow();
//        window.setBackgroundDrawableResource(R.drawable.loading_bg);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        __loadingAnim();
    }

    private void __loadingAnim() {
        ImageView iv = findViewById(R.id.load);

        ObjectAnimator animator = ObjectAnimator.ofFloat(iv, "rotation", 0f, 360.0f);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);//设置动画重复次数
        animator.setRepeatMode(ValueAnimator.RESTART);//动画重复模式
        animator.start();

    }

}

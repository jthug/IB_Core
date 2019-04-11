package com.lianer.core.custom;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import com.lianer.core.R;

public class SearchEditText extends AppCompatEditText {

    private static final String TAG = "SearchEditText";

    private Drawable searchImg, delImg;

    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        int pxDimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getContext().getResources().getDisplayMetrics());
        setPadding(pxDimension, 0, pxDimension, 0);
        searchImg = getContext().getResources().getDrawable(R.drawable.search);
        delImg = getContext().getResources().getDrawable(R.drawable.ic_clear);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawables();//内容变换后
            }
        });
        setDrawables();
    }

    private void setDrawables() {
        if (length() < 1) {
            setCompoundDrawablesWithIntrinsicBounds(searchImg, null, null, null);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(searchImg, null, delImg, null);
            Drawable[] compoundDrawables = getCompoundDrawables();
            // 0 ,1 ,2, 3对应左，上，右，下
            //如果图片小可以拉伸下,代码如下
//            Rect bounds = compoundDrawables[2].getBounds();
//            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
//                    getContext().getResources().getDisplayMetrics());
//            compoundDrawables[2].setBounds(bounds.left, bounds.top, bounds.right + size, bounds.bottom + size);
//            setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable[] compoundDrawables = getCompoundDrawables();
        if (compoundDrawables[2] != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY)) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }
}
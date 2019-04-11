package com.lianer.core.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lianer.core.R;

/**
 * 标题栏自定义
 * @author allison
 */
public class TitlebarView extends RelativeLayout {
    private TextView tv_title, tv_right;
    private ImageView iv_left, iv_right;
    private onViewClick mClick;

    public TitlebarView(Context context) {
        this(context, null);
    }

    public TitlebarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitlebarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this);
        initView(view);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitlebarView, defStyleAttr, 0);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.TitlebarView_leftDrawble:
                    iv_left.setImageResource(array.getResourceId(attr, 0));
                    break;
                case R.styleable.TitlebarView_centerTextColor:
                    tv_title.setTextColor(array.getColor(attr, Color.BLACK));
                    break;
                case R.styleable.TitlebarView_centerTitle:
                    tv_title.setText(array.getString(attr));
                    break;
                case R.styleable.TitlebarView_rightDrawable:
                    iv_right.setImageResource(array.getResourceId(attr, 0));
                    break;
                case R.styleable.TitlebarView_rightText:
                    tv_right.setText(array.getString(attr));
                    break;
                case R.styleable.TitlebarView_rightTextColor:
                    tv_right.setTextColor(array.getColor(attr, Color.BLACK));
                    break;
            }
        }
        array.recycle();
        iv_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.leftClick();
            }
        });
        iv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.rightImgClick();
            }
        });
        tv_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClick.rightTextClick();
            }
        });
    }

    private void initView(View view) {
        tv_title = view.findViewById(R.id.tv_title);
        tv_right = view.findViewById(R.id.tv_right);
        iv_left = view.findViewById(R.id.iv_left);
        iv_right = view.findViewById(R.id.iv_right);
    }

    public void setOnViewClick(onViewClick click) {
        mClick = click;
    }

    //设置左边返回按钮的显示与隐藏
    public void showLeftDrawable() {
        if (iv_left != null) {
            iv_left.setVisibility(View.VISIBLE);
        }
    }

    //设置右边显示的控件  0:右边按钮 1:右边文字
    public void setRightWidgetVisible(int flag) {
        switch (flag) {
            case 0:
                if (iv_right != null)
                    iv_right.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (tv_right != null)
                    tv_right.setVisibility(VISIBLE);
                break;
        }
    }

    //设置标题
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }


    //设置右标题
    public void setRightText(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_right.setText(title);
        }
    }

    //设置标题大小
    public void setTitleSize(int size) {
        if (tv_title != null) {
            tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }
    }

    //设置右标题大小
    public void setRightTextSize(int size) {
        if (tv_right != null) {
            tv_right.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        }
    }

    //设置左图标
    public void setLeftDrawable(int res) {
        if (iv_left != null) {
            iv_left.setImageResource(res);
        }
    }

    //设置右图标
    public void setRightDrawable(int res) {
        if (iv_right != null) {
            iv_right.setImageResource(res);
        }
    }

    public interface onViewClick {
        void leftClick();

        void rightTextClick();

        void rightImgClick();
    }
}

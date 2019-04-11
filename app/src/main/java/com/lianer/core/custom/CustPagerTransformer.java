package com.lianer.core.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 实现ViewPager左右滑动时的时差
 *
 * @author allison
 */
public class CustPagerTransformer implements ViewPager.PageTransformer {

    private int maxTranslateOffsetX;
    private ViewPager viewPager;

    private static float VIEW_HEIGHT = 0.15f;//左右两边fragment的高度，值越高，高度越小
    private static float VIEW_PADDING = 110;//左右两边fragment距离中间fragment的间距，值越大，间距越近


    public CustPagerTransformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, VIEW_PADDING);
    }

    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }

        int leftInScreen = view.getLeft() - viewPager.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * VIEW_HEIGHT / viewPager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
        }
    }

    /**
     * dp和像素转换
     */
    private int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

}

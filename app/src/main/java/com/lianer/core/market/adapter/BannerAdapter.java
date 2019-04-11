package com.lianer.core.market.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.market.BannerFragment;
import com.lianer.core.market.bean.BannerBean;

import java.util.List;

/**
 * banner 适配器
 *
 * @author allison
 */
public class BannerAdapter extends FragmentStatePagerAdapter {

    Context mContext;
    List<BannerFragment> mFragments;
    List<String> mImages;
    List<BannerBean> mBannerDataList;

    public BannerAdapter(Context context, FragmentManager fm, List<BannerFragment> fragments, List<String> images, List<BannerBean> bannerDataList) {
        super(fm);
        this.mContext = context;
        this.mFragments = fragments;
        this.mImages = images;
        this.mBannerDataList = bannerDataList;
    }

    @Override
    public Fragment getItem(final int position) {
        BannerFragment fragment = mFragments.get(position % 500);
        fragment.bindData(mImages.get(position % mImages.size()));
        fragment.setOnImageClick(() -> {
            if (mBannerDataList.size() != 0) {
                Intent intent = new Intent(mContext, BannerDetailAct.class);
                intent.putExtra("webUrl",
                        MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED
                                ? mBannerDataList.get(position % mBannerDataList.size()).getCn().getUrl()
                                : mBannerDataList.get(position % mBannerDataList.size()).getEn().getUrl());
                mContext.startActivity(intent);
            }
        });
        return fragment;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public void setmImages(List<String> mImages) {
        this.mImages = mImages;
    }

    public void setmBannerDataList(List<BannerBean> mBannerDataList) {
        this.mBannerDataList = mBannerDataList;
    }
}

package com.lianer.core.market;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lianer.core.R;
import com.lianer.core.base.BaseFragment;
import com.lianer.core.databinding.FragmentMarketBinding;
import com.lianer.core.market.model.MarketModel;

/**
 * 市场
 *
 * @author allison
 */
public class MarketFrag extends BaseFragment {

    FragmentMarketBinding marketBinding;
    public MarketModel marketModel;

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        marketBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_market, container, false);
        marketModel = new MarketModel(mActivity, marketBinding);
        //禁止手势滑动
        marketBinding.contractFilter.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        marketModel.initClick();
        marketModel.initBannerViewPager(getFragmentManager());
        marketModel.initRecyclerView();
        marketModel.initRefresh();
        marketModel.initMortgageRecyclerView();
        marketModel.loadMortgageData();
        return marketBinding.getRoot();
    }

    @Override
    protected void initData() {
        marketModel.loadBannerData();
        marketModel.loadData();
        marketModel.loadTransactionRecord();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

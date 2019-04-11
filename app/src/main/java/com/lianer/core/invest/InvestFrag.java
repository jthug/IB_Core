package com.lianer.core.invest;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.base.BaseFragment;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.RVItemDecoration;
import com.lianer.core.invest.adapter.InvestHomeAdapter;
import com.lianer.core.invest.bean.ContractHomeRequest;
import com.lianer.core.invest.model.BannerDelete;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.GlideImageLoader;
import com.lianer.core.utils.HttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.config.ContractStatus.CONTRACT_HOME;

/**
 * 投资页
 *
 * @author allison
 */
public class InvestFrag extends BaseFragment {

    View mView;
    InvestHomeAdapter investHomeAdapter;
    List<ContractResponse> dataList = new ArrayList<>();
    int offset;
    String orderbyCondition;//查询条件
    String currentWalletAddress;
    Banner banner;
    RecyclerView recyclerView;
    View vsContractNull;
    SmartRefreshLayout refreshLayout;
    List<String> bannerImages = new ArrayList<>();
    List<BannerDelete> bannerBeanList = new ArrayList<>();
    int languageType;

    @Override
    public void onResume() {
        super.onResume();
        if (mActivity != null) {
            HLWallet hlWallet = HLWalletManager.shared().getCurrentWallet(mActivity);
            if (hlWallet != null) {
                currentWalletAddress = hlWallet.getAddress();
                if (investHomeAdapter != null) {
                    investHomeAdapter.setWalletAddress(currentWalletAddress);
                }
            }
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_invest, null);

        initRecyclerView();
        initRefresh();
        initBannar();

        return mView;
    }

    /**
     * 初始化bannar
     */
    private void initBannar() {
        languageType = MultiLanguageUtil.getInstance().getLanguageType();
        banner = mView.findViewById(R.id.banner);
        banner.setOnBannerListener(position -> {
            if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
                if (!TextUtils.isEmpty(bannerBeanList.get(position).getPageCnUrl())) {
                    Intent intent = new Intent(getContext(), BannerDetailAct.class);
                    intent.putExtra("webUrl", bannerBeanList.get(position).getPageCnUrl());
                    mActivity.startActivity(intent);
                }
            } else {
                if (!TextUtils.isEmpty(bannerBeanList.get(position).getPageEnUrl())) {
                    Intent intent = new Intent(getContext(), BannerDetailAct.class);
                    intent.putExtra("webUrl", bannerBeanList.get(position).getPageEnUrl());
                    mActivity.startActivity(intent);
                }
            }
        });
        //设置轮播时间
        banner.setDelayTime(3000);
    }

    @SuppressLint("CheckResult")
    private void loadBanner() {
        HttpUtil.queryBanner("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<List<BannerDelete>>() {
                    @Override
                    protected void success(List<BannerDelete> bannerBeans) {
                        if (bannerBeans != null && bannerBeans.size() != 0) {
                            if (bannerBeanList.size() != 0) {
                                bannerBeanList.clear();
                            }
                            bannerBeanList.addAll(bannerBeans);
                            if (bannerImages.size() != 0) {
                                bannerImages.clear();
                            }
                            for (BannerDelete bannerBean : bannerBeans) {
                                //判断中英文模式
                                if (languageType == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
                                    bannerImages.add(bannerBean.getImageCnUrl());
                                } else {
                                    bannerImages.add(bannerBean.getImageEnUrl());
                                }
                            }

                            banner.setImages(bannerImages).setImageLoader(new GlideImageLoader()).start();
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
                            banner.setBackgroundResource(R.drawable.banner_cn);
                        } else {
                            banner.setBackgroundResource(R.drawable.banner_en);
                        }
                    }
                });

    }

    @Override
    protected void initData() {
        loadBanner();
        loadContractData();
    }

    /**
     * 加载合约列表数据
     */
    private void loadContractData() {
        initResponseData(initRequestBody());
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        recyclerView = mView.findViewById(R.id.recyclerView);
        investHomeAdapter = new InvestHomeAdapter(mActivity, dataList, null);
        investHomeAdapter.setWalletAddress(currentWalletAddress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置Adapter
        recyclerView.addItemDecoration(new RVItemDecoration(LinearLayoutManager.VERTICAL, 30));
        recyclerView.setAdapter(investHomeAdapter);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //解决数据加载不完的问题
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        //解决数据加载完成后, 没有停留在顶部的问题
        recyclerView.setFocusable(false);
    }

    /**
     * 初始化刷新组件
     */
    private void initRefresh() {
        refreshLayout = mView.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableAutoLoadMore(false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadBanner();
            initResponseData(initRequestBody());
        });
    }

    /**
     * 加载请求参数
     */
    private String initRequestBody() {
        int count = 10;
        orderbyCondition = "contractCreateDate desc";
        ContractHomeRequest homeRequest = new ContractHomeRequest();
        homeRequest.setContractStatusList(CONTRACT_HOME);
        homeRequest.setCount(count);
        homeRequest.setOffset(offset);
        homeRequest.setOrderby(orderbyCondition);
        Gson gson = new Gson();
        return gson.toJson(homeRequest);
    }

    /**
     * 获取响应数据
     */
    private void initResponseData(String jsonRequestParams) {
        HttpUtil.getContractList(jsonRequestParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<List<ContractResponse>>() {
                    @Override
                    protected void success(List<ContractResponse> contractResponses) {
                        refreshLayout.finishRefresh();
                        if (contractResponses != null && contractResponses.size() != 0) {
                            showContractData();
                            if (dataList.size() != 0) {
                                dataList.clear();
                            }
                            dataList.addAll(contractResponses);
                            if (dataList.size() == 10) {
                                dataList.add(new ContractResponse());//加上全部合约文本按钮
                            }
                            investHomeAdapter.notifyDataSetChanged();
                        } else {
                            showContractEmptyView();
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        refreshLayout.finishRefresh();
                        KLog.i(error.getMessage());
                        showContractEmptyView();
                    }
                });
    }

    /**
     * 显示合约为空布局
     */
    private void showContractEmptyView() {
        recyclerView.setVisibility(View.GONE);
        if (vsContractNull == null) {
            ViewStub vsContractNullStub = mView.findViewById(R.id.contract_null);
            vsContractNull = vsContractNullStub.inflate();
            ImageView contractPicture = vsContractNull.findViewById(R.id.contract_state_picture);
            contractPicture.setImageResource(R.drawable.contract_null);
            TextView contractNullWarn = vsContractNull.findViewById(R.id.contract_state_warn);
//            Button contractBtn = vsContractNull.findViewById(R.id.contract_btn);
//            contractPicture.setImageResource(R.drawable.record_null);
//            contractNullWarn.setText(R.string.no_contract_sheet);
//            contractBtn.setText(R.string.go_invest);
//            contractBtn.setVisibility(View.GONE);
        } else {
            vsContractNull.setVisibility(View.VISIBLE);
        }
    }


    public void showContractData() {
        recyclerView.setVisibility(View.VISIBLE);
        if (vsContractNull != null) {
            vsContractNull.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }
}

package com.lianer.core.market.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Singleton;
import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.R;
import com.lianer.core.app.Constants;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.ContractActivity;
import com.lianer.core.contract.ContractTusdActivity;
import com.lianer.core.custom.CustPagerTransformer;
import com.lianer.core.databinding.FragmentMarketBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.market.BannerFragment;
import com.lianer.core.market.adapter.BannerAdapter;
import com.lianer.core.market.bean.BannerBean;
import com.lianer.core.market.bean.BannerResponse;
import com.lianer.core.market.bean.MarketContractBean;
import com.lianer.core.market.bean.MarketContractResponse;
import com.lianer.core.market.bean.MarketListRequest;
import com.lianer.core.market.bean.MortgagedAssetsResponse;
import com.lianer.core.market.bean.MortgagedfAssetsBean;
import com.lianer.core.market.bean.TransactionRecordResponse;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.HttpUtil;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.utils.ThreadUtils.runOnUiThread;


/**
 * 市场逻辑处理类
 *
 * @author allison
 */
public class MarketModel {


    private FragmentMarketBinding mMarketBinding;
    private Context mContext;
    private QuickAdapter quickAdapter, mortgageAdapter;
    private List<MarketContractBean> mDataList;
    private List<BannerBean> mBannerDataList;
    private int page = 1;
    private int freshState;
    private String type = Constants.MARKET_MORTGAGE_RATE_TYPE, sort = Constants.MARKET_SORT_ASC;
    private boolean isSort = true, isInterestRate, isTimeLimit, isNumber;
    private List<String> token = new ArrayList<>();
    private List<String> imgList = new ArrayList<>();
    private List<BannerFragment> fragments = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private boolean isRunning = true;
    private List<MortgagedfAssetsBean> mMortgageList;
    private List<String> mMortgagedSelectedtList = new ArrayList<>();

    public MarketModel(Context context, FragmentMarketBinding mMarketBinding) {
        this.mContext = context;
        this.mMarketBinding = mMarketBinding;
        this.mBannerDataList = new ArrayList<>();
        this.mDataList = new ArrayList<>();
        this.mMortgageList = new ArrayList<>();
    }

    /**
     * 初始化 banner
     *
     * @param fragmentManager
     */
    public void initBannerViewPager(FragmentManager fragmentManager) {
        // 1. viewPager添加parallax效果，使用PageTransformer就足够了
        mMarketBinding.marketBannerViewpager.setPageTransformer(false, new CustPagerTransformer(mContext));
        mMarketBinding.marketBannerViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        for (int i = 0; i < 4; i++) {
//            // 预先准备fragment
//            imgList.add("");
//        }

        // 2. viewPager添加adapter
        for (int i = 0; i < 500; i++) {
            // 预先准备fragment
            fragments.add(new BannerFragment());
        }

        bannerAdapter = new BannerAdapter(mContext, fragmentManager, fragments, imgList, mBannerDataList);
//        mMarketBinding.marketBannerViewpager.setAdapter(bannerAdapter);
//        mMarketBinding.marketBannerViewpager.setCurrentItem(fragments.size() / 2);

        new Thread() {
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 往下跳一位
                    runOnUiThread(() -> mMarketBinding.marketBannerViewpager.setCurrentItem(mMarketBinding.marketBannerViewpager.getCurrentItem() + 1));
                }
            }

        }.start();
    }

    /**
     * 初始化合约列表
     */
    public void initRecyclerView() {
        quickAdapter = new QuickAdapter<MarketContractBean>(mDataList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_market_contract;
            }

            @Override
            public void convert(VH holder, MarketContractBean contractBean, int position) {
                //借币人地址对比
                if (contractBean.getBorrowAddress().equalsIgnoreCase(HLWalletManager.shared().getCurrentWallet(mContext).getAddress())){
                    if(MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_EN){
                        holder.setImage(R.id.market_mine_label, R.drawable.ic_icon_mine_en);
                    }else{
                        holder.setImage(R.id.market_mine_label, R.drawable.ic_icon_mine);
                    }
                }
                //eth值
                holder.setText(R.id.market_value, Convert.fromWei(contractBean.getAmount(), Convert.Unit.ETHER).toString());

                String tokenSymbol;//获取当前抵押资产的简称
                // ETH
                if(contractBean.getLenderToken() == null){
                    holder.setText(R.id.market_coins_type, mContext.getString(R.string.eth));
                    tokenSymbol = MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress());
                    String mortgage = CommomUtil.getTokenValue(mContext,contractBean.getTokenAddress(),contractBean.getMortgage());
                    holder.setText(R.id.market_mortgage_amount, CommomUtil.doubleFormat(mortgage) + " " + tokenSymbol);
                    holder.setImage(R.id.market_eth_img,R.drawable.ic_eth);
                }

                else if (contractBean.getLenderToken().equalsIgnoreCase(Constants.ASSETS_TUSD_ADDRESS)){
                    holder.setText(R.id.market_coins_type, mContext.getString(R.string.tusd));
                    holder.setImage(R.id.market_eth_img,R.drawable.ic_tusd);
                    // TUSD 1
                    if(contractBean.getTokenAddress().equalsIgnoreCase("0x0000000000000000000000000000000000000000")){
                        String mortgage = Convert.fromWei(contractBean.getMortgage(), Convert.Unit.ETHER).toString();
                        holder.setText(R.id.market_mortgage_amount,mContext.getString(R.string.eth_amount, CommomUtil.doubleFormat(mortgage)) );
                    }
                    // TUSD 2
                    else {
                        tokenSymbol = MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress());
                        String mortgage = CommomUtil.getTokenValue(mContext,contractBean.getTokenAddress(),contractBean.getMortgage());
                        holder.setText(R.id.market_mortgage_amount, CommomUtil.doubleFormat(mortgage) + " " + tokenSymbol);
                    }
                }

                //周期
                int cycle = Integer.parseInt(contractBean.getCycle()) / 86400;
                holder.setText(R.id.market_timelimit, cycle + "");

                //利率
                double smallValue = Double.parseDouble(contractBean.getInterest()) / 10;
                if (smallValue % 1 == 0) {
                    holder.setText(R.id.market_interest_rate, String.format("%s ‰", String.valueOf(smallValue).substring(0, String.valueOf(smallValue).indexOf("."))));
                } else {
                    holder.setText(R.id.market_interest_rate, String.format("%s ‰", String.valueOf(smallValue)));
                }

//                //抵押资产
//                String mortgage = CommomUtil.getTokenValue(contractBean.getTokenAddress(),contractBean.getMortgage());
//                holder.setText(R.id.market_mortgage_amount, String.format("%s %s", CommomUtil.decimalToZeroPoint(mortgage), MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress())));

                //抵押率
                holder.setText(R.id.market_yesterday_value, String.format("%s\t%%", String.valueOf(contractBean.getMortgageRate())));

                holder.getView(R.id.rl_all_contract).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(contractBean.getLenderToken() == null){
                            Intent intent = new Intent(mContext, ContractActivity.class);
                            intent.putExtra("ContractId", -2L);
                            intent.putExtra("ContractAddress", contractBean.getAddress());
                            mContext.startActivity(intent);
                        }else if (contractBean.getLenderToken().equalsIgnoreCase(Constants.ASSETS_TUSD_ADDRESS)){
                            Intent intent = new Intent(mContext, ContractTusdActivity.class);
                            intent.putExtra("ContractId", -2L);
                            intent.putExtra("ContractAddress", contractBean.getAddress());
                            mContext.startActivity(intent);
                        }
                    }
                });
            }
        };
        //设置布局管理器
        mMarketBinding.marketRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        //设置Adapter
        mMarketBinding.marketRecyclerview.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        mMarketBinding.marketRecyclerview.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化抵押资产筛选列表
     */
    public void initMortgageRecyclerView() {
        mortgageAdapter = new QuickAdapter<MortgagedfAssetsBean>(mMortgageList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_mortgaged_type;
            }

            @Override
            public void convert(VH holder, MortgagedfAssetsBean mortgagedfAssetsBean, int position) {
                TextView mortgageType = holder.getView(R.id.mortgage_type);
                mortgageType.setText(mortgagedfAssetsBean.getTokenAbbreviation());

                holder.getView(R.id.mortgage_type).setSelected(mMortgageList.get(position).isSelected());

                mortgageType.setOnClickListener(v -> {
                    //设置选择状态
                    holder.getView(R.id.mortgage_type).setSelected(!mMortgageList.get(position).isSelected());
                    //设置选择数据
                    if (mortgageType.isSelected()) {
                        mMortgagedSelectedtList.add(String.valueOf(mortgagedfAssetsBean.getTokenNum()));
                        mMortgageList.get(position).setSelected(true);
                    } else {
                        mMortgagedSelectedtList.remove(String.valueOf(mortgagedfAssetsBean.getTokenNum()));
                        mMortgageList.get(position).setSelected(false);
                    }
                });
            }
        };
        //设置布局管理器
        mMarketBinding.mortgageTypeRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        //设置Adapter
        mMarketBinding.mortgageTypeRecyclerView.setAdapter(mortgageAdapter);
        //设置增加或删除条目的动画
        mMarketBinding.mortgageTypeRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化刷新组件（下拉刷新，上拉加载）
     */
    public void initRefresh() {
        mMarketBinding.marketRefresh.setEnableRefresh(true);
        mMarketBinding.marketRefresh.setEnableLoadMore(true);
        mMarketBinding.marketRefresh.setOnRefreshListener(refreshlayout -> {
            page = 1;
            freshState = 1;
            loadData();
            loadMortgageData();
        });
        mMarketBinding.marketRefresh.setOnLoadMoreListener(refreshlayout -> {
            page++;
            freshState = 2;
            loadData();
        });
    }

    /**
     * 加载banner数据
     */
    public void loadBannerData() {
        HttpUtil.getBanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<BannerResponse>() {
                    @Override
                    protected void success(BannerResponse bannerResponse) {
                        if (bannerResponse != null) {
                            if (bannerResponse.getCode().equals(Constants.REQUEST_SUCCESS)) {

                                List<BannerBean> bannerBeans = bannerResponse.getData();

                                if (freshState != 2) {
                                    //有数据的话需要清空数据
                                    if (mBannerDataList.size() != 0) {
                                        mBannerDataList.clear();
                                    }
                                    if (imgList.size() != 0) {
                                        imgList.clear();
                                    }

                                }

                                if (bannerBeans.size() != 0) {
                                    mBannerDataList.addAll(bannerBeans);
                                    for (int i = 0; i < bannerBeans.size(); i++) {
                                        if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
                                            imgList.add(bannerBeans.get(i).getCn().getImg());
                                        } else {
                                            imgList.add(bannerBeans.get(i).getEn().getImg());
                                        }
                                    }

                                    bannerAdapter.setmImages(imgList);
                                    bannerAdapter.setmBannerDataList(mBannerDataList);
                                    mMarketBinding.marketBannerViewpager.setAdapter(bannerAdapter);
                                    mMarketBinding.marketBannerViewpager.setCurrentItem(fragments.size() / 2);
                                }
                            }
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.i(error.getMessage());
                        if (mBannerDataList.size() != 0) {
                            mBannerDataList.clear();
                        }

                        if (imgList.size() != 0) {
                            imgList.clear();
                        }

                        for (int i = 0; i < 3; i++) {
                            imgList.add("");
                        }

                        bannerAdapter.setmImages(imgList);
                        bannerAdapter.setmBannerDataList(mBannerDataList);
                        mMarketBinding.marketBannerViewpager.setAdapter(bannerAdapter);
                        mMarketBinding.marketBannerViewpager.setCurrentItem(fragments.size() / 2);
                    }
                });
    }

    public void loadTransactionRecord (){
            HttpUtil.getTransactionRecord()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<TransactionRecordResponse>() {
                        @Override
                        protected void success(TransactionRecordResponse data) {
                            String sAmount = data.getAmount();
//                            int amount = Integer.parseInt(sAmount);
//                            if (amount>=10000){
//                                sAmount = amount/10000.0+"万";
//                            }
                            long eth = Convert.fromWei(data.getTransactionValueETH(), Convert.Unit.ETHER).longValue();
                            long tusd = Convert.fromWei(data.getTransactionValueTUSD(), Convert.Unit.ETHER).longValue();
                            String sEth = String.valueOf(eth);
                            String sTusd = String.valueOf(tusd);
                            if (eth>=10000){
                                BigDecimal bEth = new BigDecimal(eth / 10000.0).setScale(2, RoundingMode.HALF_UP);
                                sEth = bEth+"万";
                            }
                            if (tusd>=10000){
                                BigDecimal bTusd = new BigDecimal(tusd / 10000.0).setScale(2, RoundingMode.HALF_UP);
                                sTusd = bTusd+"万";
                            }
                            mMarketBinding.transactionRecord.setText(mContext.getString(R.string.transaction_record_statistics,
//                                    data.getAmount(),data.getTransactionValueETH(),data.getTransactionValueTUSD()));
                                    sAmount,sEth,sTusd));
                            mMarketBinding.transactionRecord.setSelected(true);
                        }

                        @Override
                        protected void failure(HLError error) {

                        }
                    });
    }

    /**
     * 加载合约列表数据
     */
    public void loadData() {
        HttpUtil.getMarketList(setMarketListRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<MarketContractResponse>(mContext, false) {
                    @Override
                    protected void success(MarketContractResponse marketContractResponse) {
                        if (marketContractResponse != null) {
                            //停止刷新
                            mMarketBinding.marketRefresh.finishRefresh();
                            //停止加载
                            mMarketBinding.marketRefresh.finishLoadMore();
                            if (marketContractResponse.getCode().equals(Constants.REQUEST_SUCCESS)) {
                                List<MarketContractBean> marketContractBeans = marketContractResponse.getData();
                                if (marketContractBeans.size() == 0) {
                                    showContractEmptyView();
                                }

                                if (freshState != 2) {
                                    //有数据的话需要清空数据
                                    if (mDataList.size() != 0) {
                                        mDataList.clear();
                                    }
                                }

                                if (marketContractBeans.size() != 0) {

                                    mDataList.addAll(marketContractBeans);
                                    //显示列表，隐藏空布局
                                    notifyListData();

                                    //判断是否已无更多数据
                                    if (marketContractBeans.size() < 20) {
                                        mMarketBinding.marketRefresh.finishLoadMoreWithNoMoreData();
                                    }
                                } else {
                                    if (freshState != 2) {
                                        //显示空布局，隐藏列表
                                        showContractEmptyView();
                                    } else {
                                        mMarketBinding.marketRefresh.finishLoadMoreWithNoMoreData();
                                    }
                                }
                            } else {
                                showContractEmptyView();
                            }
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        mMarketBinding.marketRefresh.finishLoadMore();
                        mMarketBinding.marketRefresh.finishRefresh();
                        showContractEmptyView();
                        KLog.i(error.getMessage());
                    }
                });

    }


    /**
     * 加载抵押资产筛选列表
     */
    public void loadMortgageData() {
        HttpUtil.getMortgagedAssets()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<MortgagedAssetsResponse>() {
                    @Override
                    protected void success(MortgagedAssetsResponse mortgagedAssetsResponse) {
                        if (mortgagedAssetsResponse != null) {
                            if (mortgagedAssetsResponse.getCode().equals(Constants.REQUEST_SUCCESS)) {
                                List<MortgagedfAssetsBean> tempMortgagedfAssetsBeans = new ArrayList<>(mMortgageList);
                                if (mMortgageList.size() != 0) {
                                    mMortgageList.clear();
                                }

                                List<MortgagedfAssetsBean> mortgagedfAssetsBeanList = mortgagedAssetsResponse.getData();
                                if (mortgagedfAssetsBeanList.size() != 0) {
                                    mMortgageList.addAll(mortgagedfAssetsBeanList);
                                    for (MortgagedfAssetsBean mortgagedfAssetsBean : tempMortgagedfAssetsBeans) {
                                        for (int i = 0; i < mortgagedfAssetsBeanList.size(); i++) {
                                            if (mortgagedfAssetsBean.getTokenNum() == mortgagedfAssetsBeanList.get(i).getTokenNum()) {
                                                mMortgageList.get(i).setSelected(mortgagedfAssetsBean.isSelected());
                                            }
                                        }
                                    }
                                    mortgageAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.i(error.getMessage());
                    }
                });

    }

    /**
     * 重置
     */
    public void reset() {
        if (mMortgagedSelectedtList.size() != 0) {
            mMortgagedSelectedtList.clear();
        }
        //重置为未点击状态
        for (int i = 0; i < mMortgageList.size(); i++) {
            mMortgageList.get(i).setSelected(false);
        }
        mortgageAdapter.notifyDataSetChanged();
    }

    /**
     * 获取已选择的抵押资产筛选列表
     */
    private List<String> getmMortgagedSelectedtList() {
        return mMortgagedSelectedtList;
    }

    /**
     * 设置市场列表请求数据
     */
    private String setMarketListRequestBody() {
        MarketListRequest marketListRequest = new MarketListRequest();
        marketListRequest.setType(type);
        marketListRequest.setSort(sort);
        marketListRequest.setToken(token);
        int offset = 10;
        marketListRequest.setOffset(String.valueOf(offset));
        marketListRequest.setPage(String.valueOf(page));
        String jsonParams = Singleton.gson().toJson(marketListRequest);
        KLog.i(jsonParams);
        return jsonParams;
    }

    /**
     * 刷新列表数据
     */
    private void notifyListData() {
        mMarketBinding.marketRecyclerview.setVisibility(View.VISIBLE);
        mMarketBinding.contractNull.setVisibility(View.GONE);
        quickAdapter.notifyDataSetChanged();
    }

    /**
     * 显示合约为空布局
     */
    private void showContractEmptyView() {
        mMarketBinding.marketRecyclerview.setVisibility(View.GONE);
        mMarketBinding.contractNull.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化点击事件
     */
    public void initClick() {
        changeChooseStatus(mMarketBinding.marketMortgageRate);
        //抵押率
        mMarketBinding.marketMortgageRate.setOnClickListener(v -> {
            type = Constants.MARKET_MORTGAGE_RATE_TYPE;
            changeChooseStatus(mMarketBinding.marketMortgageRate);
            changeSort(0);
            hindDrawerLayout();
            loadData();
        });
        //利率
        mMarketBinding.marketInterestRate.setOnClickListener(v -> {
            type = Constants.MARKET_INTEREST_TYPE;
            changeChooseStatus(mMarketBinding.marketInterestRate);
            changeSort(1);
            hindDrawerLayout();
            loadData();
        });
        //周期
        mMarketBinding.marketCycle.setOnClickListener(v -> {
            type = Constants.MARKET_CYCLE_TYPE;
            changeChooseStatus(mMarketBinding.marketCycle);
            changeSort(2);
            hindDrawerLayout();
            loadData();
        });
        //数量
        mMarketBinding.marketBorrowAmount.setOnClickListener(v -> {
            type = Constants.MARKET_AMOUNT_TYPE;
            changeChooseStatus(mMarketBinding.marketBorrowAmount);
            changeSort(3);
            hindDrawerLayout();
            loadData();
        });
        //筛选
        mMarketBinding.choose.setOnClickListener(v -> switchDrawerLayout());

        //确定
        mMarketBinding.sure.setOnClickListener(v -> {
            if (mMarketBinding.contractFilter.isDrawerOpen(mMarketBinding.marketDrawerLayout)) {
                mMarketBinding.contractFilter.closeDrawer(mMarketBinding.marketDrawerLayout);
            }
        });
        //重置
        mMarketBinding.reset.setOnClickListener(v -> reset());
        //drawerlayout监听事件
        mMarketBinding.contractFilter.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(View drawerView) {

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                filterDataByToken(getmMortgagedSelectedtList());
            }
            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });


    }

    /**
     * 改变排序规则
     */
    private void changeSort(int type) {
        Drawable downSelected = mContext.getResources().getDrawable(R.drawable.ic_filter_desc);
        downSelected.setBounds(0, 0, downSelected.getMinimumWidth(), downSelected.getMinimumHeight());
        Drawable upSelected = mContext.getResources().getDrawable(R.drawable.ic_filter_asc);
        upSelected.setBounds(0, 0, upSelected.getMinimumWidth(), upSelected.getMinimumHeight());

        switch (type) {
            case 0://抵押率
                isSort = !isSort;
                sort = isSort ? Constants.MARKET_SORT_ASC : Constants.MARKET_SORT_DESC;
                if (isInterestRate) {
                    isInterestRate = false;
                }
                if (isTimeLimit) {
                    isTimeLimit = false;
                }
                if (isNumber) {
                    isNumber = false;
                }
                mMarketBinding.marketMortgageRate.setCompoundDrawables(null, null, isSort ? downSelected : upSelected, null);
                break;
            case 1://利率
                isInterestRate = !isInterestRate;
                sort = isInterestRate ? Constants.MARKET_SORT_DESC : Constants.MARKET_SORT_ASC;
                if (isSort) {
                    isSort = false;
                }
                if (isTimeLimit) {
                    isTimeLimit = false;
                }
                if (isNumber) {
                    isNumber = false;
                }
                mMarketBinding.marketInterestRate.setCompoundDrawables(null, null, isInterestRate ? downSelected : upSelected, null);
                break;
            case 2://周期
                isTimeLimit = !isTimeLimit;
                sort = isTimeLimit ? Constants.MARKET_SORT_DESC : Constants.MARKET_SORT_ASC;
                if (isSort) {
                    isSort = false;
                }
                if (isInterestRate) {
                    isInterestRate = false;
                }
                if (isNumber) {
                    isNumber = false;
                }
                mMarketBinding.marketCycle.setCompoundDrawables(null, null, isTimeLimit ? downSelected : upSelected, null);
                break;
            case 3://数量
                isNumber = !isNumber;
                sort = isNumber ? Constants.MARKET_SORT_DESC : Constants.MARKET_SORT_ASC;
                if (isSort) {
                    isSort = false;
                }
                if (isInterestRate) {
                    isInterestRate = false;
                }
                if (isTimeLimit) {
                    isTimeLimit = false;
                }
                mMarketBinding.marketBorrowAmount.setCompoundDrawables(null, null, isNumber ? downSelected : upSelected, null);
                break;

        }

    }

    /**
     * 改变筛选栏的显示状态
     */
    private void changeChooseStatus(TextView textView) {
        mMarketBinding.marketMortgageRate.setSelected(false);
        mMarketBinding.marketInterestRate.setSelected(false);
        mMarketBinding.marketCycle.setSelected(false);
        mMarketBinding.marketBorrowAmount.setSelected(false);

        Drawable unSelected = mContext.getResources().getDrawable(R.drawable.ic_filter_normal);
        // 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
        unSelected.setBounds(0, 0, unSelected.getMinimumWidth(), unSelected.getMinimumHeight());
        Drawable selected = mContext.getResources().getDrawable(R.drawable.ic_filter_desc);
        selected.setBounds(0, 0, selected.getMinimumWidth(), selected.getMinimumHeight());

        mMarketBinding.marketMortgageRate.setCompoundDrawables(null, null, unSelected, null);
        mMarketBinding.marketInterestRate.setCompoundDrawables(null, null, unSelected, null);
        mMarketBinding.marketCycle.setCompoundDrawables(null, null, unSelected, null);
        mMarketBinding.marketBorrowAmount.setCompoundDrawables(null, null, unSelected, null);

        textView.setCompoundDrawables(null, null, selected, null);
        textView.setSelected(true);
    }

    /**
     * 筛选数据（通过所选的代币）
     */
    private void filterDataByToken(List<String> mortgagedAssets) {
        if (mortgagedAssets == null) return;

        if (token.size() != 0) {
            token.clear();
        }

        if (mortgagedAssets.size() != 0) {
            token.addAll(mortgagedAssets);
        }

        loadData();

    }

    /**
     * 切换抽屉状态  显示/隐藏
     */
    private void switchDrawerLayout() {
        if (!mMarketBinding.contractFilter.isDrawerOpen(mMarketBinding.marketDrawerLayout)) {
            mMarketBinding.contractFilter.openDrawer(mMarketBinding.marketDrawerLayout);
        } else {
            mMarketBinding.contractFilter.closeDrawer(mMarketBinding.marketDrawerLayout);
        }
    }

    /**
     * 隐藏抽屉栏
     */
    private void hindDrawerLayout() {
        if (mMarketBinding.contractFilter.isDrawerOpen(mMarketBinding.marketDrawerLayout)) {
            mMarketBinding.contractFilter.closeDrawer(mMarketBinding.marketDrawerLayout);
        }
    }

}

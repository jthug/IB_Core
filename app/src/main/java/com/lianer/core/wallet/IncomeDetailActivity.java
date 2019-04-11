package com.lianer.core.wallet;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.google.gson.Gson;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityIncomeDetailBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.wallet.bean.IncomeDetailBean;
import com.lianer.core.wallet.bean.IncomeDetailResponse;
import org.web3j.utils.Convert;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 资产详情
 *
 * @author allison
 */
public class IncomeDetailActivity extends BaseActivity {

    private ActivityIncomeDetailBinding mBinding;
    private List<IncomeDetailBean> mDatas = new ArrayList<>();
    private QuickAdapter quickAdapter;

    private int page = 1;
    private int freshState;//刷新状态  0:正常状态  1:下拉刷新  2:上拉加载
    private HLWallet mWallet;
    private String mType ;
    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_income_detail);
        mWallet = HLWalletManager.shared().getCurrentWallet(this);
        mType = getIntent().getStringExtra("Tpye");


        initTitle();
        initRecyclerView();
        initRefreshView();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();

        initHttpData();

    }



    private void initRefreshView() {
        //下拉刷新
        mBinding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            page = 1;
            freshState = 1;
            initHttpData();
        });
        //上拉加载
        mBinding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            page++;
            freshState = 2;
            initHttpData();
        });
    }

    private void initHttpData() {

        AddressAndPage addressAndPage = new AddressAndPage();
        addressAndPage.setToAddress(mWallet.getAddress());
        addressAndPage.setPage(page+"");
        if (mType.equals("Dividend")) {

                    HttpUtil.queryMyDividendDetail(new Gson().toJson(addressAndPage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<IncomeDetailResponse>(IncomeDetailActivity.this, freshState == 0) {
                    @Override
                    protected void success(IncomeDetailResponse data) {
                        KLog.w("我的分红详情 :"+     data);
                        loadTxInfo(data);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :"+ error);
                        errorDealwith();
                    }
                });
        } else if(mType.equals("Mining")){
            HttpUtil.queryMyMiningDetail(new Gson().toJson(addressAndPage))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<IncomeDetailResponse>(IncomeDetailActivity.this, freshState == 0) {
                        @Override
                        protected void success(IncomeDetailResponse data) {
                            KLog.w("我的挖矿详情 :"+     data);
                            loadTxInfo(data);
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.w("failure :"+ error);
                            errorDealwith();
                        }
                    });
        }

    }

    /**
     * 数据请求异常处理
     */
    private void errorDealwith() {
        if (freshState == 0) {
            mBinding.ivRecordNull.setVisibility(View.VISIBLE);
            mBinding.tvRecordNull.setVisibility(View.VISIBLE);
        } if (freshState == 1) {
            mBinding.refreshLayout.finishRefresh();
        } else {
            mBinding.refreshLayout.finishLoadMore();
        }
    }

    /**
     * 加载交易信息
     */
    private void loadTxInfo(IncomeDetailResponse response) {
        switch (freshState) {
            case 0:
                if (mDatas.size() != 0) {
                    mDatas.clear();
                }
                if (response != null) {
                    List<IncomeDetailBean> datas = response.getData();
                    boolean isZeroList = datas.size() == 0;
                    mBinding.ivRecordNull.setVisibility(isZeroList ? View.VISIBLE : View.GONE);
                    mBinding.tvRecordNull.setVisibility(isZeroList ? View.VISIBLE : View.GONE);
                    mBinding.refreshLayout.setVisibility(isZeroList ? View.GONE : View.VISIBLE);
                    if (!isZeroList) {
                        mDatas.addAll(datas);
                        quickAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case 1:
                if (mDatas.size() != 0) {
                    mDatas.clear();
                }
                if (response != null) {
                    List<IncomeDetailBean> datas = response.getData();
                    boolean isZeroList = datas.size() == 0;
                    if (!isZeroList) {
                        mDatas.addAll(datas);
                        quickAdapter.notifyDataSetChanged();
                    }
                    mBinding.refreshLayout.finishRefresh();
                }
                break;
            case 2:
                if (response != null) {
                    List<IncomeDetailBean> datas = response.getData();
                    boolean isZeroList = datas.size() == 0;
                    if (!isZeroList) {
                        mDatas.addAll(datas);
                        quickAdapter.notifyDataSetChanged();
                    } else {
                        mBinding.refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
                mBinding.refreshLayout.finishLoadMore();
                break;
        }
    }


    /**
     * 初始化资产图标
     */
    private void initTitle() {

        if (mType.equals("Dividend")) {
            mBinding.assetsLogo.setImageResource(R.drawable.ic_eth);
            mBinding.title.setText(getString(R.string.cumulative_dividend));
            mBinding.titlebar.setTitle(getResources().getString(R.string.dividend_detail));
        }
        else  if (mType.equals("Mining")){
            mBinding.assetsLogo.setImageResource(R.drawable.ic_nest);
            mBinding.title.setText(getString(R.string.cumulative_mining));
            mBinding.titlebar.setTitle(getResources().getString(R.string.mining_detail));
        }

        mBinding.assetsAmount.setText(getIntent().getStringExtra("tokenValue"));

        mBinding.titlebar.setLeftDrawable(R.drawable.ic_back);
        mBinding.titlebar.showLeftDrawable();
        mBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                finish();
            }

            @Override
            public void rightTextClick() {

            }

            @Override
            public void rightImgClick() {

            }
        });
    }





    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        quickAdapter = new QuickAdapter<IncomeDetailBean>(mDatas) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_income_detail;
            }

            @Override
            public void convert(VH holder, IncomeDetailBean data, int position) {
                //交易时间
                String txTime = DateUtils.timedate(data.getTimeStamp());
                //交易值
                String txValue = "";

                holder.setText(R.id.trasaction_time, txTime.substring(0, txTime.length() - 3));
                txValue = Convert.fromWei(data.getValue(), Convert.Unit.ETHER).toString();
                holder.setText(R.id.trasaction_amount, txValue);
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        mBinding.rvAssetsDetaile.setLayoutManager(layoutManager);
        //设置Adapter
        mBinding.rvAssetsDetaile.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        mBinding.rvAssetsDetaile.setItemAnimator(new DefaultItemAnimator());
    }


    public  class AddressAndPage{
        private String toAddress;
        private String page;

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }

}

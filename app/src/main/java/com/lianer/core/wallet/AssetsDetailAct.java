package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Utils;
import com.lianer.core.R;
import com.lianer.core.config.Constant;
import com.lianer.core.custom.GlideRoundTransform;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityAssetsDetailBinding;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.wallet.bean.TokenProfileBean;
import com.lianer.core.wallet.bean.TxRecordBean;
import com.lianer.core.wallet.bean.TxRecordReponseBean;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 资产详情
 *
 * @author allison
 */
public class AssetsDetailAct extends BaseActivity {

    private ActivityAssetsDetailBinding assetsDetailBinding;
    private List<TxRecordBean> txRecordBeans = new ArrayList<>();
    private QuickAdapter quickAdapter;
    private String currentAddress;
    private int page = 1;
    private int freshState;//刷新状态  0:正常状态  1:下拉刷新  2:上拉加载
    private String walletAddress;//当前钱包地址

    @Override
    protected void initViews() {
        assetsDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_assets_detail);

        initTitleBar();
        initAssetsLogo();
        initClick();
        initRecyclerView();
        initRefreshView();
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.i("资产详情onResume");
        if (currentAddress == null) {
            currentAddress = getIntent().getStringExtra(Constants.ASSETS_ADDRESS);
        }
        initHttpData();
        initAssetsBalance();
    }

    /**
     * 初始化资产余额
     */
    private void initAssetsBalance() {
        if (assetsDetailBinding != null) {
            walletAddress = Constant.PREFIX_16 + HLWalletManager.shared().getCurrentWallet(this).getWalletFile().getAddress();
            Flowable.just(walletAddress)
                    .map(s -> {
                        String balance = null;
                        try {
                            if (getIntent().getStringExtra(Constants.ASSETS_NAME).equalsIgnoreCase(Constants.ASSETS_ETH)) {
                                String balanceValue = TransferUtil.getEthBanlance(TransferUtil.getWeb3j(), walletAddress);
                                balance = balanceValue;
                            } else {
                                String balanceValue = TransferUtil.getTokenBalance(TransferUtil.getWeb3j(), walletAddress, currentAddress);
                                balance = balanceValue;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return balance;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<String>(this, false) {
                        @Override
                        protected void success(String data) {
                            assetsDetailBinding.assetsAmount.setText(data);
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
                        }
                    });
        }
    }

    private void initRefreshView() {
        //下拉刷新
        assetsDetailBinding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            page = 1;
            freshState = 1;
            initHttpData();
        });
        //上拉加载
        assetsDetailBinding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            page++;
            freshState = 2;
            initHttpData();
        });
    }

    private void initHttpData() {
        int offset = 10;
        String walletAddress = Constant.PREFIX_16 + HLWalletManager.shared().getCurrentWallet(this).getWalletFile().getAddress();
        if (getIntent().getStringExtra(Constants.ASSETS_NAME).equalsIgnoreCase(Constants.ASSETS_ETH)) {
            HttpUtil.getTxRecord(currentAddress, Constants.API_KEY, page, offset)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<TxRecordReponseBean>(AssetsDetailAct.this, freshState == 0) {
                        @Override
                        protected void success(TxRecordReponseBean txRecordReponseBean) {
                            loadTxInfo(txRecordReponseBean);
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
                            errorDealwith();
                        }
                    });
        } else {
            HttpUtil.getTokenTxRecord(currentAddress, walletAddress, page, offset, Constants.API_KEY)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<TxRecordReponseBean>(AssetsDetailAct.this, freshState == 0) {
                        @Override
                        protected void success(TxRecordReponseBean txRecordReponseBean) {
                            loadTxInfo(txRecordReponseBean);
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
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
            assetsDetailBinding.ivRecordNull.setVisibility(View.VISIBLE);
            assetsDetailBinding.tvRecordNull.setVisibility(View.VISIBLE);
        } if (freshState == 1) {
            assetsDetailBinding.refreshLayout.finishRefresh();
        } else {
            assetsDetailBinding.refreshLayout.finishLoadMore();
        }
    }

    /**
     * 加载交易信息
     */
    private void loadTxInfo(TxRecordReponseBean txRecordReponseBean) {
        switch (freshState) {
            case 0:
                if (txRecordBeans.size() != 0) {
                    txRecordBeans.clear();
                }
                if (txRecordReponseBean != null) {
                    List<TxRecordBean> txRecordBeanList = txRecordReponseBean.getResult();
                    boolean isZeroList = txRecordBeanList.size() == 0;
                    assetsDetailBinding.ivRecordNull.setVisibility(isZeroList ? View.VISIBLE : View.GONE);
                    assetsDetailBinding.tvRecordNull.setVisibility(isZeroList ? View.VISIBLE : View.GONE);
                    assetsDetailBinding.refreshLayout.setVisibility(isZeroList ? View.GONE : View.VISIBLE);
                    if (!isZeroList) {
                        txRecordBeans.addAll(txRecordBeanList);
                        quickAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case 1:
                if (txRecordBeans.size() != 0) {
                    txRecordBeans.clear();
                }
                if (txRecordReponseBean != null) {
                    List<TxRecordBean> txRecordBeanList = txRecordReponseBean.getResult();
                    boolean isZeroList = txRecordBeanList.size() == 0;
                    if (!isZeroList) {
                        txRecordBeans.addAll(txRecordBeanList);
                        quickAdapter.notifyDataSetChanged();
                    }
                    assetsDetailBinding.refreshLayout.finishRefresh();
                }
                break;
            case 2:
                if (txRecordReponseBean != null) {
                    List<TxRecordBean> txRecordBeanList = txRecordReponseBean.getResult();
                    boolean isZeroList = txRecordBeanList.size() == 0;
                    if (!isZeroList) {
                        txRecordBeans.addAll(txRecordBeanList);
                        quickAdapter.notifyDataSetChanged();
                    } else {
                        assetsDetailBinding.refreshLayout.finishLoadMoreWithNoMoreData();
                    }
                }
                assetsDetailBinding.refreshLayout.finishLoadMore();
                break;
        }
    }


    /**
     * 初始化资产图标
     */
    private void initAssetsLogo() {
        Intent intent = getIntent();
        if (intent.getStringExtra(Constants.ASSETS_LOGO).equalsIgnoreCase(Constants.ASSETS_ETH)) {
            assetsDetailBinding.assetsLogo.setImageResource(R.drawable.ic_eth);
        }
        else {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_default_eth)
                    .transform(new GlideRoundTransform(AssetsDetailAct.this, (int) getResources().getDimension(R.dimen.dp_20)));
            Glide.with(AssetsDetailAct.this)
                    .load(Constants.ASSET_IMAGE_ADDRESS + intent.getStringExtra(Constants.ASSETS_LOGO) + Constants.PNG)
                    .apply(options)
                    .into(assetsDetailBinding.assetsLogo);
        }

//        else if (intent.getStringExtra(Constants.ASSETS_LOGO).equalsIgnoreCase(Constants.ASSETS_NEST_ADDRESS)) {
//            assetsDetailBinding.assetsLogo.setImageResource(R.drawable.ic_nest);
//        }

        assetsDetailBinding.assetsName.setText(intent.getStringExtra(Constants.ASSETS_NAME));
    }

    /**
     * 初始化点击事件
     */
    private void initClick() {
        //转出
        assetsDetailBinding.turnOut.setOnClickListener(v -> {
            Intent intent = new Intent(this, TransferActivity.class);
            intent.putExtra(Constants.ASSETS_NAME, getIntent().getStringExtra(Constants.ASSETS_NAME));
            intent.putExtra(Constants.ASSETS_ADDRESS, getIntent().getStringExtra(Constants.ASSETS_ADDRESS));
            startActivity(intent);
        });
        //转入
        assetsDetailBinding.switchTo.setOnClickListener(v -> startActivity(new Intent(this, WalletAddressActivity.class)));
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        assetsDetailBinding.titlebar.setLeftDrawable(R.drawable.ic_back);
        assetsDetailBinding.titlebar.showLeftDrawable();
        assetsDetailBinding.titlebar.setTitle(getResources().getString(R.string.assets_detail));
        assetsDetailBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
        quickAdapter = new QuickAdapter<TxRecordBean>(txRecordBeans) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_assets_detail;
            }

            @Override
            public void convert(VH holder, TxRecordBean data, int position) {
                //交易时间
                String txTime = DateUtils.timedate(data.getTimeStamp());
                //交易类型 转出/转入
                String txType;
                //交易值
                String txValue = "";
                //token logo
                String  symbol = "";

                holder.setText(R.id.trasaction_time, txTime.substring(0, txTime.length() - 3));

                String fromAddress = data.getFrom();
                if (getIntent().getStringExtra(Constants.ASSETS_NAME).equalsIgnoreCase(Constants.ASSETS_ETH)) {//eth交易记录
//                    txValue = Convert.fromWei(data.getValue(), Convert.Unit.ETHER).toString();
                    txValue = Convert.fromWei(data.getValue(), Convert.Unit.ETHER).setScale(4,BigDecimal.ROUND_DOWN).toString();
                    if (fromAddress.equals(currentAddress)) {
                        txType = setTurnOutData(holder, txValue, data);
                    } else {
                        txType = setSwitchToData(holder, txValue, data);
                    }
                    symbol = "ETH";
                } else {//代币交易记录
                    for (TokenProfileBean tokenProfileBean : Constants.tokenProfileBeans) {
                        if (tokenProfileBean.getAddress().equalsIgnoreCase(currentAddress)) {
                            symbol = tokenProfileBean.getSymbol();
                            int decimals = Integer.valueOf(tokenProfileBean.getDecimals());
//                            txValue = Utils.roundByScale(Double.valueOf(data.getValue()) / Math.pow(10, decimals), decimals);
                            txValue = new BigDecimal(data.getValue()).divide(new BigDecimal("10").pow(decimals)).toString();
//                            txValue = Utils.switchTo4Decimal(txValue);
                        }
                    }
                    if (fromAddress.equals(walletAddress)) {
                        txType = setTurnOutData(holder,  Utils.switchTo4Decimal(txValue), data);
                    } else {
                        txType = setSwitchToData(holder,  Utils.switchTo4Decimal(txValue), data);
                    }
                }
                holder.setText(R.id.trasaction_status, txType);

                //item点击事件
                String finalTxType = txType;
                String finalSymbol = symbol;
                String finalTxValue = txValue;
                holder.getView(R.id.rl_assets_detail).setOnClickListener(v -> {
                    Intent intent = new Intent(AssetsDetailAct.this, TxRecordDetailAct.class);
                    intent.putExtra("txDetail", data);
                    intent.putExtra("txValue", finalTxValue);
                    intent.putExtra("txType", finalTxType);
                    intent.putExtra("txTime", txTime);
                    intent.putExtra("navigateType", 0);
                    intent.putExtra("symbol", finalSymbol);
                    double actualPayGas = Double.valueOf(data.getGasUsed())
                            * Double.valueOf(data.getGasPrice());
                    String tempValue = String.valueOf(Convert.fromWei(String.valueOf(actualPayGas), Convert.Unit.ETHER));
                    intent.putExtra("txGas", tempValue);
                    startActivity(intent);
                });

            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        assetsDetailBinding.rvAssetsDetaile.setLayoutManager(layoutManager);
        //设置Adapter
        assetsDetailBinding.rvAssetsDetaile.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        assetsDetailBinding.rvAssetsDetaile.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 设置转出信息
     * @param holder
     * @param txValue
     * @param data
     * @return
     */
    private String setTurnOutData(QuickAdapter.VH holder, String txValue, TxRecordBean data) {
        holder.setText(R.id.trasaction_amount, getString(R.string.sub) + txValue);
        holder.setText(R.id.trasaction_address, data.getHash());
        holder.setTextColor(R.id.trasaction_amount, getResources().getColor(R.color.c4));
        return getResources().getString(R.string.turn_out);
    }

    private String setSwitchToData(QuickAdapter.VH holder, String txValue, TxRecordBean data) {
        holder.setText(R.id.trasaction_amount, getString(R.string.add) + txValue);
        holder.setText(R.id.trasaction_address, data.getHash());

        //TODO 下个版本迭代 转入分分红和挖矿
//        if (true) {//分红
//            holder.setTextColor(R.id.trasaction_amount, getResources().getColor(R.color.c5));
//            //显示分红图标
//            holder.getView(R.id.transaction_reward).setVisibility(View.VISIBLE);
//        } else if (true){
//            holder.setTextColor(R.id.trasaction_amount, getResources().getColor(R.color.c5));
//            holder.getView(R.id.transaction_reward).setVisibility(View.VISIBLE);
//            //显示挖矿图标
//
//        }
        if (true) {
            holder.getView(R.id.transaction_reward).setVisibility(View.GONE);
            holder.setTextColor(R.id.trasaction_amount, getResources().getColor(R.color.c5));
        }
        return getResources().getString(R.string.switch_to);
    }

}

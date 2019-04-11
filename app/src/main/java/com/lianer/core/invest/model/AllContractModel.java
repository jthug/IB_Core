package com.lianer.core.invest.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianer.common.base.QuickAdapter;
import com.lianer.core.R;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.ContractDetailAct;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.contract.bean.TokenMortgageBean;
import com.lianer.core.custom.RVItemDecoration;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.utils.CommomUtil;

import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;

/**
 * 全部合约逻辑处理类
 *
 * @author allison
 */
public class AllContractModel {


    Context mContext;

    QuickAdapter statusAdapter, typeAdapter;
    List<String> contractStatusList = new ArrayList<>();
    List<TokenMortgageBean> contractTypeList = new ArrayList<>();
    List<String> statusSelectedFilter = new ArrayList<>();//合约状态
    List<String> typeSelectedFilter = new ArrayList<>();//抵押资产类型

    public AllContractModel(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 初始化合约状态筛选列表
     */
    public void initContractStatusRecyclerView(RecyclerView statusRecyclerView) {
        contractStatusList.addAll(ContractStatus.CONTRACT_STATUS_FILTER);
        statusAdapter = new QuickAdapter<String>(contractStatusList) {


            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_all_contract_filter;
            }

            @Override
            public void convert(VH holder, String data, int position) {
                TextView filterCondition = holder.getView(R.id.contract_filter_condition);
                filterCondition.setText(Arrays.asList(mContext.getResources().getStringArray(R.array.contract_status)).get(Integer.parseInt(data)));

                //已选筛选条件改变颜色为蓝色,否则为灰色
                filterCondition.setSelected(false);
                for (String str : statusSelectedFilter) {
                    filterCondition.setSelected(data.equals(str));
                }

                filterCondition.setOnClickListener(v -> {
                    if (statusSelectedFilter.contains(data)) {
                        statusSelectedFilter.remove(data);
                        filterCondition.setSelected(false);
                    } else {
                        statusSelectedFilter.add(data);
                        filterCondition.setSelected(true);
                    }
                });
            }
        };

        GridLayoutManager layoutManager2 = new GridLayoutManager(mContext, 2);
        statusRecyclerView.setLayoutManager(layoutManager2);
        statusRecyclerView.setAdapter(statusAdapter);
        statusRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化抵押资产类型筛选列表
     */
    public void initAssetsTypeRecyclerView(RecyclerView typeRecyclerView) {
        contractTypeList.addAll(MortgageAssets.getTokenMortgageList(mContext));

        typeAdapter = new QuickAdapter<TokenMortgageBean>(contractTypeList) {


            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_all_contract_filter;
            }

            @Override
            public void convert(VH holder, TokenMortgageBean data, int position) {
                TextView filterCondition = holder.getView(R.id.contract_filter_condition);

                String tokenAddress = data.getTokenAddress();

                //已选筛选条件改变颜色为蓝色,否则为灰色
                filterCondition.setSelected(false);
                for (String str : typeSelectedFilter) {
                    filterCondition.setSelected(tokenAddress.equals(str));
                }

                filterCondition.setText(data.getTokenAbbreviation());

                filterCondition.setOnClickListener(v -> {
                    if (typeSelectedFilter.contains(tokenAddress)) {
                        typeSelectedFilter.remove(tokenAddress);
                        filterCondition.setSelected(false);
                    } else {
                        typeSelectedFilter.add(tokenAddress);
                        filterCondition.setSelected(true);
                    }
                });
            }

        };

        GridLayoutManager layoutManager2 = new GridLayoutManager(mContext, 2);
        typeRecyclerView.setLayoutManager(layoutManager2);
        typeRecyclerView.setAdapter(typeAdapter);
        typeRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public List<String> getStatusSelectedFilter() {
        return statusSelectedFilter;
    }

    public List<String> getTypeSelectedFilter() {
        return typeSelectedFilter;
    }

    /**
     * 重置筛选
     */
    public void resetFilterData() {
        if (statusSelectedFilter.size() != 0) {
            statusSelectedFilter.clear();
            statusAdapter.notifyDataSetChanged();
        }
        if (typeSelectedFilter.size() != 0) {
            typeSelectedFilter.clear();
            typeAdapter.notifyDataSetChanged();
        }
    }

    QuickAdapter<ContractResponse> quickAdapter;

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView(RecyclerView contractRecyclerView, List<ContractResponse> dataList) {
        quickAdapter = new QuickAdapter<ContractResponse>(dataList) {


            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_all_contract;
            }

            @Override
            public void convert(VH holder, ContractResponse data, int position) {
                holder.setText(R.id.investment_amount, Convert.fromWei(data.getInvestmentAmount(), Convert.Unit.ETHER).toString());
                holder.setText(R.id.time_limit, data.getTimeLimit() + "");

                double interestRate = data.getInterestRate()/10d;
                String interestRateStr = String.valueOf(interestRate);
                holder.setText(R.id.interest_rate, interestRate >= 1 ? interestRateStr.substring(0,interestRateStr.indexOf(".")) : interestRateStr);

                String mortageAssetsAmount = Convert.fromWei(data.getMortgageAssetsAmount(), Convert.Unit.ETHER).toString();
                if (mortageAssetsAmount.contains(".")) {
                    mortageAssetsAmount = mortageAssetsAmount.substring(0, mortageAssetsAmount.indexOf("."));
                }
                holder.setText(R.id.mortgage_assets_amount, MortgageAssets.getTokenSymbol(mContext,data.getMortgageAssetsType()) + " " + mortageAssetsAmount);

                String priecToEth = Convert.fromWei(data.getPriceToEth(), Convert.Unit.ETHER).toString();
                if (priecToEth.contains(".")) {
                    priecToEth = priecToEth.substring(0, priecToEth.indexOf("."));
                }
                holder.setText(R.id.price_to_eth, priecToEth + " ETH");

                //item的点击事件
                holder.getView(R.id.rl_all_contract).setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, ContractDetailAct.class);
                    intent.putExtra(CONTRACT_RESPONSE, data);
                    mContext.startActivity(intent);
                });

                //状态图片显示
                String status = CommomUtil.judgeContractStatus(data.getContractStatus());
                if (TextUtils.isEmpty(status)) {
                    return;
                }
                switch (status) {
                    case ContractStatus.RELEASED://我的 and 可投资
                        String borrowAddress = data.getBorrowAddress();
                        if (HLWalletManager.shared().getCurrentWallet(mContext) == null) {
                            holder.setImage(R.id.contract_status, R.drawable.contract_invest);
                        } else {
                            if (borrowAddress.equals(HLWalletManager.shared().getCurrentWallet(mContext).getAddress())) {//我的
                                holder.setImage(R.id.contract_status, R.drawable.contract_mine);
                            } else {//可投资
                                holder.setImage(R.id.contract_status, R.drawable.contract_invest);
                            }
                        }

                        break;
                    case ContractStatus.OVERDUE://已逾期
                        holder.setImage(R.id.contract_status, R.drawable.contract_overdue);
                        break;
                    case ContractStatus.EFFECTED://已生效
                        holder.setImage(R.id.contract_status, R.drawable.contract_effect);
                        break;
                    case ContractStatus.REPAYMENT://已还款
                        holder.setImage(R.id.contract_status, R.drawable.contract_repayment);
                        break;
                    case ContractStatus.RECAPTURED://已取回
                        holder.setImage(R.id.contract_status, R.drawable.contract_take_back);
                        break;
                }
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        //设置布局管理器
        contractRecyclerView.setLayoutManager(layoutManager);
        contractRecyclerView.addItemDecoration(new RVItemDecoration(LinearLayout.VERTICAL, 30));
        //设置Adapter
        contractRecyclerView.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        contractRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public QuickAdapter<ContractResponse> getQuickAdapter() {
        return quickAdapter;
    }
}

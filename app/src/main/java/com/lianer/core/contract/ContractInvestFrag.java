package com.lianer.core.contract;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseFragment;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Utils;
import com.lianer.core.R;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractInvestRequest;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.contract.model.ContractInvestAndBorrowModel;
import com.lianer.core.custom.RVItemDecoration;
import com.lianer.core.custom.ShadowImageView;
import com.lianer.core.lauch.MainAct;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.HttpUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的合约--投资
 *
 * @author allison
 */
public class ContractInvestFrag extends BaseFragment {

    View mView;
    QuickAdapter quickAdapter;
    List<ContractResponse> contractResponseList = new ArrayList<>();
    int offset = 0;//偏移量
    String borrowAddress;
    int freshState;//刷新状态
    SmartRefreshLayout refreshLayout;
    List<String> stateList = new ArrayList<>();
    RecyclerView recyclerView;
    ContractInvestAndBorrowModel contractInvestAndBorrowModel;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            if (quickAdapter != null) {
                initInvestData();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initInvestData();
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_contract_invest, null);
        return mView;
    }

    @Override
    protected void initData() {
        initRecyclerView();
        initRefresh();

//        initInvestData();
    }


    /**
     * 初始化投资数据
     */
    private void initInvestData() {
        if (stateList.size() != 0) {
            stateList.clear();
        }
        stateList.addAll(ContractStatus.CONTRACT_INVEST);
        initResponseData(initRequestBody());
    }

    /**
     * 获取响应数据
     */
    private void initResponseData(String jsonRequestParams) {
        KLog.i(jsonRequestParams);
        HttpUtil.getContractList(jsonRequestParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<List<ContractResponse>>() {
                    @Override
                    protected void success(List<ContractResponse> investResponses) {
                        switch (freshState) {
                            case 0:
                                if (contractResponseList.size() != 0) {
                                    contractResponseList.clear();
                                }
                                if (investResponses != null && investResponses.size() != 0) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    contractResponseList.addAll(investResponses);
                                } else {
                                    showEmptyView();
                                }
                                quickAdapter.notifyDataSetChanged();
                                break;
                            case 1:
                                if (contractResponseList.size() != 0) {
                                    contractResponseList.clear();
                                }
                                refreshLayout.finishRefresh();
                                if (investResponses != null && investResponses.size() != 0) {
                                    contractResponseList.addAll(investResponses);
                                } else {
                                    showEmptyView();
                                }
                                quickAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                if (investResponses != null && investResponses.size() != 0) {
                                    contractResponseList.addAll(investResponses);
                                    quickAdapter.notifyDataSetChanged();
                                    refreshLayout.finishLoadMore();
                                } else {
                                    refreshLayout.finishLoadMore();
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                break;
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.i(error.getMessage());
                        showEmptyView();
                    }
                });
    }

    /**
     * 显示记录为空的视图
     */
    private void showEmptyView() {
        LinearLayout contractNull = mView.findViewById(R.id.ll_contract_null);
        contractNull.setVisibility(View.VISIBLE);
        ImageView nullPiture = mView.findViewById(R.id.contract_state_picture);
        nullPiture.setImageResource(R.drawable.contract_null);
        TextView nullWarn = mView.findViewById(R.id.contract_state_warn);
        nullWarn.setText(getString(R.string.no_invest_contract));
//        Button contractBtn = mView.findViewById(R.id.contract_btn);
//        contractBtn.setText(getString(R.string.go_invest));
//        contractBtn.setOnClickListener(v -> navigateToInvest());
    }

    /**
     * 切换到到投资tab页面
     */
    private void navigateToInvest() {
        MainAct mainAct = (MainAct) mActivity;
        mainAct.changeTabStatus(0);
    }

    /**
     * 加载请求参数
     */
    private String initRequestBody() {
        int count = 20;
        borrowAddress = HLWalletManager.shared().getCurrentWallet(mActivity).getAddress();
        ContractInvestRequest contractInvestRequest = new ContractInvestRequest();
        contractInvestRequest.setContractStatusList(stateList);
        contractInvestRequest.setCount(count);
        contractInvestRequest.setOffset(offset);
        contractInvestRequest.setInvestmentAddress(borrowAddress);
        Gson gson = new Gson();
        return gson.toJson(contractInvestRequest);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        recyclerView = mView.findViewById(R.id.recyclerView);
        contractInvestAndBorrowModel = new ContractInvestAndBorrowModel(getActivity());
        quickAdapter = new QuickAdapter<ContractResponse>(contractResponseList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_contract;
            }

            @Override
            public void convert(VH holder, ContractResponse data, int position) {
                //合约地址
                holder.setText(R.id.contract_address, data.getContractAddress());
                holder.getView(R.id.contract_address).setOnClickListener(v -> CommomUtil.navigateToEthScan(getContext(), true, data.getContractAddress()));

                //判断是否是已取回状态
                String contractStatus = data.getContractStatus();
                if (contractStatus.equals(ContractStatus.RECAPTURED)) {
                    ShadowImageView shadowImageView = holder.getView(R.id.contract_logo);

//                    Glide.with(mActivity).load(MortgageAssets.getTokenLogo(mActivity, data.getMortgageAssetsType())).into( shadowImageView);
                    holder.setText(R.id.contract_type, getString(R.string.mortgaged_assets));

                    holder.setText(R.id.contract_value, Convert.fromWei(data.getMortgageAssetsAmount(), Convert.Unit.ETHER).toString() + " " + MortgageAssets.getTokenSymbol(getContext(),data.getMortgageAssetsType()));
                } else {
                    ShadowImageView shadowImageView = holder.getView(R.id.contract_logo);
                    shadowImageView.setImageResource(R.drawable.ic_eth);
                    holder.setText(R.id.contract_type, getString(R.string.daoqibenxi));
                    try {
                        holder.setText(R.id.contract_value, Utils.switchTo4Decimal(Convert.fromWei(data.getPrincipalAndInterest(), Convert.Unit.ETHER).toString()) + " ETH");
                    } catch (NumberFormatException e) {
                        KLog.w("到期本息 值异常");
                    }

                }

                String status = CommomUtil.judgeContractStatus(contractStatus);

                if (!TextUtils.isEmpty(status)) {
                    //状态显示值颜色
                    switch (status) {
                        case ContractStatus.EFFECTED://已生效/已投资
                            //状态显示值
                            holder.setText(R.id.contract_state, getString(R.string.invested));
                            //状态颜色
                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_059EFF));
                            //时间
                            holder.setText(R.id.contract_time, getString(R.string.expire_date) + "  " + DateUtils.timesTwo(data.getContractExpireDate()));
                            break;
                        case ContractStatus.REPAYMENT://已还款
                            holder.setText(R.id.contract_state, getString(R.string.repaid));
                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_111111));
                            holder.setText(R.id.contract_time, getString(R.string.repayment_time) + "  " + DateUtils.timesTwo(data.getContractRepaymentDate()));
                            break;
                        case ContractStatus.OVERDUE://已逾期
                            holder.setText(R.id.contract_state, getString(R.string.overdue));
                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_F5222D));
                            holder.setText(R.id.contract_time, getString(R.string.expire_date) + "  " + DateUtils.timesTwo(data.getContractExpireDate()));
                            break;
                        case ContractStatus.RECAPTURED://已取回
                            holder.setText(R.id.contract_state, getString(R.string.take_back));
                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_111111));
                            holder.setText(R.id.contract_time, getString(R.string.take_back_time) + "  " + DateUtils.timesTwo(data.getContractTakebackDate()));
                            break;
                    }
                }

                //设置item点击事件
                holder.getView(R.id.rl_assets_detail).setOnClickListener(v -> {
                    Intent intent = new Intent(mActivity, ContractDetailAct.class);
                    intent.putExtra(Constants.CONTRACT_RESPONSE, contractResponseList.get(position));
                    intent.putExtra("launchFlag", 1);
                    startActivity(intent);
                });

                contractInvestAndBorrowModel.checkContractData(data, position, (contractResponse, position1) -> {
                    if (contractResponseList.size() != 0) {
                        contractResponseList.get(position1).setContractStatus(contractResponse.getContractStatus());
                        quickAdapter.notifyItemChanged(position1);
                    }
                });

            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置Adapter
        recyclerView.setAdapter(quickAdapter);
        recyclerView.addItemDecoration(new RVItemDecoration(LinearLayoutManager.VERTICAL, 30));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化刷新组件
     */
    private void initRefresh() {
        refreshLayout = mView.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            offset = 0;
            freshState = 1;
            initResponseData(initRequestBody());
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            offset = contractResponseList.size();
            freshState = 2;
            initResponseData(initRequestBody());
        });
    }

}

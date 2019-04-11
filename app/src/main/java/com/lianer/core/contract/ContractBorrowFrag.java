package com.lianer.core.contract;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
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
import com.lianer.common.utils.Utils;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseFragment;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractBorrowRequest;
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
import com.lianer.core.utils.TransferUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的合约--借币
 *
 * @author allison
 */
public class ContractBorrowFrag extends BaseFragment {

    View mView;
    QuickAdapter quickAdapter;
    List<ContractResponse> dataList = new ArrayList<>();
    String borrowAddress;
    int offset;//偏移量
    int freshState;
    SmartRefreshLayout refreshLayout;
    List<String> stateList = new ArrayList<>();
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
        mView = inflater.inflate(R.layout.fragment_contract_borrow, null);
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
        stateList.addAll(ContractStatus.CONTRACT_BORROW);
        initResponseData(initRequestBody());
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
                    protected void success(List<ContractResponse> borrowResponse) {
                        switch (freshState) {
                            case 0:
                                if (dataList.size() != 0) {
                                    dataList.clear();
                                }
                                if (borrowResponse != null && borrowResponse.size() != 0) {
                                    refreshLayout.setVisibility(View.VISIBLE);
                                    dataList.addAll(borrowResponse);
                                } else {
                                    showEmptyView();
                                }
                                quickAdapter.notifyDataSetChanged();
                                break;
                            case 1:
                                if (dataList.size() != 0) {
                                    dataList.clear();
                                }
                                refreshLayout.finishRefresh();
                                if (borrowResponse != null && borrowResponse.size() != 0) {
                                    dataList.addAll(borrowResponse);
                                    quickAdapter.notifyDataSetChanged();
                                } else {
                                    showEmptyView();
                                }
                                quickAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                if (borrowResponse != null && borrowResponse.size() != 0) {
                                    dataList.addAll(borrowResponse);
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
        nullWarn.setText(getString(R.string.no_borrow_contract));
////        Button contractBtn = mView.findViewById(R.id.contract_btn);
//        contractBtn.setText(R.string.go_borrow);
//        contractBtn.setOnClickListener(v -> navigateToBorrow());
    }

    /**
     * 跳转到借币页面
     */
    private void navigateToBorrow() {
        MainAct mainAct = (MainAct) mActivity;
        mainAct.changeTabStatus(1);
    }

    /**
     * 加载请求参数
     */
    private String initRequestBody() {
        int count = 20;
        borrowAddress = HLWalletManager.shared().getCurrentWallet(mActivity).getAddress();
        ContractBorrowRequest contractInvestRequest = new ContractBorrowRequest();
        contractInvestRequest.setContractStatusList(stateList);
        contractInvestRequest.setCount(count);
        contractInvestRequest.setOffset(offset);
        contractInvestRequest.setBorrowAddress(borrowAddress);
        Gson gson = new Gson();
        return gson.toJson(contractInvestRequest);
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        RecyclerView recyclerView = mView.findViewById(R.id.recyclerView);
        contractInvestAndBorrowModel = new ContractInvestAndBorrowModel(getActivity());
        quickAdapter = new QuickAdapter<ContractResponse>(dataList) {


            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_contract;
            }

            @SuppressLint("CheckResult")
            @Override
            public void convert(VH holder, ContractResponse data, int position) {
                //合约地址
                holder.setText(R.id.contract_address, data.getContractAddress());
                holder.getView(R.id.contract_address).setOnClickListener(v -> CommomUtil.navigateToEthScan(getContext(), true, data.getContractAddress()));

                String status = CommomUtil.judgeContractStatus(data.getContractStatus());

                ShadowImageView shadowImageView = holder.getView(R.id.contract_logo);
                shadowImageView.setImageResource(R.drawable.ic_eth);

                if (!TextUtils.isEmpty(status)) {
                    //合约动态类型
                    switch (status) {
                        case ContractStatus.TERMINATED://已终止
                            //设置不可点击
                            holder.getView(R.id.rl_assets_detail).setEnabled(false);

                            //背景色
                            holder.setItemBackgroundColor(R.id.rl_assets_detail, Color.parseColor("#FAFAFA"));
                            //合约动态类型
                            holder.setText(R.id.contract_type, getString(R.string.borrow));
//                            holder.setText(R.id.contract_value, Utils.switchToDecimal(Convert.fromWei(data.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString(), "0.00") + " ETH");
                            //静止状态
                            holder.setText(R.id.contract_state, getString(R.string.terminated));
                            //时间
                            holder.setText(R.id.contract_time, getString(R.string.terminated_time) + DateUtils.timesTwo(data.getContractTerminationDate()));

                            //字体颜色
                            holder.setTextColor(R.id.contract_state, Color.parseColor("#8C8C8C"));
                            holder.setTextColor(R.id.contract_type, Color.parseColor("#8C8C8C"));
                            holder.setTextColor(R.id.contract_value, Color.parseColor("#8C8C8C"));
                            break;
                        case ContractStatus.RELEASED://已发布
                            holder.getView(R.id.rl_assets_detail).setEnabled(true);

                            holder.setText(R.id.contract_type, getString(R.string.daoqibenxi));
                            holder.setText(R.id.contract_state, getString(R.string.release));
                            holder.setText(R.id.contract_time, getString(R.string.create_time) + " " + DateUtils.timesTwo(data.getContractCreateDate()));
                            if (!TextUtils.isEmpty(data.getPrincipalAndInterest())) {
                                holder.setText(R.id.contract_value, Convert.fromWei(data.getPrincipalAndInterest(), Convert.Unit.ETHER).toString() + " ETH");
                            }

                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_059EFF));
                            break;
                        case ContractStatus.DISBANDED://已解散
                            holder.getView(R.id.rl_assets_detail).setEnabled(false);

                            holder.setItemBackgroundColor(R.id.rl_assets_detail, Color.parseColor("#FAFAFA"));
                            holder.setText(R.id.contract_type, getString(R.string.take_back));
                            holder.setText(R.id.contract_value, Utils.switchTo4Decimal(Convert.fromWei(data.getMortgageAssetsAmount(), Convert.Unit.ETHER).toString()) + " " + MortgageAssets.getTokenSymbol(getContext(),data.getMortgageAssetsType()));
                            holder.setText(R.id.contract_state, getString(R.string.disbanded));
                            holder.setText(R.id.contract_time, getString(R.string.disband_time) + " " + DateUtils.timesTwo(data.getContractDissolutionDate()));

                            holder.setTextColor(R.id.contract_state, Color.parseColor("#8C8C8C"));
                            holder.setTextColor(R.id.contract_type, Color.parseColor("#8C8C8C"));
                            holder.setTextColor(R.id.contract_value, Color.parseColor("#8C8C8C"));
                            break;
                        case ContractStatus.EFFECTED://已生效
                            holder.getView(R.id.rl_assets_detail).setEnabled(true);

                            if (data.getContractStatus().equals(ContractStatus.CONTRACT_STSTUS_17)) {//当前状态为合约即将到期
                                holder.setText(R.id.contract_type, getString(R.string.daoqibenxi));
                                holder.setText(R.id.contract_state, getString(R.string.fast_overdue));
                                holder.setText(R.id.contract_time, getString(R.string.create_time) + " " + DateUtils.timesTwo(data.getContractCreateDate()));
                                holder.setText(R.id.contract_value, Convert.fromWei(data.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString() + " ETH");
                                holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_F5222D));
                                //显示左边红色图片
                                holder.getView(R.id.red_vertical_line).setVisibility(View.VISIBLE);
                            } else {
                                //判断状态逾期状态
                                try {
                                    IBContractUtil.readOnlyIBTUSDContract(TransferUtil.getWeb3j(),
                                            HLWalletManager.shared().getCurrentWallet(getContext()).getAddress(), data.getContractAddress())
                                            .map(ibContract -> {
                                                if (ibContract != null) {
//                                                    return ibContract.expireState().sendAsync().get();
                                                }
                                                return BigInteger.valueOf(0);
                                            }).subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(bigInteger -> {
                                                switch (bigInteger.toString()) {
                                                    case "0"://未逾期 0 显示已生效状态
                                                        holder.setText(R.id.contract_type, getString(R.string.wait_repayment));
                                                        holder.setText(R.id.contract_state, getString(R.string.take_effect));
                                                        holder.setText(R.id.contract_time, getString(R.string.expire_time) + " " + DateUtils.timesTwo(data.getContractExpireDate()));
                                                        holder.setText(R.id.contract_value, Convert.fromWei(data.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString() + " ETH");
                                                        holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_059EFF));
                                                        break;
                                                    case "4"://即将逾期 4
                                                        data.setContractStatus(ContractStatus.CONTRACT_STSTUS_17);
                                                        data.setContractCreateDate(null);
                                                        HttpUtil.updateContractState(new Gson().toJson(data))//快逾期，更新服务器合约状态
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(new DisposableObserver<String>() {
                                                                    @Override
                                                                    public void onNext(String s) {
                                                                        KLog.i("快逾期，状态更新成功");
                                                                        //刷新数据
                                                                        data.setContractStatus(ContractStatus.CONTRACT_STSTUS_17);
                                                                        quickAdapter.notifyItemChanged(position);
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {
                                                                        KLog.i("快逾期，状态更新失败");
                                                                    }

                                                                    @Override
                                                                    public void onComplete() {
                                                                        KLog.i("快逾期，状态更新错误");
                                                                    }
                                                                });
                                                        break;
                                                }
                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case ContractStatus.REPAYMENT://已还款
                            //设置不可点击
                            holder.getView(R.id.rl_assets_detail).setEnabled(true);

                            holder.setText(R.id.contract_type, getString(R.string.repaid));
                            holder.setText(R.id.contract_state, getString(R.string.repaid));
                            holder.setText(R.id.contract_time, getString(R.string.repayment_time) + " " + DateUtils.timesTwo(data.getContractRepaymentDate()));
                            holder.setText(R.id.contract_value, Convert.fromWei(data.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString() + " ETH");
                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_111111));
                            break;
                        case ContractStatus.OVERDUE://已逾期
                            //设置不可点击
                            holder.getView(R.id.rl_assets_detail).setEnabled(true);

                            holder.setText(R.id.contract_type, getString(R.string.wait_repayment));
                            holder.setText(R.id.contract_state, getString(R.string.overdue));
                            holder.setText(R.id.contract_time, getString(R.string.expire_date) + " " + DateUtils.timesTwo(data.getContractExpireDate()));
                            holder.setText(R.id.contract_value, Convert.fromWei(data.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString() + " ETH");
                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_F5222D));
                            break;
                        case ContractStatus.RECAPTURED://已取回
                            //设置不可点击
                            holder.getView(R.id.rl_assets_detail).setEnabled(true);

                            holder.setText(R.id.contract_type, getString(R.string.already_released));
                            holder.setText(R.id.contract_state, getString(R.string.already_released));
                            holder.setText(R.id.contract_time, getString(R.string.already_released_time) + " " + DateUtils.timesTwo(data.getContractTakebackDate()));
                            if (!TextUtils.isEmpty(data.getPrincipalAndInterest())) {
                                holder.setText(R.id.contract_value, Convert.fromWei(data.getPrincipalAndInterest(), Convert.Unit.ETHER).toString() + " " + MortgageAssets.getTokenSymbol(getContext(),data.getMortgageAssetsType()));
                            }

                            holder.setTextColor(R.id.contract_state, getResources().getColor(R.color.clr_111111));
                            break;
                    }
                }

                //设置item点击事件
                holder.getView(R.id.rl_assets_detail).setOnClickListener(v -> {
                    Intent intent = new Intent(mActivity, ContractDetailAct.class);
                    intent.putExtra(Constants.CONTRACT_RESPONSE, dataList.get(position));
                    intent.putExtra("launchFlag", 1);
                    startActivity(intent);
                });


                contractInvestAndBorrowModel.checkContractData(data, position, (contractResponse, position1) -> {
                    if (dataList.size() != 0) {
                        dataList.get(position1).setContractStatus(contractResponse.getContractStatus());
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
            offset = dataList.size();
            freshState = 2;
            initResponseData(initRequestBody());
        });
        refreshLayout.setEnableAutoLoadMore(false);
    }

}

package com.lianer.core.contract.model;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Singleton;
import com.lianer.core.R;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.databinding.ActivityContractOperateBinding;
import com.lianer.core.dialog.DeleteDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.ShareUtil;
import com.lianer.core.utils.SnackbarUtil;

import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.config.ContractStatus.CONTRACT_STATUS_END;
import static com.lianer.core.config.ContractStatus.CONTRACT_STATUS_OVERDUE;
import static com.lianer.core.config.ContractStatus.CONTRACT_STATUS_REPAID;
import static com.lianer.core.config.ContractStatus.CONTRACT_STATUS_WAIT_INVEST;
import static com.lianer.core.config.ContractStatus.CONTRACT_STATUS_WAIT_MORTGAGE;
import static com.lianer.core.config.ContractStatus.CONTRACT_STATUS_WAIT_REPAYMENT;

/**
 * 合约操作逻辑处理（删除和导出）
 */
public class ContractOperateModel {


    private Context mContext;
    public QuickAdapter quickAdapter;
    private int offset, freshState;
    private List<ContractBean> mDataList, mSelectedDataList = new ArrayList<>();
    private List<Boolean> mSelectedList = new ArrayList<>();
    private ActivityContractOperateBinding mContractOperateBinding;
    private String mOperateType;//合约操作类型


    public ContractOperateModel(Context mContext, List<ContractBean> dataList, ActivityContractOperateBinding mContractOperateBinding, String operateType) {
        this.mContext = mContext;
        this.mDataList = dataList;
        this.mContractOperateBinding = mContractOperateBinding;
        this.mOperateType = operateType;
    }

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView() {
        quickAdapter = new QuickAdapter<ContractBean>(mDataList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_contract_operate;
            }

            @Override
            public void convert(VH holder, ContractBean contractBean, int position) {

                //设置item背景
                //待还款 R.drawable.item_contract_c3_bg
                //已逾期 R.drawable.item_contract_c4_bg
                //已结束 R.drawable.item_contract_c1_bg
                //设置合约状态
                //仅待投资状态有发布按钮
                //合约对应时间类型
                //合约对应时间
                ImageView selectImg = holder.getView(R.id.contract_operate_select);
                selectImg.setImageResource(mOperateType.equals("export") ? R.drawable.contract_export_selector : R.drawable.contract_delete_selector);
                switch (contractBean.getContractState()) {
                    case CONTRACT_STATUS_WAIT_MORTGAGE:
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_1));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_1));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getCreateTime()));
                        break;
                    case CONTRACT_STATUS_WAIT_INVEST:
                        selectImg.setVisibility(mOperateType.equals("export") ? View.VISIBLE : View.GONE);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_2));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.publish_time));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getCreateTime()));

                        holder.getView(R.id.item_contract_publish).setVisibility(View.VISIBLE);
                        break;
                    case CONTRACT_STATUS_WAIT_REPAYMENT:
                        selectImg.setVisibility(mOperateType.equals("export") ? View.VISIBLE : View.GONE);
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c3_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_3));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_3));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getExpiryTime()));
                        break;
                    case CONTRACT_STATUS_REPAID:
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_5));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_5));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getEndTime()));

                        //隐藏轮询按钮
                        holder.getView(R.id.item_contract_polling).setVisibility(View.GONE);

                        //已结束状态需要改变item的背景颜色和字体颜色
                        holder.getView(R.id.rl_assets_detail).setBackgroundResource(R.drawable.item_contract_end_bg);
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c1_bg);
                        holder.setTextColor(R.id.item_contract_status, mContext.getResources().getColor(R.color.c11));
//                        holder.setTextColor(R.id.item_contract_type, mContext.getResources().getColor(R.color.c11));
                        break;
                    case CONTRACT_STATUS_OVERDUE:
                        selectImg.setVisibility(mOperateType.equals("export") ? View.VISIBLE : View.GONE);
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c4_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_4));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_4));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getExpiryTime()));
                        break;
                    case CONTRACT_STATUS_END:
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c1_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_6));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_6));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getEndTime()));

                        //隐藏轮询按钮
                        holder.getView(R.id.item_contract_polling).setVisibility(View.GONE);

                        //已结束状态需要改变item的背景颜色和字体颜色
                        holder.getView(R.id.rl_assets_detail).setBackgroundResource(R.drawable.item_contract_end_bg);
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c1_bg);
                        holder.setTextColor(R.id.item_contract_status, mContext.getResources().getColor(R.color.c11));
//                        holder.setTextColor(R.id.item_contract_type, mContext.getResources().getColor(R.color.c11));
                        break;
                }

                //设置合约类型
                if (contractBean.getBorrowerAddress().equalsIgnoreCase(HLWalletManager.shared().getCurrentWallet(mContext).getAddress())) {
                    //借币
                    holder.setText(R.id.item_contract_type, mContext.getString(R.string.borrow_money));
                    holder.setTextBackground(R.id.item_contract_type, R.drawable.contract_label_black_bg);
                } else {
                    if (!contractBean.getInvestorAddress().equalsIgnoreCase(HLWalletManager.shared().getCurrentWallet(mContext).getAddress())) {
                        //关注
                        holder.setText(R.id.item_contract_type, mContext.getString(R.string.follow));
                        holder.setTextBackground(R.id.item_contract_type, R.drawable.contract_label_dark_bg);
                    } else {
                        //投资
                        holder.setText(R.id.item_contract_type, mContext.getString(R.string.investment));
                        holder.setTextBackground(R.id.item_contract_type, R.drawable.contract_label_gray_bg);
                    }
                }

                //数量
                holder.setText(R.id.item_contract_coins_value, Convert.fromWei(contractBean.getAmount(), Convert.Unit.ETHER).toString());


                String tokenSymbol;//获取当前抵押资产的简称
                // ETH
                if(contractBean.getContractType() == null || contractBean.getContractType().equals("0")){
                    holder.setText(R.id.item_contract_coins_type, mContext.getString(R.string.eth));
                    tokenSymbol = MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress());
                    String mortgage = CommomUtil.getTokenValue(mContext,contractBean.getTokenAddress(),contractBean.getMortgage());
                    holder.setText(R.id.item_contract_token_value, CommomUtil.doubleFormat(mortgage) + " " + tokenSymbol);
                }
                // TUSD 1
                else if(contractBean.getContractType().equals("1")){
                    holder.setText(R.id.item_contract_coins_type, mContext.getString(R.string.tusd));
                    String mortgage = Convert.fromWei(contractBean.getMortgage(), Convert.Unit.ETHER).toString();
                    holder.setText(R.id.item_contract_token_value,mContext.getString(R.string.eth_amount, CommomUtil.doubleFormat(mortgage)) );

                }
                // TUSD 2
                else if(contractBean.getContractType().equals("2")){
                    holder.setText(R.id.item_contract_coins_type, mContext.getString(R.string.tusd));
                    tokenSymbol = MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress());
                    String mortgage = CommomUtil.getTokenValue(mContext,contractBean.getTokenAddress(),contractBean.getMortgage());
                    holder.setText(R.id.item_contract_token_value, CommomUtil.doubleFormat(mortgage) + " " + tokenSymbol);
                }

                if(contractBean.getContractType() == null || contractBean.getContractType().equals("0")){
                    holder.setText(R.id.item_contract_maturity_interest, String.format(mContext.getString(R.string.eth_amount),
                            CommomUtil.decimalTo4Point(
                                    Convert.fromWei(contractBean.getExpire(), Convert.Unit.ETHER).toString())));
                }else if(contractBean.getContractType().equals("1")|| contractBean.getContractType().equals("2")){}{
                    holder.setText(R.id.item_contract_maturity_interest, String.format(mContext.getString(R.string.tusd_amount),
                            CommomUtil.decimalTo4Point(
                                    Convert.fromWei(contractBean.getExpire(), Convert.Unit.ETHER).toString())));
                }
                selectImg.setSelected(mSelectedList.get(position));
                //合约操作的点击事件
                holder.getView(R.id.contract_operate_select).setOnClickListener(v -> {
                    if (selectImg.getVisibility() == View.VISIBLE) {
                        selectImg.setSelected(!selectImg.isSelected());

                        mSelectedList.set(position, selectImg.isSelected());
                    }

                    mContractOperateBinding.contractOperate.setEnabled(getmSelectedDataList().size() != 0);
                });

                holder.getView(R.id.rl_assets_detail).setOnClickListener(v -> {
                    if (selectImg.getVisibility() == View.VISIBLE) {
                        selectImg.setSelected(!selectImg.isSelected());

                        mSelectedList.set(position, selectImg.isSelected());
                    }

                    mContractOperateBinding.contractOperate.setEnabled(getmSelectedDataList().size() != 0);
                });

            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        //设置布局管理器
        mContractOperateBinding.contractOperateRecyclerview.setLayoutManager(gridLayoutManager);
        //设置Adapter
        mContractOperateBinding.contractOperateRecyclerview.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        mContractOperateBinding.contractOperateRecyclerview.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 初始化刷新组件
     */
    public void initRefresh() {
        mContractOperateBinding.contractOperateRefreshLayout.setEnableRefresh(false);
        mContractOperateBinding.contractOperateRefreshLayout.setEnableLoadMore(true);
        mContractOperateBinding.contractOperateRefreshLayout.setOnRefreshListener(refreshlayout -> {
            offset = 0;
            freshState = 1;
            loadData();
        });
        mContractOperateBinding.contractOperateRefreshLayout.setOnLoadMoreListener(refreshlayout -> {
            offset++;
            freshState = 2;
            loadData();
        });
    }

    /**
     * 加载数据
     */
    public void loadData() {
        List<ContractBean> data = DBUtil.queryContractsByOffset(offset);
        if (data.size() == 0) {
            showContractEmptyView();
        }

        if (freshState != 2) {
            //有数据的话需要清空数据
            if (mDataList.size() != 0) {
                mDataList.clear();
            }

            if (mSelectedList.size() != 0) {
                mSelectedList.clear();
            }
        }

        //停止刷新
        mContractOperateBinding.contractOperateRefreshLayout.finishRefresh();
        //停止加载
        mContractOperateBinding.contractOperateRefreshLayout.finishLoadMore();

        if (data.size() != 0) {

            mDataList.addAll(data);
            //显示列表，隐藏空布局
            notifyListData();

            for (int i = 0; i < data.size(); i++) {
                mSelectedList.add(false);
            }

            //判断是否已无更多数据
            if (data.size() < 20) {
                mContractOperateBinding.contractOperateRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        } else {
            if (freshState != 2) {
                //显示空布局，隐藏列表
                showContractEmptyView();
            } else {
                mContractOperateBinding.contractOperateRefreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    /**
     * 初始化点击事件
     */
    public void initClick() {
        //取消
        mContractOperateBinding.cancel.setOnClickListener(v -> ((Activity)mContext).finish());
        //导出、删除
        mContractOperateBinding.contractOperate.setOnClickListener(v -> {
            List<ContractBean> contractBeans = getmSelectedDataList();
            KLog.i(Singleton.gson().toJson(contractBeans));
            if (contractBeans.size() != 0) {
                //执行操作
                if (mOperateType.equals("export")) {//导出
                    Flowable.just(1)
                            .flatMap(s -> ShareUtil.exportContractQRImg(mContext,contractBeans))
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new HLSubscriber<Boolean>(mContext, true) {
                                @Override
                                protected void success(Boolean data) {
                                    SnackbarUtil.DefaultSnackbar(mContractOperateBinding.getRoot(), mContext.getString(R.string.export_success)).show();
                                    new android.os.Handler().postDelayed(() -> ((Activity)mContext).finish(), 1000);
                                }

                                @Override
                                protected void failure(HLError error) {
                                    SnackbarUtil.DefaultSnackbar(mContractOperateBinding.getRoot(), mContext.getString(R.string.export_failed)).show();
                                }
                            });
                } else {//删除
                    new DeleteDialog(new CenterDialog(R.layout.dlg_delete_wallet, mContext), mContext.getString(R.string.delete_contract), mContext.getString(R.string.delete_contract_warn), () -> {
                        for(ContractBean contractBean : contractBeans) {
                            DBUtil.delete(contractBean);
                        }
                        ((Activity)mContext).finish();
                    });

                }
            }
        });
    }

    /**
     * 刷新列表数据
     */
    private void notifyListData() {
        mContractOperateBinding.contractOperateRefreshLayout.setVisibility(View.VISIBLE);
        mContractOperateBinding.contractNull.setVisibility(View.GONE);
        quickAdapter.notifyDataSetChanged();
    }

    /**
     * 显示合约为空布局
     */
    private void showContractEmptyView() {
        mContractOperateBinding.contractOperateRefreshLayout.setVisibility(View.GONE);
        mContractOperateBinding.contractNull.setVisibility(View.VISIBLE);
    }

    private List<ContractBean> getmSelectedDataList() {
        if (mSelectedDataList.size() != 0) {
            mSelectedDataList.clear();
        }
        for (int i = 0; i < mDataList.size(); i++) {
            if (mSelectedList.get(i)) {
                mSelectedDataList.add(mDataList.get(i));
            }
        }
        return mSelectedDataList;
    }
}

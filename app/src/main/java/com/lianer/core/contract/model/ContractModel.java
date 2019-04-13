package com.lianer.core.contract.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.SmartContract.TUSD.IBTUSDContract;
import com.lianer.core.SmartContract.TUSD.IBTUSDContractStatues;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseBean;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.ContractActivity;
import com.lianer.core.contract.ContractTusdActivity;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.databinding.FragmentContractBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.PopupWindowUtil;
import com.lianer.core.utils.ShareUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;

import org.web3j.utils.Convert;

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
 * 我的合约
 *
 * @author allison
 */
public class ContractModel {


    private Context mContext;
    public QuickAdapter quickAdapter;
    private int offset, freshState;
    public List<ContractBean> mDataList;
    private FragmentContractBinding mFragmentContractBinding;


    public ContractModel(Context mContext, List<ContractBean> dataList, FragmentContractBinding fragmentContractBinding) {
        this.mContext = mContext;
        this.mDataList = dataList;
        this.mFragmentContractBinding = fragmentContractBinding;
    }

    /**
     * 初始化recyclerView
     */
    public void initRecyclerView() {
        quickAdapter = new QuickAdapter<ContractBean>(mDataList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_management_contract;
            }

            @Override
            public void convert(VH holder, ContractBean contractBean, int position) {

                //设置item背景 position为临时测试数据
                //待转入抵押、待投资 R.drawable.item_contract_c2_bg
                //待还款 R.drawable.item_contract_c3_bg
                //已逾期 R.drawable.item_contract_c4_bg
                //已结束、已还款 R.drawable.item_contract_c1_bg
                //设置合约状态 position为临时测试数据
                //仅待投资状态有发布按钮
                //合约对应时间类型
                //合约对应时间
                if(contractBean.getContractState() == null){
                    return;
                }
                switch (contractBean.getContractState()) {
                    case CONTRACT_STATUS_WAIT_MORTGAGE:
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c2_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_1));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_1));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getCreateTime()));
                        break;
                    case CONTRACT_STATUS_WAIT_INVEST:
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c2_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_2));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.publish_time));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getCreateTime()));

                        holder.getView(R.id.item_contract_publish).setVisibility(View.VISIBLE);
                        //设置发布按钮点击事件
                        holder.getView(R.id.item_contract_publish).setOnClickListener(v -> {
                            ImageView publishImg = holder.getView(R.id.item_contract_publish);
                            PopupWindow popupWindow = PopupWindowUtil.itemPopupWindow((Activity) mContext);
                            View view = popupWindow.getContentView();
                            if (HLWalletManager.shared().getCurrentWallet(mContext).getAddress().equals(contractBean.getBorrowerAddress())) {
                                view.findViewById(R.id.publish_contract).setOnClickListener(v1 -> {
                                    publishContract(contractBean);
                                    popupWindow.dismiss();
                                });
                            } else {
                                view.findViewById(R.id.publish_contract).setAlpha(0.4f);
                            }
                            view.findViewById(R.id.share_contract).setOnClickListener(v1 -> {
                                //TODO 分享
//                                Intent intent = new Intent(mContext, ExportContractActivity.class);
//                                intent.putExtra("ContractId",contractBean.getContractId());
//                                mContext.startActivity(intent);
                                ShareUtil.shareContract(mContext, ShareUtil.generateSharePic(mContext, contractBean));
                                popupWindow.dismiss();
                            });
                            popupWindow.showAsDropDown(holder.getView(R.id.item_contract_publish), -(int) (publishImg.getWidth() * 1.5), 0);
                        });
                        break;
                    case CONTRACT_STATUS_WAIT_REPAYMENT:
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c3_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_3));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_3));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getExpiryTime()));
                        break;
                    case CONTRACT_STATUS_REPAID:
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c1_bg);
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
                        holder.getView(R.id.item_contract_top).setBackgroundResource(R.drawable.item_contract_c4_bg);
                        holder.setText(R.id.item_contract_status, mContext.getString(R.string.status_4));

                        holder.setText(R.id.item_contract_time_type, mContext.getString(R.string.time_4));
                        holder.setText(R.id.item_contract_time, DateUtils.timesTwo(contractBean.getExpiryTime()));
//                        holder.setText(R.id.item_contract_time, DateUtils.timeDiff(String.valueOf(System.currentTimeMillis() / 1000), contractBean.getExpiryTime()));
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

                ImageView itemPolling = holder.getView(R.id.item_contract_polling);

                //轮询按钮点击事件
                itemPolling.setOnClickListener(v -> {
                    //动画
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.contract_status_loading);
                    LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
                    animation.setInterpolator(lin);
                    itemPolling.startAnimation(animation);
                    itemPolling.setSelected(true);
                    //查询链上合约状态
                    try {
                        Flowable.just(1)
                                .flatMap(s -> IBContractUtil.readOnlyIBTUSDContract(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(mContext).getAddress(), contractBean.getContractAddress()))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HLSubscriber<String>() {
                                    @Override
                                    protected void success(String contractAddress) {
//                                        IBTUSDContractStatues ibContractStatues = null;
//                                        try {
//                                            ibContractStatues = IBContractUtil.getTUSDContractStatus(contractAddress);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                        String contractState = ibContractStatues.getContractState();
//                                        KLog.i("链上合约状态===" + contractState);
//                                        KLog.i("本地数据库合约状态===" + contractBean.getContractState());
//
//                                        boolean isContractStateMatch = contractState.equals(contractBean.getContractState());
//                                        KLog.i("链上合约状态是否和本地数据库合约状态相同===" + isContractStateMatch);
//
//                                        itemPolling.setSelected(false);
//                                        itemPolling.clearAnimation();
//
//                                        if (!isContractStateMatch) {
//                                            //如果合约状态不同，更新链上合约状态到本地数据库
//                                            ContractBean tempContractBean = IBContractUtil.getTusdContractBean(ibContractStatues);
//                                            tempContractBean.setContractId(contractBean.getContractId());
//                                            DBUtil.update(tempContractBean);
//
//                                            //更新页面数据
//                                            mDataList.set(position, tempContractBean);
//                                            notifyItemChanged(position);
//                                        }

                                        Flowable.just(contractAddress)
                                                .map(s->IBContractUtil.getTUSDContractStatus(s))
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new HLSubscriber<IBTUSDContractStatues>() {
                                                    @Override
                                                    protected void success(IBTUSDContractStatues ibContractStatues) {
                                                        String contractState = ibContractStatues.getContractState();
                                                        KLog.i("链上合约状态===" + contractState);
                                                        KLog.i("本地数据库合约状态===" + contractBean.getContractState());

                                                        boolean isContractStateMatch = contractState.equals(contractBean.getContractState());
                                                        KLog.i("链上合约状态是否和本地数据库合约状态相同===" + isContractStateMatch);

                                                        itemPolling.setSelected(false);
                                                        itemPolling.clearAnimation();

                                                        if (!isContractStateMatch) {
                                                            //如果合约状态不同，更新链上合约状态到本地数据库
                                                            ContractBean tempContractBean = IBContractUtil.getTusdContractBean(ibContractStatues);
                                                            tempContractBean.setContractId(contractBean.getContractId());
                                                            DBUtil.update(tempContractBean);

                                                            //更新页面数据
                                                            mDataList.set(position, tempContractBean);
                                                            notifyItemChanged(position);
                                                        }
                                                    }

                                                    @Override
                                                    protected void failure(HLError error) {

                                                    }
                                                });


                                    }

                                    @Override
                                    protected void failure(HLError error) {
                                        KLog.w(error.getMessage());
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                KLog.w("Contract :" +contractBean.getContractType());
                holder.getView(R.id.rl_assets_detail).setOnClickListener(v -> {
                    Intent intent;
                    if(contractBean.getContractType() == null || contractBean.getContractType().equals("0")){
                        intent = new Intent(mContext, ContractActivity.class);
                    }else{
                        intent = new Intent(mContext, ContractTusdActivity.class);
                    }
                    intent.putExtra("ContractId", contractBean.getContractId());
                    mContext.startActivity(intent);
                });

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
                    tokenSymbol = MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress());
                    String mortgage = Convert.fromWei(contractBean.getMortgage(), Convert.Unit.ETHER).toString();
                    holder.setText(R.id.item_contract_token_value,mContext.getString(R.string.eth_amount, CommomUtil.doubleFormat(mortgage)) );
                }
                // TUSD 2
                else if(contractBean.getContractType().equals("2")){
                    holder.setText(R.id.item_contract_coins_type, mContext.getString(R.string.tusd));
                    tokenSymbol = MortgageAssets.getTokenSymbolByAddress(mContext, contractBean.getTokenAddress());
                    KLog.e("tokenAddress="+contractBean.getTokenAddress()+" tokenSymbol="+tokenSymbol);
                    String mortgage = CommomUtil.getTokenValue(mContext,contractBean.getTokenAddress(),contractBean.getMortgage());
                    holder.setText(R.id.item_contract_token_value, CommomUtil.doubleFormat(mortgage) + " " + tokenSymbol);
                }



                //到期本息
                if(contractBean.getContractType() == null || contractBean.getContractType().equals("0")){
                    holder.setText(R.id.item_contract_maturity_interest, String.format(mContext.getString(R.string.eth_amount),
                            CommomUtil.decimalTo4Point(
                                    Convert.fromWei(contractBean.getExpire(), Convert.Unit.ETHER).toString())));
                }else if(contractBean.getContractType().equals("1")|| contractBean.getContractType().equals("2")){
                    holder.setText(R.id.item_contract_maturity_interest, String.format(mContext.getString(R.string.tusd_amount),
                            CommomUtil.decimalTo4Point(
                                    Convert.fromWei(contractBean.getExpire(), Convert.Unit.ETHER).toString())));
                }

            }
        };
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        //设置布局管理器
        mFragmentContractBinding.recyclerView.setLayoutManager(gridLayoutManager);
        //设置Adapter
        mFragmentContractBinding.recyclerView.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        mFragmentContractBinding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        //避免刷新闪烁和状态异常
        ((SimpleItemAnimator)mFragmentContractBinding.recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }


    /**
     * 初始化刷新组件
     */
    public void initRefresh() {
        mFragmentContractBinding.refreshLayout.setEnableRefresh(false);
        mFragmentContractBinding.refreshLayout.setEnableLoadMore(true);
        mFragmentContractBinding.refreshLayout.setOnRefreshListener(refreshlayout -> {
            offset = 0;
            freshState = 1;
            loadData();
        });
        mFragmentContractBinding.refreshLayout.setOnLoadMoreListener(refreshlayout -> {
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
        }

        //停止刷新
        mFragmentContractBinding.refreshLayout.finishRefresh();
        //停止加载
        mFragmentContractBinding.refreshLayout.finishLoadMore();

        if (data.size() != 0) {

            mDataList.addAll(data);
            //显示列表，隐藏空布局
            notifyListData();

            //判断是否已无更多数据
            if (data.size() < 20) {
                mFragmentContractBinding.refreshLayout.finishLoadMoreWithNoMoreData();
            }
        } else {
            if (freshState != 2) {
                //显示空布局，隐藏列表
                showContractEmptyView();
            } else {
                mFragmentContractBinding.refreshLayout.finishLoadMoreWithNoMoreData();
            }
        }
    }

    /**
     * 刷新列表数据
     */
    private void notifyListData() {
        mFragmentContractBinding.refreshLayout.setVisibility(View.VISIBLE);
        mFragmentContractBinding.contractNull.setVisibility(View.GONE);
        quickAdapter.notifyDataSetChanged();
    }

    /**
     * 显示合约为空布局
     */
    private void showContractEmptyView() {
        mFragmentContractBinding.refreshLayout.setVisibility(View.GONE);
        mFragmentContractBinding.contractNull.setVisibility(View.VISIBLE);
    }

    /**
     * 发布合约到市场
     */
    public void publishContract(ContractBean contractBean) {
        String jsonParams = "{\n" +
                "\t\"address\": \"" + contractBean.getContractAddress() + "\"\n" +
                "}";
        KLog.i(jsonParams);

        // 借贷合约
        if( contractBean.getContractType() == null || contractBean.getContractType().equals("0")){
            HttpUtil.publishContract(jsonParams).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<BaseBean>() {
                        @Override
                        protected void success(BaseBean data) {
                            if (data.getCode().equals(Constants.REQUEST_SUCCESS)) {
                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_publish_success)).show();
                            }
                            else if (data.getCode().equals(Constants.REQUEST_CONTRACT_EXISTED)){
                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_contract_existed)).show();
                            }
                            else {
                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_publish_fail)).show();
                            }
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
                        }
                    });
        }
        //TUSD 借贷合约
        else if(contractBean.getContractType().equals("1") || contractBean.getContractType().equals("2")){
            HttpUtil.publishTusdContract(jsonParams).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<BaseBean>() {
                        @Override
                        protected void success(BaseBean data) {
                            if (data.getCode().equals(Constants.REQUEST_SUCCESS)) {
                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_publish_success)).show();
                            }
                            else if (data.getCode().equals(Constants.REQUEST_CONTRACT_EXISTED)){
                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_contract_existed)).show();
                            }
                            else {
                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_publish_fail)).show();
                            }
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
                        }
                    });
        }

    }

    private long mSize;
    public void refershContract(){
        List<ContractBean> datas = DBUtil.queryContracts();
        mSize = datas.size();
        for (ContractBean data:
             datas) {
            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.getTUSDContractInfo(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(mContext).getAddress(), data.getContractAddress()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<ContractBean>() {
                        @Override
                        protected void success(ContractBean contractBean) {
                            KLog.i("Ethereum :" + contractBean.getContractState());
                            KLog.i("Local :" + data.getContractState());

                            boolean isContractStateMatch =  contractBean.getContractState().equals(data.getContractState());
                            KLog.i("isMatch :" + isContractStateMatch);

                            //如果合约状态不同，更新链上合约状态到本地数据库
                            if (!isContractStateMatch &&
                                    //校验地址是否匹配
                                    DBUtil.queryContractById(data.getContractId()).getContractAddress().equalsIgnoreCase(contractBean.getContractAddress())) {

                                contractBean.setContractId(data.getContractId());

                                DBUtil.update(contractBean);
                                KLog.i("Format Data :" + contractBean.getContractAddress());
                            }
                            mSize--;
                            if(mSize == 0){
                                quickAdapter.notifyDataSetChanged();
                                KLog.i("Refersh Data" );
                            }
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.w(error.getMessage());
                        }
                    });
        }


    }

}

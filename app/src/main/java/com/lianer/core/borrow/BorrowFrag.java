package com.lianer.core.borrow;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseFragment;
import com.lianer.common.base.QuickAdapter;
import com.lianer.core.R;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.TokenMortgageBean;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.TransferUtil;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 借币页
 */
public class BorrowFrag extends BaseFragment {

    View mView;
    private List<TokenMortgageBean> mDatas = new ArrayList<>();
    private QuickAdapter mAdapter;
    private int page = 1;
    private int freshState;//刷新状态  0:正常状态  1:下拉刷新  2:上拉加载
    private RecyclerView currencyList;
    private TypedArray logos ;
    private HLWallet mWallet;
    private View lasterView = null;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_borrow, null);
        return mView;
    }

    @Override
    protected void initData() {
        super.initData();
        mWallet = HLWalletManager.shared().getCurrentWallet(getActivity());
        logos = getResources().obtainTypedArray(R.array.token_logo_list);
        mDatas = MortgageAssets.getTokenMortgageList(getActivity());

        initRecyclerView();
//        initResponseData("");
    }


    private void initRecyclerView() {
        currencyList = mView.findViewById(R.id.currency_list);
        mAdapter = new QuickAdapter<TokenMortgageBean>(mDatas) {

            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_borrowing;
            }

            @Override
            public void convert(VH holder, TokenMortgageBean data, int position) {

                //代币名称
                holder.setText(R.id.token_name, data.getTokenAbbreviation());
                //代币logo
                holder.setImage(R.id.token_logo,logos.getResourceId(position,0) );

                //折扣率
                holder.setText(R.id.discount_rate, data.getTokenDiscountRate() +"%");
                //代币余额
                getTokenBanlace(data,holder);

                //代币地址
                String address = data.getTokenAddress().substring(0,10)  + "......" + data.getTokenAddress().substring(34,42);
                holder.setText(R.id.token_address, address);

                //代币地址跳转
                holder.getView(R.id.token_address).setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), EtherScanWebActivity.class);
                    intent.putExtra("ContractAddress",  data.getTokenAddress());
                    startActivity(intent);
                });
                //展开详情
                holder.getView(R.id.token_layout).setOnClickListener(v -> {
                    if(holder.getView(R.id.project_detail_layout).getVisibility() == View.VISIBLE){
                        holder.getView(R.id.project_detail_layout).setVisibility(View.GONE);
                    }else{
                        if(lasterView != null){
                            lasterView.setVisibility(View.GONE);
                        }
                        lasterView = holder.getView(R.id.project_detail_layout);
                        holder.getView(R.id.project_detail_layout).setVisibility(View.VISIBLE);
                    }
                });

                //点击借币
                holder.setText(R.id.btn_borrowing, getString(R.string.borrowing_now));
                holder.getView(R.id.btn_borrowing).setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(),BorrowingActivity.class);
                    //抵押代币类型
                    intent.putExtra("AssetsType",data.getTokenNum());
                    //抵押折扣率
                    intent.putExtra("DiscountRate",Integer.parseInt(data.getTokenDiscountRate()));
                    //资产昨日均价
                    intent.putExtra("ExchangeRate",Convert.fromWei(data.getTokenPrice(), Convert.Unit.ETHER).doubleValue());
                    startActivity(intent);
                });
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //设置布局管理器
        currencyList.setLayoutManager(layoutManager);
        //设置Adapter
        currencyList.setAdapter(mAdapter);
        //设置增加或删除条目的动画
        currencyList.setItemAnimator(new DefaultItemAnimator());
    }


    private void getTokenBanlace(TokenMortgageBean data ,QuickAdapter.VH holder){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getTokenBalance(TransferUtil.getWeb3j(),mWallet.getAddress(), data.getTokenAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(getActivity(),false) {
                    @Override
                    protected void success(String value) {
                        //token 余额
                        holder.setText(R.id.current_balance, CommomUtil.decimalTo4Point(value)+" "+data.getTokenAbbreviation());
                        if((new BigDecimal(value)).intValue() != 0){
                            BigDecimal maxValue = new BigDecimal(value)
                                    .multiply((new BigDecimal(data.getTokenDiscountRate())).divide(new BigDecimal("100")).multiply(Convert.fromWei(data.getTokenPrice(), Convert.Unit.ETHER)));

                            holder.setText(R.id.maximum_loanable, getString(R.string.maximum_loanable, CommomUtil.decimalTo4Point(maxValue+"")));
                        }else{
                            holder.setText(R.id.maximum_loanable, getString(R.string.maximum_loanable, "0.0000"));
                            holder.getView(R.id.btn_borrowing).setEnabled(false);
                            holder.getView(R.id.btn_borrowing).setBackgroundResource(R.drawable.gray_oval_btn);
                            ((TextView)holder.getView(R.id.btn_borrowing)).setTextColor(getResources().getColor(R.color.clr_8c8c8c));
                        }

                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w(error);
                        //token 余额
                        holder.setText(R.id.current_balance, "0.0000 "+data.getTokenAbbreviation());
                        holder.setText(R.id.maximum_loanable, getString(R.string.maximum_loanable, "0.0000"));
                        holder.getView(R.id.btn_borrowing).setEnabled(false);
                        holder.getView(R.id.btn_borrowing).setBackgroundResource(R.drawable.gray_oval_btn);
                        ((TextView)holder.getView(R.id.btn_borrowing)).setTextColor(getResources().getColor(R.color.clr_666666));
                    }
                });

    }


//    /**
//     * 获取响应数据
//     */
//    private void initResponseData(String jsonRequestParams) {
//        HttpUtil.queryExchangRate(jsonRequestParams)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new HLSubscriber<List<ExchangeRateBean>>(getActivity(),true) {
//                    @Override
//                    protected void success(List<ExchangeRateBean> data) {
//                        //TODO 测试币
//                        data.add(new ExchangeRateBean(1,"0xCC9Ce3B4737956edC71612A44d4F57DE24F186b5","0,90000000000000000,90",""));
//                        for(int i = 0 ; i < data.size(); i++ ){
//                            String[] array = data.get(i).getValue().split(",");
//                            MortgageAssets.setTokenRate(data.get(i).getKey(),array[2],array[1]);
//                        }
//                        mDatas = MortgageAssets.getTokenList();
//                        mAdapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    protected void failure(HLError error) {
//
//                    }
//                });
//    }



}

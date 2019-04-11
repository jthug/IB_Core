package com.lianer.core.borrow;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityBorrowingBinding;
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
import cn.qqtheme.framework.picker.SinglePicker;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;


public class BorrowingActivity extends BaseActivity implements View.OnClickListener  {

    private ActivityBorrowingBinding mBinding;
    /**
     * 借币数量
     */
    private int mAmount;
    /**
     * 借币周期
     */
    private int mCycle;
    /**
     * 借币利息
     */
    private double mInterest;
    /**
     * 到期应还本息
     */
    private double mDuePayment;
    /**
     * 抵押资产类型
     */
    private String mAssetsType = "";

    /**
     * 汇率
     */
    private double mExchangeRate = 0d;
    /**
     * 抵押折扣率
     */
    private int mDiscountRate = 0;

    /**
     * 抵押资产
     */
    private double mAssetsAmount;

    /**
     * 手续费
     */
    private double mHandlingFee;
    /**
     * 实际到账
     */
    private double mActualArrival;


    //TODO 手续费
    private static final double COMMISSION_RATE = 0.01d;


    private String mTokenAddress ;

    //代币余额
    private String mTokenBalance;
    private HLWallet mWallet;

    //免责协议
    private boolean isConsent = false;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_borrowing);
        mBinding.titlebar.showLeftDrawable();
        mBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                onBackPressed();
            }

            @Override
            public void rightTextClick() {

            }

            @Override
            public void rightImgClick() {

            }
        });
        mBinding.borrowingAmount.setOnClickListener(this);
        mBinding.borrowingCycle.setOnClickListener(this);
        mBinding.borrowingInterest.setOnClickListener(this);
        mBinding.createContract.setOnClickListener(this);
        mBinding.readConfirm.setOnClickListener(this);
        mBinding.userAgreement.setOnClickListener(this);
    }


    @Override
    protected void initData() {
        mWallet = HLWalletManager.shared().getCurrentWallet(this);

        //抵押代币类型
        mAssetsType = getIntent().getStringExtra("AssetsType");
        mBinding.mortgageAssetsType.setText(MortgageAssets.getTokenSymbol(getApplicationContext(),mAssetsType));
        mTokenAddress = MortgageAssets.getTokenAddress(getApplicationContext(),mAssetsType);

        //抵押折扣率
        mDiscountRate = getIntent().getIntExtra("DiscountRate",0);
        mBinding.mortgageDiscountRate.setText(mDiscountRate+"%");
        //资产昨日均价
        mExchangeRate = getIntent().getDoubleExtra("ExchangeRate",0d);
        mBinding.exchangeRate.setText(mExchangeRate+"");
        mBinding.exchangeRateType.setText(MortgageAssets.getTokenSymbol(getApplicationContext(),mAssetsType)+"/ETH");

        getTokenBanlace();

    }



    private void getTokenBanlace(){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getTokenBalance(TransferUtil.getWeb3j(),mWallet.getAddress(), mTokenAddress))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(BorrowingActivity.this,false) {
                    @Override
                    protected void success(String data) {
                        mTokenBalance = CommomUtil.decimalTo4Point(data);;
                        mBinding.balanceAmount.setText(getString(R.string.current_token_amount,MortgageAssets.getTokenSymbol(getApplicationContext(),mAssetsType),mTokenBalance));

                    }

                    @Override
                    protected void failure(HLError error) {
                    }
                });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            //借币数量选择
            case R.id.borrowing_amount:
                onBorrowingAmountPicker();
                break;
            //借币周期选择
            case R.id.borrowing_cycle:
                onBorrowingCyclePicker();
                break;
            //借币利息选择
            case R.id.borrowing_interest:
                onBorrowingInterestPicker();
                break;
            //免责协议阅读
            case R.id.read_confirm:
                isConsent = !isConsent;
                if(isConsent){
                    mBinding.readConfirm.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.ic_agreement_selected),
                            null, null, null);

                }else{
                    mBinding.readConfirm.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.ic_agreement_unselected),
                            null, null, null);

                }
                //激活按钮
                inputCheck();
                break;
            //免责协议详情
            case R.id.user_agreement:
                intent = new Intent(this, BannerDetailAct.class);
//                if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_CHINESE);
//                } else {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_ENGLISH);
//                }
                intent.putExtra("webUrl", Constants.USER_AGREEMENT);
                startActivity(intent);
                break;
            //创建合约
            case R.id.create_contract:

                 intent = new Intent(this,ContractDeployActivity.class);
                ContractResponse contractInfo = new ContractResponse();
                //借币数量
                contractInfo.setBorrowAssetsAmount(dataProcessing(mAmount+""));
                //投资数量
                contractInfo.setInvestmentAmount(dataProcessing(mAmount+""));
                //周期
                contractInfo.setTimeLimit(mCycle);
                //利率
                contractInfo.setInterestRate((int) (mInterest*10));
                //折扣率
                contractInfo.setDiscountRate(mDiscountRate+"");
                //到期本息
                contractInfo.setPrincipalAndInterest(dataProcessing(mDuePayment+""));
                //抵押资产数量
                contractInfo.setMortgageAssetsAmount(dataProcessing(mAssetsAmount+""));
                //手续费
                contractInfo.setPoundage(dataProcessing(mHandlingFee+""));
                //实际到账
                contractInfo.setActualAmount(dataProcessing(mActualArrival+""));
                //昨日均价
                contractInfo.setMortgageAssetsPrice(dataProcessing(mExchangeRate+""));
                //抵押资产折合eth
                contractInfo.setPriceToEth(dataProcessing((mAssetsAmount * mExchangeRate)+""));
                //抵押资产类型
                contractInfo.setMortgageAssetsType(mAssetsType);

                KLog.w("参数设置： "+ new Gson().toJson(contractInfo));
                intent.putExtra(CONTRACT_RESPONSE,contractInfo);
                startActivity(intent);
                break;
        }
    }

    private void inputCheck(){
        if(isConsent && mAmount > 0 && mCycle > 0 && mInterest > 0){
            mBinding.createContract.setBackgroundResource(R.drawable.gradient_oval_btn);
            mBinding.createContract.setTextColor(getResources().getColor(R.color.clr_F5F5F5));
            mBinding.createContract.setEnabled(true);
        }else{
            mBinding.createContract.setBackgroundResource(R.drawable.gray_oval_btn);
            mBinding.createContract.setTextColor(getResources().getColor(R.color.clr_666666));
            mBinding.createContract.setEnabled(false);
        }
    }

    //去除小数点
    private String dataProcessing(String original){
        String value =  Convert.toWei(original,Convert.Unit.ETHER)+"";
        if(value.contains(".")){
            return value.substring(0,value.indexOf("."));
        }
        return value;
    }

    //计算到期本息还款
    private void totalCalculation(){
        if(mAmount > 0 && mCycle> 0 && mInterest> 0){
            mDuePayment = mAmount*(mInterest/1000d)*mCycle +mAmount;
            mBinding.duePayment.setText(getString(R.string.eth_amount,mDuePayment+""));

            //抵押代币数量
            mAssetsAmount = mAmount / mExchangeRate / (mDiscountRate/100d);
            mBinding.mortgageAssetsAmount.setText(CommomUtil.decimalTo4Point(mAssetsAmount+""));

            //抵押资产折合ETH
            mBinding.mortgageAssetsEquivalent.setText(CommomUtil.decimalTo4Point(mAssetsAmount * mExchangeRate+""));

            //手续费
            mHandlingFee = mAmount * COMMISSION_RATE;
            mBinding.handlingFee.setText(mHandlingFee+"");

            //实际到账
            mActualArrival = mAmount - mHandlingFee;
            mBinding.actualArrival.setText(mActualArrival+"");

            //token余额不足
            if((new BigDecimal(mTokenBalance)).compareTo(new BigDecimal(mAmount)) == -1){
                mBinding.balanceAmount.setTextColor(getResources().getColor(R.color.clr_F5222D));
            }
            //显示计算结果UI
            mBinding.calculationResult.setVisibility(View.VISIBLE);
        }
        //激活按钮
        inputCheck();
    }

    /**
     * 借币数量选择器
     */
    public void onBorrowingAmountPicker() {
        List<Integer> data = new ArrayList<>();
        for(int i = 0 ; i < 20 ; i++ ){
            data.add((i+1));
        }
        SinglePicker<Integer> picker = new SinglePicker<>(this, data);
        //item宽度
        picker.setItemWidth(200);
        //标题栏设置
        View headerView = View.inflate(this, R.layout.picker_header, null);
         TextView title = (TextView) headerView.findViewById(R.id.picker_title);
         Button btnSure = (Button) headerView.findViewById(R.id.picker_sure);
         Button btnCancel = (Button) headerView.findViewById(R.id.picker_cancel);
        title.setText(getString(R.string.borrowing_amount));
        picker.setHeaderView(headerView);
        picker.setOffset(4);//偏移量
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(false);

        picker.setAnimationStyle(R.style.Animation_CustomPopup);
        picker.setLabel("ETH");

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.borrowingAmount.setText(picker.getSelectedItem()+" ETH");
                mBinding.borrowingAmount.setTextColor(getResources().getColor(R.color.clr_111111));
                mAmount = picker.getSelectedItem();
                totalCalculation();
                picker.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            }
        });

        picker.show();
    }

    /**
     * 借币周期选择器
     */
    public void onBorrowingCyclePicker() {
        List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(3);
        data.add(7);
        data.add(14);
        data.add(21);
        data.add(30);
        data.add(45);
        data.add(60);
        data.add(90);
        SinglePicker<Integer> picker = new SinglePicker<>(this, data);
        //item宽度
        picker.setItemWidth(200);
        //标题栏设置
        View headerView = View.inflate(this, R.layout.picker_header, null);
        TextView title = (TextView) headerView.findViewById(R.id.picker_title);
        Button btnSure = (Button) headerView.findViewById(R.id.picker_sure);
        Button btnCancel = (Button) headerView.findViewById(R.id.picker_cancel);
        title.setText(getString(R.string.borrowing_cycle));
        picker.setHeaderView(headerView);
        picker.setOffset(4);//偏移量
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(false);

        picker.setAnimationStyle(R.style.Animation_CustomPopup);
        picker.setLabel(getString(R.string.day));

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.borrowingCycle.setText(picker.getSelectedItem()+" 天");
                mBinding.borrowingCycle.setTextColor(getResources().getColor(R.color.clr_111111));
                mCycle = picker.getSelectedItem();
                totalCalculation();
                picker.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            }
        });

        picker.show();
    }

    /**
     * 借币利息选择器
     */
    public void onBorrowingInterestPicker() {
        List<Double> data = new ArrayList<>();
        data.add(0.5d);
        for(int i = 1 ; i < 11 ; i++ ){
            data.add(1.0d * i);
        }
        data.add(15d);
        data.add(20d);
        data.add(25d);
        data.add(30d);

        SinglePicker<Double> picker = new SinglePicker<>(this, data);
        //item宽度
        picker.setItemWidth(200);
        //标题栏设置
        View headerView = View.inflate(this, R.layout.picker_header, null);
        TextView title = (TextView) headerView.findViewById(R.id.picker_title);
        Button btnSure = (Button) headerView.findViewById(R.id.picker_sure);
        Button btnCancel = (Button) headerView.findViewById(R.id.picker_cancel);
        title.setText(getString(R.string.borrowing_interest));
        picker.setHeaderView(headerView);
        picker.setOffset(4);//偏移量
        picker.setCanceledOnTouchOutside(true);
        picker.setSelectedIndex(0);
        picker.setCycleDisable(false);

        picker.setAnimationStyle(R.style.Animation_CustomPopup);
        picker.setLabel("‰");

        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.borrowingInterest.setText(picker.getSelectedItem()+" ‰");
                mBinding.borrowingInterest.setTextColor(getResources().getColor(R.color.clr_F87962));
                mInterest = picker.getSelectedItem();
                totalCalculation();
                picker.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.dismiss();
            }
        });

        picker.show();
    }


}

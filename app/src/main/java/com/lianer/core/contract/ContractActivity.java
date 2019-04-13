package com.lianer.core.contract;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.base.BaseBean;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.contract.bean.TokenMortgageBean;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityContractBinding;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.dialog.SureAndCancelDialog;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.PopupWindowUtil;
import com.lianer.core.utils.ShareUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.wallet.TxRecordDetailAct;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContractActivity extends BaseActivity implements View.OnClickListener {

    private ActivityContractBinding mBinding;
    private HLWallet mWallet;
    //密码输入框
    private InputWalletPswDialog inputWalletPswDialog;

    private String mPassword = "";
    private Credentials mCredentials;

    //交易哈希
    private String mTxHash;
    //工厂合约地址
    private String factoryContractAddress;
    //全网gas 均价
    private BigInteger gasPrice;

    private BigInteger mGasLimit = BigInteger.valueOf(200000);
    private BigInteger mGasPrice;
    private String mEthBalance = "0";

    //手续费
    private static final double COMMISSION_RATE = 0.01d;
    //汇率
    private double mExchangeRate = 0d;
    //抵押折扣率
    private int mDiscountRate = 0;

    //合约信息
    private ContractBean mContractBean;
    //合约id（数据库id）
    private long mContractId;
    //到期本息
    private double mDuePayment = 1.001d;

    private String contractAddress; //合约地址

    private static final String CONTRACT_CONFIG = "contract_config";
    private static final String CONTRACT_DEPLOY = "contract_deploy";
    private static final String CONTRACT_MORTGAGE = "contract_mortgage";
    private static final String CONTRACT_RETRIEVE_MORTGAGE = "contract_retrieve_mortgage";
    private static final String CONTRACT_INVEST = "contract_invest";
    private static final String CONTRACT_REPAYMENT = "contract_repayment";
    private static final String CONTRACT_OVERDUE = "contract_overdue";

    //用户协议标识flag
    private boolean isAgree = true;
    //已有交易正在打包中
    boolean isKnow;

    //保留四位
    DecimalFormat fourFormat = new DecimalFormat("0.0000");
    //保留八位
    DecimalFormat eightFormat = new DecimalFormat("0.00000000");

    //需转入抵押资产
    String needMortgageAssets;
    //已转入抵押资产
    String mortgageAssets;
    //抵押资产简称
    String tokenymbol;
    private TextView tvPublish;
    private TextView tvShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contract);
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

        mBinding.ivMenu.setOnClickListener(v->{
            PopupWindow popupWindow = PopupWindowUtil.itemPopupWindow(this);

            View view = popupWindow.getContentView();
            tvPublish = view.findViewById(R.id.publish_contract);
            tvShare = view.findViewById(R.id.share_contract);
            ContractBean contractBean = DBUtil.queryContractByAddress(contractAddress);
            String contractState = "";
            String borrowerAddress = "";
            if (contractBean!=null){
                contractState = contractBean.getContractState();
                borrowerAddress = contractBean.getBorrowerAddress();
            }
            if (contractBean==null||!ContractStatus.CONTRACT_STATUS_WAIT_INVEST.equals(contractState)){
                tvPublish.setEnabled(false);
                tvPublish.setAlpha(0.4f);
            }

            if (HLWalletManager.shared().getCurrentWallet(this).getAddress().equals(borrowerAddress)) {
                tvPublish.setOnClickListener(v1 -> {
                    publishContract(contractBean);
                    popupWindow.dismiss();
                });
            }
            if (contractBean==null){
                tvShare.setEnabled(false);
                tvShare.setAlpha(0.4f);
            }
            tvShare.setOnClickListener(v1 -> {
                //TODO 分享
//                                Intent intent = new Intent(mContext, ExportContractActivity.class);
//                                intent.putExtra("ContractId",contractBean.getContractId());
//                                mContext.startActivity(intent);
                ShareUtil.shareContract(this, ShareUtil.generateSharePic(this, contractBean));
                popupWindow.dismiss();
            });
            popupWindow.showAsDropDown(mBinding.ivMenu,-(int) (mBinding.ivMenu.getWidth() * 2.8), mBinding.ivMenu.getHeight()/3);

        });

        mContractId = getIntent().getLongExtra("ContractId", -1);
        //部署新合约
        if (mContractId == -1) {
            mContractBean = new ContractBean();
            mContractBean.setAmount("1");
            mContractBean.setCycle("1");
            mContractBean.setInterest("0.5");
            mContractBean.setContractState(ContractStatus.CONTRACT_STATUS_WAIT_DEPLOY);
            initContractInfo();
        }
        //本地不存在该合约，通过地址查询
        else if (mContractId == -2) {
            getContract(getIntent().getStringExtra("ContractAddress"));
        }
        //本地存在该合约
        else {
            mContractBean = DBUtil.queryContractById(mContractId);
            getContract(mContractBean.getContractAddress());
        }

    }


    //获取工厂合约信息
    private void getfactoryContractAddress() {
        try {

            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.getFactoryContractAddress(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(this).getAddress()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                        @Override
                        protected void success(String address) {
                            factoryContractAddress = address;
                            KLog.w("factoryContractAddress = " + address);
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.w(error.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取合约信息
    private void getContract(String contractAddress) {
        this.contractAddress = contractAddress;
        try {

            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.getTUSDContractInfo(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(this).getAddress(), contractAddress))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<ContractBean>(ContractActivity.this, true) {
                        @Override
                        protected void success(ContractBean contractBean) {

                            mContractBean = contractBean;
                            if (mContractId > 0) {
                                mContractBean.setContractId(mContractId);
                                //更新数据库
                                DBUtil.update(mContractBean);
                            }
                            initContractInfo();
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.w(error.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化合约信息
    private void initContractInfo() {
        //合约不存在，部署新合约
        if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_WAIT_DEPLOY)) {
            deployExecutable();
            return;
        }
        //填充合约数据
        fillContractInfo();

        switch (mContractBean.getContractState()) {
            //待转入抵押资产
            case ContractStatus.CONTRACT_STATUS_WAIT_MORTGAGE:
                deployExecuted();
                //合约部署者可执行抵押
                if (mWallet.getAddress().equalsIgnoreCase(mContractBean.getBorrowerAddress())) {
                    mortgageExecutable();
                }
                break;
            //待投资
            case ContractStatus.CONTRACT_STATUS_WAIT_INVEST:
                deployExecuted();
                mortgageExecuted();
                //非合约部署者可投资
                if (!mWallet.getAddress().equalsIgnoreCase(mContractBean.getBorrowerAddress())) {
                    investExecutable();
                }
                //合约部署者可取回抵押资产
                else {
                    retrieveMortgageExecutable();
                }
                break;
            //待还款
            case ContractStatus.CONTRACT_STATUS_WAIT_REPAYMENT:
                deployExecuted();
                mortgageExecuted();
                investExecuted();
                //合约部署者可还款
                if (mWallet.getAddress().equalsIgnoreCase(mContractBean.getBorrowerAddress())) {
                    repaymentExecutable();
                }
                break;
            //合约已逾期
            case ContractStatus.CONTRACT_STATUS_OVERDUE:
                deployExecuted();
                mortgageExecuted();
                investExecuted();
                //已逾期，还款不可执行
                repaymentNotExecutable();

                //投资人可获取抵押资产
                if (mWallet.getAddress().equalsIgnoreCase(mContractBean.getInvestorAddress())) {
                    overDueExecutable();
                }
                break;
            //合约已还款
            case ContractStatus.CONTRACT_STATUS_REPAID:
                deployExecuted();
                mortgageExecuted();
                investExecuted();
                repaymentExecuted();
                //未逾期，逾期不可执行
                overDueNotExecutable();
                break;
            //合约已结束
            case ContractStatus.CONTRACT_STATUS_END:
                deployExecuted();
                mortgageExecuted();
                investExecuted();
                //已逾期，还款不可执行
                repaymentNotExecutable();
                overDueExecuted();
                break;

        }
    }


    private void fillContractInfo() {
        //合约地址
        if (mContractBean.getContractAddress() != null) {
            fillAddress(mBinding.contractBaseAddress.contractAddress, mContractBean.getContractAddress());
        }
        //借币人地址
        if (mContractBean.getBorrowerAddress() != null) {
            fillAddress(mBinding.contractBaseAddress.borrowAddress, mContractBean.getBorrowerAddress());
        }
        //投资人地址
        if (mContractBean.getInvestorAddress() != null && !mContractBean.getInvestorAddress().equalsIgnoreCase("0x0000000000000000000000000000000000000000")) {
            fillAddress(mBinding.contractBaseAddress.investAddress, mContractBean.getInvestorAddress());
        }

        //借贷金额
        mBinding.contractStep1.borrowingAmount.setText(getString(R.string.eth_amount, toEther(mContractBean.getAmount())));
        //借贷周期
        mBinding.contractStep1.borrowingCycle.setText(getString(R.string.day_amount, cycleTransform(mContractBean.getCycle())));
        //借贷利率
        mBinding.contractStep1.borrowingInterest.setText(getString(R.string.rate_unit, interestTransform(mContractBean.getInterest())));
        //借贷手续费
        mBinding.contractStep1.handlingFee.setText(getString(R.string.eth_amount, toEther(mContractBean.getServiceCharge())));
        //借贷实际到账
        mBinding.contractStep1.actualArrival.setText(getString(R.string.eth_amount, toEther(mContractBean.getActualAccount())));
        //代币名称
        tokenymbol = MortgageAssets.getTokenSymbolByAddress(getApplicationContext(), mContractBean.getTokenAddress());

        //抵押需求数量
        needMortgageAssets = CommomUtil.doubleFormat( tokenInteger(mContractBean.getNeedMortgage()).toString()) + " " + tokenymbol;
        //合约实际数量
        mortgageAssets = CommomUtil.doubleFormat( tokenInteger(mContractBean.getMortgage()).toString())+ " " + tokenymbol;

        //抵押资产信息
        if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_WAIT_MORTGAGE)) {
            mBinding.contractStep2.mortgageAmount.setText(needMortgageAssets);
            mBinding.contractStep2.mortgageText.setText(getString(R.string.mortgage_insufficient));
        } else {
            mBinding.contractStep2.mortgageAmount.setText(mortgageAssets);
            mBinding.contractStep2.mortgageText.setText(getString(R.string.mortgage_sufficient));
        }
        //未投资，可取回抵押资产
        if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_WAIT_INVEST) && mWallet.getAddress().equalsIgnoreCase(mContractBean.getBorrowerAddress())) {
            mBinding.contractStep2.stateText.setText(getString(R.string.step_get_mortgage));
            mBinding.contractStep2.mortgageHint.setText(getString(R.string.step_get_mortgage_hint));
        }

        //投资金额
        mBinding.contractStep3.investAmount.setText(toEther(mContractBean.getAmount()) + " ETH");
        //到期收益
        mBinding.contractStep3.dueIncome.setText(toEther(mContractBean.getExpire()) + " ETH");
        if (mContractBean.getInvestmentTime() != null && !mContractBean.getInvestmentTime().equals("0")) {
            mBinding.contractStep3.investTime.setText(DateUtils.timedate(mContractBean.getInvestmentTime()));
        }

        //还款信息
        mBinding.contractStep4.duePayment.setText(toEther(mContractBean.getExpire()) + " ETH");
        mBinding.contractStep4.retrieveMortgage.setText(mortgageAssets);
        if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_REPAID)) {
            mBinding.contractStep4.repaymentTime.setText(DateUtils.timedate(mContractBean.getEndTime()));
        } else if (!mContractBean.getExpiryTime().equals("0")) {
            mBinding.contractStep4.repaymentTime.setText(DateUtils.timedate(mContractBean.getExpiryTime()));
        }

        //逾期信息
        mBinding.contractStep5.getMortgage.setText(mortgageAssets);
        if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_OVERDUE)) {
            long overdueTime = System.currentTimeMillis() / 1000 - Long.valueOf(mContractBean.getExpiryTime());
            mBinding.contractStep5.overdueTime.setText(getString(R.string.day_hour, overdueTime / 86400 + "", overdueTime / 3600 + ""));
        }
        if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_END)) {
            mBinding.contractStep5.overdueTime.setText(DateUtils.timedate(mContractBean.getEndTime()));
        }
    }

    //部署可执行
    private void deployExecutable() {
        //获取工厂合约地址
        getfactoryContractAddress();
//        //获取抵押信息
        MortgageAssets.refreshMortgageToken(this, true);
        mBinding.contractStep1.stateBar.setEnabled(false);
        mBinding.contractStep1.stateText.setEnabled(false);
        mBinding.contractStep1.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep1.deployState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep1.deployState.setEnabled(false);
        mBinding.contractStep1.deployState.setText(getString(R.string.executable));
        mBinding.arrowStep1.setEnabled(false);

        mBinding.contractStep1.contractDeploy.setElevation(20f);
        mBinding.contractStep1.contractDeploy.setOnClickListener(this);

        mBinding.btnAction.setVisibility(View.VISIBLE);
        mBinding.btnAction.setText(R.string.config_contract);
        mBinding.btnAction.setTag(CONTRACT_CONFIG);
        mBinding.btnAction.setOnClickListener(this);
        //部署合约，钱包地址即为借币地址
        fillAddress(mBinding.contractBaseAddress.borrowAddress, mWallet.getAddress());

        mBinding.userAgreement.setOnClickListener(this);
        mBinding.readConfirm.setOnClickListener(this);
        mGasLimit = BigInteger.valueOf(1000000);
    }

    //部署已执行
    private void deployExecuted() {
        mBinding.contractStep1.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep1.deployState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep1.deployState.setText(getString(R.string.executed));
        mBinding.arrowStep1.setEnabled(false);
        mBinding.contractStep1.amountText.setEnabled(false);
        mBinding.contractStep1.borrowingAmount.setEnabled(false);
        mBinding.contractStep1.cycleText.setEnabled(false);
        mBinding.contractStep1.borrowingCycle.setEnabled(false);
        mBinding.contractStep1.interestText.setEnabled(false);
        mBinding.contractStep1.borrowingInterest.setEnabled(false);
        mBinding.contractStep1.handlingFee.setEnabled(false);
        mBinding.contractStep1.handlingFeeText.setEnabled(false);
        mBinding.contractStep1.actualArrival.setEnabled(false);
        mBinding.contractStep1.actualArrivalText.setEnabled(false);
    }

    //抵押可执行
    private void mortgageExecutable() {
        mBinding.contractStep2.stateBar.setEnabled(false);
        mBinding.contractStep2.stateText.setEnabled(false);
        mBinding.contractStep2.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep2.mortgagState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep2.mortgagState.setEnabled(false);
        mBinding.contractStep2.mortgagState.setText(getString(R.string.executable));
        mBinding.arrowStep2.setEnabled(false);

        mBinding.contractStep2.mortgageAmount.setEnabled(false);
        mBinding.contractStep2.mortgageText.setEnabled(false);

        mBinding.contractStep2.contractMortgage.setElevation(20f);
        mBinding.contractStep2.contractMortgage.setOnClickListener(this);

        mBinding.btnAction.setVisibility(View.VISIBLE);
        mBinding.btnAction.setText(R.string.paying_mortgage_assets);
        mBinding.btnAction.setTag(CONTRACT_MORTGAGE);
        mBinding.btnAction.setOnClickListener(this);
        mBinding.llPublishToMarket.setVisibility(View.VISIBLE);
    }

    //取回抵押可执行
    private void retrieveMortgageExecutable() {
        mBinding.contractStep2.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep2.mortgagState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep2.stateBar.setEnabled(false);
        mBinding.contractStep2.stateText.setEnabled(false);
        mBinding.contractStep2.mortgagState.setEnabled(false);
        mBinding.contractStep2.mortgagState.setText(getString(R.string.executable));
        mBinding.arrowStep2.setEnabled(false);

        mBinding.contractStep2.mortgageAmount.setEnabled(false);
        mBinding.contractStep2.mortgageText.setEnabled(false);

        mBinding.contractStep2.contractMortgage.setElevation(20f);
        mBinding.contractStep2.contractMortgage.setOnClickListener(this);

        mBinding.btnAction.setVisibility(View.VISIBLE);
        mBinding.btnAction.setText(R.string.retrieve_mortgage);
        mBinding.btnAction.setTag(CONTRACT_RETRIEVE_MORTGAGE);
        mBinding.btnAction.setOnClickListener(this);
    }

    //抵押已执行
    private void mortgageExecuted() {
        mBinding.contractStep2.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep2.mortgagState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep2.mortgagState.setText(getString(R.string.executed));
        mBinding.contractStep2.mortgageAmount.setEnabled(false);
        mBinding.contractStep2.mortgageText.setEnabled(false);
        mBinding.arrowStep2.setEnabled(false);
    }

    //投资可执行
    private void investExecutable() {
        mBinding.contractStep3.stateBar.setEnabled(false);
        mBinding.contractStep3.stateText.setEnabled(false);
        mBinding.contractStep3.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep3.investState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep3.investState.setEnabled(false);
        mBinding.contractStep3.investState.setText(getString(R.string.executable));
        mBinding.arrowStep3.setEnabled(false);

        mBinding.contractStep3.investAmount.setEnabled(false);
        mBinding.contractStep3.investText.setEnabled(false);
        mBinding.contractStep3.investAmount.setEnabled(false);
        mBinding.contractStep3.timeText.setEnabled(false);
        mBinding.contractStep3.dueIncome.setEnabled(false);
        mBinding.contractStep3.dueIncomeText.setEnabled(false);

        mBinding.contractStep3.contractInvest.setElevation(20f);
        mBinding.contractStep3.contractInvest.setOnClickListener(this);

        mBinding.btnAction.setVisibility(View.VISIBLE);
        mBinding.btnAction.setText(R.string.invest);
        mBinding.btnAction.setTag(CONTRACT_INVEST);
        mBinding.btnAction.setOnClickListener(this);

        mBinding.userLayout.setVisibility(View.VISIBLE);
        mBinding.userAgreement.setOnClickListener(this);
        mBinding.readConfirm.setOnClickListener(this);
        mGasLimit = BigInteger.valueOf(1000000);
    }

    //投资已执行
    private void investExecuted() {
        mBinding.contractStep3.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep3.investState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep3.investState.setText(getString(R.string.executed));
        mBinding.contractStep3.investAmount.setEnabled(false);
        mBinding.contractStep3.investText.setEnabled(false);
        mBinding.contractStep3.investTime.setEnabled(false);
        mBinding.contractStep3.timeText.setEnabled(false);
        mBinding.contractStep3.dueIncome.setEnabled(false);
        mBinding.contractStep3.dueIncomeText.setEnabled(false);
        mBinding.arrowStep3.setEnabled(false);
    }

    //还款可执行
    private void repaymentExecutable() {
        mBinding.contractStep4.stateBar.setEnabled(false);
        mBinding.contractStep4.stateText.setEnabled(false);
        mBinding.contractStep4.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep4.repaymentState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep4.repaymentState.setEnabled(false);
        mBinding.contractStep4.repaymentState.setText(getString(R.string.executable));
        mBinding.arrowStep4.setEnabled(false);

        mBinding.contractStep4.duePayment.setEnabled(false);
        mBinding.contractStep4.duePaymentText.setEnabled(false);
        mBinding.contractStep4.retrieveMortgage.setEnabled(false);
        mBinding.contractStep4.retrieveMortgageText.setEnabled(false);
        mBinding.contractStep4.repaymentTime.setEnabled(false);
        mBinding.contractStep4.repaymentTimeText.setEnabled(false);

        mBinding.contractStep4.contractRepayment.setElevation(20f);
        mBinding.contractStep4.contractRepayment.setOnClickListener(this);

        mBinding.btnAction.setVisibility(View.VISIBLE);
        mBinding.btnAction.setText(R.string.repayment);
        mBinding.btnAction.setTag(CONTRACT_REPAYMENT);
        mBinding.btnAction.setOnClickListener(this);
    }

    //还款已执行
    private void repaymentExecuted() {
        mBinding.contractStep4.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep4.repaymentState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep4.repaymentState.setText(getString(R.string.executed));
        mBinding.contractStep4.duePayment.setEnabled(false);
        mBinding.contractStep4.duePaymentText.setEnabled(false);
        mBinding.contractStep4.retrieveMortgage.setEnabled(false);
        mBinding.contractStep4.retrieveMortgageText.setEnabled(false);
        mBinding.contractStep4.repaymentTime.setEnabled(false);
        mBinding.contractStep4.repaymentTimeText.setEnabled(false);
        mBinding.arrowStep4.setEnabled(false);
    }

    //还款未执行
    private void repaymentNotExecutable() {
        mBinding.contractStep4.repaymentState.setText(getString(R.string.not_executable));
        mBinding.contractStep4.duePayment.setEnabled(false);
        mBinding.contractStep4.duePaymentText.setEnabled(false);
        mBinding.contractStep4.retrieveMortgage.setEnabled(false);
        mBinding.contractStep4.retrieveMortgageText.setEnabled(false);
        mBinding.contractStep4.repaymentTime.setEnabled(false);
        mBinding.contractStep4.repaymentTimeText.setEnabled(false);
        mBinding.arrowStep4.setEnabled(false);
    }

    //逾期可执行
    private void overDueExecutable() {
        mBinding.contractStep5.stateBar.setEnabled(false);
        mBinding.contractStep5.stateText.setEnabled(false);
        mBinding.contractStep5.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep5.overdueState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep5.overdueState.setEnabled(false);
        mBinding.contractStep5.overdueState.setText(getString(R.string.executable));

        mBinding.contractStep5.overdueTime.setEnabled(false);
        mBinding.contractStep5.getMortgage.setEnabled(false);
        mBinding.contractStep5.overdueText.setEnabled(false);
        mBinding.contractStep5.getMortgageText.setEnabled(false);

        mBinding.contractStep5.contractOverdue.setElevation(20f);
        mBinding.contractStep5.contractOverdue.setOnClickListener(this);

        mBinding.btnAction.setVisibility(View.VISIBLE);
        mBinding.btnAction.setText(R.string.get_mortgage);
        mBinding.btnAction.setTag(CONTRACT_OVERDUE);
        mBinding.btnAction.setOnClickListener(this);
    }

    //逾期已执行
    private void overDueExecuted() {
        mBinding.contractStep5.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep5.overdueState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep5.overdueState.setText(getString(R.string.executed));
        mBinding.contractStep5.overdueTime.setEnabled(false);
        mBinding.contractStep5.getMortgage.setEnabled(false);
        mBinding.contractStep5.overdueText.setEnabled(false);
        mBinding.contractStep5.getMortgageText.setEnabled(false);

    }

    //逾期未执行
    private void overDueNotExecutable() {
        mBinding.contractStep5.overdueState.setText(getString(R.string.not_executable));
        mBinding.contractStep5.overdueTime.setEnabled(false);
        mBinding.contractStep5.getMortgage.setEnabled(false);
        mBinding.contractStep5.overdueText.setEnabled(false);
        mBinding.contractStep5.getMortgageText.setEnabled(false);

    }

    //填充地址
    private void fillAddress(TextView targetView, String address) {
        targetView.setTextColor(getResources().getColor(R.color.c3));
        String text = address.substring(0, 8) + "..." + address.substring(address.length() - 8, address.length());
        targetView.setText(text);
        targetView.setTextSize(11);
        targetView.setOnClickListener(v -> {
            Intent intent = new Intent(ContractActivity.this, EtherScanWebActivity.class);
            intent.putExtra("ContractAddress", address);
            startActivity(intent);
        });

        //长按复制
        targetView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CommomUtil.copy(ContractActivity.this,mBinding.getRoot(),address);
                return true;
            }
        });
    }

    private BigDecimal toEther(String value) {
        return Convert.fromWei(value, Convert.Unit.ETHER);
    }

    private BigDecimal toWei(String value) {
        return Convert.toWei(value, Convert.Unit.ETHER);
    }

    // 周期转换
    private String cycleTransform(String value) {
        return Integer.valueOf(value) / 86400 + "";
    }

    // 利率转换
    private String interestTransform(String value) {
        return Double.valueOf(value) / 10 + "";
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {
        mWallet = HLWalletManager.shared().getCurrentWallet(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.contract_step_1:
                //配置合约
                contractConfig();
                break;

            case R.id.contract_step_2:
                //抵押
                if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_WAIT_MORTGAGE)) {
                    transferConfirm(CONTRACT_MORTGAGE, getString(R.string.paying_mortgage_assets),
                            needMortgageAssets,
                            mContractBean.getContractAddress());
                }
                //取回抵押
                else if (mContractBean.getContractState().equals(ContractStatus.CONTRACT_STATUS_WAIT_INVEST)) {
                    retrieveDialog();
                }
                break;

            case R.id.contract_step_3:
                transferConfirm(CONTRACT_INVEST, getString(R.string.invest),
                        toEther(mContractBean.getAmount()) + " ETH",
                        mContractBean.getContractAddress());
                break;

            case R.id.contract_step_4:
                transferConfirm(CONTRACT_REPAYMENT, getString(R.string.repayment),
                        toEther(mContractBean.getExpire()) + " ETH",
                        mContractBean.getContractAddress());
                break;

            case R.id.contract_step_5:
                transferConfirm(CONTRACT_OVERDUE, getString(R.string.get_mortgage),
                        mortgageAssets,
                        mContractBean.getContractAddress());
                break;

            case R.id.btn_action:
                switch (mBinding.btnAction.getTag().toString()) {

                    //配置合约
                    case CONTRACT_CONFIG:
                        contractConfig();
                        break;
                    //合约部署
                    case CONTRACT_DEPLOY:
                        transferConfirm(CONTRACT_DEPLOY, getString(R.string.deploy_contract), "0 ETH", "----");
                        break;
                    //转入抵押
                    case CONTRACT_MORTGAGE:
                        transferConfirm(CONTRACT_MORTGAGE, getString(R.string.paying_mortgage_assets),
                                needMortgageAssets,
                                mContractBean.getContractAddress());
                        break;
                    //取回抵押
                    case CONTRACT_RETRIEVE_MORTGAGE:
                        retrieveDialog();
                        break;
                    //投资
                    case CONTRACT_INVEST:
                        transferConfirm(CONTRACT_INVEST, getString(R.string.invest),
                                toEther(mContractBean.getAmount()) + " ETH",
                                mContractBean.getContractAddress());
                        break;
                    //还款
                    case CONTRACT_REPAYMENT:
                        transferConfirm(CONTRACT_REPAYMENT, getString(R.string.repayment),
                                toEther(mContractBean.getExpire()) + " ETH",
                                mContractBean.getContractAddress());
                        break;
                    //逾期
                    case CONTRACT_OVERDUE:
                        transferConfirm(CONTRACT_OVERDUE, getString(R.string.get_mortgage),
                                mortgageAssets,
                                mContractBean.getContractAddress());
                        break;

                }
                break;

            //免责协议阅读
            case R.id.read_confirm:
                isAgree = !isAgree;
                if (isAgree) {
                    mBinding.readConfirm.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.ic_agreement_selected),
                            null, null, null);
                    mBinding.btnAction.setEnabled(true);
                } else {
                    mBinding.readConfirm.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(
                            R.drawable.ic_agreement_unselected),
                            null, null, null);
                    mBinding.btnAction.setEnabled(false);
                }
                break;
            //免责协议详情
            case R.id.user_agreement:
                Intent intent = new Intent(this, BannerDetailAct.class);
//                if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_CHINESE);
//                } else {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_ENGLISH);
//                }
                intent.putExtra("webUrl", Constants.USER_AGREEMENT);
                startActivity(intent);
                break;
        }
    }


    //去除小数点
    private String dataProcessing(String original) {
        String value = Convert.toWei(original, Convert.Unit.ETHER) + "";
        if (value.contains(".")) {
            return value.substring(0, value.indexOf("."));
        }
        return value;
    }

    //token 小数位
    private String tokenDecimals(String tokenValue) {
//        BigDecimal decimals = new BigDecimal("10").pow(IBContractUtil.getTokenDecimals(TransferUtil.getWeb3j(), mWallet.getAddress(), mContractBean.getTokenAddress()));
        BigDecimal decimals = new BigDecimal("10").pow(18);
        BigInteger value = new BigDecimal(tokenValue).multiply(decimals).toBigInteger();
        return value.toString();
    }

    //token 整数显示
    private BigDecimal tokenInteger(String tokenValue) {
        BigDecimal decimals = new BigDecimal("10").pow(IBContractUtil.getTokenDecimals(TransferUtil.getWeb3j(), mWallet.getAddress(), mContractBean.getTokenAddress()));
        BigDecimal value = new BigDecimal(tokenValue).divide(decimals);
        return value;
    }


    //交易确认弹窗
    @SuppressLint("CheckResult")
    private void transferConfirm(String status, String transferType, String transferAmount, String transferAddress) {

//        SharedPreferences sharedPreferences = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
//        long nonce = sharedPreferences.getLong(Constants.TRANSACTION_NONCE,0);
        //当前有交易正在执行
//        if(IBContractUtil.getNonce(TransferUtil.getWeb3j(),mWallet.getAddress()) <=  nonce && !isKnow&&nonce!=0){
//            nonceDialog();
//            return;
//        }

//        long nonce1 = 0;
//        try {
//            nonce1 = IBContractUtil.getNonce(TransferUtil.getWeb3j(), mWallet.getAddress());
//        } catch (Exception e) {
//            e.printStackTrace();
////            Log.e("xxxxxx111",e.getMessage());
//            SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.nonce_error)).show();
//            return;
//        }
//        if( nonce1> nonce && !isKnow){
//            nonceDialog();
//            return;
//        }
        Flowable.just(1)
                .map(s->{
                    long nonce1 = IBContractUtil.getNonce(TransferUtil.getWeb3j(), mWallet.getAddress());
                    return nonce1;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<Long>() {
                    @Override
                    protected void success(Long nonce1) {
                        Log.e("成功",nonce1+"xxx");
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
                        long nonce = sharedPreferences.getLong(Constants.TRANSACTION_NONCE,0);
                        if( nonce1> nonce && !isKnow){
                            nonceDialog();
                            return;
                        }
                        doOnNext(status,transferType,transferAmount,transferAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        Log.e("失败",error.getMessage()+"xxx");
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.nonce_error)).show();
                    }
                });


    }

    private void doOnNext(String status, String transferType, String transferAmount, String transferAddress) {
        PopupWindow popupWindow = PopupWindowUtil.contracTransactionPopupWindow(ContractActivity.this);
        View popupView = popupWindow.getContentView();
        ((TextView) popupView.findViewById(R.id.transfer_type)).setText(transferType);
        ((TextView) popupView.findViewById(R.id.transfer_amount)).setText(transferAmount);
        if (!transferAddress.equals("----")) {
            ((TextView) popupView.findViewById(R.id.transfer_address)).setText(CommomUtil.splitWalletAddress(transferAddress));
        }

        Button btnAction = popupView.findViewById(R.id.btn_action);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                invokePasswordInput(status);
            }
        });
        if (status.equals(CONTRACT_INVEST)) {
            btnAction.setText(R.string.pay_and_invest);
        } else {
            btnAction.setText(transferType);
        }

        SeekBar seekBar = popupView.findViewById(R.id.seek_bar);
        TextView showGasPrise = popupView.findViewById(R.id.show_gas_prise);
        TextView ethAvailableAmount = popupView.findViewById(R.id.eth_available_amount);
        TextView gasAmount = popupView.findViewById(R.id.gas_amount);

        TextView tokenAvailable = popupView.findViewById(R.id.token_available);
        //抵押资产，查询代币余额
        if (status.equals(CONTRACT_MORTGAGE)) {
            getTokenBanlace(tokenAvailable, btnAction);
        }
        //获取eth 余额
        getEthBanlance(ethAvailableAmount, seekBar);
        seekBar.setEnabled(false);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //gas price
                mGasPrice = gasPrice.multiply(new BigInteger((progress) + "")).divide(new BigInteger("10")).add(gasPrice);
                showGasPrise.setText(Convert.fromWei(mGasPrice + "", Convert.Unit.GWEI) + " Gwei");
                //gas Amount
                BigDecimal gas = Convert.fromWei(mGasLimit.multiply(mGasPrice) + "", Convert.Unit.ETHER);
                gasAmount.setText(getString(R.string.eth_amount, gas));

                BigDecimal value;
                switch (status) {
                    case CONTRACT_INVEST:
                        value = gas.add(toEther(mContractBean.getAmount()));
                        break;
                    case CONTRACT_REPAYMENT:
                        value = gas.add(toEther(mContractBean.getExpire()));
                        break;

                    default:
                        value = gas;
                        break;
                }
                //ETH 余额检测
                if ((new BigDecimal(mEthBalance).compareTo(value)) == 1) {
                    ethAvailableAmount.setEnabled(true);
                } else {
                    ethAvailableAmount.setEnabled(false);
                }

                if (ethAvailableAmount.isEnabled() && tokenAvailable.isEnabled()) {
                    btnAction.setEnabled(true);
                } else {
                    btnAction.setEnabled(false);
                }
                // 同步滑块文字显示当前进度
                int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                showGasPrise.measure(spec, spec);
                int quotaWidth = showGasPrise.getMeasuredWidth();

                int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                showGasPrise.measure(spec2, spec2);
                int sbWidth = seekBar.getMeasuredWidth();
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) showGasPrise.getLayoutParams();
                params.leftMargin = (int) (((double) progress / seekBar.getMax()) * sbWidth - (double) quotaWidth * progress / seekBar.getMax());
                showGasPrise.setLayoutParams(params);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        popupWindow.showAtLocation(mBinding.getRoot(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //获取eth余额
    private void getEthBanlance(TextView ethAvailableAmount, SeekBar seekBar) {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        //保留小数点后四位
                        mEthBalance = CommomUtil.decimalTo4Point(data);
                        //ETH余额
                        ethAvailableAmount.setText(getString(R.string.available_amount, "ETH", mEthBalance));
                        //gas均价
                        getEthPrice(seekBar);
                    }

                    @Override
                    protected void failure(HLError error) {
                    }
                });
    }

    //获取token余额
    private void getTokenBanlace(TextView targetView, Button btnAction) {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getTokenBalance(TransferUtil.getWeb3j(), mWallet.getAddress(), mContractBean.getTokenAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        String tokenBalance = CommomUtil.decimalTo4Point(data);
                        targetView.setText(getString(R.string.current_token_amount, tokenymbol, tokenBalance));
                        if (Double.valueOf(tokenBalance) >= Double.valueOf(tokenInteger(mContractBean.getMortgage()).toString())) {
                            targetView.setEnabled(true);
                            btnAction.setEnabled(true);
                        } else {
                            targetView.setEnabled(false);
                            btnAction.setEnabled(false);
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                    }
                });

    }

    //获取gas 均价
    private void getEthPrice(SeekBar seekBar) {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthGasPrice(TransferUtil.getWeb3j()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<BigInteger>(ContractActivity.this, false) {
                    @Override
                    protected void success(BigInteger data) {
                        KLog.w("EthPrice = " + data);
                        if(data.compareTo(new BigInteger("1000000000")) == -1){
                            gasPrice = new BigInteger("1000000000");
                        }else {
                            gasPrice = data;
                        }
                        seekBar.setEnabled(true);
                        seekBar.setProgress(20);
                    }

                    @Override
                    protected void failure(HLError error) {
                        gasPrice = new BigInteger("1000000000");
                        seekBar.setEnabled(true);
                        seekBar.setProgress(20);
                    }
                });
    }


    //输入密码
    private void invokePasswordInput(String status) {
        CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, ContractActivity.this);
        SecurityConfigure configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
        new SecurityKeyboard(this,centerDialog.getContentView(),configure);

        inputWalletPswDialog = new InputWalletPswDialog(centerDialog, new InputWalletPswDialog.BtnListener() {
            @Override
            public void sure() {
                mPassword = inputWalletPswDialog.getWalletPsd();

                try {
                    mCredentials = Credentials.create(LWallet.decrypt(mPassword, mWallet.walletFile));
                } catch (CipherException e) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    mPassword = "";
                }
                if (mCredentials == null) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }
                switch (status) {
                    case CONTRACT_DEPLOY:
                        deployContract();
                        break;

                    case CONTRACT_MORTGAGE:
                        payAssets();
                        break;

                    case CONTRACT_RETRIEVE_MORTGAGE:
                        getMortgageAssets();
                        break;

                    case CONTRACT_INVEST:
                        investContract();
                        break;

                    case CONTRACT_REPAYMENT:
                        repayment();
                        break;

                    case CONTRACT_OVERDUE:
                        overDue();
                        break;
                }

            }
        });

    }

    //部署合约
    private void deployContract() {
        Flowable.just(1)
                .flatMap(s ->
                        //部署合约
                        IBContractUtil.deployContract(ContractActivity.this,
                                TransferUtil.getWeb3j(),
                                mCredentials, factoryContractAddress,
                                mGasPrice, mGasLimit,
                                tokenDecimals(mContractBean.getNeedMortgage()),
                                dataProcessing(mContractBean.getAmount()),
                                MortgageAssets.getTokenTypeByAddress(getApplicationContext(), mContractBean.getTokenAddress()),
                                mContractBean.getCycle(), ((int) (Double.valueOf(mContractBean.getInterest()) * 10)) + ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTxHash = data;
                        KLog.w("deploy start : " + mTxHash);
                        //添加部署消息
                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_ONE, getString(R.string.message_status_1),false,contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("error : " + error.getMessage()+Thread.currentThread().getName());
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.send_transaction_failed));
                    }
                });
    }

    //转入抵押资产
    private void payAssets() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.ERC20Transfer(ContractActivity.this,
                        TransferUtil.getWeb3j(),
                        mCredentials,
                        mContractBean.getTokenAddress(),
                        mContractBean.getContractAddress(),
                        mGasPrice, mGasLimit,
                        tokenInteger(mContractBean.getMortgage()).toString()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTxHash = data;
                        KLog.w(mTxHash);
                        //添加消息
                        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_TWO, getString(R.string.message_status_2),mBinding.cbPublish.isChecked(),contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.send_transaction_failed)).show();
                        KLog.w(error.getMessage());
                    }
                });
    }

    //取回抵押资产
    private void getMortgageAssets() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.cancelContract(ContractActivity.this,
                        TransferUtil.getWeb3j(),
                        mCredentials, mContractBean.getContractAddress(),
                        mGasPrice, mGasLimit))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTxHash = data;
                        KLog.w(mTxHash);
                        //添加消息
                        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_THREE, getString(R.string.message_status_3),false,contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w(error.getMessage());
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.send_transaction_failed));
                    }
                });
    }

    // 投资
    private void investContract() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.investSend(ContractActivity.this,
                        TransferUtil.getWeb3j(),
                        mCredentials, mContractBean.getContractAddress(),
                        mGasPrice, mGasLimit, new BigInteger(mContractBean.getAmount())))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTxHash = data;
                        KLog.w(mTxHash);

                        //市场合约添加到本地
                        if (mContractId == -2 && !DBUtil.queryContractByContractAddress(mContractBean.getContractAddress())) {
                            mContractId = DBUtil.insert(mContractBean);
                        }
                        //添加消息
                        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_FOUR, getString(R.string.message_status_4),false,contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w(error.getMessage());
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.send_transaction_failed));
                    }
                });

    }

    // 还款
    private void repayment() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.sendRepayment(ContractActivity.this,
                        TransferUtil.getWeb3j(),
                        mCredentials, mContractBean.getContractAddress(),
                        mGasPrice, mGasLimit, new BigInteger(mContractBean.getExpire())))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTxHash = data;
                        KLog.w(mTxHash);
                        //添加消息
                        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_FIVE, getString(R.string.message_status_5),false,contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.send_transaction_failed));
                        KLog.w(error.getMessage());
                    }
                });

    }

    // 逾期
    private void overDue() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.applyForAssets(ContractActivity.this,
                        TransferUtil.getWeb3j(),
                        mCredentials, mContractBean.getContractAddress(),
                        mGasPrice, mGasLimit))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTxHash = data;
                        KLog.w(mTxHash);
                        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_SIX, getString(R.string.message_status_6),false,contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.send_transaction_failed));
                        KLog.w(error.getMessage());
                    }
                });

    }

    //添加交易信息
    private void paddingTransaction(int msgStatus, String txType,boolean isPublish,String contractAddress) {
        //添加消息
        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
        saveMessageAndPush(false, mContractId, mTxHash, 1, msgStatus,isPublish,contractAddress);

        Intent intent = new Intent(ContractActivity.this, TxRecordDetailAct.class);
        intent.putExtra("txType", txType);
        intent.putExtra("txHash", mTxHash);
        intent.putExtra("navigateType", 2);
        startActivity(intent);
    }

    private void contractConfig() {
        try {
            initContractParam();
        } catch (Exception e) {
            KLog.e(e.getMessage());
            SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), e.getMessage()).show();
        }
    }

    //刷新合约配置信息
    private void refreshContractStep1() {
        mBinding.contractStep1.stateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep1.deployState.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mBinding.contractStep1.borrowingAmount.setText(getString(R.string.eth_amount, mContractBean.getAmount()));
        mBinding.contractStep1.borrowingCycle.setText(getString(R.string.day_amount, mContractBean.getCycle()));
        mBinding.contractStep1.borrowingInterest.setText(getString(R.string.rate_unit, mContractBean.getInterest()));
        mBinding.contractStep1.handlingFee.setText(getString(R.string.eth_amount, mContractBean.getServiceCharge()));
        mBinding.contractStep1.actualArrival.setText(getString(R.string.eth_amount, mContractBean.getActualAccount()));
        mBinding.contractStep1.amountText.setEnabled(false);
        mBinding.contractStep1.borrowingAmount.setEnabled(false);
        mBinding.contractStep1.cycleText.setEnabled(false);
        mBinding.contractStep1.borrowingCycle.setEnabled(false);
        mBinding.contractStep1.interestText.setEnabled(false);
        mBinding.contractStep1.borrowingInterest.setEnabled(false);
        mBinding.contractStep1.handlingFee.setEnabled(false);
        mBinding.contractStep1.handlingFeeText.setEnabled(false);
        mBinding.contractStep1.actualArrival.setEnabled(false);
        mBinding.contractStep1.actualArrivalText.setEnabled(false);

        String needMortgageAssets = new BigDecimal(mContractBean.getNeedMortgage()).setScale(0, BigDecimal.ROUND_UP).longValue() + " " + tokenymbol;
        mBinding.contractStep2.mortgageAmount.setText(needMortgageAssets);
        mBinding.contractStep3.investAmount.setText(mContractBean.getAmount() + " ETH");
        mBinding.contractStep3.dueIncome.setText(mContractBean.getExpire() + " ETH");
        mBinding.contractStep4.duePayment.setText(mContractBean.getExpire() + " ETH");
        mBinding.contractStep4.retrieveMortgage.setText(needMortgageAssets);
        mBinding.contractStep5.getMortgage.setText(needMortgageAssets);
    }

    //初始化合约配置参数
    private void initContractParam() {

        PopupWindow contractParamPopupWindow = PopupWindowUtil.contractParamPopupWindow(ContractActivity.this);

        View contractParamPopup = contractParamPopupWindow.getContentView();
        final TextView textView = contractParamPopup.findViewById(R.id.due_payment);
        //借币数量
        WheelView amountWheelView = contractParamPopup.findViewById(R.id.wheelview_amount);
        List<Integer> amoutData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            amoutData.add((i + 1));
        }
        amountWheelView.setItems(amoutData);


        amountWheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                mContractBean.setAmount(amoutData.get(index) + "");
                textView.setText(totalCalculation() + " ETH");
            }
        });
        if (Integer.valueOf(mContractBean.getAmount()) != 0) {
            amountWheelView.setItems(amoutData, amoutData.indexOf(Integer.valueOf(mContractBean.getAmount())));
        }
        //借币周期
        WheelView cycleWheelView = contractParamPopup.findViewById(R.id.wheelview_cycle);
        List<Integer> cycleData = new ArrayList<>();
        cycleData.add(1);
        cycleData.add(3);
        cycleData.add(7);
        cycleData.add(14);
        cycleData.add(21);
        cycleData.add(30);
        cycleData.add(45);
        cycleData.add(60);
        cycleData.add(90);
        cycleWheelView.setItems(cycleData);

        cycleWheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                mContractBean.setCycle(cycleData.get(index) + "");
                textView.setText(totalCalculation() + " ETH");
            }
        });
        if (Integer.valueOf(mContractBean.getCycle()) != 0) {
            cycleWheelView.setItems(cycleData, cycleData.indexOf(Integer.valueOf(mContractBean.getCycle())));
        }
        //借币利率
        WheelView rateWheelView = contractParamPopup.findViewById(R.id.wheelview_rate);
        List<Double> rateData = new ArrayList<>();
        rateData.add(0.1d);
        rateData.add(0.2d);
        rateData.add(0.3d);
        rateData.add(0.4d);
        rateData.add(0.5d);
        rateData.add(0.5d);
        for (int i = 1; i < 11; i++) {
            rateData.add(1.0d * i);
        }
        rateData.add(15d);
        rateData.add(20d);
        rateData.add(25d);
        rateData.add(30d);
        rateWheelView.setItems(rateData);

        rateWheelView.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                mContractBean.setInterest(rateData.get(index) + "");
                ;
                textView.setText(totalCalculation() + " ETH");
            }
        });
        if (Double.valueOf(mContractBean.getInterest()) != 0) {
            rateWheelView.setItems(rateData, rateData.indexOf(Double.valueOf(mContractBean.getInterest())));
        }
        final TextView mortgageAssetsAmount = contractParamPopup.findViewById(R.id.mortgage_assets_amount);
        final TextView equivalent = contractParamPopup.findViewById(R.id.equivalent);
        final TextView discountRate = contractParamPopup.findViewById(R.id.discount_rate);

        com.wx.wheelview.widget.WheelView wheelView = contractParamPopup.findViewById(R.id.wheelview);

        final int[] wheelPosition = {0};
        wheelView.setOnWheelItemSelectedListener(new com.wx.wheelview.widget.WheelView.OnWheelItemSelectedListener<TokenMortgageBean>() {
            @Override
            public void onItemSelected(int position, TokenMortgageBean data) {
                fillMortgageInfo(data, discountRate, mortgageAssetsAmount, equivalent);
                tokenymbol = data.getTokenAbbreviation();
                wheelPosition[0] = position;
            }
        });

        contractParamPopup.findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractParamPopupWindow.dismiss();
                refreshContractStep1();
                mBinding.btnAction.setText(getString(R.string.deploy_contract));
                mBinding.btnAction.setTag(CONTRACT_DEPLOY);
                mBinding.btnAction.setEnabled(true);
                mBinding.userLayout.setVisibility(View.VISIBLE);
            }
        });


        View btnCancel = contractParamPopup.findViewById(R.id.cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractParamPopupWindow.dismiss();
            }
        });
        FrameLayout mortgageLayout = contractParamPopup.findViewById(R.id.mortgage_layout);
        View btnPrevious = contractParamPopup.findViewById(R.id.btn_previous);
        View btnNext = contractParamPopup.findViewById(R.id.btn_next);
        TextView title = contractParamPopup.findViewById(R.id.title);
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText(getString(R.string.input_contract_param));
                mortgageLayout.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.GONE);
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText(getString(R.string.choose_mortgage_assets));
                mortgageLayout.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                btnPrevious.setVisibility(View.VISIBLE);
                fillMortgageInfo(MortgageAssets.getTokenMortgageList(ContractActivity.this).get(wheelPosition[0]), discountRate, mortgageAssetsAmount, equivalent);

            }
        });

        textView.setText(totalCalculation() + " ETH");

        contractParamPopupWindow.showAtLocation(mBinding.getRoot(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void fillMortgageInfo(TokenMortgageBean data, TextView discountRate, TextView mortgageAssetsAmount, TextView equivalent) {


        //折扣率
        mDiscountRate = Integer.valueOf(data.getTokenDiscountRate());
        discountRate.setText(data.getTokenDiscountRate() + "%");
        //昨日均价
        mExchangeRate = Convert.fromWei(data.getTokenPrice(), Convert.Unit.ETHER).doubleValue();

        //抵押资产数量
        mContractBean.setNeedMortgage(fourFormat.format(mDuePayment / mExchangeRate / (mDiscountRate / 100d)) + "");
        mortgageAssetsAmount.setText(mContractBean.getNeedMortgage() + " " + data.getTokenAbbreviation());
        mContractBean.setTokenAddress(data.getTokenAddress());
        //折合
        equivalent.setText(fourFormat.format(Double.valueOf(mContractBean.getNeedMortgage()) * mExchangeRate) + " ETH");
        //手续费
        mContractBean.setServiceCharge(Double.valueOf(mContractBean.getAmount()) * COMMISSION_RATE + "");
        //实际到账
        mContractBean.setActualAccount(Double.valueOf(mContractBean.getAmount()) - Double.valueOf(mContractBean.getServiceCharge()) + "");
//        //抵押资产类型
//       mContractBean.setTokenAddress(MortgageAssets.getTokenAddress(getApplicationContext(), data.getTokenNum()));
    }


    //计算到期本息还款
    private String totalCalculation() {
        int amout = Integer.valueOf(mContractBean.getAmount());
        int cycle = Integer.valueOf(mContractBean.getCycle());
        double rate = Double.valueOf(mContractBean.getInterest());

        if (amout > 0 && cycle > 0 && rate > 0) {
            mDuePayment = amout * rate * cycle / 1000d + amout;
        }
        mContractBean.setExpire(mDuePayment + "");
        return fourFormat.format(mDuePayment);
    }

    /**
     * 取回抵押弹窗
     */
    private void retrieveDialog() {
        new SureAndCancelDialog(new CenterDialog(R.layout.dialog_retrieve_mortgage_assets, this), new SureAndCancelDialog.BtnListener() {
            @Override
            public void sure() {
                transferConfirm(CONTRACT_RETRIEVE_MORTGAGE,
                        getString(R.string.retrieve_mortgage),
                        mortgageAssets,
                        mContractBean.getContractAddress());
            }
        });
    }

    /**
     * nonce
     */
    private void nonceDialog() {
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, ContractActivity.this), null, getString(R.string.nonce_hint), getString(R.string.nonce_warning), Gravity.LEFT);
        isKnow = true;
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
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.contract_publish_success)).show();
                            }
                            else if (data.getCode().equals(Constants.REQUEST_CONTRACT_EXISTED)){
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.contract_contract_existed)).show();
                            }
                            else {
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.contract_publish_fail)).show();
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
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.contract_publish_success)).show();
                            }
                            else if (data.getCode().equals(Constants.REQUEST_CONTRACT_EXISTED)){
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.contract_contract_existed)).show();
                            }
                            else {
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.contract_publish_fail)).show();
                            }
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
                        }
                    });
        }

    }

}

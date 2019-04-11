package com.lianer.core.contract;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lianer.core.SmartContract.ETH.IBContract;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Utils;
import com.lianer.core.R;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.contract.model.ContractDetailModel;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityConstractDetailBinding;
import com.lianer.core.dialog.ProjectProfileDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.TransferUtil;

import org.web3j.utils.Convert;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;

/**
 * 合约详情
 */
public class ContractDetailAct extends BaseActivity {

    ActivityConstractDetailBinding constractDetailBinding;
    ContractResponse contractResponse;
    ContractDetailModel contractDetailModel = new ContractDetailModel(this);
    String currentWalletAddress;
    int launchFlag;//跳转入口标识  0：其他 1：我的合约

    @Override
    protected void initViews() {
        initTitleBar();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        constractDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_constract_detail);
        constractDetailBinding.titlebar.showLeftDrawable();
        constractDetailBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
//                Intent intent = new Intent(ContractDetailAct.this, InvestActivity.class);
//                intent.putExtra(Constants.CONTRACT_RESPONSE, contractResponse);
//                startActivity(intent);
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

    @Override
    protected void initData() {
        Intent intent = getIntent();
        contractResponse = (ContractResponse) intent.getSerializableExtra(CONTRACT_RESPONSE);
        launchFlag = intent.getIntExtra("launchFlag", 0);
        setWidgetData();
    }

    /**
     * 获取合约信息
     * @param contractAddress 合约地址
     */
    private void getContract(String contractAddress) {
        try {
            KLog.w(new Gson().toJson(contractResponse));
            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.readOnlyIBContract(TransferUtil.getWeb3j(),HLWalletManager.shared().getCurrentWallet(this).getAddress(),contractAddress))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<IBContract>(ContractDetailAct.this,true) {
                        @Override
                        protected void success(IBContract contract) {

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

    /**
     * 设置页面数据
     */
    private void setWidgetData() {
        if (HLWalletManager.shared().getCurrentWallet(this) != null) {
            currentWalletAddress = HLWalletManager.shared().getCurrentWallet(this).getAddress();
        }
        //设置页面顶部以及中间的控件数据
        setHearAndCenterData();

        setBottomData();
    }

    /**
     * 设置顶部和中间的数据
     */
    private void setHearAndCenterData() {
        //利率
        double interestRate = contractResponse.getInterestRate()/10d;
        String interestRateStr = String.valueOf(interestRate);
        constractDetailBinding.includeHead.interestRate.setText(interestRate >= 1 ? interestRateStr.substring(0,interestRateStr.indexOf(".")) : interestRateStr);
        //周期
        constractDetailBinding.includeHead.timeLimit.setText(contractResponse.getTimeLimit() + "");
        //合约地址
        constractDetailBinding.includeHead.contractAddress.setText(contractResponse.getContractAddress());
        constractDetailBinding.includeHead.contractAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(ContractDetailAct.this, true, contractResponse.getContractAddress()));
        //到期本息
        if (!TextUtils.isEmpty(contractResponse.getPrincipalAndInterest())) {
            constractDetailBinding.includeHead.principalInterest.setText(String.format("%s ETH", CommomUtil.decimalTo4Point(Convert.fromWei(contractResponse.getPrincipalAndInterest(), Convert.Unit.ETHER).toString())));
        } else {
            constractDetailBinding.includeHead.principalInterest.setText(String.format("%s ETH", "0.00"));
        }
        if (currentWalletAddress == null) {
            //已投数量  到期本息
            constractDetailBinding.includeHead.amountInvested.setText(getString(R.string.amount_invested));
            constractDetailBinding.includeHead.expireEth.setText(R.string.daoqibenxi);
            //已投资产数量
            constractDetailBinding.includeHead.investmentAmount.setText(String.format("%s ETH", Utils.switchTo4Decimal(Convert.fromWei(contractResponse.getInvestmentAmount(), Convert.Unit.ETHER).toString())));
        } else {
            if (!contractResponse.getBorrowAddress().equalsIgnoreCase(currentWalletAddress)) {
                //已投数量  到期本息
                if (launchFlag == 0) {
                    constractDetailBinding.includeHead.amountInvested.setText(getString(R.string.number_of_available));
                } else {
                    constractDetailBinding.includeHead.amountInvested.setText(getString(R.string.amount_invested));
                }
                constractDetailBinding.includeHead.expireEth.setText(R.string.daoqibenxi);
                //已投资产数量
                constractDetailBinding.includeHead.investmentAmount.setText(String.format("%s ETH", Convert.fromWei(contractResponse.getInvestmentAmount(), Convert.Unit.ETHER).toString()));
            } else {
                //借币数量  到期应还本息
                constractDetailBinding.includeHead.amountInvested.setText(getString(R.string.borrow_amount));
                constractDetailBinding.includeHead.expireEth.setText(R.string.due_payment);
                //借币数量
                constractDetailBinding.includeHead.investmentAmount.setText(String.format("%s ETH", Convert.fromWei(contractResponse.getBorrowAssetsAmount(), Convert.Unit.ETHER).toString()));
            }
        }

        // 代币图标
        Glide.with(this).load(MortgageAssets.getTokenLogo(this,contractResponse.getMortgageAssetsType())).into( constractDetailBinding.includeCenter.tokenLogo);
        //代币名称
        constractDetailBinding.includeCenter.tokenType.setText(MortgageAssets.getTokenSymbol(getApplicationContext(),contractResponse.getMortgageAssetsType()));
        //抵押数量
        String mortageAssetsAmount = CommomUtil.decimalTo4Point(Convert.fromWei(contractResponse.getMortgageAssetsAmount(), Convert.Unit.ETHER).toString());
        constractDetailBinding.includeCenter.mortgageAssetsAmount.setText(String.format(getString(R.string.mortgageAmount), mortageAssetsAmount));
        //折合
        String priecToEth = CommomUtil.decimalTo4Point(Convert.fromWei(contractResponse.getPriceToEth(), Convert.Unit.ETHER).toString());
        constractDetailBinding.includeCenter.priceToEth.setText(String.format("%s ETH", priecToEth));
        //创建时均价
        constractDetailBinding.includeCenter.tokenToPrice.setText(String.format("%s/ETH", MortgageAssets.getTokenSymbol(getApplicationContext(),contractResponse.getMortgageAssetsType())));
        constractDetailBinding.includeCenter.mortgageAssetsPrice.setText(Utils.switchTo4Decimal(Convert.fromWei(contractResponse.getMortgageAssetsPrice(), Convert.Unit.ETHER).toString()));
        //折扣率
        constractDetailBinding.includeCenter.discountRate.setText(String.format("%s%%", contractResponse.getDiscountRate()));

        constractDetailBinding.includeCenter.projectProfile.setOnClickListener(v -> {
            new ProjectProfileDialog(constractDetailBinding.getRoot(), this, new CenterDialog(R.layout.dlg_asset_profile, ContractDetailAct.this), contractResponse, null);
        });
    }

    /**
     * 设置底部数据
     */
    private void setBottomData() {
        //判断是投资还是借币
        if (!contractResponse.getBorrowAddress().equalsIgnoreCase(currentWalletAddress)) {//投资
            contractDetailModel.loadInvestView(contractResponse, constractDetailBinding.llContractDetail);
        } else {//借币
            contractDetailModel.loadBorrowView(this, contractResponse, constractDetailBinding.llContractDetail, constractDetailBinding.titlebar);
        }
    }

}

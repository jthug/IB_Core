package com.lianer.core.borrow;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.google.gson.Gson;
import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.ETH.IBDataContract;
import com.lianer.core.SmartContract.ETH.IBFactoryContract;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityContractDeployBinding;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;


public class ContractDeployActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ContractDeployActivity";
    private ActivityContractDeployBinding mBinding;
//    /**
//     * 借币数量
//     */
//    private String mAmount;
//    /**
//     * 借币周期
//     */
//    private String mCycle;
//    /**
//     * 借币利息
//     */
//    private String mInterest;
//    /**
//     * 到期应还本息
//     */
//    private String mDuePayment;
//    /**
//     * 抵押资产
//     */
//    private String mAssets;
//    /**
//     * 手续费
//     */
//    private String mHandlingFee;
//    /**
//     * 实际到账
//     */
//    private String mActualArrival;

    private BigInteger mGasLimit = BigInteger.valueOf(1000000);
    private BigInteger mGasPrice;
    private String mETHBalance;
    private HLWallet mWallet;
    private InputWalletPswDialog inputWalletPswDialog;
    private String mPassword = "";
    private Credentials mCredentials;
    private ContractResponse mContractInfo;

    private String mTxHash;
    //TODO 数据合约地址 0xa9dAA46b490D510a51B3CF7252f6702513E548C3  0x62b5fdb895ff301c6634ab15585bb549beccaab2
    private String DATA_CONTRACT_ADDRESS = "0x62b5fdb895ff301c6634ab15585bb549beccaab2";
    private SecurityConfigure configure;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contract_deploy);
        mBinding.titlebar.showLeftDrawable();
        configure = new SecurityConfigure().setDefaultKeyboardType(KeyboardType.NUMBER);
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

        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //gas不能为0
                if (progress == 0) {
                    return;
                }
                updateSeekBar(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBinding.deployContract.setOnClickListener(this);
        mBinding.helpInfo.setOnClickListener(this);
        mBinding.seekBar.setEnabled(false);
    }


    @Override
    protected void initData() {
        mContractInfo = (ContractResponse) getIntent().getSerializableExtra(CONTRACT_RESPONSE);
        mWallet = HLWalletManager.shared().getCurrentWallet(this);
        if(mContractInfo == null){
            return;
        }
        //借币数量
        mBinding.borrowingAmount.setText(getString(R.string.eth_amount, Convert.fromWei(mContractInfo.getBorrowAssetsAmount(), Convert.Unit.ETHER)));
        //借币周期
        mBinding.borrowingCycle.setText(getString(R.string.day_amount, mContractInfo.getTimeLimit() + ""));
        //借币利息
        mBinding.borrowingInterest.setText(mContractInfo.getInterestRate() / 10d + " ‰");
        //到期本息
        mBinding.duePayment.setText(getString(R.string.eth_amount, Convert.fromWei(mContractInfo.getPrincipalAndInterest(), Convert.Unit.ETHER)));
        //抵押代币数量
        mBinding.assetsAmount.setText(CommomUtil.decimalTo4Point(Convert.fromWei(mContractInfo.getMortgageAssetsAmount(), Convert.Unit.ETHER)+"")+ " " + MortgageAssets.getTokenSymbol(getApplicationContext(),mContractInfo.getMortgageAssetsType()));
        //手续费
        mBinding.handlingFee.setText(getString(R.string.eth_amount, Convert.fromWei(mContractInfo.getPoundage(), Convert.Unit.ETHER)));
        //实际到账
        mBinding.actualArrival.setText(getString(R.string.eth_amount, Convert.fromWei(mContractInfo.getActualAmount(), Convert.Unit.ETHER)));

        getEthBanlance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_info:
                gasWarnDlgShow();
                break;
            case R.id.deploy_contract:

                if (!mPassword.isEmpty()) {
                    invokeDeployContract();
                } else {
                    CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, ContractDeployActivity.this);
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
                            invokeDeployContract();
                        }
                    });
                }
                break;
        }
    }

    private void invokeDeployContract() {
        try {

            Flowable.just(1)
                    .flatMap(s ->
//                            IBContractUtil.deployContract(TransferUtil.getWeb3j(), mCredentials,
//                            mGasPrice, mGasLimit,
//                            mContractInfo.getMortgageAssetsAmount(),
//                            mContractInfo.getBorrowAssetsAmount(),
//                            mContractInfo.getMortgageAssetsType(),
//                            mContractInfo.getTimeLimit()+"",mContractInfo.getInterestRate()+""))

                            //加载数据合约
                            IBContractUtil.loadIBDateContract(TransferUtil.getWeb3j(), mCredentials, mGasPrice, mGasLimit, DATA_CONTRACT_ADDRESS))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<IBDataContract>(ContractDeployActivity.this, true) {
                        @Override
                        protected void success(IBDataContract contract) {

                            //获取工厂合约地址
                            String factoryAddress = null;
                            try {
//                                factoryAddress = contract.factoryAddress().sendAsync().get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String finalFactoryAddress = factoryAddress;
                            Flowable.just(1)
                                    .flatMap(s ->
                                            //加载工厂合约
                                            IBContractUtil.loadIBFactoryContract(TransferUtil.getWeb3j(), mCredentials, mGasPrice, mGasLimit, finalFactoryAddress))
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new HLSubscriber<IBFactoryContract>(ContractDeployActivity.this, true) {
                                        @Override
                                        protected void success(IBFactoryContract contract) {

                                            Flowable.just(1)
                                                    .flatMap(s ->
                                                            //部署合约
                                                            IBContractUtil.deployContract(TransferUtil.getWeb3j(), contract,
                                                                    mGasPrice, mGasLimit,
                                                                    mContractInfo.getMortgageAssetsAmount(),
                                                                    mContractInfo.getBorrowAssetsAmount(),
                                                                    mContractInfo.getMortgageAssetsType(),
                                                                    mContractInfo.getTimeLimit() + "", mContractInfo.getInterestRate() + ""))
                                                    .subscribeOn(Schedulers.newThread())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new HLSubscriber<String>(ContractDeployActivity.this, true) {
                                                        @Override
                                                        protected void success(String data) {
                                                            mTxHash = data;
                                                            Log.w(TAG,"deploy start : " + mTxHash);
                                                            initResponseData(initRequestBody());
                                                        }

                                                        @Override
                                                        protected void failure(HLError error) {

                                                        }
                                                    });
                                        }

                                        @Override
                                        protected void failure(HLError error) {

                                        }
                                    });
                        }

                        @Override
                        protected void failure(HLError error) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载请求参数
     */
    private String initRequestBody() {
        ContractResponse creatContract = mContractInfo;

        // 合约hash地址
        creatContract.setContractHash(mTxHash);
        // 借款人地址
        creatContract.setBorrowAddress(mWallet.getAddress());
        // 合约状态
        creatContract.setContractStatus(ContractStatus.CONTRACT_STSTUS_0);
        // 合约创建时间
        creatContract.setContractCreateDate(System.currentTimeMillis() / 1000 + "");
        // TODO Token type
        creatContract.setMortgageAssetsType(mContractInfo.getMortgageAssetsType());
        //合约地址暂无
        creatContract.setContractAddress(null);

        Gson gson = new Gson();
        KLog.w("部署： " + gson.toJson(creatContract));
        return gson.toJson(creatContract);
    }


    /**
     * 获取响应数据
     */
    private void initResponseData(String jsonRequestParams) {
        HttpUtil.createContract(jsonRequestParams, new HttpUtil.ContractStateCallBack() {
            @Override
            public void onSuccess(ContractResponse responses) {
                if (responses != null) {
                    KLog.w("onSuccess", responses.toString());
                    Intent intent = new Intent(ContractDeployActivity.this, ContractStateActivity.class);
                    intent.putExtra(CONTRACT_RESPONSE, responses);
                    startActivity(intent);
                } else {
                    KLog.w("fail", "responses = null");
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                KLog.w("onFailure", errorMsg.toString());
            }
        });
    }


    private void getEthBanlance() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(ContractDeployActivity.this, false) {
                    @Override
                    protected void success(String data) {
                        mETHBalance = CommomUtil.decimalTo4Point(data);
                        //ETH余额
                        mBinding.ethAvailableAmount.setText(getString(R.string.available_amount, "ETH", mETHBalance));
                        updateSeekBar(30);

                    }

                    @Override
                    protected void failure(HLError error) {
                    }
                });
    }


    /**
     * gas费用说明弹窗
     */
    private void gasWarnDlgShow() {
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, ContractDeployActivity.this), null, getString(R.string.gas_info), getString(R.string.gas_description), Gravity.LEFT);

    }

    private void updateSeekBar(int progress) {
        //gas price
        mGasPrice = new BigInteger(progress + "00000000");
        mBinding.showGasPrise.setText(progress / 10d + " Gwei");
        //gas Amount
        BigDecimal gas = Convert.fromWei(BigDecimal.valueOf(progress * mGasLimit.intValue() * Math.pow(10, 9)), Convert.Unit.ETHER);
        mBinding.gasAmount.setText(getString(R.string.eth_amount, gas));
        //ETH 余额检测
        checkETHBalance(gas);
        // 同步滑块文字显示当前进度
        synchronizeSeekText(progress);
        mBinding.seekBar.setEnabled(true);
    }

    /**
     * 滑块文字显示当前进度
     */
    private void synchronizeSeekText(int progress) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mBinding.showGasPrise.measure(spec, spec);
        int quotaWidth = mBinding.showGasPrise.getMeasuredWidth();

        int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mBinding.showGasPrise.measure(spec2, spec2);
        int sbWidth = mBinding.seekBar.getMeasuredWidth();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mBinding.showGasPrise.getLayoutParams();
        params.leftMargin = (int) (((double) progress / mBinding.seekBar.getMax()) * sbWidth - (double) quotaWidth * progress / mBinding.seekBar.getMax());
        mBinding.showGasPrise.setLayoutParams(params);
    }

    /**
     * ETH余额不足，红字提醒
     */
    private void checkETHBalance(BigDecimal gas) {
        if (Double.valueOf(mETHBalance) < gas.doubleValue()) {
            mBinding.ethAvailableAmount.setTextColor(getResources().getColor(R.color.clr_F5222D));
            mBinding.deployContract.setBackgroundResource(R.drawable.gray_oval_btn);
            mBinding.deployContract.setTextColor(getResources().getColor(R.color.clr_666666));
            mBinding.deployContract.setEnabled(false);
        } else {
            mBinding.ethAvailableAmount.setTextColor(getResources().getColor(R.color.clr_059EFF));
            mBinding.deployContract.setBackgroundResource(R.drawable.gradient_oval_btn);
            mBinding.deployContract.setTextColor(getResources().getColor(R.color.clr_F5F5F5));
            mBinding.deployContract.setEnabled(true);
        }
    }


}

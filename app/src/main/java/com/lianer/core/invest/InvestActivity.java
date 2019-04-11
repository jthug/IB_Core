package com.lianer.core.invest;

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
import com.lianer.core.R;
import com.lianer.core.SmartContract.ETH.IBContract;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.borrow.ContractStateActivity;
import com.lianer.core.contract.bean.ContractInvestResponse;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityInvestBinding;
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
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;
import static com.lianer.core.config.ContractStatus.CONTRACT_STSTUS_13;

/**
 * 投资
 *
 * @author allison
 */
public class InvestActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "InvestActivity";
    ActivityInvestBinding mBinding;
    private BigInteger mGasLimit = BigInteger.valueOf(100000);
    private BigInteger mGasPrice;
    private String mETHBalance;
    private HLWallet mWallet;
    private InputWalletPswDialog inputWalletPswDialog;
    private String mPassword ="";
    private Credentials mCredentials;
    private String mTxHash;
    private ContractResponse mContractInfo;
    private BigDecimal mAmount;
    private SecurityConfigure configure;


    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_invest);
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
        configure = new SecurityConfigure().setDefaultKeyboardType(KeyboardType.NUMBER);
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //gas不能为0
                if(progress == 0){
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
        mBinding.btnPay.setOnClickListener(this);
        mBinding.helpInfo.setOnClickListener(this);
        mBinding.seekBar.setEnabled(false);
    }

    @Override
    protected void initData() {
        mContractInfo = (ContractResponse) getIntent().getSerializableExtra(CONTRACT_RESPONSE);
        KLog.w(mContractInfo.toString());
        mWallet = HLWalletManager.shared().getCurrentWallet(this);

        //投资金额
        mAmount = Convert.fromWei(mContractInfo.getInvestmentAmount(), Convert.Unit.ETHER);
        mBinding.investAmount.setText(mAmount+" ETH");

        getEthBanlance();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.help_info:
                gasWarnDlgShow();
                break;
            case R.id.btn_pay:

                if(!mPassword.isEmpty()){
                    invokePayAssets();
                }else{
                    CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, InvestActivity.this);
                    SecurityKeyboard securityKeyboard = new SecurityKeyboard(this, centerDialog.getContentView(), configure);
                    inputWalletPswDialog = new InputWalletPswDialog(centerDialog, new InputWalletPswDialog.BtnListener() {
                        @Override
                        public void sure() {
                            mPassword = inputWalletPswDialog.getWalletPsd();

                            try {
                                mCredentials = Credentials.create(LWallet.decrypt(mPassword, mWallet.walletFile));
                            } catch (CipherException e) {
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.current_psd_error)).show();
                                mPassword = "";

                            }
                            invokePayAssets();
                        }
                    });
                }
                break;
        }
    }



    private void invokePayAssets(){
        try {
            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.loadIBContract(TransferUtil.getWeb3j(),mCredentials,mContractInfo.getContractAddress()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<IBContract>(InvestActivity.this,true) {
                        @Override
                        protected void success(IBContract contract) {
                            try {
                                //设置GasPrice
                                contract.setGasPrice(mGasPrice);
//                                BigInteger investWei = new BigInteger(mContractInfo.getInvestmentAmount());
                                BigInteger investWei = new BigInteger(IBContractUtil.getIBContractStatus(contract).getAmount());
//                                    Flowable.just(1)
//                                            .flatMap(s -> IBContractUtil.investETH(contract, investWei))
//                                            .subscribeOn(Schedulers.newThread())
//                                            .observeOn(AndroidSchedulers.mainThread())
//                                            .subscribe(new HLSubscriber<String>(InvestActivity.this, true) {
//                                                @Override
//                                                protected void success(String data) {
//                                                    mTxHash = data;
//                                                    KLog.w(mTxHash);
//                                                    // 合约状态
//                                                    mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_13);
//                                                    // hash地址
//                                                    mContractInfo.setContractHash(mTxHash);
//                                                    // 投资交易hash地址
//                                                    mContractInfo.setInvestmentHash(mTxHash);
//                                                    // 投资状态
//                                                    mContractInfo.setInvestmentStatus(ContractStatus.CONTRACT_STSTUS_13);
//                                                    // 投资人地址
//                                                    mContractInfo.setInvestmentAddress(mWallet.getAddress());
//
//                                                    createInvestInfo();
//
////                                                    updateContractRequst(updateRequestBody());
//
//                                                }
//
//                                                @Override
//                                                protected void failure(HLError error) {
//                                                    KLog.w( error.getMessage());
//                                                }
//                                            });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected void failure(HLError error) {
                            Log.w(TAG,error.getMessage());
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增投资人信息
     */
    private void createInvestInfo() {
        ContractInvestResponse contractInvestResponse = new ContractInvestResponse();
        contractInvestResponse.setContractAddress(mContractInfo.getContractAddress());
        contractInvestResponse.setBorrowerAddress(mContractInfo.getInvestmentAddress());
        contractInvestResponse.setBorrowerAmount(mContractInfo.getInvestmentAmount());
        contractInvestResponse.setBorrowerHash(mTxHash);
        contractInvestResponse.setDatetime(String.valueOf(System.currentTimeMillis()/1000));
        contractInvestResponse.setStatus(CONTRACT_STSTUS_13);
        KLog.i("投资人信息json字符串：" + new Gson().toJson(contractInvestResponse));
        HttpUtil.createInvest(new Gson().toJson(contractInvestResponse))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ContractInvestResponse>() {
                    @Override
                    public void onNext(ContractInvestResponse contractInvestResponse) {
                        KLog.i("新增投资人成功");
                        Intent intent = new Intent(InvestActivity.this, ContractStateActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE, mContractInfo);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.i("新增投资人失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 更新合约状态的请求参数
     */
    private String updateRequestBody() {
        ContractResponse data = mContractInfo;

        // 合约时间置空
        data.setContractCreateDate(null);
        Gson gson = new Gson();
        Log.w(TAG,gson.toJson(data));
        return gson.toJson(data);
    }


    /**
     * 更新合约业务状态
     */
    private void updateContractRequst(String jsonRequestParams) {
        HttpUtil.updateContractState(jsonRequestParams, new HttpUtil.updateContractCallBack() {
            @Override
            public void onSuccess(Object responses) {

                if (responses != null) {
                    KLog.i("修改合约返回的数据" + responses.toString());
                    if (responses.toString().equals("1")) {
                        KLog.i("修改成功");
                        Intent intent = new Intent(InvestActivity.this, ContractStateActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE, mContractInfo);
                        startActivity(intent);
                    }
                } else {
                    KLog.i("数据为空");

                }

            }

            @Override
            public void onFailure(String errorMsg) {
                KLog.i("修改失败");
            }
        });
    }

    /**
     * gas费用说明弹窗
     */
    private void gasWarnDlgShow() {
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, InvestActivity.this), null, getString(R.string.gas_info), getString(R.string.gas_description), Gravity.LEFT);

    }

    private void getEthBanlance(){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(),mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(InvestActivity.this,true) {
                    @Override
                    protected void success(String data) {
                        //ETH余额
                        mETHBalance = CommomUtil.decimalTo4Point(data);
                        mBinding.ethAvailableAmount.setText(getString(R.string.available_amount,"ETH",mETHBalance));
                        updateSeekBar(30);
                    }

                    @Override
                    protected void failure(HLError error) {
                    }
                });
    }



    private  void updateSeekBar(int progress){
        //gas price
        mGasPrice = new BigInteger(progress+"00000000");
        mBinding.showGasPrise.setText(progress/10d+" Gwei");
        //gas Amount
        BigDecimal gas = Convert.fromWei(BigDecimal.valueOf(progress * mGasLimit.intValue() * Math.pow(10,9)), Convert.Unit.ETHER);
        mBinding.gasAmount.setText(gas+" ETH");
        //ETH 余额检测
        checkETHBalance(gas);
        // 同步滑块文字显示当前进度
        synchronizeSeekText(progress);

        BigDecimal value = gas.add(mAmount);
        mBinding.actualPayment.setText(value+" ETH");
        mBinding.seekBar.setEnabled(true);
    }

    /**
     * 滑块文字显示当前进度
     */
    private void synchronizeSeekText(int progress){
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mBinding.showGasPrise.measure(spec, spec);
        int quotaWidth = mBinding.showGasPrise.getMeasuredWidth();

        int spec2 = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mBinding.showGasPrise.measure(spec2, spec2);
        int sbWidth = mBinding.seekBar.getMeasuredWidth();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)  mBinding.showGasPrise.getLayoutParams();
        params.leftMargin = (int) (((double) progress / mBinding.seekBar.getMax()) * sbWidth - (double) quotaWidth * progress / mBinding.seekBar.getMax());
        mBinding.showGasPrise.setLayoutParams(params);
    }

    /**
     *  ETH余额不足，红字提醒
     */
    private void checkETHBalance(BigDecimal gas){
        if(Double.valueOf(mETHBalance) < (gas.doubleValue()+mAmount.doubleValue())){
            mBinding.ethAvailableAmount.setTextColor(getResources().getColor(R.color.clr_F5222D));
            mBinding.btnPay.setBackgroundResource(R.drawable.gray_oval_btn);
            mBinding.btnPay.setTextColor(getResources().getColor(R.color.clr_666666));
            mBinding.btnPay.setEnabled(false);
        }else{
            mBinding.ethAvailableAmount.setTextColor(getResources().getColor(R.color.clr_059EFF));
            mBinding.btnPay.setBackgroundResource(R.drawable.gradient_oval_btn);
            mBinding.btnPay.setTextColor(getResources().getColor(R.color.clr_F5F5F5));
            mBinding.btnPay.setEnabled(true);
        }
    }



}

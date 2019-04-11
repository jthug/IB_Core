package com.lianer.core.contract;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.google.gson.Gson;
import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.ETH.IBContract;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.borrow.ContractStateActivity;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityDisbandExplainBinding;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.etherscan.EtherScanWebActivity;
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

/**
 * 解散合约
 */
public class DisbandContractAct extends BaseActivity implements View.OnClickListener {

    ActivityDisbandExplainBinding mBinding;
    private BigInteger mGasLimit = BigInteger.valueOf(1000000);
    private BigInteger mGasPrice;
    private String mETHBalance;
    private HLWallet mWallet;
    private InputWalletPswDialog inputWalletPswDialog;
    private String mPassword = "";
    private Credentials mCredentials;
    private String mTxHash;
    private ContractResponse mContractInfo;
    private SecurityConfigure configure;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_disband_explain);
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
        mBinding.btnDisband.setOnClickListener(this);
        mBinding.helpInfo.setOnClickListener(this);
        mBinding.seekBar.setEnabled(false);
    }

    @Override
    protected void initData() {
        mContractInfo = (ContractResponse) getIntent().getSerializableExtra(CONTRACT_RESPONSE);
        KLog.w(mContractInfo.toString());
        mWallet = HLWalletManager.shared().getCurrentWallet(this);

        mBinding.mortgageAssetsAmount.setText(CommomUtil.decimalTo4Point(Convert.fromWei(mContractInfo.getMortgageAssetsAmount(),Convert.Unit.ETHER)+""));
        mBinding.mortgageAssetsType.setText(MortgageAssets.getTokenSymbol(getApplicationContext(),mContractInfo.getMortgageAssetsType()));
        mBinding.contractAddress.setText(mContractInfo.getContractAddress());
        mBinding.contractAddress.setOnClickListener(this);
        getEthBanlance();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.help_info:
                gasWarnDlgShow();
                break;
            case R.id.contract_address:
                Intent intent = new Intent(this, EtherScanWebActivity.class);
                intent.putExtra("ContractAddress",  mContractInfo.getContractAddress());
                startActivity(intent);
                break;
            case R.id.btn_disband:

                if (!mPassword.isEmpty()) {
                    invokeDisband();
                } else {
                    CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, DisbandContractAct.this);
                    SecurityKeyboard securityKeyboard = new SecurityKeyboard(this, centerDialog.getContentView(), configure);
                    inputWalletPswDialog = new InputWalletPswDialog(centerDialog, new InputWalletPswDialog.BtnListener() {
                        @Override
                        public void sure() {
                            mPassword = inputWalletPswDialog.getWalletPsd();

                            try {
                                mCredentials = Credentials.create(LWallet.decrypt(mPassword, mWallet.walletFile));
                            } catch (CipherException e) {
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error))
                                        .show();
                                mPassword = "";

                            }
                            invokeDisband();
                        }
                    });
                }
                break;
        }
    }


    private void invokeDisband() {
        try {
            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.loadIBContract(TransferUtil.getWeb3j(), mCredentials, mContractInfo.getContractAddress()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<IBContract>(DisbandContractAct.this, true) {
                        @Override
                        protected void success(IBContract contract) {
//                            Flowable.just(1)
//                                    .flatMap(s -> IBContractUtil.cancelContract(contract.getContractAddress()))
//                                    .subscribeOn(Schedulers.newThread())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(new HLSubscriber<String>(DisbandContractAct.this, true) {
//                                        @Override
//                                        protected void success(String data) {
//                                            mTxHash = data;
//                                            KLog.w(mTxHash);
//                                            mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_9);
//                                            mContractInfo.setContractHash(mTxHash);
//                                            updateContractRequst(updateRequestBody());
//                                        }
//                                        @Override
//                                        protected void failure(HLError error) {
//                                            KLog.w(error.getMessage());
//                                        }
//                                    });
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
     * 更新合约状态的请求参数
     */
    private String updateRequestBody() {
        ContractResponse data = mContractInfo;
        // 合约时间置空
        data.setContractCreateDate(null);
        Gson gson = new Gson();
        KLog.w(gson.toJson(data));
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
                        Intent intent = new Intent(DisbandContractAct.this, ContractStateActivity.class);
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
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, DisbandContractAct.this), null, getString(R.string.gas_info), getString(R.string.gas_description), Gravity.LEFT);

    }


    private void getEthBanlance() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(DisbandContractAct.this, true) {
                    @Override
                    protected void success(String data) {
                        //ETH余额
                        mETHBalance = CommomUtil.decimalTo4Point(data);
                        mBinding.ethAvailableAmount.setText(getString(R.string.available_amount, "ETH", mETHBalance));
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
        mBinding.gasAmount.setText(gas+"ETH");
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
            mBinding.btnDisband.setBackgroundResource(R.drawable.gray_oval_btn);
            mBinding.btnDisband.setTextColor(getResources().getColor(R.color.clr_666666));
            mBinding.btnDisband.setEnabled(false);
        } else {
            mBinding.ethAvailableAmount.setTextColor(getResources().getColor(R.color.clr_059EFF));
            mBinding.btnDisband.setBackgroundResource(R.drawable.gradient_oval_btn);
            mBinding.btnDisband.setTextColor(getResources().getColor(R.color.clr_F5F5F5));
            mBinding.btnDisband.setEnabled(true);
        }
    }
}

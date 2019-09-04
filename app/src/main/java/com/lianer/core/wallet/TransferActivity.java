package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.app.Constants;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityTransferBinding;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.PopupWindowUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.zxinglibrary.android.CaptureActivity;
import com.lianer.core.zxinglibrary.common.Constant;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;


import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class TransferActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTransferBinding mBinding;
    private String mETHBalance, mTokenBalance = "0";
    private HLWallet mWallet;
    private boolean isGasEnough = false;
    private boolean isTokenEnough = false;
    private String mAddress;
    private BigInteger mGasLimit = BigInteger.valueOf(200000);
    private BigInteger mGasPrice;
    private String mAmount = "0";

    private InputWalletPswDialog inputWalletPswDialog;
    private Credentials mCredentials;
    private String mTokenAddress;
    private String mTokenName;
    private BigDecimal gas;
    private BigInteger gasPrice;
    //已有交易正在打包中
    boolean isKnow;
    private SecurityConfigure configure;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transfer);
        mBinding.titlebar.showLeftDrawable();
        mBinding.titlebar.setRightDrawable(R.drawable.scan_selector);
        mBinding.titlebar.setRightWidgetVisible(0);
        configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
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

                AndPermission.with(TransferActivity.this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(TransferActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, 111);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.request_permissions)).show();
                            }
                        }).start();
            }
        });
//        mBinding.scanCode.setOnClickListener(this);
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateSeekBar(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBinding.transferAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable s) {
                mAmount = mBinding.transferAmount.getText().toString().trim();
                //Token余额检测
                checkTokenBalance();

            }
        });
        mBinding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
//                long nonce = sharedPreferences.getLong(Constants.TRANSACTION_NONCE,0);
////                if(IBContractUtil.getNonce(TransferUtil.getWeb3j(),mWallet.getAddress()) <=  nonce && !isKnow){
////                    nonceDialog();
////                    return;
////                }
//                long nonce1 = 0;
//                try {
//                    nonce1 = IBContractUtil.getNonce(TransferUtil.getWeb3j(), mWallet.getAddress());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.nonce_error)).show();
//                    return;
//                }
//                if(nonce1> nonce && !isKnow){
//                    nonceDialog();
//                    return;
//                }
//                transferConfirm();

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
                                transferConfirm();
                            }

                            @Override
                            protected void failure(HLError error) {
                                Log.e("失败",error.getMessage()+"xxx");
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.nonce_error)).show();
                            }
                        });
            }
        });
        mBinding.seekBar.setEnabled(false);
        mBinding.helpInfo.setOnClickListener(this);
    }

    //发起交易
    private void transfer() {
        CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, TransferActivity.this);
        SecurityKeyboard securityKeyboard = new SecurityKeyboard(this, centerDialog.getContentView(), configure);
        inputWalletPswDialog = new InputWalletPswDialog(centerDialog, () -> {
              String password = inputWalletPswDialog.getWalletPsd();

                try {
                    mCredentials = Credentials.create(LWallet.decrypt(password, mWallet.walletFile));
                } catch (CipherException e) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    getStackMsg(e);
                }
                if(mCredentials == null){
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }
                invokeTransfer();
            });
    }

    @Override
    protected void initData() {
        mWallet = HLWalletManager.shared().getCurrentWallet(this);

        mTokenName = getIntent().getStringExtra(Constants.ASSETS_NAME);

        mTokenAddress = getIntent().getStringExtra(Constants.ASSETS_ADDRESS);

        mBinding.transferType.setText(mTokenName);

//        if (!mTokenName.equals("ETH")) {
//            //查询Token余额
//            getTokenBanlace();
//        } else {
//            //查询ETH余额
//            getEthBanlance();
//        }
        if (!mTokenName.equalsIgnoreCase("hpb")) {
            //查询Token余额
            getTokenBanlace();
        } else {
            //查询ETH余额
            getEthBanlance();
        }

    }

    private void getEthBanlance() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(), mWallet.getAddress()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(TransferActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        //保留小数点后四位
                        mETHBalance = CommomUtil.decimalTo4Point(data);
                        mTokenBalance = CommomUtil.decimalTo4Point(data);
                        //ETH余额
                        mBinding.availableAmount.setText(getString(R.string.available_amount, mTokenName, mETHBalance));
                        mBinding.ethAvailableAmount.setText(getString(R.string.available_amount, mTokenName, mETHBalance));
                        //gas均价
                        getEthPrice();
                    }

                    @Override
                    protected void failure(HLError error) {
                    }
                });
    }


    private void getEthPrice() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthGasPrice(TransferUtil.getWeb3j()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<BigInteger>(TransferActivity.this, false) {
                    @Override
                    protected void success(BigInteger data) {
                        KLog.w("EthPrice = " + data);
                        if(data.compareTo(new BigInteger("1000000000")) == -1){
                            gasPrice = new BigInteger("1000000000");
                        }else {
                            gasPrice = data;
                        }
                        updateSeekBar(20);
                    }

                    @Override
                    protected void failure(HLError error) {
                        gasPrice = new BigInteger("1000000000");
                        updateSeekBar(20);
                    }
                });
    }


    private void getTokenBanlace() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getTokenBalance(TransferUtil.getWeb3j(), mWallet.getAddress(), mTokenAddress))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(TransferActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        mTokenBalance = CommomUtil.decimalTo4Point(data);

                        mBinding.availableAmount.setText(getString(R.string.current_token_amount, mTokenName, mTokenBalance));
                        Flowable.just(1)
                                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(), mWallet.getAddress()))
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HLSubscriber<String>(TransferActivity.this, true) {
                                    @Override
                                    protected void success(String data) {
                                        mETHBalance = CommomUtil.decimalTo4Point(data);

                                        //ETH余额
                                        mBinding.ethAvailableAmount.setText(getString(R.string.available_amount, "HPB", mETHBalance));
                                        //gas均价
                                        getEthPrice();
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

    private void invokeTransfer() {
        try {
            if (!mTokenName.equalsIgnoreCase("hpb")) {
                //Token转账
                Flowable.just(1)
                        .flatMap(s -> IBContractUtil.ERC20Transfer(TransferActivity.this,
                                TransferUtil.getWeb3j(),
                                mCredentials,
                                mTokenAddress, mAddress,
                                mGasPrice, mGasLimit,
                                mAmount))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<String>(TransferActivity.this, true) {
                            @Override
                            protected void success(String data) {
                                KLog.w("hash = " + data);

                                String transferAmout = mBinding.transferAmount.getText().toString().trim();//转账金额
                                String transferType = mBinding.transferType.getText().toString().trim();
                                String targetAddress = mBinding.targetAddress.getText().toString().trim();
                                saveMessageAndPush(false, null, data, ContractStatus.TRANSFER_TX, ContractStatus.MESSAGE_TURN_OUT,transferAmout,transferType,targetAddress);

                                Intent intent = new Intent(TransferActivity.this, TxRecordDetailAct.class);
                                intent.putExtra("txHash", data);
                                intent.putExtra("txType", getString(R.string.turn_out));
                                intent.putExtra("navigateType", 2);
                                startActivity(intent);
                                TransferActivity.this.finish();
                            }

                            @Override
                            protected void failure(HLError error) {
                                KLog.w(error);
                                if(error.getMessage().contains(mAddress.replace("0x",""))|| error.getMessage().contains("Bitsize must be 8 bit aligned")){
                                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.address_error)).show();
                                }else{
                                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.transfer_fail)).show();
                                }

                            }
                        });
            } else {
                //ETH转账
                Flowable.just(1)
                        .flatMap(s -> IBContractUtil.rawTransaction(TransferActivity.this,
                                TransferUtil.getWeb3j(),
                                mAddress,
                                mGasPrice, mGasLimit,
                                mAmount,
                                mWallet,
                                mCredentials))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<String>(TransferActivity.this, true) {
                            @Override
                            protected void success(String data) {
                                KLog.w("hash = " + data);

                                //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
                                saveMessageAndPush(false, null, data, ContractStatus.TRANSFER_TX, ContractStatus.MESSAGE_TURN_OUT,false,null);

                                Intent intent = new Intent(TransferActivity.this, TxRecordDetailAct.class);
                                intent.putExtra("txHash", data);
                                intent.putExtra("txType", getString(R.string.turn_out));
                                intent.putExtra("navigateType", 2);
                                startActivity(intent);
                            }

                            @Override
                            protected void failure(HLError error) {
                                KLog.w(error);
                                if(error.getMessage().contains(mAddress.replace("0x","")) || error.getMessage().contains("Bitsize must be 8 bit aligned")){
                                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.address_error)).show();
                                }else{
                                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.transfer_fail)).show();
                                }
                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStackMsg(Exception e) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        // 扫描二维码/条码回传
        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);

                mBinding.targetAddress.setText(content);
            }else{
                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.scan_cancel)).show();
            }
        }
    }

    /**
     * gas费用说明弹窗
     */
    private void gasWarnDlgShow() {
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, TransferActivity.this), null, getString(R.string.gas_info), getString(R.string.gas_description), Gravity.LEFT);

    }

    //余额检测，是否足够完成交易
    private boolean balanceCheck() {
        double inputValue = 0;
        double gasValue = 0;
        double tokenValue = 0;
        try {
            inputValue = Double.valueOf(mAmount);
            gasValue = gas.doubleValue();
            tokenValue = Double.valueOf(mTokenBalance);
        } catch (NumberFormatException e) {
            getStackMsg(e);
        }
        boolean isEnough ;
        if (mTokenName.equals("ETH")) {
            isEnough = tokenValue >= (inputValue + gasValue);
        } else {
            isEnough = tokenValue >= inputValue;
        }
        return isEnough;
    }

    //ETH余额不足，红字提醒
    private void checkETHBalance() {
        double inputValue = 0;
        double gasValue = 0;
        double ethValue = 0;
        try {
            inputValue = Double.valueOf(mAmount);
            gasValue = gas.doubleValue();
            ethValue = Double.valueOf(mETHBalance);
        } catch (NumberFormatException e) {
            getStackMsg(e);
        }
        boolean isEnough ;
        if (mTokenName.equals("ETH")) {
            isEnough = ethValue >= (inputValue + gasValue);
        } else {
            isEnough = ethValue >= gasValue;
        }
        if (isEnough) {
            mBinding.ethAvailableAmount.setEnabled(true);
            isGasEnough = true;
        } else {
            mBinding.ethAvailableAmount.setEnabled(false);
            isGasEnough = false;
        }
        prepareTransfer();
    }

    //指定Token余额不足，红字提醒
    private void checkTokenBalance() {

        if (mTokenBalance == null || mTokenBalance.equals("") || mAmount.equals("") || mAmount == null) {
            isTokenEnough = false;
            return;
        }

        if (balanceCheck()) {
            isTokenEnough = true;
            mBinding.availableAmount.setEnabled(true);
            if (mTokenName.equals("ETH")) {
                mBinding.ethAvailableAmount.setEnabled(true);
            }
        } else {
            isTokenEnough = false;
            mBinding.availableAmount.setEnabled(false);
            if (mTokenName.equals("ETH")) {
                mBinding.ethAvailableAmount.setEnabled(false);
            }
        }
        prepareTransfer();
    }

    private void updateSeekBar(int progress) {
        //gas price
        mGasPrice = gasPrice.multiply(new BigInteger((progress) + "")).divide(new BigInteger("10")).add(gasPrice);
        mBinding.showGasPrise.setText(Convert.fromWei(mGasPrice + "", Convert.Unit.GWEI) + " Gwei");
        //gas Amount
        gas = Convert.fromWei(mGasLimit.multiply(mGasPrice) + "", Convert.Unit.ETHER);

        mBinding.gasAmount.setText(getString(R.string.eth_amount, gas));
        //ETH 余额检测
        checkETHBalance();
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

    private void prepareTransfer() {
        mAddress = mBinding.targetAddress.getText().toString().trim();

        if (isTokenEnough && isGasEnough && mAddress.length() == 42) {
            mBinding.btnNext.setEnabled(true);
        } else {
            mBinding.btnNext.setEnabled(false);
        }
    }

    private void transferConfirm() {
        PopupWindow popupWindow = PopupWindowUtil.transferPopupWindow(TransferActivity.this);
        View popupView = popupWindow.getContentView();
        ((TextView) popupView.findViewById(R.id.transfer_type)).setText(getString(R.string.token_send,mTokenName));
        ((TextView) popupView.findViewById(R.id.transfer_amount)).setText(mAmount + " " + mTokenName);
        ((TextView) popupView.findViewById(R.id.transfer_address)).setText(CommomUtil.splitWalletAddress(mAddress));
        ((TextView) popupView.findViewById(R.id.gas)).setText(getString(R.string.eth_amount,gas));

        popupView.findViewById(R.id.btn_transfer).setOnClickListener(v -> {
            popupWindow.dismiss();
            transfer();
        });
        popupWindow.showAtLocation(mBinding.getRoot(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }
    /**
     * nonce
     */
    private void nonceDialog() {
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, TransferActivity.this), null, getString(R.string.nonce_hint), getString(R.string.nonce_warning), Gravity.LEFT);
        isKnow = true;
    }

}

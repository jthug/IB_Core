package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityKeyboard;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.SmartContract.MappingContract;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityEarnDetailBinding;
import com.lianer.core.dialog.InputWalletPswDialog;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.PopupWindowUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;

import org.reactivestreams.Publisher;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple9;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MyEarningActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ActivityEarnDetailBinding binding;
    private HLWallet currentWallet;
    private String contractAddress = ""; //分红合约地址
    private long nextAbonusTime;
    private long nowAbonusTime;
    private Timer timer;
    private BigDecimal authorizedValue = BigDecimal.valueOf(0.0000);
    private int currentOperation = 0;  //当前操作：0代表存入授权，1代表取出，2存入
    private String textInput = "0";
    private InputWalletPswDialog inputWalletPswDialog;
    private Credentials credentials;
    private String nestCOntractAddress;
    private BigInteger gasPrice;
    private BigInteger mGasPrice;
    private BigInteger mGasLimit = BigInteger.valueOf(100000);
    private String mTxHash;
    private final int TAG_RECEIVE = 0;
    private final int TAG_OTHER = 1;
    private BigDecimal ethAmount = BigDecimal.valueOf(0.00000000);
    private BigDecimal nestAmount = BigDecimal.valueOf(0.0000);
    private BigDecimal myNest = BigDecimal.valueOf(0.0000);
    private BigDecimal savedNest = BigDecimal.valueOf(0.0000);
    private BigDecimal availableEth = BigDecimal.valueOf(0.00000000);
    private BigDecimal nestBalance = BigDecimal.valueOf(0.0000);

    //已有交易正在打包中
    boolean isKnow;


    @Override
    protected void initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earn_detail);
        initTitle();
        currentWallet = HLWalletManager.shared().getCurrentWallet(this);
        getAbonusInfo();
        getAbonusAdress();

        binding.rlInPort.setOnClickListener(this);
        binding.rlOutPort.setOnClickListener(this);
        binding.btnEarn.setOnClickListener(this);
        binding.btnAction.setOnClickListener(this);
        binding.etInput.addTextChangedListener(this);
    }

    public static String valueDisplay(long value) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(value);
    }


    /**
     * 倒计时
     */
    private void countDown() {
        long currentTime = System.currentTimeMillis() / 1000;
        long diffTime;

        if (currentTime >= nowAbonusTime) {   //奖励发放倒计时
            binding.tvTimeTag.setText(getString(R.string.to_dividend));
            binding.btnAction.setEnabled(false);
            diffTime = nextAbonusTime - currentTime;
        } else { //奖励领取倒计时
            binding.tvTimeTag.setText(getString(R.string.collection_dividend));
            diffTime = nowAbonusTime - currentTime;
            if (availableEth.compareTo(BigDecimal.valueOf(0.00000000))==0){
                binding.btnAction.setEnabled(false);
            }else {
                binding.btnAction.setEnabled(true);
            }
        }

        KLog.e("diffTime=="+diffTime+"  nowAbonusTime=="+nowAbonusTime+"  nextAbonusTime=="+nextAbonusTime+" currentTime=="+currentTime);
        if (diffTime > 0) {
            if (timer == null) {
                timer = new Timer();
            }

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis() / 1000;
                    long diffTime;
                    if (currentTime>=nowAbonusTime){
                        diffTime = nextAbonusTime - currentTime;
                    }else {
                        diffTime = nowAbonusTime - currentTime;
                    }
                    long day = diffTime / (3600 * 24);
                    long hour = (diffTime - day * 3600 * 24) / 3600;
                    long minute = (diffTime - day * 3600 * 24 - hour * 3600) / 60;
                    long second = diffTime - day * 3600 * 24 - hour * 3600 - minute * 60;
                    if (day == 0 && hour == 0 && minute == 0 && second == 0) {
                        getAbonusInfo();
                        cancel();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvTime.setText(getString(R.string.countdown_time, day + "", hour + "", minute + "", second + ""));
                        }
                    });

                }
            };
            timer.schedule(timerTask, 0, 1000);
        } else {
            getAbonusInfo();
        }
    }


    private void getAbonusAdress() {
        Flowable.just(1)
                .flatMap(new Function<Integer, Publisher<String[]>>() {
                    @Override
                    public Publisher<String[]> apply(Integer integer) throws Exception {
                        BigInteger gasPrice = Convert.toWei("3", Convert.Unit.GWEI).toBigInteger();
                        BigInteger gasLimit = BigInteger.valueOf(1000000);

                        ReadonlyTransactionManager transactionManager = new ReadonlyTransactionManager(TransferUtil.getWeb3j(), currentWallet.getAddress());
                        MappingContract contract = MappingContract.load(Constants.MappingAddress, TransferUtil.getWeb3j(), transactionManager, gasPrice, gasLimit);

                        String address = contract.checkAddress("abonus").sendAsync().get();
                        String nestAddress = contract.checkAddress("nest").sendAsync().get();
                        String[] datas = {address, nestAddress};
                        return Flowable.just(datas);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String[]>() {
                    @Override
                    protected void success(String[] datas) {
                        fillAbonusAddress(datas[0]);
                        nestCOntractAddress = datas[1];
                    }

                    @Override
                    protected void failure(HLError error) {

                    }
                });
    }

    private void fillAbonusAddress(String data) {
        contractAddress = data;
        String address = dealWithAddress(data);
        binding.tvContractAddress.setText(address);
        binding.tvContractAddress.setOnClickListener(this);

    }

    @NonNull
    private String dealWithAddress(String data) {
        String address = "";
        if (data.length() > 18) {
            address = data.substring(0, 8) + "..." + data.substring(data.length() - 8);
        } else {
            address = data;
        }
        return address;
    }

    private void getAbonusInfo() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getAbonusInfo(TransferUtil.getWeb3j(), currentWallet.getAddress()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    protected void success(Tuple9<BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger, BigInteger> data) {
                        //下次分红时间
                        nextAbonusTime = data.getValue1().longValue();
                        //本次分红截止时间
                        nowAbonusTime = data.getValue2().longValue();
                        //ETH数
                        ethAmount = Convert.fromWei(data.getValue3().toString(), Convert.Unit.ETHER).setScale(8, BigDecimal.ROUND_DOWN);
                        //nest数
                        nestAmount = new BigDecimal(data.getValue4()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 4, BigDecimal.ROUND_DOWN);
                        //我的nest
                        myNest = new BigDecimal(data.getValue5()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 4, BigDecimal.ROUND_DOWN);
                        //参与分红的nest
                        savedNest = new BigDecimal(data.getValue6()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 4, BigDecimal.ROUND_DOWN);
                        //可领取的分红
                        availableEth = Convert.fromWei(data.getValue7().toString(), Convert.Unit.ETHER).setScale(8, BigDecimal.ROUND_DOWN);
                        //授权金额
                        authorizedValue = new BigDecimal(data.getValue8()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 4, BigDecimal.ROUND_DOWN);
                        //nest余额
                        nestBalance = new BigDecimal(data.getValue9()).divide(BigDecimal.valueOf(Math.pow(10, 18)), 4, BigDecimal.ROUND_DOWN);
                        fillInfo();
                        //开始倒计时
                        countDown();
                        if (authorizedValue.compareTo(BigDecimal.valueOf(0)) != 0) {
                            binding.etInput.setText(authorizedValue.toPlainString());
                            binding.btnEarn.setEnabled(true);
                            binding.btnEarn.setText(getString(R.string.authorized_success));
                            currentOperation = 2;
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w("failure :" + error);
                    }
                });
    }


    private void fillInfo() {

        binding.dividingPool.setText(ethAmount.toPlainString() + " ETH");
        binding.tvAvailableEth.setText(availableEth.toPlainString() + " ETH");
        binding.tvConmunityNest.setText(nestAmount.toPlainString() + " NEST");
        binding.tvJoinedNest.setText(savedNest.toPlainString() + " NEST");
        binding.tvNestAmout.setText(getString(R.string.current_nest_amount, nestBalance.toPlainString()));

    }

    private void initTitle() {
        binding.titlebar.showLeftDrawable();
        binding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_contract_address:
                Intent intent = new Intent(MyEarningActivity.this, EtherScanWebActivity.class);
                intent.putExtra("ContractAddress", contractAddress);
                startActivity(intent);
                break;
            case R.id.rl_in_port:
                //存入
                showInPort();
                break;
            case R.id.rl_out_port:
                //取出
                showOutPort();
                break;
            case R.id.btn_earn:
                btnEarnClick();
                break;
            case R.id.btn_action:
                //领取
                receive();
                break;
        }
    }

    //领取奖励
    private void receive() {
        showPopupWindow(TAG_RECEIVE);
    }

    private void btnEarnClick() {
        showPopupWindow(TAG_OTHER);
    }

    private void showPopupWindow(int tag) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
        long nonce = sharedPreferences.getLong(Constants.TRANSACTION_NONCE, 0);
//        if(IBContractUtil.getNonce(TransferUtil.getWeb3j(),mWallet.getAddress()) <= nonce && !isKnow){
//            nonceDialog();
//            return;
//        }
        long nonce1 = 0;
        try {
            nonce1 = IBContractUtil.getNonce(TransferUtil.getWeb3j(), currentWallet.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
            SnackbarUtil.DefaultSnackbar(binding.getRoot(), getString(R.string.nonce_error)).show();
            return;
        }
        if (nonce1 > nonce && !isKnow) {
            nonceDialog();
            return;
        }
        PopupWindow popupWindow = PopupWindowUtil.contracTransactionPopupWindow(this);
        View popupView = popupWindow.getContentView();
        TextView transferType = (TextView) popupView.findViewById(R.id.transfer_type);
        ((TextView) popupView.findViewById(R.id.transfer_address)).setText(dealWithAddress(contractAddress));
        Button btnAction = popupView.findViewById(R.id.btn_action);
        if (tag == TAG_RECEIVE) {
            ((TextView) popupView.findViewById(R.id.transfer_amount)).setText(availableEth.toPlainString() + " ETH");
            transferType.setText(getString(R.string.receive_income));
            btnAction.setText(getString(R.string.receive_income));
        } else {
            ((TextView) popupView.findViewById(R.id.transfer_amount)).setText(textInput + " NEST");
            if (currentOperation == 0) {  //授权存入
                btnAction.setText(getString(R.string.authorized));
                transferType.setText(getString(R.string.authorized));
            } else if (currentOperation == 1) {    //取出
                btnAction.setText(getString(R.string.withdraw));
                transferType.setText(getString(R.string.withdraw));
            } else {    //存入
                btnAction.setText(getString(R.string.deposit));
                transferType.setText(getString(R.string.deposit));
            }
        }
        TextView tvAvailableEth = popupView.findViewById(R.id.eth_available_amount);
        TextView tvAvailableToken = popupView.findViewById(R.id.token_available);
        tvAvailableToken.setText(getString(R.string.current_token_amount, "Nest", nestBalance));
        if (new BigDecimal(textInput).compareTo(nestBalance) > 0) {
            btnAction.setEnabled(false);
        }

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                invokePasswordInput(tag);
            }
        });

        SeekBar seekBar = popupView.findViewById(R.id.seek_bar);
        seekBar.setEnabled(false);
        TextView showGasPrise = popupView.findViewById(R.id.show_gas_prise);
        TextView ethAvailableAmount = popupView.findViewById(R.id.eth_available_amount);
        TextView gasAmount = popupView.findViewById(R.id.gas_amount);
        getNestAvailable(tvAvailableEth, seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //gas price
                mGasPrice = gasPrice.multiply(new BigInteger(progress + "")).divide(new BigInteger("10")).add(gasPrice);
                showGasPrise.setText(Convert.fromWei(mGasPrice + "", Convert.Unit.GWEI) + " Gwei");
                //gas Amount
                BigDecimal gas = Convert.fromWei(mGasLimit.multiply(mGasPrice) + "", Convert.Unit.ETHER);
                gasAmount.setText(getString(R.string.eth_amount, gas));

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
        popupWindow.showAtLocation(binding.getRoot(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void getEthPrice(SeekBar seekBar) {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthGasPrice(TransferUtil.getWeb3j()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<BigInteger>(MyEarningActivity.this, false) {
                    @Override
                    protected void success(BigInteger data) {
                        KLog.w("EthPrice = " + data);
                        if (data.compareTo(new BigInteger("1000000000")) == -1) {
                            gasPrice = new BigInteger("1000000000");
                        } else {
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

    private void getNestAvailable(TextView tvAvailableEth, SeekBar seekBar) {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEthBanlance(TransferUtil.getWeb3j(), currentWallet.getAddress()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>() {
                    @Override
                    protected void success(String data) {
                        String tokenBalance = CommomUtil.decimalTo4Point(data);
                        tvAvailableEth.setText(getString(R.string.current_token_amount, "ETH", tokenBalance));

                        getEthPrice(seekBar);
                    }

                    @Override
                    protected void failure(HLError error) {

                    }
                });

    }

    private void showOutPort() {
        binding.tvOutPort.setTextColor(getResources().getColor(R.color.c5));
        binding.lineOut.setVisibility(View.VISIBLE);

        binding.tvInPort.setTextColor(getResources().getColor(R.color.c11));
        binding.lineIn.setVisibility(View.GONE);

        binding.tvOperate.setText(getString(R.string.withdeaw_number));
        binding.tvNestAmout.setText(getString(R.string.my_saved_nest) + myNest);
        binding.etInput.setHint(getString(R.string.get_number_hint));
        binding.btnEarn.setText(getString(R.string.withdraw_nest));

        currentOperation = 1;
    }

    private void showInPort() {
        binding.tvInPort.setTextColor(getResources().getColor(R.color.c5));
        binding.lineIn.setVisibility(View.VISIBLE);

        binding.tvOutPort.setTextColor(getResources().getColor(R.color.c11));
        binding.lineOut.setVisibility(View.GONE);

        binding.tvOperate.setText(getString(R.string.deposit_number));
        binding.tvNestAmout.setText(getString(R.string.current_nest_amount, nestBalance));
        binding.etInput.setHint(getString(R.string.saved_number_hint));
        binding.btnEarn.setText(getString(R.string.authrizated_deposit_amount));

        if (authorizedValue == BigDecimal.valueOf(0.0000)) {
            currentOperation = 0;
        } else {
            currentOperation = 2;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textInput = s.toString();
        BigDecimal input;
        if (s.length() == 0) {
            input = new BigDecimal("0");
        } else {
            input = new BigDecimal(s.toString());
        }
//        if (s.length()>1&&s.toString().startsWith("0")){
//            if (s.charAt(1)!='.'){
//                binding.etInput.setText("");
//                Toast.makeText(this,"请输入小数点",Toast.LENGTH_SHORT).show();
//            }
//        }
        if (currentOperation != 1) { //存入授权和存入
            if (s.length() > 0 && nestBalance.compareTo(input) >= 0 && input.compareTo(BigDecimal.valueOf(0)) != 0) {
                binding.btnEarn.setEnabled(true);
            } else {
                binding.btnEarn.setEnabled(false);
            }
        } else { //取出
            if (s.length() > 0 && myNest.compareTo(input) >= 0 && input.compareTo(BigDecimal.valueOf(0)) != 0) {
                binding.btnEarn.setEnabled(true);
            } else {
                binding.btnEarn.setEnabled(false);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void paddingTransaction(int msgStatus, String txType, boolean isPublish, String contractAddress) {
        //添加消息
        saveMessageAndPush(false, -1L, mTxHash, 1, msgStatus, isPublish, contractAddress);
        Intent intent = new Intent(MyEarningActivity.this, TxRecordDetailAct.class);
        intent.putExtra("txType", txType);
        intent.putExtra("txHash", mTxHash);
        intent.putExtra("navigateType", 2);
        startActivity(intent);
    }


    //输入密码

    private void invokePasswordInput(int tag) {

        SecurityConfigure configure = new SecurityConfigure()
                .setDefaultKeyboardType(KeyboardType.NUMBER);
        //.setLetterEnabled(false); 关闭字母
        CenterDialog centerDialog = new CenterDialog(R.layout.dlg_input_wallet_pwd, MyEarningActivity.this);
        new SecurityKeyboard(this, centerDialog.getContentView(), configure);
        inputWalletPswDialog = new InputWalletPswDialog(centerDialog, new InputWalletPswDialog.BtnListener() {
            @Override
            public void sure() {
                String mPassword = inputWalletPswDialog.getWalletPsd();

                try {
                    credentials = Credentials.create(LWallet.decrypt(mPassword, currentWallet.walletFile));

                } catch (CipherException e) {
                    SnackbarUtil.DefaultSnackbar(binding.getRoot(), getString(R.string.current_psd_error)).show();
                    mPassword = "";
                    return;
                }
                if (credentials == null) {
                    SnackbarUtil.DefaultSnackbar(binding.getRoot(), getString(R.string.current_psd_error)).show();
                    return;
                }

                if (tag == TAG_RECEIVE) {
                    receiveEarn();
                } else {
                    if (currentOperation == 0) {  //存入授权
                        contractApprove();
                    } else if (currentOperation == 1) {  // 取出
                        depositOut();
                    } else {  //存入
                        depositIn();
                    }
                }
            }
        });

    }

    //领取收益
    private void receiveEarn() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.getEarn(TransferUtil.getWeb3j(), credentials))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<TransactionReceipt>(MyEarningActivity.this, true) {
                    @Override
                    protected void success(TransactionReceipt data) {
                        if (data != null) {
                            mTxHash = data.getTransactionHash();
                            KLog.w("领取成功" + data.getTransactionHash());
                            paddingTransaction(ContractStatus.MESSAGE_STSTUS_GET_EARN,getString(R.string.receive_income),false,contractAddress);
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.e("领取失败" + error.getMessage());
                    }
                });
    }

    //授权

    private void contractApprove() {
        String amount = new BigDecimal(textInput).multiply(BigDecimal.valueOf(Math.pow(10, 18))).toPlainString();
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.ERC20Approve(MyEarningActivity.this,
                        TransferUtil.getWeb3j(), credentials,
                        nestCOntractAddress,
                        contractAddress,
                        mGasPrice, mGasLimit,
                        amount))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<String>(MyEarningActivity.this, true) {
                    @Override
                    protected void success(String data) {
                        KLog.w("授权Hash：" + data);

                        mTxHash = data;
                        KLog.w(mTxHash);

//                        //市场合约添加到本地
//                        if (mContractId == -2 && !DBUtil.queryContractByContractAddress(mContractBean.getContractAddress())) {
//
//                            mContractId = DBUtil.insert(mContractBean);
//                        }
//                        //添加消息
//                        //只有在转入抵押资产时勾选同时发布合约到市场  isPublish才传true
//                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_APPROVE, getString(R.string.message_status_7),false,contractAddress);

                        paddingTransaction(ContractStatus.MESSAGE_STSTUS_APPROVE, getString(R.string.message_status_7), false, contractAddress);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.e("授权失败：" + error.getMessage());
                    }
                });
    }

    //取出
    private void depositOut() {
        BigInteger amount = new BigDecimal(textInput).multiply(BigDecimal.valueOf(Math.pow(10, 18))).toBigInteger();
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.retrieve(TransferUtil.getWeb3j(), credentials, amount))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<TransactionReceipt>(MyEarningActivity.this, true) {
                    @Override
                    protected void success(TransactionReceipt data) {
                        if (data != null) {
                            KLog.w("存入hash" + data.getTransactionHash());
                            mTxHash = data.getTransactionHash();
                            paddingTransaction(ContractStatus.MESSAGE_STSTUS_DEPOSIT_OUT, getString(R.string.message_status_10), false, contractAddress);
                        } else {
                            KLog.e("取出失败");
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.e("取回失败" + error.getMessage());
                    }
                });
    }

    //存入
    private void depositIn() {
        BigInteger amount = new BigDecimal(textInput).multiply(BigDecimal.valueOf(Math.pow(10, 18))).toBigInteger();
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.depositIn(TransferUtil.getWeb3j(), credentials, amount))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<TransactionReceipt>(MyEarningActivity.this, true) {
                    @Override
                    protected void success(TransactionReceipt data) {
                        if (data != null) {
                            KLog.w("存入Hash" + data.getTransactionHash());
                            mTxHash = data.getTransactionHash();
                            paddingTransaction(ContractStatus.MESSAGE_STSTUS_DEPOSIT_IN, getString(R.string.message_status_9), false, contractAddress);
                        } else {
                            KLog.e("存入失败");
                        }

                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.e("存入失败了" + error.getMessage());
                    }
                });
    }

    private void nonceDialog() {
        new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, this), null, getString(R.string.nonce_hint), getString(R.string.nonce_warning), Gravity.LEFT);
        isKnow = true;
    }
}

package com.lianer.core.wallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lianer.common.utils.DateUtils;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Singleton;
import com.lianer.common.utils.Utils;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.contract.MessageCenterAct;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.contract.bean.ContractEventBean;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databean.InfoDataBean;
import com.lianer.core.databean.NormalDataBean;
import com.lianer.core.databinding.ActivityTxRecordDetailBinding;
import com.lianer.core.lauch.MainAct;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.ScheduleCompat;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.QRCodeUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.wallet.bean.TxRecordBean;

import org.greenrobot.eventbus.EventBus;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

/**
 * 交易记录详情
 */
public class TxRecordDetailAct extends BaseActivity {

    private ActivityTxRecordDetailBinding txRecordDetailBinding;
    private String timeAndStatus;
    private int[] images = {R.drawable.ic_msg_failed, R.drawable.ic_msg_succeed, R.drawable.ic_msg_packing};
    private int navigateType;
    private HLWallet mWallet;

    @Override
    protected void initViews() {
        txRecordDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_tx_record_detail);
        initTitleBar();
    }

    @Override
    protected void initData() {
        mWallet = HLWalletManager.shared().getCurrentWallet(this);
        setViewData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        txRecordDetailBinding.titlebar.showLeftDrawable();
        txRecordDetailBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (navigateType) {
            case 2:
            case 3:
                Intent messageCenterIntent = new Intent(TxRecordDetailAct.this, MessageCenterAct.class);
                messageCenterIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                messageCenterIntent.putExtra("backType", 1);
                startActivity(messageCenterIntent);
                break;
            default:
                finish();
                break;
        }
    }

    /**
     * 设置控件数据
     */
    @SuppressLint("CheckResult")
    private void setViewData() {
//        String txHash = "0xfa56d4616ecd195f5ec2d96c588e5b0f63d7a626da68ad0154c852a9af5e2a40";//交易异常
//        String txHash = "0x7bfbd1a3be97f270a302198eb5861abd45cdca5e0fa4585b562490fe39dc4c18";//交易成功
//        String txHash = "0x8ec0e469365651664ed3b477b1a217ddf9b0dd02a7321fa418fb242870b82725";//交易失败
        int length =  CommomUtil.dip2px(TxRecordDetailAct.this,getResources().getDimension(R.dimen.dp_90));
        Intent intent = getIntent();
        //根据跳转页面标识获取不同的对象数据
        navigateType = intent.getIntExtra("navigateType", 0);//0：资产详情跳转 1：消息中心跳转 2：完成交易跳转 3：通知栏跳转

        String transferAmount = intent.getStringExtra("transferAmount");
        String tokenType = intent.getStringExtra("tokenType");
        String targetAddress = intent.getStringExtra("targetAddress");

        switch (navigateType) {
            case 0:
                TxRecordBean txRecordBean = (TxRecordBean) intent.getSerializableExtra("txDetail");
                txRecordDetailBinding.tokenType.setText(getIntent().getStringExtra("symbol"));
                Flowable.just(txRecordBean.getHash())
                        .map(s -> TransferUtil.getTransaction(TransferUtil.getWeb3j(), s))//通过交易hash获取交易值
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<InfoDataBean.DataBean>() {
                            @Override
                            protected void success(InfoDataBean.DataBean data) {
                                //交易值
                                String addOrSub = intent.getStringExtra("txType").equals("转出") ? getString(R.string.sub) : getString(R.string.add);
//                                txRecordDetailBinding.txAmount.setText(String.format("%s %s", addOrSub, Convert.fromWei(String.valueOf(data.getValue()), Convert.Unit.ETHER).toString()));
                                txRecordDetailBinding.txAmount.setText(String.format("%s %s", addOrSub, intent.getStringExtra("txValue")));
                            }

                            @Override
                            protected void failure(HLError error) {

                            }
                        });


                //加载交易状态图标
                Flowable.just(txRecordBean.getHash())
                        .map(s -> {
//                            TransactionReceipt transactionReceipt = TransferUtil.getContractDeployStatus(s);
                            String status = TransferUtil.getContractDeployStatus(s);
//                            if (transactionReceipt != null) {
//                                return Integer.parseInt(transactionReceipt.getStatus().substring(2));
//                            }
                            Log.e("AAAAAAA",status);
                            if (!status.equals("12005")){
                                if (status.equals("200")){
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                            return 2;//打包中
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<Integer>() {
                            @Override
                            protected void success(Integer status) {
                                txRecordDetailBinding.txStatusLogo.setImageResource(images[status]);
                                ////交易时间 and 交易状态
                                if (status != 2) {
                                    if (status == 0) {
                                        timeAndStatus = intent.getStringExtra("txTime") + getString(R.string.transaction_fail);
                                    } else {
                                        timeAndStatus = intent.getStringExtra("txTime") + getString(R.string.transaction_success);
                                    }

                                    txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                } else {
                                    txRecordDetailBinding.timeAndStatus.setText(R.string.in_the_package);
                                }
                            }

                            @Override
                            protected void failure(HLError error) {
                                //交易已经存在，不需要处理交易异常的情况
                                KLog.i(error.getMessage());
                            }
                        });

                //交易类型
                txRecordDetailBinding.txType.setText(intent.getStringExtra("txType"));
                //矿工费
                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount), intent.getStringExtra("txGas")));
                //转入地址
                txRecordDetailBinding.txToAddress.setText(!TextUtils.isEmpty(txRecordBean.getTo()) ? txRecordBean.getTo() : txRecordBean.getContractAddress());
                //转出地址
                txRecordDetailBinding.txFromAddress.setText(txRecordBean.getFrom());
                //交易 Hash
                txRecordDetailBinding.txHash.setText(txRecordBean.getHash());
                //交易hash二维码

                txRecordDetailBinding.qrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(Constants.BASE_TX_URL + txRecordBean.getHash(), length, length));

                txRecordDetailBinding.txFromAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, txRecordBean.getFrom()));
                txRecordDetailBinding.txToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, !TextUtils.isEmpty(txRecordBean.getTo()) ? txRecordBean.getTo() : txRecordBean.getContractAddress()));
                txRecordDetailBinding.txHash.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, false, txRecordBean.getHash()));
                break;
            case 1://从消息中心页面跳转过来
                String txHash = intent.getStringExtra("txHash");
                //交易类型
                txRecordDetailBinding.txType.setText(intent.getStringExtra("txType"));
                //交易 Hash
                txRecordDetailBinding.txHash.setText(txHash);
                //交易hash二维码
                txRecordDetailBinding.qrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(Constants.BASE_TX_URL + txHash, length, length));
                //交易hash点击事件
                txRecordDetailBinding.txHash.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, false, txHash));

                Flowable.just(txHash)
//                        .map(s -> TransferUtil.getTransaction(TransferUtil.getWeb3j(), s))//通过交易hash获取交易值
                        .map(s->TransferUtil.getTransaction(TransferUtil.getWeb3j(),s))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<InfoDataBean.DataBean>(TxRecordDetailAct.this, true) {
                            @Override
                            protected void success(InfoDataBean.DataBean data) {
                                KLog.i("transaction" + Singleton.gson().toJson(data));

                                //加载交易状态图标
                                Flowable.just(txHash)
                                        .map(TransferUtil::getContractDeployStatus)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new HLSubscriber<String>() {
                                            @Override
                                            protected void success(String status) {
//                                                KLog.i("transactionReceipt" + Singleton.gson().toJson(transactionReceipt));

                                                //交易状态logo
                                                //交易时间 and 交易状态
                                                //矿工费
                                                if (!status.equals("12005")) {
                                                    if (status.equals("12001")) {//失败
                                                        txRecordDetailBinding.txStatusLogo.setImageResource(images[0]);
                                                        timeAndStatus = DateUtils.timedate(intent.getStringExtra("txTime")) + getString(R.string.transaction_fail);
                                                    } else {//成功
                                                        txRecordDetailBinding.txStatusLogo.setImageResource(images[1]);
                                                        timeAndStatus = DateUtils.timedate(intent.getStringExtra("txTime")) + getString(R.string.transaction_success);
                                                        //恢复合约
                                                        restoreContract(txHash);
                                                    }

                                                    double gasUsed = Double.valueOf(data.getGas());
                                                    String gasValue = Utils.doubleFormat(gasUsed * Double.valueOf(data.getGasPrice().toString()));
                                                    txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount), Convert.fromWei(gasValue, Convert.Unit.ETHER).toString()));
                                                }

                                                txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                            }

                                            @Override
                                            protected void failure(HLError error) {
                                                //交易处于pending状态
                                                KLog.i(error.getMessage());
                                                txRecordDetailBinding.txStatusLogo.setImageResource(images[2]);
                                                timeAndStatus = DateUtils.timedate(intent.getStringExtra("txTime")) + getString(R.string.in_the_package);
                                                txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount),  Convert.fromWei(new BigDecimal(data.getGasPrice()).multiply(new BigDecimal(data.getGas())), Convert.Unit.ETHER).toString()));
                                            }
                                        });

                                //交易值
                                String value = data.getValue().toString();
                                String addOrSub;
                                switch (intent.getStringExtra("txType")) {
                                    case "转出":
                                    case "投资合约":
                                    case "还款":
                                        addOrSub = getString(R.string.sub);
                                        break;
                                    default:
                                        addOrSub = getString(R.string.add);
                                        break;
                                }


                                //转出地址
                                txRecordDetailBinding.txFromAddress.setText(data.getFrom());
                                if (tokenType!=null){
                                    txRecordDetailBinding.tokenType.setText(tokenType);
                                }else {
                                    txRecordDetailBinding.tokenType.setText("ETH");
                                }

                                if (transferAmount!=null){
                                    txRecordDetailBinding.txAmount.setText(String.format("%s %s", addOrSub, transferAmount));
                                }else {
                                    txRecordDetailBinding.txAmount.setText(String.format("%s %s", addOrSub, Convert.fromWei(value, Convert.Unit.ETHER).toString()));
                                }
                                //转入地址
                                if (targetAddress!=null){
                                    txRecordDetailBinding.txToAddress.setText(targetAddress);
                                }else {
                                    txRecordDetailBinding.txToAddress.setText(data.getTo());
                                }
                                //转入地址点击事件
                                txRecordDetailBinding.txFromAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, data.getFrom()));
                                //转出地址点击事件
                                txRecordDetailBinding.txToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, data.getTo()));
                            }

                            @Override
                            protected void failure(HLError error) {
                                KLog.i("transaction为空" + error.getMessage());

                                //判断是否在两分钟之内
                                //在以太坊上查不到交易记录、即交易异常
                                String txTime = intent.getStringExtra("txTime");
                                if (DateUtils.timeDiffIsSecond(txTime)) {//打包中
                                    txRecordDetailBinding.txStatusLogo.setImageResource(images[2]);
                                    txRecordDetailBinding.txAmount.setText("0");
                                    timeAndStatus = DateUtils.timedate(intent.getStringExtra("txTime")) + getString(R.string.in_the_package);
                                } else {//交易异常
                                    txRecordDetailBinding.txStatusLogo.setImageResource(images[0]);
                                    txRecordDetailBinding.txAmount.setText("0");
                                    timeAndStatus = DateUtils.timedate(intent.getStringExtra("txTime")) + getString(R.string.transaction_error);

                                    //更新数据库为交易失败状态
                                    MessageCenterBean messageCenterBean = DBUtil.queryByTxHash(txHash);
                                    messageCenterBean.setPackingStatus(2);
                                    DBUtil.update(messageCenterBean);
                                }

                                txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount), "\t0"));
                            }
                        });
                break;
            case 2://从转账页面跳转过来
                String transationHash = intent.getStringExtra("txHash");
                //交易类型
                txRecordDetailBinding.txType.setText(intent.getStringExtra("txType"));
                //交易 Hash
                txRecordDetailBinding.txHash.setText(transationHash);
                //交易hash二维码
                txRecordDetailBinding.qrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(Constants.BASE_TX_URL + transationHash, length, length));
                //交易hash点击事件
                txRecordDetailBinding.txHash.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, false, transationHash));

                //加载交易状态图标
                txRecordDetailBinding.txStatusLogo.setImageResource(images[2]);
                timeAndStatus = DateUtils.timedate(String.valueOf(System.currentTimeMillis() / 1000)) + getString(R.string.in_the_package);
                txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);

                Flowable.just(transationHash)
                        .map(s -> TransferUtil.getTransaction(TransferUtil.getWeb3j(), s))//通过交易hash获取交易值
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<InfoDataBean.DataBean>(TxRecordDetailAct.this, true) {
                            @Override
                            protected void success(InfoDataBean.DataBean data) {
                                KLog.i("transaction" + Singleton.gson().toJson(data));

                                //gas费
                                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount),  Convert.fromWei(new BigDecimal(data.getGasPrice()).multiply(new BigDecimal(data.getGas())), Convert.Unit.ETHER).toString()));

                                //交易值
                                String value = data.getValue().toString();
                                String addOrSub;
                                switch (intent.getStringExtra("txType")) {
                                    case "转出":
                                    case "投资合约":
                                    case "还款":
                                        addOrSub = getString(R.string.sub);
                                        break;
                                    default:
                                        addOrSub = getString(R.string.add);
                                        break;
                                }
                                txRecordDetailBinding.txAmount.setText(String.format("%s %s", addOrSub, Convert.fromWei(value, Convert.Unit.ETHER).toString()));
                                //转入地址
                                txRecordDetailBinding.txToAddress.setText(data.getTo());
                                //转出地址
                                txRecordDetailBinding.txFromAddress.setText(data.getFrom());
                                //转入地址点击事件
                                txRecordDetailBinding.txFromAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, data.getFrom()));
                                //转出地址点击事件
                                txRecordDetailBinding.txToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, data.getTo()));
                            }

                            @Override
                            protected void failure(HLError error) {
                                KLog.i("transaction为空" + error.getMessage());
                                //在以太坊上查不到交易记录、即交易异常
                                txRecordDetailBinding.txAmount.setText("0");
                                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount), "\t0"));
                            }
                        });
                break;
            case 3://从通知栏跳转过来
                MessageCenterBean messageCenterBean = DBUtil.queryByTxHash(intent.getStringExtra("txHash"));

                int txType = messageCenterBean.getTxType();
                int txStatus = messageCenterBean.getTxStatusValue();
                //交易类型
                txRecordDetailBinding.txType.setText(getString(ContractStatus.MESSAGE_STATUS[txStatus]));

                //交易 Hash
                txRecordDetailBinding.txHash.setText(intent.getStringExtra("txHash"));
                //交易hash二维码
                txRecordDetailBinding.qrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(Constants.BASE_TX_URL + intent.getStringExtra("txHash"), length, length));
                //交易hash的点击事件
                txRecordDetailBinding.txHash.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, false, intent.getStringExtra("txHash")));

                Flowable.just(intent.getStringExtra("txHash"))
                        .map(s -> TransferUtil.getTransaction(TransferUtil.getWeb3j(), s))//通过交易hash获取交易值
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new HLSubscriber<InfoDataBean.DataBean>(TxRecordDetailAct.this, true) {
                            @Override
                            protected void success(InfoDataBean.DataBean data) {
                                KLog.i(new Gson().toJson(data));

                                String value = data.getValue().toString();

                                //加载交易状态图标
                                Flowable.just(intent.getStringExtra("txHash"))
                                        .map(s -> {
//                                            TransactionReceipt transactionReceipt = TransferUtil.getContractDeployStatus(s);
                                            String status = TransferUtil.getContractDeployStatus(s);
                                            if (!status.equals("12005")){
                                                if (status.equals("200")){
                                                    return 1;
                                                } else {
                                                    return 0;
                                                }
                                            }
                                            return 2;//打包中
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new HLSubscriber<Integer>() {
                                            @Override
                                            protected void success(Integer status) {
                                                txRecordDetailBinding.txStatusLogo.setImageResource(images[status]);
                                                ////交易时间 and 交易状态
                                                if (status != 2) {
                                                    if (status == 0) {
                                                        timeAndStatus = DateUtils.timedate(messageCenterBean.getTxCreateTime()) + getString(R.string.transaction_fail);
                                                    } else {
                                                        timeAndStatus = DateUtils.timedate(messageCenterBean.getTxCreateTime()) + getString(R.string.transaction_success);
                                                    }

                                                    txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                                } else {
                                                    timeAndStatus = DateUtils.timedate(messageCenterBean.getTxCreateTime()) + getString(R.string.in_the_package);
                                                    txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                                }
                                            }

                                            @Override
                                            protected void failure(HLError error) {
                                                KLog.i(error.getMessage());
                                            }
                                        });


                                //交易值
                                if (txType == 0) {
                                    txRecordDetailBinding.txAmount.setText(String.format("%s %s", getString(R.string.sub), Convert.fromWei(value, Convert.Unit.ETHER).toString()));
                                } else {
                                    txRecordDetailBinding.txAmount.setText(String.format("%s %s", getString(R.string.add), Convert.fromWei(value, Convert.Unit.ETHER).toString()));
                                }

                                //矿工费
                                Flowable.just(intent.getStringExtra("txHash"))
                                        .map(TransferUtil::getContractDeployStatus)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new HLSubscriber<String>() {
                                            @Override
                                            protected void success(String status) {
                                                if (!status.equals("12005")) {
//                                                    KLog.i("getGasUsed" + transactionReceipt.getGasUsed());
//                                                    double gasUsed = transactionReceipt.getGasUsed().doubleValue();
                                                    String gasValue = Utils.doubleFormat(0.001 * Double.valueOf(data.getGasPrice().toString()));
                                                    txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount), Convert.fromWei(gasValue, Convert.Unit.ETHER).toString()));
                                                }
                                            }

                                            @Override
                                            protected void failure(HLError error) {
                                                KLog.i(error.getMessage());
                                                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount),  Convert.fromWei(new BigDecimal(data.getGasPrice()).multiply(new BigDecimal(data.getGas())), Convert.Unit.ETHER).toString()));
                                            }
                                        });
                                //转入地址
                                txRecordDetailBinding.txToAddress.setText(data.getTo());
                                //转出地址
                                txRecordDetailBinding.txFromAddress.setText(data.getFrom());

                                txRecordDetailBinding.txFromAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, data.getFrom()));
                                txRecordDetailBinding.txToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(TxRecordDetailAct.this, true, data.getTo()));
                            }

                            @Override
                            protected void failure(HLError error) {
                                KLog.i(error.getMessage());
                                //处理交易异常的情况
                                txRecordDetailBinding.txStatusLogo.setImageResource(images[0]);
                                txRecordDetailBinding.txAmount.setText("0");
                                timeAndStatus = DateUtils.timedate(messageCenterBean.getTxCreateTime()) + getString(R.string.transaction_error);
                                txRecordDetailBinding.timeAndStatus.setText(timeAndStatus);
                                txRecordDetailBinding.txGas.setText(String.format(getString(R.string.eth_amount), "0"));
                            }
                        });
                break;

        }

    }

    private void restoreContract(String txhash){
        switch (getIntent().getStringExtra("txType")) {
            case "部署合约":
            case "投资":
                txRecordDetailBinding.btnRestore.setVisibility(View.VISIBLE);
                break;
        }
        txRecordDetailBinding.btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (getIntent().getStringExtra("txType")) {
                    case "部署合约":
                        getAddressFromLog(txhash);
                        break;

                    case "投资":
//                        chekcContract(transactionReceipt.getContractAddress());
                        chekcContract(txhash);
                        break;
                }
            }
        });
    }

    public  void getAddressFromLog(String txhash) {
//        KLog.w(transactionReceipt.toString());
//        String contractAddress = "0x" + transactionReceipt.getLogs().get(0).getData().substring(26);
        String jsonParams1 = "{\n" +
                "\t\"transactionHash\": \"" + txhash + "\"\n" +
                "}";
        HttpUtil.getContractAddress(jsonParams1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<NormalDataBean>() {
                    @Override
                    protected void success(NormalDataBean data) {
                        if ("200".equals(data.getCode())){
                            String contractAddress = data.getData().get(0);
                            chekcContract(contractAddress);
                        }else {
                            KLog.e("获取合约地址失败");
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.e("获取合约地址失败"+error.getMessage());
                    }
                });
//        chekcContract(contractAddress);

    }

    //合约校验
    private void chekcContract(String txhash) {
        String jsonParams1 = "{\n" +
                "\t\"transactionHash\": \"" + txhash + "\"\n" +
                "}";

        HttpUtil.getContractAddress(jsonParams1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<NormalDataBean>() {
                    @Override
                    protected void success(NormalDataBean data) {
                        String code = data.getCode();
                        if ("200".equals(code)){
                            String contractAddress = data.getData().get(0);
                            String jsonParams = "{\n" +
                                    "\t\"contractAddress\": \"" + contractAddress + "\"\n" +
                                    "}";
                            HttpUtil.isContract(jsonParams)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new HLSubscriber<NormalDataBean>() {
                                        @Override
                                        protected void success(NormalDataBean data) {
                                            String code = data.getCode();
                                            if ("200".equals(code)){
                                                String isContract = data.getData().get(0);
                                                if ("true".equals(isContract)) {
                                                    if (DBUtil.queryContractByContractAddress(contractAddress)) {
                                                        SnackbarUtil.DefaultSnackbar(txRecordDetailBinding.getRoot(), getString(R.string.import_fail_existed)).show();
                                                    } else {
                                                        //DataBase 合约不存在
                                                        getContract(contractAddress);
                                                    }
                                                } else {
                                                    //Tusd 合约校验
                                                    checkTusdContract(contractAddress);
                                                }
                                            }else {
                                                KLog.e("合约校验失败-网络异常");
                                            }
                                        }

                                        @Override
                                        protected void failure(HLError error) {
                                            KLog.e("合约校验失败");
                                        }
                                    });
                        }else {
                            KLog.e("获取合约地址失败-网络异常");
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.e("获取合约地址失败");
                    }
                });

    }

    //Tusd 合约校验
    private void checkTusdContract(String contractAddress){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.isIBTusdContract(TransferUtil.getWeb3j(), mWallet.getAddress(), contractAddress))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<Boolean>(TxRecordDetailAct.this, true) {
                    @Override
                    protected void success(Boolean isIBContract) {

                        if (isIBContract) {
                            if (DBUtil.queryContractByContractAddress(contractAddress)) {
                                SnackbarUtil.DefaultSnackbar(txRecordDetailBinding.getRoot(), getString(R.string.import_fail_existed)).show();
                            } else {
                                //DataBase 合约不存在
                                getContract(contractAddress);
                            }
                        } else {
                            SnackbarUtil.DefaultSnackbar(txRecordDetailBinding.getRoot(), getString(R.string.import_fail_not_nest)).show();
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(txRecordDetailBinding.getRoot(),getString(R.string.import_fail_not_nest)).show();
                    }
                });
    }

    //获取合约信息
    public  void getContract( String contractAddress) {
        try {
            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.getTUSDContractInfo(TransferUtil.getWeb3j(), mWallet.getAddress(), contractAddress))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<ContractBean>(TxRecordDetailAct.this, true) {
                        @Override
                        protected void success(ContractBean contractBean) {
                            DBUtil.insert(contractBean);

                            //通知合约管理页刷新数据
                            EventBus.getDefault().post(new ContractEventBean(contractBean, ContractEventBean.ADD_CONTRACT));
                            SnackbarUtil.DefaultSnackbar(txRecordDetailBinding.getRoot(),getString(R.string.import_contract_success)).show();

                            Flowable.just(1)
                                    .delay(2000, TimeUnit.MILLISECONDS)
                                    .compose(ScheduleCompat.apply())
                                    .subscribe(integer -> {
                                        startActivity(new Intent(TxRecordDetailAct.this,MainAct.class));
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
    }



}

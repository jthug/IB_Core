package com.lianer.core.borrow;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;
import com.google.gson.Gson;
import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.SmartContract.ETH.IBContract;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.ContractDetailAct;
import com.lianer.core.contract.DisbandContractAct;
import com.lianer.core.contract.GetMortgageAssetsAct;
import com.lianer.core.contract.RepaymentActivity;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityContractStateBinding;
import com.lianer.core.dialog.ContractTerminationDialog;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.invest.InvestActivity;
import com.lianer.core.lauch.MainAct;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.TransferUtil;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import static com.lianer.core.app.Constants.CONTRACT_RESPONSE;


public class ContractStateActivity extends BaseActivity implements View.OnClickListener {

    private ActivityContractStateBinding mBinding;
    private ContractResponse mContractInfo;
    private IBContract mContract;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contract_state);
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
        mBinding.btnAction.setOnClickListener(this);
        mBinding.resultContent.setOnClickListener(this);
        mBinding.terminationContract.setOnClickListener(this);
    }


    @Override
    protected void initData() {
        mContractInfo = (ContractResponse) getIntent().getSerializableExtra(CONTRACT_RESPONSE);
        KLog.w("合约 = "+mContractInfo.toString());

        String txHash = "";
        String hash = "";
        String contractAddress = "";
        String address = "";
//        getContract(mContractInfo.getContractAddress());
        //根据状态加载
        switch (mContractInfo.getContractStatus()){
            //合约部署中
            case ContractStatus.CONTRACT_STSTUS_0:
                mBinding.contractProgress.setImageResource(R.drawable.deploy_ing);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_deploying);
                mBinding.contractStateTxt.setText(getString(R.string.contract_deploying));
                mBinding.btnAction.setText(R.string.back);
                mBinding.titlebar.setTitle(getString(R.string.borrow));

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;
            //合约部署失败
            case ContractStatus.CONTRACT_STSTUS_1:
                mBinding.contractProgress.setImageResource(R.drawable.deploy_fail);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_deploy_fail);
                mBinding.contractStateTxt.setText(getString(R.string.contract_deploy_fail));
                mBinding.btnAction.setText(R.string.back_retry);
                mBinding.titlebar.setTitle(getString(R.string.borrow));

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);
                break;
            //合约部署成功
            case ContractStatus.CONTRACT_STSTUS_2:
                mBinding.contractProgress.setImageResource(R.drawable.deploy_success);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_deploy_success);
                mBinding.contractStateTxt.setText(getString(R.string.contract_deploy_success));
                mBinding.btnAction.setText(R.string.paying_mortgage_assets);
                mBinding.titlebar.setTitle(getString(R.string.borrow));

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);

                //显示合约终止选项
                mBinding.terminationContract.setVisibility(View.VISIBLE);

                break;

            case ContractStatus.CONTRACT_STSTUS_4:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_end);
                mBinding.contractStateTxt.setText(getString(R.string.contract_terminated));
                mBinding.btnAction.setText(R.string.back);
                mBinding.titlebar.setTitle(getString(R.string.borrow));

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);

                break;

            //合约转入抵押资产中
            case ContractStatus.CONTRACT_STSTUS_5:
                mBinding.contractProgress.setImageResource(R.drawable.deploy_assets_ing);
                mBinding.contractStateImg.setImageResource(R.drawable.invest_loading);
                mBinding.contractStateTxt.setText(getString(R.string.contract_assets_transfer));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);
                break;

            //合约转入抵押资产失败
            case ContractStatus.CONTRACT_STSTUS_6:
                mBinding.contractProgress.setImageResource(R.drawable.deploy_assets_fail);
                mBinding.contractStateImg.setImageResource(R.drawable.invest_fail);
                mBinding.contractStateTxt.setText(getString(R.string.contract_assets_transfer_fail));
                mBinding.btnAction.setText(R.string.back_retry);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                //显示合约终止选项
                mBinding.terminationContract.setVisibility(View.VISIBLE);
                break;

            //合约发布完成
            case ContractStatus.CONTRACT_STSTUS_7:
                mBinding.contractProgress.setImageResource(R.drawable.deploy_release);
                mBinding.contractStateImg.setImageResource(R.drawable.invest_success);
                mBinding.contractStateTxt.setText(getString(R.string.contract_release));
                mBinding.btnAction.setText(R.string.go_contract_detail);

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);

//                getContract(mContractInfo.getContractAddress());
                break;

            //合约解散中
            case ContractStatus.CONTRACT_STSTUS_9:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_disbanding);
                mBinding.contractStateTxt.setText(getString(R.string.contract_disbanding));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);
                break;

            //合约解散失败
            case ContractStatus.CONTRACT_STSTUS_10:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_disband_fail);
                mBinding.contractStateTxt.setText(getString(R.string.contract_disband_fail));

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);
                break;

            //合约解散成功
            case ContractStatus.CONTRACT_STSTUS_11:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.contract_disband_success);
                mBinding.contractStateTxt.setText(getString(R.string.contract_disband_success));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);
                break;

            //投资资产转入中
            case ContractStatus.CONTRACT_STSTUS_13:
                mBinding.contractProgress.setImageResource(R.drawable.invest_coming_bg);
                mBinding.contractStateImg.setImageResource(R.drawable.invest_loading);
                mBinding.contractStateTxt.setText(getString(R.string.invest_loading));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;

            //投资资产转入失败
            case ContractStatus.CONTRACT_STSTUS_14:
                mBinding.contractProgress.setImageResource(R.drawable.invest_failed_bg);
                mBinding.contractStateImg.setImageResource(R.drawable.invest_fail);
                mBinding.contractStateTxt.setText(getString(R.string.invest_fail));
                mBinding.btnAction.setText(R.string.back_retry);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;

            //投资完成
            case ContractStatus.CONTRACT_STSTUS_15:
                mBinding.contractProgress.setImageResource(R.drawable.invest_success_bg);
                mBinding.contractStateImg.setImageResource(R.drawable.invest_success);
                mBinding.contractStateTxt.setText(getString(R.string.invest_success));
                mBinding.btnAction.setText(R.string.go_contract_detail);

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);

                getContract(mContractInfo.getContractAddress());
                break;


            //还款中
            case ContractStatus.CONTRACT_STSTUS_18:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.repaymenting);
                mBinding.contractStateTxt.setText(getString(R.string.repaymenting));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;

            //还款失败
            case ContractStatus.CONTRACT_STSTUS_19:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.repayment_fail);
                mBinding.contractStateTxt.setText(getString(R.string.repayment_fail));
                mBinding.btnAction.setText(R.string.back_retry);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;

            //还款成功
            case ContractStatus.CONTRACT_STSTUS_20:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.repayment_success);
                mBinding.contractStateTxt.setText(getString(R.string.repayment_success));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);

                break;



            //资产取回中
            case ContractStatus.CONTRACT_STSTUS_23:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.get_assets_ing);
                mBinding.contractStateTxt.setText(getString(R.string.get_assets_ing));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;

            //资产取回失败
            case ContractStatus.CONTRACT_STSTUS_24:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.get_assets_fail);
                mBinding.contractStateTxt.setText(getString(R.string.get_assets_fail));
                mBinding.btnAction.setText(R.string.back_retry);

                mBinding.resultInfo.setText(R.string.hash_address);
                txHash = mContractInfo.getContractHash();
                hash = txHash.substring(0,10)  + "......" + txHash.substring(txHash.length()-10,txHash.length());
                mBinding.resultContent.setText(hash);

                break;

            ///资产取回成功
            case ContractStatus.CONTRACT_STSTUS_25:
                mBinding.contractProgress.setVisibility(View.GONE);
                mBinding.contractStateImg.setImageResource(R.drawable.get_assets_success);
                mBinding.contractStateTxt.setText(getString(R.string.get_assets_success));
                mBinding.btnAction.setText(R.string.back);

                mBinding.resultInfo.setText(R.string.contract_address);
                contractAddress = mContractInfo.getContractAddress();
                address = contractAddress.substring(0,10)  + "......" + contractAddress.substring(contractAddress.length()-10,contractAddress.length());
                mBinding.resultContent.setText(address);

                break;

        }

        updateContractState();
    }


    private void updateContractState(){
        switch (mContractInfo.getContractStatus()){
            //合约部署成功，支付抵押资产
            case ContractStatus.CONTRACT_STSTUS_2:
                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_3);
                updateContractRequst(updateRequestBody());

                break;


            //合约发布完成，查看合约详情
            case ContractStatus.CONTRACT_STSTUS_7:
                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_8);
//              // 合约时间
//              mContractInfo.setContractCreateDate(System.currentTimeMillis()/1000+"");
                updateContractRequst(updateRequestBody());
                break;
            //合约解散成功
            case ContractStatus.CONTRACT_STSTUS_11:
                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_12);
                //解散时间
                mContractInfo.setContractDissolutionDate(System.currentTimeMillis()/1000+"");
                updateContractRequst(updateRequestBody());
                break;

            //还款成功
            case ContractStatus.CONTRACT_STSTUS_20:
                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_21);
                //还款时间
               mContractInfo.setContractRepaymentDate(System.currentTimeMillis()/1000+"");
                updateContractRequst(updateRequestBody());
                break;

            //资产取回成功
            case ContractStatus.CONTRACT_STSTUS_25:
                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_26);
                //资产取回时间
                mContractInfo.setContractTakebackDate(System.currentTimeMillis()/1000+"");
                updateContractRequst(updateRequestBody());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_action:
                switch (mContractInfo.getContractStatus()){
                    //合约部署中,返回首页
                    case ContractStatus.CONTRACT_STSTUS_0:
                        backToContractTab();
                        break;

                    //合约部署失败，返回重试
                    case ContractStatus.CONTRACT_STSTUS_1:
                        intent = new Intent(ContractStateActivity.this, ContractDeployActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE,  mContractInfo);
                        startActivity(intent);
                        break;
                    //合约部署成功，支付抵押资产
                    case ContractStatus.CONTRACT_STSTUS_2:
                    //待打入抵押资产
                    case ContractStatus.CONTRACT_STSTUS_3:
                        intent = new Intent(ContractStateActivity.this,ContractPayAssetsActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE,mContractInfo);
                        startActivity(intent);
                        break;

                    //合约终止
                    case ContractStatus.CONTRACT_STSTUS_4:
                        backToContractTab();
                        break;

                    //合约转入抵押资产中,返回首页
                    case ContractStatus.CONTRACT_STSTUS_5:
                        backToContractTab();
                        break;

                    //合约转入抵押资产失败，返回重试
                    case ContractStatus.CONTRACT_STSTUS_6:
                        intent = new Intent(ContractStateActivity.this, ContractPayAssetsActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE,  mContractInfo);
                        startActivity(intent);
                        break;

                    //合约发布完成，查看合约详情
                    case ContractStatus.CONTRACT_STSTUS_7:
                    //合约已发布
                    case ContractStatus.CONTRACT_STSTUS_8:
                        intent = new Intent(ContractStateActivity.this,ContractDetailAct.class);
                        KLog.w(mContractInfo.toString());
                        intent.putExtra(CONTRACT_RESPONSE,mContractInfo);
                        startActivity(intent);
                        break;

                    //合约解散中,返回首页
                    case ContractStatus.CONTRACT_STSTUS_9:
                        backToContractTab();
                        break;
                    //合约解散失败，返回重试
                    case ContractStatus.CONTRACT_STSTUS_10:
                        intent = new Intent(ContractStateActivity.this, DisbandContractAct.class);
                        intent.putExtra(CONTRACT_RESPONSE,  mContractInfo);
                        startActivity(intent);
                        break;
                    //合约解散成功
                    case ContractStatus.CONTRACT_STSTUS_11:
                    //合约解散
                    case ContractStatus.CONTRACT_STSTUS_12:
                        backToContractTab();
                        break;

                    //合约投资中,返回首页
                    case ContractStatus.CONTRACT_STSTUS_13:
                        backToContractTab();
                        break;

                    //合约投资失败,返回重试
                    case ContractStatus.CONTRACT_STSTUS_14:
                        intent = new Intent(ContractStateActivity.this, InvestActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE,  mContractInfo);
                        startActivity(intent);
                        break;

                    //合约投资成功
                    case ContractStatus.CONTRACT_STSTUS_15:
                        //合约已生效
                    case ContractStatus.CONTRACT_STSTUS_16:
                        intent = new Intent(ContractStateActivity.this,ContractDetailAct.class);
                        intent.putExtra(CONTRACT_RESPONSE,mContractInfo);
                        startActivity(intent);
                        break;

                    //还款中,返回首页
                    case ContractStatus.CONTRACT_STSTUS_18:
                        backToContractTab();
                        break;
                    //还款失败，返回重试
                    case ContractStatus.CONTRACT_STSTUS_19:
                        intent = new Intent(ContractStateActivity.this, RepaymentActivity.class);
                        intent.putExtra(CONTRACT_RESPONSE,  mContractInfo);
                        startActivity(intent);
                        break;
                    //还款成功
                    case ContractStatus.CONTRACT_STSTUS_20:
                    //合约已还款
                    case ContractStatus.CONTRACT_STSTUS_21:
                        backToContractTab();
                        break;


                    //资产取回中,返回首页
                    case ContractStatus.CONTRACT_STSTUS_23:
                        backToContractTab();
                        break;
                    //资产取回失败，返回重试
                    case ContractStatus.CONTRACT_STSTUS_24:
                        intent = new Intent(ContractStateActivity.this, GetMortgageAssetsAct.class);
                        intent.putExtra(CONTRACT_RESPONSE,  mContractInfo);
                        startActivity(intent);
                        break;
                    //资产取回成功
                    case ContractStatus.CONTRACT_STSTUS_25:
                    //合约执行逾期取回
                    case ContractStatus.CONTRACT_STSTUS_26:
                        backToContractTab();
                        break;
                }
                break;


            //Etherscan 查询
            case R.id.result_content:
                switch (mContractInfo.getContractStatus()){
                    case ContractStatus.CONTRACT_STSTUS_2:
                    case ContractStatus.CONTRACT_STSTUS_4:
                    case ContractStatus.CONTRACT_STSTUS_7:
                    case ContractStatus.CONTRACT_STSTUS_11:
                    case ContractStatus.CONTRACT_STSTUS_15:
                    case ContractStatus.CONTRACT_STSTUS_20:
                    case ContractStatus.CONTRACT_STSTUS_25:
                        intent = new Intent(ContractStateActivity.this, EtherScanWebActivity.class);
                        intent.putExtra("ContractAddress",  mContractInfo.getContractAddress());
                        startActivity(intent);
                        break;

                        default:
                            intent = new Intent(ContractStateActivity.this, EtherScanWebActivity.class);
                            intent.putExtra("TxHash",  mContractInfo.getContractHash());
                            startActivity(intent);
                            break;
                }

                break;

            //终止合约
            case R.id.termination_contract:
                terminationContract();
                break;
        }
    }


    private void backToContractTab(){
        startActivity(new Intent(ContractStateActivity.this,MainAct.class).putExtra(Tag.INDEX,2));
    }
    /**
     * 终止合约
     */
    private void terminationContract() {
        new ContractTerminationDialog(new CenterDialog(R.layout.dialog_contract_termination, this), new ContractTerminationDialog.BtnListener() {
            @Override
            public void confirm() {
                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_4);
                mContractInfo.setContractTerminationDate(System.currentTimeMillis()/1000+"");
                updateContractRequst(updateRequestBody());
            }
        });

    }

    private void getContract(String contractAddress) {
        try {

            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.readOnlyIBContract(TransferUtil.getWeb3j(),HLWalletManager.shared().getCurrentWallet(this).getAddress(),contractAddress))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<IBContract>(ContractStateActivity.this,true) {
                        @Override
                        protected void success(IBContract contract) {
                            mContract = contract;
                            //合约投资成功
                          if(mContractInfo.getContractStatus().equals(ContractStatus.CONTRACT_STSTUS_15))  {
                                mContractInfo.setContractStatus(ContractStatus.CONTRACT_STSTUS_16);

//                                BigInteger deadline = null;
//                                try {
//                                    deadline = mContract.showExpireDate().sendAsync().get();
//                                    KLog.w(mContract.showContractState().sendAsync().get());
//                                } catch (ExecutionException e) {
//                                    e.printStackTrace();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                KLog.w("到期时间：" + deadline);
//                                //到期时间
//                                mContractInfo.setContractExpireDate(deadline + "");
//
//                                //TODO 周期
//                                BigInteger cycle = new BigInteger("86400").multiply(new BigInteger(mContractInfo.getTimeLimit() + ""));
//                                //生效时间
//                                mContractInfo.setContractEffectDate(deadline.subtract(cycle) + "");
//                                KLog.w("生效时间：" + deadline.subtract(cycle) + "");

                                updateContractRequst(updateRequestBody());
                            }
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
     * 更新服务器合约钱包
     */
    private void updateContractRequst(String jsonRequestParams) {
        HttpUtil.updateContractState(jsonRequestParams, new HttpUtil.updateContractCallBack() {
            @Override
            public void onSuccess(Object responses) {

                if (responses != null) {
                    KLog.i("修改合约状态" + responses.toString());
                    if (responses.toString().equals("1")) {
                        KLog.i("修改成功");
                        Intent intent;
                        switch (mContractInfo.getContractStatus()){

                            //合约终止
                            case ContractStatus.CONTRACT_STSTUS_4:
                                intent = new Intent(ContractStateActivity.this,ContractStateActivity.class);
                                intent.putExtra(CONTRACT_RESPONSE,mContractInfo);
                                startActivity(intent);
                                break;
                        }

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
    String creatTime ;
    /**
     * 更新合约状态的请求参数
     */
    private String updateRequestBody() {
        creatTime = mContractInfo.getContractCreateDate();
        ContractResponse data = new ContractResponse();
        data = mContractInfo;
        // 合约时间置空
        data.setContractCreateDate(null);
        Gson gson = new Gson();
        String body = gson.toJson(data);
        mContractInfo.setContractCreateDate(creatTime);
        return body;
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

}

package com.lianer.core.contract.model;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.Gson;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.ETH.IBContract;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.TransferUtil;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * 我的合约-投资
 */
public class ContractInvestAndBorrowModel {

    Context mContext;

    public ContractInvestAndBorrowModel(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 检查当前合约状态的是否匹配链上数据，不匹配：更新服务器的合约数据  匹配：不做操作
     *
     * @param contractResponse              合约对象
     * @param position                      当前合约在列表的位置
     * @param updateInvestAndBorrowCallback 数据更新回调
     */
    @SuppressLint("CheckResult")
    public void checkContractData(ContractResponse contractResponse, int position, UpdateInvestAndBorrowCallback updateInvestAndBorrowCallback) {
        try {
            if(contractResponse.getContractStatus().equals("4")){
                return;
            }
            //0.部署成功 1.已发布 2.已生效 3.已还款 4.已取回抵押资产 5.已解散 6.24小时后预期 7.已逾期
            Flowable<IBContract> ibContractFlowable = IBContractUtil.readOnlyIBContract(TransferUtil.getWeb3j(),
                    HLWalletManager.shared().getCurrentWallet(mContext).getAddress(),
                    contractResponse.getContractAddress());//获取合约对象
                    ibContractFlowable.map(ibContract -> ibContract.showContractState().sendAsync().get())//获取合约状态
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bigInteger -> {
                        KLog.i("借币合约状态：" + bigInteger);
                        KLog.i("借币业务状态：" + contractResponse.getContractStatus());
                        boolean isBetweenContractList = CommomUtil.isBetweenContractList(bigInteger.toString(), contractResponse.getContractStatus());
                        KLog.i("借币业务状态与合约状态是否匹配：" + (isBetweenContractList ? "匹配" : "不匹配"));
                        //判断业务状态是否在合约状态
                        if (!isBetweenContractList) {//不再范围内
                            //把业务状态改为合约状态对应的业务状态
                            //0.部署成功 1.已发布 2.已生效 3.已还款 4.已取回抵押资产 5.已解散 6.24小时后预期 7.已逾期
                            switch (String.valueOf(bigInteger)) {
                                case "0":
                                    contractResponse.setContractStatus(ContractStatus.DEPLOY_LIST.get(0));
                                    break;
                                case "1":
                                    contractResponse.setContractStatus(ContractStatus.RELEASED_LIST.get(0));
                                    break;
                                case "2":
                                    contractResponse.setContractStatus(ContractStatus.EFFECTED_LIST.get(0));
                                    //在已生效的条件下，判断合约是否快到期
//                                    ibContractFlowable
//                                            .map(ibContract -> ibContract.expireState().sendAsync().get())
//                                            .subscribeOn(Schedulers.io())
//                                            .observeOn(AndroidSchedulers.mainThread())
//                                            .subscribe(o -> {
//                                                // 未逾期 0, 即将逾期 4, 逾期返回 5.
//                                                if (o.toString().equals("0")) {
//                                                    contractResponse.setContractStatus(ContractStatus.REPAYMENT_LIST.get(0));
//                                                } else if (o.toString().equals("4")) {
//                                                    contractResponse.setContractStatus(ContractStatus.EFFECTED_LIST.get(2));
//                                                } else {
//                                                    //不做操作
//                                                }
//                                            });
                                    break;
                                case "3":
                                    contractResponse.setContractStatus(ContractStatus.REPAYMENT_LIST.get(0));
                                    break;
                                case "4":
                                    contractResponse.setContractStatus(ContractStatus.RECAPTURED_LIST.get(0));
                                    break;
                                case "5":
                                    contractResponse.setContractStatus(ContractStatus.DISBANDED_LIST.get(0));
                                    break;
                                case "6":
                                    contractResponse.setContractStatus(ContractStatus.EFFECTED_LIST.get(2));
                                    break;
                                case "7":
                                    contractResponse.setContractStatus(ContractStatus.OVERDUE_LIST.get(0));
                                    break;
                            }
                            updateContractRequst(contractResponse, position, updateInvestAndBorrowCallback);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateContractRequst(ContractResponse contractResponse, int position, UpdateInvestAndBorrowCallback updateInvestAndBorrowCallback) {
        HttpUtil.updateContractState(updateContractRequestBody(contractResponse))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String responses) {
                        if (responses != null) {
                            KLog.i("借币修改合约返回的数据" + responses);
                            if (responses.equals("1")) {
                                if (updateInvestAndBorrowCallback != null) {
                                    updateInvestAndBorrowCallback.onUpdataSuccess(contractResponse, position);
                                }
                            }
                        } else {
                            KLog.i("数据为空");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        KLog.i("修改失败");
                    }

                    @Override
                    public void onComplete() {
                        KLog.i("修改完成");
                    }
                });
    }

    /**
     * 更新合约状态的请求参数
     */
    private String updateContractRequestBody(ContractResponse data) {
        // 合约时间置空
        data.setContractCreateDate(null);
        Gson gson = new Gson();
        KLog.i("借币更新合约状态请求数据" + gson.toJson(data));
        return gson.toJson(data);
    }

    public interface UpdateInvestAndBorrowCallback {
        void onUpdataSuccess(ContractResponse contractResponse, int position);
    }

}

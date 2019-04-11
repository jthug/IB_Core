package com.lianer.core.utils;

import android.content.Context;

import com.lianer.common.utils.ACache;
import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseBean;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.contract.bean.ContractEventBean;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;

import org.greenrobot.eventbus.EventBus;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 轮询工具类
 */
public class PollingUtil {

    public static void startPolling(Context context, List<Boolean> pollingBooleanList, int position, String txHash, MessageCenterBean messageCenterBean, OnUpdatePageData onUpdatePageData) {
        Flowable.just(txHash)
                .map(s -> TransferUtil.getTransaction(TransferUtil.getWeb3j(), s))//通过交易hash获取交易值
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<Transaction>() {
                    @Override
                    protected void success(Transaction data) {
//                        KLog.i("transaction" + Singleton.gson().toJson(data));

                        //加载交易状态图标
                        Flowable.just(txHash)
                                .map(TransferUtil::getContractDeployStatus)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new HLSubscriber<TransactionReceipt>() {
                                               @Override
                                               protected void success(TransactionReceipt transactionReceipt) {
//                                        KLog.i("transactionReceipt" + Singleton.gson().toJson(transactionReceipt));

                                                   if (!pollingBooleanList.get(position)) {
                                                       pollingBooleanList.set(position, true);

                                                       ACache.get(context).put(Tag.IS_RED_NOTIFICATION, "true");

//                                        KLog.i("TransactionReceipt不为空，说明交易已经完成-->" + transactionReceipt.toString());
                                                       String status = transactionReceipt.getStatus();
                                                       if (status.substring(2).equals("1")) {
                                                           KLog.i("交易成功");

//                                                           //把本地缓存数据改成'交易成功'状态
//                                                           messageCenterBean.setPackingStatus(1);
//                                                           DBUtil.update(messageCenterBean);

                                                           //合约交易还需要进行一下处理
                                                           if (messageCenterBean.getTxType() == 1) {
                                                               //修改合约状态
                                                               switch (messageCenterBean.getTxStatusValue()) {
                                                                   //部署成功
                                                                   case ContractStatus.MESSAGE_STSTUS_ONE:
                                                                       generateContract(context, transactionReceipt,messageCenterBean);
                                                                       break;

                                                                   case ContractStatus.MESSAGE_STSTUS_APPROVE:
                                                                       //更新'交易成功'状态
                                                                       messageCenterBean.setPackingStatus(1);
                                                                       DBUtil.update(messageCenterBean);
                                                                       break;
                                                                   case ContractStatus.MESSAGE_STSTUS_DEPOSIT_IN:   //由于不能通过ContractId找到该消息，故单独判断
                                                                       messageCenterBean.setPackingStatus(1);
                                                                       DBUtil.update(messageCenterBean);
                                                                       break;
                                                                   case ContractStatus.MESSAGE_STSTUS_DEPOSIT_OUT: //由于不能通过ContractId找到该消息，故单独判断
                                                                       messageCenterBean.setPackingStatus(1);
                                                                       DBUtil.update(messageCenterBean);
                                                                       break;
                                                                   case ContractStatus.MESSAGE_STSTUS_GET_EARN:
                                                                       messageCenterBean.setPackingStatus(1);
                                                                       DBUtil.update(messageCenterBean);
                                                                       break;
                                                                   default:
                                                                       //查询链上合约状态
                                                                       try {
                                                                           ContractBean tempContractBean = DBUtil.queryContractById(messageCenterBean.getContractId());
                                                                           Flowable.just(tempContractBean.getContractAddress())
                                                                                   .flatMap(contractAddress -> IBContractUtil.getTUSDContractInfo(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(context).getAddress(), contractAddress))
                                                                                   .subscribeOn(Schedulers.newThread())
                                                                                   .observeOn(AndroidSchedulers.mainThread())
                                                                                   .subscribe(new HLSubscriber<ContractBean>() {
                                                                                       @Override
                                                                                       protected void success(ContractBean contractBean) {
                                                                                           //更新'交易成功'状态
                                                                                           messageCenterBean.setPackingStatus(1);
                                                                                           DBUtil.update(messageCenterBean);
                                                                                           //更新合约状态
                                                                                           contractBean.setContractId(messageCenterBean.getContractId());
                                                                                           DBUtil.update(contractBean);

                                                                                           //通知合约管理页刷新数据
                                                                                           EventBus.getDefault().post(new ContractEventBean(contractBean, ContractEventBean.UPDATE_CONTRACT));
                                                                                       }

                                                                                       @Override
                                                                                       protected void failure(HLError error) {
                                                                                           KLog.w(error.getMessage());
                                                                                       }
                                                                                   });
                                                                       } catch (Exception e) {
                                                                           e.printStackTrace();
                                                                       }
                                                                       break;
                                                               }

                                                               boolean isPublish = messageCenterBean.getIsPublish();
                                                               if (isPublish){
                                                                   String contractAddress = messageCenterBean.getContractAddress();
                                                                   if (contractAddress==null){
                                                                       KLog.e("contractAddress 为空");
                                                                       return;
                                                                   }

                                                                   ContractBean contractBean = DBUtil.queryContractByAddress(contractAddress);
                                                                   if (contractBean==null){
                                                                       KLog.e("contractBean 为空");
                                                                       return;
                                                                   }

                                                                   publishContract(contractBean);
                                                               }

                                                           } else{ //转账交易
                                                               //更新'交易成功'状态
                                                               messageCenterBean.setPackingStatus(1);
                                                               DBUtil.update(messageCenterBean);

                                                           }

                                                           if (onUpdatePageData != null) {
                                                               onUpdatePageData.onTxSuccess(messageCenterBean);
                                                           }
                                                       } else {
                                                           KLog.i("交易失败");

                                                           //把本地缓存数据改成'打包失败'状态并且更新当前item的状态
                                                           messageCenterBean.setPackingStatus(2);
                                                           DBUtil.update(messageCenterBean);

                                                           if (onUpdatePageData != null) {
                                                               onUpdatePageData.onTxFailure(messageCenterBean);
                                                           }
                                                       }
                                                   }

                                               }

                                               @Override
                                               protected void failure(HLError error) {
                                                   //交易处于pending状态
//                                                   KLog.i("pending===" + error.getMessage());

                                                   new Thread(() -> {
                                                       try {
                                                           Thread.sleep(5000);
                                                           startPolling(context, pollingBooleanList, position, txHash, messageCenterBean, onUpdatePageData);
                                                       } catch (InterruptedException e) {
                                                           e.printStackTrace();
                                                       }
                                                   }).start();
                                               }
                                           }

                                );


                    }

                    @Override
                    protected void failure(HLError error) {
                        //在以太坊上查不到交易记录、即交易异常
                        //把本地缓存数据改成'打包失败'状态并且更新当前item的状态
                        KLog.i("交易凭证为空" + error.getMessage());
                        new Thread(() -> {
                            try {
                                Thread.sleep(5000);
                                startPolling(context, pollingBooleanList, position, txHash, messageCenterBean, onUpdatePageData);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                });
    }

    public interface OnUpdatePageData {
        void onTxSuccess(MessageCenterBean centerBean);

        void onTxFailure(MessageCenterBean centerBean);
    }

    public static void generateContract(Context context, TransactionReceipt transactionReceipt,MessageCenterBean messageCenterBean) {
        KLog.w(transactionReceipt.toString());
        String contractAddress = "0x" + transactionReceipt.getLogs().get(0).getData().substring(26);
        if(!DBUtil.queryContractByContractAddress(contractAddress)){
            getContract(context, contractAddress,messageCenterBean);
        }
    }

    //获取合约信息
    public static void getContract(Context context, String contractAddress,MessageCenterBean messageCenterBean) {
        try {

            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.getTUSDContractInfo(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(context).getAddress(), contractAddress))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<ContractBean>(context, false) {
                        @Override
                        protected void success(ContractBean contractBean) {
                            if(!DBUtil.queryContractByContractAddress(contractAddress) && contractBean.getContractState() != null ) {
                                //更新'交易成功'状态
                                messageCenterBean.setPackingStatus(1);
                                DBUtil.update(messageCenterBean);
                                //添加合约
                                DBUtil.insert(contractBean);
                                //通知合约管理页刷新数据
                                EventBus.getDefault().post(new ContractEventBean(contractBean, ContractEventBean.ADD_CONTRACT));
                            }
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.e(error.getMessage());
                            //延时1s再次发送
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getContract(context,contractAddress,messageCenterBean);
                                }
                            },2000);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 发布合约到市场
     */
    public static void publishContract(ContractBean contractBean) {
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
//                            if (data.getCode().equals(Constants.REQUEST_SUCCESS)) {
//                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_publish_success)).show();
//                            }
//                            else if (data.getCode().equals(Constants.REQUEST_CONTRACT_EXISTED)){
//                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_contract_existed)).show();
//                            }
//                            else {
//                                SnackbarUtil.DefaultSnackbar(mFragmentContractBinding.getRoot(), mContext.getString(R.string.contract_publish_fail)).show();
//                            }
                            KLog.e("借贷合约自动发送成功");
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

                            KLog.e("借贷合约自动发送成功");
                        }

                        @Override
                        protected void failure(HLError error) {
                            KLog.i(error.getMessage());
                        }
                    });
        }

    }


}

package com.lianer.core.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lianer.common.utils.KLog;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.contract.bean.MessageCenterEventBean;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.PollingUtil;
import com.lianer.core.utils.TransferUtil;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PollingService extends Service {

    private final String TAG = "PollingService";

    public PollingService() {
    }

    @Override
    public void onCreate() {
        KLog.w(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.w(TAG, "onStartCommand---startId: " + startId);
        updateDataSource();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        KLog.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        KLog.e(TAG, "onBind");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void updateDataSource() {
        KLog.i("service 更新数据");
        Flowable.just(1)
                .map(integer -> DBUtil.queryPackingData())//查询数据库打包中数据
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<List<MessageCenterBean>>() {
                    @Override
                    protected void success(List<MessageCenterBean> data) {
                        if (data == null || data.size() == 0) {
                            KLog.i("暂无打包中数据");
                            return;
                        }

                        KLog.i(data.get(0).toString());

                        List<Boolean> pollingBooleanList = new ArrayList<>();
                        for (int i = 0; i < data.size(); i++) {
                            pollingBooleanList.add(false);
                        }

                        //轮询操作
                        //判断状态是否在进行中状态
                        for (int i = 0; i < data.size(); i++) {
                            MessageCenterBean messageCenterBean = data.get(i);
                            if (messageCenterBean.getPackingStatus() == 0) {
                                int finalI = i;
                                try {
                                    new Thread(() -> PollingUtil.startPolling(getApplicationContext(), pollingBooleanList, finalI, messageCenterBean.getTxHash(), messageCenterBean, new PollingUtil.OnUpdatePageData() {

                                        @Override
                                        public void onTxSuccess(MessageCenterBean centerBean) {
                                            KLog.i("service 推送成功通知");
                                            //改变消息图标为带红点图标
                                            EventBus.getDefault().post(new MessageCenterEventBean());
                                            try {
                                                //同步本地nonce
                                                BigInteger nonce = IBContractUtil.transactionNonce(PollingService.this, TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(PollingService.this).getAddress());
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            CommomUtil.pushNotification(getApplicationContext(), centerBean.getTxHash(), getString(ContractStatus.MESSAGE_STATUS[messageCenterBean.getTxStatusValue()]));
                                        }

                                        @Override
                                        public void onTxFailure(MessageCenterBean centerBean) {
                                            KLog.i("service 推送失败通知");
                                            //改变消息图标为带红点图标
                                            EventBus.getDefault().post(new MessageCenterEventBean());

                                            try {
                                                //同步本地nonce
                                                BigInteger nonce = IBContractUtil.transactionNonce(PollingService.this, TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(PollingService.this).getAddress());
                                            } catch (ExecutionException e) {
                                                e.printStackTrace();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            CommomUtil.pushNotification(getApplicationContext(), centerBean.getTxHash(), getString(ContractStatus.MESSAGE_STATUS[messageCenterBean.getTxStatusValue()]));
                                        }
                                    })).start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.i(error.getMessage());
                    }
                });
    }
}
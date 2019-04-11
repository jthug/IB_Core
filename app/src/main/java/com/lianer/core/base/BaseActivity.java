package com.lianer.core.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.lianer.common.utils.ACache;
import com.lianer.common.utils.KLog;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.common.utils.qumi.QMUIStatusBarHelper;
import com.lianer.core.app.PollingService;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.ContractActivity;
import com.lianer.core.contract.bean.MessageCenterEventBean;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.utils.DBUtil;
import com.tendcloud.tenddata.TCAgent;

import org.greenrobot.eventbus.EventBus;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 固定竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 状态栏设置
        QMUIStatusBarHelper.setStatusBarDarkMode(this);
        initViews();
        if (isRegisterEventBus()) {
            EventBus.getDefault().register(this);
        }
        initData();

        Uri uridata = this.getIntent().getData();
        if(uridata != null && HLWalletManager.shared().getCurrentWallet(this) != null){
            String queryString = uridata.getQuery();
            KLog.i("queryString:" + queryString);
            String contractAddress = queryString.split("=")[1];
            KLog.i("contractAddress:" + contractAddress);
            Intent contractIntent = new Intent(this, ContractActivity.class);
            contractIntent.putExtra("ContractId", -2L);
            contractIntent.putExtra("ContractAddress", contractAddress);
            startActivity(contractIntent);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
    }

    protected abstract void initViews();

    protected abstract void initData();

    protected boolean isRegisterEventBus() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 保存消息对象到本地数据库，传递事件给相应页面，发起通知
     * @param hash             交易hash
     * @param txType           交易类型 0：转账交易 1：合约交易
     * @param txStatusValue    交易状态值 0：转出 1：部署合约 2：转入抵押 3：取回抵押 4：投资合约 5：还款 6：获取抵押
     * @param isPublish        是否发布到市场
     * @param contractAddress  合约地址
     */
    public void saveMessageAndPush(boolean isShowNotification, Long contractId, String hash, int txType, int txStatusValue,boolean isPublish,String contractAddress) {
        //改变消息图标为带红点图标
        DBUtil.insertMessageToDB(contractId,hash, txType, txStatusValue,null,null,null,isPublish,contractAddress);
        //缓存红点是否显示标识
        ACache.get(this).put(Tag.IS_RED_NOTIFICATION, "true");
        EventBus.getDefault().post(new MessageCenterEventBean());

        startService(new Intent(this, PollingService.class));

    }

    /**
     *
     * @param isShowNotification
     * @param contractId
     * @param hash                  交易hash
     * @param txType                交易类型 0：转账交易 1：合约交易
     * @param txStatusValue         交易状态值 0：转出 1：部署合约 2：转入抵押 3：取回抵押 4：投资合约 5：还款 6：获取抵押
     * @param transactionValue      交易金额
     * @param tokenSymbol           交易token类型
     * @param destAddress           转入接收钱包地址
     */
    public void saveMessageAndPush(boolean isShowNotification, Long contractId, String hash, int txType, int txStatusValue,String transactionValue,String tokenSymbol,String destAddress) {
        //改变消息图标为带红点图标
        DBUtil.insertMessageToDB(contractId,hash, txType, txStatusValue,transactionValue,tokenSymbol,destAddress,false,null);
        //缓存红点是否显示标识
        ACache.get(this).put(Tag.IS_RED_NOTIFICATION, "true");
        EventBus.getDefault().post(new MessageCenterEventBean());

        startService(new Intent(this, PollingService.class));

    }

    @Override
    protected void onPause() {
        super.onPause();
        TCAgent.onPageEnd(this,getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        TCAgent.onPageStart(this,getClass().getSimpleName());
    }
}

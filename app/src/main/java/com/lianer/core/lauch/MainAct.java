package com.lianer.core.lauch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Singleton;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.common.utils.qumi.QMUIStatusBarHelper;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.app.PollingService;
import com.lianer.core.base.BaseFragment;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.ContractFrag;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.databinding.ActivityMainBinding;
import com.lianer.core.lauch.bean.VersionBean;
import com.lianer.core.lauch.bean.VersionResponse;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.market.MarketFrag;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.HttpUtil;
import com.lianer.core.utils.PermissionUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.utils.update.UpdateDialogUtil;
import com.lianer.core.wallet.CreateAndImportActivity;
import com.lianer.core.wallet.WalletFrag;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 主界面
 *
 * @author allison
 */
public class MainAct extends FragmentActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    private List<BaseFragment> frags = new ArrayList<>();
    private int mCurrentIndex = 0;
    List<ImageView> mImageViews = new ArrayList<>();
    List<TextView> mTextViews = new ArrayList<>();
    FragmentManager fragmentManager;
    ContractFrag contractFrag;
    MarketFrag marketFrag;
    WalletFrag walletFrag;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isMainActivityDestroy", true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏设置
        QMUIStatusBarHelper.setStatusBarDarkMode(this);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState != null && savedInstanceState.getBoolean("isMainActivityDestroy", false)) {
            contractFrag = (ContractFrag) fragmentManager.findFragmentByTag("frag0");
            marketFrag = (MarketFrag) fragmentManager.findFragmentByTag("frag1");
            walletFrag = (WalletFrag) fragmentManager.findFragmentByTag("frag2");
            fragmentManager.beginTransaction().remove(contractFrag).commit();
            fragmentManager.beginTransaction().remove(marketFrag).commit();
            fragmentManager.beginTransaction().remove(walletFrag).commit();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initClick();

        initData();
        checkUpdate();
        openPollingService();

        PermissionUtil.checkPermission(this);
    }




    private void initClick() {
        binding.lyFirst.setOnClickListener(this);
        binding.lySecond.setOnClickListener(this);
        binding.lyThird.setOnClickListener(this);
    }

    /**
     * 初始化页面数据
     */
    private void initData() {
        mImageViews.add(binding.imgBottomFirst);
        mImageViews.add(binding.imgBottomSecond);
        mImageViews.add(binding.imgBottomThird);

        mTextViews.add(binding.txBottomFirst);
        mTextViews.add(binding.txBottomSecond);
        mTextViews.add(binding.txBottomThird);

        marketFrag = new MarketFrag();
        contractFrag = new ContractFrag();
        walletFrag = new WalletFrag();
        frags.add(contractFrag);
        frags.add(marketFrag);
        frags.add(walletFrag);
        binding.imgBottomFirst.setSelected(true);
        binding.txBottomFirst.setSelected(true);
        addFragmentStack(frags, R.id.main_frame, 0);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_first:
                changeTabStatus(0);
                break;
            case R.id.ly_second:
                changeTabStatus(1);
                break;
            case R.id.ly_third:
                changeTabStatus(2);
                break;

        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int index = intent.getIntExtra(Tag.INDEX, -1);
        if (index >= 0 && index < frags.size()) {
            changeTabStatus(index);
        }
    }


    /**
     * 根据位置索引改变tab状态
     */
    public void changeTabStatus(int position) {
        if (position != 0) {
            if (isNavigateToWalletCreate()) {
                return;
            }
        }
        int mSelectIndex;
        if (mCurrentIndex != position) {
            mSelectIndex = position;
        } else {
            return;
        }

        mImageViews.get(mCurrentIndex).setSelected(false);
        mImageViews.get(mSelectIndex).setSelected(true);

        mTextViews.get(mCurrentIndex).setSelected(false);
        mTextViews.get(mSelectIndex).setSelected(true);

        mCurrentIndex = mSelectIndex;
        addFragmentStack(frags, R.id.main_frame, position);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
    }

    public boolean isNavigateToWalletCreate() {
        if (HLWalletManager.shared().getCurrentWallet(this) == null) {//钱包未创建
            Intent intent = new Intent(MainAct.this, CreateAndImportActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

//     用来计算返回键的点击间隔时间
//    private long exitTime = 0;
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if ((System.currentTimeMillis() - exitTime) > 2000) {
//                //弹出提示，可以有多种方式
////                Snackbar.make(binding.getRoot(), getString(R.string.password_error), Snackbar.LENGTH_LONG)
////                        .show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    protected void addFragmentStack(List<BaseFragment> fragmentList, int add2LayoutId, int position) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment = fragmentList.get(position);
        if (!fragment.isAdded()) {
            ft.add(add2LayoutId, fragment, "frag" + position);
        }
        for (int i = 0; i < fragmentList.size(); i++) {
            if (i == position) {
                ft.show(fragmentList.get(i));
            } else {
                ft.hide(fragmentList.get(i));
            }
        }
        ft.commit();
    }

    /**
     * 判断是否需要更新
     */
    @SuppressLint("CheckResult")
    private void checkUpdate() {
        String jsonParams = "{\n" +
                "\t\"deviceType\": \"Android\"\n" +
                "}";
                HttpUtil.queryVersion(jsonParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<VersionResponse>() {
                    @Override
                    protected void success(VersionResponse versionResponse) {
                        KLog.i("版本信息===》" + Singleton.gson().toJson(versionResponse));
                        if (versionResponse.getCode().equals(Constants.REQUEST_SUCCESS)) {
                                VersionBean versionBean = versionResponse.getData();
                                if (versionBean != null) {
                                    //判断版本是否一致，是否要更新
                                    boolean isUpdate = false;
                                    try{
                                        isUpdate = Integer.parseInt(versionBean.getVersionCode()) > CommomUtil.getAppVersionCode(MainAct.this);
                                    }catch (Exception e){
                                        KLog.w(e.getMessage());
                                    }
                                    if (isUpdate) {

                                        //同步链上与本地nonce
                                        try {
                                            BigInteger nonce = IBContractUtil.transactionNonce(MainAct.this, TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(MainAct.this).getAddress());
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        new UpdateDialogUtil(new CenterDialog(R.layout.dlg_version_update,MainAct.this), MainAct.this, versionBean, null);
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

    /**
     * 开启轮询服务
     */
    public void openPollingService() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, PollingService.class));
//        } else {
            startService(new Intent(this, PollingService.class));
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (frags.size() != 0) {
            frags.clear();
        }
        if (mImageViews.size() != 0) {
            mImageViews.clear();
        }
        if (mTextViews.size() != 0) {
            mTextViews.clear();
        }
    }
}

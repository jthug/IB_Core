package com.lianer.core.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;

import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.custom.RVItemDecoration;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityWalletManagerListBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.wallet.adapter.WalletListAdapter;

import org.web3j.crypto.WalletFile;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 多钱包管理界面
 */
public class WalletManagerListAct extends BaseActivity implements WalletListAdapter.OnItemClickListener {

    private ActivityWalletManagerListBinding mBinding;
    private List<HLWallet> mWallets;
    private WalletListAdapter mWalletListAdapter;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_manager_list);

        initTitleBar();

        initRecyclerView();
    }

    private void initRecyclerView() {
        mBinding.rvWalletList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBinding.rvWalletList.addItemDecoration(new RVItemDecoration(LinearLayoutManager.VERTICAL, 40));

    }

    private void initTitleBar() {
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
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mWallets = HLWalletManager.shared().getWallets();
        mWalletListAdapter = new WalletListAdapter(mWallets);
        mWalletListAdapter.setOnItemClickListener(this);
        mBinding.rvWalletList.setAdapter(mWalletListAdapter);
    }

    @Override
    public void onItemClick(int tag, int position) {
        switch (tag) {
            case WalletListAdapter.IMPORTORCREATE:
                if (HLWalletManager.shared().getWallets().size() >= 5) {
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.wallet_number_limit)).show();
                } else {
                    Intent intent = new Intent(this, CreateAndImportActivity.class);
                    startActivity(intent);
                }
                break;
            case WalletListAdapter.WALLETSETTING:
                Intent intent1 = new Intent(this, WalletSettingActivity.class);
                HLWallet hlWallet = mWallets.get(position);
                WalletFile walletFile = hlWallet.getWalletFile();
                intent1.putExtra("walletAddress", hlWallet.getAddress());
                intent1.putExtra("walletName", hlWallet.getWalletName());
                startActivity(intent1);
                break;
            case WalletListAdapter.SWITCHWALLET:

                //每次切换钱包重新缓存当前钱包nonce
                long nonce = 0;
                try {
                    nonce = IBContractUtil.getNonce(TransferUtil.getWeb3j(), mWallets.get(position).getAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                    SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.nonce_error)).show();
                    return;
                }
                SharedPreferences sp = getSharedPreferences(Constants.TRANSACTION_INFO, Context.MODE_PRIVATE);
                HLWalletManager.shared().switchCurrentWallet(WalletManagerListAct.this, mWallets.get(position));
                mWalletListAdapter.refreshData();

                sp.edit().putLong(Constants.TRANSACTION_NONCE, nonce).apply();
                Flowable.just(1)
                        .delay(300,TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                finish();
                            }
                        });

                break;
        }
    }
}

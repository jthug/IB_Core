package com.lianer.core.contract;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.lianer.common.utils.KLog;
import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityImportContractBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.stuff.ScheduleCompat;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.utils.TransferUtil;
import com.lianer.core.zxinglibrary.android.CaptureActivity;
import com.lianer.core.zxinglibrary.common.Constant;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 转入收款
 *
 * @author bowen
 */
public class ImportContractActivity extends BaseActivity implements View.OnClickListener {

    private ActivityImportContractBinding mBinding;
    private String mAddress;
    private String dataContractAddress;
    private HLWallet mWallet;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_import_contract);
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
        mBinding.btnScan.setOnClickListener(this);
        mBinding.contractAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 42) {
                    mBinding.btnImport.setEnabled(true);
                } else {
                    mBinding.btnImport.setEnabled(false);
                }
                mAddress = s.toString();
                KLog.w(s.length() + "");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.btnImport.setOnClickListener(this);
        mWallet = HLWalletManager.shared().getCurrentWallet(this);
    }

    //获取合约信息
    private void getContract(String contractAddress) {
        try {

            Flowable.just(1)
                    .flatMap(s -> IBContractUtil.getTUSDContractInfo(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(this).getAddress(), contractAddress))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new HLSubscriber<ContractBean>(ImportContractActivity.this, true) {
                        @Override
                        protected void success(ContractBean contractBean) {
                            //添加
                            DBUtil.insert(contractBean);
                            SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.import_contract_success)).show();

                            Flowable.just(1)
                                    .delay(2000, TimeUnit.MILLISECONDS)
                                    .compose(ScheduleCompat.apply())
                                    .subscribe(integer -> {
                                        finish();
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


    //合约校验
    private void chekcContract() {
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.isIBContract(TransferUtil.getWeb3j(), mWallet.getAddress(), mAddress))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<Boolean>(ImportContractActivity.this, true) {
                    @Override
                    protected void success(Boolean isIBContract) {

                        if (isIBContract) {
                            if (DBUtil.queryContractByContractAddress(mAddress)) {
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.import_fail_existed)).show();
                            } else {
                                //DB 合约不存在
                                getContract(mAddress);
                            }
                        } else {
                            //Tusd 合约校验
                            checkTusdContract();
                        }
                    }

                    @Override
                    protected void failure(HLError error) {

                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.import_fail_not_nest)).show();
                    }
                });


    }

    //Tusd 合约校验
    private void checkTusdContract(){
        Flowable.just(1)
                .flatMap(s -> IBContractUtil.isIBTusdContract(TransferUtil.getWeb3j(), mWallet.getAddress(), mAddress))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<Boolean>(ImportContractActivity.this, true) {
                    @Override
                    protected void success(Boolean isIBContract) {

                        if (isIBContract) {
                            if (DBUtil.queryContractByContractAddress(mAddress)) {
                                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.import_fail_existed)).show();
                            } else {
                                //DB 合约不存在
                                getContract(mAddress);
                            }
                        } else {
                            SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.import_fail_not_nest)).show();
                        }
                    }

                    @Override
                    protected void failure(HLError error) {
                        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),getString(R.string.import_fail_not_nest)).show();
                    }
                });
    }

    @Override
    protected void initData() {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 扫描二维码/条码回传
        if (requestCode == 111 && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                String address = content;
                if (address.contains("LoanContractAddress=")) {
                    address = address.split("=")[1];
                }
                mBinding.contractAddress.setText(address);
            } else {
                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(), getString(R.string.scan_cancel)).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_import:
                chekcContract();

                break;

            case R.id.btn_scan:
                AndPermission.with(this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(ImportContractActivity.this, CaptureActivity.class);
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
                break;
        }
    }
}

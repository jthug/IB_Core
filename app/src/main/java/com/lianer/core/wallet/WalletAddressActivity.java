package com.lianer.core.wallet;

import android.databinding.DataBindingUtil;
import android.view.View;

import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityReceiptBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.QRCodeUtil;

/**
 * 转入收款
 *
 * @author bowen
 */
public class WalletAddressActivity extends BaseActivity {

    private ActivityReceiptBinding mBinding;
    private String mAddress;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_receipt);
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
        mBinding.btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommomUtil.copy(WalletAddressActivity.this,mBinding.getRoot(),mAddress);
            }
        });

    }

    @Override
    protected void initData() {
        mAddress = HLWalletManager.shared().getCurrentWallet(this).getAddress();
        mBinding.showQrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(mAddress, 720, 720));
        mBinding.walletAddress.setText(mAddress);
    }
}

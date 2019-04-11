package com.lianer.core.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.lianer.core.base.BaseFragment;
import com.lianer.common.utils.Singleton;
import com.lianer.core.R;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.utils.QRCodeUtil;


public class ExportKeystoreQRCodeFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    Button BtnShow;
    ImageView showQRCode;
    String keystore;
    boolean isShow;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_export_keystore_qr_code, null);

        BtnShow = mView.findViewById(R.id.btn_show);
        showQRCode =  mView.findViewById(R.id.show_qr_code);
        BtnShow.setOnClickListener(this);

        return mView;
    }



    @Override
    protected void initData() {
        super.initData();
//        HLWallet currentWallet = HLWalletManager.shared().getCurrentWallet(getContext());
        ExportKeystoreActivity activity = (ExportKeystoreActivity) getActivity();
        HLWallet wallet = HLWalletManager.shared().getWallet(activity.mWalletAddress);
        keystore = Singleton.gson().toJson(wallet.walletFile);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_show:
                if(isShow){
                    showQRCode.setImageResource(R.drawable.hide_qr_code);
                    BtnShow.setText(getString(R.string.show_qr_code));
                }else{
                    showQRCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(keystore,720,720));
                    BtnShow.setText(getString(R.string.hide_qr_code));
                }
                isShow = !isShow;
                break;
        }
    }
}

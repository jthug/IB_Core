package com.lianer.core.wallet;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lianer.core.base.BaseFragment;
import com.lianer.common.utils.Singleton;
import com.lianer.core.R;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.utils.SnackbarUtil;


public class ExportKeystoreFileFragment extends BaseFragment implements View.OnClickListener {

    View mView;
    Button BtnCopy;
    TextView showKeystore;
    String keystore;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_export_keystore_file, null);

        BtnCopy = mView.findViewById(R.id.btn_copy);
        showKeystore =  mView.findViewById(R.id.show_keystore);
        BtnCopy.setOnClickListener(this);

        return mView;
    }



    @Override
    protected void initData() {
        super.initData();
//        HLWallet currentWallet = HLWalletManager.shared().getCurrentWallet(getContext());
        ExportKeystoreActivity activity = (ExportKeystoreActivity) getActivity();
        HLWallet wallet = HLWalletManager.shared().getWallet(activity.mWalletAddress);
        keystore = Singleton.gson().toJson(wallet.walletFile);
        showKeystore.setText(keystore);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_copy:
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(keystore);
                SnackbarUtil.DefaultSnackbar(mView,getString(R.string.copy_success)).show();
                break;
        }
    }
}

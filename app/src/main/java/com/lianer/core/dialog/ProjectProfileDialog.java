package com.lianer.core.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.lianer.core.R;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.contract.bean.TokenMortgageBean;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.SnackbarUtil;

/**
 * 项目介绍弹窗
 * @author allison
 */
public class ProjectProfileDialog implements View.OnClickListener {
    Context mContext;
    Dialog mDialog;
    BtnListener mListener;
    ContractResponse mContractResponse;
    TokenMortgageBean tokenMortgageBean;
    View mView;

    /**
     * @param mDialog
     * @param listener
     */
    public ProjectProfileDialog(View view, Context context, Dialog mDialog, ContractResponse contractResponse, BtnListener listener) {
        this.mView = view;
        this.mContext = context;
        this.mListener = listener;
        this.mDialog = mDialog;
        mContractResponse = contractResponse;
        __init();
    }

    private void __init() {

        tokenMortgageBean = MortgageAssets.getTokenBean(mContext,mContractResponse.getMortgageAssetsType());
        if (tokenMortgageBean != null) {
            //项目名称记简称
            TextView projectIntroduction = mDialog.findViewById(R.id.project_introduction);
            projectIntroduction.setText(String.format(mContext.getString(R.string.project_introduction),
                    tokenMortgageBean.getTokenAbbreviation(),
                    tokenMortgageBean.getTokenAbbreviation()));
            //token地址
            TextView tokenAddress = mDialog.findViewById(R.id.token_address);
            tokenAddress.setText(tokenMortgageBean.getTokenAddress());
            //项目简介
            TextView tokenPrile = mDialog.findViewById(R.id.token_profile);
            tokenPrile.setText(tokenMortgageBean.getTokenAbbreviation());
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
            TextView warnKnow = mDialog.findViewById(R.id.i_know);
            TextView selectProject = mDialog.findViewById(R.id.select_project);
            TextView copyTokenAddress = mDialog.findViewById(R.id.copy_token_address);
            warnKnow.setOnClickListener(this);
            selectProject.setOnClickListener(this);
            copyTokenAddress.setOnClickListener(this);
            mDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        mDialog.dismiss();
        switch (v.getId()) {
            case R.id.select_project://查询项目
                Intent intent = new Intent(mContext, EtherScanWebActivity.class);
                intent.putExtra("ContractAddress",  tokenMortgageBean.getTokenAddress());
                mContext.startActivity(intent);
                break;
            case R.id.copy_token_address://复制Token地址
                SnackbarUtil.DefaultSnackbar(mView, mContext.getString(R.string.copy_success)).show();
                CommomUtil.copyText(mContext, tokenMortgageBean.getTokenAddress());
                break;
        }

    }

    public interface BtnListener {
        void iKnow();
    }
}

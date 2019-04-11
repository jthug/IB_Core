package com.lianer.core.contract.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lianer.common.utils.Utils;
import com.lianer.core.R;
import com.lianer.core.app.Constants;
import com.lianer.core.borrow.BannerDetailAct;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.contract.DisbandContractAct;
import com.lianer.core.contract.GetMortgageAssetsAct;
import com.lianer.core.contract.RepaymentActivity;
import com.lianer.core.contract.bean.ContractResponse;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.invest.InvestActivity;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.SnackbarUtil;
import com.lianer.core.wallet.CreateAndImportActivity;

import org.web3j.utils.Convert;

public class ContractDetailModel {

    Context mContext;

    public ContractDetailModel(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 加载已投资布局
     */
    public void loadInvestView(ContractResponse contractResponse, View rootView) {
        //加载底部布局
        ViewStub viewStub = rootView.findViewById(R.id.vs_invest);
        View loadView = viewStub.inflate();

        //设置按钮的状态颜色及点击事件
        TextView nowInvest = rootView.findViewById(R.id.bottom_btn);

        final boolean[] isAgree = {true};

        //判断服务器状态处于哪种静态状态
        String status = CommomUtil.judgeContractStatus(contractResponse.getContractStatus());
        if (TextUtils.isEmpty(status)) {
            return;
        }

        switch (status) {
            case ContractStatus.RELEASED://可投资，已发布
                TimeScheduleModel.setInvestScheduleTo8(mContext, rootView, contractResponse);

                //显示协议选择按钮
                loadView.findViewById(R.id.layout_agree).setVisibility(View.VISIBLE);
                TextView agreement = loadView.findViewById(R.id.agree);
                agreement.setOnClickListener(v -> {
                    isAgree[0] = !isAgree[0];
                    agreement.setCompoundDrawablesWithIntrinsicBounds(isAgree[0] ? mContext.getResources().getDrawable(
                            R.drawable.ic_agreement_selected) : mContext.getResources().getDrawable(
                            R.drawable.ic_agreement_unselected),
                            null, null, null);
                });
                TextView userAgreement = loadView.findViewById(R.id.user_agreement);
                //协议跳转
                userAgreement.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, BannerDetailAct.class);
//                if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_CHINESE);
//                } else {
//                    intent.putExtra("webUrl", Constants.USER_AGREEMENT_ENGLISH);
//                }
                    intent.putExtra("webUrl", Constants.USER_AGREEMENT);
                    mContext.startActivity(intent);
                });

                //获取投资信息
                if (contractResponse.getInvestment() != null && contractResponse.getInvestment().getBorrowerAddress()
                        .equalsIgnoreCase(HLWalletManager.shared()
                                .getCurrentWallet(mContext).getAddress())) {//投资人地址和当前钱包的地址相同，说明该合约对于当前地址为正在投资中状态
                    nowInvest.setText(R.string.investing);
                    nowInvest.setTextColor(Color.parseColor("#8C8C8C"));
                    nowInvest.setEnabled(false);
                    nowInvest.setBackgroundResource(R.drawable.gray_rectangle_bg);
                } else {//投资人地址和当前钱包的地址不相同，说明该合约对于当前地址为可投资状态
                    nowInvest.setText(R.string.now_invest);
                    nowInvest.setTextColor(Color.parseColor("#ffffff"));
                    nowInvest.setEnabled(true);
                    nowInvest.setBackgroundResource(R.drawable.gradient_rectangle_bg);
                    nowInvest.setOnClickListener(v -> {//跳转到投资页面
                        if (!isAgree[0]) {
                            SnackbarUtil.DefaultSnackbar(rootView, mContext.getString(R.string.agree_agreement)).show();
                            return;
                        }

                        if (HLWalletManager.shared().getCurrentWallet(mContext) == null) {//钱包不存在
                            Intent intent = new Intent(mContext, CreateAndImportActivity.class);
                            mContext.startActivity(intent);
                            return;
                        }

                        Intent intent = new Intent(mContext, InvestActivity.class);
                        intent.putExtra(Constants.CONTRACT_RESPONSE, contractResponse);
                        mContext.startActivity(intent);
                    });

                }

                //最底部按钮点击状态和背景颜色设置

                break;
            case ContractStatus.EFFECTED://已投资/已生效
                TimeScheduleModel.setInvestScheduleTo16(mContext, rootView, contractResponse);

                nowInvest.setText(R.string.invested);
                nowInvest.setTextColor(Color.parseColor("#8C8C8C"));
                nowInvest.setEnabled(false);
                nowInvest.setBackgroundResource(R.drawable.gray_rectangle_bg);
                break;
            case ContractStatus.REPAYMENT://借方已还款
                TimeScheduleModel.setInvestScheduleTo21(mContext, rootView, contractResponse);

                nowInvest.setText(R.string.repaymented);
                nowInvest.setTextColor(Color.parseColor("#8C8C8C"));
                nowInvest.setEnabled(false);
                nowInvest.setBackgroundResource(R.drawable.gray_rectangle_bg);
                break;
            case ContractStatus.OVERDUE://已逾期--取回抵押资产
                TimeScheduleModel.setInvestScheduleTo22(mContext, rootView, contractResponse);

                nowInvest.setText(R.string.take_back_assets);
                nowInvest.setTextColor(Color.parseColor("#ffffff"));
                nowInvest.setEnabled(true);
                nowInvest.setBackgroundResource(R.drawable.gradient_rectangle_bg);
                nowInvest.setOnClickListener(v -> {//跳转到取回抵押资产页面
                    Intent intent = new Intent(mContext, GetMortgageAssetsAct.class);
                    intent.putExtra(Constants.CONTRACT_RESPONSE, contractResponse);
                    mContext.startActivity(intent);
                });
                break;
            case ContractStatus.RECAPTURED://已取回--抵押资产已取回
                TimeScheduleModel.setInvestScheduleTo26(mContext, rootView, contractResponse);

                nowInvest.setText(R.string.taken_back_assets);
                nowInvest.setTextColor(Color.parseColor("#8C8C8C"));
                nowInvest.setEnabled(false);
                nowInvest.setBackgroundResource(R.drawable.gray_rectangle_bg);
                break;
        }

        //借方钱包地址
        TextView borrowAddress = loadView.findViewById(R.id.borrow_address);
        borrowAddress.setText(contractResponse.getBorrowAddress());
        borrowAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getBorrowAddress()));
    }

    /**
     * 加载借币布局
     */
    public void loadBorrowView(Context context, ContractResponse contractResponse, View rootView, TitlebarView titlebarView) {
        //设置手续费和实际到账数量值
        LinearLayout borrowCenter = rootView.findViewById(R.id.ll_borrow_center);
        borrowCenter.setVisibility(View.VISIBLE);
        TextView poundage = borrowCenter.findViewById(R.id.poundage);
        poundage.setText(Utils.switchTo4Decimal(Convert.fromWei(contractResponse.getPoundage(), Convert.Unit.ETHER).toString()));
        TextView actualAmount = borrowCenter.findViewById(R.id.actual_amount);
        actualAmount.setText(Utils.switchTo4Decimal(Convert.fromWei(contractResponse.getActualAmount(), Convert.Unit.ETHER).toString()));

        //加载底部布局
        ViewStub viewStub = rootView.findViewById(R.id.vs_borrow);
        View loadView = viewStub.inflate();

        //判断服务器状态处于哪种静态状态
        String status = CommomUtil.judgeContractStatus(contractResponse.getContractStatus());
        if (TextUtils.isEmpty(status)) {
            return;
        }
        switch (status) {
            case ContractStatus.RELEASED://可投资，已发布
                TimeScheduleModel.setInvestScheduleTo8(mContext, rootView, contractResponse);

                TextView bs = rootView.findViewById(R.id.bottom_btn);
                bs.setVisibility(View.GONE);

                //显示需要的布局
                rootView.findViewById(R.id.released_layout).setVisibility(View.VISIBLE);

                //手续费转入地址
                TextView poundageAddress = loadView.findViewById(R.id.poundage_address);
                poundageAddress.setText(Constants.POUNDAGE_ADDRESS);
                poundageAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));

                //合约解散点击事件
                Button disbandContract = rootView.findViewById(R.id.disband_contract);
                disbandContract.setOnClickListener(v -> {
                    Intent intent = new Intent(context, DisbandContractAct.class);
                    intent.putExtra(Constants.CONTRACT_RESPONSE, contractResponse);
                    context.startActivity(intent);
                });

                //合约解散说明点击事件
                TextView contractDisbandExplain = rootView.findViewById(R.id.contract_disband_explain);
                contractDisbandExplain.setOnClickListener(v -> new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, context), null, context.getString(R.string.contract_disband_explain), context.getString(R.string.disband_explain), Gravity.START));
                break;
            case ContractStatus.EFFECTED://已生效
            case ContractStatus.EXPIRED://快逾期
                //还款说明点击事件
                titlebarView.setRightWidgetVisible(1);
                titlebarView.setRightText(mContext.getString(R.string.repayment_statement));
                titlebarView.findViewById(R.id.tv_right).setOnClickListener(v -> {
                    //还款提醒
                    new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, context), null, context.getString(R.string.repayment_statement),
                            context.getString(R.string.repayment_statement_content));
                });
                TimeScheduleModel.setBorrowScheduleTo16(mContext, rootView, contractResponse);

                rootView.findViewById(R.id.effect_layout).setVisibility(View.VISIBLE);
                //投资方钱包地址
                TextView investerWalletAddress = rootView.findViewById(R.id.invester_wallet_address);
                investerWalletAddress.setText(contractResponse.getInvestmentAddress());
                investerWalletAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getInvestmentAddress()));

                //手续费转入地址
                TextView poundageToAddress = rootView.findViewById(R.id.poundage_to_address);
                poundageToAddress.setText(Constants.POUNDAGE_ADDRESS);
                poundageToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));
                //按钮显示的状态
                TextView borrowStatus = rootView.findViewById(R.id.bottom_btn);
                borrowStatus.setText(R.string.repay);
                borrowStatus.setBackgroundResource(R.drawable.gradient_rectangle_bg);
                borrowStatus.setTextColor(Color.parseColor("#ffffff"));
                borrowStatus.setOnClickListener(v ->
                {
                    Intent intent = new Intent(context, RepaymentActivity.class);
                    intent.putExtra(Constants.CONTRACT_RESPONSE, contractResponse);
                    context.startActivity(intent);
                });
                break;
            case ContractStatus.OVERDUE://已逾期
                TimeScheduleModel.setBorrowScheduleTo22(mContext, rootView, contractResponse);

                rootView.findViewById(R.id.effect_layout).setVisibility(View.VISIBLE);
                TextView investerWalletAddress2 = rootView.findViewById(R.id.invester_wallet_address);
                investerWalletAddress2.setText(contractResponse.getInvestmentAddress());
                investerWalletAddress2.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getInvestmentAddress()));

                TextView poundageToAddress2 = rootView.findViewById(R.id.poundage_to_address);
                poundageToAddress2.setText(Constants.POUNDAGE_ADDRESS);
                poundageToAddress2.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));
                TextView borrowStatus2 = rootView.findViewById(R.id.bottom_btn);
                borrowStatus2.setText(R.string.overdue);
                break;
            case ContractStatus.REPAYMENT://已还款
                TimeScheduleModel.setBorrowScheduleTo21(mContext, rootView, contractResponse);

                rootView.findViewById(R.id.effect_layout).setVisibility(View.VISIBLE);
                TextView investerWalletAddress3 = rootView.findViewById(R.id.invester_wallet_address);
                investerWalletAddress3.setText(contractResponse.getInvestmentAddress());
                investerWalletAddress3.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getInvestmentAddress()));

                TextView poundageToAddress3 = rootView.findViewById(R.id.poundage_to_address);
                poundageToAddress3.setText(Constants.POUNDAGE_ADDRESS);
                poundageToAddress3.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));
                TextView borrowStatus3 = rootView.findViewById(R.id.bottom_btn);
                borrowStatus3.setText(R.string.repaid);
                break;
            case ContractStatus.RECAPTURED://已取回
                TimeScheduleModel.setBorrowScheduleTo26(mContext, rootView, contractResponse);

                rootView.findViewById(R.id.effect_layout).setVisibility(View.VISIBLE);
                TextView iWalletAddress = rootView.findViewById(R.id.invester_wallet_address);
                iWalletAddress.setText(contractResponse.getInvestmentAddress());
                iWalletAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getInvestmentAddress()));

                TextView pToAddress = rootView.findViewById(R.id.poundage_to_address);
                pToAddress.setText(Constants.POUNDAGE_ADDRESS);
                pToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));
                TextView bStatus = rootView.findViewById(R.id.bottom_btn);
                bStatus.setText(R.string.taken_back_assets);
                break;
            case ContractStatus.DISBANDED://已解散
                rootView.findViewById(R.id.effect_layout).setVisibility(View.VISIBLE);
                TextView inWalletAddress = rootView.findViewById(R.id.invester_wallet_address);
                inWalletAddress.setText(contractResponse.getInvestmentAddress());
                inWalletAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getInvestmentAddress()));

                TextView pounToAddress = rootView.findViewById(R.id.poundage_to_address);
                pounToAddress.setText(Constants.POUNDAGE_ADDRESS);
                pounToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));
                TextView borStatus = rootView.findViewById(R.id.bottom_btn);
                borStatus.setText(R.string.disbanded);
                break;
            case ContractStatus.TERMINATED://已终止
                rootView.findViewById(R.id.effect_layout).setVisibility(View.VISIBLE);
                TextView inveWalletAddress = rootView.findViewById(R.id.invester_wallet_address);
                inveWalletAddress.setText(contractResponse.getInvestmentAddress());
                inveWalletAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, contractResponse.getInvestmentAddress()));

                TextView poundaToAddress = rootView.findViewById(R.id.poundage_to_address);
                poundaToAddress.setText(Constants.POUNDAGE_ADDRESS);
                poundaToAddress.setOnClickListener(v -> CommomUtil.navigateToEthScan(mContext, true, Constants.POUNDAGE_ADDRESS));
                TextView borroStatus = rootView.findViewById(R.id.bottom_btn);
                borroStatus.setText(R.string.terminated);
                break;
        }
    }

}

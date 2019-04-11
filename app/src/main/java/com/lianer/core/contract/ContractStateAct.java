package com.lianer.core.contract;

import android.databinding.DataBindingUtil;

import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityBorrowContractStateBinding;

/**
 * 合约状态页面
 *
 * @author allison
 */
public class ContractStateAct extends BaseActivity {

    ActivityBorrowContractStateBinding borrowContractStateBinding;

    @Override
    protected void initViews() {
        borrowContractStateBinding = DataBindingUtil.setContentView(this, R.layout.activity_borrow_contract_state);
        initTitleBar();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        int titleType = 0;
        switch (titleType) {
            case 0://解散合约
                borrowContractStateBinding.titlebar.setTitle(getString(R.string.disband_contract));
                break;
            case 1://还款
                borrowContractStateBinding.titlebar.setTitle(getString(R.string.status_repayment));
                break;
        }
        borrowContractStateBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
            @Override
            public void leftClick() {
                finish();
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
        loadViewData();
    }

    /**
     * 加载控件数据
     */
    private void loadViewData() {
        //图片状态
        //文字状态
        //地址类型
        //按钮文字及点击事件
        int status = 0;
        switch (status) {
            case 0://合约解散中
                borrowContractStateBinding.include.statusPicture.setImageResource(R.drawable.contract_disbanding);
                borrowContractStateBinding.include.statusContent.setText(R.string.contract_disbanding);
                //hash地址
                borrowContractStateBinding.include.statusHashAddress.setText("hash地址");
                borrowContractStateBinding.include.btnAction.setText(R.string.back);
                borrowContractStateBinding.include.address.setText(R.string.hash_address);
                break;
            case 1://合约解散成功
                borrowContractStateBinding.include.statusPicture.setImageResource(R.drawable.contract_disband_success);
                borrowContractStateBinding.include.statusContent.setText(R.string.contract_disband_success);
                //hash地址
                borrowContractStateBinding.include.statusHashAddress.setText("合约地址");
                borrowContractStateBinding.include.btnAction.setText(R.string.see_wallet);
                borrowContractStateBinding.include.address.setText(R.string.contract_address);
                break;
            case 2://合约解散失败
                borrowContractStateBinding.include.statusPicture.setImageResource(R.drawable.contract_disband_fail);
                borrowContractStateBinding.include.statusContent.setText(R.string.contract_disband_fail);
                //hash地址
                borrowContractStateBinding.include.statusHashAddress.setText("hash地址");
                borrowContractStateBinding.include.btnAction.setText(R.string.return_to_retry);
                borrowContractStateBinding.include.address.setText(R.string.hash_address);
                break;
            case 3://还款中
                borrowContractStateBinding.include.statusPicture.setImageResource(R.drawable.repaymenting);
                borrowContractStateBinding.include.statusContent.setText(R.string.repaymenting);
                //hash地址
                borrowContractStateBinding.include.statusHashAddress.setText("hash地址");
                borrowContractStateBinding.include.btnAction.setText(R.string.back);
                borrowContractStateBinding.include.address.setText(R.string.hash_address);
                break;
            case 4://还款成功
                borrowContractStateBinding.include.statusPicture.setImageResource(R.drawable.repayment_success);
                borrowContractStateBinding.include.statusContent.setText(R.string.repayment_success);
                //hash地址
                borrowContractStateBinding.include.statusHashAddress.setText("合约地址");
                borrowContractStateBinding.include.btnAction.setText(R.string.see_wallet);
                borrowContractStateBinding.include.address.setText(R.string.contract_address);
                break;
            case 5://还款失败
                borrowContractStateBinding.include.statusPicture.setImageResource(R.drawable.repayment_fail);
                borrowContractStateBinding.include.statusContent.setText(R.string.repayment_fail);
                //hash地址
                borrowContractStateBinding.include.statusHashAddress.setText("hash地址");
                borrowContractStateBinding.include.btnAction.setText(R.string.return_to_retry);
                borrowContractStateBinding.include.address.setText(R.string.hash_address);
                break;

        }
    }
}

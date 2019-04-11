package com.lianer.core.contract;

import android.databinding.DataBindingUtil;

import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.contract.model.ContractOperateModel;
import com.lianer.core.databinding.ActivityContractOperateBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 合约操作 导出合约/删除合约
 *
 * @author allison
 */
public class ContractOperateAct extends BaseActivity {

    List<ContractBean> dataList = new ArrayList<>();
    ActivityContractOperateBinding contractOperateBinding;
    ContractOperateModel contractOperateModel;


    @Override
    protected void initViews() {
        String contractOprateType = getIntent().getStringExtra("contractOperateType");
        contractOperateBinding = DataBindingUtil.setContentView(this, R.layout.activity_contract_operate);

        contractOperateBinding.contractOperate.setEnabled(false);
        //改变标题文字
        //改变操作按钮文字 导出/删除
        if (contractOprateType.equals("export")) {//导出
            contractOperateBinding.contractOperateTitle.setText(R.string.output_contract);
            contractOperateBinding.contractOperate.setText(R.string.export);

        } else {//删除
            contractOperateBinding.contractOperateTitle.setText(R.string.delete_contract);
            contractOperateBinding.contractOperate.setText(R.string.delete);
        }

        contractOperateModel = new ContractOperateModel(this, dataList, contractOperateBinding, contractOprateType);
        contractOperateModel.initRecyclerView();
        contractOperateModel.initRefresh();

        contractOperateModel.initClick();
    }

    @Override
    protected void initData() {
        contractOperateModel.loadData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataList.size() != 0) {
            dataList.clear();
        }
    }
}

package com.lianer.core.contract;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lianer.common.utils.ACache;
import com.lianer.core.base.BaseFragment;
import com.lianer.core.R;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.contract.bean.ContractEventBean;
import com.lianer.core.contract.bean.MessageCenterEventBean;
import com.lianer.core.contract.model.ContractModel;
import com.lianer.core.databinding.FragmentContractBinding;
import com.lianer.core.lauch.MainAct;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.stuff.LWallet;
import com.lianer.core.utils.PopupWindowUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.tx.ChainId;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 合约页
 *
 * @author allison
 */
public class ContractFrag extends BaseFragment {

    List<ContractBean> dataList = new ArrayList<>();
    ContractModel contractModel;
    FragmentContractBinding fragmentContractBinding;

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initData();
        }
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentContractBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contract, container, false);

        contractModel = new ContractModel(mActivity, dataList, fragmentContractBinding);
        contractModel.initRecyclerView();
        contractModel.initRefresh();
        //刷新合约
        contractModel.refershContract();
        initClick();

        //铃铛的图片
        String isRedNotification = ACache.get(getContext()).getAsString(Tag.IS_RED_NOTIFICATION) == null ? "false" : ACache.get(getContext()).getAsString(Tag.IS_RED_NOTIFICATION);
        if (isRedNotification.equals("true")) {
            fragmentContractBinding.ivLeft.setImageResource(R.drawable.notification_red_dot_selector);
        } else {
            fragmentContractBinding.ivLeft.setImageResource(R.drawable.notification_selector);
        }
        return fragmentContractBinding.getRoot();
    }

    @Override
    protected void initData() {
        contractModel.loadData();
    }

    /**
     * 初始化点击事件
     */
    private void initClick() {
        //部署ETH合约
        fragmentContractBinding.deployEthContract.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContractActivity.class);
            intent.putExtra("ContractId", -1L);
            startActivity(intent);
        });
        //部署Tusd合约
        fragmentContractBinding.deployTusdContract.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ContractTusdActivity.class);
            intent.putExtra("ContractId", -1L);
            startActivity(intent);
        });
        //去市场看看
        fragmentContractBinding.marketLook.setOnClickListener(v -> {
            if (mActivity != null) {
                ((MainAct) mActivity).changeTabStatus(1);
            }
        });

        fragmentContractBinding.ivLeft.setOnClickListener(v -> {
            //点击之后铃铛取消红点
            fragmentContractBinding.ivLeft.setImageResource(R.drawable.notification_selector);
            //把本地消息红点状态数据修改为false
            ACache.get(getContext()).put(Tag.IS_RED_NOTIFICATION, "false");
            Intent intent = new Intent(getContext(), MessageCenterAct.class);
            startActivity(intent);
        });
        fragmentContractBinding.contractOperate.setOnClickListener(v -> PopupWindowUtil.menuPopupWindow(getActivity()).showAtLocation(getView(), Gravity.TOP | Gravity.END, 0, 0));
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEvent(MessageCenterEventBean event) {
        if (fragmentContractBinding != null) {
            fragmentContractBinding.ivLeft.setImageResource(R.drawable.notification_red_dot_selector);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void contractEvent(ContractEventBean event) {
        switch (event.getEventType()) {
            case ContractEventBean.ADD_CONTRACT:
                if (contractModel != null) {
                    if (contractModel.mDataList != null) {
                        if (!contractModel.mDataList.contains(event.getContractBean())) {
                            contractModel.mDataList.add(0, event.getContractBean());
                            contractModel.quickAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
            case ContractEventBean.UPDATE_CONTRACT:
                if (contractModel != null) {
                    if (contractModel.mDataList != null) {
                        List<ContractBean> tempContractList = new ArrayList<>(contractModel.mDataList);
                        for (int i = 0; i < tempContractList.size(); i++) {
                            if (event.getContractBean().getContractId().equals(tempContractList.get(i).getContractId())) {
                                contractModel.mDataList.set(i, event.getContractBean());
                                contractModel.quickAdapter.notifyItemChanged(i);
                            }
                        }
                    }
                }
                break;
        }


    }

}

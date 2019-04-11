package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.lianer.core.app.NestApp;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.ACache;
import com.lianer.common.utils.Utils;
import com.lianer.core.R;
import com.lianer.core.config.Tag;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityConfirmMnemonicBinding;
import com.lianer.core.dialog.KnowDialog;
import com.lianer.core.lauch.MainAct;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;
import com.lianer.core.wallet.bean.WalletAddrEventBean;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class ConfirmMnemonicAct extends BaseActivity {

    private ActivityConfirmMnemonicBinding confirmMnemonicBinding;
    private List<String> mnemonicData = new ArrayList<>();//原助记词
    private List<String> mnemonicRandomData = new ArrayList<>();//随机化助记词
    private List<String> mnemonicSelectData = new ArrayList<>();//选择助记词
    private List<Boolean> mnemonicImgVisible = new ArrayList<>();//助记词选中状态
    private QuickAdapter quickAdapter;
    private int selectIndex;//助记词选择索引

    @Override
    protected void initViews() {
        confirmMnemonicBinding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_mnemonic);
        confirmMnemonicBinding.titlebar.showLeftDrawable();
        confirmMnemonicBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
        confirmMnemonicBinding.reset.setOnClickListener(v -> resetMnemonic(false));
        initRecyclerView();
    }

    @Override
    protected void initData() {
        String mnemonics = ACache.get(this).getAsString(Tag.MNEMONICS);
        if (!TextUtils.isEmpty(mnemonics)) {
            String[] mnemonicArray = mnemonics.split(" ");
            Collections.addAll(mnemonicData, mnemonicArray);
            Collections.addAll(mnemonicRandomData, mnemonicArray);
            resetMnemonic(true);
        }
    }

    /**
     * 重置助记词的顺序
     */
    private void resetMnemonic(boolean isShuffle) {
        if (selectIndex != 0) {
            selectIndex = 0;
            mnemonicSelectData.clear();
            mnemonicImgVisible.clear();
            for (int i = 0; i < mnemonicRandomData.size(); i++) {
                mnemonicImgVisible.add(i, false);
            }
        }
        setResetEnable(false);
        confirmMnemonicBinding.mnemonicErrorWarn.setVisibility(View.INVISIBLE);
        if (isShuffle) {
            Collections.shuffle(mnemonicRandomData);
        }
        quickAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化recyclerView
     */
    private void initRecyclerView() {
        quickAdapter = new QuickAdapter<String>(mnemonicRandomData) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_mnemonic;
            }

            @Override
            public void convert(VH holder, String data, int position) {
                holder.setText(R.id.singel_mnemonic, data);
                holder.getView(R.id.rl_item_mnemonic).setOnClickListener(v -> {
                    if (holder.getView(R.id.select_index).getVisibility() == View.GONE) {
                        mnemonicSelectData.add(selectIndex, data);
                        mnemonicImgVisible.add(selectIndex, true);
                        holder.getView(R.id.select_index).setVisibility(View.VISIBLE);
                        holder.setText(R.id.select_index, String.valueOf(selectIndex + 1));
                        selectIndex++;
                        if (!confirmMnemonicBinding.reset.isEnabled())
                            //当用户点击1个单词后，重置按钮变可点击状态
                            setResetEnable(true);
                    }
                    if (mnemonicSelectData.size() == 12) {
                        if (Utils.eqList(mnemonicSelectData, mnemonicData)) {
                            new KnowDialog(new CenterDialog(R.layout.dlg_messge_warn, ConfirmMnemonicAct.this), () -> navigateToHome(), getString(R.string.validate_success), getResources().getString(R.string.mnemonic_validate_success));
                        } else {
                            confirmMnemonicBinding.mnemonicErrorWarn.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }

        };
        LinearLayoutManager layoutManager = new GridLayoutManager(ConfirmMnemonicAct.this, 3);
        //设置布局管理器
        confirmMnemonicBinding.rvMnemonic.setLayoutManager(layoutManager);
        //设置Adapter
        confirmMnemonicBinding.rvMnemonic.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        confirmMnemonicBinding.rvMnemonic.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * 设置重置按钮可点击状态
     *
     * @param b 可点击状态
     */
    private void setResetEnable(boolean b) {
        confirmMnemonicBinding.reset.setEnabled(b);
    }

    /**
     * 跳转到首页
     */
    private void navigateToHome() {
        try {
            ACache.get(ConfirmMnemonicAct.this).put(Tag.IS_BACKUP, "true");

            HLWallet tempHlWallet = NestApp.tempHlWallet;
            HLWalletManager.shared().saveWallet(this, tempHlWallet);

            Intent intent = new Intent(ConfirmMnemonicAct.this, MainAct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            EventBus.getDefault().post(new WalletAddrEventBean());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        selectIndex = 0;
        if (mnemonicData.size() != 0) {
            mnemonicData.clear();
        }
        if (mnemonicRandomData.size() != 0) {
            mnemonicRandomData.clear();
        }
        if (mnemonicSelectData.size() != 0) {
            mnemonicSelectData.clear();
        }
        if (mnemonicImgVisible.size() != 0) {
            mnemonicImgVisible.clear();
        }
    }
}

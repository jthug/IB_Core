package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityCreateAndImportBinding;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.model.HLWallet;

import java.util.List;

/**
 * 导入和创建界面
 */
public class CreateAndImportActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCreateAndImportBinding binding;

    @Override
    protected void initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_and_import);
        initTitleBar();
        initClick();
    }

    private void initClick() {
        binding.btnImport.setOnClickListener(this);
        binding.btnCreate.setOnClickListener(this);
    }

    private void initTitleBar() {
        List<HLWallet> wallets = HLWalletManager.shared().getWallets();
        if (wallets==null||wallets.size()==0){
            binding.titlebar.setVisibility(View.GONE);
        }
        binding.titlebar.showLeftDrawable();
        binding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create:
                Intent creIntent = new Intent(this, CreateWalletAct.class);
                startActivity(creIntent);
                break;
            case R.id.btn_import:
                Intent impIntent = new Intent(this, ImportWalletAct.class);
                startActivity(impIntent);
                break;
        }
    }
}

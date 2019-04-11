package com.lianer.core.wallet;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.gs.utils.DisplayUtils;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.base.BaseFragment;
import com.lianer.core.R;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityImportWalletBinding;
import com.lianer.core.wallet.adapter.FragmentAdapter;

import java.util.Arrays;
import java.util.List;

/**
 * 导入钱包）（助记词、keystore）
 * @author allison
 */
public class ImportWalletAct extends BaseActivity {

    private ActivityImportWalletBinding importWalletBinding;

    @Override
    protected void initViews() {
        importWalletBinding = DataBindingUtil.setContentView(this, R.layout.activity_import_wallet);
        importWalletBinding.titlebar.showLeftDrawable();
        importWalletBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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

        List<BaseFragment> fragments = Arrays.asList(
                new UnlockPrivateKeyFragment(),
                new UnlockMnemonicFragment(),
                new UnlockKeystoreFragment()
        );
        List<String> labels = Arrays.asList(getString(R.string.private_key),getString(R.string.mnemonic),getString(R.string.keystore_file));
        importWalletBinding.viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(),fragments,labels));
        importWalletBinding.tabLayout.setupWithViewPager(importWalletBinding.viewPager);

        //tablayout添加分割线
        LinearLayout linearLayout = (LinearLayout) importWalletBinding.tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.divider_vertical));
        linearLayout.setDividerPadding(DisplayUtils.dp2px(this,10));

    }

    @Override
    protected void initData() {

    }
}

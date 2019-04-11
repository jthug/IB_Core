package com.lianer.core.wallet;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.lianer.core.base.BaseActivity;
import com.lianer.common.base.QuickAdapter;
import com.lianer.common.utils.ACache;
import com.lianer.core.R;
import com.lianer.core.config.Tag;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityMnemonicBinding;
import com.lianer.core.dialog.KnowDialog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 备份助记词
 * @author allison
 */
public class MnemonicAct extends BaseActivity {

    private ActivityMnemonicBinding mnemonicBinding;
    private List<String> mnemonicData = new ArrayList<>();
    private QuickAdapter quickAdapter;
//    private ScreenShotListenManager screenShotListenManager;
//    private boolean isHasScreenShotListener = false;

    @Override
    protected void initViews() {
        // 禁止截屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
//        screenShotListenManager = ScreenShotListenManager.newInstance(this);
        mnemonicBinding = DataBindingUtil.setContentView(this, R.layout.activity_mnemonic);
        mnemonicBinding.titlebar.showLeftDrawable();
        mnemonicBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
        mnemonicBinding.backupMnemonic.setOnClickListener(v -> {
            Intent intent = new Intent(MnemonicAct.this, ConfirmMnemonicAct.class);
            startActivity(intent);
        });
        initRecyclerView();
        initDialog();
    }

    /**
     * 初始化截图警告弹出框
     */
    private void initDialog() {
        new KnowDialog(new CenterDialog(R.layout.dlg_dont_screenshot, MnemonicAct.this),
                () -> {
                },
                getResources().getString(R.string.dont_screenshot),
                getResources().getString(R.string.dont_screenshot_warn));
    }

    @Override
    protected void initData() {
        String mnemonics = ACache.get(this).getAsString(Tag.MNEMONICS);
        if (!TextUtils.isEmpty(mnemonics)) {
            String[] mnemonicArray = mnemonics.split(" ");
            Collections.addAll(mnemonicData, mnemonicArray);
            quickAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView() {
        quickAdapter = new QuickAdapter<String>(mnemonicData) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_mnemonic;
            }

            @Override
            public void convert(VH holder, String data, int position) {
                holder.getView(R.id.select_index).setVisibility(View.VISIBLE);
                holder.setText(R.id.select_index, String.valueOf(position + 1));
                holder.setText(R.id.singel_mnemonic, data);
            }

        };
        LinearLayoutManager layoutManager = new GridLayoutManager(MnemonicAct.this , 3);
        //设置布局管理器
        mnemonicBinding.rvMnemonic.setLayoutManager(layoutManager);
        //设置Adapter
        mnemonicBinding.rvMnemonic.setAdapter(quickAdapter);
        //设置增加或删除条目的动画
        mnemonicBinding.rvMnemonic.setItemAnimator( new DefaultItemAnimator());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startScreenShotListen();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        stopScreenShotListen();
    }

//    /**
//     * 监听
//     */
//    private void startScreenShotListen() {
//        if (!isHasScreenShotListener && screenShotListenManager != null) {
//            screenShotListenManager.setListener(imagePath -> new KnowDialog(new CenterDialog(R.layout.dlg_dont_screenshot, MnemonicAct.this),
//                    () -> {
//                    },
//                    getResources().getString(R.string.dont_screenshot),
//                    getResources().getString(R.string.dont_screenshot_warn)));
//            screenShotListenManager.startListen();
//            isHasScreenShotListener = true;
//        }
//    }
//
//    /**
//     * 停止监听
//     */
//    private void stopScreenShotListen() {
//        if (isHasScreenShotListener && screenShotListenManager != null) {
//            screenShotListenManager.stopListen();
//            isHasScreenShotListener = false;
//        }
//    }

//    private void requestCemera() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            PermissionsUtil.requestPermission(getApplication(), new PermissionListener() {
//                @Override
//                public void permissionGranted(@NonNull String[] permissions) {
//                    startScreenShotListen();
//                }
//
//                @Override
//                public void permissionDenied(@NonNull String[] permissions) {
//                }
//            }, Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//    }

}

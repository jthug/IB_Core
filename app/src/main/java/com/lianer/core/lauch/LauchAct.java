package com.lianer.core.lauch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import com.lianer.common.utils.ACache;
import com.lianer.common.utils.Utils;
import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.config.Tag;
import com.lianer.core.config.TusdMortgageAssets;
import com.lianer.core.custom.CenterDialog;
import com.lianer.core.dialog.NotificationWarnDialog;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.wallet.CreateAndImportActivity;
import com.ypz.bangscreentools.BangScreenTools;

/**
 * 启动页
 *
 * @author allison
 */
public class LauchAct extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //used onCreat method
        BangScreenTools.getBangScreenTools().transparentStatusCutout(this.getWindow(), this);
        //used onWindowFocusChanged method
        BangScreenTools.getBangScreenTools().windowChangeTransparentStatusCutout(this.getWindow());

//        hideBottomUIMenu();

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_lauch);

        //获取抵押代币
        MortgageAssets.refreshMortgageToken(this,false);
        TusdMortgageAssets.refreshMortgageToken(this,false);
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断是否开启通知权限
        if (Utils.isNotificationsEnabled(this)) {
            navigateToMain();
        } else {
            new NotificationWarnDialog(new CenterDialog(R.layout.dlg_notification_power_warn, this), new NotificationWarnDialog.BtnListener() {
                @Override
                public void allow() {
                    // 根据isOpened结果，判断是否需要提醒用户跳转AppInfo页面，去打开App通知权限
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }

                @Override
                public void refuse() {
                    navigateToMain();
                }
            });
        }

    }

    /**
     * 判断是否已创建钱包，跳转对应页面
     */
    private void navigateToMain() {
        //延时3秒跳转
        new Handler().postDelayed(() -> {
            //判断钱包是否完成备份流程标识
            if (TextUtils.isEmpty(ACache.get(LauchAct.this).getAsString(Tag.IS_BACKUP))) {
                if (HLWalletManager.shared().getCurrentWallet(LauchAct.this) != null) {
                    HLWalletManager.shared().deleteWallet(LauchAct.this);
                }
            }

            Intent intent;
            if (HLWalletManager.shared().getCurrentWallet(LauchAct.this) == null) {
                //未创建钱包跳转
                intent = new Intent(LauchAct.this, CreateAndImportActivity.class);
            } else {
                //已创建/导入钱包跳转
                intent = new Intent(LauchAct.this, MainAct.class);
            }
            startActivity(intent);
            finish();
        }, 3000);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //隐藏虚拟按键，并且全屏
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
}

package com.lianer.core.utils.update;


import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lianer.common.utils.language.LanguageType;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.R;
import com.lianer.core.lauch.bean.VersionBean;

/**
 * 应用更新Dialog工具类
 */
public class UpdateDialogUtil implements View.OnClickListener {
    private Dialog mDialog;
    private Activity mActivity;
    private VersionBean mVersionBean;
    private TextView update_now, cancel;
    private View view;

    public UpdateDialogUtil(Dialog mDialog, Activity mActivity, VersionBean versionBean, UpdateListener listener) {
        this.mDialog = mDialog;
        this.mActivity = mActivity;
        this.mVersionBean = versionBean;
        __init();
    }

    private void __init() {
        TextView versionUpdate = mDialog.findViewById(R.id.version_update);
        TextView updateTitleWarn = mDialog.findViewById(R.id.update_title_warn);
        TextView updateContent = mDialog.findViewById(R.id.update_content);
        cancel = mDialog.findViewById(R.id.cancel);
        view = mDialog.findViewById(R.id.update_vertical_line);
        update_now = mDialog.findViewById(R.id.update_now);

        versionUpdate.setText(String.format("%s%s", mActivity.getString(R.string.version_update), mVersionBean.getVersionName()));
        updateTitleWarn.setText(String.format(mActivity.getString(R.string.version_update_content), mVersionBean.getVersionName()));
        if (MultiLanguageUtil.getInstance().getLanguageType() == LanguageType.LANGUAGE_CHINESE_SIMPLIFIED) {
            updateContent.setText(mVersionBean.getLogLanguages().getCn().replace("\\n","\n"));
        } else {
            updateContent.setText(mVersionBean.getLogLanguages().getEn().replace("\\n","\n"));
        }
        cancel.setOnClickListener(this);
        update_now.setOnClickListener(this);
        if (mVersionBean.getForceUpdate().equals("1")) {
            cancel.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        } else {
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
        }

        mDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                mDialog.dismiss();
                break;
            case R.id.update_now:
                if (mVersionBean.getForceUpdate().equals("0")) {
                    cancel.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                }
                downloadAndInstall(update_now);
                break;
        }

    }

    public void downloadAndInstall (TextView updateInfo) {
        new InstallUtils(mActivity, mVersionBean.getUrl(), "nest", new InstallUtils.DownloadCallBack() {
            @Override
            public void onStart() {
                Log.i("update_app", "InstallUtils---onStart");
                updateInfo.setEnabled(false);
                updateInfo.setText("0%");
            }

            @Override
            public void onComplete(String path) {
                Log.i("update_app", "InstallUtils---onComplete:" + path);
                InstallUtils.installAPK(mActivity, path, new InstallUtils.InstallCallBack() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(mActivity, "正在安装程序", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFail(Exception e) {
                        Toast.makeText(mActivity, "安装失败", Toast.LENGTH_SHORT).show();
                    }
                });
                updateInfo.setText("100%");
            }

            @Override
            public void onLoading(long total, long current) {
                Log.i("update_app", "InstallUtils----onLoading:-----total:" + total + ",current:" + current);
                updateInfo.setText((int) (current * 100 / total) + "%");
            }

            @Override
            public void onFail(Exception e) {
                Log.i("update_app", "InstallUtils---onFail:" + e.getMessage());
                updateInfo.setText(R.string.download_failure);
            }

        }).downloadAPK();
    }

    public interface UpdateListener {
    }

}

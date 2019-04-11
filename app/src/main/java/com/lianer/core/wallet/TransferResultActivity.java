package com.lianer.core.wallet;

import android.databinding.DataBindingUtil;
import android.view.View;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.R;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityTransferResultBinding;


public class TransferResultActivity extends BaseActivity implements View.OnClickListener {

    private ActivityTransferResultBinding mBinding;

    @Override
    protected void initViews() {
        mBinding =DataBindingUtil.setContentView(this, R.layout.activity_transfer_result);
        mBinding.titlebar.showLeftDrawable();
        mBinding.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
        mBinding.btnDetail.setOnClickListener(v -> finish());

    }


    @Override
    protected void initData() {


    }

    private static String getStackMsg(Exception e) {
        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

}

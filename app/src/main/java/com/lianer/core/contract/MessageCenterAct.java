package com.lianer.core.contract;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lianer.common.utils.ACache;
import com.lianer.core.R;
import com.lianer.core.base.BaseActivity;
import com.lianer.core.config.Tag;
import com.lianer.core.contract.bean.MessageCenterBean;
import com.lianer.core.contract.model.MessageCenterModel;
import com.lianer.core.custom.TitlebarView;
import com.lianer.core.databinding.ActivityMessageCenterBinding;
import com.lianer.core.lauch.MainAct;

import java.util.ArrayList;
import java.util.List;

public class MessageCenterAct extends BaseActivity{

    List<MessageCenterBean> dataList = new ArrayList<>();
    ActivityMessageCenterBinding messageCenter;
    MessageCenterModel messageCenterModel;
    private ImageView ivDelete;
    private LinearLayout llbottom;
    private Button btnCancel;
    private Button btnDelete;

    @Override
    protected void initViews() {
        messageCenter = DataBindingUtil.setContentView(this, R.layout.activity_message_center);
        messageCenterModel = new MessageCenterModel(this, messageCenter, dataList);
        initTitleBar();
        messageCenterModel.initRefresh();
        messageCenterModel.initRecyclerView();
        messageCenterModel.initView();
    }

    @Override
    protected void initData() {
        messageCenterModel.loadData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        messageCenter.titlebar.showLeftDrawable();
        messageCenter.titlebar.setOnViewClick(new TitlebarView.onViewClick() {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ACache.get(this).put(Tag.IS_RED_NOTIFICATION, "false");
        if (getIntent() != null) {
            if (getIntent().getIntExtra("backType", 0) == 1) {
                Intent intent = new Intent(this, MainAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            finish();
        }
    }
}

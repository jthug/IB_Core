package com.lianer.core.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.lianer.core.R;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.ContractActivity;
import com.lianer.core.contract.ContractOperateAct;
import com.lianer.core.contract.ContractTusdActivity;
import com.lianer.core.contract.ImportContractActivity;
import com.lianer.core.contract.TokenWheelAdapter;
import com.lianer.core.contract.bean.TokenMortgageBean;

import java.util.List;

import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;

public class PopupWindowUtil {

    /**
     * 菜单弹窗
     * @param activity
     * @return
     */
    public static PopupWindow menuPopupWindow(Activity activity) {
        //准备PopupWindow的布局View
        View popupView = LayoutInflater.from(activity).inflate(R.layout.menu_popup_window, null);
        //初始化一个PopupWindow，width和height都是WRAP_CONTENT
        PopupWindow popupWindow = new PopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT,  activity.getWindowManager().getDefaultDisplay().getHeight()-100);
        //设置PopupWindow的视图内容
        popupWindow.setContentView(popupView);
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        //设置PopupWindow动画
        popupWindow.setAnimationStyle(R.style.DownSlidingAnimation);
        //设置是否允许PopupWindow的范围超过屏幕范围
        popupWindow.setClippingEnabled(true);
        //设置PopupWindow消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(activity,1.0f);
            }
        });

        popupView.findViewById(R.id.external_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.eth_contract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,ContractActivity.class);
                intent.putExtra("ContractId",-1L);
                activity.startActivity(intent);
                popupWindow.dismiss();
            }
        });

        popupView.findViewById(R.id.tusd_contract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity,ContractTusdActivity.class);
                intent.putExtra("ContractId",-1L);
                activity.startActivity(intent);
                popupWindow.dismiss();
            }
        });

        popupView.findViewById(R.id.import_contract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity,ImportContractActivity.class));
                popupWindow.dismiss();
            }
        });

        if(DBUtil.queryContracts().size() > 0) {
            popupView.findViewById(R.id.output_contract).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent exportIntent = new Intent(activity, ContractOperateAct.class);
                    exportIntent.putExtra("contractOperateType", "export");
                    activity.startActivity(exportIntent);
                    popupWindow.dismiss();
                }
            });
            popupView.findViewById(R.id.delete_contract).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent deleteIntent = new Intent(activity,ContractOperateAct.class);
                    deleteIntent.putExtra("contractOperateType", "delete");
                    activity.startActivity(deleteIntent);
                    popupWindow.dismiss();
                }
            });
        }else{
            popupView.findViewById(R.id.output_contract).setAlpha(0.4f);
            popupView.findViewById(R.id.delete_contract).setAlpha(0.4f);
        }


        setBackgroundAlpha(activity,0.5f);

        return popupWindow;
    }

    /**
     * 分享弹窗
     * @param activity
     * @return
     */
    public static PopupWindow itemPopupWindow(Activity activity) {
        //准备PopupWindow的布局View
        View popupView = LayoutInflater.from(activity).inflate(R.layout.item_popup_window, null);
        //初始化一个PopupWindow，width和height都是WRAP_CONTENT
        PopupWindow popupWindow = new PopupWindow(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置PopupWindow的视图内容
        popupWindow.setContentView(popupView);
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        //设置PopupWindow动画
        popupWindow.setAnimationStyle(R.style.DownSlidingAnimation);
        //设置是否允许PopupWindow的范围超过屏幕范围
        popupWindow.setClippingEnabled(true);
        //设置PopupWindow消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(activity,1.0f);
            }
        });

        popupView.findViewById(R.id.external_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        setBackgroundAlpha(activity,0.5f);

        return popupWindow;
    }
    /**
     * 转账弹窗
     * @param activity
     * @return
     */
    public static PopupWindow transferPopupWindow(Activity activity) {
        //准备PopupWindow的布局View
        View popupView = LayoutInflater.from(activity).inflate(R.layout.transfer_popup_window, null);
        //初始化一个PopupWindow，width和height都是WRAP_CONTENT
        PopupWindow popupWindow = new PopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow的视图内容
        popupWindow.setContentView(popupView);
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        //设置PopupWindow动画
        popupWindow.setAnimationStyle(R.style.Animation_CustomPopup);
        //设置是否允许PopupWindow的范围超过屏幕范围
        popupWindow.setClippingEnabled(true);
        //设置PopupWindow消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(activity,1.0f);
            }
        });
        popupView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.external_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        setBackgroundAlpha(activity,0.5f);

        return popupWindow;
    }


    /**
     * 交易弹窗
     * @param activity
     * @return
     */
    public static PopupWindow contracTransactionPopupWindow(Activity activity) {
        //准备PopupWindow的布局View
        View popupView = LayoutInflater.from(activity).inflate(R.layout.contract_transaction_popup_window, null);
        //初始化一个PopupWindow，width和height都是WRAP_CONTENT
        PopupWindow popupWindow = new PopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow的视图内容
        popupWindow.setContentView(popupView);
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        //设置PopupWindow动画
        popupWindow.setAnimationStyle(R.style.Animation_CustomPopup);
        //设置是否允许PopupWindow的范围超过屏幕范围
        popupWindow.setClippingEnabled(true);
        //设置PopupWindow消失监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(activity,1.0f);
            }
        });
        popupView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupView.findViewById(R.id.external_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        setBackgroundAlpha(activity,0.5f);

        return popupWindow;
    }

    /**
     * 抵押资产弹窗
     * @param activity
     * @return
     */
    public static PopupWindow mortgageTokenPopupWindow(Activity activity) {

        //准备PopupWindow的布局View
        View popupView = LayoutInflater.from(activity).inflate(R.layout.mortgage_token_popup_window, null);
        //初始化一个PopupWindow，width和height都是WRAP_CONTENT
        PopupWindow mortgageTokenPopupWindow = new PopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow的视图内容
        mortgageTokenPopupWindow.setContentView(popupView);
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        mortgageTokenPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mortgageTokenPopupWindow.setOutsideTouchable(true);
        //设置PopupWindow动画
        mortgageTokenPopupWindow.setAnimationStyle(R.style.Animation_CustomPopup);
        //设置是否允许PopupWindow的范围超过屏幕范围
        mortgageTokenPopupWindow.setClippingEnabled(true);
        //设置PopupWindow消失监听
        mortgageTokenPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                // popupWindow隐藏时恢复屏幕正常透明度
//                setBackgroundAlpha(activity,1.0f);
            }
        });
        popupView.findViewById(R.id.external_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mortgageTokenPopupWindow.dismiss();
            }
        });

//        setBackgroundAlpha(activity,0.5f);

        com.wx.wheelview.widget.WheelView wheelView =  popupView.findViewById(R.id.wheelview);

        TokenWheelAdapter adapter = new TokenWheelAdapter(activity);
        wheelView.setWheelAdapter(adapter);
        wheelView.setWheelSize(5);
        wheelView.setWheelData(MortgageAssets.getTokenMortgageList(activity));
        wheelView.setSkin(com.wx.wheelview.widget.WheelView.Skin.None);
        wheelView.setLoop(false);
        wheelView.setWheelClickable(true);
        com.wx.wheelview.widget.WheelView.WheelViewStyle style = new com.wx.wheelview.widget.WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#CCCCCC");
        style.textColor =  Color.parseColor("#666666");
        style.textAlpha = 1f;
        wheelView.setStyle(style);
//        wheelView.setOnWheelItemClickListener(new com.wx.wheelview.widget.WheelView.OnWheelItemClickListener() {
//            @Override
//            public void onItemClick(int position, Object o) {
//                KLog.w(o.toString());
//            }
//        });
//        wheelView.setOnWheelItemSelectedListener(new com.wx.wheelview.widget.WheelView.OnWheelItemSelectedListener<TokenBorrowingBean>() {
//            @Override
//            public void onItemSelected(int position, TokenBorrowingBean data) {
//                KLog.w(data.toString());
//
//            }
//        });

        return mortgageTokenPopupWindow;
    }



    /**
     * 部署
     * @param activity
     * @return
     */
    public static PopupWindow contractParamPopupWindow(Activity activity) {

        //准备PopupWindow的布局View
        View popupView = LayoutInflater.from(activity).inflate(R.layout.contract_param_popup_window, null);
        //初始化一个PopupWindow，width和height都是WRAP_CONTENT
        PopupWindow contractParamPopupWindow = new PopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow的视图内容
        contractParamPopupWindow.setContentView(popupView);
        //点击空白区域PopupWindow消失，这里必须先设置setBackgroundDrawable，否则点击无反应
        contractParamPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        contractParamPopupWindow.setOutsideTouchable(true);
        //设置PopupWindow动画
        contractParamPopupWindow.setAnimationStyle(R.style.Animation_CustomPopup);
        //设置是否允许PopupWindow的范围超过屏幕范围
        contractParamPopupWindow.setClippingEnabled(true);
        //设置PopupWindow消失监听

        setBackgroundAlpha(activity,0.5f);


        //借币数量选择
        WheelView amountWheelView =  popupView.findViewById(R.id.wheelview_amount);
        ViewGroup.LayoutParams params1 = amountWheelView.getLayoutParams();
        params1.width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
        amountWheelView.setLayoutParams(params1);

        amountWheelView.setTextColor(0xFF666666,0xFFCCCCCC);
        amountWheelView.setTextSize(16);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setRatio(1.0f );//线比率
        config.setColor(0xFF666666);//线颜色
        config.setAlpha(100);//线透明度
        config.setThick(ConvertUtils.toPx(activity, 1f));//线粗
        amountWheelView.setDividerConfig(config);
        amountWheelView.setCycleDisable(true);


        //借币周期选择
        WheelView cycleWheelView =  popupView.findViewById(R.id.wheelview_cycle);
        ViewGroup.LayoutParams param2 = amountWheelView.getLayoutParams();
        param2.width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
        cycleWheelView.setLayoutParams(param2);
        cycleWheelView.setTextColor(0xFF666666,0xFFCCCCCC);

        cycleWheelView.setTextSize(16);
        cycleWheelView.setDividerConfig(config);
        cycleWheelView.setCycleDisable(true);

        //借币利率选择
        WheelView rateWheelView =  popupView.findViewById(R.id.wheelview_rate);
        ViewGroup.LayoutParams params3 = amountWheelView.getLayoutParams();
        params3.width = activity.getWindowManager().getDefaultDisplay().getWidth() / 3;
        rateWheelView.setLayoutParams(params3);
        rateWheelView.setTextColor(0xFF666666,0xFFCCCCCC);

        rateWheelView.setTextSize(16);
        rateWheelView.setDividerConfig(config);
        rateWheelView.setCycleDisable(true);
        //抵押资产选择
        com.wx.wheelview.widget.WheelView wheelView =  popupView.findViewById(R.id.wheelview);
        TokenWheelAdapter adapter = new TokenWheelAdapter(activity);
        wheelView.setWheelAdapter(adapter);
        wheelView.setWheelSize(5);
        wheelView.setWheelData(MortgageAssets.getTokenMortgageList(activity));
        List<TokenMortgageBean> tokenMortgageList = MortgageAssets.getTokenMortgageList(activity);
        Log.e("xxxxxx",tokenMortgageList.get(0).getTokenAbbreviation());
        wheelView.setSkin(com.wx.wheelview.widget.WheelView.Skin.None);
        wheelView.setLoop(false);
        wheelView.setWheelClickable(true);
        com.wx.wheelview.widget.WheelView.WheelViewStyle style = new com.wx.wheelview.widget.WheelView.WheelViewStyle();
        style.selectedTextColor = Color.parseColor("#CCCCCC");
        style.textColor =  Color.parseColor("#666666");
        style.textAlpha = 1f;
        style.backgroundColor = Color.parseColor("#3C3C3C");
        wheelView.setStyle(style);


        contractParamPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(activity,1.0f);
            }
        });

        popupView.findViewById(R.id.external_area).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractParamPopupWindow.dismiss();
            }
        });


        return contractParamPopupWindow;
    }


    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    public static void setBackgroundAlpha(Activity activity ,float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        activity.getWindow().setAttributes(lp);
    }
}

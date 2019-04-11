package com.lianer.core.contract.model;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.lianer.common.utils.DateUtils;
import com.lianer.core.R;
import com.lianer.core.contract.bean.ContractResponse;

public class TimeScheduleModel {

    public static void setInvestScheduleTo8 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub timeViewStub = rootView.findViewById(R.id.three_time);
        View timeView = timeViewStub.inflate();
        //初始化需要使用的控件
        TextView firstSchedule = timeView.findViewById(R.id.schedule_first);
        TextView secondSchedule = timeView.findViewById(R.id.schedule_second);
        TextView thirdSchedule = timeView.findViewById(R.id.schedule_third);

        TextView firstScheduleTime = timeView.findViewById(R.id.schedule_time_first);
        TextView secondScheduleTime = timeView.findViewById(R.id.schedule_time_second);
        TextView thirdScheduleTime = timeView.findViewById(R.id.schedule_time_third);

        //改变颜色
        firstSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        firstScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        //设置文本
        firstSchedule.setText(R.string.create_time);
        secondSchedule.setText(R.string.effect_time);
        thirdSchedule.setText(R.string.expire_time);
        firstScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        secondScheduleTime.setText(R.string.invested_effect);

        //设置小圆圈和线的颜色
        View firstCircle = timeView.findViewById(R.id.first_circle);
        View firstLine = timeView.findViewById(R.id.first_line);
        firstCircle.setSelected(true);
        firstLine.setSelected(true);

    }

    /**
     * 已生效时间进度显示
     */
    public static void setInvestScheduleTo16 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub vsTakeEffect = rootView.findViewById(R.id.four_time);
        View takeEffectView = vsTakeEffect.inflate();

        //初始化需要使用的控件
        TextView firstTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_first);
        TextView secondTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_second);
        TextView thirdTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_third);
        TextView fourthTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_fourth);

        TextView firstTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_first);
        TextView secondTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_second);
        TextView thirdTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_third);
        TextView fourTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_fourth);

        firstTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        firstTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        firstTakeEffectSchedule.setText(R.string.create_time);
        secondTakeEffectSchedule.setText(R.string.effect_time);
        thirdTakeEffectSchedule.setText(R.string.expire_time);
        fourthTakeEffectSchedule.setText(R.string.repayment_time);

        firstTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractExpireDate())) {
            thirdTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractExpireDate()));
        }
        fourTakeEffectScheduleTime.setText(R.string.borrow_no_repayment);

        //设置小圆圈和线的颜色
        View firstCircle = takeEffectView.findViewById(R.id.first_circle);
        View secondCircle = takeEffectView.findViewById(R.id.second_circle);
        View firstLine = takeEffectView.findViewById(R.id.first_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        firstLine.setSelected(true);
    }

    public static void setInvestScheduleTo21 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub timeViewStub = rootView.findViewById(R.id.three_time);
        View timeView = timeViewStub.inflate();
        //初始化需要使用的控件
        TextView firstSchedule = timeView.findViewById(R.id.schedule_first);
        TextView secondSchedule = timeView.findViewById(R.id.schedule_second);
        TextView thirdSchedule = timeView.findViewById(R.id.schedule_third);

        TextView firstScheduleTime = timeView.findViewById(R.id.schedule_time_first);
        TextView secondScheduleTime = timeView.findViewById(R.id.schedule_time_second);
        TextView thirdScheduleTime = timeView.findViewById(R.id.schedule_time_third);

        //改变颜色
        firstSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        firstScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        //设置文本
        firstSchedule.setText(R.string.create_time);
        secondSchedule.setText(R.string.effect_time);
        thirdSchedule.setText(R.string.repayment_time);
        firstScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractRepaymentDate())) {
            thirdScheduleTime.setText(DateUtils.timedate(contractResponse.getContractRepaymentDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = timeView.findViewById(R.id.first_circle);
        View secondCircle = timeView.findViewById(R.id.second_circle);
        View thirdCircle = timeView.findViewById(R.id.third_circle);
        View firstLine = timeView.findViewById(R.id.first_line);
        View secondLine = timeView.findViewById(R.id.second_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        firstLine.setSelected(true);
        secondLine.setSelected(true);
    }

    public static void setInvestScheduleTo22 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub vsTakeEffect = rootView.findViewById(R.id.four_time);
        View takeEffectView = vsTakeEffect.inflate();

        //初始化需要使用的控件
        TextView firstTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_first);
        TextView secondTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_second);
        TextView thirdTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_third);
        TextView fourthTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_fourth);

        TextView firstTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_first);
        TextView secondTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_second);
        TextView thirdTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_third);
        TextView fourTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_fourth);

        firstTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourthTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_F5222D));

        firstTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_F5222D));

        firstTakeEffectSchedule.setText(R.string.create_time);
        secondTakeEffectSchedule.setText(R.string.effect_time);
        thirdTakeEffectSchedule.setText(R.string.expire_time);
        fourthTakeEffectSchedule.setText(context.getString(R.string.overdue)+ "\t");

        firstTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractExpireDate())) {
            thirdTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractExpireDate()));
            fourTakeEffectScheduleTime.setText(DateUtils.timeDiff(String.valueOf(System.currentTimeMillis()/1000), contractResponse.getContractExpireDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = takeEffectView.findViewById(R.id.first_circle);
        View secondCircle = takeEffectView.findViewById(R.id.second_circle);
        View thirdCircle = takeEffectView.findViewById(R.id.third_circle);
        View fourthCircle = takeEffectView.findViewById(R.id.fouth_circle);
        View firstLine = takeEffectView.findViewById(R.id.first_line);
        View secondLine = takeEffectView.findViewById(R.id.second_line);
        View thirdLine = takeEffectView.findViewById(R.id.third_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        fourthCircle.setBackgroundResource(R.drawable.circle_hollow_red);
        firstLine.setSelected(true);
        secondLine.setSelected(true);
        thirdLine.setSelected(true);
    }

    public static void setInvestScheduleTo26 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub vsTakeEffect = rootView.findViewById(R.id.four_time);
        View takeEffectView = vsTakeEffect.inflate();

        //初始化需要使用的控件
        TextView firstTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_first);
        TextView secondTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_second);
        TextView thirdTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_third);
        TextView fourthTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_fourth);

        TextView firstTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_first);
        TextView secondTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_second);
        TextView thirdTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_third);
        TextView fourTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_fourth);

        firstTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourthTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        firstTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        firstTakeEffectSchedule.setText(R.string.create_time);
        secondTakeEffectSchedule.setText(R.string.effect_time);
        thirdTakeEffectSchedule.setText(R.string.expire_time);
        fourthTakeEffectSchedule.setText(R.string.take_back);

        firstTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractExpireDate())) {
            thirdTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractExpireDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractTakebackDate())) {
            fourTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractTakebackDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = takeEffectView.findViewById(R.id.first_circle);
        View secondCircle = takeEffectView.findViewById(R.id.second_circle);
        View thirdCircle = takeEffectView.findViewById(R.id.third_circle);
        View fourthCircle = takeEffectView.findViewById(R.id.fouth_circle);
        View firstLine = takeEffectView.findViewById(R.id.first_line);
        View secondLine = takeEffectView.findViewById(R.id.second_line);
        View thirdLine = takeEffectView.findViewById(R.id.third_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        fourthCircle.setSelected(true);
        firstLine.setSelected(true);
        secondLine.setSelected(true);
        thirdLine.setSelected(true);
    }

    public static void setBorrowScheduleTo16 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub timeViewStub = rootView.findViewById(R.id.three_time);
        View timeView = timeViewStub.inflate();
        //初始化需要使用的控件
        TextView firstSchedule = timeView.findViewById(R.id.schedule_first);
        TextView secondSchedule = timeView.findViewById(R.id.schedule_second);
        TextView thirdSchedule = timeView.findViewById(R.id.schedule_third);

        TextView firstScheduleTime = timeView.findViewById(R.id.schedule_time_first);
        TextView secondScheduleTime = timeView.findViewById(R.id.schedule_time_second);
        TextView thirdScheduleTime = timeView.findViewById(R.id.schedule_time_third);

        //改变颜色
        firstSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        firstScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        //设置文本
        firstSchedule.setText(R.string.create_time);
        secondSchedule.setText(R.string.effect_time);
        thirdSchedule.setText(R.string.expire_time);
        firstScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractExpireDate())) {
            thirdScheduleTime.setText(DateUtils.timedate(contractResponse.getContractExpireDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = timeView.findViewById(R.id.first_circle);
        View secondCircle = timeView.findViewById(R.id.second_circle);
        View thirdCircle = timeView.findViewById(R.id.third_circle);
        View firstLine = timeView.findViewById(R.id.first_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        firstLine.setSelected(true);

    }

    public static void setBorrowScheduleTo22 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub vsTakeEffect = rootView.findViewById(R.id.four_time);
        View takeEffectView = vsTakeEffect.inflate();

        //初始化需要使用的控件
        TextView firstTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_first);
        TextView secondTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_second);
        TextView thirdTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_third);
        TextView fourthTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_fourth);

        TextView firstTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_first);
        TextView secondTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_second);
        TextView thirdTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_third);
        TextView fourTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_fourth);

        firstTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourthTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_F5222D));

        firstTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_F5222D));

        firstTakeEffectSchedule.setText(R.string.create_time);
        secondTakeEffectSchedule.setText(R.string.effect_time);
        thirdTakeEffectSchedule.setText(R.string.expire_time);
        fourthTakeEffectSchedule.setText(context.getString(R.string.overdue)+ "\t");

        firstTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractExpireDate())) {
            thirdTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractExpireDate()));
            fourTakeEffectScheduleTime.setText(DateUtils.timeDiff(String.valueOf(System.currentTimeMillis()/1000), contractResponse.getContractExpireDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = takeEffectView.findViewById(R.id.first_circle);
        View secondCircle = takeEffectView.findViewById(R.id.second_circle);
        View thirdCircle = takeEffectView.findViewById(R.id.third_circle);
        View fourthCircle = takeEffectView.findViewById(R.id.fouth_circle);
        View firstLine = takeEffectView.findViewById(R.id.first_line);
        View secondLine = takeEffectView.findViewById(R.id.second_line);
        View thirdLine = takeEffectView.findViewById(R.id.third_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        fourthCircle.setSelected(true);
        firstLine.setSelected(true);
        secondLine.setSelected(true);
        thirdLine.setSelected(true);
    }

    public static void setBorrowScheduleTo21 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub timeViewStub = rootView.findViewById(R.id.three_time);
        View timeView = timeViewStub.inflate();
        //初始化需要使用的控件
        TextView firstSchedule = timeView.findViewById(R.id.schedule_first);
        TextView secondSchedule = timeView.findViewById(R.id.schedule_second);
        TextView thirdSchedule = timeView.findViewById(R.id.schedule_third);

        TextView firstScheduleTime = timeView.findViewById(R.id.schedule_time_first);
        TextView secondScheduleTime = timeView.findViewById(R.id.schedule_time_second);
        TextView thirdScheduleTime = timeView.findViewById(R.id.schedule_time_third);

        //改变颜色
        firstSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        firstScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        //设置文本
        firstSchedule.setText(R.string.create_time);
        secondSchedule.setText(R.string.effect_time);
        thirdSchedule.setText(R.string.repaid);
        firstScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractRepaymentDate())) {
            thirdScheduleTime.setText(DateUtils.timedate(contractResponse.getContractRepaymentDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = timeView.findViewById(R.id.first_circle);
        View secondCircle = timeView.findViewById(R.id.second_circle);
        View thirdCircle = timeView.findViewById(R.id.third_circle);
        View firstLine = timeView.findViewById(R.id.first_line);
        View secondLine = timeView.findViewById(R.id.second_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        firstLine.setSelected(true);
        secondLine.setSelected(true);

    }

    public static void setBorrowScheduleTo26 (Context context, View rootView, ContractResponse contractResponse) {
        ViewStub vsTakeEffect = rootView.findViewById(R.id.four_time);
        View takeEffectView = vsTakeEffect.inflate();

        //初始化需要使用的控件
        TextView firstTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_first);
        TextView secondTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_second);
        TextView thirdTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_third);
        TextView fourthTakeEffectSchedule = takeEffectView.findViewById(R.id.schedule_fourth);

        TextView firstTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_first);
        TextView secondTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_second);
        TextView thirdTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_third);
        TextView fourTakeEffectScheduleTime = takeEffectView.findViewById(R.id.schedule_time_fourth);

        firstTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourthTakeEffectSchedule.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        firstTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        secondTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        thirdTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));
        fourTakeEffectScheduleTime.setTextColor(context.getResources().getColor(R.color.clr_8c8c8c));

        firstTakeEffectSchedule.setText(R.string.create_time);
        secondTakeEffectSchedule.setText(R.string.effect_time);
        thirdTakeEffectSchedule.setText(R.string.expire_time);
        fourthTakeEffectSchedule.setText(R.string.take_back);

        firstTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractCreateDate()));
        if (!TextUtils.isEmpty(contractResponse.getContractEffectDate())) {
            secondTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractEffectDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractExpireDate())) {
            thirdTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractExpireDate()));
        }
        if (!TextUtils.isEmpty(contractResponse.getContractTakebackDate())) {
            fourTakeEffectScheduleTime.setText(DateUtils.timedate(contractResponse.getContractTakebackDate()));
        }

        //设置小圆圈和线的颜色
        View firstCircle = takeEffectView.findViewById(R.id.first_circle);
        View secondCircle = takeEffectView.findViewById(R.id.second_circle);
        View thirdCircle = takeEffectView.findViewById(R.id.third_circle);
        View fourthCircle = takeEffectView.findViewById(R.id.fouth_circle);
        View firstLine = takeEffectView.findViewById(R.id.first_line);
        View secondLine = takeEffectView.findViewById(R.id.second_line);
        View thirdLine = takeEffectView.findViewById(R.id.third_line);
        firstCircle.setSelected(true);
        secondCircle.setSelected(true);
        thirdCircle.setSelected(true);
        fourthCircle.setSelected(true);
        firstLine.setSelected(true);
        secondLine.setSelected(true);
        thirdLine.setSelected(true);
    }

}

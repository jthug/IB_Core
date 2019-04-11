package com.lianer.core.config;

import com.lianer.core.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContractStatus {

    //合约状态值
    public static final String CONTRACT_STSTUS_0 = "0";//合约部署中
    public static final String CONTRACT_STSTUS_1 = "1";//合约部署失败
    public static final String CONTRACT_STSTUS_2 = "2";//合约部署成功
    public static final String CONTRACT_STSTUS_3 = "3";//待转入抵押资产
    public static final String CONTRACT_STSTUS_4 = "4";//已终止                静态
    public static final String CONTRACT_STSTUS_5 = "5";//资产转入中 抵押资产
    public static final String CONTRACT_STSTUS_6 = "6";//资产转入失败
    public static final String CONTRACT_STSTUS_7 = "7";//资产转入成功
    public static final String CONTRACT_STSTUS_8 = "8";//已发布                静态
    public static final String CONTRACT_STSTUS_9 = "9";//合约解散中
    public static final String CONTRACT_STSTUS_10 = "10";//合约解散失败
    public static final String CONTRACT_STSTUS_11 = "11";//合约解散成功
    public static final String CONTRACT_STSTUS_12 = "12";//已解散              静态
    public static final String CONTRACT_STSTUS_13 = "13";//资产转入中 eth
    public static final String CONTRACT_STSTUS_14 = "14";//资产转入失败
    public static final String CONTRACT_STSTUS_15 = "15";//资产转入成功
    public static final String CONTRACT_STSTUS_16 = "16";//已生效              静态
    public static final String CONTRACT_STSTUS_17 = "17";//合约快到期           静态
    public static final String CONTRACT_STSTUS_18 = "18";//还款中
    public static final String CONTRACT_STSTUS_19 = "19";//还款失败
    public static final String CONTRACT_STSTUS_20 = "20";//还款成功
    public static final String CONTRACT_STSTUS_21 = "21";//已还款              静态
    public static final String CONTRACT_STSTUS_22 = "22";//已逾期              静态
    public static final String CONTRACT_STSTUS_23 = "23";//资产取回中
    public static final String CONTRACT_STSTUS_24 = "24";//资产取回失败
    public static final String CONTRACT_STSTUS_25 = "25";//资产取回成功
    public static final String CONTRACT_STSTUS_26 = "26";//已取回              静态

    public static final List<String> CONTRACT_HOME = Arrays.asList("7", "8", "9", "10", "13", "14");//首页合约 (可投资)
    //    public static final List<String> ALL_CONTRACT = Arrays.asList("7", "8", "9", "10", "13",
//            "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26");//全部合约
    public static final List<String> ALL_CONTRACT = Arrays.asList("7", "8", "9", "10", "13", "14");
    public static final List<String> DYNAMIC_CONTRACT = Arrays.asList("0", "1", "2", "3", "5", "6",
            "7", "9", "10", "11", "13", "14", "15", "18", "19", "20", "23", "24", "25");//动态合约
    public static final List<String> CONTRACT_INVEST = Arrays.asList(                  "16","17","18","19","21","22","26");//我的合约-投资
    public static final List<String> CONTRACT_BORROW = Arrays.asList("4", "8",  "12", "13","14","15","16","17","21","22","23","24","26");//我的合约-借币

    public static final List<String> CONTRACT_STATUS_FILTER = Arrays.asList("8", "21", "16", "22", "26");//全部合约筛选条件

    public static final String RELEASED = "released";//已发布
    public static final String EFFECTED = "effected";//已生效
    public static final String REPAYMENT = "repayment";//已还款
    public static final String OVERDUE = "overdue";//已逾期
    public static final String RECAPTURED = " recaptured";//已取回
    public static final String TERMINATED = "terminated";//已终止
    public static final String EXPIRED = "expired";//合约快到期
    public static final String DISBANDED = "disbanded";//已解散

    public static final List<String> DEPLOY_LIST = Arrays.asList("2", "3", "5", "6");
    public static final List<String> RELEASED_LIST = Arrays.asList("7", "8", "9", "10", "13", "14");
    public static final List<String> EFFECTED_LIST = Arrays.asList("15", "16", "17", "18", "19");
    public static final List<String> REPAYMENT_LIST = Arrays.asList("20", "21");
    public static final List<String> OVERDUE_LIST = Arrays.asList("22", "23", "24");
    public static final List<String> RECAPTURED_LIST = Arrays.asList("25", "26");
    public static final List<String> TERMINATED_LIST = Collections.singletonList("4");
    public static final List<String> DISBANDED_LIST = Arrays.asList("11", "12");

    public static final String CONTRACT_STATUS_WAIT_DEPLOY = "-1";//待部署
    public static final String CONTRACT_STATUS_WAIT_MORTGAGE = "0";//待转入抵押
    public static final String CONTRACT_STATUS_WAIT_INVEST= "1";//待投资
    public static final String CONTRACT_STATUS_WAIT_REPAYMENT = "2";//待还款
    public static final String CONTRACT_STATUS_REPAID = "3";//已还款
    public static final String CONTRACT_STATUS_OVERDUE = "4";//已逾期
    public static final String CONTRACT_STATUS_END = "5";//已结束

    public static final int MESSAGE_TURN_OUT = 0;//转出
    public static final int MESSAGE_STSTUS_ONE = 1;//部署合约
    public static final int MESSAGE_STSTUS_TWO = 2;//转入抵押
    public static final int MESSAGE_STSTUS_THREE = 3;//取回抵押
    public static final int MESSAGE_STSTUS_FOUR = 4;//投资合约
    public static final int MESSAGE_STSTUS_FIVE = 5;//还款
    public static final int MESSAGE_STSTUS_SIX = 6;//获取抵押

    public static final int MESSAGE_STSTUS_APPROVE = 7;//授权
    public static final int MESSAGE_STSTUS_APPROVE_CANCEL = 8;//授权取消
    public static final int MESSAGE_STSTUS_DEPOSIT_IN= 9;//存入
    public static final int MESSAGE_STSTUS_DEPOSIT_OUT= 10;//取出
    public static final int MESSAGE_STSTUS_GET_EARN= 11;//领取收益


    public static final int TRANSFER_TX = 0;
    public static final int CONTRACT_TX = 1;

    //设置交易状态值
    public static final int[] MESSAGE_STATUS = {R.string.turn_out, R.string.message_status_1, R.string.message_status_2, R.string.message_status_3, R.string.message_status_4,
            R.string.message_status_5, R.string.message_status_6, R.string.message_status_7, R.string.message_status_8,R.string.message_status_9,R.string.message_status_10,R.string.receive_income};
}

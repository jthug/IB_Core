package com.lianer.core.utils;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;

import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.app.NestApp;
import com.lianer.core.config.ContractStatus;
import com.lianer.core.etherscan.EtherScanWebActivity;
import com.lianer.core.manager.HLWalletManager;
import com.lianer.core.wallet.TxRecordDetailAct;
import com.lianer.core.wallet.bean.TokenProfileBean;

import java.math.BigDecimal;
import java.util.ArrayList;

public class CommomUtil {

    public static String getTokenValue(Context context,String tokenAddress, String value) {
        for (TokenProfileBean tokenProfileBean : Constants.tokenProfileBeans) {
            if (tokenProfileBean.getAddress().equalsIgnoreCase(tokenAddress)) {
                int decimals = Integer.valueOf(tokenProfileBean.getDecimals());
                return new BigDecimal(value).divide(new BigDecimal("10").pow(decimals)).toString();
            }
        }
        int tokenDecimals = IBContractUtil.getTokenDecimals(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(context).getAddress(), tokenAddress);
        return new BigDecimal(value).divide(new BigDecimal("10").pow(tokenDecimals)).toString();
    }

    public static  void copy(Context context,View view,String text){
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(text);
        SnackbarUtil.DefaultSnackbar(view, context.getString(R.string.copy_success)).show();
    }

    /**
     * 在app处于后台的情况下推送通知
     *
     * @param context 上下文
     * @param txHash  交易hash
     * @param txType  交易类型
     */
    public static void pushNotification(Context context, String txHash, String txType) {
        //判断app是否处于后台
        if (NestApp.isAppBackground()) {
            //推送通知
            NotificationUtils notification = new NotificationUtils(context);
            notification.sendNotification(context.getString(R.string.trading_reminder),
                    context.getString(R.string.trading_reminder_content), txHash, txType, TxRecordDetailAct.class);
        }
    }

    /**
     * 截取地址前8位及后8位拼接
     * 0x65C18AE8Cc5DecfD092DEF023e951087d1cF4Dbf
     * 0x65C18A...d1cF4Dbf
     *
     * @return
     */
    public static String splitWalletAddress(String address) {
        String str = address.substring(0, 8);
        String str2 = address.substring(address.length() - 8, address.length());
        return str + "..." + str2;
    }

    //app版本
    public static String getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1";
    }


    //app code版本
    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取整数位
     *
     * @param str
     */
    public static String decimalToZeroPoint(String str) {
//        DecimalFormat intFormat = new DecimalFormat("0");
//        str = intFormat.format(new BigDecimal(str));
        long value = new BigDecimal(str).setScale(0, BigDecimal.ROUND_UP).longValue();
        return value+"";
    }

    /**
     * 截取数据到小数点后四位
     *
     * @param str
     */
    public static String decimalTo4Point(String str) {
        if (str.contains(".")) {
            if (str.substring(str.indexOf("."), str.length()).length() > 4) {
                return str.substring(0, str.indexOf(".") + 5);
            }
        }
        return str;
    }

    /**
     * 截取数据到小数点后2位
     *
     * @param str
     */
    public static String doubleFormat(String str) {
        if (str.contains(".")) {
            if (str.substring(str.indexOf("."), str.length()).length() > 2) {
                return str.substring(0, str.indexOf(".") + 3);
            }
        }
        return str;
    }

    /**
     * 跳转到EtherScan
     *
     * @param context           上下文
     * @param isContractAddress 是否是合约地址
     * @param selectValue       查询关键字
     */
    public static void navigateToEthScan(Context context, boolean isContractAddress, String selectValue) {
        Intent intent = new Intent(context, EtherScanWebActivity.class);
        if (isContractAddress) {
            intent.putExtra("ContractAddress", selectValue);
        } else {
            intent.putExtra("TxHash", selectValue);
        }
        context.startActivity(intent);
    }

    /**
     * 判断合约业务状态属于哪个静态状态的范围
     *
     * @param contractStatus 合约业务状态
     * @return 静态状态
     */
    public static String judgeContractStatus(String contractStatus) {
        if (ContractStatus.RELEASED_LIST.contains(contractStatus)) {
            return ContractStatus.RELEASED;
        }
        if (ContractStatus.EFFECTED_LIST.contains(contractStatus)) {
            if (contractStatus.contains("17")) {
                return ContractStatus.EXPIRED;
            } else {
                return ContractStatus.EFFECTED;
            }
        }
        if (ContractStatus.REPAYMENT_LIST.contains(contractStatus)) {
            return ContractStatus.REPAYMENT;
        }
        if (ContractStatus.OVERDUE_LIST.contains(contractStatus)) {
            return ContractStatus.OVERDUE;
        }
        if (ContractStatus.RECAPTURED_LIST.contains(contractStatus)) {
            return ContractStatus.RECAPTURED;
        }
        if (ContractStatus.TERMINATED_LIST.contains(contractStatus)) {
            return ContractStatus.TERMINATED;
        }
        if (ContractStatus.DISBANDED_LIST.contains(contractStatus)) {
            return ContractStatus.DISBANDED;
        }
        return "";
    }

    /**
     * 判断是否快要逾期
     *
     * @param contractCreateTime 合约创建时间
     * @return 是否逾期状态
     */
    public static boolean isFastOverdue(String contractCreateTime) {
        long timeFor24 = 24 * 60 * 60;
        Long timeDiff = System.currentTimeMillis() / 1000 - Long.valueOf(contractCreateTime);
        if (timeDiff <= timeFor24) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前合约状态对应的所有业务状态
     *
     * @param contractStatus 合约状态 0.部署成功 1.已发布 2.已生效 3.已还款 4.已取回抵押资产 5.已解散 6.24小时后预期 7.已逾期
     * @return 业务状态区间范围
     */
    public static boolean isBetweenContractList(String contractStatus, String serviceStatus) {
        if (contractStatus.equals("0")) {
            return ContractStatus.DEPLOY_LIST.contains(serviceStatus);
        }
        if (contractStatus.equals("1")) {
            return ContractStatus.RELEASED_LIST.contains(serviceStatus);
        }
        if (contractStatus.equals("2")) {
            return ContractStatus.EFFECTED_LIST.contains(serviceStatus);
        }
        if (contractStatus.equals("3")) {
            return ContractStatus.REPAYMENT_LIST.contains(serviceStatus);
        }
        if (contractStatus.equals("4")) {
            return ContractStatus.RECAPTURED_LIST.contains(serviceStatus);
        }
        if (contractStatus.equals("5")) {
            return ContractStatus.DISBANDED_LIST.contains(serviceStatus);
        }
        if (contractStatus.contains("6")) {
            return ContractStatus.EFFECTED_LIST.contains(serviceStatus);
        }
//        contractStatus.equals("7")
        return ContractStatus.OVERDUE_LIST.contains(serviceStatus);
    }

    /**
     * 复制文本
     *
     * @param walletAddress 钱包地址
     */
    public static void copyText(Context context, final String walletAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    //第一个参数，是描述复制的内容，也可以和内容一样。
                    ClipData copyData = ClipData.newPlainText(walletAddress, walletAddress);
                    if (mClipboardManager != null) {
                        mClipboardManager.setPrimaryClip(copyData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }).start();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }



    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (TextUtils.isEmpty(ServiceName)) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
}

package com.lianer.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by goldze on 2017/5/14.
 * 常用工具类
 */
public final class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("should be initialized in application");
    }


    /**
     * 判断两个集合的元素和顺序是否相同
     * @param list1
     * @param list2
     * @return
     */
    public static boolean eqList(List<String> list1, List<String> list2){
        boolean bl = true;
        if(list1.size() == list2.size()){
            for(int i=0; i<list1.size(); i++){
                if((list1.get(i)).equals(list2.get(i))){
                    continue;
                } else {
                    bl = false;
                    break;
                }
            }
        } else {
            bl = false;
        }
        return bl;
    }

    public static List<String> getJsonFromAssets (String floderName) {
        List<String> jsonStrList = new ArrayList<>();
        String[] files;
        try {
            // 获得Assets一共有几多文件
            files = context.getResources().getAssets().list(floderName);
            for (String file : files) {
                jsonStrList.add(getJson(floderName, file, context));
            }
        } catch (IOException e1) {
            return null;
        }
        return jsonStrList;
    }

    private static String getJson(String floderName, String fileName,Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(floderName + "/" + fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 读取Assets中图片
     * @param fileName  assets对应的文件目录
     * @return 图片位图
     */
    public static Bitmap getImageFromAssetsFile(String fileName)
    {
        Bitmap image = null;
        AssetManager am = getContext().getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return image;

    }

    /**
     * 保留4位小数
     * @param str  字符串数值
     * @return
     */
    public static String switchTo4Decimal (String str) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.0000");
        return df.format(Double.valueOf(str));
    }


    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全
     *
     * @param v     需要格式化的数字
     * @param scale 小数点后保留几位
     * @return
     */
    public static String roundByScale(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if(scale == 0){
            return new DecimalFormat("0.").format(v);
        }
        String formatStr = "0.";
        for(int i=0;i<scale;i++){
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }

    /**
     * 判断通知是否开启
     * @param context
     * @return
     */
    public static boolean isNotificationsEnabled(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        boolean isOpened = manager.areNotificationsEnabled();
        return isOpened;
    }

    /**
     * Double数值过大时,默认使用科学计数法的解决办法
     */
    public static String doubleFormat (double value) {
        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        String bbb = nf.format(value);
        return nf.format(value);
    }


}
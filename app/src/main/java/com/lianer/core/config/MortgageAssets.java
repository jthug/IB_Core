package com.lianer.core.config;

import android.content.Context;

import com.google.gson.Gson;
import com.lianer.common.utils.KLog;
import com.lianer.core.contract.bean.TokenMortgageBean;
import com.lianer.core.contract.bean.TokenMortgageEntity;
import com.lianer.core.stuff.HLError;
import com.lianer.core.stuff.HLSubscriber;
import com.lianer.core.utils.HttpUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class MortgageAssets {
    private static  final  String FILE_NAME = "tokenMortgage.txt";


    private static List<TokenMortgageBean> tokenMortgageList = new ArrayList<>();

    public static List<TokenMortgageBean> getTokenMortgageList(Context context) {
        if (tokenMortgageList.size() == 0) {
            String result = load(context);
            if(result != null){
                result = result.replaceAll("\\u0000","");
                TokenMortgageEntity response = new Gson().fromJson(result,TokenMortgageEntity.class);
                tokenMortgageList.addAll(response.getData());
            }

        }
        return tokenMortgageList;
    }


    /**
     * 获取响应数据
     */
    public static void refreshMortgageToken(Context context,boolean needLoading) {

        HttpUtil.queryMortgageToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HLSubscriber<TokenMortgageEntity>(context, needLoading) {
                    @Override
                    protected void success(TokenMortgageEntity data) {
                        tokenMortgageList.clear();
                        tokenMortgageList.addAll(data.getData());
                        String result = new Gson().toJson(data);
                        KLog.w("MortgageAssets = "+result);
                        save(context,result);
                    }

                    @Override
                    protected void failure(HLError error) {
                        KLog.w(error);
                    }
                });
    }

    private static void save(Context context,String JsonData){
        try {
            FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
            osw.write(JsonData);
            osw.flush();
            fos.flush();  //flush是为了输出缓冲区中所有的内容
            osw.close();
            fos.close();  //写入完成后，将两个输出关闭
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String load(Context context){
        try {
            FileInputStream fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            char[] input = new char[fis.available()];  //available()用于获取filename内容的长度
            isr.read(input);  //读取并存储到input中

            isr.close();
            fis.close();//读取完成后关闭

            String str = new String(input); //将读取并存放在input中的数据转换成String输出
            return str;
        } catch (FileNotFoundException e) {
            //文件为空，发起请求
            refreshMortgageToken(context,false);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getTokenAddress(Context context,String type) {
        for (TokenMortgageBean date:getTokenMortgageList(context)) {
            if(date.getTokenNum().equals(type)){
                return date.getTokenAddress();
            }
        }
        return "";
    }


    public static String getTokenSymbol(Context context ,String type) {
        for (TokenMortgageBean date:getTokenMortgageList(context)) {
            if(date.getTokenNum().equals(type)){
                return date.getTokenAbbreviation();
            }
        }
        return "";
    }


    public static String getTokenSymbolByAddress(Context context ,String address) {
        for (TokenMortgageBean date:getTokenMortgageList(context)) {
            if(date.getTokenAddress().equalsIgnoreCase(address)){
                return date.getTokenAbbreviation();
            }
        }
        return "";
    }

    public static String getTokenTypeByAddress(Context context ,String address) {
        for (TokenMortgageBean date:getTokenMortgageList(context)) {
            if(date.getTokenAddress().equalsIgnoreCase(address)){
                return date.getTokenNum();
            }
        }
        return "";
    }

    public static int  getTokenIndex(Context context ,String type) {
        for (int i = 0 ; i< getTokenMortgageList(context).size() ;i++) {
            if(tokenMortgageList.get(i).getTokenNum().equals(type)){
                return i;
            }
        }
        return 0;
    }

    public static String  getTokenLogo(Context context,String type) {
        for (TokenMortgageBean date:getTokenMortgageList(context)) {
            if(date.getTokenNum().equals(type)){
                return date.getTokenImage();
            }
        }
        return "";
    }


    public static TokenMortgageBean  getTokenBean(Context context,String type) {
        for (TokenMortgageBean date:getTokenMortgageList(context)) {
            if(date.getTokenNum().equals(type)){
                return date;
            }
        }
        return null;
    }

}

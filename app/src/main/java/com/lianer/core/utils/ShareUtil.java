package com.lianer.core.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.lianer.core.R;
import com.lianer.core.SmartContract.IBContractUtil;
import com.lianer.core.app.Constants;
import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.manager.HLWalletManager;

import org.web3j.utils.Convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Flowable;

public class ShareUtil {

    public static void shareContract(Context context,Bitmap bitmap){
        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null,null));
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        // 指定发送的内容 (EXTRA_STREAM 对于文件 Uri
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

        // 指定发送内容的类型 (MIME type)
        sendIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(sendIntent, "Contract Share"));
    }

    public static Bitmap generateSharePic(Context context ,ContractBean contractBean){
        Bitmap bmp  = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        canvas.drawColor(context.getResources().getColor(R.color.c2));
        //nest logo
        canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.nest_white),442,180,paint);

        paint.setColor(context.getResources().getColor(R.color.c9));
        //line
        canvas.drawLine(48,360,1032,360,paint);
        canvas.drawLine(48,1080,1032,1080,paint);

        paint.setColor(context.getResources().getColor(R.color.c11));
        paint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.t2));
        canvas.drawText(context.getString(R.string.contract_type),96,520,paint);
        canvas.drawText(context.getString(R.string.invest_assets),96,600,paint);
        canvas.drawText(context.getString(R.string.mortgaged_assets),96,680,paint);
        canvas.drawText(context.getString(R.string.borrow_cycle),96,760,paint);
        canvas.drawText(context.getString(R.string.due_payment),96,840,paint);
        canvas.drawText(context.getString(R.string.contract_address),96,920,paint);

        paint.setColor(context.getResources().getColor(R.color.c7));
        paint.setFakeBoldText(true);//设置是否为粗体文字
        paint.setTextAlign(Paint.Align.RIGHT);//设置Paint文字对齐
        canvas.drawText(context.getString(R.string.contract_eth),984,520,paint);
        canvas.drawText(toEther(contractBean.getAmount()) +" ETH",984,600,paint);
        canvas.drawText( CommomUtil.getTokenValue(context,contractBean.getTokenAddress(),contractBean.getMortgage())+" "+ MortgageAssets.getTokenSymbolByAddress(context,contractBean.getTokenAddress()),984,680,paint);
        canvas.drawText(Integer.valueOf(contractBean.cycle)/86400 +" 天",984,760,paint);
        canvas.drawText(toEther(contractBean.expire )+" ETH",984,840,paint);
        canvas.drawText(CommomUtil.splitWalletAddress(contractBean.getContractAddress()),984,920,paint);

        String type = null;
        if(contractBean.getContractType() == null || contractBean.getContractType().equals("0")){
            type = "&type=ETH";
        }else if(contractBean.getContractType().equals("1") || contractBean.getContractType().equals("2")){
            type = "&type=TUSD";
        }
        canvas.drawBitmap(QRCodeUtil.createQRCodeBitmap(Constants.SHARE_URL + contractBean.getContractAddress() + type,540,540),270,1230,paint);

        return bmp;
    }

    public  static void saveImageToGallery(Context context, Bitmap bmp,String imgName) {
        String local_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 创建文件夹
//        File appDir = new File(Environment.getExternalStorageDirectory(), "imageok");
        File appDir = new File(local_path, "ContractSnapshot");
        //判断不存在就创建
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //以时间命名
        String fileName = CommomUtil.splitWalletAddress(imgName) + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        file.delete();
//        // 最后通知图库更新
//        String path = Environment.getExternalStorageDirectory().getPath();
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));

    }



    public static Flowable<Boolean> exportContractQRImg(Context context ,List<ContractBean> contractBeans){
        return Flowable.just(1)
                .flatMap(s -> {
                    for (ContractBean data: contractBeans) {
                        Bitmap bmp = generateSharePic(context,data);
                        saveImageToGallery(context,bmp,data.getContractAddress());
                    }
                    // 最后通知图库更新
                    String path = Environment.getExternalStorageDirectory().getPath();
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                    return Flowable.just(true);
                });
    }

    private static  BigDecimal toEther(String value){
        return Convert.fromWei(value,Convert.Unit.ETHER);
    }

    private static BigDecimal toToken (Context context,String value,String address){
        int decimal = IBContractUtil.getTokenDecimals(TransferUtil.getWeb3j(), HLWalletManager.shared().getCurrentWallet(context).getAddress(),address);
        return new BigDecimal(value).divide(new BigDecimal("10").pow(decimal));
    }

}

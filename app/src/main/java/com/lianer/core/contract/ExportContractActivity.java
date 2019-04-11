package com.lianer.core.contract;



import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.lianer.core.R;

import com.lianer.core.base.BaseActivity;

import com.lianer.core.config.MortgageAssets;
import com.lianer.core.contract.bean.ContractBean;
import com.lianer.core.databinding.ActivityExportContractBinding;

import com.lianer.core.utils.CommomUtil;
import com.lianer.core.utils.DBUtil;
import com.lianer.core.utils.QRCodeUtil;
import com.lianer.core.utils.SnackbarUtil;

import org.web3j.utils.Convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;


/**
 * 转入收款
 *
 * @author bowen
 */
public class ExportContractActivity extends BaseActivity implements View.OnClickListener {

    private ActivityExportContractBinding mBinding;
    private ContractBean mContractBean;

    @Override
    protected void initViews() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_export_contract);
        mBinding.showQrCode.setOnClickListener(this);
        mBinding.btnSave.setOnClickListener(this);
        mBinding.btnShare.setOnClickListener(this);
    }


    private Bitmap bitmap;
    private File file;
    @Override
    protected void initData() {
        long contractId = getIntent().getLongExtra("ContractId",0);
        mContractBean = DBUtil.queryContractById(contractId);
        mBinding.showQrCode.setImageBitmap(QRCodeUtil.createQRCodeBitmap(mContractBean.getContractAddress(),720,720));

        mBinding.investAssets.setText(toEther(mContractBean.getAmount()) +" ETH");
        mBinding.mortgagedAssets.setText(toEther(mContractBean.getMortgage()) +" "+ MortgageAssets.getTokenSymbolByAddress(getApplicationContext(),mContractBean.getTokenAddress()));
        mBinding.borrowCycle.setText(mContractBean.cycle +" 天");
        mBinding.duePayment.setText(toEther(mContractBean.expire )+" ETH");
        mBinding.contractAddress.setText(CommomUtil.splitWalletAddress(mContractBean.getContractAddress()));


    }
    private BigDecimal toEther(String value){
        return Convert.fromWei(value,Convert.Unit.ETHER);
    }

    private Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();
        cv.setDrawingCacheEnabled(true);
        cv.buildDrawingCache();
        Bitmap bmp = cv.getDrawingCache();
        if (bmp == null) {
            return null;
        }
        bmp.setHasAlpha(false);
        bmp.prepareToDraw();
        return bmp;
    }


    /**
     * Activity screenCap
     *
     * @param activity
     * @return
     */
    public  Bitmap activityShot(Activity activity) {
        /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();

        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        WindowManager windowManager = activity.getWindowManager();

        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        //去掉状态栏
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width,
                height-statusBarHeight);

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        // 最后通知图库更新
        String path = Environment.getExternalStorageDirectory().getPath();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),"Save Success");
        return bitmap;
    }



    public  void saveImageToGallery(Activity context, Bitmap bmp) {
        String local_path = Environment.getExternalStorageDirectory().getAbsolutePath();
        // 创建文件夹
//        File appDir = new File(Environment.getExternalStorageDirectory(), "imageok");
        File appDir = new File(local_path, "ContractSnapshot");
        //判断不存在就创建
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //以时间命名
        String fileName = CommomUtil.splitWalletAddress(mContractBean.getContractAddress()) + ".jpg";
         file = new File(appDir, fileName);
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
        // 最后通知图库更新
        String path = Environment.getExternalStorageDirectory().getPath();
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));

    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


   private void share(ContractBean contractBean){
       Intent sendIntent = new Intent();
       sendIntent.setAction(Intent.ACTION_SEND);

//        // 指定发送的内容 (EXTRA_STREAM 对于文件 Uri )
       sendIntent.putExtra(Intent.EXTRA_STREAM, getImageContentUri(this,file));

        // 指定发送内容的类型 (MIME type)
       sendIntent.setType("image/jpeg");
       startActivity(Intent.createChooser(sendIntent, "Contract Share"));
   }

    private Bitmap paint(){
        Bitmap bmp  = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        canvas.drawColor(getResources().getColor(R.color.c2));
        //nest logo
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nest_white),442,180,paint);

        paint.setColor(getResources().getColor(R.color.c9));
        //line
        canvas.drawLine(48,360,1032,360,paint);
        canvas.drawLine(48,1080,1032,1080,paint);

        paint.setColor(getResources().getColor(R.color.c11));
        paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.t2));
        canvas.drawText(getString(R.string.contract_type),96,520,paint);
        canvas.drawText(getString(R.string.invest_assets),96,600,paint);
        canvas.drawText(getString(R.string.mortgaged_assets),96,680,paint);
        canvas.drawText(getString(R.string.borrow_cycle),96,760,paint);
        canvas.drawText(getString(R.string.due_payment),96,840,paint);
        canvas.drawText(getString(R.string.contract_address),96,920,paint);

        paint.setColor(getResources().getColor(R.color.c7));
        paint.setFakeBoldText(true);//设置是否为粗体文字
        paint.setTextAlign(Paint.Align.RIGHT);//设置Paint文字对齐
        canvas.drawText(getString(R.string.contract_eth),984,520,paint);
        canvas.drawText(toEther(mContractBean.getAmount()) +" ETH",984,600,paint);
        canvas.drawText(toEther(mContractBean.getMortgage()) +" "+ MortgageAssets.getTokenSymbolByAddress(getApplicationContext(),mContractBean.getTokenAddress()),984,680,paint);
        canvas.drawText(mContractBean.cycle +" 天",984,760,paint);
        canvas.drawText(toEther(mContractBean.expire )+" ETH",984,840,paint);
        canvas.drawText(CommomUtil.splitWalletAddress(mContractBean.getContractAddress()),984,920,paint);


        canvas.drawBitmap(QRCodeUtil.createQRCodeBitmap(mContractBean.getContractAddress(),540,540),270,1230,paint);


        return bmp;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
//                bitmap  = captureScreen(this);
                bitmap  =  paint();
                saveImageToGallery(this,bitmap);
                SnackbarUtil.DefaultSnackbar(mBinding.getRoot(),"Save Success").show();
                break;
            case R.id.btn_share:
                bitmap  =  activityShot(this);
                saveImageToGallery(this,bitmap);
                share(mContractBean);
                break;

        }
    }
}

package com.lianer.core.qrcode;


/**
 * @Class: QRCodeScanActivity
 * @Description: 自定义条形码/二维码扫描
 */
public class QRCodeScanActivity  {

//    /**
//     * 条形码扫描管理器
//     */
//    private CaptureManager mCaptureManager;
//
//    /**
//     * 条形码扫描视图
//     */
//    private DecoratedBarcodeView mBarcodeView;
//    private final static  int DEVICE_PHOTO_REQUEST = 10;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrcode_scan);
//        mBarcodeView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);
//        mCaptureManager = new CaptureManager(this, mBarcodeView);
//        mCaptureManager.initializeFromIntent(getIntent(), savedInstanceState);
//        mCaptureManager.decode();
//
//        TitlebarView titlebar = findViewById(R.id.titlebar);
//        titlebar.showLeftDrawable();
//        titlebar.setOnViewClick(new TitlebarView.onViewClick() {
//            @Override
//            public void leftClick() {
//                onBackPressed();
//            }
//
//            @Override
//            public void rightTextClick() {
//
//            }
//
//            @Override
//            public void rightImgClick() {
//            }
//        });
//
//        findViewById(R.id.btn_album).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, DEVICE_PHOTO_REQUEST);
//            }
//        });
//        checkPermission();
//    }
//
//    private void checkPermission() {
//        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
//                    .WRITE_EXTERNAL_STORAGE)) {
//                SnackbarUtil.DefaultSnackbar(QRCodeScanActivity.this.getWindow().getDecorView(), getString(R.string.request_permissions)).show();
//            }
//            //申请权限
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        //用户没有进行有效的设置操作，返回
//        if (requestCode == Activity.RESULT_CANCELED ){
//
//            return;
//        }
//        if (DEVICE_PHOTO_REQUEST == requestCode){
//            if (null != data) {
//                Uri selectedImage = data.getData();
//                String[] filePathColumns = {MediaStore.Images.Media.DATA};
//                String imagePath;
//                Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
//                if (c != null) {
//                    c.moveToFirst();
//                    int columnIndex = c.getColumnIndex(filePathColumns[0]);
//                    imagePath = c.getString(columnIndex);
//                    c.close();
//
//                    Bitmap bm = BitmapFactory.decodeFile(imagePath);
//                    decodeQRCode(bm);
//                }
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void decodeQRCode(Bitmap bmp){
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                return QRCodeDecoder.syncDecodeQRCode(bmp);
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                if (TextUtils.isEmpty(result)) {
//                    SnackbarUtil.DefaultSnackbar(QRCodeScanActivity.this.getWindow().getDecorView(), getString(R.string.decode_failed)).show();
//                } else {
//                    resultIntent(result);
//                }
//            }
//        }.execute();
//    }
//
//    private  void   resultIntent(String result) {
//        Intent intent = new Intent(Intents.Scan.ACTION);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        intent.putExtra(Intents.Scan.RESULT, result);
//        QRCodeScanActivity.this.setResult(Activity.RESULT_OK, intent);
//        finish();
//    }
//
//
//    @Override
//    protected void initViews() {
//
//    }
//
//    @Override
//    protected void initData() {
//
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mCaptureManager.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mCaptureManager.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mCaptureManager.onDestroy();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mCaptureManager.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 权限处理
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        mCaptureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    /**
//     * 按键处理
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return mBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
//    }
}

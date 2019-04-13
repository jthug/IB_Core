package com.lianer.core.app;


import android.content.Context;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.google.gson.Gson;
import com.lianer.common.base.BaseApplication;

import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Utils;
import com.lianer.common.utils.language.MultiLanguageUtil;
import com.lianer.core.R;
import com.lianer.core.custom.refresh.CustomRefreshFooter;
import com.lianer.core.custom.refresh.CustomRefreshHeader;
import com.lianer.core.greendao.gen.ContractBeanDao;
import com.lianer.core.greendao.gen.DaoMaster;
import com.lianer.core.greendao.gen.DaoSession;
import com.lianer.core.greendao.gen.MessageCenterBeanDao;
import com.lianer.core.model.HLWallet;
import com.lianer.core.wallet.bean.TokenProfileBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tendcloud.tenddata.TCAgent;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class NestApp extends BaseApplication {

    public static String BUGGLY_APP_ID = "cb54518660";
    public static  HLWallet tempHlWallet = null;//临时存储创建后但未进行助记词验证的钱包对象

    private static String DB_NAME = "nest.db";

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        //开启打印日志
        KLog.init(true);

        MultiLanguageUtil.init(this);

        initAssetsList();

//        initBugly();

        initDB();
//        //Growing iO 数据统计SDK
//        GrowingIO.startWithConfiguration(this, new Configuration()
//                .trackAllFragments()
//                .setChannel("官方")
//        );

        //TalkingData SDK
        if (Constants.IS_TEST){  //测试环境不开启bug统计

        }else {
            TCAgent.LOG_ON = true;
            TCAgent.init(this);
            TCAgent.setReportUncaughtExceptions(true);
        }

    }

    private void initAssetsList() {
        if (Constants.tokenProfileBeans.size() == 0) {
            Gson gson = new Gson();

            List<String> erc20s = Utils.getJsonFromAssets(Constants.ASSETS_ERC_20);
            if (erc20s != null) {
                for (String erc20 : erc20s) {
                    TokenProfileBean tokenProfileBean = gson.fromJson(erc20, TokenProfileBean.class);

                    //如果是测试环境
                    if (Constants.IS_TEST){
                        if (tokenProfileBean.getSymbol().equals("NEST")){ //如果是Nest
                            tokenProfileBean.setAddress(Constants.ASSETS_NEST_ADDRESS);
                        }
                    }

                    Constants.tokenProfileBeans.add(tokenProfileBean);
                }
            }
            List<String> coins = Utils.getJsonFromAssets(Constants.ASSETS_COINS);
            if (coins != null) {
                for (String coin : coins) {
                    TokenProfileBean tokenProfileBean = gson.fromJson(coin, TokenProfileBean.class);
                    Constants.tokenProfileBeans.add(tokenProfileBean);
                }
            }

            //添加HHQ，ZXF
            TokenProfileBean hhq = new TokenProfileBean();
            hhq.setAddress(Constants.HHQAddress);
            hhq.setDecimals("18");
            hhq.setSymbol("HHQ");
            TokenProfileBean zxf = new TokenProfileBean();
            zxf.setAddress(Constants.ZXFAddress);
            zxf.setDecimals("18");
            zxf.setSymbol("ZXF");
            Constants.tokenProfileBeans.add(hhq);
            Constants.tokenProfileBeans.add(zxf);
        }
    }

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.c0, android.R.color.darker_gray);//全局设置主题颜色
            return new CustomRefreshHeader(context);
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            //指定为经典Footer，默认是 BallPulseFooter
            return new CustomRefreshFooter(context).setDrawableSize(20);
        });
    }

//    private void initBugly() {
//        /* Bugly SDK初始化
//         * 参数1：上下文对象
//         * 参数2：APPID，平台注册时得到,注意替换成你的appId
//         * 参数3：是否开启调试模式，调试模式下会输出'CrashReport'tag的日志
//         * CrashReport.testJavaCrash(); 测试
//         */
//        CrashReport.initCrashReport(getApplicationContext(), BUGGLY_APP_ID, Constants.IS_BUGGLY);
//    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper(this, DB_NAME);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {
        public MySQLiteOpenHelper(Context context, String name) {
            super(context, name);
        }
        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {

                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
            },ContractBeanDao.class, MessageCenterBeanDao.class);
        }
    }


}

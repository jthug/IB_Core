package com.lianer.common.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.lianer.common.utils.KLog;
import com.lianer.common.utils.Utils;
import com.lianer.common.utils.language.MultiLanguageUtil;

public class BaseApplication extends Application {

    private static BaseApplication sInstance;

    private static ActivityLifecycleCallbacks mCallbacks;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Utils.init(this);

        KLog.init(true);


        mCallbacks = new ActivityLifecycleCallbacks();

        //注册监听每个activity的生命周期,便于堆栈式管理
        registerActivityLifecycleCallbacks(mCallbacks);
    }

    /**
     * 获得当前app运行的AppContext
     */
    public static BaseApplication getInstance() {
        return sInstance;
    }

    public class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private int startCount;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            AppManager.getAppManager().addActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

            startCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

            startCount--;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {

            AppManager.getAppManager().removeActivity(activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        public int getStartCount(){
            return startCount;
        }
    }

    /**
     *
     * @return  true  处于后台   false  前台
     */
    public static boolean isAppBackground() {
        if (mCallbacks.getStartCount() == 0) {
            return true;
        }
        return false;
    }
}

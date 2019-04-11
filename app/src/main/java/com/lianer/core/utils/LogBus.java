package com.lianer.core.utils;

import android.util.Log;

public class LogBus {
    private static boolean debug = true;

    public LogBus() {
    }

    public static void setDebug(boolean bool) {
        debug = bool;
    }

    public static void e(String tag, String msg) {
        if(debug) {
            show(tag, msg);
        }

    }

    public static void show(String tag, String content) {
        try {
            short e = 2048;
            long length = (long)content.length();
            if(length >= (long)e && length != (long)e) {
                while(true) {
                    if(content.length() <= e) {
                        Log.e(tag, content);
                        break;
                    }

                    String logContent = content.substring(0, e);
                    content = content.replace(logContent, "");
                    Log.e(tag, logContent);
                }
            } else {
                Log.e(tag, content);
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

    }

    public static void e(String tag, String msg, Throwable tr) {
        if(debug) {
            Log.e(tag, msg, tr);
        }

    }

    public static void i(String tag, String msg) {
        if(debug) {
            Log.i(tag, msg);
        }

    }

    public static void i(String tag, String msg, Throwable tr) {
        if(debug) {
            Log.i(tag, msg, tr);
        }

    }
}

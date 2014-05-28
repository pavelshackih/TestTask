package com.testtask.app;

import android.util.Log;

/**
 *
 */
@SuppressWarnings("unused")
public class L {

    static boolean isDebug = BuildConfig.DEBUG;

    private L() {
        throw new AssertionError();
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable throwable) {
        if (isDebug) {
            Log.e(tag, message, throwable);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug) {
            Log.e(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (isDebug) {
            Log.w(tag, message);
        }
    }

    public static void w(String tag, String message, Throwable throwable) {
        if (isDebug) {
            Log.w(tag, message, throwable);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug) {
            Log.v(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (isDebug) {
            Log.i(tag, message);
        }
    }

    public static void d(String tag, String message, Throwable throwable) {
        if (isDebug) {
            Log.d(tag, message, throwable);
        }
    }
}

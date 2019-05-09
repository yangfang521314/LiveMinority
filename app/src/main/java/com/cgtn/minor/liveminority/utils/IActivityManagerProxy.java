package com.cgtn.minor.liveminority.utils;

import android.content.Intent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * created by yf on 2019/3/7.
 */
public class IActivityManagerProxy implements InvocationHandler {
    private Object mActivityManager;
    private static final String TAG = "IActivityManagerProxy";
    public IActivityManagerProxy(Object activityManager) {
        this.mActivityManager = activityManager;
    }
    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if ("startActivity".equals(method.getName())) {//1
            Intent intent = null;
            int index = 0;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            intent = (Intent) args[index];
            Intent subIntent = new Intent();//2
            String packageName = "com.cgtn.minor.liveminority.ui.activity";
            subIntent.setClassName(packageName,packageName+".SubActivity");//3
            subIntent.putExtra(HookHelper.TARGET_INTENT, intent);//4
            args[index] = subIntent;//5
        }
        return method.invoke(mActivityManager, args);
    }
}

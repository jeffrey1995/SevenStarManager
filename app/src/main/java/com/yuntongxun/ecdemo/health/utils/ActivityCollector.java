package com.yuntongxun.ecdemo.health.utils;

import android.app.Activity;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrtian on 2016/5/13.
 */
public class ActivityCollector {
    public static String TAG = "ActivityCollector";
    private static List<Activity> activityList = new LinkedList<Activity>();
    /**
     * 添加Activity到容器中
     * @param activity
     */
    public static void addActivity(Activity activity)
    {
        Log.w(TAG,activity.getTaskId()+"add~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
        activityList.add(activity);
    }

    /**
     * 从容器中移除Activity
     * @param activity
     */
    public static void removeActivity(Activity activity) {
        Log.w(TAG,activity.getTaskId()+"remove~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
        activityList.remove(activity);
    }

    /**
     * 遍历所有Activity并finish
     */
    public static void finishAll()
    {
        for(Activity activity:activityList)
        {
            Log.w(TAG,activity.getTaskId()+"finish~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~`");
            activity.finish();
        }
    }
}

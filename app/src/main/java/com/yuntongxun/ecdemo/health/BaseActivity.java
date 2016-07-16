package com.yuntongxun.ecdemo.health;

import android.app.Activity;
import android.os.Bundle;

import com.yuntongxun.ecdemo.health.utils.ActivityCollector;


/**
 *
 * 所有Activity继承于BaseActivity
 * 方便管理所有的活动
 * 方便测试
 * Created by mrtian on 2016/5/11.
 */
public class BaseActivity extends Activity {
    public static String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);

    }
}

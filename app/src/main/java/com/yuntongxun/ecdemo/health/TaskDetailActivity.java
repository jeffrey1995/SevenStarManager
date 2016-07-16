package com.yuntongxun.ecdemo.health;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class TaskDetailActivity extends BaseActivity
{
    private TextView userName;
    private TextView contactTel;
    private TextView taskDate;
    private TextView taskTime;
    private TextView taskAddress;
    private TextView taskIntroduction;
    private TextView taskTarget;
    private TextView taskNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        initView();
    }
    private void initView()
    {
        userName = (TextView)findViewById(R.id.user_name);
        contactTel = (TextView)findViewById(R.id.contact_tel);
        taskDate = (TextView)findViewById(R.id.task_date);
        taskTime = (TextView)findViewById(R.id.task_time);
        taskAddress = (TextView)findViewById(R.id.task_address);
        taskIntroduction = (TextView)findViewById(R.id.task_introduction);
        taskTarget = (TextView)findViewById(R.id.task_target);
        taskNotice = (TextView)findViewById(R.id.task_notice);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Map<String, String> map = (HashMap)bundle.getSerializable("data");
        userName.setText(map.get("userName"));
        contactTel.setText(map.get("contactTel"));
        taskAddress.setText(map.get("address"));
        taskDate.setText(map.get("date"));

        taskTime.setText(map.get("startTime") + "-" + map.get("endTime"));

        String temp = "----------";
        taskIntroduction.setText(map.get("introduction") == null ? temp : map.get("introduction"));
        taskTarget.setText(map.get("target") == null ? temp : map.get("target"));
        taskNotice.setText(map.get("notice") == null ? temp : map.get("notice"));
    }
}

package com.yuntongxun.ecdemo.health;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.health.utils.ActivityCollector;
import com.yuntongxun.ecdemo.ui.account.LoginActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener
{
    private Button taskList;
    private Button taskReport;
    private Button orderService;
    private Button incomeLook;
    private Button trainMessage;
    private Button customsMessage;
    private Button employLocation;
    private Button vedioTalk;
    private ImageView iv_information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView()
    {
        taskList = (Button)findViewById(R.id.task_list);
        taskReport =(Button)findViewById(R.id.task_report);
        orderService =(Button)findViewById(R.id.order_service);
        incomeLook=(Button)findViewById(R.id.income_look);
        trainMessage =(Button)findViewById(R.id.train_message);
        customsMessage =(Button)findViewById(R.id.customs_message);
        employLocation=(Button)findViewById(R.id.employ_location);
        vedioTalk=(Button)findViewById(R.id.vedio_talk);
        iv_information = (ImageView) findViewById(R.id.iv_information);

        taskList.setOnClickListener(this);
        taskReport.setOnClickListener(this);
        orderService.setOnClickListener(this);
        incomeLook.setOnClickListener(this);
        trainMessage.setOnClickListener(this);
        customsMessage.setOnClickListener(this);
        employLocation.setOnClickListener(this);
        vedioTalk.setOnClickListener(this);
        iv_information.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            //个人信息
            case R.id.iv_information:
                startActivity(new Intent(this,UserInformation.class));
                break;
            //任务报告
            case R.id.task_report:
            {
                //任务列表、任务报告和员工定位打开的是同一个activity，但是该activity进行的操作不同，用一个flag标识
                Intent intent = new Intent(this, TaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("flag", 2);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            //任务列表
            case R.id.task_list:
            {
                //任务列表、任务报告和员工定位打开的是同一个activity，但是该activity进行的操作不同，用一个flag标识
                Intent intent = new Intent(this, TaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("flag", 0);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            //抢单服务
            case R.id.order_service:
                startActivity(new Intent( this, OrderServiceActivity.class));
                break;
            //收入查看
            case R.id.income_look:
                startActivity(new Intent( this, IncomeCheckActivity.class));
                break;
            //培训信息
            case R.id.train_message:
                startActivity(new Intent( this, TrainInformationActivity.class));
                break;
            //客户推送
            case R.id.customs_message:
                startActivity(new Intent( this, PushActivity.class));
                break;
            //员工定位
            case R.id.employ_location:
            {
                //任务列表、任务报告和员工定位打开的是同一个activity，但是该activity进行的操作不同，用一个flag标识
                Intent intent = new Intent(this, TaskListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("flag", 1);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            //视频通话
            case R.id.vedio_talk:
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
    }
}

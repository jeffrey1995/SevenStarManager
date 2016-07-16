package com.yuntongxun.ecdemo.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.health.share.ShareManager;
import com.yuntongxun.ecdemo.health.utils.ActivityCollector;

public class UserInformation extends BaseActivity implements View.OnClickListener{
    public static String TAG = "UserInformation";
    private TextView tv_realName;
    private TextView tv_userName;
    private TextView tv_grade;
    private EditText edt_password;
    private ImageView iv_back;
    private Button btn_exit;
    private ShareManager shareManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_information);
        shareManager = new ShareManager(this);
        initView();
        getInformation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getInformation() {
        tv_realName.setText(shareManager.getAccountName());
        tv_userName.setText(shareManager.getAccount());
        edt_password.setText(shareManager.getPassword());
        tv_grade.setText(shareManager.getStarID());
    }

    private void initView() {
        tv_realName = (TextView) findViewById(R.id.tv_real_name);
        tv_userName = (TextView) findViewById(R.id.tv_account);
        tv_grade = (TextView) findViewById(R.id.tv_grade);
        edt_password = (EditText) findViewById(R.id.edt_password);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btn_exit = (Button) findViewById(R.id.btn_exit);

        edt_password.setFocusable(false);

        iv_back.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.iv_back:    //返回上一级
                finish();
                break;
            case R.id.btn_exit:    //退出登录
                shareManager.setIsLogin(false);  //设置成未登录
                ActivityCollector.finishAll();   //销毁所有活动
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}

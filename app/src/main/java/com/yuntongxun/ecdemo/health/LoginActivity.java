package com.yuntongxun.ecdemo.health;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.health.share.ShareContents;
import com.yuntongxun.ecdemo.health.share.ShareManager;
import com.yuntongxun.ecdemo.health.utils.ActivityCollector;
import com.yuntongxun.ecdemo.health.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private String TAG = "LoginActivity";
    private EditText edt_account;
    private EditText edt_password;
    private CheckBox cbox_remember;
    private Button btn_login;
    private ShareManager shareManager;
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        initView();
        shareManager = new ShareManager(this);
        btn_login.setOnClickListener(this);
        getInformation();
        registerReceiver();
        if (shareManager.getIsLogin()) autoLogin(); //自动登录
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    /**
     * 注册广播监听网络
     */
    private void registerReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        //尝试登录
        Map<String,String> req = new HashMap<String,String>();
        req.put(ShareContents.account, shareManager.getAccount());
        req.put(ShareContents.password, shareManager.getPassword());
        req.put(ShareContents.isRemember, shareManager.getIsRemenber()+"");

        //网络请求
        volley_Post(getString(R.string.url_login), req);
    }

    /**
     * 登录
     */
    private void login() {
        Log.d(TAG,"start login!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        String account = edt_account.getText().toString();
        String password = edt_password.getText().toString();
        boolean isRemenber = cbox_remember.isChecked();
        if(checkInformation(account,password) == 0)//如果格式没问题的话
        {
            //尝试登录
            Map<String,String> req = new HashMap<String,String>();
            req.put(ShareContents.account, account);
            req.put(ShareContents.password, password);
            req.put(ShareContents.isRemember, isRemenber + "");

            //网络请求
            volley_Post(getString(R.string.url_login), req);
        }
    }

    private void initView() {
        edt_account = (EditText) findViewById(R.id.edt_account);
        edt_password = (EditText) findViewById(R.id.edt_password);
        cbox_remember = (CheckBox) findViewById(R.id.cbox_remenber);
        btn_login = (Button) findViewById(R.id.btn_login);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_login:
                login();
                break;
        }
    }

    /**
     * 返回码含义：
     * 0：格式正确
     * 1：账号或密码为空
     * @return
     */
    private int checkInformation(String account,String password)
    {

        if (account.equals("") || password.equals(""))
        {
            Toast.makeText(this,"账号或者密码不能为空！",Toast.LENGTH_SHORT).show();
            return 1;
        }
        return 0;
    }

    /**
     * 发送登录请求
     * @param url_add
     * @param req
     */
    private void volley_Post(String url_add, final Map<String,String> req)
    {
        String url = getString(R.string.url_host);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Log.d(TAG, "response!" + s);

                Map<String,Object> map = Utils.getGson().fromJson(s, Map.class);

                String status = map.get("status").toString();
                String msg = map.get("msg").toString();

                Log.d(TAG, status+"....."+msg);
                if(status.equals("1"))
                {

                    if(msg.equals("登录成功"))
                    {

                        Map<String,Object> data = (Map)map.get("data");

                        req.put(ShareContents.employeeID, (int)(double)data.get("employeeID") + "");
                        req.put(ShareContents.starID, (int)(double)data.get("starID") + "");
                        req.put(ShareContents.accountName, data.get("accountName").toString());

                        saveInformation(req);   //本地缓存数据

                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplication(),msg, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplication(),msg, Toast.LENGTH_LONG).show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //请求失败处理
                Toast.makeText(getApplication(),"login failed!",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                return req;
            }
        };
        request.setTag(TAG);
        ECApplication.getRequestQueue().add(request);
    }

    /**
     * 在登录成功后，保存信息
     * @param info
     */
    private void saveInformation(Map<String,String> info)
    {
        Log.d(TAG, "start saveInformation");
        ShareManager shareManager = new ShareManager(this);
        shareManager.setAccount(info.get(ShareContents.account).toString());
        shareManager.setPassword(info.get(ShareContents.password).toString());
        shareManager.setIsRemenber(Boolean.parseBoolean(info.get(ShareContents.isRemember)));
        shareManager.setStarID(info.get(ShareContents.starID).toString());
        shareManager.setAccountName(info.get(ShareContents.accountName).toString());
        shareManager.setEmployeeID(info.get(ShareContents.employeeID).toString());

        shareManager.setIsLogin(true); //设置成已登录状态
    }

    /**
     * 用来恢复保存的信息
     * @return
     */
    private void getInformation()
    {
        if (shareManager.getIsRemenber())
        {
            edt_account.setText(shareManager.getAccount());
            edt_password.setText(shareManager.getPassword());
            cbox_remember.setChecked(true);
        }
    }

    /**
     * 网络状态改变接收器
     */
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable())
            {
                Toast.makeText(context,"网络正常！",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context,"网络已断开，请检查网络连接！",Toast.LENGTH_SHORT).show();
            }
        }
    }
}

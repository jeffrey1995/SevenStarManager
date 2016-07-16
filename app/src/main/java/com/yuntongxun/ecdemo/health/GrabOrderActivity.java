package com.yuntongxun.ecdemo.health;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.health.share.ShareManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrabOrderActivity extends BaseActivity
{
    private TextView name;
    private TextView time;
    private TextView address;
    private Button grabOrder;

    private Map<String, Object> map;        //储存上一个activity传过来的数据
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        name = (TextView)findViewById(R.id.custom_name);
        time = (TextView)findViewById(R.id.service_time);
        address = (TextView)findViewById(R.id.service_address);
        grabOrder = (Button)findViewById(R.id.grab_order);

        initData();
        grabOrder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Map<String, String> req = new HashMap<>();
                req.put("employeeId", new ShareManager(GrabOrderActivity.this).getEmployeeID());
                int orderId = (int)(double)map.get("orderID");
                req.put("orderId", orderId + "");
                Log.w("lan", req.toString());
                volley_Post(getResources().getString(R.string.url_grab_order), req);
            }
        });
    }
    private void initData()
    {
        //将从上一个界面传来的数据填充到各个控件中
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        map = (HashMap)bundle.getSerializable("data");

        name.setText(map.get("name").toString());
        time.setText(map.get("orderDate").toString());
        address.setText(map.get("address").toString());
    }

    /**
     * 请求服务器数据
     * @param url_add   添加的请求url
     * @param req       参数列表
     */
    private void volley_Post(String url_add, final Map<String,String> req)
    {
        String url = getResources().getString(R.string.url_host);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String s)
            {
                //请求成功
                Map<String,Object> response = gson.fromJson(s,Map.class);

                Toast.makeText(GrabOrderActivity.this, response.get("msg").toString(),
                        Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                //请求失败处理
                Toast.makeText(getApplication(),"failed!",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                return req;
            }
        };
        request.setTag("doPost");
        ECApplication.getRequestQueue().add(request);
    }
}

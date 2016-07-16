package com.yuntongxun.ecdemo.health;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuntongxun.ecdemo.health.share.ShareManager;
import com.yuntongxun.ecdemo.health.utils.ActivityCollector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PushActivity extends BaseActivity implements View.OnClickListener
{
    private Button lastPage;
    private Button nextPage;
    private TextView currentPageTv;         //显式当前页数的textView
    private TextView totalPageTv;           //显式总页数的textView
    private int currentPage = 1;            //当前页数
    private int totalPage = 0;              //总页数
    private int pageCount = 10;             //页面容量

    private ListView listView;
    private List<Map<String, String>> list = new ArrayList<>();
    private SimpleAdapter adapter;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    //由工作线程向主线程发送消息
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            String string = (String)msg.obj;
            totalPage = Integer.parseInt(string);
            totalPageTv.setText(string);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_list);

        //上一页、下一页
        lastPage = (Button)findViewById(R.id.last_page);
        nextPage = (Button)findViewById(R.id.next_page);

        lastPage.setOnClickListener(this);
        nextPage.setOnClickListener(this);

        //当前页数、总页数
        currentPageTv = (TextView)findViewById(R.id.current_page);
        totalPageTv = (TextView)findViewById(R.id.total_page);
        currentPageTv.setText(currentPage + "");

        //数据列表
        listView = (ListView)findViewById(R.id.listView);
        getData(currentPage);
        adapter = new SimpleAdapter(PushActivity.this, list, R.layout.list_push,
                new String [] {"name", "classifiedName", "merchantName", "sellPrice"},
                new int [] {R.id.push_name, R.id.push_type, R.id.push_merchant, R.id.push_price});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent();
                intent.setClass(PushActivity.this, CustomListActivity.class);

                //将点击的item对应的数据传入下一个activity
                Bundle bundle = new Bundle();
                Map<String, String> map = list.get(position);
                bundle.putSerializable("data", map.get("name"));
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 包装请求服务器需要的参数，访问服务器
     * @param page 请求的页数
     */
    public void getData(int page)
    {
        Map<String, String> req = new HashMap<>();
        req.put("employeeId", new ShareManager(this).getEmployeeID());
        req.put("page", String.valueOf(page));
        req.put("size", String.valueOf(pageCount));
        volley_Post(getResources().getString(R.string.url_push_list), req);
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

                if(response.get("status").equals("0"))
                {
                    Toast.makeText(PushActivity.this, response.get("msg").toString(),
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //更新服务列表
                    Map<String, Object> data = (Map)response.get("data");
                    list.clear();
                    list.addAll((List) data.get("dataList"));
                    adapter.notifyDataSetChanged();
                    //得到总页数发送给主线程
                    int pageSize = (int)(double)data.get("totalPage");
                    Message msg = new Message();
                    msg.obj = pageSize + "";
                    handler.sendMessage(msg);
                }
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

    /**
     * 为此activity的控件注册监听器
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.last_page:
                toLastPage();
                break;
            case R.id.next_page:
                toNextPage();
                break;
            default:
                break;
        }
    }

    /**
     * 下一页点击事件
     */
    private void toNextPage()
    {
        if(currentPage != totalPage)
        {
            getData(++currentPage);
            currentPageTv.setText(currentPage + "");
        }
        else
        {
            Toast.makeText(this, "当前是最后一页", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上一页点击事件
     */
    private void toLastPage()
    {
        if(currentPage != 1)
        {
            getData(--currentPage);
            currentPageTv.setText(currentPage + "");
        }
        else
        {
            Toast.makeText(this, "当前是第一页", Toast.LENGTH_SHORT).show();
        }
    }
}

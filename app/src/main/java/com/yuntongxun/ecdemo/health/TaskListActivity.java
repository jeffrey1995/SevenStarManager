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
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
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

public class TaskListActivity extends Activity implements View.OnClickListener, TencentLocationListener
{
    private String TAG = "TaskListActivity";
    private int flag;                      //标记是该activity是怎样被启动的
    private int id;                        //记录被点击的item对应的任务ID

    private Button lastPage;
    private Button nextPage;
    private TextView currentPageTv;         //显式当前页数的textView
    private TextView totalPageTv;           //显式总页数的textView
    private int currentPage = 1;            //当前页数
    private int totalPage = 0;              //总页数
    private int pageCount = 5;             //页面容量

    private ListView listView;
    private List<Map<String, Object>> list = new ArrayList<>();
    private SimpleAdapter adapter;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();

    private TencentLocationManager locationManager;         //腾讯地图定位

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
        setContentView(R.layout.activity_task_list);

        //获得腾讯地图定位Manager
        locationManager = TencentLocationManager.getInstance(this);

        initView();
    }

    private void initView()
    {
        //得到从上一个activity传来的数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        flag = (int)bundle.get("flag");

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
        adapter = new SimpleAdapter(TaskListActivity.this, list, R.layout.list_task,
                new String [] {"userName"}, new int [] {R.id.task_custom_name});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Map<String, Object> map = list.get(position);
                //若是点击“任务列表”进入该activity，则点击查看任务详细
                if (flag == 0)
                {
                    Intent intent = new Intent();
                    intent.setClass(TaskListActivity.this, TaskDetailActivity.class);

                    //将点击的item对应的数据传入下一个activity
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", (Serializable) map);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }
                else if(flag == 1)           //若是点击“员工定位”进入该activity，则点击进行员工定位
                {
                    TaskListActivity.this.id = (int)(double)map.get("taskID");
                    startLocation();
                }
                else                         //若是点击“任务报告”进入该activity，则点击进入任务上传界面
                {
                    Intent intent = new Intent();
                    intent.setClass(TaskListActivity.this, TaskReportActivity.class);

                    //将点击的item对应的数据传入下一个activity
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", (int)(double)map.get("taskID"));
                    bundle.putString("userName", map.get("userName").toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocation();
        ActivityCollector.removeActivity(this);
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
        volley_Post(getResources().getString(R.string.url_task_list), req, 0);
    }

    /**
     * 请求服务器数据
     * @param url_add   添加的请求url
     * @param req       参数列表
     * @param flag      标记请求类型，0为请求任务列表，1为员工上传开始服务信息
     */
    private void volley_Post(String url_add, final Map<String,String> req, final int flag)
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

                if(flag == 1 || response.get("status").equals("0"))
                {
                    Toast.makeText(TaskListActivity.this, response.get("msg").toString(),
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

    //开始定位
    public void startLocation()
    {
        Log.w(TAG,"startLocation");
        //创建一个定位请求
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(0).setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        //执行请求
        int error = locationManager.requestLocationUpdates(request, this);
        Log.w(TAG,error + "");
    }

    //结束定位
    public void stopLocation()
    {
        locationManager.removeUpdates(this);
    }

    /**
     * 当定位位置发生变化时调用
     * @param tencentLocation 获得的地址
     * @param error           返回的错误码
     * @param s               错误原因
     */
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String s)
    {
        if (TencentLocation.ERROR_OK == error)
        {
            Log.w(TAG,"ERROR_OK");
            // 定位成功
            String city = tencentLocation.getCity();
            String district = tencentLocation.getDistrict();
            String town = tencentLocation.getTown();
            String village = tencentLocation.getVillage();
            String street = tencentLocation.getStreet();
            String streetNo = tencentLocation.getStreetNo();
            String location = city + district + town + village + street + streetNo;
            Toast.makeText(this, location, Toast.LENGTH_SHORT).show();

            Map<String, String> req = new HashMap<>();
            req.put("taskId", id + "");
            req.put("choose", 3 + "");
            volley_Post(getResources().getString(R.string.url_task_choose), req, 1);
            stopLocation();
        } else
        {
            // 网络问题引起的定位失败
            Toast.makeText(this, "定位城市失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 腾讯地图：当GPS, WiFi, Radio 等状态发生变化
     * @param name   设备名, GPS, WIFI, RADIO 中的某个
     * @param status 状态码, STATUS_ENABLED, STATUS_DISABLED, STATUS_UNKNOWN 中的某个
     * @param desc   状态描述
     */
    @Override
    public void onStatusUpdate(String name, int status, String desc)
    {
    }
}

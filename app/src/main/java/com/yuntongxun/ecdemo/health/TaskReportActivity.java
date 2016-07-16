package com.yuntongxun.ecdemo.health;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.health.crop_image.CropImageIntentBuilder;
import com.yuntongxun.ecdemo.health.crop_image.Util;
import com.yuntongxun.ecdemo.health.share.ShareManager;
import com.yuntongxun.ecdemo.health.utils.MediaStoreUtils;
import com.yuntongxun.ecdemo.health.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskReportActivity extends BaseActivity implements View.OnClickListener{
    private ImageView iv_back;
    private ImageView iv_photo01;
    //private ImageView iv_photo02;
    //private ImageView iv_photo03;
    private EditText edt_task_report;
    private Button btn_uploading;

    private String TAG = "TaskReportActivity";
    private ShareManager shareManager;
    private final int pageSize = 100;
    private ArrayAdapter<String> adapter;
    private List<Map<String,Object>> dataList;
    private List<String> photoList;

    private static int CAMERA_REQUEST_CODE_01 = 1_1;
//    private static int CAMERA_REQUEST_CODE_02 = 1_2;
//    private static int CAMERA_REQUEST_CODE_03 = 1_3;

    private static int GALLERY_REQUEST_CODE_01 = 2_1;
//    private static int GALLERY_REQUEST_CODE_02 = 2_2;
//    private static int GALLERY_REQUEST_CODE_03 = 2_3;

    private static int CROP_REQUEST_CODE_01 = 3_1;
//    private static int CROP_REQUEST_CODE_02 = 3_2;
//    private static int CROP_REQUEST_CODE_03 = 3_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_report);
        initView();
        shareManager = new ShareManager(this);
        getClientName();

    }

    /**
     * 获取用户信息
     */
    private void getClientName() {
        photoList = new ArrayList<String>();

        Map<String,String> request = new HashMap<String,String>();
        request.put("employeeId", shareManager.getEmployeeID());
        request.put("size", pageSize + "");
    }

    private void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_photo01 = (ImageView) findViewById(R.id.iv_photo01);
//        iv_photo02 = (ImageView) findViewById(R.id.iv_photo02);
//        iv_photo03 = (ImageView) findViewById(R.id.iv_photo03);
        edt_task_report = (EditText) findViewById(R.id.edt_task_report);
        btn_uploading = (Button) findViewById(R.id.btn_uploading);
        iv_back.setOnClickListener(this);
        iv_photo01.setOnClickListener(this);
//        iv_photo02.setOnClickListener(this);
//        iv_photo03.setOnClickListener(this);
        btn_uploading.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            //返回
            case R.id.iv_back:
                finish();
                break;
            //拍照1
            case R.id.iv_photo01:
                addPhoto(1);
                break;
//            //拍照2
//            case R.id.iv_photo02:
//                addPhoto(2);
//                break;
//            //拍照3
//            case R.id.iv_photo03:
//                addPhoto(3);
//                break;
            //图片上传
            case R.id.btn_uploading:
                Map<String,String> req = new HashMap<String,String>();
                Log.w(TAG,"taskId is " +getIntent().getExtras().getInt("taskID")+"");
                req.put("taskId", getIntent().getExtras().getInt("taskID")+"");
                req.put("explain",edt_task_report.getText().toString());
                req.put("pictures",JSON.toJSONString(photoList));
                volley_Post_taskReport(getString(R.string.url_task_report),req);
        }
    }

    /**
     * 添加要上传图片操作
     * @param photoId
     */
    void addPhoto(final int photoId){
        new AlertDialog.Builder(TaskReportActivity.this)
                .setTitle("添加照片")
                .setItems(new String[]{"拍照", "从相册选择"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //选择拍照
                                if (which == 0) {
                                    if(photoId == 1)
                                    {
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        Uri uri = Uri.fromFile(Utils.getFile(ECApplication.PATH_LOCAL_IMG
                                                + getString(R.string.path_temp_image)));
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                        startActivityForResult(intent, CAMERA_REQUEST_CODE_01);
                                    }
//                                    else if (photoId == 2)
//                                    {
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        Uri uri = Uri.fromFile(Utils.getFile(ECApplication.PATH_LOCAL_IMG
//                                                + getString(R.string.path_temp_image)));
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                                        startActivityForResult(intent, CAMERA_REQUEST_CODE_02);
//                                    }
//                                    else if (photoId == 3)
//                                    {
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        Uri uri = Uri.fromFile(Utils.getFile(ECApplication.PATH_LOCAL_IMG
//                                                + getString(R.string.path_temp_image)));
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                                        startActivityForResult(intent, CAMERA_REQUEST_CODE_03);
//                                    }
                                }
                                //选择从相册选择
                                else if (which == 1) {
                                    if (photoId == 1)
                                    {
                                        startActivityForResult(MediaStoreUtils.getPickImageIntent(TaskReportActivity.this), GALLERY_REQUEST_CODE_01);
                                    }
//                                    else if (photoId == 2)
//                                    {
//                                        startActivityForResult(MediaStoreUtils.getPickImageIntent(TaskReportActivity.this), GALLERY_REQUEST_CODE_02);
//                                    }
//                                    else if (photoId == 3)
//                                    {
//                                        startActivityForResult(MediaStoreUtils.getPickImageIntent(TaskReportActivity.this), GALLERY_REQUEST_CODE_03);
//                                    }
                                }
                            }
                        }).show();
    }


    /**
     * 选择图片返回
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, requestCode + "--" + resultCode);
        //创建剪裁完的图像01
        if (requestCode == CAMERA_REQUEST_CODE_01 || requestCode == GALLERY_REQUEST_CODE_01
                ||requestCode == CROP_REQUEST_CODE_01)
        {
            File croppedImageFile = Utils.getLocalImageFile(ECApplication.PATH_USER_ICON_01);
            Uri croppedImageUri = Uri.fromFile(croppedImageFile);
            //拍照请求的返回处理01
            if (requestCode == CAMERA_REQUEST_CODE_01 && resultCode == RESULT_OK) {
                Log.w(TAG,"camera_request!");
                Uri uri = Uri.fromFile(Utils.getFile(ECApplication.PATH_LOCAL_IMG
                        + getString(R.string.path_temp_image)));
                startImageZoom(uri, croppedImageUri,1);
            }
            //图库请求的返回处理01
            else if (requestCode == GALLERY_REQUEST_CODE_01 && resultCode == RESULT_OK) {
                Log.w(TAG, "gallery_request!");
                //当用户从图库挑选完一张图片，启动 CropImage Activity
                startImageZoom(data.getData(), croppedImageUri,1);
            }
            //裁剪请求的返回处理01
            else if (requestCode == CROP_REQUEST_CODE_01 && resultCode == RESULT_OK) {
                Log.w(TAG, "crop_request!");
                Bitmap bm = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
                iv_photo01.setImageBitmap(bm);
                photoList.add(Utils.getBase64String(Utils.getLocalImageFile(ECApplication.PATH_USER_ICON_01)));
                Utils.deleteLocalTempImage(TaskReportActivity.this);
            }
        }


        //创建剪裁完的图像02
//        else if (requestCode == CAMERA_REQUEST_CODE_02 || requestCode == GALLERY_REQUEST_CODE_02
//                ||requestCode == CROP_REQUEST_CODE_02)
//        {
//            File croppedImageFile = Utils.getLocalImageFile(ECApplication.PATH_USER_ICON_02);
//            Uri croppedImageUri = Uri.fromFile(croppedImageFile);
//            //拍照请求的返回处理02
//            if (requestCode == CAMERA_REQUEST_CODE_02 && resultCode == RESULT_OK) {
//            Log.w(TAG, "camera_request!");
//            Uri uri = Uri.fromFile(Utils.getFile(ECApplication.PATH_LOCAL_IMG
//                    + getString(R.string.path_temp_image)));
//            startImageZoom(uri, croppedImageUri,2);
//            }
//            //图库请求的返回处理02
//            else if (requestCode == GALLERY_REQUEST_CODE_02 && resultCode == RESULT_OK) {
//                Log.w(TAG, "gallery_request!");
//                //当用户从图库挑选完一张图片，启动 CropImage Activity
//                startImageZoom(data.getData(), croppedImageUri,2);
//            }
//            //裁剪请求的返回处理02
//            else if (requestCode == CROP_REQUEST_CODE_02 && resultCode == RESULT_OK) {
//                Log.w(TAG, "crop_request!");
//                Bitmap bm = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
//                iv_photo02.setImageBitmap(bm);
//                photoList.add(Utils.getBase64String(Utils.getLocalImageFile(ECApplication.PATH_USER_ICON_02)));
//                Utils.deleteLocalTempImage(TaskReportActivity.this);
//            }
//        }


        //创建剪裁完的图像03
//        else if (requestCode == CAMERA_REQUEST_CODE_03 || requestCode == GALLERY_REQUEST_CODE_03
//                ||requestCode == CROP_REQUEST_CODE_03) {
//            File croppedImageFile = Utils.getLocalImageFile(ECApplication.PATH_USER_ICON_03);
//            Uri croppedImageUri = Uri.fromFile(croppedImageFile);
//            //拍照请求的返回处理03
//            if (requestCode == CAMERA_REQUEST_CODE_03 && resultCode == RESULT_OK) {
//                Log.w(TAG, "camera_request!");
//                Uri uri = Uri.fromFile(Utils.getFile(ECApplication.PATH_LOCAL_IMG
//                        + getString(R.string.path_temp_image)));
//                startImageZoom(uri, croppedImageUri,3);
//            }
//
//            //图库请求的返回处理03
//            else if (requestCode == GALLERY_REQUEST_CODE_03 && resultCode == RESULT_OK) {
//                Log.w(TAG, "gallery_request!");
//                //当用户从图库挑选完一张图片，启动 CropImage Activity
//                startImageZoom(data.getData(), croppedImageUri,3);
//            }
//
//            //裁剪请求的返回处理03
//            else if (requestCode == CROP_REQUEST_CODE_03 && resultCode == RESULT_OK) {
//                Log.w(TAG, "crop_request!");
//                Bitmap bm = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
//                iv_photo03.setImageBitmap(bm);
//                photoList.add(Utils.getBase64String(Utils.getLocalImageFile(ECApplication.PATH_USER_ICON_03)));
//                Utils.deleteLocalTempImage(TaskReportActivity.this);
//            }
//        }


    }

    /** 启动图片剪裁
     *
     * @param uri 待裁剪图片文件的uri
     * @param croppedImageUri 剪裁完图片文件的uri
     */
    private void startImageZoom(Uri uri, Uri croppedImageUri, int photoId) {

        //设置输出图片文件的Uri，裁剪框设为蓝色，并将裁切出的大小设置为300*300 像素大小的正方形

        CropImageIntentBuilder cropImage = new CropImageIntentBuilder(250, 250, croppedImageUri);
        cropImage.setOutlineColor(0xFF03A9F4);
        cropImage.setSourceImage(uri);
        if (photoId == 1)
        {
            startActivityForResult(cropImage.getIntent(this), CROP_REQUEST_CODE_01);
        }
//        else if (photoId == 2)
//        {
//            startActivityForResult(cropImage.getIntent(this), CROP_REQUEST_CODE_02);
//        }
//        else if (photoId == 3)
//        {
//            startActivityForResult(cropImage.getIntent(this), CROP_REQUEST_CODE_03);
//        }
    }

    /**
     * 上传任务报告
     * @param url_add
     * @param req
     */
    private void volley_Post_taskReport(String url_add, final Map<String,String> req)
    {

        String url = getString(R.string.url_host);
        url = url + url_add;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                //请求成功
                Log.d(TAG, "response!" + s);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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
        request.setTag(TAG);
        ECApplication.getRequestQueue().add(request);
    }

}

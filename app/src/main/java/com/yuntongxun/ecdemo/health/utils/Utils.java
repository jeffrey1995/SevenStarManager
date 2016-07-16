package com.yuntongxun.ecdemo.health.utils;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuntongxun.ecdemo.ECApplication;
import com.yuntongxun.ecdemo.R;
import com.yuntongxun.ecdemo.health.share.ShareManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mrtian on 2016/5/12.
 */
public class Utils {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
    public static Gson getGson()
    {
        return gson;
    }


    /**
     * 获得用户的头像文件
     *
     * @return 用户的头像文件对象，但硬盘上这个文件此时不一定存在
     */
    public static File getLocalImageFile(String path) {
        File tmpDir = new File(ECApplication.PATH_LOCAL_IMG);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        String imgAbsolutePath = tmpDir.getAbsolutePath() + path;
        return new File(imgAbsolutePath);
    }

    /**
     * 获得文件
     *
     * @param absolutePath 绝对路径
     * @return 该路径对应的文件
     */
    public static File getFile(String absolutePath) {
        return new File(absolutePath);
    }

    /**
     * 清除本地临时拍照文件
     */
    public static void deleteLocalTempImage(Context mContext) {
        Utils.getFile(ECApplication .PATH_LOCAL_IMG
                + mContext.getString(R.string.path_temp_image)).delete();
    }


    /**
     * 获得base64格式图片字符串
     *
     * @return 输出base64格式图片字符串
     */
    public static String getBase64String(File ImageFile) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(ImageFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String retStr = Base64.encodeToString(data, Base64.DEFAULT);
        return retStr;
    }
}

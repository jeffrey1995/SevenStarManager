package com.yuntongxun.ecdemo.health.share;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mrtian on 2016/5/11.
 */
public class ShareManager {
    private SharedPreferences share;
    private SharedPreferences.Editor editor;
    private String TAG = "ShareManager";

    public ShareManager(Context context) {
        super();
        share = context.getSharedPreferences(ShareContents.ShareName, Context.MODE_PRIVATE);
        editor = share.edit();
    }

    public void clear()
    {
        editor.clear().commit();
    }

    public String getAccount() {
        return share.getString(ShareContents.account,"");
    }

    public void setAccount(String account) {
        editor.putString(ShareContents.account, account).commit();
    }

    public boolean getIsRemenber() {
        return share.getBoolean(ShareContents.isRemember, false);
    }

    public void setIsRemenber(boolean isRemenber) {
        editor.putBoolean(ShareContents.isRemember, isRemenber).commit();
    }

    public String getPassword() {
        return share.getString(ShareContents.password, "");
    }

    public void setPassword(String password) {
        editor.putString(ShareContents.password, password).commit();
    }

    public String getAccountName() {
        return share.getString(ShareContents.accountName,"");
    }

    public void setAccountName(String accountName) {
        editor.putString(ShareContents.accountName,accountName).commit();
    }

    public String getStarID() {
        return share.getString(ShareContents.starID,"");
    }

    public void setStarID(String starID) {
        editor.putString(ShareContents.starID,starID).commit();
    }

    public String getEmployeeID() {
        return share.getString(ShareContents.employeeID,"");
    }

    public void setEmployeeID(String employeeID) {
        editor.putString(ShareContents.employeeID,employeeID).commit();
    }

    public boolean getIsLogin() {
        return share.getBoolean(ShareContents.isLogin,false);
    }

    public void setIsLogin(boolean isLogin) {
        editor.putBoolean(ShareContents.isLogin,isLogin).commit();
    }
}

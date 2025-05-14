package com.wcl.testdemo.api.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.R;


/**
 * @Author WCL
 * @Date 2025/5/12 16:47
 * @Version
 * @Description 网络请求:错误码分析处理.
 */
public class ApiErrorHelper {

    public String msg;
    public boolean needLogin = false;

    public ApiErrorHelper(String errorCode, String errorMsg) {
        if (!TextUtils.isEmpty(errorCode)) {
            switch (errorCode) {
                case "VC_0006"://token解析错误,需要:弹出提示同时跳转到登录页面.
                    this.msg = Utils.getApp().getString(R.string.err_no_login);
                    needLogin = true;
                    break;
            }
        }
        if (TextUtils.isEmpty(this.msg)) {
            if (errorMsg.contains("Unable to resolve host") || errorMsg.contains("HTTP Request error")) {
                this.msg = Utils.getApp().getString(R.string.err_no_network);
            } else {
                this.msg = (TextUtils.isEmpty(errorMsg) ? Utils.getApp().getString(R.string.err_unknown) : errorMsg) + (TextUtils.isEmpty(errorCode) ? "" : (" (" + errorCode + ")"));
            }
        }
    }

}

package com.wcl.testdemo.api.bean.response;

import com.wcl.testdemo.api.ApiResponse;

/**
 * @Author WCL
 * @Date 2025/5/13 10:16
 * @Version
 * @Description 钛马星[APK升级检测]API响应体.
 */
public class ApkUpgrade extends ApiResponse {

    private boolean returnSuccess;
    private String eTag;

    @Override
    public boolean isSuccess() {
        return returnSuccess;
    }
}
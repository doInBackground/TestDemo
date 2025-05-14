package com.wcl.testdemo.api.bean.response;

import com.wcl.testdemo.api.ApiResponse;

/**
 * @Author WCL
 * @Date 2025/5/13 10:16
 * @Version
 * @Description 钛马星[标记广告位点击或展示]API响应体.
 */
public class MarkResponse extends ApiResponse {

    private boolean returnSuccess;//服务器返回的是否标记成功.

    @Override
    public boolean isSuccess() {
        return returnSuccess;
    }
}
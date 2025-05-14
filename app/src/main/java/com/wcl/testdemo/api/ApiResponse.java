package com.wcl.testdemo.api;


import android.text.TextUtils;

/**
 * @Author WCL
 * @Date 2025/5/12 16:22
 * @Version
 * @Description 网络请求:基础响应类.(成员变量的名字,需要根据服务器固定返回的"根结构"来调整)
 */
public class ApiResponse {
    /**
     * Comment: 后台返回的,本次请求的响应码.("0"=成功;其他=失败)
     */
    public String errCode;
    /**
     * Comment: 后台返回的,本次请求的错误信息.(约定只有当[errCode≠"0"]才有值)
     */
    public String errMsg;

    public boolean isSuccess() {
        return TextUtils.equals("0", errCode);
    }

}

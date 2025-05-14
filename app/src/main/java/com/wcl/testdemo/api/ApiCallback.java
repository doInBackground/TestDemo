package com.wcl.testdemo.api;


import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.api.utils.ApiErrorHelper;

/**
 * @Author WCL
 * @Date 2025/5/12 17:12
 * @Version
 * @Description 网络请求:回调.
 */
public abstract class ApiCallback<T extends ApiResponse> {

    /**
     * 在发送网络请求之前.
     * (可以用来显示加载框)
     */
    public void onStart() {
    }

    /**
     * Api调用已完成,并得到了正确的响应.
     *
     * @param response 响应对象
     */
    public abstract void onSuccess(T response);

    /**
     * Api调用已完成,但收到了错误的响应,例如网络错误、服务器的错误响应等.
     *
     * @param errApiResponse 错误响应对象
     */
    public void onFailed(ApiResponse errApiResponse) {
        ApiErrorHelper help = new ApiErrorHelper(errApiResponse.errCode, errApiResponse.errMsg);
        if (help.needLogin) {
            //清除用户信息.
            //展示登录提示框.
        } else {
            ToastUtils.showShort(help.msg);
        }
    }

    /**
     * Api调用已完全完成.
     * (可以用来关闭加载框)
     */
    public void onStop() {
    }

}

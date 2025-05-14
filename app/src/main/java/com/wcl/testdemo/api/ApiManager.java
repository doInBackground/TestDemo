package com.wcl.testdemo.api;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.BuildConfig;
import com.wcl.testdemo.api.bean.response.ApkUpgrade;
import com.wcl.testdemo.constant.Host;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Author WCL
 * @Date 2025/5/13 17:05
 * @Version
 * @Description 网络请求管理类.
 */
public class ApiManager {

    private static final int TIME_OUT = 15;//网络请求超时时长(单位:秒).
    private static ApiManager mApiManager;//本类的实例对象.
    public ApiService mApiService;//网络请求的接口类对象.

    //私有化构造.
    private ApiManager() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String log) {
                LogUtils.d("HttpLog", log);
            }
        });//网络日志拦截器.
        logInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);//设置网络日志拦截器展示等级.
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Host.HOST_MAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        mApiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取本类的单例单例对象.
     *
     * @return 本类的单例单例对象
     */
    public static ApiManager getInstance() {
        if (mApiManager == null) {
            mApiManager = new ApiManager();
        }
        return mApiManager;
    }

    //执行HTTP请求并处理默认响应.
    private <T extends ApiResponse> void request(Call<T> call, final ApiCallback<T> callback) {
        callback.onStart();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    T apiResponse = response.body();
                    if (apiResponse != null) {
                        if (apiResponse.isSuccess()) {//业务上的成功与否.
                            callback.onSuccess(apiResponse);
                        } else {
                            callback.onFailed(apiResponse);
                        }
                    } else {
                        callback.onFailed(getErrorApiResponse("HTTP RESPONSE ERROR, CODE:" + response.code()));
                    }
                } else {
                    callback.onFailed(getErrorApiResponse("HTTP ERROR CODE:" + response.code()));
                }
                callback.onStop();
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                callback.onFailed(getErrorApiResponse("HTTP Request error!"));
                callback.onStop();
            }
        });
    }

    //通过错误信息创建一个默认的错误响应对象.
    private ApiResponse getErrorApiResponse(String errMsg) {
        ApiResponse rs = new ApiResponse();
        rs.errMsg = errMsg;
        return rs;
    }

    //============================================================接口方法包装============================================================

    /**
     * 钛马星升级检测接口.
     *
     * @param specVersion spec版本
     * @param callback    回调
     */
    public void checkApkUpgrade(String specVersion, ApiCallback<ApkUpgrade> callback) {
        Call<ApkUpgrade> call = mApiService.checkApkUpgrade(specVersion, BuildConfig.DEBUG ? 1 : 2);
        request(call, callback);
    }

}

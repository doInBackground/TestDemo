package com.wcl.testdemo.api;

import com.wcl.testdemo.api.bean.request.MarkRequest;
import com.wcl.testdemo.api.bean.response.ApkUpgrade;
import com.wcl.testdemo.api.bean.response.MarkResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @Author WCL
 * @Date 2025/5/12 16:10
 * @Version
 * @Description 网络请求:接口类.
 */
public interface ApiService {

    /**
     * 测试接口.
     *
     * @return 本次网络请求的基本信息
     */
    @GET("https://httpbin.org/get")
    Call<ResponseBody> test();

    /**
     * 钛马星升级检测接口.
     *
     * @param specVersion spec版本
     * @param apkStatus   0-禁用; 1-内部发布; 2-外部发布;
     * @return 升级信息
     */
    //http://mg2.91carnet.com/carNet/sc/mg/upgradeapp/checkApkUpgrade?specVersion=60&apkStatus=2
    @GET("http://mg2.91carnet.com/carNet/sc/mg/upgradeapp/checkApkUpgrade")
    Call<ApkUpgrade> checkApkUpgrade(@Query("specVersion") String specVersion, @Query("apkStatus") int apkStatus);

    /**
     * 钛马星标记广告位点击或展示.
     *
     * @param markRequest 请求体
     * @return 标记结果
     */
    @Headers({"url_name:user"})
    @POST("http://mg2.91carnet.com/carNet/sc/mg/information/mark")
    Call<MarkResponse> markAd(@Body MarkRequest markRequest);

}

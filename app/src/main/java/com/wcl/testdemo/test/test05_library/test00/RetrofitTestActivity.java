package com.wcl.testdemo.test.test05_library.test00;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.api.ApiCallback;
import com.wcl.testdemo.api.ApiManager;
import com.wcl.testdemo.api.bean.request.MarkRequest;
import com.wcl.testdemo.api.bean.response.ApkUpgrade;
import com.wcl.testdemo.api.bean.response.MarkResponse;
import com.wcl.testdemo.init.BaseActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Author WCL
 * @Date 2023/11/30 17:06
 * @Version
 * @Description Retrofit测试界面.
 */
public class RetrofitTestActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://无参[GET]接口测试-直接调用-原始数据获取.
                ApiManager.getInstance().mApiService.test().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.isSuccessful()) {
                                ResponseBody responseBody = response.body();//通过设置响应对象为"ResponseBody"的方式,来获取服务端原始数据.
                                String content = new String(responseBody.bytes());//服务端原始数据.
                                LogUtils.d("网络请求,响应成功:", content);
                            } else {
                                LogUtils.d("网络请求,响应失败:", response.message());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        LogUtils.d("网络请求,请求失败:", throwable.toString());
                    }
                });
                break;
            case R.id.tv_1://有参[GET]接口测试-直接调用.
                ApiManager.getInstance().mApiService.checkApkUpgrade("60", 2).enqueue(new Callback<ApkUpgrade>() {
                    @Override
                    public void onResponse(Call<ApkUpgrade> call, Response<ApkUpgrade> response) {
                        ApkUpgrade apkUpgrade = response.body();
                        LogUtils.d("网络请求:", GsonUtils.toJson(apkUpgrade));
                    }

                    @Override
                    public void onFailure(Call<ApkUpgrade> call, Throwable throwable) {
                        LogUtils.d("网络请求,请求失败:", throwable.toString());
                    }
                });
                break;
            case R.id.tv_2://有参[GET]接口测试-包装调用.
                ApiManager.getInstance().checkApkUpgrade("60", new ApiCallback<ApkUpgrade>() {
                    @Override
                    public void onSuccess(ApkUpgrade response) {
                        LogUtils.d("网络请求:", GsonUtils.toJson(response));
                    }
                });
                break;
            case R.id.tv_3://有参[POST]接口测试-直接调用.
                ApiManager.getInstance().mApiService.markAd(MarkRequest.click(100000)).enqueue(new Callback<MarkResponse>() {
                    @Override
                    public void onResponse(Call<MarkResponse> call, Response<MarkResponse> response) {
                        MarkResponse apiResponse = response.body();//由于广告ID是乱填的,会收到服务器返回的"调用失败"信息.
                        LogUtils.d("网络请求:", GsonUtils.toJson(apiResponse));
                    }

                    @Override
                    public void onFailure(Call<MarkResponse> call, Throwable throwable) {
                        LogUtils.d("网络请求,请求失败:", throwable.toString());
                    }
                });
                break;
            case R.id.tv_4://
                break;
            case R.id.tv_5://
                break;
            case R.id.tv_6://
                break;
            case R.id.tv_7://
                break;
            case R.id.tv_8://
                break;
            case R.id.tv_9://
                break;
            case R.id.tv_10://
                break;
            case R.id.tv_11://
                break;
            case R.id.tv_12://
                break;
            case R.id.tv_13://
                break;
            case R.id.tv_14://
                break;
            case R.id.tv_15://
                break;
            case R.id.tv_16://
                break;
            case R.id.tv_17://
                break;
            case R.id.tv_18://
                break;
            case R.id.tv_19://
                break;
            case R.id.tv_20://
                break;
        }
    }

}
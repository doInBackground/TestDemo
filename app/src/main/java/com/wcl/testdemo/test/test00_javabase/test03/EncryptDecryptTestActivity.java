package com.wcl.testdemo.test.test00_javabase.test03;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2024/1/10 14:33
 * @Version
 * @Description 加密解密测试界面.
 */
public class EncryptDecryptTestActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    /**
     * Comment:AES加解密的密钥KEY(必须32位).
     * 数字字母:占1位.
     * 汉字:占3位.
     */
    private final String AES_KEY = "wei01234567899876543210123456789";
    /**
     * Comment:加密前-原始数据.
     */
    private final String mAesSrcData = "wcl123456789";
    /**
     * Comment:加密后-加密数据.
     */
    private String mAesResultData = "05D550D8DFBBF14B32247618F7268A2A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_decrypt_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://AES加密.
                test0AesEncrypt();
                break;
            case R.id.tv_1://AES解密.
                test1AesDecrypt();
                break;
            case R.id.tv_2://
                break;
            case R.id.tv_3://
                break;
            case R.id.tv_4:
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

    //AES解密.
    private void test1AesDecrypt() {
        byte[] bytes = EncryptUtils.decryptHexStringAES(mAesResultData, AES_KEY.getBytes(), "AES/ECB/PKCS5Padding", null);
        print(mAesResultData + "\n解密后结果:\n" + new String(bytes));
    }

    //AES加密.
    private void test0AesEncrypt() {
        mAesResultData = EncryptUtils.encryptAES2HexString(mAesSrcData.getBytes(), AES_KEY.getBytes(), "AES/ECB/PKCS5Padding", null);
        print(mAesSrcData + "\n加密后结果:\n" + mAesResultData);
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

}
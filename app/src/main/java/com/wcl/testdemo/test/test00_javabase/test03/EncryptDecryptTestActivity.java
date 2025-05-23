package com.wcl.testdemo.test.test00_javabase.test03;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.wcl.testdemo.init.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2024/1/10 14:33
 * @Version
 * @Description 加密解密测试界面.
 */
public class EncryptDecryptTestActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    /**
     * Comment:解析MD5的按钮.
     */
    @BindView(R.id.tv_2)
    TextView mTv2;
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
    /**
     * Comment:解析文件MD5的文件夹名.
     */
    private final String GET_MD5_DIR_NAME = "GET_MD5_DIR";
    /**
     * Comment:解析文件MD5的文件夹File.
     */
    private final File mGetMd5Dir = new File(PathUtils.getExternalAppDataPath(), GET_MD5_DIR_NAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt_decrypt_test);
        ButterKnife.bind(this);
        boolean hasGetMd5Dir = FileUtils.createOrExistsDir(mGetMd5Dir);//创建解析文件MD5的文件夹.
        if (hasGetMd5Dir) {
            mTv2.setText(mTv2.getText() + "(文件夹路径:" + mGetMd5Dir.getAbsolutePath() + ")");
        } else {
            mTv2.setText(mTv2.getText() + "(文件夹路径创建失败:" + mGetMd5Dir.getAbsolutePath() + ")");
        }
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
            case R.id.tv_2://解析指定文件夹下所有文件的MD5值.
                StringBuilder sb = new StringBuilder();
                List<File> files = FileUtils.listFilesInDir(mGetMd5Dir);
                for (File file : files) {
                    if (FileUtils.isFile(file)) {
                        String md5 = FileUtils.getFileMD5ToString(file);
                        sb.append("文件名: ").append(file.getName()).append("\r\n")
                                .append("MD5: ").append(md5).append("\r\n\r\n");
                    }
                }
                print(sb.toString());
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
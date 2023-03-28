package com.wcl.testdemo.utils;

import android.net.Uri;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    /**
     * Uri转File文件.
     *
     * @param srcUri     uri
     * @param targetFile file
     * @return file
     */
    public static File copyUri2File(Uri srcUri, File targetFile) {
        InputStream is = null;
        try {
            is = Utils.getApp().getContentResolver().openInputStream(srcUri);
//            File file = new File(Utils.getApp().getExternalCacheDir(), "" + System.currentTimeMillis());
//            UtilsBridge.writeFileFromIS(file.getAbsolutePath(), is);
            FileIOUtils.writeFileFromIS(targetFile, is);
            return targetFile;
        } catch (FileNotFoundException e) {
            LogUtils.e("流拷贝出错:", e);
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.os.HandlerThread;
import android.util.Size;

import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

/**
 * @Author WCL
 * @Date 2023/5/18 9:44
 * @Version
 * @Description CameraX工具类.
 */
class CameraXHelper {

    private HandlerThread mHandlerThread;
    private CameraX.LensFacing mCurrentFacing = CameraX.LensFacing.BACK;
    private Preview.OnPreviewOutputUpdateListener mListener;

    public CameraXHelper(LifecycleOwner lifecycleOwner, Preview.OnPreviewOutputUpdateListener listener) {
        this.mListener = listener;
        mHandlerThread = new HandlerThread("Analyze-thread");
        mHandlerThread.start();
        CameraX.bindToLifecycle(lifecycleOwner, getPreView());
    }

    private Preview getPreView() {
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetResolution(new Size(640, 480))//分辨率并不是最终的分辨率，CameraX会自动根据设备的支持情况，结合你的参数，设置一个最为接近的分辨率.
                .setLensFacing(mCurrentFacing) //前置或者后置摄像头.
                .build();
        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(mListener);
        return preview;
    }

}

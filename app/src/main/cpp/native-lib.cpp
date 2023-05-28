#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <pthread.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <iostream>
#include <android/log.h>
#include <android/native_window_jni.h>

using namespace cv;
//检测器的Adapter.
DetectionBasedTracker *tracker = 0;
ANativeWindow *window = 0;

#include <android/log.h>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"David",__VA_ARGS__)

//实例化适配器,方块丢给Adapter. 图像 未知图像 关键点 提供
class CascadeDetectorAdapter : public DetectionBasedTracker::IDetector {
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector) : IDetector(), maniuDetector(detector) {
    }

    void detect(const cv::Mat &image, std::vector<cv::Rect> &objects) {
//        Detector->detectMultiScale(image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
        maniuDetector->detectMultiScale(image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
    }

private:
    CascadeDetectorAdapter();

    //分类器作用是分类.
    cv::Ptr<cv::CascadeClassifier> maniuDetector;
};


//com\wcl\testdemo\test\test06_audio_video\test04_opencv\test00\NativeUtils.java
//com_wcl_testdemo_test_test06_1audio_1video_test04_1opencv_test00_NativeUtils

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test04_1opencv_test00_NativeUtils_init(JNIEnv *env, jobject thiz, jstring model_) {

    const char *model = env->GetStringUTFChars(model_, 0);
    //检测器 CascadeClassifier * cascadeClassifier = new CascadeClassifier(model)

    //CascadeClassifier *cascadeClassifier = new CascadeClassifier(model);
    //智能指针Ptr:自己实现了析构函数,opencv实例化所有对象.
    Ptr<CascadeClassifier> classifier = makePtr<CascadeClassifier>(model);
    Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(classifier);//创建一个检测器.


    Ptr<CascadeClassifier> classifier1 = makePtr<CascadeClassifier>(model);
    Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(classifier1);//创建一个跟踪器.


    DetectionBasedTracker::Parameters detectorParams;
    tracker = new DetectionBasedTracker(mainDetector, trackingDetector, detectorParams);//DetectionBasedTracker相当于RecyclerView.IDetector相当于适配器Adapter.
    tracker->run();
//    CascadeDetectorAdapter *cascadeDetectorAdapter = new CascadeDetectorAdapter(classifier);
    env->ReleaseStringUTFChars(model_, model);
}

static int index = 0;

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test04_1opencv_test00_NativeUtils_postData(JNIEnv *env, jobject thiz, jbyteArray data_, jint w, jint h, jint cameraId) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);
    //    data  数据未知的数据
    //图像的意思
    //    Mat * mat = new Mat(h + h / 2, w, CV_8UC1, data);
    //data   nv21
    Mat src(h + h / 2, w, CV_8UC1, data);
    //颜色格式的转换 nv21->RGBA
    cvtColor(src, src, COLOR_YUV2RGBA_NV21);
    if (cameraId == 1) {//前置摄像头.
        rotate(src, src, ROTATE_90_COUNTERCLOCKWISE);//旋转90度.
        flip(src, src, 1);//镜像
    } else {//后置摄像头
        rotate(src, src, ROTATE_90_CLOCKWISE);//顺时针旋转90度.
    }
    Mat gray;
    //转灰色图
    cvtColor(src, gray, COLOR_RGBA2GRAY);
    //增强对比度 (直方图均衡)
    equalizeHist(gray, gray);
//    char p[100];
//    mkdir("/sdcard/maniu/", 0777);
//    faces   List<Rect>  = faces
    std::vector<Rect> faces;

    tracker->process(gray);
//    检测到的结果  矩形框  人脸
    tracker->getObjects(faces);

//    faces 有数据     意思是   识别 出来了   位置
    for (Rect face: faces) {
//        sprintf(p, "/sdcard/maniu/%d.jpg", index++);
//        Mat m;
//        m = gray(face).clone();
//        resize(m, m, Size(24, 24));
//        imwrite(p, m);

        LOGI(" 识别出 width: %d  height: %d", face.width, face.height);
//        原图
//        Scalar *scalar = new Scalar(0, 0, 255);
//        rectangle(src, face,*scalar);
//   Scalar(0, 0, 255)
        //画矩形,分别指定rbga
        rectangle(src, face, Scalar(255, 0, 255));
    }
//     话一个框框  释放
//    数据画到SurfaceView
    if (window) {
        //设置windows的属性
        // 因为旋转了 所以宽、高需要交换
        //这里使用 cols 和rows 代表 宽、高 就不用关心上面是否旋转了
//        初始化了
//        画面中   window  缓冲区 设置 大小
        ANativeWindow_setBuffersGeometry(window, src.cols, src.rows, WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_Buffer buffer;
        do {
            if (!window) {
                break;
            }
            ANativeWindow_setBuffersGeometry(window, src.cols, src.rows, WINDOW_FORMAT_RGBA_8888);
            ANativeWindow_Buffer buffer;
            if (ANativeWindow_lock(window, &buffer, 0)) {
                ANativeWindow_release(window);
                window = 0;
                break;
            }

            uint8_t *dstData = static_cast<uint8_t *>(buffer.bits);//待显示的缓冲区
            int dstlineSize = buffer.stride * 4;

            uint8_t *srcData = src.data;
            int srclineSize = src.cols * 4;
            for (int i = 0; i < buffer.height; ++i) {
                memcpy(dstData + i * dstlineSize, srcData + i * srclineSize, srclineSize);
            }
            ANativeWindow_unlockAndPost(window);
        } while (0);
    }
    //释放Mat
    //内部采用的 引用计数
    src.release();
    gray.release();
    env->ReleaseByteArrayElements(data_, data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test04_1opencv_test00_NativeUtils_setSurface(JNIEnv *env, jobject thiz, jobject surface) {
    if (window) {
        ANativeWindow_release(window);
        window = 0;
    }
//        渲染surface   --->window   --->window
    window = ANativeWindow_fromSurface(env, surface);
}
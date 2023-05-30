#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "WEI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

//com\wcl\testdemo\utils\MyTestNativeUtils
//com_wcl_testdemo_utils_MyTestNativeUtils

/**
 * 从Native层,获取字符串.
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_wcl_testdemo_utils_MyTestNativeUtils_stringFromJNI(JNIEnv *env, jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

/**
 * 从Native层,测试打印字符串.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_utils_MyTestNativeUtils_testLog(JNIEnv *env, jobject /* this */) {
    LOGD("我是从C++打印的字符串!");
    LOGI("我是从C++打印的字符串!");
    LOGW("我是从C++打印的字符串!");
    LOGE("我是从C++打印的字符串!");
    LOGF("我是从C++打印的字符串!");
}

/**
 * LogD打印日志.
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_utils_MyTestNativeUtils_logD(JNIEnv *env, jobject thiz, jstring msg) {
    const char *chars = env->GetStringUTFChars(msg, NULL);
    if (!chars) {
        return;
    }
    LOGD("%s", chars);
}
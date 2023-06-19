#include <jni.h>
#include <string>
#include <android/log.h>
#include "test/include/test.h"

#define TAG "WEI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

//com\wcl\testdemo\utils\MyTestNativeUtils
//com_wcl_testdemo_utils_MyTestNativeUtils
//JNI方法命名规则:Java_全类名_方法名(注意全类名的'.'用'_'代替,全类名或方法名中若存在'_'则用'_1'代替)

//so加载后,会(动态/静态)注册函数表,键为方法名,值为函数地址.Java层调用时,通过函数名在表中找到对应函数地址再调用.
//静态注册:要求C/C++层的函数名符合某种特定的要求,来将两边联系起来.
//动态注册:不再根据特定路径查找函数的实现.用Java中Native方法的方法签名和Native层中对应的实现函数,来将两边联系起来.(系统中多用此方法)

/**
 * 从Native层,获取字符串.
 */
extern "C" //在C++文件中声明使用C方式定义,否则Java层找不到'Java_全类名_方法名'对应的函数.(C不支持重载,函数名就是"函数名";C++支持重载,函数名是"函数名(参数类型列表)")
JNIEXPORT //宏:"_attribute_((visibility("default")))",表示方法的可见性. default:外部(java)可见. hidden:外部不可见.
jstring //返回值.
JNICALL
Java_com_wcl_testdemo_utils_MyTestNativeUtils_stringFromJNI(
        JNIEnv *env, //"JNIEnv* env"必是JNI函数第一个参数.
        jobject /* this */ //java的native方法:为static时,此参数为"jclass clazz"; 非static时,此参数为"jobject thiz";
) {
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
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_utils_MyTestNativeUtils_logD(JNIEnv *env, jobject thiz, jstring msg) {
    const char *chars = env->GetStringUTFChars(msg, NULL);
    if (!chars) {
        return;
    }
    LOGD("%s", chars);
}

/**
 * 调用测试.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_utils_MyTestNativeUtils_test(JNIEnv *env, jobject thiz) {
    test();
}
#ifndef MYMUSIC_ANDROIDLOG_H
#define MYMUSIC_ANDROIDLOG_H

#include <jni.h>
#include "android/log.h"

#define LOG_DEBUG true
#define TAG "WEI-Native"

extern _JavaVM *javaVM;

//#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
//#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
//#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

#define LOGD(FORMAT, ...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,FORMAT,##__VA_ARGS__) // 定义LOGD类型
#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,TAG , FORMAT,##__VA_ARGS__) // 定义LOGI类型
#define LOGW(FORMAT, ...) __android_log_print(ANDROID_LOG_WARN,TAG , FORMAT,##__VA_ARGS__) // 定义LOGW类型
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,TAG ,FORMAT,##__VA_ARGS__) // 定义LOGE类型
#define LOGF(FORMAT, ...) __android_log_print(ANDROID_LOG_FATAL,TAG ,FORMAT,##__VA_ARGS__) // 定义LOGF类型

#endif //MYMUSIC_ANDROIDLOG_H
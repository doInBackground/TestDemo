//
// Created by WCL on 2023/6/21.
//
#include "AndroidLog.h"
#include <jni.h>
#include <string>

_JavaVM *javaVM = NULL;

extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("JNI_OnLoad()被调用!!!");
    jint result = -1;
    javaVM = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    return JNI_VERSION_1_4;
}

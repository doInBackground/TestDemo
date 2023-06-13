#include "MNCallJava.h"

MNCallJava::MNCallJava(_JavaVM *javaVM, JNIEnv *env, jobject *obj) {

    this->javaVM = javaVM;
    this->jniEnv = env;
    this->jobj = *obj;
    this->jobj = env->NewGlobalRef(jobj);

    jclass jlz = jniEnv->GetObjectClass(jobj);
    if (!jlz) {
        if (LOG_DEBUG) {
            LOGE("get jclass wrong");
        }
        return;
    }

    jmid_prepared = env->GetMethodID(jlz, "onCallPrepared", "()V");
//    ------------新加-----------------
    jmid_timeinfo = env->GetMethodID(jlz, "onCallTimeInfo", "(II)V");


}

MNCallJava::~MNCallJava() {

}

void MNCallJava::onCallPrepared(int type) {
    if (type == MAIN_THREAD) {
        jniEnv->CallVoidMethod(jobj, jmid_prepared);
    } else if (type == CHILD_THREAD) {
        JNIEnv *jniEnv;
        if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            if (LOG_DEBUG) {
                LOGE("get child thread jnienv worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_prepared);
        javaVM->DetachCurrentThread();
    }

}

//回调Java.
void MNCallJava::onCallTimeInfo(int type, int curr, int total) {
    if (type == MAIN_THREAD) {
        jniEnv->CallVoidMethod(jobj, jmid_timeinfo, curr, total);
    } else if (type == CHILD_THREAD) {
        JNIEnv *jniEnv;
        if (javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            if (LOG_DEBUG) {
                LOGE("call onCallTimeInfo worng");
            }
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_timeinfo, curr, total);
        javaVM->DetachCurrentThread();
    }
}
#ifndef MYMUSIC_WLCALLJAVA_H
#define MYMUSIC_WLCALLJAVA_H

#include "jni.h"
#include <linux/stddef.h>
#include "AndroidLog.h"

#define MAIN_THREAD 0
#define CHILD_THREAD 1

class MNCallJava {

public:
    _JavaVM *javaVM = NULL;
    JNIEnv *jniEnv = NULL;
    jobject jobj;
    jmethodID jmid_prepared;
    jmethodID jmid_timeinfo;

public:

    MNCallJava(_JavaVM *javaVM, JNIEnv *env, jobject *obj);

    ~MNCallJava();

    void onCallPrepared(int type);

    void onCallTimeInfo(int type, int curr, int total);
};
#endif //MYMUSIC_WLCALLJAVA_H
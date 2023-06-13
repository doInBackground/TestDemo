#include <jni.h>
#include <string>
#include "MNFFmpeg.h"
#include "MNPlaystatus.h"

extern "C" {
#include <libavformat/avformat.h>
}

//com\wcl\testdemo\test\test06_audio_video\test05_ffmpeg\test03\player\MNPlayer
//com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_MNPlayer

_JavaVM *javaVM = NULL;
MNCallJava *callJava = NULL;
MNFFmpeg *fFmpeg = NULL;
MNPlaystatus *playstatus = NULL;

bool nexit = true;

extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    jint result = -1;
    javaVM = vm;
    JNIEnv *env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {

        return result;
    }
    return JNI_VERSION_1_4;
}

/**
 * FFmpeg:准备.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1prepared(JNIEnv *env, jobject instance, jstring source_) {
    LOGI("JNI: prepare() -> %s", source_);
    const char *source = env->GetStringUTFChars(source_, 0);
    if (fFmpeg == NULL) {
        if (callJava == NULL) {
            callJava = new MNCallJava(javaVM, env, &instance);
        }
        playstatus = new MNPlaystatus();
        fFmpeg = new MNFFmpeg(playstatus, callJava, source);
        fFmpeg->prepared();
    }
}

/**
 * FFmpeg:开始.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1start(JNIEnv *env, jobject instance) {
    LOGI("JNI: start()");
    if (fFmpeg != NULL) {
        fFmpeg->start();
    }
}

/**
 *FFmpeg:插入指定位置播放.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1seek(JNIEnv *env, jobject thiz, jint secds) {
    LOGI("JNI: seek() -> %d  ", secds);
    if (fFmpeg != NULL) {
        fFmpeg->seek(secds);
    }
}

/**
 *OpenSLES:暂停播放.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1pause(JNIEnv *env, jobject thiz) {
    LOGI("JNI: pause()");
    if (fFmpeg != NULL) {
        fFmpeg->pause();
    }
}

/**
 *OpenSLES:继续播放.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1resume(JNIEnv *env, jobject thiz) {
    LOGI("JNI: resume()");
    if (fFmpeg != NULL) {
        fFmpeg->resume();
    }
}

/**
 *OpenSLES:切换声道.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1mute(JNIEnv *env, jobject thiz, jint mute) {
    LOGI("JNI: mut() -> 声道:%d", mute);
    if (fFmpeg != NULL) {
        fFmpeg->setMute(mute);
    }
}

/**
 *OpenSLES:改变音量.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1volume(JNIEnv *env, jobject thiz, jint percent) {
    LOGI("JNI: volume() -> 音量:%d", percent);
    if (fFmpeg != NULL) {
        fFmpeg->setVolume(percent);
    }
}

/**
 *SoundTouch:音频倍速.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1speed(JNIEnv *env, jobject thiz, jfloat speed) {
    LOGI("JNI: speed() -> 倍速:%s", speed);
    if (fFmpeg != NULL) {
        fFmpeg->setSpeed(speed);
    }
}

/**
 *SoundTouch:音频变调.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1pitch(JNIEnv *env, jobject thiz, jfloat pitch) {
    LOGI("JNI: pitch() -> 声调:%s", pitch);
    if (fFmpeg != NULL) {
        fFmpeg->setPitch(pitch);
    }
}

/**
 *停止播放,释放资源.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test03_player_NativePlayer_n_1stop(JNIEnv *env, jobject thiz) {
    LOGI("JNI: stop()");
    if (!nexit) {
        return;
    }
    nexit = false;//正在退出,只调用一次.
    if (fFmpeg != NULL) {
        fFmpeg->release();
        delete (fFmpeg);
        if (callJava != NULL) {
            delete (callJava);
            callJava = NULL;
        }
        if (playstatus != NULL) {
            delete (playstatus);
            playstatus = NULL;
        }
    }
    nexit = true;
}
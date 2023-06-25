#include <jni.h>
#include <string>
#include "MyFFmpeg.h"
#include "MyPlaystatus.h"
#include "../music_player/AndroidLog.h"

extern "C"
{
#include <libavformat/avformat.h>
}

//_JavaVM *javaVM = NULL;
MyCallJava *video_call_java = NULL;
MyFFmpeg *video_ffmpeg = NULL;
MyPlaystatus *video_play_status = NULL;

bool isVideoStopping = false;

//extern "C" JNIEXPORT jint JNICALL
//JNI_OnLoad(JavaVM *vm, void *reserved) {
//    jint result = -1;
//    javaVM = vm;
//    JNIEnv *env;
//    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
//        return result;
//    }
//    return JNI_VERSION_1_4;
//}

//com\wcl\testdemo\test\test06_audio_video\test05_ffmpeg\test04\player\MNPlayer.java
//com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer.java

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1prepared(JNIEnv *env, jobject instance, jstring source_) {
    LOGI("JNI: prepare() -> %s", source_);
    const char *source = env->GetStringUTFChars(source_, 0);
    if (video_ffmpeg == NULL) {
        LOGD("FFmpeg为空.");
        if (video_call_java == NULL) {
            video_call_java = new MyCallJava(javaVM, env, &instance);
        }
        video_play_status = new MyPlaystatus();
        video_ffmpeg = new MyFFmpeg(video_play_status, video_call_java, source);
        video_ffmpeg->parpared();
    } else {
        LOGD("FFmpeg不为空.");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1start(JNIEnv *env, jobject instance) {
    LOGI("JNI: start()");
    if (video_ffmpeg != NULL) {
        video_ffmpeg->start();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1seek(JNIEnv *env, jobject thiz, jint secds) {
    LOGI("JNI: seek() -> %d  ", secds);
    if (video_ffmpeg != NULL) {
        video_ffmpeg->seek(secds);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1pause(JNIEnv *env, jobject thiz) {
    LOGI("JNI: pause()");
    if (video_ffmpeg != NULL) {
        video_ffmpeg->pause();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1resume(JNIEnv *env, jobject thiz) {
    LOGI("JNI: resume()");
    if (video_ffmpeg != NULL) {
        video_ffmpeg->resume();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1mute(JNIEnv *env, jobject thiz, jint mute) {
    LOGI("JNI: mut() -> 声道:%d", mute);
    if (video_ffmpeg != NULL) {
        video_ffmpeg->setMute(mute);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1volume(JNIEnv *env, jobject thiz, jint percent) {
    LOGI("JNI: volume() -> 音量:%d", percent);
    if (video_ffmpeg != NULL) {
        video_ffmpeg->setVolume(percent);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1speed(JNIEnv *env, jobject thiz, jfloat speed) {
    LOGI("JNI: speed() -> 倍速:%s", speed);
    if (video_ffmpeg != NULL) {
        video_ffmpeg->setSpeed(speed);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1pitch(JNIEnv *env, jobject thiz, jfloat pitch) {
    LOGI("JNI: pitch() -> 声调:%s", pitch);
    if (video_ffmpeg != NULL) {
        video_ffmpeg->setPitch(pitch);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test04_player_MNPlayer_n_1stop(JNIEnv *env, jobject thiz) {
    LOGI("JNI: stop()");
    if (isVideoStopping) {//正在退出,只调用一次.
        return;
    }
    isVideoStopping = true;
    if (video_ffmpeg != NULL) {
        video_ffmpeg->release();
        delete (video_ffmpeg);
        video_ffmpeg = NULL;
        if (video_call_java != NULL) {
            delete (video_call_java);
            video_call_java = NULL;
        }
        if (video_play_status != NULL) {
            delete (video_play_status);
            video_play_status = NULL;
        }
    }
    isVideoStopping = false;
}
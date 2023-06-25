#ifndef MYMUSIC_WLAUDIO_H
#define MYMUSIC_WLAUDIO_H

#include "MyQueue.h"
#include "MyPlaystatus.h"
#include "MyCallJava.h"
#include "SoundTouch.h"

extern "C"
{
#include "libavcodec/avcodec.h"
#include <libswresample/swresample.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
}
using namespace soundtouch;

class MyAudio {

public:
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;
    MyQueue *queue = NULL;
    MyPlaystatus *playstatus = NULL;

    pthread_t thread_play;
    AVPacket *avPacket = NULL;
    AVFrame *avFrame = NULL;
    int ret = 0;
    uint8_t *buffer = NULL;
    int data_size = 0;
    int sample_rate = 0;
    float pitch = 1.0f;
    // 引擎接口
    SLObjectItf engineObject = NULL;
    SLEngineItf engineEngine = NULL;

    //混音器
    SLObjectItf outputMixObject = NULL;
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

    //pcm
    SLObjectItf pcmPlayerObject = NULL;
    SLPlayItf pcmPlayerPlay = NULL;
    SLVolumeItf pcmVolumePlay = NULL;
    //缓冲器队列接口
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = NULL;


//    -------------新加的----------------
    int duration = 0;
//时间单位         总时间/帧数   单位时间     *   时间戳= pts  * 总时间/帧数   h264    记录int  位数最小化   单位    帧率
    AVRational time_base;
//当前时间
    double now_time;//当前frame时间

    double clock;//当前播放的时间    准确时间

    MyCallJava *callJava = NULL;
    double last_tiem; //上一次调用时间
//立体声
    int mute = 2;
    SLMuteSoloItf pcmMutePlay = NULL;
    int volumePercent = 100;

//    倍速
    float speed = 1.0f;
    SoundTouch *soundTouch = NULL;
//新的缓冲区
    SAMPLETYPE *sampleBuffer = NULL;
//    入参 出餐
    uint8_t *out_buffer = NULL;
//    波处理完了没
    bool finished = true;

//    新波的实际个数
    int nb = 0;
    int num = 0;
public:
    MyAudio(MyPlaystatus *playstatus, int sample_rate, MyCallJava *callJava);

    ~MyAudio();

    void play();

    int resampleAudio(void **pcmbuf);

    void initOpenSLES();

    int getCurrentSampleRateForOpensles(int sample_rate);

    void onCallTimeInfo(int type, int curr, int total);

    void pause();

    void resume();

    void setMute(int mute);

    void setVolume(int percent);

    void setSpeed(float speed);

    int getSoundTouchData();

    void setPitch(float pitch);

    void release();
};


#endif //MYMUSIC_WLAUDIO_H

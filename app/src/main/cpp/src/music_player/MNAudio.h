#ifndef MYMUSIC_WLAUDIO_H
#define MYMUSIC_WLAUDIO_H

#include "MNQueue.h"
#include "MNPlaystatus.h"
#include "MNCallJava.h"
#include "SoundTouch.h"

extern "C" {
#include "libavcodec/avcodec.h"
#include <libswresample/swresample.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
}
using namespace soundtouch;

class MNAudio {

public:
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;
    MNQueue *queue = NULL;
    MNPlaystatus *playstatus = NULL;

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
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = NULL;//缓冲器队列接口

//    -------------新加的----------------
    int duration = 0;

    AVRational time_base;//单位时间的时长(即:总时间/总帧数) //时间戳 = pts(表示该帧序号) * 总时间 / 总帧数.
//当前时间
    double now_time;//当前frame时间

    double clock;//当前播放的时间    准确时间

    MNCallJava *callJava = NULL;
    double last_tiem; //上一次调用时间
//立体声
    int mute = 2;
    SLMuteSoloItf pcmMutePlay = NULL;
    int volumePercent = 100;

//    倍速
    float speed = 1.0f;
    SoundTouch *soundTouch = NULL;
    SAMPLETYPE *sampleBuffer = NULL;//新的缓冲区.用来存放经过SoundTouch处理后的音频数据.

    uint8_t *out_buffer = NULL;//入参出参指针.

    bool finished = true;//标识波是否处理完了.

//    新波的实际个数
    int nb = 0;
    int num = 0;

public:

    MNAudio(MNPlaystatus *playstatus, int sample_rate, MNCallJava *callJava);

    ~MNAudio();

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

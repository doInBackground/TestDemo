#ifndef FFMPEGMUSICPLAYER_MNVIDEO_H
#define FFMPEGMUSICPLAYER_MNVIDEO_H


#include "MyPlaystatus.h"
#include "MyCallJava.h"
#include "MyQueue.h"
#include "MyAudio.h"
#include "../music_player/AndroidLog.h"


extern "C"
{
#include "libavcodec/avcodec.h"
#include "libavutil/time.h"
#include <libavutil/imgutils.h>
#include <libswscale/swscale.h>
}

class MyVideo {
public:
    MyQueue *queue = NULL;
    int streamIndex = -1;
    AVCodecContext *avCodecContext = NULL;
    AVCodecParameters *codecpar = NULL;
    MyPlaystatus *playstatus = NULL;
    MyCallJava *wlCallJava = NULL;
    pthread_mutex_t codecMutex;
    pthread_t thread_play;
//    -------------------新加--------------
    double clock = 0;//当前播放时间,准确时间.
    double delayTime = 0;//视频帧间休眠时间,通过判断视频与音频播放时间的差值,实时调整出来.
    double defaultDelayTime = 0.04;//默认休眠时间,40ms,0.04s,帧率25帧.
    MyAudio *audio = NULL;//视频类持有音频类的引用,方便以音频为准的音视频同步时,知道音频当前播放时间clock.
    AVRational time_base;
public:
    MyVideo(MyPlaystatus *playstatus, MyCallJava *wlCallJava);

    ~MyVideo();

    void play();

    double getDelayTime(double diff);

    double getFrameDiffTime(AVFrame *avFrame);
};


#endif //FFMPEGMUSICPLAYER_MNVIDEO_H

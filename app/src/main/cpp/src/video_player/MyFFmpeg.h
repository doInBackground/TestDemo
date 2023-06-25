#ifndef MYMUSIC_WLFFMPEG_H
#define MYMUSIC_WLFFMPEG_H

#include "MyCallJava.h"
#include "pthread.h"
#include "MyAudio.h"
#include "MyVideo.h"
#include "MyPlaystatus.h"

extern "C"
{
#include <libavutil/time.h>
#include "libavformat/avformat.h"
};


class MyFFmpeg {

public:
    MyCallJava *callJava = NULL;
    const char *url = NULL;
    pthread_t decodeThread;
    AVFormatContext *pFormatCtx = NULL;
    MyAudio *audio = NULL;
    MyVideo *video = NULL;
    MyPlaystatus *playstatus = NULL;

    int duration = 0;
    pthread_mutex_t seek_mutex;
    pthread_mutex_t init_mutex;
    bool exit = false;
public:
    MyFFmpeg(MyPlaystatus *playstatus, MyCallJava *callJava, const char *url);

    ~MyFFmpeg();

    void parpared();

    void decodeFFmpegThread();

    void start();

    void pause();

    void seek(int64_t secds);

    void resume();

    void setMute(int mute);

    void setVolume(int percent);

    void setSpeed(float speed);

    void setPitch(float pitch);

    void release();

    int getCodecContext(AVCodecParameters *codecpar, AVCodecContext **avCodecContext);

};


#endif //MYMUSIC_WLFFMPEG_H

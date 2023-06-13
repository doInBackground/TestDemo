#ifndef MYMUSIC_WLQUEUE_H
#define MYMUSIC_WLQUEUE_H

#include "queue"
#include "pthread.h"
#include "AndroidLog.h"
#include "MNPlaystatus.h"

extern "C" {
#include "libavcodec/avcodec.h"
}

//FFmpeg从数据源读取到的未解码数据格式为AVPacket,解码后数据格式为AVFrame.
class MNQueue {

public:
    std::queue<AVPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;
    MNPlaystatus *playstatus = NULL;

public:

    MNQueue(MNPlaystatus *playstatus);

    ~MNQueue();

    int putAvpacket(AVPacket *packet);

    int getAvpacket(AVPacket *packet);

    int getQueueSize();

    void clearAvpacket();

};

#endif //MYMUSIC_WLQUEUE_H

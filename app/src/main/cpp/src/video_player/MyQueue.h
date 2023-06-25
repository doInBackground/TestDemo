#ifndef MYMUSIC_WLQUEUE_H
#define MYMUSIC_WLQUEUE_H

#include "queue"
#include "pthread.h"
#include "../music_player/AndroidLog.h"
#include "MyPlaystatus.h"

extern "C"
{
#include "libavcodec/avcodec.h"
}


class MyQueue {

public:
    std::queue<AVPacket *> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;
    MyPlaystatus *playstatus = NULL;

public:

    MyQueue(MyPlaystatus *playstatus);

    ~MyQueue();

    int putAvpacket(AVPacket *packet);

    int getAvpacket(AVPacket *packet);

    int getQueueSize();

    void clearAvpacket();


};


#endif //MYMUSIC_WLQUEUE_H

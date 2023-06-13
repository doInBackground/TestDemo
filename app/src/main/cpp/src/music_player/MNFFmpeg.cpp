#include "MNFFmpeg.h"

MNFFmpeg::MNFFmpeg(MNPlaystatus *playstatus, MNCallJava *callJava, const char *url) {
    this->playstatus = playstatus;
    this->callJava = callJava;
    this->url = url;
    pthread_mutex_init(&seek_mutex, NULL);
    pthread_mutex_init(&init_mutex, NULL);
}

//创建线程时,该函数被当作参数传入,表示回调函数,其中的代码运行在子线程,为C环境.
//参数data为pthread_create()的最后一个参数,通过强转,来解决C环境无法调用C++函数的问题.
void *decodeFFmpeg(void *data) {
    MNFFmpeg *wlFFmpeg = (MNFFmpeg *) data;
    wlFFmpeg->decodeFFmpegThread();
    pthread_exit(&wlFFmpeg->decodeThread);//退出当前线程.
}

void MNFFmpeg::prepared() {
    pthread_create(&decodeThread, NULL, decodeFFmpeg, this);//创建线程.
}

//解码前的初始化准备操作.
void MNFFmpeg::decodeFFmpegThread() {
    pthread_mutex_lock(&init_mutex);
    av_register_all();
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();
    if (avformat_open_input(&pFormatCtx, url, NULL, NULL) != 0) {
        if (LOG_DEBUG) {
            LOGE("can not open url :%s", url);
        }
        return;
    }
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        if (LOG_DEBUG) {
            LOGE("can not find streams from %s", url);
        }
        return;
    }
    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) { //得到音频流
            if (audio == NULL) {
                audio = new MNAudio(playstatus, pFormatCtx->streams[i]->codecpar->sample_rate, callJava);
                audio->streamIndex = i;

                audio->duration = pFormatCtx->duration / AV_TIME_BASE;//音频总时长pFormatCtx->duration,单位微秒需要转换成秒.
                audio->time_base = pFormatCtx->streams[i]->time_base;
                audio->codecpar = pFormatCtx->streams[i]->codecpar;
                duration = audio->duration;
            }
        }
    }

    AVCodec *dec = avcodec_find_decoder(audio->codecpar->codec_id);
    if (!dec) {
        if (LOG_DEBUG) {
            LOGE("can not find decoder");
        }
        return;
    }

    audio->avCodecContext = avcodec_alloc_context3(dec);
    if (!audio->avCodecContext) {
        if (LOG_DEBUG) {
            LOGE("can not alloc new decodecctx");
        }
        return;
    }

    if (avcodec_parameters_to_context(audio->avCodecContext, audio->codecpar) < 0) {
        if (LOG_DEBUG) {
            LOGE("can not fill decodecctx");
        }
        return;
    }

    if (avcodec_open2(audio->avCodecContext, dec, 0) != 0) {
        if (LOG_DEBUG) {
            LOGE("cant not open audio strames");
        }
        return;
    }
    callJava->onCallPrepared(CHILD_THREAD);
    pthread_mutex_unlock(&init_mutex);
}

//开始解码.
void MNFFmpeg::start() {
    if (audio == NULL) {
        if (LOG_DEBUG) {
            LOGE("audio is null");
            return;
        }
    }
    audio->play();

    int count = 0;

    while (playstatus != NULL && !playstatus->exit) {

        if (playstatus->seek) {//seek是耗时操作,seek过程中不解码.
            continue;
        }

        if (audio->queue->getQueueSize() > 40) {//放入队列前要判断队列当前大小.
            continue;
        }

        AVPacket *avPacket = av_packet_alloc();
        if (av_read_frame(pFormatCtx, avPacket) == 0) {//不断的循环取数据.
            //有数据.
            if (avPacket->stream_index == audio->streamIndex) {//是音频流.
                count++;
                if (LOG_DEBUG) {
//                    LOGE("解码第 %d 帧", count);
                }
                audio->queue->putAvpacket(avPacket);//未解码数据放入队列.
            } else {
                av_packet_free(&avPacket);
                av_free(avPacket);
            }
        } else {
            av_packet_free(&avPacket);
            av_free(avPacket);
            while (playstatus != NULL && !playstatus->exit) {
                if (audio->queue->getQueueSize() > 0) {
                    continue;
                } else {
                    playstatus->exit = true;
                    break;
                }
            }
        }
    }

    if (LOG_DEBUG) {
        LOGD("解码完成");
    }
}

void MNFFmpeg::seek(int64_t secds) {
    if (duration <= 0) {
        return;
    }
    if (secds >= 0 && secds <= duration) {
        if (audio != NULL) {
            playstatus->seek = true; //seek为耗时操作,标识现在为seek状态.
            audio->queue->clearAvpacket();//清空之前缓存的队列.
            audio->clock = 0;
            audio->last_tiem = 0;
            pthread_mutex_lock(&seek_mutex);//加锁.
            int64_t rel = secds * AV_TIME_BASE;
            avformat_seek_file(
                    pFormatCtx, //上下文.
                    -1, //流轨道索引(-1表示所有轨道).
                    INT64_MIN,
                    rel, //当前seek的时间戳(单位:微秒).
                    INT64_MAX,
                    0
            );
            pthread_mutex_unlock(&seek_mutex);//解锁.
            playstatus->seek = false;
        }
    }
}

MNFFmpeg::~MNFFmpeg() {
    pthread_mutex_destroy(&seek_mutex);
    pthread_mutex_destroy(&init_mutex);
}

void MNFFmpeg::pause() {
    if (audio != NULL) {
        audio->pause();
    }

}

void MNFFmpeg::resume() {
    if (audio != NULL) {
        audio->resume();
    }
}

void MNFFmpeg::setMute(int mute) {
    if (audio != NULL) {
        audio->setMute(mute);
    }
}

void MNFFmpeg::setVolume(int percent) {
    if (audio != NULL) {
        audio->setVolume(percent);
    }
}

void MNFFmpeg::setSpeed(float speed) {
    if (audio != NULL) {
        audio->setSpeed(speed);
    }

}

void MNFFmpeg::setPitch(float pitch) {
    if (audio != NULL) {
        audio->setPitch(pitch);
    }
}

void MNFFmpeg::release() {
    if (LOG_DEBUG) {
        LOGI("开始释放Ffmpe");
    }
    playstatus->exit = true;
//    队列        stop    exit
    int sleepCount = 0;
    pthread_mutex_lock(&init_mutex);
    while (!exit) {
        if (sleepCount > 1000) {
            exit = true;

        }
        if (LOG_DEBUG) {
            LOGI("wait ffmpeg  exit %d", sleepCount);
        }
        sleepCount++;
        av_usleep(1000 * 10);//暂停10毫秒
    }

    if (audio != NULL) {
        audio->release();
        delete (audio);
        audio = NULL;
    }

    if (LOG_DEBUG) {
        LOGI("释放 封装格式上下文");
    }
    if (pFormatCtx != NULL) {
        avformat_close_input(&pFormatCtx);
        avformat_free_context(pFormatCtx);
        pFormatCtx = NULL;
    }
    if (LOG_DEBUG) {
        LOGI("释放 callJava");
    }
    if (callJava != NULL) {
        callJava = NULL;
    }
    if (LOG_DEBUG) {
        LOGI("释放 playstatus");
    }
    if (playstatus != NULL) {
        playstatus = NULL;
    }
    pthread_mutex_unlock(&init_mutex);
}


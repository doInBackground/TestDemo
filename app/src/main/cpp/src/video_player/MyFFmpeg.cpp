
#include "MyFFmpeg.h"

MyFFmpeg::MyFFmpeg(MyPlaystatus *playstatus, MyCallJava *callJava, const char *url) {
    this->playstatus = playstatus;
    this->callJava = callJava;
    this->url = url;
    pthread_mutex_init(&seek_mutex, NULL);
    pthread_mutex_init(&init_mutex, NULL);
}

void *decodeVideoFFmpeg(void *data) {
    MyFFmpeg *wlFFmpeg = (MyFFmpeg *) data;
    wlFFmpeg->decodeFFmpegThread();
    pthread_exit(&wlFFmpeg->decodeThread);
}

void MyFFmpeg::parpared() {
    pthread_create(&decodeThread, NULL, decodeVideoFFmpeg, this);
}

int MyFFmpeg::getCodecContext(AVCodecParameters *codecpar, AVCodecContext **avCodecContext) {
    AVCodec *dec = avcodec_find_decoder(codecpar->codec_id);
    if (!dec) {
        if (LOG_DEBUG) {
            LOGE("can not find decoder");
        }
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }

    *avCodecContext = avcodec_alloc_context3(dec);
    if (!audio->avCodecContext) {
        if (LOG_DEBUG) {
            LOGE("------------------can not alloc new decodecctx");
        }
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }

    if (avcodec_parameters_to_context(*avCodecContext, codecpar) < 0) {
        if (LOG_DEBUG) {
            LOGE("can not fill decodecctx");
        }
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }

    if (avcodec_open2(*avCodecContext, dec, 0) != 0) {
        if (LOG_DEBUG) {
            LOGE("cant not open audio strames");
        }
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }
    return 0;
}

void MyFFmpeg::decodeFFmpegThread() {
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
        if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO)//得到音频流
        {
            if (audio == NULL) {
                audio = new MyAudio(playstatus, pFormatCtx->streams[i]->codecpar->sample_rate, callJava);
                audio->streamIndex = i;
//                 pFormatCtx->duration  微妙 转换成秒
                audio->duration = pFormatCtx->duration / AV_TIME_BASE;
                audio->time_base = pFormatCtx->streams[i]->time_base;
                audio->codecpar = pFormatCtx->streams[i]->codecpar;
                duration = audio->duration;
            }
        } else if (pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            if (video == NULL) {
                video = new MyVideo(playstatus, callJava);
                video->streamIndex = i;
                video->codecpar = pFormatCtx->streams[i]->codecpar;
//        这个地方进行赋值
                video->time_base = pFormatCtx->streams[i]->time_base;
//                默认  40ms  帧率   25   40ms    读取帧率     25帧    1       50  /2
                int num = pFormatCtx->streams[i]->avg_frame_rate.num;//分子.
                int den = pFormatCtx->streams[i]->avg_frame_rate.den;//分母.
//                25帧    平均下来  40ms     60帧    16,.66ms      3  defaultDelayTime   默认
                if (num != 0 && den != 0) {
                    int fps = num / den;//[25 / 1]
                    video->defaultDelayTime = 1.0 / fps;//秒
                }
            }
        }
    }
    if (audio != NULL) {
        getCodecContext(audio->codecpar, &audio->avCodecContext);
    }
    if (video != NULL) {
        getCodecContext(video->codecpar, &video->avCodecContext);
    }

    callJava->onCallParpared(CHILD_THREAD);
    pthread_mutex_unlock(&init_mutex);
}

void MyFFmpeg::start() {

    if (audio == NULL) {
        if (LOG_DEBUG) {
            LOGE("audio is null");
            return;
        }
    }
    audio->play();
    video->play();
    video->audio = audio;
    int count = 0;

    while (playstatus != NULL && !playstatus->exit) {
//        seek  耗时
        if (playstatus->seek) {
            continue;
        }
//        放入队列
        if (audio->queue->getQueueSize() > 40) {
            continue;
        }

        AVPacket *avPacket = av_packet_alloc();
        if (av_read_frame(pFormatCtx, avPacket) == 0) {
            if (avPacket->stream_index == audio->streamIndex) {
                //解码操作
                count++;
                audio->queue->putAvpacket(avPacket);
            } else if (avPacket->stream_index == video->streamIndex) {
//                LOGE("解码视频第 %d 帧", count);
                video->queue->putAvpacket(avPacket);
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

void MyFFmpeg::seek(int64_t secds) {
    if (duration <= 0) {
        return;
    }
    if (secds >= 0 && secds <= duration) {
        playstatus->seek = true;
        pthread_mutex_lock(&seek_mutex);
        int64_t rel = secds * AV_TIME_BASE;
        LOGE("rel time %d", secds);
        avformat_seek_file(pFormatCtx, -1, INT64_MIN, rel, INT64_MAX, 0);
        //seek时想要保证音视频及时同步,音频和视频的缓存队列都要清空.
        if (audio != NULL) {
            audio->queue->clearAvpacket();
            audio->clock = 0;
            audio->last_tiem = 0;
            avcodec_flush_buffers(audio->avCodecContext);
        }
        if (video != NULL) {
            video->queue->clearAvpacket();
            pthread_mutex_lock(&video->codecMutex);
            avcodec_flush_buffers(video->avCodecContext);
            pthread_mutex_unlock(&video->codecMutex);
        }
        pthread_mutex_unlock(&seek_mutex);
        playstatus->seek = false;
    }


}

MyFFmpeg::~MyFFmpeg() {
    pthread_mutex_destroy(&seek_mutex);
    pthread_mutex_destroy(&init_mutex);
}

void MyFFmpeg::pause() {
    if (audio != NULL) {
        audio->pause();
    }
    playstatus->pause = true;
}

void MyFFmpeg::resume() {
    if (audio != NULL) {
        audio->resume();
    }
    playstatus->pause = false;
}

void MyFFmpeg::setMute(int mute) {
    if (audio != NULL) {
        audio->setMute(mute);
    }
}

void MyFFmpeg::setVolume(int percent) {
    if (audio != NULL) {
        audio->setVolume(percent);
    }
}

void MyFFmpeg::setSpeed(float speed) {
    if (audio != NULL) {
        audio->setSpeed(speed);
    }

}

void MyFFmpeg::setPitch(float pitch) {
    if (audio != NULL) {
        audio->setPitch(pitch);
    }
}

void MyFFmpeg::release() {
    if (LOG_DEBUG) {
        LOGI("开始释放FFmpeg");
    }
    playstatus->exit = true;
//    队列        stop    exit
    int sleepCount = 0;
    pthread_mutex_lock(&init_mutex);
    while (!exit) {
        if (sleepCount > 30) {
            exit = true;
        }
        if (LOG_DEBUG) {
            LOGE("wait ffmpeg  exit %d", sleepCount);
        }
        sleepCount++;
        av_usleep(1000 * 100);//暂停100毫秒
    }

    if (audio != NULL) {
        audio->release();
        delete (audio);
        audio = NULL;
    }

    if (LOG_DEBUG) {
        LOGE("释放 封装格式上下文");
    }
    if (pFormatCtx != NULL) {
        avformat_close_input(&pFormatCtx);
        avformat_free_context(pFormatCtx);
        pFormatCtx = NULL;
    }
    if (LOG_DEBUG) {
        LOGE("释放 callJava");
    }
    if (callJava != NULL) {
        callJava = NULL;
    }
    if (LOG_DEBUG) {
        LOGE("释放 playstatus");
    }
    if (playstatus != NULL) {
        playstatus = NULL;
    }
    pthread_mutex_unlock(&init_mutex);
}


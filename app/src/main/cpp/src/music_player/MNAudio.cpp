#include "MNAudio.h"

MNAudio::MNAudio(MNPlaystatus *playstatus, int sample_rate, MNCallJava *callJava) {
    this->playstatus = playstatus;
    this->sample_rate = sample_rate;
    queue = new MNQueue(playstatus);
    buffer = (uint8_t *) av_malloc(sample_rate * 2 * 2);//AvPacket(未解码数据)转AvFrame(解码数据)时的缓冲区.
    this->callJava = callJava;
    sampleBuffer = static_cast<SAMPLETYPE *>(malloc(sample_rate * 2 * 2));//申请大小与buffer相同.
    //初始化SoundTouch.
    soundTouch = new SoundTouch();
    soundTouch->setSampleRate(sample_rate);//采样频率.
    soundTouch->setChannels(2);//声道数.
    soundTouch->setTempo(speed);//速度.(1.1, 1.5, 2.1)
    soundTouch->setPitch(pitch);//音调.
}

MNAudio::~MNAudio() {

}

void *decodPlay(void *data) {
    MNAudio *wlAudio = (MNAudio *) data;
    wlAudio->initOpenSLES();
    pthread_exit(&wlAudio->thread_play);
}

void MNAudio::play() {
    pthread_create(&thread_play, NULL, decodPlay, this);
}

int MNAudio::resampleAudio(void **pcmbuf) {

    while (playstatus != NULL && !playstatus->exit) {
        avPacket = av_packet_alloc();
        if (queue->getAvpacket(avPacket) != 0) {
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        ret = avcodec_send_packet(avCodecContext, avPacket);
        if (ret != 0) {
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        avFrame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, avFrame);
        if (ret == 0) {

            if (avFrame->channels && avFrame->channel_layout == 0) {
                avFrame->channel_layout = av_get_default_channel_layout(avFrame->channels);
            } else if (avFrame->channels == 0 && avFrame->channel_layout > 0) {
                avFrame->channels = av_get_channel_layout_nb_channels(avFrame->channel_layout);
            }

            SwrContext *swr_ctx;

            swr_ctx = swr_alloc_set_opts(
                    NULL,
                    AV_CH_LAYOUT_STEREO,
                    AV_SAMPLE_FMT_S16,
                    avFrame->sample_rate,
                    avFrame->channel_layout,
                    (AVSampleFormat) avFrame->format,
                    avFrame->sample_rate,
                    NULL, NULL
            );
            if (!swr_ctx || swr_init(swr_ctx) < 0) {
                av_packet_free(&avPacket);
                av_free(avPacket);
                avPacket = NULL;
                av_frame_free(&avFrame);
                av_free(avFrame);
                avFrame = NULL;
                swr_free(&swr_ctx);
                continue;
            }

            nb = swr_convert(
                    swr_ctx,
                    &buffer,
                    avFrame->nb_samples,
                    (const uint8_t **) avFrame->data,
                    avFrame->nb_samples
            );
//            LOGI("nb------>%d", nb);
            int out_channels = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
            data_size = nb * out_channels * av_get_bytes_per_sample(AV_SAMPLE_FMT_S16);

            //计算时间: pts表示该帧的序号.
//            now_time = avFrame->pts *  time_base.num / (double) time_base.den;//计算当前帧时间(方式一).
            now_time = avFrame->pts * av_q2d(time_base);//计算当前帧时间(方式二).
            if (now_time < clock) {
                now_time = clock;
            }
            clock = now_time;
//
            *pcmbuf = buffer;//解码后的pcm数据,记录在入参中.
//            if (LOG_DEBUG) {
//                LOGI("data_size is %d", data_size);
//            }
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            av_frame_free(&avFrame);
            av_free(avFrame);
            avFrame = NULL;
            swr_free(&swr_ctx);
            break;
        } else {
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            av_frame_free(&avFrame);
            av_free(avFrame);
            avFrame = NULL;
            continue;
        }
    }
    return data_size;
}

//喇叭播放需要数据,会不断调用该方法取数据才能播放.
//生产者消费者模型:喇叭是消费者,驱动了数据的不断生产.
void pcmBufferCallBack(SLAndroidSimpleBufferQueueItf bf, void *context) {
//    LOGI("-------->打印 ");
    MNAudio *wlAudio = (MNAudio *) context;
    if (wlAudio != NULL) {

//        int buffersize = wlAudio->resampleAudio();
        int buffersize = wlAudio->getSoundTouchData();
//        buffersize  25       sountouch       opensles   100
        if (buffersize > 0) {
            wlAudio->clock += buffersize / ((double) (wlAudio->sample_rate * 2 * 2));
//            100*1000字节
            if (wlAudio->clock - wlAudio->last_tiem >= 0.1) {//pcmBufferCallBac()回调次数比较频繁,通过反射调用Java比较耗性能,所以此处控制一下调用频率.
                wlAudio->last_tiem = wlAudio->clock;
                wlAudio->callJava->onCallTimeInfo(CHILD_THREAD, wlAudio->clock, wlAudio->duration);//回调Java.
            }
            (*wlAudio->pcmBufferQueue)->Enqueue(wlAudio->pcmBufferQueue, (char *) wlAudio->sampleBuffer, buffersize * 2 * 2);
        }
    }
}

//初始化OpenSLES.
void MNAudio::initOpenSLES() {

    SLresult result;
    result = slCreateEngine(&engineObject, 0, 0, 0, 0, 0);
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);

    //第二步，创建混音器
    const SLInterfaceID mids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean mreq[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, mids, mreq);
    (void) result;
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    (void) result;
    result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB, &outputMixEnvironmentalReverb);
    if (SL_RESULT_SUCCESS == result) {
        result = (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(outputMixEnvironmentalReverb, &reverbSettings);
        (void) result;
    }
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, 0};


    // 第三步，配置PCM格式信息
    SLDataLocator_AndroidSimpleBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE, 2};

    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,//播放pcm格式的数据
            2,//2个声道（立体声）
            static_cast<SLuint32>(getCurrentSampleRateForOpensles(sample_rate)),//44100hz的频率
            SL_PCMSAMPLEFORMAT_FIXED_16,//位数 16位
            SL_PCMSAMPLEFORMAT_FIXED_16,//和位数一致就行
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//立体声（前左前右）
            SL_BYTEORDER_LITTLEENDIAN//结束标志
    };
    SLDataSource slDataSource = {&android_queue, &pcm};

    const SLInterfaceID ids[3] = {SL_IID_BUFFERQUEUE, SL_IID_VOLUME, SL_IID_MUTESOLO};
    const SLboolean req[3] = {SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE, SL_BOOLEAN_TRUE};

    (*engineEngine)->CreateAudioPlayer(engineEngine, &pcmPlayerObject, &slDataSource, &audioSnk, 2, ids, req);
    (*pcmPlayerObject)->Realize(pcmPlayerObject, SL_BOOLEAN_FALSE);//初始化播放器.

    //得到接口后才能调用其方法.
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_PLAY, &pcmPlayerPlay);//获取Player接口.//控制播放暂停恢复的句柄.
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_MUTESOLO, &pcmMutePlay);//获取声道操作接口.
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_VOLUME, &pcmVolumePlay);//获取音量操作接口.
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_BUFFERQUEUE, &pcmBufferQueue);//注册回调缓冲区,获取缓冲队列接口.

    //缓冲接口回调
    (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmBufferCallBack, this);
//    获取播放状态接口
    (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);
    pcmBufferCallBack(pcmBufferQueue, this);
}

int MNAudio::getCurrentSampleRateForOpensles(int sample_rate) {
    int rate = 0;
    switch (sample_rate) {
        case 8000:
            rate = SL_SAMPLINGRATE_8;
            break;
        case 11025:
            rate = SL_SAMPLINGRATE_11_025;
            break;
        case 12000:
            rate = SL_SAMPLINGRATE_12;
            break;
        case 16000:
            rate = SL_SAMPLINGRATE_16;
            break;
        case 22050:
            rate = SL_SAMPLINGRATE_22_05;
            break;
        case 24000:
            rate = SL_SAMPLINGRATE_24;
            break;
        case 32000:
            rate = SL_SAMPLINGRATE_32;
            break;
        case 44100:
            rate = SL_SAMPLINGRATE_44_1;
            break;
        case 48000:
            rate = SL_SAMPLINGRATE_48;
            break;
        case 64000:
            rate = SL_SAMPLINGRATE_64;
            break;
        case 88200:
            rate = SL_SAMPLINGRATE_88_2;
            break;
        case 96000:
            rate = SL_SAMPLINGRATE_96;
            break;
        case 192000:
            rate = SL_SAMPLINGRATE_192;
            break;
        default:
            rate = SL_SAMPLINGRATE_44_1;
    }
    return rate;
}

void MNAudio::pause() {
    if (pcmPlayerPlay != NULL) {
        (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PAUSED);
    }
}

void MNAudio::resume() {
    if (pcmPlayerPlay != NULL) {
        (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);
    }
}

//设置引擎音量.
void MNAudio::setVolume(int percent) {
    if (pcmVolumePlay != NULL) {
        if (percent > 30) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -20);
        } else if (percent > 25) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -22);
        } else if (percent > 20) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -25);
        } else if (percent > 15) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -28);
        } else if (percent > 10) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -30);
        } else if (percent > 5) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -34);
        } else if (percent > 3) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -37);
        } else if (percent > 0) {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -40);
        } else {
            (*pcmVolumePlay)->SetVolumeLevel(pcmVolumePlay, (100 - percent) * -100);
        }
    }
}

//切换声道.
void MNAudio::setMute(int mute) {
    LOGI("操作声道的OpenSLES接口:%p", pcmMutePlay);
    if (pcmMutePlay == NULL) {
        return;
    }
    this->mute = mute;
    if (mute == 0) {//right
        (*pcmMutePlay)->SetChannelMute(pcmMutePlay, 1, false);
        (*pcmMutePlay)->SetChannelMute(pcmMutePlay, 0, true);
    } else if (mute == 1) {//left
        (*pcmMutePlay)->SetChannelMute(pcmMutePlay, 1, true);
        (*pcmMutePlay)->SetChannelMute(pcmMutePlay, 0, false);
    } else if (mute == 2) {//center
        (*pcmMutePlay)->SetChannelMute(pcmMutePlay, 1, false);//打开.
        (*pcmMutePlay)->SetChannelMute(pcmMutePlay, 0, false);//打开.
    }
}

void MNAudio::setSpeed(float speed) {
    this->speed = speed;
    if (soundTouch != NULL) {
        soundTouch->setTempo(speed);
    }

}

int MNAudio::getSoundTouchData() {
//        重新整理波形  sountouch
    while (playstatus != NULL && !playstatus->exit) {
//        LOGI("------------------循环---------------------------finished %d", finished);
        out_buffer = NULL;
        if (finished) {
            finished = false;
//            从网络流 文件 读取数据 out_buffer  字节数量  out_buffer   是一个旧波
            data_size = this->resampleAudio(reinterpret_cast<void **>(&out_buffer));//方法执行完后pcm数据就在out_buffer中.
            if (data_size > 0) {
                for (int i = 0; i < data_size / 2 + 1; i++) {//循环进行数据赋值.
                    //sampleBuffer是short类型占2字节. out_buffer是char类型占1字节.
                    sampleBuffer[i] = (out_buffer[i * 2] | ((out_buffer[i * 2 + 1]) << 8));//偶数位字节不动,其后奇数位字节左移8位,最后或操作合并.
                }
                soundTouch->putSamples(sampleBuffer, nb);//丢给SoundTouch进行波的整理.
                num = soundTouch->receiveSamples(sampleBuffer, data_size / 4);//接受一个新波sampleBuffer.
//                LOGI("------------第一个num %d ", num);
            } else {
                soundTouch->flush();
            }
        }

        if (num == 0) {
            finished = true;
            continue;
        } else {
            if (out_buffer == NULL) {
                num = soundTouch->receiveSamples(sampleBuffer, data_size / 4);
//                LOGI("------------第二个num %d ", num);
                if (num == 0) {
                    finished = true;
                    continue;
                }
            }
//            LOGI("---------------- 结束1 -----------------------");
            return num;
        }
    }
    LOGI("---------------- 结束2 -----------------------");
    return 0;
}

void MNAudio::setPitch(float pitch) {
    this->pitch = pitch;
    if (soundTouch != NULL) {
        soundTouch->setPitch(pitch);
    }
}

void MNAudio::release() {
//    队列
    if (queue != NULL) {
        delete (queue);
        queue = NULL;
    }
    if (pcmPlayerObject != NULL) {
        (*pcmPlayerObject)->Destroy(pcmPlayerObject);
        pcmPlayerObject = NULL;
        pcmPlayerPlay = NULL;
        pcmBufferQueue = NULL;
    }

    if (outputMixObject != NULL) {
        (*outputMixObject)->Destroy(outputMixObject);
        outputMixObject = NULL;
        outputMixEnvironmentalReverb = NULL;
    }

    if (engineObject != NULL) {
        (*engineObject)->Destroy(engineObject);
        engineObject = NULL;
        engineEngine = NULL;
    }

    if (buffer != NULL) {
        free(buffer);
        buffer = NULL;
    }

    if (avCodecContext != NULL) {
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }

    if (playstatus != NULL) {
        playstatus = NULL;
    }
    if (callJava != NULL) {
        callJava = NULL;
    }

}

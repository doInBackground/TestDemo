//
// Created by maniu on 2021/4/18.
//

#include "MyVideo.h"

MyVideo::MyVideo(MyPlaystatus *playstatus, MyCallJava *wlCallJava) {
    this->playstatus = playstatus;
    this->wlCallJava = wlCallJava;
    queue = new MyQueue(playstatus);
    pthread_mutex_init(&codecMutex, NULL);
}

void *playVideo(void *data) {
//    C函数 1   C++函数2
    MyVideo *video = static_cast<MyVideo *>(data);
//    死循环轮训
    while (video->playstatus != NULL && !video->playstatus->exit) {
//         解码 seek   puase   队列没有数据
        if (video->playstatus->seek) {
            av_usleep(1000 * 100);
            continue;
        }
        if (video->playstatus->pause) {
            av_usleep(1000 * 100);
            continue;
        }
        if (video->queue->getQueueSize() == 0) {
//            网络不佳  请慢慢等待  回调应用层
            if (!video->playstatus->load) {
                video->playstatus->load = true;
                video->wlCallJava->onCallLoad(CHILD_THREAD, true);
                av_usleep(1000 * 100);
                continue;
            }

        }

        AVPacket *avPacket = av_packet_alloc();
        if (video->queue->getAvpacket(avPacket) != 0) {
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
//        视频解码 比较耗时  多线程环境
        pthread_mutex_lock(&video->codecMutex);
//解码操作
        if (avcodec_send_packet(video->avCodecContext, avPacket) != 0) {
//            括号就失败了
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            pthread_mutex_unlock(&video->codecMutex);
            continue;
        }
        AVFrame *avFrame = av_frame_alloc();

        if (avcodec_receive_frame(video->avCodecContext, avFrame) != 0) {
//          括号就失败了
            av_frame_free(&avFrame);
            av_free(avFrame);
            avFrame = NULL;
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            pthread_mutex_unlock(&video->codecMutex);
            continue;
        }
//        此时解码成功了  如果 之前是yuv420  ----》   opengl
        if (avFrame->format == AV_PIX_FMT_YUV420P) {
//            压缩1  原始数据2
//            avFrame->data[0];//y
//            avFrame->data[1];//u
//            avFrame->data[2];//v
//            直接转换   yuv420     ---> yuv420
//其他格式 --yuv420
//休眠33ms  不可取33 * 1000
//计算  音频 视频
//            av_usleep(33 * 1000);


            double diff = video->getFrameDiffTime(avFrame);//音视频时间差.
//            通过diff 计算休眠时间
            av_usleep(video->getDelayTime(diff) * 1000000);
            video->wlCallJava->onCallRenderYUV(
                    video->avCodecContext->width,
                    video->avCodecContext->height,
                    avFrame->data[0],
                    avFrame->data[1],
                    avFrame->data[2]);
//            LOGE("当前视频是YUV420P格式");
        } else {
//            LOGE("当前视频不是YUV420P格式");
            AVFrame *pFrameYUV420P = av_frame_alloc();
            int num = av_image_get_buffer_size(
                    AV_PIX_FMT_YUV420P,
                    video->avCodecContext->width,
                    video->avCodecContext->height,
                    1);
            uint8_t *buffer = static_cast<uint8_t *>(av_malloc(num * sizeof(uint8_t)));
            av_image_fill_arrays(
                    pFrameYUV420P->data,
                    pFrameYUV420P->linesize,
                    buffer,
                    AV_PIX_FMT_YUV420P,
                    video->avCodecContext->width,
                    video->avCodecContext->height,
                    1);
            SwsContext *sws_ctx = sws_getContext(
                    video->avCodecContext->width,
                    video->avCodecContext->height,
                    video->avCodecContext->pix_fmt,
                    video->avCodecContext->width,
                    video->avCodecContext->height,
                    AV_PIX_FMT_YUV420P,
                    SWS_BICUBIC, NULL, NULL, NULL);

            if (!sws_ctx) {
                av_frame_free(&pFrameYUV420P);
                av_free(pFrameYUV420P);
                av_free(buffer);
                pthread_mutex_unlock(&video->codecMutex);
                continue;
            }
            sws_scale(
                    sws_ctx,
                    reinterpret_cast<const uint8_t *const *>(avFrame->data),
                    avFrame->linesize,
                    0,
                    avFrame->height,
                    pFrameYUV420P->data,
                    pFrameYUV420P->linesize);
            //渲染
            video->wlCallJava->onCallRenderYUV(
                    video->avCodecContext->width,
                    video->avCodecContext->height,
                    pFrameYUV420P->data[0],
                    pFrameYUV420P->data[1],
                    pFrameYUV420P->data[2]);

            av_frame_free(&pFrameYUV420P);
            av_free(pFrameYUV420P);
            av_free(buffer);
            sws_freeContext(sws_ctx);
        }
        av_frame_free(&avFrame);
        av_free(avFrame);
        avFrame = NULL;
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;
        pthread_mutex_unlock(&video->codecMutex);
    }
    pthread_exit(&video->thread_play);
}

void MyVideo::play() {
//    子线程播放   解码
    pthread_create(&thread_play, NULL, playVideo, this);
}

//获取当前音频时间和当前视频时间之间的差距.
double MyVideo::getFrameDiffTime(AVFrame *avFrame) {
    //获取视频时间戳.
    double pts = av_frame_get_best_effort_timestamp(avFrame);
    if (pts == AV_NOPTS_VALUE) {
        pts = 0;
    }
//     1.001*40ms
    pts *= av_q2d(time_base);//等价于: pts = pts * time_base.num / time_base.den;//当前时间=当前索引*单位时间.

    if (pts > 0) {
        clock = pts;
    }

    double diff = audio->clock - clock;//音视频时间差=音频当前时间-视频当前时间.
//    LOGD("当前音频时间:%f\n当前视频时间:%f\n当前音视频时间差:%f\n", audio->clock, clock, diff);
    return diff;//大于0表示视频落后,小于0表示视频超前.(单位:秒)
}

//得到视频帧间的动态延时时长(不再是之前测试用例的固定33ms),即每帧之间手动休眠时间.
double MyVideo::getDelayTime(double diff) {//diff:音视频时间差(单位:秒).
//    LOGD("音视频时间差:%f", diff);

    //音视频差距太大.
    if (diff >= 5) {//音频太快,视频怎么也赶不上:视频队列全部清空,直接解析最新的.
        queue->clearAvpacket();
        delayTime = defaultDelayTime;
        return delayTime;
    } else if (diff <= -5) {//视频太快,音频赶不上:音频队列全部清空.
        audio->queue->clearAvpacket();
        delayTime = defaultDelayTime;
        return delayTime;
    }

    //音视频相差500ms以上,稍微过度处理下视频帧间休眠时长.(可能能感觉到视频变速)
    if (diff >= 0.5) {//音频快,视频慢.
        delayTime = 0;
        return delayTime;
    } else if (diff <= -0.5) {//音频慢,视频快
        delayTime = defaultDelayTime * 2;
        return delayTime;
    }

    //音视频相差3ms以上,但相差不多时,在用户不太能感觉到的情况下,稍微调整视频帧间休眠时长.
    if (diff > 0.003) {//音频超越视频3ms.
        delayTime = delayTime * 2 / 3;//减少视频帧间休眠时长.
        //防止计算出来的休眠时长与默认休眠时长差距太离谱,使用户明显察觉到,故限制最大最小值.
        if (delayTime < defaultDelayTime / 2) {
            delayTime = defaultDelayTime * 2 / 3;
        } else if (delayTime > defaultDelayTime * 2) {
            delayTime = defaultDelayTime * 2;
        }
        return delayTime;
    } else if (diff < -0.003) {//视频超越音频3ms.
        delayTime = delayTime * 3 / 2;//增加视频帧间休眠时长.
        if (delayTime < defaultDelayTime / 2) {
            delayTime = defaultDelayTime * 2 / 3;
        } else if (delayTime > defaultDelayTime * 2) {
            delayTime = defaultDelayTime * 2;
        }
        return delayTime;
    }

    //音视频相差3ms以内不处理.

    return defaultDelayTime;//单位:秒.
}

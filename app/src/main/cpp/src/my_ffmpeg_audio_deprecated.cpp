#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
#include "ffmpeg/libavcodec/avcodec.h"
#include "ffmpeg/libavformat/avformat.h"
#include "ffmpeg/libswscale/swscale.h"
#include "ffmpeg/libavutil/imgutils.h"
#include "ffmpeg/libswresample/swresample.h"
}

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "WEI-Native", __VA_ARGS__)

/**
 * 播发指定路径的音频(存在调用过时方法).
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test02_PlayAudioActivity_playSoundDeprecated(JNIEnv *env, jobject instance, jstring input_) {
    const char *input = env->GetStringUTFChars(input_, 0);
    av_register_all();
    avformat_network_init();
    AVFormatContext *pFormatCtx = avformat_alloc_context();
    if (avformat_open_input(&pFormatCtx, input, NULL, NULL) != 0) {
        LOGE("%s","打开输入视频文件失败!");
        return;
    }
    if(avformat_find_stream_info(pFormatCtx,NULL) < 0){
        LOGE("%s","获取视频信息失败!");
        return;
    }
    int audio_stream_idx=-1;
    int i=0;
    for (int i = 0; i < pFormatCtx->nb_streams; ++i) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            LOGE("找到音频id %d", pFormatCtx->streams[i]->codec->codec_type);
            audio_stream_idx=i;
            break;
        }
    }
    AVCodecContext *pCodecCtx= pFormatCtx->streams[audio_stream_idx]->codec;
    AVCodec *pCodex = avcodec_find_decoder(pCodecCtx->codec_id);
    if (avcodec_open2(pCodecCtx, pCodex, NULL)<0) {
        return;
    }
    AVPacket *packet = (AVPacket *)av_malloc(sizeof(AVPacket));
    AVFrame *frame = av_frame_alloc();
    int out_channels_nb = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);
    SwrContext *swrContext = swr_alloc();
    uint64_t  out_ch_layout=AV_CH_LAYOUT_STEREO;
    enum AVSampleFormat out_format=AV_SAMPLE_FMT_S16;
    int out_sample_rate = pCodecCtx->sample_rate;
    swr_alloc_set_opts(swrContext, out_ch_layout, out_format, out_sample_rate, pCodecCtx->channel_layout, pCodecCtx->sample_fmt, pCodecCtx->sample_rate, 0, NULL);
    swr_init(swrContext);
    uint8_t *out_buffer = (uint8_t *) av_malloc(44100 * 2);

    jclass david_player = env->GetObjectClass(instance);
    jmethodID createAudio = env->GetMethodID(david_player, "createTrack", "(II)V");
    env->CallVoidMethod(instance, createAudio, 44100, out_channels_nb);
    jmethodID audio_write = env->GetMethodID(david_player, "playTrack", "([BI)V");
    int got_frame;
    while (av_read_frame(pFormatCtx, packet) >= 0) {
        if (packet->stream_index == audio_stream_idx) {
            avcodec_decode_audio4(pCodecCtx, frame, &got_frame, packet);//过时的解码方式.
            if (got_frame>=0) {
                swr_convert(swrContext, &out_buffer, 44100 * 2,(const uint8_t **)(frame->data), frame->nb_samples);
                int size = av_samples_get_buffer_size(NULL, out_channels_nb, frame->nb_samples, AV_SAMPLE_FMT_S16, 1);
                jbyteArray audio_sample_array = env->NewByteArray(size);
                env->SetByteArrayRegion(audio_sample_array, 0, size,reinterpret_cast<const jbyte *>(out_buffer));
                env->CallVoidMethod(instance, audio_write, audio_sample_array, size);
                env->DeleteLocalRef(audio_sample_array);
            }
        }
    }
    av_frame_free(&frame);
    av_free(packet);
    swr_free(&swrContext);
    avcodec_close(pCodecCtx);
    avformat_close_input(&pFormatCtx);
    env->ReleaseStringUTFChars(input_, input);
}
#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "libavutil/imgutils.h"
#include "libswresample/swresample.h"
}

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, "WEI-Native", __VA_ARGS__)

//com\wcl\testdemo\test\test06_audio_video\test05_ffmpeg\test02\PlayAudioActivity
//com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test02_PlayAudioActivity

/**
 * 播发指定路径的音频.
 */
extern "C" JNIEXPORT void JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test02_PlayAudioActivity_playSound(JNIEnv *env, jobject instance, jstring input_) {

    const char *input = env->GetStringUTFChars(input_, 0);//音频路径字符串,变成C++字符串.

    av_register_all();//注册FFmpeg所有的组件.
    avformat_network_init();//初始化FFmpeg网络模块,使得可以播放网络音频.
    AVFormatContext *pFormatCtx = avformat_alloc_context();//实例化FFmpeg上下文.

    //打开音视频文件或音视频流url.
    if (avformat_open_input(&pFormatCtx, input, NULL, NULL) != 0) {
        LOGE("%s", "播放音频时,打开文件失败!");
        return;
    }

    //查找文件的流信息.
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {//结果小于0表示:不可读取的流信息,是一个损坏的音视频.
        LOGE("%s", "获取视频信息失败!");
        return;
    }

    int audio_stream_idx = -1;//定义一个音频流索引.
    int i = 0;
    for (int i = 0; i < pFormatCtx->nb_streams; ++i) {//遍历所有的流的数量.
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {//是音频流.
            LOGE("音频流ID: %d", pFormatCtx->streams[i]->codec->codec_type);
            audio_stream_idx = i;
            break;
        }
    }

    AVCodecContext *pCodecCtx = pFormatCtx->streams[audio_stream_idx]->codec;//解码器上下文.
    AVCodec *pCodex = avcodec_find_decoder(pCodecCtx->codec_id);//获取解码器.
    if (avcodec_open2(pCodecCtx, pCodex, NULL) < 0) {//打开解码器.
        LOGE("Couldn't open codec!");
        return;
    }

    AVPacket *av_packet = (AVPacket *) av_malloc(sizeof(AVPacket));//申请AVPacket,装FFmpeg读取到的数据(此处是aac).
    AVFrame *av_frame = av_frame_alloc();//申请AVFrame,装解码后的数据(此处是pcm).

    int out_channel_nb = av_get_channel_layout_nb_channels(AV_CH_LAYOUT_STEREO);//获取音频通道数.

    //转换器相关.
    SwrContext *swrContext = swr_alloc();//转换器上下文.
    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO;
    enum AVSampleFormat out_format = AV_SAMPLE_FMT_S16;
    int out_sample_rate = pCodecCtx->sample_rate;
    swr_alloc_set_opts(
            swrContext, //重采样上下文.
            //输出的.
            out_ch_layout, //输出的layout,如:5.1声道..
            out_format, //输出的样本格式. Float,S16,S24
            out_sample_rate, //输出的样本率.可以不变.
            //输入的.
            pCodecCtx->channel_layout, //输入的layout.
            pCodecCtx->sample_fmt, //输入的样本格式.
            pCodecCtx->sample_rate, //输入的样本率.
            0, //日志,不用管,可直接传0.
            NULL //日志,不用管,可直接传0.
    );//转换器的代码.
    swr_init(swrContext);//初始化转化器上下文.
    uint8_t *out_buffer = (uint8_t *) av_malloc(44100 * 2);//音频转换(重采样)后的缓冲区,即1s的pcm个数.

    //反射的方式调用Java.
    jclass audio_player = env->GetObjectClass(instance);//获取class对象.
    //处理createTrack().
    jmethodID createTrackMethod = env->GetMethodID(audio_player, "createTrack", "(II)V");//根据函数签名获取Java函数ID.
    env->CallVoidMethod(instance, createTrackMethod, 44100, out_channel_nb);//根据函数ID调用Java函数.
    //处理playTrack().
    jmethodID playTrackMethod = env->GetMethodID(audio_player, "playTrack", "([BI)V");//根据函数签名获取Java函数ID.

    //解码数据.
    while (av_read_frame(pFormatCtx, av_packet) >= 0) {//不断的循环取数据.
        //判断读出来的数据是什么数据?
        if (av_packet->stream_index == audio_stream_idx) {//音频的数据.
            //解码时,先avcodec_send_packet()再avcodec_receive_frame().
            //编码时,先avcodec_send_frame()再avcodec_receive_packet().
            int ret = avcodec_send_packet(pCodecCtx, av_packet);//放入待解码数据,正常返回0.
//            LOGE("解码成功 %d", ret);
            if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGE("解码出错或结束!");
                break;
            }
            ret = avcodec_receive_frame(pCodecCtx, av_frame);//接收解码后的数据到avFrame.
            if (ret < 0 && ret != AVERROR_EOF) {
                LOGE("读取出错!");
                break;
            }
            if (ret >= 0) {
                swr_convert(
                        //输出数据.
                        swrContext,
                        &out_buffer,
                        44100 * 2,
                        //输入数据.
                        (const uint8_t **) (av_frame->data),
                        av_frame->nb_samples
                );//音频重采样.
                int size = av_samples_get_buffer_size( //音频大小只与"采样频率"&"采样位数"&"通道数"有关.
                        NULL,
                        out_channel_nb, //通道数.
                        av_frame->nb_samples, //采样频率.
                        AV_SAMPLE_FMT_S16, //采样位数.
                        1
                );//根据AVFrame来确定解码后数据的大小.
                jbyteArray audio_sample_array = env->NewByteArray(size);//新建Java的字节数组.
                env->SetByteArrayRegion(audio_sample_array, 0, size, reinterpret_cast<const jbyte *>(out_buffer));//将C数组中的数据,赋值到Java数组jbyteArray中去.
                env->CallVoidMethod(instance, playTrackMethod, audio_sample_array, size);//根据函数ID调用Java函数.
                env->DeleteLocalRef(audio_sample_array);//释放Java数组.
            }
        }
    }

    //释放.
    av_frame_free(&av_frame);
    av_free(av_packet);
    swr_free(&swrContext);
    avcodec_close(pCodecCtx);
    avformat_close_input(&pFormatCtx);
    env->ReleaseStringUTFChars(input_, input);

    //子线程处理涉及虚拟机绑定(JVM虚拟机),此例暂未实现.

}
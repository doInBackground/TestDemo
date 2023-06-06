#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/native_window_jni.h>

extern "C" { //外部为C++编译,内部为C编译.
//#include "libavcodec/avcodec.h"
//#include "libavformat/avformat.h"
//#include "libavutil/imgutils.h"
//#include "libswscale/swscale.h"
//#include <libavutil/time.h>
#include "ffmpeg/libavcodec/avcodec.h"
#include "ffmpeg/libavformat/avformat.h"
#include "ffmpeg/libavutil/imgutils.h"
#include "ffmpeg/libswscale/swscale.h"
#include <ffmpeg/libavutil/time.h>
}

#define LOGD(...) __android_log_print(ANDROID_LOG_INFO,"WEI-Native",__VA_ARGS__)

//com\wcl\testdemo\test\test06_audio_video\test05_ffmpeg\activity\FFmpegTestActivity
//com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_activity_FFmpegTestActivity

/**
 * 返回FFmpeg的配置信息.
 */
extern "C" JNIEXPORT jstring JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_activity_FFmpegTestActivity_getFFmpegInfo(JNIEnv *env, jobject /* this */) {
    std::string hello = avcodec_configuration();//返回FFmpeg的配置信息.
    return env->NewStringUTF(hello.c_str());
}

static AVFormatContext *avFormatContext;//FFmpeg上下文.
static AVCodecContext *avCodecContext;//解码器上下文.
AVCodec *vCodec;//解码器.
ANativeWindow *nativeWindow;//Java传来的Surface.
ANativeWindow_Buffer windowBuffer;
static AVPacket *avPacket;//FFmpeg从视频文件读取到的数据.
static AVFrame *avFrame, *rgbFrame;//avFrame:由AVPacket转义而来的AVFrame; rgbFrame:由avFrame处理后即将展示到Surface的临时AVFrame;
struct SwsContext *swsContext;//转换器上下文.
uint8_t *outbuffer;

//com\wcl\testdemo\test\test06_audio_video\test05_ffmpeg\test01\PlayVideoActivity
//com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test01_PlayVideoActivity

/**
 * 播放指定路径的视频.
 */
extern "C" JNIEXPORT jint JNICALL
Java_com_wcl_testdemo_test_test06_1audio_1video_test05_1ffmpeg_test01_PlayVideoActivity_play(JNIEnv *env, jobject thiz, jstring url_, jobject surface) {

    const char *url = env->GetStringUTFChars(url_, 0);//视频路径字符串,变成C++字符串.

    avcodec_register_all();//注册所有的组件.
    avFormatContext = avformat_alloc_context();//实例化FFmpeg上下文.

    //打开视频文件或视频流url.
    if (avformat_open_input(&avFormatContext, url, NULL, NULL) != 0) {
        LOGD("Couldn't open input stream.");
        return -1;
    }
    LOGD("打开视频成功.");

    //查找文件的流信息.
    if (avformat_find_stream_info(avFormatContext, NULL) < 0) {//结果小于0表示:不可读取的流信息,是一个损坏的视频.
        LOGD("Couldn't find stream information.");
        return -1;
    }

    int videoindex = -1;//定义一个视频流索引.
    for (int i = 0; i < avFormatContext->nb_streams; i++) {//变量所有的流的数量.
        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {//如果是视频流.
            videoindex = i;//视频解封装就表示找到视频流索引.
            break;
        }
    }
    if (videoindex == -1) {
        LOGD("Couldn't find a video stream.");
        return -1;
    }
    LOGD("找到了视频流.");

    //视频流: h264 & h265
    //视频流解析需要对应算法,对应不同解码器,就需要解码器上下文.
    avCodecContext = avFormatContext->streams[videoindex]->codec;//解码器上下文.
    vCodec = avcodec_find_decoder(avCodecContext->codec_id);//获取解码器,参数类似于MediaCodec编解码器设置类型时传的"video/avc".

    //打开解码器.
    if (avcodec_open2(avCodecContext, vCodec, NULL) < 0) {
        LOGD("Couldn't open codec.");
        return -1;
    }
    LOGD("打开了解码成功.");

    //获取Java传下来的Surface.
    nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (0 == nativeWindow) {
        LOGD("Couldn't get native window from surface.");
        return -1;
    }

    //需要实例化三个容器: AvPacket(视频宽高) -> AvFrame(视频宽高) -> (缓冲区) -> AvFrame(播放器宽高).

    avPacket = av_packet_alloc();//FFmpeg实例化AVPacket,并初始化大小.
    avFrame = av_frame_alloc();//FFmpeg实例化AvFrame,并初始化大小.
    rgbFrame = av_frame_alloc();//实例化rgbFrame.

    //输入数据大小与AvFrame有关系,输出数据大小与Surface有关系.

    //输入:
    int width = avCodecContext->width;//视频宽.
    int height = avCodecContext->height;//视频高.
    int numBytes = av_image_get_buffer_size(AV_PIX_FMT_RGBA, width, height, 1);//确定输入数据的大小.
    LOGD("计算解码后的rgb %d", numBytes);
    outbuffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));//实例化一个输入缓冲区.
    av_image_fill_arrays(rgbFrame->data, rgbFrame->linesize, outbuffer, AV_PIX_FMT_RGBA, width, height, 1);//将缓冲区设置给rgbFrame.

    //输出:
    swsContext = sws_getContext(width, height, avCodecContext->pix_fmt, //输入数据信息.
                                width, height, AV_PIX_FMT_RGBA, //输出数据信息.
                                SWS_BICUBIC, NULL, NULL, NULL);//转换器上下文.

    if (0 > ANativeWindow_setBuffersGeometry(nativeWindow, width, height, WINDOW_FORMAT_RGBA_8888)) {
        LOGD("Couldn't set buffers geometry.");
        ANativeWindow_release(nativeWindow);
        return -1;
    }
    LOGD("ANativeWindow_setBuffersGeometry成功.");

    while (av_read_frame(avFormatContext, avPacket) >= 0) {//不断的循环取数据.
        //判断读出来的数据是什么数据?
        if (avPacket->stream_index == videoindex) {//此处仅处理视频数据,音频数据先不管.
            //解码时,先avcodec_send_packet()再avcodec_receive_frame().
            //编码时,先avcodec_send_frame()再avcodec_receive_packet().
            int ret = avcodec_send_packet(avCodecContext, avPacket);//放入待解码数据.
            if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGD("解码出错或结束!");
                return -1;
            }
            ret = avcodec_receive_frame(avCodecContext, avFrame);//接收解码后的数据到avFrame.
            if (ret == AVERROR(EAGAIN)) { //还有数据.
                continue;
            } else if (ret < 0) {
                break;
            }
            //此时获取到了解码后的未压缩的数据,宽高可能与播放器宽高不一致,需要处理.
            sws_scale(swsContext, avFrame->data, avFrame->linesize, 0, avCodecContext->height, rgbFrame->data, rgbFrame->linesize);
            if (ANativeWindow_lock(nativeWindow, &windowBuffer, NULL) < 0) { //锁定ANativeWindow.
                LOGD("cannot lock window!");
            } else {
                //将图像绘制到界面上,注意这里pFrameRGBA一行的像素和windowBuffer一行的像素长度可能不一致,需要转换好,否则可能花屏.
                uint8_t *dst = (uint8_t *) windowBuffer.bits;
                for (int h = 0; h < height; h++) {
                    memcpy(dst + h * windowBuffer.stride * 4, //dst:
                           outbuffer + h * rgbFrame->linesize[0], //src:
                           rgbFrame->linesize[0] //copy_amount:
                    );
                }
                switch (avFrame->pict_type) {//判断IPB帧.
                    case AV_PICTURE_TYPE_I:
                        LOGD("I");
                        break;
                    case AV_PICTURE_TYPE_P:
//                        LOGD("P");
                        break;
                    case AV_PICTURE_TYPE_B:
//                        LOGD("B");
                        break;
                    default:;
                        break;
                }
            }
            av_usleep(1000 * 33);//帧间隔还未确定,播放速度不准确,此处暂用休眠处理.
            ANativeWindow_unlockAndPost(nativeWindow);//一帧播放完毕,解锁ANativeWindow.
        }
        //音频解码.
    }
    //释放资源.
    avformat_free_context(avFormatContext);//释放FFmpeg上下文.
    env->ReleaseStringUTFChars(url_, url);//释放字符串.
    return -1;
}
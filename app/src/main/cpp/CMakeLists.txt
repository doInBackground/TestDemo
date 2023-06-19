cmake_minimum_required(VERSION 3.4.1) #设置CMake构造本地库所需要的最低版本.

message(-------------)
message(${CMAKE_SOURCE_DIR}) #打印CMakeLists.txt所在文件夹的路径.
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}") #类似于设置系统环境变量,使得可以找到该路径下(不递归)的库进行编译.
message(-------------)

# aux_source_directory在参数1目录中查找所有源文件并赋值给参数2,供add_library时添加源文件(可多次指定,不递归)。
aux_source_directory(./src/music_player MUSIC_PLAYER_SOURCE) #音频播放器代码.
aux_source_directory(src/soundtouch/SoundTouch SOUND_TOUCH_SOURCE) #音频波重采样库[倍速/变调]源码.
aux_source_directory(src/test/test TEST_SOURCE) #测试工程源码.
aux_source_directory(src/test/test_temp TEST_SOURCE_TEMP) #测试工程源码.

#add_library( lib_opencv SHARED IMPORTED)
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libopencv_java4.so)

#<<<<<<<<<<<<<<<<<<<<<<<指定头文件>>>>>>>>>>>>>>>>>>>>>>>>
#[1]通过"include_directories"指定头文件的文件夹路径后,该路径可理解为被记录成"环境变量".当"外部"需要引入头文件时,填写头文件对于该"环境变量"的相对路径即可.
#[2]如果同时存在多个同名头文件(相对路径和文件名都相同),引入时优先引入与源文件同文件夹下的头文件,没有的话则引入最先通过"include_directories"定义的头文件.
#[3]如果指定头文件路径时,层级重复指定(即同时指定头文件的父路径和爷路径),源文件引入头文件时通过两种相对路径(即".h"或"xx/.h")都可以引入.
#[4]如果外部不会使用本工程,则可以不用"include_directories"指定头文件路径,源文件亦可通过"头文件对于自身的相对路径"引入头文件.
#自建的"cpp/include"文件夹下,存放了以库形式(非源码形式)引入的三方库的头文件.
include_directories(include/opencv) #指定opencv需要被引入的头文件的路径.
include_directories(include/ffmpeg) #指定ffmpeg需要被引入的头文件的路径.
include_directories(src/soundtouch/include) #(音频波重采样库[倍速/变调])头文件引入.
include_directories(src/soundtouch/SoundTouch) #(音频波重采样库[倍速/变调])源文件中也有头文件需要引入.
#include_directories(src/test/include) #(测试工程)头文件引入.(因为测试工程头文件的引入全部使用了相对路径,故此处可以不用指定头文件路径)

#@@@@@@@@@@@@@@@@@@@@@@@创建库@@@@@@@@@@@@@@@@@@@@@@@@
add_library(
        my-opencv #自定义库名称. 动态库文件命名:[lib库名称.so]
        SHARED #库类型. 生成动态库使用SHARED,生成静态库使用STATIC.
        src/my-opencv.cpp #源文件的相对路径(相对于CMakeLists.txt). 可变参数,每多一个源文件,此处就要多填一个参数.
)

#@@@@@@@@@@@@@@@@@@@@@@@创建库@@@@@@@@@@@@@@@@@@@@@@@@
add_library(
        my-test
        SHARED
        src/my-test.cpp
        ${TEST_SOURCE} #测试工程源码.
        ${TEST_SOURCE_TEMP} #测试工程源码.
)

#@@@@@@@@@@@@@@@@@@@@@@@创建库@@@@@@@@@@@@@@@@@@@@@@@@
add_library(
        my-ffmpeg
        SHARED
        src/my_ffmpeg_video.cpp #解码播放视频.
        src/my_ffmpeg_audio.cpp #解码播放音频.
        src/my_ffmpeg_audio_deprecated.cpp #解码播放音频(过时).
        ${MUSIC_PLAYER_SOURCE} #音频播放器代码.
        ${SOUND_TOUCH_SOURCE} #音频波重采样库[倍速/变调]源码.
)

#-----------------------寻找库------------------------
#find_library(
#        log-lib #自定义名称.
#        log #真实的库名.
#        # log其实也是系统自带库,可以不用find_library直接引入.
#        # log真实路径:SDK\ndk\21.1.6352462\platforms\android-27\arch-arm\usr\lib\liblog.so
#)

#OpenCV需要libc++_shared.so,但无需在此配置,需要在gradle中配置声明下载该库.

#+++++++++++++++++++++++链接库++++++++++++++++++++++++
target_link_libraries(
        my-opencv #指定目标库.与add_library的库名称一定要相同.
        # 可变参数: 每多链接一个库,此处就要多填一个参数.
        opencv_java4
        log #系统自带库直接引入即可. #若通过find_library寻找库时,此处引入写法为: ${log-lib}
        jnigraphics #系统自带库直接引入即可.
        android #系统自带库直接引入即可.
)

#+++++++++++++++++++++++链接库++++++++++++++++++++++++
target_link_libraries(
        my-test
        log
)

#+++++++++++++++++++++++链接库++++++++++++++++++++++++
target_link_libraries(
        my-ffmpeg
        avcodec
        avdevice
        avfilter
        avformat
        avutil
        swresample
        swscale
        android
        log
        OpenSLES #音频播放库[播放暂停/声道/音量].
)
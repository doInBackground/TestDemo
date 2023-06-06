cmake_minimum_required(VERSION 3.4.1) #设置CMake构造本地库所需要的最低版本.

message(-------------)
message(${CMAKE_SOURCE_DIR}) #打印CMakeLists.txt所在文件夹的路径.
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}") #类似于设置系统环境变量,使得可以找到该路径下(不递归)的库进行编译.
message(-------------)

#add_library( lib_opencv SHARED IMPORTED)
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libopencv_java4.so)

#<<<<<<<<<<<<<<<<<<<<<<<指定头文件>>>>>>>>>>>>>>>>>>>>>>>>
include_directories(include) #指定需要被引入的头文件的路径.
include_directories(include/ffmpeg) #指定需要被引入的头文件的路径.

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
)

#@@@@@@@@@@@@@@@@@@@@@@@创建库@@@@@@@@@@@@@@@@@@@@@@@@
add_library(
        my-ffmpeg
        SHARED
        src/my-ffmpeg1.cpp
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
)
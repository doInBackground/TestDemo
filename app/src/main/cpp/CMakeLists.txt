cmake_minimum_required(VERSION 3.4.1)

include_directories(include)

add_library( # Sets the name of the library.
        native-lib
        SHARED
        native-lib.cpp
        )

#add_library( lib_opencv SHARED IMPORTED)
#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}/libopencv_java4.so)
message(-------------)
message(${CMAKE_SOURCE_DIR})
#系统环境变量
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
message(-------------)
message(${CMAKE_SOURCE_DIR})

#log-lib
find_library(
        log-lib
        log
)

#libc++_shared.so     不需要配置

#  手机   c++_shared.so
target_link_libraries( # Specifies the target library.
        native-lib
        # Links the target library to the log library
        # included in the NDK.
        opencv_java4
        ${log-lib}
        jnigraphics
        android
        )
cmake_minimum_required(VERSION 3.4.1)

include_directories(${CMAKE_SOURCE_DIR}/librtmp)
add_subdirectory(librtmp)

add_library(
        native-lib
        SHARED


        ../jni/com_wind_ndk_camera_live_RtmpClient.cpp
        JavaCallHelper.cpp
        VideoChannel.cpp
)

include_directories(${CMAKE_SOURCE_DIR}/x264/${ANDROID_ABI}/include)



find_library(
        log-lib
        log)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/x264/${ANDROID_ABI}/lib")

target_link_libraries(
        native-lib
        x264
        rtmp
        ${log-lib})

# 图像处理
include_directories(libyuv/include)
add_subdirectory(libyuv)
add_library(
        ImageUtils
        SHARED
        ../jni/com_wind_ndk_camera_live_ImageUtil.cpp
)
target_link_libraries(
        ImageUtils
        yuv
)



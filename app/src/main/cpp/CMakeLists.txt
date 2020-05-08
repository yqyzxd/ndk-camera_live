cmake_minimum_required(VERSION 3.4.1)


add_subdirectory(libyuv)
include_directories(libyuv/include)
add_library(
        native-lib
        SHARED
        ../jni/com_wind_ndk_camera_live_ImageUtil.cpp)


find_library(
        log-lib
        log)

target_link_libraries(
        native-lib

        yuv
        ${log-lib})
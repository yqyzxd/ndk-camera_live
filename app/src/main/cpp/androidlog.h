//
// Created by 史浩 on 2020/5/10.
//

#ifndef NDK_CAMERA_LIVE_ANDROIDLOG_H
#define NDK_CAMERA_LIVE_ANDROIDLOG_H

#include <android/log.h>

#define  LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#endif //NDK_CAMERA_LIVE_ANDROIDLOG_H

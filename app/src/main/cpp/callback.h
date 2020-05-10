//
// Created by 史浩 on 2020/5/10.
//

#ifndef NDK_CAMERA_LIVE_CALLBACK_H
#define NDK_CAMERA_LIVE_CALLBACK_H


#include <rtmp.h>

typedef void (*Callback)(RTMPPacket* ,int needTimestamp);
#endif //NDK_CAMERA_LIVE_CALLBACK_H

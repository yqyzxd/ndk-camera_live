//
// Created by wind on 2020/5/8.
//
#include "com_wind_ndk_camera_live_ImageUtil.h"
#include <libyuv.h>
#include <libyuv/rotate.h>

JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_ImageUtil_i420Rotate
        (JNIEnv *env, jclass, jbyteArray i420Bytes_, jint rotation_, jint width_, jint height_) {

    jbyte *i420Bytes = env->GetByteArrayElements(i420Bytes_, 0);

    uint8_t* i420= reinterpret_cast<uint8_t *>(i420Bytes);

    uint8_t* src_y= i420;
    int src_stride_y =width_;

    uint8_t* src_u=i420+width_*height_;
    int src_stride_u=width_/2;

    uint8_t* src_v=i420+width_*height_+width_*height_/4;
    int src_stride_v=width_/2;

    int size=width_*height_*3/2;
    uint8_t dst[size];

    uint8_t* dst_y=dst;
    uint8_t* dst_u=dst+width_*height_;
    uint8_t* dst_v=dst+width_*height_+width_*height_/4;

    libyuv::I420Rotate(src_y,width_,src_u,width_/2,src_v,width_/2,dst_y,height_,dst_u,height_/2,dst_v,height_/2,width_,height_,static_cast<libyuv::RotationMode>(rotation_));

  /*  jbyteArray result=env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0,size, reinterpret_cast<const jbyte *>(dst));*/

    env->ReleaseByteArrayElements(i420Bytes_, i420Bytes, 0);

    env->SetByteArrayRegion(i420Bytes_, 0,size, reinterpret_cast<const jbyte *>(dst));


}
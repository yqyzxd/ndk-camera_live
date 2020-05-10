//
// Created by wind on 2020/5/8.
//
#include "com_wind_ndk_camera_live_ImageUtil.h"
#include <libyuv.h>
#include <libyuv/rotate.h>

JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_ImageUtil_i420Rotate
        (JNIEnv *env, jclass, jbyteArray src_,jbyteArray dst_, jint rotation_, jint width_, jint height_) {

    jbyte *i420Bytes = env->GetByteArrayElements(src_, 0);

    jbyte *dstBytes = env->GetByteArrayElements(dst_, 0);

    uint8_t* i420= reinterpret_cast<uint8_t *>(i420Bytes);
    uint8_t* dst= reinterpret_cast<uint8_t *>(dstBytes);


    uint8_t* src_y= i420;
    int src_stride_y =width_;

    uint8_t* src_u=i420+width_*height_;
    int src_stride_u=width_/2;

    uint8_t* src_v=i420+width_*height_+width_*height_/4;
    int src_stride_v=width_/2;

    int size=width_*height_*3/2;
    /*   uint8_t dst[size];*/

    uint8_t* dst_y=dst;
    uint8_t* dst_u=dst+width_*height_;
    uint8_t* dst_v=dst+width_*height_+width_*height_/4;

    libyuv::I420Rotate(src_y,width_,src_u,width_/2,src_v,width_/2,dst_y,height_,dst_u,height_/2,dst_v,height_/2,width_,height_,static_cast<libyuv::RotationMode>(rotation_));

  /*  jbyteArray result=env->NewByteArray(size);
    env->SetByteArrayRegion(result, 0,size, reinterpret_cast<const jbyte *>(dst));*/

    env->ReleaseByteArrayElements(src_, i420Bytes, 0);
    env->ReleaseByteArrayElements(dst_, dstBytes, 0);

    env->SetByteArrayRegion(dst_, 0,size, reinterpret_cast<const jbyte *>(dst));


}

JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_ImageUtil_i420Scale
        (JNIEnv *env, jclass clazz, jbyteArray src_, jbyteArray dst_,
         jint srcWidth,
         jint srcHeight, jint dstWidth, jint dstHeight){
    jbyte *data = env->GetByteArrayElements(src_, 0);
    uint8_t *src = reinterpret_cast<uint8_t *>(data);



    int64_t size = (dstWidth * dstHeight * 3) >> 1;
    uint8_t dst[size];
    uint8_t *src_y;
    uint8_t *src_u;
    uint8_t *src_v;
    int src_stride_y;
    int src_stride_u;
    int src_stride_v;
    uint8_t *dst_y;
    uint8_t *dst_u;
    uint8_t *dst_v;
    int dst_stride_y;
    int dst_stride_u;
    int dst_stride_v;


    src_stride_y = srcWidth;
    src_stride_u = srcWidth >> 1;
    src_stride_v = src_stride_u;

    dst_stride_y = dstWidth;
    dst_stride_u = dstWidth >> 1;
    dst_stride_v = dst_stride_u;

    int src_y_size = srcWidth * srcHeight;
    int src_u_size = src_stride_u * (srcHeight >> 1);
    src_y = src;
    src_u = src + src_y_size;
    src_v = src + src_y_size + src_u_size;

    int dst_y_size = dstWidth * dstHeight;
    int dst_u_size = dst_stride_u * (dstHeight >> 1);
    dst_y = dst;
    dst_u = dst + dst_y_size;
    dst_v = dst + dst_y_size + dst_u_size;

    libyuv::I420Scale(src_y, src_stride_y,
                      src_u, src_stride_u,
                      src_v, src_stride_v,
                      srcWidth, srcHeight,
                      dst_y, dst_stride_y,
                      dst_u, dst_stride_u,
                      dst_v, dst_stride_v,
                      dstWidth, dstHeight,
                      libyuv::FilterMode::kFilterNone);
    env->ReleaseByteArrayElements(src_, data, 0);

    env->SetByteArrayRegion(dst_, 0, size, reinterpret_cast<const jbyte *>(dst));
}

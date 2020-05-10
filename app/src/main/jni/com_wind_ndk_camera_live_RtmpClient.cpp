//
// Created by 史浩 on 2020/5/8.
//
#include "com_wind_ndk_camera_live_RtmpClient.h"
#include "../cpp/JavaCallHelper.h"
#include "../cpp/VideoChannel.h"
#include <pthread.h>
#include <rtmp.h>
#include <string>

pthread_t pid;
JavaVM* javaVm=0;
VideoChannel* videoChannel=0;
RTMP* rtmp=0;
char* path=0;
JavaCallHelper* javaCallHelper=0;
uint64_t startTime;
pthread_mutex_t mutex;
jint JNICALL JNI_OnLoad(JavaVM* vm,void* reserved){
    javaVm=vm;
    return JNI_VERSION_1_4;
}

void callback(RTMPPacket* packet,int needTimestamp){

    if (rtmp){
        packet->m_nInfoField2=rtmp->m_stream_id;
        if (needTimestamp){
            packet->m_nTimeStamp=RTMP_GetTime()-startTime;
        }

        //参数 1 表示放入队列
        RTMP_SendPacket(rtmp,packet,1);
    }
    RTMPPacket_Free(packet);
    delete packet;

}

void* connect(void* args){
    int ret;
    rtmp=RTMP_Alloc();
    RTMP_Init(rtmp);

    do{
        ret=RTMP_SetupURL(rtmp,path);
        if(!ret){
            //todo 解析url失败,需要通知java层
            break;
        }
        //开启输出模式
        RTMP_EnableWrite(rtmp);
        ret=RTMP_Connect(rtmp,0);
        if (!ret){
            //todo 需要通知java层 服务器连接失败
            break;
        }
        ret=RTMP_ConnectStream(rtmp,0);
        if (!ret){
            //todo 需要通知java层 流连接失败
            break;
        }
    }while (0);
    if (!ret){
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        rtmp=0;
    }
    delete path;
    path=0;

    //通知java层可以开始推流了
    javaCallHelper->onPrepare(ret);
    startTime=RTMP_GetTime();
    return 0;
}
extern "C"
JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_RtmpClient_native_1init
        (JNIEnv *env, jobject jobj){

    javaCallHelper=new JavaCallHelper(javaVm,env,jobj);
    pthread_mutex_init(&mutex,0);
}

extern "C"
JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_RtmpClient_native_1initVideoEnc
        (JNIEnv *env , jobject, jint width, jint height, jint fps, jint bitrate){
    videoChannel=new VideoChannel();
    videoChannel->openCodec(width,height,fps,bitrate);
    videoChannel->setCallback(callback);

}

extern "C"
JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_RtmpClient_native_1connect
        (JNIEnv *env, jobject, jstring jurl){

    const char* url=env->GetStringUTFChars(jurl,0);

    path=new char[strlen(url)+1];
    strcpy(path,url);

    pthread_create(&pid,NULL,connect,0);

    env->ReleaseStringUTFChars(jurl,url);
}
extern "C"
JNIEXPORT void JNICALL Java_com_wind_ndk_camera_live_RtmpClient_native_1sendVideo
(JNIEnv * env, jobject, jbyteArray jbytes){


    //送去编码
    jbyte * data=env->GetByteArrayElements(jbytes,0);
    pthread_mutex_lock(&mutex);
    videoChannel->encode(reinterpret_cast<uint8_t *>(data));
    pthread_mutex_unlock(&mutex);
    env->ReleaseByteArrayElements(jbytes,data,0);

}
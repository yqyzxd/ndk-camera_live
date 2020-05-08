//
// Created by 史浩 on 2020/5/8.
//

#ifndef NDK_CAMERA_LIVE_JAVACALLHELPER_H
#define NDK_CAMERA_LIVE_JAVACALLHELPER_H

#include "../../../../../../../Library/Android/sdk/ndk/20.0.5594570/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/jni.h"

#define THREAD_MAIN 1
#define THREAD_CHILD 2

class JavaCallHelper {

    JavaCallHelper(JavaVM* _javaVm,JNIEnv* env,jobject &_jobj);
    ~JavaCallHelper();

    void onPrepare(jboolean connected,int thread=THREAD_CHILD);

public:
    JavaVM* _javaVm;
    JNIEnv* env;
    jobject_jobj;
    jmethodID jmid_prepare;
};


#endif //NDK_CAMERA_LIVE_JAVACALLHELPER_H

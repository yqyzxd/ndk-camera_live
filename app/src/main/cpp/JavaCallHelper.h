//
// Created by 史浩 on 2020/5/8.
//

#ifndef NDK_CAMERA_LIVE_JAVACALLHELPER_H
#define NDK_CAMERA_LIVE_JAVACALLHELPER_H

#include <jni.h>
#define THREAD_MAIN 1
#define THREAD_CHILD 2

class JavaCallHelper {
public:
    JavaCallHelper(JavaVM* _javaVm,JNIEnv* env,jobject& jobj);
    ~JavaCallHelper();

    void onPrepare(jboolean connected,int thread=THREAD_CHILD);

public:
    JavaVM*  javaVm;
    JNIEnv* env;
    jobject jobj;
    jmethodID jmid_prepare;
};


#endif //NDK_CAMERA_LIVE_JAVACALLHELPER_H

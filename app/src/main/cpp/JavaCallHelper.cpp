//
// Created by 史浩 on 2020/5/8.
//

#include "JavaCallHelper.h"

JavaCallHelper::JavaCallHelper(JavaVM *javaVm, JNIEnv *env, jobject& jobj) {
    this->javaVm = javaVm;
    this->env = env;
    this->jobj = env->NewGlobalRef(jobj);

    jclass jobjClass = env->GetObjectClass(jobj);
    jmid_prepare = env->GetMethodID(jobjClass, "onPrepare", "(Z)V");
}

JavaCallHelper::~JavaCallHelper() {
    env->DeleteGlobalRef(jobj);
    jobj=0;
}

void JavaCallHelper::onPrepare(jboolean connected, int thread) {
    if (thread == THREAD_CHILD) {
        JNIEnv *jniEnv;
        if (javaVm->AttachCurrentThread(&jniEnv, 0) != JNI_OK) {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_prepare,connected);
        javaVm->DetachCurrentThread();
    } else{
        this->env->CallVoidMethod(jobj, jmid_prepare,connected);
    }
}
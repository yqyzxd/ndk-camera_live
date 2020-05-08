package com.wind.ndk.camera.live;

import android.view.TextureView;

import androidx.lifecycle.LifecycleOwner;

/**
 * Created By wind
 * on 2020/5/8
 */
public class RtmpClient {
    private static final String TAG="RtmpClient";

    private LifecycleOwner lifecycleOwner;
    private int width;
    private int height;
    private VideoChannel videoChannel;
    public RtmpClient(LifecycleOwner lifecycleOwner){
        this.lifecycleOwner=lifecycleOwner;
        nativeInit();
    }

    public void initVideo(TextureView textureView,int width,int height,
                          int fps,int bitrate){
        this.width=width;
        this.height=height;

        videoChannel=new VideoChannel(lifecycleOwner,textureView,this);
        native_initVideoEnc(width,height,fps,bitrate);
    }



    public void startLive(String url){
        native_connect(url);
    }


    private native void nativeInit();
    private native void native_initVideoEnc(int width, int height, int fps, int bitrate);
    private native void native_connect(String url);

}

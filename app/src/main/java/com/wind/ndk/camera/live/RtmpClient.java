package com.wind.ndk.camera.live;

import android.view.TextureView;

import androidx.lifecycle.LifecycleOwner;

/**
 * Created By wind
 * on 2020/5/8
 */
public class RtmpClient {
    private static final String TAG="RtmpClient";
    static {
        System.loadLibrary("native-lib");
    }
    private LifecycleOwner lifecycleOwner;
    private int width;
    private int height;
    private VideoChannel videoChannel;
    public RtmpClient(LifecycleOwner lifecycleOwner){
        this.lifecycleOwner=lifecycleOwner;
        native_init();
    }

    public void initVideo(TextureView textureView,int width,int height,
                          int fps,int bitrate){
        this.width=width;
        this.height=height;

        videoChannel=new VideoChannel(lifecycleOwner,textureView,this);
        native_initVideoEnc(width,height,fps,bitrate);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void startLive(String url){
        native_connect(url);
    }


    public void stop() {
        if (videoChannel!=null){
            videoChannel.stop();
        }
    }
    private boolean connected;
    private void onPrepare(boolean connected){
        this.connected=connected;
        System.out.println("RTMP:"+connected);
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendVideo(byte[] i420Bytes) {
        native_sendVideo(i420Bytes);
    }



    private native void native_init();
    private native void native_initVideoEnc(int width, int height, int fps, int bitrate);
    private native void native_connect(String url);
    private native void native_sendVideo(byte[] i420Bytes);

}

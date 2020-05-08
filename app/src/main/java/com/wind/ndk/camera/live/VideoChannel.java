package com.wind.ndk.camera.live;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;

import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.lifecycle.LifecycleOwner;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created By wind
 * on 2020/5/8
 */
public class VideoChannel {

    private LifecycleOwner lifecycleOwner;
    private RtmpClient rtmpClient;
    private TextureView textureView;

    private FileOutputStream fos;
    private HandlerThread handlerThread;
    private CameraX.LensFacing mLensFacing;
    public VideoChannel(LifecycleOwner lifecycleOwner, TextureView textureView,RtmpClient rtmpClient){
        this.lifecycleOwner=lifecycleOwner;
        this.textureView=textureView;
        this.rtmpClient=rtmpClient;
        mLensFacing=CameraX.LensFacing.FRONT;
        handlerThread=new HandlerThread("analyzer");
        handlerThread.start();
        String dir= Environment.getExternalStorageDirectory().getAbsolutePath();

        try {
            fos=new FileOutputStream(dir+"/a.yuv");
        }catch (Exception e){
            e.printStackTrace();
        }
        CameraX.bindToLifecycle((LifecycleOwner) this,getImageAnalysis(),getPreview());
    }

    public Preview getPreview(){
        PreviewConfig config = new PreviewConfig.Builder()
                .setLensFacing(mLensFacing)
                .build();
        Preview preview = new Preview(config);
        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput previewOutput) {
                        // Your code here. For example, use previewOutput.getSurfaceTexture()
                        // and post to a GL renderer.
                        textureView.setSurfaceTexture(previewOutput.getSurfaceTexture());
                    }

                    ;
                });
        return preview;
    }

    public ImageAnalysis getImageAnalysis(){

        ImageAnalysisConfig config =
                new ImageAnalysisConfig.Builder()
                        .setCallbackHandler(new Handler(handlerThread.getLooper()))
                        .setLensFacing(mLensFacing)
                        .setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)//非阻塞模式
                        .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(config);

        imageAnalysis.setAnalyzer(
                new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(ImageProxy image, int rotationDegrees) {
                        int width=image.getWidth();
                        int height=image.getHeight();
                        System.out.println("width:"+width+"  height:"+height);
                        //将YUV_420_888格式的数据转成I420
                        byte i420Bytes[]=ImageUtil.yuv420ToI420(image.getImage());
                        //根据rotation进行旋转
                        if (rotationDegrees==90 || rotationDegrees==270){
                            ImageUtil.i420Rotate(i420Bytes,rotationDegrees,width,height);
                        }
                        try {
                            fos.write(i420Bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return imageAnalysis;
    }

    public void stop(){
        try {
            handlerThread.quitSafely();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

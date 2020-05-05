package com.wind.ndk.camera.live;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

public class MainActivity extends AppCompatActivity {


    private TextureView textureView;
    private HandlerThread handlerThread;
    private CameraX.LensFacing mLensFacing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.texture_view);
        mLensFacing=CameraX.LensFacing.FRONT;
        handlerThread=new HandlerThread("analyzer");
        handlerThread.start();
        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            CameraX.bindToLifecycle((LifecycleOwner) this,getImageAnalysis(),getPreview());
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1111);
        }

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
                        int w=image.getWidth();
                        int h=image.getHeight();
                        System.out.println("w:"+w+"   h:"+h+"  rotationDegrees:"+rotationDegrees);
                    }
                });
        return imageAnalysis;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }
}

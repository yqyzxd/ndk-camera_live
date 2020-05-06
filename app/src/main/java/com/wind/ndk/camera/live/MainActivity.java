package com.wind.ndk.camera.live;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    private TextureView textureView;
    private HandlerThread handlerThread;
    private CameraX.LensFacing mLensFacing;

    private FileOutputStream fos;
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
         granted &= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            String dir=Environment.getExternalStorageDirectory().getAbsolutePath();

            try {
                fos=new FileOutputStream(dir+"/a.yuv");
            }catch (Exception e){
                e.printStackTrace();
            }

            CameraX.bindToLifecycle((LifecycleOwner) this,getImageAnalysis(),getPreview());
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1111);
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

                        int width=image.getWidth();
                        int height=image.getHeight();
                        System.out.println("width:"+width+"  height:"+height);
                        //将YUV_420_888格式的数据转成I420
                        byte bytes[]=ImageUtil.yuv420ToI420(image.getImage());
                        try {
                            fos.write(bytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        return imageAnalysis;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        handlerThread.quitSafely();
    }
}

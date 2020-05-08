package com.wind.ndk.camera.live;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.TextureView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraX;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {


    private TextureView textureView;
    private HandlerThread handlerThread;
    private CameraX.LensFacing mLensFacing;

    private  RtmpClient rtmpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.texture_view);

        boolean granted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
         granted &= ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            rtmpClient=new RtmpClient(this);
            rtmpClient.initVideo(textureView,480,640,10,640_000);
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1111);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}

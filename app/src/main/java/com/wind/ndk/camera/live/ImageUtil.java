package com.wind.ndk.camera.live;

import android.media.Image;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created By wind
 * on 2020/5/6
 */
public class ImageUtil {

    public static final String TAG="ImageUtil";

    public static  byte[] yuv420ToI420(Image image) {

        int width=image.getWidth();
        int height=image.getHeight();
        Image.Plane planes[]=image.getPlanes();

        byte[] i420Bytes=new byte[width*height*3/2];

        byte[] rowData=new byte[planes[0].getRowStride()];

        int planeOffset=0;
        for (int i=0;i<planes.length;i++) {
            switch (i){
                case 0:
                    planeOffset=0;
                    break;
                case 1:
                    planeOffset=width*height;
                    break;
                case 2:
                    planeOffset=width*height+width*height/4;
                    break;
            }

            ByteBuffer buffer=planes[i].getBuffer();
            int rowStride=planes[i].getRowStride();
            int pixelStride=planes[i].getPixelStride();
            if (true) {
                Log.v(TAG, "pixelStride " + pixelStride);
                Log.v(TAG, "rowStride " + rowStride);
                Log.v(TAG, "width " + width);
                Log.v(TAG, "height " + height);
                Log.v(TAG, "buffer size " + buffer.remaining());
            }

            int shift=(i==0)?0:1;
            int w=width>>shift;
            int h=height>>shift;

            for (int row = 0; row < h; row++) {
                if (pixelStride==1){
                    buffer.get(i420Bytes,planeOffset,w);
                }else {
                    int len=w*pixelStride-1;
                    buffer.get(rowData,0,len);
                    for (int col=0;col<w;col++){
                        i420Bytes[planeOffset++]=rowData[col*pixelStride];
                    }
                }

                if (row<h-1){
                    buffer.position((row+1)*rowStride);
                }


            }
        }


        return i420Bytes;
    }
}

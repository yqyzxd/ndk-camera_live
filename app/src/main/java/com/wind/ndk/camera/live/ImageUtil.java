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

    /**
     * YUV420是一个系列 包含yv12 NV21 I420等
     * @param image
     * @return
     */
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
                /**
                 * Y                         U
                 * pixelStride 1             2
                 * rowStride 576             567
                 * width 576                 567
                 * height 432                432
                 * buffer size 248832        124415
                 *
                 * 看buffer size Y是248832 那么U应该是248832/4=62208 但实际上有124415所以另外的62207是填充数据
                 * 为什么填充数据是62207而不是62208  ，最后一位省略了。 相当于是 U0U0U0U
                 */
                Log.v(TAG, "pixelStride " + pixelStride);
                Log.v(TAG, "rowStride " + rowStride);
                Log.v(TAG, "width " + width);
                Log.v(TAG, "height " + height);
                Log.v(TAG, "buffer size " + buffer.remaining());
            }

            int shift=(i==0)?0:1;
            int w=width>>shift;
            int h=height>>shift;
            buffer.position(0);
            for (int row = 0; row < h; row++) {
                int len=0;
                if (pixelStride==1){
                    len=w;
                    buffer.get(i420Bytes,planeOffset,len);
                    planeOffset+=len;
                }else {
                    len = w  * pixelStride -1; //按理应该是w*pixelStride，但是最后一位可能没有
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

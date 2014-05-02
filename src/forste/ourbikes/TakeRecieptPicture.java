package forste.ourbikes;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.os.Build;

public class TakeRecieptPicture extends Activity implements Callback {
    private Camera camera;
    private SurfaceView mSurfaceView;
    SurfaceHolder mSurfaceHolder;

    private TouchSurfaceView mGLSurfaceView;

    ShutterCallback shutter = new ShutterCallback(){

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub
            // No action to be perfomed on the Shutter callback.

        }

       };
    PictureCallback raw = new PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            // No action taken on the raw data. Only action taken on jpeg data.
      }

       };

    PictureCallback jpeg = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub

            FileOutputStream outStream = null;
            try{
                outStream = new FileOutputStream("/sdcard/test.jpg");
                outStream.write(data);
                outStream.close();
            }catch(FileNotFoundException e){
                Log.d("Camera", e.getMessage());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("Camera", e.getMessage());
            }

        }

       };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
    WindowManager.LayoutParams.FLAG_FULLSCREEN);


         mGLSurfaceView = new TouchSurfaceView(this); 
     addContentView(mGLSurfaceView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));

        mSurfaceView = new SurfaceView(this);
         addContentView(mSurfaceView, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
       mSurfaceHolder = mSurfaceView.getHolder();
       mSurfaceHolder.addCallback(this);
       mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT|LayoutParams.FILL_PARENT);  //was FLAG_BLUR_BEHIND
       }

    private void takePicture() {
        // TODO Auto-generated method stub
        camera.takePicture(shutter, raw, jpeg);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        Camera.Parameters p = camera.getParameters();
        p.setPreviewSize(arg2, arg3);
        try {
        camera.setPreviewDisplay(arg0);
        } catch (IOException e) {
        e.printStackTrace();
        }
        camera.startPreview(); 
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    	if (camera == null) {
    		camera = Camera.open();
    		try {
    			camera.setPreviewDisplay(holder);

    			// TODO test how much setPreviewCallbackWithBuffer is faster
    			camera.setPreviewCallback((PreviewCallback) this);
    		} catch (IOException e) {
    			camera.release();
    			camera = null;
    		}
    	}
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }
    


}
   
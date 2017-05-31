package com.ak.mrcam.app;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by dg hdghfd on 19-04-2017.
 */

public class CameraPreview extends SurfaceView implements
        SurfaceHolder.Callback {
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    public MediaRecorder mrec = new MediaRecorder();
    Context context;
    public static int rotation;

    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context=context;
        this.mCamera = camera;
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            // left blank for now
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.stopPreview();
        mCamera.release();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
                               int width, int height) {
        /*// start preview with new settings
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // intentionally left blank for a test
        }*/

        Camera.CameraInfo camInfo = new Camera.CameraInfo();
        int cameraRotationOffset = camInfo.orientation;


        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Camera.Parameters parameters = mCamera.getParameters();
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        int degrees=0;


        switch (display.getRotation()) {
            case Surface.ROTATION_0:
              //  parameters.setPreviewSize(height, width);
                mCamera.setDisplayOrientation(90);
                degrees = 0;
                break;
            case Surface.ROTATION_90:
              //  parameters.setPreviewSize(height, width);
                mCamera.setDisplayOrientation(0);
                degrees = 90;
                break;
            case Surface.ROTATION_180:
               // parameters.setPreviewSize(height, width);
                mCamera.setDisplayOrientation(270);
                degrees = 180;
                break;
            case Surface.ROTATION_270:
               // parameters.setPreviewSize(height, width);
                mCamera.setDisplayOrientation(80);
                degrees = 270;
                break;
        }

        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (camInfo.orientation + degrees) % 360;
        } else { // back-facing
            rotation = (camInfo.orientation - degrees + 360) % 360;
        }

        Log.v("rotation"," asdf "+rotation);
        parameters.setPreviewSize(height, width);
        //mCamera.setDisplayOrientation(rotation);



        previewCamera(surfaceHolder);

    }


    public void previewCamera(SurfaceHolder mHolder) {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            //Log.d(APP_CLASS, "Cannot start preview", e);
            e.printStackTrace();
        }
    }
}
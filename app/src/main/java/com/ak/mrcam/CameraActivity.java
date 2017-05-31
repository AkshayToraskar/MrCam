package com.ak.mrcam;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ak.mrcam.app.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;


public class CameraActivity extends AppCompatActivity {

    public static String TAG = CameraActivity.class.getName();
    public static int SWITCHER = 0; //0camera 1recording
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public MediaRecorder mMediaRecorder;
    public boolean isRecording;
    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    public void onBtnClick(View view) {

        int id = view.getId();

        switch (id) {


            case R.id.record_button:

                switch (SWITCHER) {
                    case 0:
                        takePicture();
                        break;
                    case 1:
                        startRecording();
                        break;
                }
                break;

            case R.id.photo_video_camera_switcher:
                if (SWITCHER == 0) {
                    SWITCHER = 1;
                } else {
                    SWITCHER = 0;
                }
                break;

        }

    }

    public void startRecording() {
        if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            //setCaptureButtonBackground(R.drawable.btn_capture_video);
            isRecording = false;

            //captureButton.setVisibility(View.VISIBLE);

            //NewSessionActivity.saveCapturedData.onVideoCaptured(videoUri);


        } else {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();

                // inform the user that recording has started
               // setCaptureButtonBackground(R.drawable.btn_stop_capture_video);

                //captureButton.setVisibility(View.GONE);

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }
    }

    public void takePicture() {


        Log.i(TAG, "Tacking picture");


        Camera.PictureCallback callback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "Saving a bitmap to file");
                File pictureFile = null;
                try {
                    pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

                    if (pictureFile == null) {
                        return;
                    }


                    // imgData = data.clone();

                    //intent.putExtra("byteData", imgData);
                    //setResult(RESULT_OK, intent);


                    /*Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                    android.hardware.Camera.getCameraInfo(0, info);
                    Bitmap bitmap = rotate(realImage, info.orientation);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();*/

                /*int size = bitmap.getRowBytes() * bitmap.getHeight();
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                bitmap.copyPixelsToBuffer(byteBuffer);
                byte[] byteArray = byteBuffer.array();*/


                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();


                  //  imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);


                   // NewSessionActivity.saveCapturedData.onPictureTaken(data, imageUri);
                    mCamera.startPreview();

                    //    mCameraPreview.destroyDrawingCache();
                    //    finish();


                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }
            }
        };


        mCamera.takePicture(null, null, callback);


    }

    private boolean prepareVideoRecorder() {

        //  mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());



        mMediaRecorder.setOrientationHint(90);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }


    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }


    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam" + File.separator +
                    "I" + timeStamp);
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + ".Gynaecam" + File.separator +
                    "V" + timeStamp);

           // videoUri = Uri.fromFile(mediaFile);
        } else {
            return null;
        }

        return mediaFile;
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

}

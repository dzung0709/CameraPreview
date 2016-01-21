package hci.com.weirdcam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import hci.com.weirdcam.fragment.FilterFragments;
import hci.com.weirdcam.util.CameraHelper;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCrosshatchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageThresholdEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener,
        FilterFragments.FilterFragmentCallback{

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private Bitmap bitmap;

    // Views
    private CardView cameraSwitchBtn;
    private CardView filterShuffleBtn;
    private ImageView cameraBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));

        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();

        init();
    }

    private void init() {
        cameraBtn = (ImageView) findViewById(R.id.camera_btn);
        cameraSwitchBtn = (CardView) findViewById(R.id.camera_switch_btn);
        filterShuffleBtn = (CardView) findViewById(R.id.filter_shuffle_btn);

        cameraBtn.setOnClickListener(this);
        cameraSwitchBtn.setOnClickListener(this);
        filterShuffleBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera != null) {
            mCamera.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.onPause();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_btn:
                takePicture();
                break;
            case R.id.camera_switch_btn:
                Intent intent = new Intent(CameraActivity.this, GalleryActivity.class);
                startActivity(intent);
                break;
            case R.id.filter_shuffle_btn:
                FilterFragments filterFragments = new FilterFragments();
                filterFragments.setFilterFragmentCallback(this);
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.footer_container, filterFragments, FilterFragments.TAG)
                        .addToBackStack(FilterFragments.TAG)
                        .commit();
                break;
        }
    }

    @Override
    public void onFilterItemClick(int position) {
        switch (position) {
            case 0:
                mGPUImage.setFilter(new GPUImageToonFilter());
                break;
            case 1:
                mGPUImage.setFilter(new GPUImageSketchFilter());
                break;
            case 2:
                mGPUImage.setFilter(new GPUImageBulgeDistortionFilter(0.3f, 0.5f, new PointF(0.5f, 0.5f)));
                break;
            case 3:
                mGPUImage.setFilter(new GPUImageGlassSphereFilter(new PointF(0.5f, 0.5f), 0.5f, 0.5f));
                break;
            case 4:
                mGPUImage.setFilter(new GPUImageEmbossFilter());
                break;
            case 5:
                mGPUImage.setFilter(new GPUImageSwirlFilter(0.5f, 0.2f, new PointF(0.5f, 0.5f)));
                break;
            case 6:
                mGPUImage.setFilter(new GPUImageCrosshatchFilter(0.01f, 0.003f));
                break;
            case 7:
                mGPUImage.setFilter(new GPUImagePosterizeFilter());
                break;
            case 8:
                mGPUImage.setFilter(new GPUImageWeakPixelInclusionFilter());
                break;
            case 9:
                mGPUImage.setFilter(new GPUImageCGAColorspaceFilter());
                break;
            case 10:
                mGPUImage.setFilter(new GPUImageDilationFilter(10));
                break;
            case 11:
                mGPUImage.setFilter(new GPUImageThresholdEdgeDetection());
                break;
        }
    }

    /**
     * Camera
     */
    private void takePicture() {
        // TODO get a size that is about the size of the screen
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        params.setRotation(90);
        mCamera.mCameraInstance.setParameters(params);
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i("ASDF", "Supported: " + size.width + "x" + size.height);
        }
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {
                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        final String fileName;
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        // mGPUImage.setImage(bitmap);
                        final GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
                        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        float currentTime = System.currentTimeMillis();
                        fileName = currentTime + ".jpg";
                        mGPUImage.saveToPictures(bitmap, "WeirdCamera",
                                fileName,
                                new GPUImage.OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri
                                                                       uri) {
                                        // Move to PhotoView
                                        File storePath = Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES);
                                        String filePath = storePath.getPath() + "/WeirdCamera/" + fileName;
                                        Intent intent = new Intent(CameraActivity.this, PhotoViewActivity.class);
                                        intent.putExtra("imagePath", filePath);
                                        startActivity(intent);

                                        bitmap.recycle();
                                        bitmap = null;
                                        pictureFile.delete();
                                        camera.startPreview();
                                        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                    }
                                });
                    }
                });
    }

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "WeirdCamera");
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
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            List<Camera.Size> list = mCameraInstance.getParameters().getSupportedPreviewSizes();
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    CameraActivity.this, mCurrentCameraId);
            CameraHelper.CameraInfo2 cameraInfo = new CameraHelper.CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /** A safe way to get an instance of the Camera object. */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mGPUImage.deleteImage();
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
}

package hci.com.weirdcam;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import java.util.List;
import java.util.Random;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBulgeDistortionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageCGAColorspaceFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGlassSphereFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSwirlFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;

    // Views
    private CardView cameraSwitchBtn;
    private CardView filterShuffleBtn;
    private ImageView cameraBtn;
    private GLSurfaceView glSurfaceView;
    private ImageView takenImageView;

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
        glSurfaceView = (GLSurfaceView) findViewById(R.id.surfaceView);
        takenImageView = (ImageView) findViewById(R.id.taken_image);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera_btn:
                if (mGPUImage != null) {
                    Bitmap bitmap = mGPUImage.getBitmapWithFilterApplied();
                    glSurfaceView.setVisibility(View.GONE);
                    takenImageView.setVisibility(View.VISIBLE);
                    takenImageView.setImageBitmap(bitmap);
                }
                break;
            case R.id.camera_switch_btn:
                mCamera.switchCamera();
                break;
            case R.id.filter_shuffle_btn:
                applyFilter();
                break;
        }
    }

    private void applyFilter() {
        Random random = new Random();
//        int filterIndex = random.nextInt(10);
        int filterIndex = 9;
        switch (filterIndex) {
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
                mGPUImage.setFilter(new GPUImageGlassSphereFilter(new PointF(0.5f, 0.5f), 0.4f, 0.5f));
                break;
            case 4:
                mGPUImage.setFilter(new GPUImageEmbossFilter());
                break;
            case 5:
                mGPUImage.setFilter(new GPUImageSwirlFilter(0.5f, 0.2f, new PointF(0.5f, 0.5f)));
                break;
            case 6:
                GPUImagePixelationFilter filter = new GPUImagePixelationFilter();
                filter.setPixel(8.0f);
                mGPUImage.setFilter(filter);
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
        }
    }

    /**
     * Camera
     */
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
                    MainActivity.this, mCurrentCameraId);
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

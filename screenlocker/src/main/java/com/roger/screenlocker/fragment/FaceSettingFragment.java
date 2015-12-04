package com.roger.screenlocker.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.faceplusplus.api.FaceDetecter;
import com.roger.screenlocker.R;
import com.roger.screenlocker.utils.faceutil.BitmapUtil;
import com.roger.screenlocker.utils.faceutil.FaceMask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FaceSettingFragment extends BaseFragment
        implements View.OnClickListener,
        SurfaceHolder.Callback,
        Camera.PreviewCallback {

    SurfaceView camerasurface = null;
    FaceMask mask = null;
    Camera camera = null;
    HandlerThread handleThread = null;
    Handler detectHandler = null;
    private int width = 320;
    private int height = 240;
    FaceDetecter facedetecter = null;

    private boolean isSaveed;
    private View mViewOk, text_saved;


    public FaceSettingFragment() {
    }


    @Override int getLayoutId() {
        return R.layout.fragment_facesetting;
    }


    @Override void initUI() {
        // TODO Auto-generated method stub
        init();
    }


    /**
     * 初始化
     */
    public void init() {
        camerasurface = (SurfaceView) mView.findViewById(R.id.camera_preview);
        mask = (FaceMask) mView.findViewById(R.id.mask);
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(480,
                800);
        handleThread = new HandlerThread("dt");
        handleThread.start();
        detectHandler = new Handler(handleThread.getLooper());
        para.addRule(RelativeLayout.CENTER_IN_PARENT);
        camerasurface.setLayoutParams(para);
        mask.setLayoutParams(para);
        camerasurface.getHolder().addCallback(this);
        camerasurface.setKeepScreenOn(true);

        facedetecter = new FaceDetecter();
        if (!facedetecter.init(getActivity(),
                "af622c0acdccd2d794f90243cb033465")) {
            Log.e("diff", "有错误 ");
        }
        facedetecter.setTrackingMode(true);
        mViewOk = mView.findViewById(R.id.btn_check);
        text_saved = mView.findViewById(R.id.text_saved);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_check:

                break;
        }
    }


    @Override public void onResume() {
        super.onResume();
        camera = Camera.open(1);
        Camera.Parameters para = camera.getParameters();
        para.setPreviewSize(width, height);
        camera.setParameters(para);
    }


    @Override public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            this.onDestroy();
        }
    }


    @Override public void onDestroy() {
        super.onDestroy();
        facedetecter.release(this.getActivity());
        handleThread.quit();
    }


    @Override public void surfaceCreated(SurfaceHolder holder) {
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(90);
        camera.startPreview();
        camera.setPreviewCallback(this);
    }


    @Override public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override public void onPreviewFrame(final byte[] data, Camera camera) {
        camera.setPreviewCallback(null);
        detectHandler.post(new Runnable() {

            @Override public void run() {
                byte[] ori = new byte[width * height];
                int is = 0;
                for (int x = width - 1; x >= 0; x--) {

                    for (int y = height - 1; y >= 0; y--) {

                        ori[is] = data[y * width + x];

                        is++;
                    }
                }

                final FaceDetecter.Face[] faceinfo = facedetecter.findFaces(ori,
                        height, width);

                getActivity().runOnUiThread(new Runnable() {

                    @Override public void run() {
                        mask.setFaceInfo(faceinfo);
                    }
                });

                Log.i("Tag", "faceinfo:" +
                        (faceinfo == null ? null : data.length + ""));
                if (faceinfo != null && faceinfo.length >= 1 && !isSaveed) {

                    // Convert to JPG
                    Camera.Size previewSize
                            = FaceSettingFragment.this.camera.getParameters()
                                                             .getPreviewSize();
                    YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21,
                            previewSize.width, previewSize.height, null);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width,
                            previewSize.height), 80, baos);
                    byte[] jdata = baos.toByteArray();

                    // Convert to Bitmap
                    Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0,
                            jdata.length);

                    if (bitmap == null) {
                        Toast.makeText(getActivity(), "未检测到人脸.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String faceImageBase64Str = BitmapUtil.bitmaptoString(
                                bitmap);
                        mHomeActivity.localSharedPreferences.edit()
                                                            .putString(
                                                                    mHomeActivity.PREFS_FACE_STRING,
                                                                    faceImageBase64Str)
                                                            .commit();
                        Toast.makeText(getActivity(), "保存成功.",
                                Toast.LENGTH_SHORT).show();
                        isSaveed = true;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override public void run() {
                                mViewOk.setVisibility(View.VISIBLE);
                                text_saved.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }

                FaceSettingFragment.this.camera.setPreviewCallback(
                        FaceSettingFragment.this);
            }
        });
    }
}

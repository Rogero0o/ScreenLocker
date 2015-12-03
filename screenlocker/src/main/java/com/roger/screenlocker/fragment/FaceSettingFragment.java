package com.roger.screenlocker.fragment;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import com.faceplusplus.api.FaceDetecter;
import com.roger.screenlocker.R;
import com.roger.screenlocker.utils.faceutil.FaceMask;
import java.io.IOException;

public class FaceSettingFragment extends BaseFragment
        implements View.OnClickListener,SurfaceHolder.Callback,
        Camera.PreviewCallback {

    SurfaceView camerasurface = null;
    FaceMask mask = null;
    Camera camera = null;
    HandlerThread handleThread = null;
    Handler detectHandler = null;
    Runnable detectRunnalbe = null;
    private int width = 320;
    private int height = 240;
    FaceDetecter facedetecter = null;


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
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(480, 800);
        handleThread = new HandlerThread("dt");
        handleThread.start();
        detectHandler = new Handler(handleThread.getLooper());
        para.addRule(RelativeLayout. CENTER_IN_PARENT);
        camerasurface.setLayoutParams(para);
        mask.setLayoutParams(para);
        camerasurface.getHolder().addCallback(this);
        camerasurface.setKeepScreenOn(true);

        facedetecter = new FaceDetecter();
        if (!facedetecter .init(getActivity(), "af622c0acdccd2d794f90243cb033465" )) {
            Log. e("diff", "有错误 " );
        }
        facedetecter.setTrackingMode(true);
    }


    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gesturereset:
                break;
            case R.id.btn_gestureset:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open(1);
        Camera.Parameters para = camera.getParameters();
        para.setPreviewSize( width, height );
        camera.setParameters(para);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            this.onDestroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        facedetecter.release(this.getActivity());
        handleThread.quit();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        camera.setPreviewCallback( null);
        detectHandler.post(new Runnable() {

            @Override
            public void run() {
                byte[] ori = new byte[ width * height ];
                int is = 0;
                for (int x = width - 1; x >= 0; x--) {

                    for (int y = height - 1; y >= 0; y--) {

                        ori[is] = data[y * width + x];

                        is++;
                    }

                }
                final FaceDetecter.Face[] faceinfo = facedetecter.findFaces( ori, height,
                        width);
                getActivity().runOnUiThread( new Runnable() {

                    @Override
                    public void run() {
                        mask.setFaceInfo(faceinfo);
                    }
                });
                FaceSettingFragment.this.camera .setPreviewCallback
                        (FaceSettingFragment.this);
            }
        });
    }

}

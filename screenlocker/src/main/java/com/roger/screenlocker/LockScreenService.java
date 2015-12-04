/**
 * ʹ�÷�ʽ��
 * 1����manifest��ע��Ȩ�ޣ�<uses-permission android:name="android.permission.DISABLE_KEYGUARD"
 * />
 * 2����manifest��ע��˷���
 * 3����toMainIntent���ó�Ҫ��ת���Ľ��棨��Ctrl+F����"#"�ַ�ɿ��ٶ�λ��
 * 4����������Main�������˷���startService(new Intent(Main.this,
 * myService.class));
 **/

package com.roger.screenlocker;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.faceplusplus.api.FaceDetecter;
import com.roger.screenlocker.fragment.FaceSettingFragment;
import com.roger.screenlocker.fragment.MenuFragment;
import com.roger.screenlocker.render.MissView;
import com.roger.screenlocker.render.util.UriUtil;
import com.roger.screenlocker.utils.DataString;
import com.roger.screenlocker.utils.GestureLockView;
import com.roger.screenlocker.utils.SliderLayout;
import com.roger.screenlocker.utils.VibratorUtil;
import com.roger.screenlocker.utils.faceutil.BitmapUtil;
import com.roger.screenlocker.utils.faceutil.FaceCompareRequest;
import com.roger.screenlocker.utils.faceutil.FaceMask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("deprecation") public class LockScreenService extends Service
        implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private KeyguardManager keyguardManager = null;
    private KeyguardManager.KeyguardLock keyguardLock = null;
    private boolean isShow;//标示是否已经显示

    SurfaceView camerasurface = null;
    FaceMask mask = null;
    Camera camera = null;
    HandlerThread handleThread = null;
    Handler detectHandler = null;
    private int width = 320;
    private int height = 240;
    FaceDetecter facedetecter = null;
    private View mView;
    private FaceCompareRequest request;

    @Override public IBinder onBind(Intent intent) {
        return null;
    }


    @Override public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.SCREEN_OFF");
        registerReceiver(screenReceiver, intentFilter);
    }


    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }


    @Override public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(screenReceiver);
        //重新启动
        if (MenuFragment.isOpen) {
            startService(new Intent(LockScreenService.this,
                    LockScreenService.class));
        }
    }


    private BroadcastReceiver screenReceiver = new BroadcastReceiver() {

        @SuppressWarnings("static-access") @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.SCREEN_OFF")) {

                keyguardManager = (KeyguardManager) context.getSystemService(
                        context.KEYGUARD_SERVICE);
                keyguardLock = keyguardManager.newKeyguardLock("");
                keyguardLock.disableKeyguard();
                if (BaseActivity.localSharedPreferences.getBoolean(
                        BaseActivity.PREFS_IS_OPEN, false)) {
                    if (!isShow) CreateFloatView();
                }
            }
        }
    };

    public static final int FLAG_LAYOUT_IN_SCREEN = 0x00000100;


    public void CreateFloatView() {
        final View mFloatView = View.inflate(getApplicationContext(),
                R.layout.screenlock_main, null);
        final WindowManager windowManager
                = (WindowManager) getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        wmParams.width = -1;
        wmParams.height = -1;
        wmParams.type = 2010;

        if (!BaseActivity.localSharedPreferences.getBoolean(
                HomeActivity.PREFS_SETTING_SHOWMENU, false)) {
            wmParams.flags = FLAG_LAYOUT_IN_SCREEN;
        }

        isShow = true;
        //初始化日期星期
        TextView mDataTextView = (TextView) mFloatView.findViewById(R.id.date);
        mDataTextView.setText(DataString.StringData());

        String path = "";
        if (!TextUtils.isEmpty(BaseActivity.localSharedPreferences.getString(
                BaseActivity.PREFS_IMAGE_PATH, ""))) {
            path = BaseActivity.localSharedPreferences.getString(
                    BaseActivity.PREFS_IMAGE_PATH, "");
        }
        else {
            path = UriUtil.getImageAbsolutePath(this.getApplication(),
                    BaseActivity.mUri);
        }
        if (TextUtils.isEmpty(path)) {
            path = HomeActivity.getSDPath() + "/" + HomeActivity.DIR_NAME +
                    "/" + HomeActivity.IMAGE_NAME;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MissView missView = (MissView) mFloatView.findViewById(
                    R.id.missview);//背景
            missView.setVisibility(View.VISIBLE);
            missView.initPicture(path);
        }
        else {
            ImageView imageView = (ImageView) mFloatView.findViewById(
                    R.id.imageview);
            imageView.setImageURI(Uri.fromFile(new File(path)));
            imageView.setVisibility(View.VISIBLE);
        }

        SliderLayout mSliderLayout = (SliderLayout) mFloatView.findViewById(
                R.id.sliderlayout);//滑动解锁
        mSliderLayout.setOnUnlockListener(new SliderLayout.OnUnlockListener() {
            @Override public void onUnlock() {
                if (BaseActivity.localSharedPreferences.getBoolean(
                        BaseActivity.PREFS_SETTING_SHAKE, false)) {
                    VibratorUtil.Vibrate(getApplicationContext(), 50);
                }
                windowManager.removeView(mFloatView);
                isShow = false;
            }
        });

        GestureLockView mGestureLockView
                = (GestureLockView) mFloatView.findViewById(
                R.id.gestureLockView);//手势解锁
        mGestureLockView.setSettingMode(false);
        String key = BaseActivity.localSharedPreferences.getString(
                HomeActivity.PREFS_GESTURE, null);

        if (BaseActivity.localSharedPreferences.getBoolean(
                BaseActivity.PREFS_SETTING_SHOWLINE, false)) {//是否隐藏连接线
            mGestureLockView.setShowLine(false);
        }
        else {
            mGestureLockView.setShowLine(true);
        }

        if (BaseActivity.localSharedPreferences.getInt(BaseActivity.PREFS_MODE,
                0) == 1) {//是否为滑动解锁
            mSliderLayout.setVisibility(View.VISIBLE);
            mGestureLockView.setVisibility(View.GONE);
        }
        else if (BaseActivity.localSharedPreferences.getInt(
                BaseActivity.PREFS_MODE, 0) == 2) {
            mSliderLayout.setVisibility(View.GONE);
            mGestureLockView.setVisibility(View.VISIBLE);
        }

        if (TextUtils.isEmpty(key)) {//手势为空则换为滑动解锁
            Toast.makeText(getApplication(),
                    getResources().getString(R.string.gesture_note),
                    Toast.LENGTH_SHORT).show();
            mGestureLockView.setVisibility(View.GONE);
            mSliderLayout.setVisibility(View.VISIBLE);
        }
        mGestureLockView.setKey(key);
        mGestureLockView.setOnGestureFinishListener(
                new GestureLockView.OnGestureFinishListener() {
                    @Override
                    public void OnGestureFinish(boolean success, String key) {
                        if (BaseActivity.localSharedPreferences.getBoolean(
                                BaseActivity.PREFS_SETTING_SHAKE, false)) {
                            VibratorUtil.Vibrate(getApplicationContext(), 50);
                        }
                        if (success) {
                            windowManager.removeView(mFloatView);
                            isShow = false;
                        }
                        else {
                            YoYo.with(Techniques.Shake)
                                .duration(1000)
                                .playOn(mFloatView.findViewById(
                                        R.id.gestureLockView));
                        }
                    }
                });

        initFace(mFloatView);
        mView = mFloatView;
        windowManager.addView(mFloatView, wmParams);
        startFace();
    }


    private void initFace(View mView) {
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
        if (!facedetecter.init(this, "af622c0acdccd2d794f90243cb033465")) {
            Log.e("diff", "有错误 ");
        }
        facedetecter.setTrackingMode(true);
    }


    private void startFace() {
        camera = Camera.open(1);
        Camera.Parameters para = camera.getParameters();
        para.setPreviewSize(width, height);
        camera.setParameters(para);
    }


    private void stopFace() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            this.onDestroy();
        }
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

                mView.post(new Runnable() {

                    @Override public void run() {
                        mask.setFaceInfo(faceinfo);
                    }
                });

                Log.i("Tag", "faceinfo:" +
                        (faceinfo == null ? null : data.length + ""));
                if (faceinfo != null && faceinfo.length >= 1) {

                    // Convert to JPG
                    Camera.Size previewSize
                            = LockScreenService.this.camera.getParameters()
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
                        Toast.makeText(LockScreenService.this, "未检测到人脸.",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String faceImageBase64Str = BitmapUtil.bitmaptoString(
                                bitmap);
                        BaseActivity.localSharedPreferences.edit()
                                                           .putString(
                                                                   HomeActivity.PREFS_FACE_STRING,
                                                                   faceImageBase64Str)
                                                           .commit();
                    }
                }
                LockScreenService.this.camera.setPreviewCallback(
                        LockScreenService.this);
            }
        });
    }
}

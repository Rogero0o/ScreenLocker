package com.roger.screenlocker.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.roger.screenlocker.HomeActivity;
import com.roger.screenlocker.R;
import com.roger.screenlocker.utils.GestureLockView;
import com.roger.screenlocker.utils.RippleView;

public class GestureSettingFragment extends BaseFragment implements View.OnClickListener {

    private final static int DELAY_TIME = 2000;
    private final static int MSG_RESULT = 1;
    private final static int MSG_OPEN_MENU = 2;
    private GestureLockView gestureLockView;

    private RippleView btn_gesturereset;
    private RippleView btn_gestureset;
    private int setGestureTimes = 0;//设置手势图形次数，初始为0
    private String firstkey;//密码
    private String key;//密码
    private TextView mTextView;//提示信息
    private Handler mHandler;

    public GestureSettingFragment() {
    }

    @Override
    int getLayoutId() {
        return R.layout.fragment_gesturesetting;
    }

    @Override
    void initUI() {
        // TODO Auto-generated method stub
        init();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(MSG_RESULT==msg.what){
                    setGestureTimes = 0;
                    mTextView.setText(R.string.gesture_setting_note);
                    mTextView.setTextColor(getResources().getColor(R.color.white));
                    gestureLockView.resetView();
                }else if(MSG_OPEN_MENU==msg.what){
                    mHomeActivity.openMenu();
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 初始化
     */
    public void init() {
        mTextView = (TextView) mView.findViewById(R.id.textview);
        btn_gesturereset = (RippleView) mView.findViewById(R.id.btn_gesturereset);
        btn_gestureset = (RippleView) mView.findViewById(R.id.btn_gestureset);
        btn_gesturereset.setOnClickListener(this);
        btn_gestureset.setOnClickListener(this);
        btn_gestureset.setClickable(false);
        gestureLockView = (GestureLockView) mView.findViewById(R.id.gestureLockView);
        //设置密码
        gestureLockView.setSettingMode(true);
        //手势完成后回调
        gestureLockView.setOnGestureFinishListener(new GestureLockView.OnGestureFinishListener() {
            @Override
            public void OnGestureFinish(boolean success, String key) {
                setGestureTimes++;
                btn_gestureset.setClickable(true);
                btn_gestureset.setTextColor(getResources().getColor(R.color.black));
                if (setGestureTimes == 1) {
                    firstkey = key;
                    mTextView.setText(R.string.gesture_setting_note1);
                    gestureLockView.setKey(key);
                    gestureLockView.setSettingMode(false);
                } else if (setGestureTimes == 2) {
                    GestureSettingFragment.this.key = key;
                    if (firstkey.equals(key)) {
                        mTextView.setText(R.string.gesture_setting_note3);
                    } else {
                        mTextView.setTextColor(getResources().getColor(R.color.red));
                        mTextView.setText(R.string.gesture_setting_save_error);
                        Toast.makeText(mContext, getResources().getString(R.string.gesture_setting_save_error), Toast.LENGTH_SHORT).show();
                        btn_gestureset.setClickable(false);
                        btn_gestureset.setTextColor(getResources().getColor(R.color.color_999999));
                        btn_gestureset.setText(getResources().getString(R.string.gesture_setting_set_continue));
                        mHandler.sendEmptyMessageDelayed(MSG_RESULT,2000);
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_gesturereset:
                setGestureTimes = 0;
                mTextView.setText(R.string.gesture_setting_note);
                mTextView.setTextColor(getResources().getColor(R.color.white));
                gestureLockView.resetView();
                break;
            case R.id.btn_gestureset:
                if (setGestureTimes == 1) {
                    mTextView.setText(R.string.gesture_setting_note2);
                    gestureLockView.resetView();
                    btn_gestureset.setClickable(false);
                    btn_gestureset.setTextColor(getResources().getColor(R.color.color_999999));
                    btn_gestureset.setText(getResources().getString(R.string.gesture_setting_set_sure));
                } else if (setGestureTimes == 2) {
                    if (firstkey.equals(key)) {
                        mHomeActivity.localSharedPreferences.edit().putString(HomeActivity.PREFS_GESTURE, key).commit();//将密码保存
                        Toast.makeText(mContext, getResources().getString(R.string.gesture_setting_save_success), Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(MSG_OPEN_MENU,2000);
                    }
                }
                break;
        }
    }
}

package com.roger.screenlocker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import com.roger.screenlocker.welcome.AnimatedMuzeiLogoFragment;

public class WelcomeActivity extends BaseActivity {

    private FrameLayout mFrameLayout;

    private boolean isInit;


    @SuppressLint("NewApi") @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInit = localSharedPreferences.getBoolean(PREFS_IS_INIT, false);
        Handler mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    Intent mIntent = new Intent();
                    if (isInit) {
                        mIntent.setClass(WelcomeActivity.this,
                                HomeActivity.class);//跳转主页面
                    }
                    else {
                        mIntent.setClass(WelcomeActivity.this,
                                InitSysActivity.class);//跳转初始化页面
                    }
                    startActivity(mIntent);
                    overridePendingTransition(R.animator.fade_in,
                            R.animator.fade_out);
                    WelcomeActivity.this.finish();
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setContentView(R.layout.activity_welcome);
            mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            mHandler.sendEmptyMessageDelayed(0, 2500);
            final AnimatedMuzeiLogoFragment logoFragment
                    = (AnimatedMuzeiLogoFragment) getFragmentManager().findFragmentById(
                    R.id.animated_logo_fragment);
            logoFragment.reset();
            mHandler.postDelayed(new Runnable() {
                @Override public void run() {
                    logoFragment.start();
                }
            }, 200);
        }
        else {
            setContentView(R.layout.activity_welcome1);
            mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
            mFrameLayout.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
    }
}

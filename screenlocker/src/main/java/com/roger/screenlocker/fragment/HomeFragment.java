package com.roger.screenlocker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.roger.screenlocker.BaseActivity;
import com.roger.screenlocker.HomeActivity;
import com.roger.screenlocker.R;
import com.roger.screenlocker.render.util.UriUtil;


public class HomeFragment extends BaseFragment {


    private RadioButton radioslide, radiogesture;
    private Handler mHandler;

    @Override
    int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    void initUI() {
        // TODO Auto-generated method stub
        radioslide = (RadioButton) mView.findViewById(R.id.radioslide);
        radiogesture = (RadioButton) mView.findViewById(R.id.radiogesture);

        if (BaseActivity.localSharedPreferences.getBoolean(BaseActivity.PREFS_IS_SLIDE_MODE, true)) {
            radioslide.setChecked(true);
            radiogesture.setChecked(false);
        } else {
            radioslide.setChecked(false);
            radiogesture.setChecked(true);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                toGesture();
            }
        };

        radiogesture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    BaseActivity.localSharedPreferences.edit().putBoolean(BaseActivity.PREFS_IS_SLIDE_MODE, false).commit();
                    if (TextUtils.isEmpty(BaseActivity.localSharedPreferences.getString(HomeActivity.PREFS_GESTURE, ""))) {
                        Toast.makeText(mContext, getResources().getString(R.string.home_notice), Toast.LENGTH_SHORT).show();
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.home_mode_gesture), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        radioslide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(mContext, getResources().getString(R.string.home_mode_slide), Toast.LENGTH_SHORT).show();
                    BaseActivity.localSharedPreferences.edit().putBoolean(BaseActivity.PREFS_IS_SLIDE_MODE, true).commit();//设置滑动解锁
                }
            }
        });

        mView.findViewById(R.id.chose_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mHomeActivity.RESULT_OK) {
            Uri uri = data.getData();
            BaseActivity.mUri = uri;
            BaseActivity.localSharedPreferences.edit().putString(BaseActivity.PREFS_IMAGE_PATH, UriUtil.getImageAbsolutePath(mContext, uri)).commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toGesture() {
        if (getActivity() == null)
            return;
        if (getActivity() instanceof HomeActivity) {
            HomeActivity ra = (HomeActivity) getActivity();
            ra.position = 2;
            ra.toGesture();
        }
    }
}

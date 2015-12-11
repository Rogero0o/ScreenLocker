package com.roger.screenlocker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.roger.screenlocker.BaseActivity;
import com.roger.screenlocker.HomeActivity;
import com.roger.screenlocker.LockScreenService;
import com.roger.screenlocker.R;
import com.roger.screenlocker.render.util.UriUtil;

public class HomeFragment extends BaseFragment {

    private Spinner mSpinner;
    private Handler mHandler;

    final String arr[] = new String[] { "无", "滑动解锁", "手势解锁", "人脸识别" };


    @Override int getLayoutId() {
        return R.layout.fragment_home;
    }


    @Override void initUI() {
        // TODO Auto-generated method stub
        mSpinner = (Spinner) mView.findViewById(R.id.spinner1);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_item, arr);

        mSpinner.setAdapter(arrayAdapter);

        arrayAdapter.setDropDownViewResource(R.layout.spinner_item_textview);

        mSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {//选择无，则关闭service
                            mHomeActivity.localSharedPreferences.edit()
                                                                .putBoolean(
                                                                        mHomeActivity.PREFS_IS_OPEN,
                                                                        false)
                                                                .commit();
                            mHomeActivity.stopService(new Intent(mHomeActivity,
                                    LockScreenService.class));
                            MenuFragment.isOpen = false;
                        }
                        else {//选择方式，则打开service
                            MenuFragment.isOpen = true;
                            mHomeActivity.localSharedPreferences.edit()
                                                                .putBoolean(
                                                                        mHomeActivity.PREFS_IS_OPEN,
                                                                        true)
                                                                .commit();
                            mHomeActivity.startService(new Intent(mHomeActivity,
                                    LockScreenService.class));
                            if (position == 1) {
                                BaseActivity.localSharedPreferences.edit()
                                                                   .putInt(BaseActivity.PREFS_MODE,
                                                                           1)
                                                                   .commit();//设置滑动解锁
                            }
                            else if (position == 2) {
                                BaseActivity.localSharedPreferences.edit()
                                                                   .putInt(BaseActivity.PREFS_MODE,
                                                                           2)
                                                                   .commit();
                                if (TextUtils.isEmpty(
                                        BaseActivity.localSharedPreferences.getString(
                                                HomeActivity.PREFS_GESTURE,
                                                ""))) {
                                    Toast.makeText(mContext,
                                            getResources().getString(
                                                    R.string.home_notice),
                                            Toast.LENGTH_SHORT).show();
                                    mHandler.sendEmptyMessageDelayed(2, 500);
                                }
                            }
                            else if (position == 3) {
                                Toast.makeText(mContext,
                                        getResources().getString(
                                                R.string.home_face_net_notice),
                                        Toast.LENGTH_SHORT).show();
                                BaseActivity.localSharedPreferences.edit()
                                                                   .putInt(BaseActivity.PREFS_MODE,
                                                                           3)
                                                                   .commit();
                                if (TextUtils.isEmpty(
                                        BaseActivity.localSharedPreferences.getString(
                                                HomeActivity.PREFS_FACE_STRING,
                                                ""))) {
                                    Toast.makeText(mContext,
                                            getResources().getString(
                                                    R.string.home_face_notice),
                                            Toast.LENGTH_SHORT).show();
                                    mHandler.sendEmptyMessageDelayed(3, 500);
                                }else{

                                }
                            }
                        }
                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

        if (!BaseActivity.localSharedPreferences.getBoolean(
                mHomeActivity.PREFS_IS_OPEN, false)) {
            mSpinner.setSelection(0);
        }
        else {
            mSpinner.setSelection(BaseActivity.localSharedPreferences.getInt(
                    BaseActivity.PREFS_MODE, 0));
        }

        mHandler = new Handler() {
            @Override public void handleMessage(Message msg) {
                super.handleMessage(msg);
                toGesture(msg.what);
            }
        };

        mView.findViewById(R.id.chose_image)
             .setOnClickListener(new View.OnClickListener() {
                 @Override public void onClick(View v) {
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


    @Override public void onResume() {
        super.onResume();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mHomeActivity.RESULT_OK) {
            Uri uri = data.getData();
            BaseActivity.mUri = uri;
            BaseActivity.localSharedPreferences.edit()
                                               .putString(
                                                       BaseActivity.PREFS_IMAGE_PATH,
                                                       UriUtil.getImageAbsolutePath(
                                                               mContext, uri))
                                               .commit();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void toGesture(int i) {
        if (getActivity() == null) return;
        if (getActivity() instanceof HomeActivity) {
            HomeActivity ra = (HomeActivity) getActivity();
            ra.position = i;
            if (i == 2) {
                ra.toGesture();
            }
            else {
                ra.toFace();
            }
        }
    }
}

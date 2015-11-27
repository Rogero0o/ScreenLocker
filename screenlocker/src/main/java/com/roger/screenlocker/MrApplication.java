package com.roger.screenlocker;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2015/4/30.
 */
public class MrApplication extends Application {
    public static final String PREFS = "locker_pref";
    private final String DIR_NAME = "Mrlocker_img";
    private final String IMAGE_NAME = "night2.jpg";

    @Override
    public void onCreate() {
        super.onCreate();
        BaseActivity.localSharedPreferences = getSharedPreferences(PREFS, 0);
        initImage();
        if (BaseActivity.localSharedPreferences.getBoolean(BaseActivity.PREFS_IS_OPEN, false)) {
            startService(new Intent(this, LockScreenService.class));
        }
    }

    private void initImage() {
        try {
            if (!TextUtils.isEmpty(BaseActivity.localSharedPreferences.getString(BaseActivity.PREFS_IMAGE_PATH, ""))) {//用户设置过
                BaseActivity.mUri = Uri.fromFile(new File(BaseActivity.localSharedPreferences.getString(BaseActivity.PREFS_IMAGE_PATH, "")));
                return;
            }
            File image = new File(getSDPath() + "/" + DIR_NAME + "/" + IMAGE_NAME);
            if (image.exists()) {
                BaseActivity.mUri = Uri.fromFile(image);
                return;
            }

            File file = new File(getSDPath() + "/" + DIR_NAME);
            if (!file.exists()) {
                file.mkdirs();
            }
            assetsDataToSD(getSDPath() + "/" + DIR_NAME + "/" + IMAGE_NAME);
            BaseActivity.mUri = Uri.fromFile(new File(getSDPath() + "/" + DIR_NAME + "/" + IMAGE_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assetsDataToSD(String fileName) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(fileName);
        myInput = this.getAssets().open(IMAGE_NAME);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    public String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist)      //如果SD卡存在，则获取跟目录
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }
}

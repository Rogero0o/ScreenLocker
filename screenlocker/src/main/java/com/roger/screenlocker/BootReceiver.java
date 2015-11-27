package com.roger.screenlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2015/5/5.
 */
public class BootReceiver extends BroadcastReceiver {
    public void onReceive(Context paramContext, Intent paramIntent) {
        if (!paramIntent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            return;
        }
        if (BaseActivity.localSharedPreferences.getBoolean(BaseActivity.PREFS_IS_OPEN, false)) {
            Intent localIntent = new Intent(paramContext, LockScreenService.class);
            paramContext.startService(localIntent);
        }
    }
}

package com.urhive.lockscreendaycountdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Chirag Bhatia on 05-09-2016.
 */
public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.urhive.lockscreendaycountdown", Context.MODE_PRIVATE);
        Intent serviceIntent = new Intent();

        if (sharedPreferences.getInt("toShow", 1) == 1) {
            if (sharedPreferences.getInt("showWhen", 0) == 0) {
                serviceIntent = new Intent(context, LockScreenPhoneService.class);
            } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
                serviceIntent = new Intent(context, LockscreenAfterUnlock.class);
            }
            Log.i("BootReciver", "onReceive: ");
            context.startService(serviceIntent);
        }
    }
}

package com.urhive.lockscreendaycountdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Chirag Bhatia on 05-09-2016.
 */
public class BootReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, LockScreenPhoneService.class);
        Log.i("BootReciver", "onReceive: ");
        context.startService(serviceIntent);
    }
}

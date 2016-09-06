package com.urhive.lockscreendaycountdown;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Chirag Bhatia on 05-09-2016.
 */
public class LockScreenTouchService extends Service {

    protected static final String TAG = "TouchReciever";
    LockScreenStateReceiver mReceiver;

    WindowManager windowManager;
    WindowManager.LayoutParams windowManagerLayoutParams;
    View view;
    boolean isShowing = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.lockscreen_empty, null);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i(TAG, "onTouch: this is touch from button");
                return false;
            }
        });

        windowManagerLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        windowManager.addView(view, windowManagerLayoutParams);

        /*mReceiver = new LockScreenStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, filter);*/
    }

    public class LockScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /*KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (myKM.inKeyguardRestrictedInputMode()) {
                Log.i(TAG, "onReceive: locked");//it is locked
                if (!isShowing) {
                    windowManager.addView(view, windowManagerLayoutParams);
                    isShowing = true;
                }
            } else {
                Log.i(TAG, "onReceive: unlocked");//it is not locked
                if (isShowing) {
                    windowManager.removeViewImmediate(view);
                    isShowing = false;
                }
            }*/

            windowManager.addView(view, windowManagerLayoutParams);
        }
    }
}

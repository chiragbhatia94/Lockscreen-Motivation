package com.urhive.lockscreendaycountdown;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

/**
 * Created by Chirag Bhatia on 25-08-2016.
 */
public class LockScreenPhoneService extends Service {

    WindowManager.LayoutParams params;
    SharedPreferences sharedPreferences;
    private BroadcastReceiver mReceiver;
    private boolean isShowing = false;
    private WindowManager windowManager;
    private View view;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inf = LayoutInflater.from(getApplicationContext());
        sharedPreferences = getSharedPreferences("com.urhive.lockscreendaycountdown", MODE_PRIVATE);

        view = inf.inflate(R.layout.lockscreen_dialog, null);

        RelativeLayout.LayoutParams widgetRLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams textRLParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        RelativeLayout textRL, widgetRL;
        textRL = (RelativeLayout) view.findViewById(R.id.textRL);
        widgetRL = (RelativeLayout) view.findViewById(R.id.heading);

        if (sharedPreferences.getInt("textShow", View.VISIBLE) == View.VISIBLE) {
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(sharedPreferences.getString("lockscreenText", ""));
            // change later for picking up shared preference values
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            textView.setTextColor(Color.WHITE);

            int TextPotraitPostion[] = {sharedPreferences.getInt("textPotraitX", 0),
                    sharedPreferences.getInt("textPotraitY", 0)};

            int TextLandscapePostion[] = {sharedPreferences.getInt("textLandscapeX", 0),
                    sharedPreferences.getInt("textLandscapeY", 0)};

            //set parameters for the textview
            if (windowManager.getDefaultDisplay().getRotation() == Surface.ROTATION_0
                    || windowManager.getDefaultDisplay().getRotation() == Surface.ROTATION_180) {
                textRLParams.leftMargin = TextPotraitPostion[0];
                textRLParams.topMargin = TextPotraitPostion[1];
            } else {
                textRLParams.leftMargin = TextLandscapePostion[0];
                textRLParams.topMargin = TextLandscapePostion[1];
            }

            textRL.setLayoutParams(textRLParams);
        } else {
            textRL.setVisibility(View.GONE);
        }

        if (sharedPreferences.getInt("widgetShow", View.VISIBLE) == View.VISIBLE) {
            TextView textPW = (TextView) view.findViewById(R.id.textPW);
            ProgressWheel pw = (ProgressWheel) view.findViewById(R.id.countdownPW);

            String title = sharedPreferences.getString("pwTitle", "");
            String date = sharedPreferences.getString("pwDate", DateTimeUtil.getCurrentDate());
            String weekDaysToCount = sharedPreferences.getString("pwWeekDaysToSelect", "1234567");
            int targetDays = sharedPreferences.getInt("pwTargetDays", 0);

            int textVisible = sharedPreferences.getInt("pwTitleVisibility", 1);

            if (textVisible != View.VISIBLE) {
                textPW.setVisibility(View.INVISIBLE);
            }

            int colors[] = new int[4];
            colors[0] = sharedPreferences.getInt("pwColorBar", -14776091);
            colors[1] = sharedPreferences.getInt("pwColorCircle", 0);
            colors[2] = sharedPreferences.getInt("pwColorText", -1);
            colors[3] = sharedPreferences.getInt("pwColorRim", -7829368);

            pw.setBarColor(colors[0]);
            pw.setCircleColor(colors[1]);

            pw.setTextColor(colors[2]);
            textPW.setTextColor(colors[2]);

            pw.setRimColor(colors[3]);
            pw.setContourColor(colors[3]);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                pw.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                pw.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }

            int noOfDays = DateTimeUtil.noOfDaysLeft(date, weekDaysToCount);

            textPW.setText(title);

            pw.setText("" + noOfDays);
            float temp = (float) noOfDays / targetDays;
            temp = temp * 360;
            pw.setProgress((int) -temp);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                pw.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                pw.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            }

            int PWPotraitPostion[] = {sharedPreferences.getInt("pwPotraitPositionX", 0),
                    sharedPreferences.getInt("pwPotraitPositionY", 0)};

            int PWLandscapePostion[] = {sharedPreferences.getInt("pwLandscapePositionX", 0),
                    sharedPreferences.getInt("pwLandscapePositionY", 0)};

            //set parameters for the textview
            if (windowManager.getDefaultDisplay().getRotation() == Surface.ROTATION_0
                    || windowManager.getDefaultDisplay().getRotation() == Surface.ROTATION_180) {
                widgetRLParams.leftMargin = PWPotraitPostion[0];
                widgetRLParams.topMargin = PWPotraitPostion[1];
            } else {
                widgetRLParams.leftMargin = PWLandscapePostion[0];
                widgetRLParams.topMargin = PWLandscapePostion[1];
            }

            widgetRL.setLayoutParams(widgetRLParams);
        } else {
            widgetRL.setVisibility(View.GONE);
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                0, 0,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        // params.gravity = Gravity.CENTER;
        params.gravity = Gravity.TOP | Gravity.START;

        //Register receiver for determining screen off and if user is present
        mReceiver = new LockScreenStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //unregister receiver when the service is destroy
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        //remove view if it is showing and the service is destroy
        if (isShowing) {
            windowManager.removeViewImmediate(view);
            isShowing = false;
        }
        super.onDestroy();
    }

    public class LockScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //if screen is turn off show the view
                if (!isShowing) {
                    windowManager.addView(view, params);
                    isShowing = true;
                }
            } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                //Handle resuming events if user is present/screen is unlocked remove the textview immediately
                if (isShowing) {
                    windowManager.removeViewImmediate(view);
                    isShowing = false;
                }
            }
        }
    }
}
package com.urhive.lockscreendaycountdown;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.todddavies.components.progressbar.ProgressWheel;

import java.io.IOException;

public class FullscreenActivity extends AppCompatActivity {
    static int[] colors;
    static int targetDays, noOfDays;
    static String title;
    static ProgressWheel pw;
    static TextView textPW;

    static RelativeLayout mainRL;
    static ViewGroup rootLayout;
    static View.OnTouchListener mainRLTOuchListener;

    static int potraitPosition[] = new int[2];
    static int landscapePosition[] = new int[2];

    RelativeLayout.LayoutParams layoutParams, lParams;

    Drawable drPotrait, drLandscape;

    int _xDelta;
    int _yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View decorView = getWindow().getDecorView();
        setContentView(R.layout.activity_fullscreen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new Runnable() {
            @SuppressLint("InlinedApi")
            @Override
            public void run() {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }.run();

        Bundle b = getIntent().getExtras();
        colors = b.getIntArray("colorArray");
        targetDays = b.getInt("targetDays");
        noOfDays = b.getInt("noOfDays");
        title = b.getString("title");

        pw = (ProgressWheel) findViewById(R.id.countdownPW);
        textPW = (TextView) findViewById(R.id.textPW);

        mainRL = (RelativeLayout) findViewById(R.id.widgetRL);
        rootLayout = (ViewGroup) findViewById(R.id.fullscreen_content);

        potraitPosition[0] = b.getInt("potraitX", 0);
        potraitPosition[1] = b.getInt("potraitY", 0);

        landscapePosition[0] = b.getInt("landscapeX", 0);
        landscapePosition[1] = b.getInt("landscapeY", 0);

        mainRLTOuchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int X = (int) event.getRawX();
                final int Y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        _xDelta = X - lParams.leftMargin;
                        _yDelta = Y - lParams.topMargin;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                            mainRL.getLocationOnScreen(potraitPosition);

                            Log.i("LocationOnScreen", "Potrait x, y: " + potraitPosition[0] + ", " + potraitPosition[1]);
                        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            mainRL.getLocationOnScreen(landscapePosition);

                            Log.i("LocationOnScreen", "Landcape x, y: " + landscapePosition[0] + ", " + landscapePosition[1]);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        layoutParams.leftMargin = X - _xDelta;
                        layoutParams.topMargin = Y - _yDelta;
                        layoutParams.rightMargin = -80;
                        layoutParams.bottomMargin = -80;
                        v.setLayoutParams(layoutParams);
                        break;
                }

                rootLayout.invalidate();
                return false;
            }
        };

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            params.leftMargin = potraitPosition[0];
            params.topMargin = potraitPosition[1];
        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            params.leftMargin = landscapePosition[0];
            params.topMargin = landscapePosition[1];
        }

        mainRL.setLayoutParams(params);

        setColor();
        setValues();

        mainRL.setOnTouchListener(mainRLTOuchListener);
    }

    // for changing colors
    void setColor() {
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
    }

    void setValues() {
        textPW.setText(title);

        pw.setText("" + noOfDays);
        float temp = (float) noOfDays / targetDays;
        temp = temp * 360;
        pw.setProgress((int) -temp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            pw.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pw.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }

        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            if (drPotrait != null)
                rootLayout.setBackgroundDrawable(drPotrait);
        } else {
            if (drLandscape != null)
                rootLayout.setBackgroundDrawable(drLandscape);
        }
    }

    public void done(View view) {
        Intent intent = new Intent();
        intent.putExtra("potraitX", potraitPosition[0]);
        intent.putExtra("potraitY", potraitPosition[1]);
        intent.putExtra("landscapeX", landscapePosition[0]);
        intent.putExtra("landscapeY", landscapePosition[1]);
        setResult(RESULT_OK, intent);
        finish();

        Log.i("LocationOnScreen", "Potrait x, y: " + potraitPosition[0] + ", " + potraitPosition[1]);
        Log.i("LocationOnScreen", "Landcape x, y: " + landscapePosition[0] + ", " + landscapePosition[1]);
    }

    public void applyWallpaper(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final View decorView = getWindow().getDecorView();
        new Runnable() {
            @SuppressLint("InlinedApi")
            @Override
            public void run() {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }.run();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    drPotrait = new BitmapDrawable(image);
                    rootLayout.setBackgroundDrawable(drPotrait);
                } else {
                    drLandscape = new BitmapDrawable(image);

                    rootLayout.setBackgroundDrawable(drLandscape);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeOrientation(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            params.leftMargin = landscapePosition[0];
            params.topMargin = landscapePosition[1];
        } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            params.leftMargin = potraitPosition[0];
            params.topMargin = potraitPosition[1];
        }
        mainRL.setLayoutParams(params);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        RelativeLayout.LayoutParams params;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_fullscreen);

            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = landscapePosition[0];
            params.topMargin = landscapePosition[1];

        } else {
            setContentView(R.layout.activity_fullscreen);

            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = potraitPosition[0];
            params.topMargin = potraitPosition[1];
        }

        pw = (ProgressWheel) findViewById(R.id.countdownPW);
        textPW = (TextView) findViewById(R.id.textPW);

        mainRL = (RelativeLayout) findViewById(R.id.widgetRL);
        rootLayout = (ViewGroup) findViewById(R.id.fullscreen_content);

        mainRL.setLayoutParams(params);

        mainRL.setOnTouchListener(mainRLTOuchListener);

        setColor();
        setValues();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final View decorView = getWindow().getDecorView();

        new Runnable() {
            @SuppressLint("InlinedApi")
            @Override
            public void run() {
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }.run();
    }
}

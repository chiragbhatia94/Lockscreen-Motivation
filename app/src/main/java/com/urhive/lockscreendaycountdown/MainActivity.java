package com.urhive.lockscreendaycountdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.todddavies.components.progressbar.ProgressWheel;

public class MainActivity extends AppCompatActivity {

    TextView textPW;
    CheckBox widgetShowCB, textShowCB;
    EditText lockscreenTextET;

    ProgressWheel pw;

    SharedPreferences sharedPreferences;
    String title = "";
    String date = "";
    String weekDaysToCount = "1234567";
    int targetDays = 7;

    int textVisible;

    int colors[];

    int noOfDays = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.urhive.lockscreendaycountdown", MODE_PRIVATE);

        Intent intent = new Intent(this, LockScreenPhoneService.class);
        startService(intent);

        textPW = (TextView) findViewById(R.id.textPW);

        pw = (ProgressWheel) findViewById(R.id.countdownPW);
        lockscreenTextET = (EditText) findViewById(R.id.lockscreenTextET);

        lockscreenTextET.setText(sharedPreferences.getString("lockscreenText", ""));

        widgetShowCB = (CheckBox) findViewById(R.id.widgetShow);
        if (sharedPreferences.getInt("widgetShow", View.VISIBLE) != View.VISIBLE) {
            widgetShowCB.setChecked(false);
        }

        textShowCB = (CheckBox) findViewById(R.id.textShow);
        if (sharedPreferences.getInt("textShow", View.VISIBLE) != View.VISIBLE) {
            textShowCB.setChecked(false);
        }

        /*
        * Map map = new HashMap();
        * map = sharedPreferences.getAll();
        * System.out.println(map.toString());
        */

        title = sharedPreferences.getString("pwTitle", "");
        date = sharedPreferences.getString("pwDate", DateTimeUtil.getCurrentDate());
        weekDaysToCount = sharedPreferences.getString("pwWeekDaysToSelect", "1234567");
        targetDays = sharedPreferences.getInt("pwTargetDays", 0);

        textVisible = sharedPreferences.getInt("pwTitleVisibility", View.VISIBLE);

        colors = new int[4];
        colors[0] = sharedPreferences.getInt("pwColorBar", -14776091);
        colors[1] = sharedPreferences.getInt("pwColorCircle", 0);
        colors[2] = sharedPreferences.getInt("pwColorText", -1);
        colors[3] = sharedPreferences.getInt("pwColorRim", -7829368);

        if (textVisible != View.VISIBLE) {
            textPW.setVisibility(View.INVISIBLE);
        }

        setColor();
        setValues();

        lockscreenTextET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedPreferences.edit().putString("lockscreenText", lockscreenTextET.getText().toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LockScreenPhoneService.class);
                stopService(intent);
                startService(intent);
                Toast.makeText(MainActivity.this, "Service Restarted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

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
        noOfDays = DateTimeUtil.noOfDaysLeft(date, weekDaysToCount);

        textPW.setText(title);

        pw.setText("" + noOfDays);
        float temp = (float) noOfDays / targetDays;
        temp = temp * 360;
        pw.setProgress((int) -temp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            pw.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pw.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    public void editWidget(View view) {
        Intent intent = new Intent(MainActivity.this, CreateDayCountdown.class);
        startActivity(intent);
    }

    public void setWidgetVisibility(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            sharedPreferences.edit().putInt("widgetShow", View.VISIBLE).apply();
        } else {
            sharedPreferences.edit().putInt("widgetShow", View.GONE).apply();
        }

        Intent intent = new Intent(MainActivity.this, LockScreenPhoneService.class);
        stopService(intent);
        startService(intent);
    }

    public void setTextVisibility(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            sharedPreferences.edit().putInt("textShow", View.VISIBLE).apply();
        } else {
            sharedPreferences.edit().putInt("textShow", View.GONE).apply();
        }
        Intent intent = new Intent(MainActivity.this, LockScreenPhoneService.class);
        stopService(intent);
        startService(intent);
    }

    public void repositionText(View view) {
        if (lockscreenTextET.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter some text to reposition!", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, RepositionTextActivity.class);
            intent.putExtra("color", Color.WHITE);
            intent.putExtra("size", 24);
            startActivity(intent);
        }
    }
}

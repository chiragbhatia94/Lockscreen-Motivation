package com.urhive.lockscreendaycountdown;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.todddavies.components.progressbar.ProgressWheel;

import java.util.Calendar;
import java.util.Date;

public class CreateDayCountdown extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_day_countdown);

        ProgressWheel pw = (ProgressWheel) findViewById(R.id.countdownPW);

        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();

        Calendar gate = Calendar.getInstance();
        gate.set(Calendar.YEAR, 2017);
        gate.set(Calendar.MONTH, 1);
        gate.set(Calendar.DATE, 4);
        Date gateDate = gate.getTime();

        long diff = gateDate.getTime() - today.getTime();
        float day =  (diff / 1000 / 60 / 60 / 24);
        pw.setText("" + (int)day);
        float x = day / 180;
        float y = x * 360;
        pw.setProgress((int)-y);
    }

    public void reposition(View view) {
        Intent intent = new Intent(CreateDayCountdown.this, RepositionActivity.class);
        startActivity(intent);
    }
}

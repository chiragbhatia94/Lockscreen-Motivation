package com.urhive.lockscreendaycountdown;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.todddavies.components.progressbar.ProgressWheel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

public class CreateDayCountdown extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    static ProgressWheel pw;
    static String date = "";
    static int noOfDays = 0;
    static int targetDays = 7;
    static String weekDaysToCount = "1234567";
    static String title = "";
    static int textVisible = View.VISIBLE;
    // for color picker
    static boolean showAccent = false;
    static int colors[] = new int[4];
    static int accentPreselect, primaryPreselect;
    static ImageView selectedIV;
    static int potraitPosition[] = new int[2];
    static int landscapePosition[] = new int[2];
    TextView dateTV, textPW, defaultColorTV;
    EditText titleET, targetET;
    CheckBox titleShown;
    ImageView colorInCircleTextIV, colorRimIV, colorBarIV, colorBackgroundIV;
    CheckBox sundayCB, mondayCB, tuesdayCB, wednesdayCB, thursdayCB, fridayCB, saturdayCB;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_day_countdown);
        defaultColorTV = (TextView) findViewById(R.id.defaultColorTV);
        pw = (ProgressWheel) findViewById(R.id.countdownPW);
        textPW = (TextView) findViewById(R.id.textPW);
        titleET = (EditText) findViewById(R.id.titleET);

        titleShown = (CheckBox) findViewById(R.id.titleCB);
        targetET = (EditText) findViewById(R.id.fullCircleET);

        dateTV = (TextView) findViewById(R.id.setDateTV);

        colorInCircleTextIV = (ImageView) findViewById(R.id.textColorIV);
        colorRimIV = (ImageView) findViewById(R.id.rimContourColorIV);
        colorBarIV = (ImageView) findViewById(R.id.barColorIV);
        colorBackgroundIV = (ImageView) findViewById(R.id.circleColorIV);

        sundayCB = (CheckBox) findViewById(R.id.sundayCB);
        mondayCB = (CheckBox) findViewById(R.id.mondayCB);
        tuesdayCB = (CheckBox) findViewById(R.id.tuesdayCB);
        wednesdayCB = (CheckBox) findViewById(R.id.wednesdayCB);
        thursdayCB = (CheckBox) findViewById(R.id.thursdayCB);
        fridayCB = (CheckBox) findViewById(R.id.fridayCB);
        saturdayCB = (CheckBox) findViewById(R.id.saturdayCB);

        /*colors[0] = ContextCompat.getColor(CreateDayCountdown.this, R.color.defaultPWBarColor);
        colors[1] = Color.TRANSPARENT;
        colors[2] = Color.WHITE;
        colors[3] = Color.GRAY;*/

        // using stored values
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.urhive.lockscreendaycountdown", MODE_PRIVATE);
        /*
        * Map map = new HashMap();
        * map = sharedPreferences.getAll();
        * System.out.println(map.toString());
        */

        title = sharedPreferences.getString("pwTitle", "");
        date = sharedPreferences.getString("pwDate", DateTimeUtil.getCurrentDate());
        weekDaysToCount = sharedPreferences.getString("pwWeekDaysToSelect", "1234567");
        targetDays = sharedPreferences.getInt("pwTargetDays", 0);

        textVisible = sharedPreferences.getInt("pwTitleVisibility", 1);

        colors = new int[4];
        colors[0] = sharedPreferences.getInt("pwColorBar", -14776091);
        colors[1] = sharedPreferences.getInt("pwColorCircle", 0);
        colors[2] = sharedPreferences.getInt("pwColorText", -1);
        colors[3] = sharedPreferences.getInt("pwColorRim", -7829368);
        setColor();

        potraitPosition[0] = sharedPreferences.getInt("pwPotraitPositionX", 0);
        potraitPosition[1] = sharedPreferences.getInt("pwPotraitPositionY", 0);

        landscapePosition[0] = sharedPreferences.getInt("pwLandscapePositionX", 0);
        landscapePosition[1] = sharedPreferences.getInt("pwLandscapePositionY", 0);


        dateTV.setText(date);
        textPW.setText(title);
        titleET.setText(title);
        if (textVisible != View.VISIBLE) {
            titleShown.setChecked(false);
            textPW.setVisibility(View.INVISIBLE);
        }
        targetET.setText(String.valueOf(targetDays));

        colorBarIV.setBackgroundColor(colors[0]);
        colorBackgroundIV.setBackgroundColor(colors[1]);
        colorInCircleTextIV.setBackgroundColor(colors[2]);
        colorRimIV.setBackgroundColor(colors[3]);

        if (!weekDaysToCount.contains("1")) {
            sundayCB.setChecked(false);
        }
        if (!weekDaysToCount.contains("2")) {
            mondayCB.setChecked(false);
        }
        if (!weekDaysToCount.contains("3")) {
            tuesdayCB.setChecked(false);
        }
        if (!weekDaysToCount.contains("4")) {
            wednesdayCB.setChecked(false);
        }
        if (!weekDaysToCount.contains("5")) {
            thursdayCB.setChecked(false);
        }
        if (!weekDaysToCount.contains("6")) {
            fridayCB.setChecked(false);
        }
        if (!weekDaysToCount.contains("7")) {
            saturdayCB.setChecked(false);
        }

        refreshValues();

        // calling shared preferences

        targetET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String x = targetET.getText().toString();
                if (!x.isEmpty()) {
                    int temp = Integer.parseInt(x);
                    if (temp < noOfDays) {
                        targetET.setError("Target days cannot be less than total number of days i.e. " + noOfDays + "!");
                    } else {
                        targetDays = temp;
                    }
                } else {
                    targetET.setError("Enter any number of target days!");
                }

                refreshValues();
                return false;
            }
        });

        titleET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title = titleET.getText().toString();
                textPW.setText(title);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void reposition(View view) {
        Intent intent = new Intent(CreateDayCountdown.this, FullscreenActivity.class);
        intent.putExtra("colorArray", colors);
        intent.putExtra("targetDays", targetDays);
        intent.putExtra("noOfDays", noOfDays);
        intent.putExtra("title", title);
        intent.putExtra("potraitX", potraitPosition[0]);
        intent.putExtra("potraitY", potraitPosition[1]);
        intent.putExtra("landscapeX", landscapePosition[0]);
        intent.putExtra("landscapeY", landscapePosition[1]);
        startActivityForResult(intent, 0);
    }

    public void setDate(View view) {
        String d[] = date.split("/");
        int day = Integer.parseInt(d[0]);
        int month = Integer.parseInt(d[1]);
        int year = Integer.parseInt(d[2]);

        DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++;
                date = DateTimeUtil.getDate(dayOfMonth, monthOfYear, year);

                dateTV.setText(date);

                refreshValues();
            }
        }, year, month - 1, day);

        dpd.setMinDate(Calendar.getInstance());

        dpd.show(getFragmentManager(), "datepickerdialog");
    }

    void refreshValues() {
        noOfDays = DateTimeUtil.noOfDaysLeft(date, weekDaysToCount);

        if (targetDays < noOfDays) {
            targetDays = noOfDays;
            targetET.setText(String.valueOf(targetDays));
        }

        pw.setText("" + noOfDays);
        float temp = (float) noOfDays / targetDays;
        temp = temp * 360;
        pw.setProgress((int) -temp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            pw.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            pw.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    public void checkDayToCount(View view) {
        CheckBox cb = (CheckBox) view;
        int c = Integer.parseInt(String.valueOf(cb.getTag()));
        if (cb.isChecked()) {
            if (!weekDaysToCount.contains("" + c)) {
                weekDaysToCount = weekDaysToCount + "" + c;
            }
        } else {
            if (weekDaysToCount.contains("" + c)) {
                String[] temp = weekDaysToCount.split(String.valueOf(c));
                weekDaysToCount = TextUtils.join("", temp);
            }
        }

        refreshValues();
    }

    public void showTitle(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            textPW.setVisibility(View.VISIBLE);
            textVisible = View.VISIBLE;
        } else {
            textPW.setVisibility(View.INVISIBLE);
            textVisible = View.INVISIBLE;
        }
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

    public void changeColor(View view) {
        int tag = Integer.parseInt(view.getTag().toString());
        primaryPreselect = colors[tag];
        selectedIV = (ImageView) view;

        new ColorChooserDialog.Builder(CreateDayCountdown.this, R.string.pick_color)
                .titleSub(R.string.shade)  // title of dialog when viewing shades of a color
                .accentMode(showAccent)  // when true, will display accent palette instead of primary palette
                .doneButton(R.string.md_done_label)  // changes label of the done button
                .cancelButton(R.string.md_cancel_label)  // changes label of the cancel button
                .backButton(R.string.md_back_label)  // changes label of the back button
                .preselect(showAccent ? accentPreselect : primaryPreselect)  // optionally preselects a color
                .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .show();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        primaryPreselect = selectedColor;
        // String hexColor = String.format("#%08X", (0xFFFFFFFF & selectedColor));
        selectedIV.setBackgroundColor(primaryPreselect);
        colors[Integer.parseInt(String.valueOf(selectedIV.getTag()))] = primaryPreselect;
        setColor();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            potraitPosition[0] = data.getIntExtra("potraitX", 0);
            potraitPosition[1] = data.getIntExtra("potraitY", 0);

            landscapePosition[0] = data.getIntExtra("landscapeX", 0);
            landscapePosition[1] = data.getIntExtra("landscapeY", 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_day_countdown_menu, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(CreateDayCountdown.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            SharedPreferences sharedPreferences = getSharedPreferences("com.urhive.lockscreendaycountdown", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("pwDate", date);
            editor.putString("pwWeekDaysToSelect", weekDaysToCount);
            editor.putString("pwTitle", title);
            editor.putInt("pwTargetDays", targetDays);

            editor.putInt("pwColorBar", colors[0]);
            editor.putInt("pwColorCircle", colors[1]);
            editor.putInt("pwColorText", colors[2]);
            editor.putInt("pwColorRim", colors[3]);

            editor.putInt("pwPotraitPositionX", potraitPosition[0]);
            editor.putInt("pwPotraitPositionY", potraitPosition[1]);

            editor.putInt("pwLandscapePositionX", landscapePosition[0]);
            editor.putInt("pwLandscapePositionY", landscapePosition[1]);

            editor.putInt("pwTitleVisibility", textVisible);

            editor.apply();

            Toast.makeText(CreateDayCountdown.this, "Done!", Toast.LENGTH_SHORT).show();

            Intent intentService = new Intent(CreateDayCountdown.this, LockScreenPhoneService.class);
            stopService(intentService);
            startService(intentService);

            Intent intent = new Intent(CreateDayCountdown.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

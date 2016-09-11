package com.urhive.lockscreendaycountdown;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.todddavies.components.progressbar.ProgressWheel;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 128;
    protected String TAG = "MainActivity";
    TextView textPW;
    CheckBox widgetShowCB, textShowCB, quoteShowCB;
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

        sharedPreferences = this.getSharedPreferences("com.urhive.lockscreendaycountdown",
                MODE_PRIVATE);

        checkDrawOverlayPermission();

        Boolean firstRun = sharedPreferences.getBoolean("firstRun", true);

        /**Intent intentIntro = new Intent(MainActivity.this, LDCIntro.class);
         startActivity(intentIntro);*/

        if (firstRun) {
            sharedPreferences.edit().putBoolean("firstRun", false).apply();
            sharedPreferences.edit().putInt("showWhen", 1).apply();
            sharedPreferences.edit().putInt("toShow", 1).apply();

            Log.i(TAG, "firstRun: creating quotation database");

            DBHelper dbHelper = new DBHelper(getApplicationContext());
            try {
                dbHelper.createDataBase();

                if (dbHelper.checkDataBase()) {
                    Log.i(TAG, "onCreate: Database copied!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(MainActivity.this, LDCIntro.class);
            startActivity(intent);
        }

        System.out.println(Arrays.toString(DBHelper.getRandomQuote(getApplicationContext(), 50)));


        Intent serviceIntent = new Intent();
        if (sharedPreferences.getInt("showWhen", 0) == 0) {
            serviceIntent = new Intent(this, LockScreenPhoneService.class);
        } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
            serviceIntent = new Intent(this, LockscreenAfterUnlock.class);
        }
        startService(serviceIntent);

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

        quoteShowCB = (CheckBox) findViewById(R.id.quoteShow);
        if (sharedPreferences.getInt("quoteShow", View.VISIBLE) != View.VISIBLE) {
            quoteShowCB.setChecked(false);
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
                sharedPreferences.edit().putString("lockscreenText", lockscreenTextET.getText()
                        .toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // sharedPreferences.edit().putString("lockscreenText", lockscreenTextET.getText
                // ().toString()).apply();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPreferences.getInt("showWhen", 0) == 0) {
                    Intent intentPhone = new Intent(MainActivity.this, LockScreenPhoneService
                            .class);
                    stopService(intentPhone);
                    startService(intentPhone);
                } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
                    Intent intentAfter = new Intent(MainActivity.this, LockscreenAfterUnlock.class);
                    stopService(intentAfter);
                    startService(intentAfter);
                }

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

    // settings visibility of widget, text & quote

    public void setWidgetVisibility(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            sharedPreferences.edit().putInt("widgetShow", View.VISIBLE).apply();
        } else {
            sharedPreferences.edit().putInt("widgetShow", View.GONE).apply();
        }

        if (sharedPreferences.getInt("showWhen", 0) == 0) {
            Intent intentPhone = new Intent(this, LockScreenPhoneService.class);
            stopService(intentPhone);
            startService(intentPhone);
        } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
            Intent intentAfter = new Intent(this, LockscreenAfterUnlock.class);
            stopService(intentAfter);
            startService(intentAfter);
        }
    }

    public void setTextVisibility(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            sharedPreferences.edit().putInt("textShow", View.VISIBLE).apply();
        } else {
            sharedPreferences.edit().putInt("textShow", View.GONE).apply();
        }

        if (sharedPreferences.getInt("showWhen", 0) == 0) {
            Intent intentPhone = new Intent(this, LockScreenPhoneService.class);
            stopService(intentPhone);
            startService(intentPhone);
        } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
            Intent intentAfter = new Intent(this, LockscreenAfterUnlock.class);
            stopService(intentAfter);
            startService(intentAfter);
        }
    }

    public void setQuoteVisibility(View view) {
        CheckBox cb = (CheckBox) view;
        if (cb.isChecked()) {
            sharedPreferences.edit().putInt("quoteShow", View.VISIBLE).apply();
        } else {
            sharedPreferences.edit().putInt("quoteShow", View.GONE).apply();
        }

        if (sharedPreferences.getInt("showWhen", 0) == 0) {
            Intent intentPhone = new Intent(this, LockScreenPhoneService.class);
            stopService(intentPhone);
            startService(intentPhone);
        } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
            Intent intentAfter = new Intent(this, LockscreenAfterUnlock.class);
            stopService(intentAfter);
            startService(intentAfter);
        }
    }

    // reposition text & quote
    public void repositionText(View view) {
        if (lockscreenTextET.getText().toString().isEmpty()) {
            Toast.makeText(MainActivity.this, "Enter some text to reposition!", Toast
                    .LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(MainActivity.this, RepositionTextActivity.class);
            intent.putExtra("color", sharedPreferences.getInt("textColor", Color.WHITE));
            intent.putExtra("size", sharedPreferences.getInt("textSize", 24));
            startActivity(intent);
        }
    }

    public void repositionQuote(View view) {
        Intent intent = new Intent(MainActivity.this, RepositionQuoteActivity.class);
        intent.putExtra("color", sharedPreferences.getInt("quoteColor", Color.WHITE));
        intent.putExtra("size", sharedPreferences.getInt("quoteSize", 18));

        intent.putExtra("colorAuthor", sharedPreferences.getInt("authorColor", Color.WHITE));
        intent.putExtra("sizeAuthor", sharedPreferences.getInt("authorSize", 16));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        /*
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white),
                 PorterDuff.Mode.SRC_ATOP);
            }
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_show) {
            // 0 on lockscreen 1 after
            if (sharedPreferences.getInt("showWhen", 0) == 1) {
                sharedPreferences.edit().putInt("showWhen", 0).apply();
                Intent intentPhone = new Intent(this, LockScreenPhoneService.class);
                Intent intentAfter = new Intent(this, LockscreenAfterUnlock.class);
                stopService(intentAfter);
                startService(intentPhone);
                Toast.makeText(MainActivity.this, "On Lockscreen!", Toast.LENGTH_SHORT).show();
            } else if (sharedPreferences.getInt("showWhen", 0) == 0) {
                sharedPreferences.edit().putInt("showWhen", 1).apply();
                Intent intentPhone = new Intent(this, LockScreenPhoneService.class);
                Intent intentAfter = new Intent(this, LockscreenAfterUnlock.class);
                stopService(intentPhone);
                startService(intentAfter);
                Toast.makeText(MainActivity.this, "After Lockscreen!", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_off) {
            // 1 to show 0 not to show
            if (sharedPreferences.getInt("toShow", 1) == 0) {
                sharedPreferences.edit().putInt("toShow", 1).apply();
                if (sharedPreferences.getInt("showWhen", 0) == 0) {
                    Intent intentPhone = new Intent(MainActivity.this, LockScreenPhoneService
                            .class);
                    stopService(intentPhone);
                    startService(intentPhone);
                } else if (sharedPreferences.getInt("showWhen", 0) == 1) {
                    Intent intentAfter = new Intent(MainActivity.this, LockscreenAfterUnlock.class);
                    stopService(intentAfter);
                    startService(intentAfter);
                }
                Toast.makeText(this, "Show", Toast.LENGTH_SHORT).show();
                item.setChecked(true);
            } else if (sharedPreferences.getInt("toShow", 1) == 1) {
                sharedPreferences.edit().putInt("toShow", 0).apply();
                Intent intentPhone = new Intent(this, LockScreenPhoneService.class);
                Intent intentAfter = new Intent(this, LockscreenAfterUnlock.class);
                stopService(intentPhone);
                stopService(intentAfter);
                Toast.makeText(this, "Not Show", Toast.LENGTH_SHORT).show();
                item.setChecked(false);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkDrawOverlayPermission() {
        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this)
                        .setTitle("Screen Overlay Permission Required!")
                        .setMessage("Lockscreen Motivation app requires screen overlay permission" +
                                " for displaying" +
                                " data on lockscreen!")
                        .setPositiveButton("Grant Permission", new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /** if not construct intent to request permission */
                                Intent intent = new Intent(Settings
                                        .ACTION_MANAGE_OVERLAY_PERMISSION,
                                        Uri.parse("package:" + getPackageName()));
                                /** request permission via start activity for result */
                                startActivityForResult(intent, REQUEST_CODE);
                            }
                        })
                        .setCancelable(false);
                alert.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /** check if received result code
         is equal our requested code for draw permission  */
        if (requestCode == REQUEST_CODE) {
            // ** if so check once again if we have permission */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // continue here - permission was granted
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent
                            .FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }
    }
}

package com.urhive.lockscreendaycountdown;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

public class LDCIntro extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        askForPermissions(new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.SYSTEM_ALERT_WINDOW}, 4);

        addSlide(AppIntro2Fragment.newInstance(getString(R.string.intro),
                getString(R.string.app_description), R.drawable.intro1, ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_500)));

        addSlide(AppIntro2Fragment.newInstance("Define a Goal", "Set a goal date!\nGet! Set! Go!", R.drawable.intro2, ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_500)));
        addSlide(AppIntro2Fragment.newInstance("Quote", "See a new quote everytime you check your phone!", R.drawable.intro3, ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_500)));
        addSlide(AppIntro2Fragment.newInstance("Show Custom Text on Lockscreen", "Want to add something other than \nowner's info to the lockscreen!", R.drawable.intro3, ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_500)));
        addSlide(AppIntro2Fragment.newInstance("Fly", "All the best for your awesome journey!", R.drawable.intro4, ContextCompat.getColor(getApplicationContext(), R.color.blue_grey_500)));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}

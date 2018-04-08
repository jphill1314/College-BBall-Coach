package com.coaching.jphil.collegebasketballcoach;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.coaching.jphil.collegebasketballcoach.fragments.StartScreenFragments.StartScreenFragment;

public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.frame_layout, new StartScreenFragment()).commit();

        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.orangeDark));
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }
}

package com.coaching.jphil.collegebasketballcoach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.fragments.StartScreenFragments.StartScreenFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StartScreenFragments.TeamCreatorFragment;

public class StartScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.frame_layout, new StartScreenFragment()).commit();

    }
}

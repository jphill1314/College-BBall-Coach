package com.coaching.jphil.collegebasketballcoach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.fragments.TeamCreatorFragment;

import static android.view.View.GONE;

public class StartScreenActivity extends AppCompatActivity {

    private Button newGame, loadGame;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        newGame = findViewById(R.id.new_game_button);
        loadGame = findViewById(R.id.load_game_button);
        title = findViewById(R.id.title_text);


        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newGame.setVisibility(GONE);
                loadGame.setVisibility(GONE);
                title.setVisibility(GONE);
                
                android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
                t.replace(R.id.frame_layout, new TeamCreatorFragment()).commit();
            }
        });
    }
}

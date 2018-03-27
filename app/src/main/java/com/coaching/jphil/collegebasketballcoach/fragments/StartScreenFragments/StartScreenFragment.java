package com.coaching.jphil.collegebasketballcoach.fragments.StartScreenFragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.StartScreenActivity;
import com.crashlytics.android.Crashlytics;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartScreenFragment extends Fragment {


    public StartScreenFragment() {
        // Required empty public constructor
    }

    Button newGame, loadGame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_screen, container, false);
        newGame = view.findViewById(R.id.new_game_button);
        loadGame = view.findViewById(R.id.load_game_button);


        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();
                t.replace(R.id.frame_layout, new TeamCreatorFragment()).commit();
            }
        });

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        if(prefs.getBoolean(getString(R.string.shared_pref_never_opened), true)){
            loadGame.setEnabled(false);
        }
        else{
            loadGame.setEnabled(true);
        }

        return view;
    }

}

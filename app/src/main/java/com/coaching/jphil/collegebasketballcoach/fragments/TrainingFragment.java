package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {


    public TrainingFragment() {
        // Required empty public constructor
    }

    SeekBar seekOffense, seekPerimeter, seekSkills;

    MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_training, container, false);

        activity = (MainActivity) getActivity();

        seekOffense = view.findViewById(R.id.s_offense);
        seekPerimeter = view.findViewById(R.id.s_perimeter);
        seekSkills = view.findViewById(R.id.s_skills);

        if(activity.currentTeam.isPlayerControlled()) {
            setupSeekBars();
        }

        return view;
    }

    private void setupSeekBars(){
        seekOffense.setProgress(activity.currentTeam.getOffenseFocus());
        seekPerimeter.setProgress(activity.currentTeam.getPerimeterFocus());
        seekSkills.setProgress(activity.currentTeam.getSkillFocus());

        seekOffense.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.currentTeam.setOffenseFocus(progress);
            }
        });

        seekPerimeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.currentTeam.setPerimeterFocus(progress);
            }
        });

        seekSkills.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.currentTeam.setSkillFocus(progress);
            }
        });
    }

}

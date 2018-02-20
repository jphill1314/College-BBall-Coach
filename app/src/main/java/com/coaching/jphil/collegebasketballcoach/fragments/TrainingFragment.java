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

        setupSeekBars();

        return view;
    }

    private void setupSeekBars(){
        seekOffense.setProgress(activity.teams[activity.playerTeamIndex].getOffenseFocus());
        seekPerimeter.setProgress(activity.teams[activity.playerTeamIndex].getPerimeterFocus());
        seekSkills.setProgress(activity.teams[activity.playerTeamIndex].getSkillFocus());

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
                activity.teams[activity.playerTeamIndex].setOffenseFocus(progress);
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
                activity.teams[activity.playerTeamIndex].setPerimeterFocus(progress);
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
                activity.teams[activity.playerTeamIndex].setSkillFocus(progress);
            }
        });
    }

}

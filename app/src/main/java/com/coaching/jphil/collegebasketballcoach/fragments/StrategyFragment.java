package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

/**
 * A simple {@link Fragment} subclass.
 */
public class StrategyFragment extends Fragment {


    public StrategyFragment() {
        // Required empty public constructor
    }

    MainActivity mainActivity;
    Team team;
    SeekBar offThree, defThree, agro, pace;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_strategy, container, false);

        mainActivity = (MainActivity) getActivity();
        team = mainActivity.currentTeam;
        offThree = view.findViewById(R.id.seek_offense_three);
        defThree = view.findViewById(R.id.seek_defense_three);
        agro = view.findViewById(R.id.seek_agro);
        pace = view.findViewById(R.id.seek_pace);

        offThree.setProgress(team.getOffenseFavorsThrees() - 25);
        defThree.setProgress(team.getDefenseFavorsThrees() - 25);
        agro.setProgress(team.getAggression() + 10);
        pace.setProgress((int) ((team.getPace() - 55) / 35.0 * 100.0));


        offThree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int offThreeProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                offThreeProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                offThreeProgress = offThreeProgress + 25;
                team.setOffenseFavorsThrees(offThreeProgress);
            }
        });

        defThree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int defThreeProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                defThreeProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                defThreeProgress = defThreeProgress + 25;
                team.setDefenseFavorsThrees(defThreeProgress);
            }
        });

        agro.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int aggression = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                aggression = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                team.setAggression(aggression - 10);
            }
        });

        pace.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int paceProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                paceProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // pace can be between 55 and 90
                paceProgress = (int)((paceProgress / 100.0) * 35 + 55);
                team.setPace(paceProgress);
            }
        });

        return view;
    }

}

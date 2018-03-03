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
    SeekBar offThree, defThree, defHelp, pace;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_strategy, container, false);

        mainActivity = (MainActivity) getActivity();
        team = mainActivity.currentTeam;
        offThree = view.findViewById(R.id.seek_offense_three);
        defThree = view.findViewById(R.id.seek_defense_three);
        defHelp = view.findViewById(R.id.seek_help);
        pace = view.findViewById(R.id.seek_pace);

        offThree.setProgress((int) (team.getOffenseFavorsThrees() / 70.0 * 100.0 - 30));
        defThree.setProgress((int) (team.getDefenseFavorsThrees() / 70.0 * 100.0 - 30));
        defHelp.setProgress((int) (team.getDefenseTendToHelp() / 70.0 * 100.0 - 30));
        pace.setProgress((int) ((team.getPace() - 50) / 30.0 * 100.0));


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
                offThreeProgress = (int) (((offThreeProgress + 30) / 100.0) * 70);
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
                defThreeProgress = (int) (((defThreeProgress + 30) / 100.0) * 70);
                team.setDefenseFavorsThrees(defThreeProgress);
            }
        });

        defHelp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int defHelpProgress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                defHelpProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                defHelpProgress = (int) (((defHelpProgress + 30) / 100.0) * 70);
                team.setDefenseTendToHelp(defHelpProgress);
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
                paceProgress = (int)((paceProgress / 100.0) * 30 + 50);

                team.setPace(paceProgress);
            }
        });

        return view;
    }

}

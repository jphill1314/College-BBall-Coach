package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerInfoFragment extends Fragment {


    public PlayerInfoFragment() {
        // Required empty public constructor
    }

    private int playerIndex;
    private TextView closeShot, midShot, longShot, ballHandle, pass, screen;
    private TextView postDef, perimDef, onBall, offBall, steal, rebound;
    private TextView stamina;
    private TextView playerName, playerMinutes, teamMinutes;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_player_info, container, false);
        mainActivity = (MainActivity) getActivity();

        Bundle args = getArguments();
        if(args != null){
            playerIndex = args.getInt("player");
        }

        closeShot = view.findViewById(R.id.close_shot);
        midShot = view.findViewById(R.id.mid_shot);
        longShot = view.findViewById(R.id.long_shot);
        ballHandle = view.findViewById(R.id.ball_handle);
        pass = view.findViewById(R.id.passing);
        screen = view.findViewById(R.id.screen);

        postDef = view.findViewById(R.id.post_def);
        perimDef = view.findViewById(R.id.perim_def);
        onBall = view.findViewById(R.id.on_ball);
        offBall = view.findViewById(R.id.off_ball);
        steal = view.findViewById(R.id.stealing);
        rebound = view.findViewById(R.id.rebound);

        stamina = view.findViewById(R.id.stamina);

        playerName = view.findViewById(R.id.player_name);
        playerMinutes = view.findViewById(R.id.minutes_text);
        teamMinutes = view.findViewById(R.id.team_minutes);

        setAttributes();

        SeekBar minutes = view.findViewById(R.id.minutes_seek);
        minutes.setProgress(mainActivity.currentTeam.getPlayers().get(playerIndex).getMinutes());
        minutes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mainActivity.currentTeam.getPlayers().get(playerIndex).setMinutes(i);
                playerMinutes.setText(getResources().getString(R.string.player_minutes,
                        mainActivity.currentTeam.getPlayers().get(playerIndex).getMinutes()));
                teamMinutes.setText(getResources().getString(R.string.team_minutes,
                        200 -mainActivity.currentTeam.getTotalMinutes()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    private void setAttributes(){
        Player player = mainActivity.currentTeam.getPlayers().get(playerIndex);

        closeShot.setText(getResources().getString(R.string.close_shot, player.getCloseRangeShot()));
        midShot.setText(getResources().getString(R.string.mid_shot, player.getMidRangeShot()));
        longShot.setText(getResources().getString(R.string.long_shot, player.getLongRangeShot()));
        ballHandle.setText(getResources().getString(R.string.ball_handle, player.getBallHandling()));
        pass.setText(getResources().getString(R.string.passing, player.getLongRangeShot()));
        screen.setText(getResources().getString(R.string.screening, player.getBallHandling()));

        postDef.setText(getResources().getString(R.string.post_def, player.getCloseRangeShot()));
        perimDef.setText(getResources().getString(R.string.perim_def, player.getMidRangeShot()));
        onBall.setText(getResources().getString(R.string.on_ball, player.getLongRangeShot()));
        offBall.setText(getResources().getString(R.string.off_ball, player.getBallHandling()));
        steal.setText(getResources().getString(R.string.steal, player.getLongRangeShot()));
        rebound.setText(getResources().getString(R.string.rebound, player.getBallHandling()));

        stamina.setText(getResources().getString(R.string.stamina, player.getStamina()));

        playerName.setText(getResources().getString(R.string.player_name_pos, player.getFullName(), player.getYearAsString(), player.getPositionAbr()));
        playerMinutes.setText(getResources().getString(R.string.player_minutes, player.getMinutes()));
        teamMinutes.setText(getResources().getString(R.string.team_minutes, 200 - mainActivity.currentTeam.getTotalMinutes()));
    }

}

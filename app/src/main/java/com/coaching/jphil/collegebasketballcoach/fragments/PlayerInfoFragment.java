package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private TextView gamesPlayed, totalMinutes;
    private TextView playerName;

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


        gamesPlayed = view.findViewById(R.id.games_played);
        totalMinutes = view.findViewById(R.id.total_minutes);

        setAttributes();

        return view;
    }

    private void setAttributes(){
        Player player = mainActivity.currentTeam.getPlayers().get(playerIndex);

        closeShot.setText(getResources().getString(R.string.close_shot, player.getCloseRangeShot()));
        midShot.setText(getResources().getString(R.string.mid_shot, player.getMidRangeShot()));
        longShot.setText(getResources().getString(R.string.long_shot, player.getLongRangeShot()));
        ballHandle.setText(getResources().getString(R.string.ball_handle, player.getBallHandling()));
        pass.setText(getResources().getString(R.string.passing, player.getPassing()));
        screen.setText(getResources().getString(R.string.screening, player.getScreening()));

        postDef.setText(getResources().getString(R.string.post_def, player.getPostDefense()));
        perimDef.setText(getResources().getString(R.string.perim_def, player.getPerimeterDefense()));
        onBall.setText(getResources().getString(R.string.on_ball, player.getOnBallDefense()));
        offBall.setText(getResources().getString(R.string.off_ball, player.getOffBallDefense()));
        steal.setText(getResources().getString(R.string.steal, player.getStealing()));
        rebound.setText(getResources().getString(R.string.rebound, player.getRebounding()));

        stamina.setText(getResources().getString(R.string.stamina, player.getStamina()));

        playerName.setText(getResources().getString(R.string.player_name_pos, player.getFullName(), player.getYearAsString(), player.getPositionAbr()));

        gamesPlayed.setText(getResources().getString(R.string.games_played, player.getGamesPlayed()));
        totalMinutes.setText(getResources().getString(R.string.total_minutes, player.getTotalMinutes()));
    }
}

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
    private TextView playerName, playerMinutes, teamMinutes;

    private ImageView star1, star2, star3, star4, star5;

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

        gamesPlayed = view.findViewById(R.id.games_played);
        totalMinutes = view.findViewById(R.id.total_minutes);

        star1 = view.findViewById(R.id.player_star_1);
        star2 = view.findViewById(R.id.player_star_2);
        star3 = view.findViewById(R.id.player_star_3);
        star4 = view.findViewById(R.id.player_star_4);
        star5 = view.findViewById(R.id.player_star_5);

        setAttributes();

        SeekBar minutes = view.findViewById(R.id.minutes_seek);
        if(mainActivity.currentTeam.isPlayerControlled()) {
            minutes.setProgress(mainActivity.currentTeam.getPlayers().get(playerIndex).getMinutes());
            minutes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    mainActivity.currentTeam.getPlayers().get(playerIndex).setMinutes(i);
                    playerMinutes.setText(getResources().getString(R.string.player_minutes,
                            mainActivity.currentTeam.getPlayers().get(playerIndex).getMinutes()));
                    teamMinutes.setText(getResources().getString(R.string.team_minutes,
                            200 - mainActivity.currentTeam.getTotalMinutes()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        else{
            view.findViewById(R.id.seek_layout).setVisibility(View.GONE);
        }

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

        gamesPlayed.setText(getResources().getString(R.string.games_played, player.getGamesPlayed()));
        totalMinutes.setText(getResources().getString(R.string.total_minutes, player.getTotalMinutes()));

        setStarRating(player.getOverallRating());
    }

    private void setStarRating(int rating){
        switch (getStarRating(rating)){
            case "0.5":
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "1":
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "1.5":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "2":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "2.5":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "3":
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "3.5":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "4":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
            case "4.5":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star5.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half_black_24dp));
                break;
            case "5":
                star1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star4.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                star5.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
                break;
        }
    }

    private String getStarRating(int rating){
        String stars;

        stars = Integer.toString(rating / 20);
        if(rating % 20 > 9){
            stars += ".5";
        }

        return stars;
    }
}

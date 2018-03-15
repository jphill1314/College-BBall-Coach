package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.GameAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Thread.sleep;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {


    public GameFragment() {
        // Required empty public constructor
    }

    private Game game;
    private int gameIndex;
    private int gameSpeed = 10;
    private boolean isInTimeout = false;

    private Button startGame, callTime;
    private TextView homeScore, awayScore, half, time, homeTO, awayTO, homeFouls, awayFouls;
    private SeekBar gameSpeedBar;
    //private Spinner spinner;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        final MainActivity activity = (MainActivity) getActivity();
        if(args != null){
            gameIndex = args.getInt("game");
            game = activity.getPlayerConference().getMasterSchedule().get(gameIndex);

        }

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        startGame = view.findViewById(R.id.start_game);
        callTime = view.findViewById(R.id.call_timeout);
        callTime.setText(getString(R.string.end_timeout));

        homeScore = view.findViewById(R.id.home_score);
        awayScore = view.findViewById(R.id.away_score);

        homeTO = view.findViewById(R.id.home_to);
        awayTO = view.findViewById(R.id.away_to);

        homeFouls = view.findViewById(R.id.home_fouls);
        awayFouls = view.findViewById(R.id.away_fouls);

        homeScore.setText(getString(R.string.scores, game.getHomeTeam().getFullName(), game.getHomeScore()));
        awayScore.setText(getString(R.string.scores, game.getAwayTeam().getFullName(), game.getAwayScore()));

        homeTO.setText(getString(R.string.timeout_left, 4));
        awayTO.setText(getString(R.string.timeout_left, 4));

        homeFouls.setText(getString(R.string.team_fouls, 0));
        awayFouls.setText(getString(R.string.team_fouls, 0));

        half = view.findViewById(R.id.current_half);
        time = view.findViewById(R.id.current_time);
        time.setText("20:00");

        half.setText(getString(R.string.half, 1));

        recyclerView = view.findViewById(R.id.game_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new GameAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.timeout_array))), 1);
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);

        gameSpeedBar = view.findViewById(R.id.game_speed_bar);
        gameSpeedBar.setProgress(gameSpeed);
        gameSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                gameSpeed = 26 - i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame.setVisibility(View.GONE);
                callTime.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                new SimGame().execute();
            }
        });

        callTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInTimeout) {
                    callTime.setText(getString(R.string.timeout));

                    if(game.getHomeTeam().isPlayerControlled()) {
                        game.coachTalk(game.getHomeTeam(), !game.getIsNeutralCourt(), game.getHomeScore()-game.getAwayScore(), ((GameAdapter)adapter).getSelectedValue());
                        game.coachTalk(game.getAwayTeam(), false, game.getAwayScore()-game.getHomeScore());
                    }
                    else{
                        game.coachTalk(game.getHomeTeam(), !game.getIsNeutralCourt(), game.getHomeScore()-game.getAwayScore());
                        game.coachTalk(game.getAwayTeam(), false, game.getAwayScore()-game.getHomeScore(), ((GameAdapter)adapter).getSelectedValue());
                    }

                    ((GameAdapter) adapter).setPlays(game.getPlays());
                    ((GameAdapter) adapter).setDisplayType(0);
                    adapter.notifyDataSetChanged();
                    isInTimeout = false;
                }
                else if(game.getPlayerWantsTO()){
                    game.setPlayerWantsTO(false);
                    callTime.setText(R.string.timeout);
                }
                else {
                    game.setPlayerWantsTO(true);
                    callTime.setText(getString(R.string.end_timeout));
                }
            }
        });

        return view;
    }


    private class SimGame extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            game.setSavePlays(true);
            game.preGameSetUp();
        }

        @Override
        protected String doInBackground(String... strings) {
            do{
                int playResult = game.simPlay();
                while(playResult != -1){
                    if(playResult == 1) {
                        isInTimeout = true;
                        publishProgress();
                        while(isInTimeout){
                            try {
                                sleep(500);
                            }
                            catch (InterruptedException e){
                                Log.e("sleep error", e.toString());
                            }
                        }
                    }
                    else{
                        publishProgress();
                        try{
                            sleep(gameSpeed * 100);
                        }
                        catch (InterruptedException e){
                            Log.e("sleep error", e.toString());
                        }
                    }
                    playResult = game.simPlay();
                }
            }while(game.startNextHalf());
            game.setIsPlayed(true);
            game.getHomeTeam().playGame(game.homeTeamWin());
            game.getAwayTeam().playGame(!game.homeTeamWin());

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress){
            updateUI();

            if(isInTimeout){
                callTimeout();
            }
        }

        @Override
        protected void onPostExecute(String results){
            updateUI();
        }

        private void updateUI(){
            homeScore.setText(getString(R.string.scores, game.getHomeTeam().getFullName(), game.getHomeScore()));
            awayScore.setText(getString(R.string.scores, game.getAwayTeam().getFullName(), game.getAwayScore()));

            homeFouls.setText(getString(R.string.team_fouls, game.getHomeFouls()));
            awayFouls.setText(getString(R.string.team_fouls, game.getAwayFouls()));

            half.setText(getString(R.string.half, game.getHalf()));
            time.setText(game.getFormattedTime());

            ((GameAdapter) adapter).setPlays(game.getPlays());
            adapter.notifyDataSetChanged();
        }
    }

    private void callTimeout(){
        ((GameAdapter)adapter).setPlays(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.timeout_array))));
        ((GameAdapter)adapter).setDisplayType(1);
        adapter.notifyDataSetChanged();

        callTime.setText(getString(R.string.end_timeout));
        homeTO.setText(getString(R.string.timeout_left, game.getHomeTimeouts()));
        awayTO.setText(getString(R.string.timeout_left, game.getAwayTimeouts()));
    }
}

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

    private Button startGame, callTime;
    private TextView homeScore, awayScore, half, time;
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
            Log.d("game", ""+gameIndex);
            game = activity.getPlayerConference().getMasterSchedule().get(gameIndex);

        }

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        startGame = view.findViewById(R.id.start_game);
        callTime = view.findViewById(R.id.call_timeout);

        homeScore = view.findViewById(R.id.home_score);
        awayScore = view.findViewById(R.id.away_score);

        homeScore.setText(getString(R.string.scores, game.getHomeTeam().getFullName(), game.getHomeScore()));
        awayScore.setText(getString(R.string.scores, game.getAwayTeam().getFullName(), game.getAwayScore()));

        half = view.findViewById(R.id.current_half);
        time = view.findViewById(R.id.current_time);
        time.setText("20:00");

        half.setText(getString(R.string.half, 1));

        recyclerView = view.findViewById(R.id.game_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new GameAdapter(null);
        recyclerView.setAdapter(adapter);

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
                startGame.setText(getString(R.string.start_2nd));
                new SimGame().execute();
            }
        });

        return view;
    }


    private class SimGame extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            game.preGameSetUp();
            do{
                while(game.simPlay()){
                    try{
                        publishProgress();
                        sleep(gameSpeed * 100);
                    }catch(InterruptedException e){
                        Log.e("Error", e.toString());
                    }
                }
            }while(game.startNextHalf());
            game.setIsPlayed(true);
            game.getHomeTeam().playGame(game.homeTeamWin());
            game.getAwayTeam().playGame(!game.homeTeamWin());

            return null;
        }

        @Override
        protected void onProgressUpdate(String... progress){
            homeScore.setText(getString(R.string.scores, game.getHomeTeam().getFullName(), game.getHomeScore()));
            awayScore.setText(getString(R.string.scores, game.getAwayTeam().getFullName(), game.getAwayScore()));

            half.setText(getString(R.string.half, game.getHalf()));
            time.setText(game.getFormattedTime());

            ((GameAdapter) adapter).setPlays(game.getPlays());
            adapter.notifyDataSetChanged();
        }
    }
}

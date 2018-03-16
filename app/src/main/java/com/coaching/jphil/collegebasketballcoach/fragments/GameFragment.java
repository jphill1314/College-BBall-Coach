package com.coaching.jphil.collegebasketballcoach.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.GameAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.GameRosterAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.GameSpeechAdapter;
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
    private boolean forceSub = false;
    private boolean alertDeadBall = false;

    private FloatingActionButton fab;
    private TextView homeScore, awayScore, half, time, homeTO, awayTO, homeFouls, awayFouls, deadBall, gameSpeedText;
    private SeekBar gameSpeedBar;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private GameRosterAdapter grAdapter;
    private boolean updateGRA = false;
    private int adapterType = 0;

    private SimGame gameAsync;

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

        homeScore = view.findViewById(R.id.home_score);
        awayScore = view.findViewById(R.id.away_score);

        homeTO = view.findViewById(R.id.home_to);
        awayTO = view.findViewById(R.id.away_to);

        homeFouls = view.findViewById(R.id.home_fouls);
        awayFouls = view.findViewById(R.id.away_fouls);

        gameSpeedText = view.findViewById(R.id.speed_text);
        deadBall = view.findViewById(R.id.alert_dead_ball);

        homeScore.setText(getString(R.string.scores, game.getHomeTeam().getFullName(), game.getHomeScore()));
        awayScore.setText(getString(R.string.scores, game.getAwayTeam().getFullName(), game.getAwayScore()));

        homeTO.setText(getString(R.string.timeout_left, 4));
        awayTO.setText(getString(R.string.timeout_left, 4));

        homeFouls.setText(getString(R.string.team_fouls, 0));
        awayFouls.setText(getString(R.string.team_fouls, 0));

        half = view.findViewById(R.id.current_half);
        time = view.findViewById(R.id.current_time);
        time.setText("20:00 (30)");

        half.setText(getString(R.string.half, 1));

        fab = view.findViewById(R.id.game_fab);

        recyclerView = view.findViewById(R.id.game_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new GameAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.timeout_array))));
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);

        gameSpeedBar = view.findViewById(R.id.game_speed_bar);
        gameSpeedBar.setProgress(gameSpeed);

        spinner = view.findViewById(R.id.game_spinner);
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getSpinnerList());
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(0, false);
        spinner.setVisibility(View.INVISIBLE);

        if(game.getHomeTeam().isPlayerControlled()){
            grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers());
        }
        else{
            grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers());
        }

        setClickListeners();

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(gameAsync != null){
            gameAsync.cancel(true);
        }
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
                    else if(playResult == 2){
                        alertDeadBall = true;
                        publishProgress();
                        while(alertDeadBall){
                            try {
                                sleep(500);
                            }
                            catch (InterruptedException e){
                                Log.e("sleep error", e.toString());
                            }
                        }
                    }
                    else if(playResult == 3){
                        forceSub = true;
                        publishProgress();
                        while(forceSub){
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

            if(forceSub){
                forceSub();
            }

            if(alertDeadBall){
                alertDeadBall();
                alertFoulTrouble();
            }

            if(isInTimeout){
                callTimeout();
                alertFoulTrouble();
            }

            if(updateGRA){
                updateGRA();
            }
        }

        @Override
        protected void onPostExecute(String results){
            updateUI();
            gameAsync = null;
        }

        private void updateUI(){
            homeScore.setText(getString(R.string.scores, game.getHomeTeam().getFullName(), game.getHomeScore()));
            awayScore.setText(getString(R.string.scores, game.getAwayTeam().getFullName(), game.getAwayScore()));

            homeFouls.setText(getString(R.string.team_fouls, game.getHomeFouls()));
            awayFouls.setText(getString(R.string.team_fouls, game.getAwayFouls()));

            half.setText(getString(R.string.half, game.getHalf()));
            time.setText(getString(R.string.game_time, game.getFormattedTime(), game.getShotClock()));

            if(adapterType == 0) {
                ((GameAdapter) adapter).setPlays(game.getPlays());
                adapter.notifyDataSetChanged();
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void forceSub(){
        if(game.getHomeTeam().isPlayerControlled()){
            changeAdapters(2);
        }
        else{
            changeAdapters(3);
        }

        Toast.makeText(getContext(), R.string.fouled_out, Toast.LENGTH_LONG).show();
    }

    private void callTimeout(){
        changeAdapters(3);

        String[] array = new String[getSpinnerList().length+1];
        for(int x = 0 ; x < getSpinnerList().length; x++){
            array[x] = getSpinnerList()[x];
        }
        array[array.length-1] = "Team speech";
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, array);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(3, true);

        gameSpeedBar.setVisibility(View.INVISIBLE);
        gameSpeedText.setVisibility(View.INVISIBLE);
        deadBall.setVisibility(View.VISIBLE);
        deadBall.setText(getString(R.string.timeout_instruction));

        homeTO.setText(getString(R.string.timeout_left, game.getHomeTimeouts()));
        awayTO.setText(getString(R.string.timeout_left, game.getAwayTimeouts()));
    }

    private void alertDeadBall(){
        gameSpeedBar.setVisibility(View.INVISIBLE);
        gameSpeedText.setVisibility(View.INVISIBLE);
        deadBall.setVisibility(View.VISIBLE);
        deadBall.setText(getString(R.string.alert_dead_ball));

        spinner.setSelection(0, true);

        changeAdapters(0);
    }

    private void alertFoulTrouble(){
        boolean showToast = false;
        int half = game.getHalf();
        int time = game.getTimeRemaining();

        if(game.getHomeTeam().isPlayerControlled()){
            for(int x = 0; x < 5; x++){
                if(game.getHomeTeam().getPlayers().get(x).isInFoulTrouble(half, time)){
                    showToast = true;
                    break;
                }
            }
        }
        else{
            for(int x = 0; x < 5; x++){
                if(game.getAwayTeam().getPlayers().get(x).isInFoulTrouble(half, time)){
                    showToast = true;
                    break;
                }
            }
        }

        if(showToast){
            Toast.makeText(getContext(), getString(R.string.alert_foul_trouble), Toast.LENGTH_LONG).show();
        }
    }

    private String[] getSpinnerList(){
        return new String[]{
                "Play-by-Play",
                game.getHomeTeamName() + " Roster",
                game.getAwayTeamName() + " Roster"
        };
    }

    private void setClickListeners(){
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!forceSub) {
                    changeAdapters(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapterType == 0){
                    if(gameAsync == null){
                        gameAsync = new SimGame();
                        gameAsync.execute();

                        spinner.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    else if(alertDeadBall){
                        alertDeadBall = false;
                        gameSpeedBar.setVisibility(View.VISIBLE);
                        gameSpeedText.setVisibility(View.VISIBLE);
                        deadBall.setVisibility(View.INVISIBLE);
                        updateGRA = true;

                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
                    }
                    else if(game.getPlayerWantsTO()){
                        game.setPlayerWantsTO(false);
                    }
                    else {
                        game.setPlayerWantsTO(true);
                    }
                }
                else if(adapterType == 1 || adapterType == 2){
                    int[] indexes = ((GameRosterAdapter)adapter).getIndexes();
                    boolean success = false;
                    if(indexes[0] != -1 && indexes[1] != -1){
                        if(game.getHomeTeam().isPlayerControlled()){
                            success = game.getHomeTeam().updateSubs(indexes[0], indexes[1]);
                        }
                        else{
                            success = game.getAwayTeam().updateSubs(indexes[0], indexes[1]);
                        }
                    }

                    if(forceSub && success){
                        forceSub = false;
                    }
                }
                else if(adapterType == 3) {
                    if (game.getHomeTeam().isPlayerControlled()) {
                        game.coachTalk(game.getHomeTeam(), !game.getIsNeutralCourt(), game.getHomeScore() - game.getAwayScore(), ((GameSpeechAdapter) adapter).getSelectedValue());
                        game.coachTalk(game.getAwayTeam(), false, game.getAwayScore() - game.getHomeScore());
                    } else {
                        game.coachTalk(game.getHomeTeam(), !game.getIsNeutralCourt(), game.getHomeScore() - game.getAwayScore());
                        game.coachTalk(game.getAwayTeam(), false, game.getAwayScore() - game.getHomeScore(), ((GameSpeechAdapter) adapter).getSelectedValue());
                    }
                    spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getSpinnerList());
                    spinner.setAdapter(spinnerAdapter);
                    spinner.setSelection(0, true);

                    gameSpeedBar.setVisibility(View.VISIBLE);
                    gameSpeedText.setVisibility(View.VISIBLE);
                    deadBall.setVisibility(View.INVISIBLE);

                    updateGRA = true;

                    isInTimeout = false;
                    changeAdapters(0);
                }

            }
        });
    }

    private void updateGRA(){
        updateGRA = false;
        if(game.getHomeTeam().isPlayerControlled()) {
            grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers());
        }
        else{
            grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers());
        }
    }

    private void changeAdapters(int type){
        if(type == 0){
            adapter = new GameAdapter(new ArrayList<>(game.getPlays()));
            if(gameAsync == null){
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
            else if(alertDeadBall){
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
            else {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_black_24dp));
            }
            fab.setVisibility(View.VISIBLE);
        }
        else if(type == 1){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
            if(game.getHomeTeam().isPlayerControlled()){
                fab.setVisibility(View.VISIBLE);
                adapter = grAdapter;
            }
            else{
                fab.setVisibility(View.GONE);
                adapter = new GameRosterAdapter(game.getHomeTeam().getPlayers());
            }

        }
        else if(type == 2){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
            if(game.getAwayTeam().isPlayerControlled()){
                fab.setVisibility(View.VISIBLE);
                adapter = grAdapter;
            }
            else{
                fab.setVisibility(View.GONE);
                adapter = new GameRosterAdapter(game.getAwayTeam().getPlayers());
            }
        }
        else if(type == 3){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
            fab.setVisibility(View.VISIBLE);
            if(game.getTimeRemaining() == 20 * 60 || (game.getHalf() > 2 && game.getTimeRemaining() == 5 * 60)){
                adapter = new GameSpeechAdapter(getResources().getStringArray(R.array.pre_game_speeches));
                deadBall.setText(getString(R.string.pre_game_instruction));
            }
            else {
                adapter = new GameSpeechAdapter(getResources().getStringArray(R.array.timeout_array));
            }
        }


        adapterType = type;
        recyclerView.setAdapter(adapter);
    }
}

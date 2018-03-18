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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.GameAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.GameRosterAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.GameSpeechAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;
import java.util.Arrays;

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
    private TextView homeScore, awayScore, half, time, homeTO, awayTO, homeFouls, awayFouls;
    private TextView deadBall, gameSpeedText, tvIntentFoul;
    private SeekBar gameSpeedBar;
    private Spinner spinner;
    private ToggleButton tbIntentFoul;
    private FrameLayout frame;
    private View strategyView;
    private ArrayAdapter<String> spinnerAdapter;

    private SeekBar offThrees, defThrees, pace, agro;
    private int pendingOffThrees, pendingDefThrees, pendingPace, pendingAgro;

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
            game = activity.masterSchedule.get(gameIndex);

        }

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        frame = view.findViewById(R.id.game_frame_layout);
        strategyView = inflater.inflate(R.layout.fragment_strategy, container, false);

        homeScore = view.findViewById(R.id.home_score);
        awayScore = view.findViewById(R.id.away_score);

        homeTO = view.findViewById(R.id.home_to);
        awayTO = view.findViewById(R.id.away_to);

        homeFouls = view.findViewById(R.id.home_fouls);
        awayFouls = view.findViewById(R.id.away_fouls);

        gameSpeedText = view.findViewById(R.id.speed_text);
        deadBall = view.findViewById(R.id.alert_dead_ball);

        offThrees = strategyView.findViewById(R.id.seek_offense_three);
        defThrees = strategyView.findViewById(R.id.seek_defense_three);
        pace = strategyView.findViewById(R.id.seek_pace);
        agro = strategyView.findViewById(R.id.seek_agro);
        tvIntentFoul = strategyView.findViewById(R.id.tv_foul);
        tbIntentFoul = strategyView.findViewById(R.id.foul_button);

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

        recyclerView = (RecyclerView) inflater.inflate(R.layout.game_list_view, container, false);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        adapter = new GameAdapter(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.timeout_array))));
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);
        frame.addView(recyclerView);

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
        setStrategyView();

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
            changeAdapters(1);
            spinner.setSelection(1, true);
        }
        else{
            changeAdapters(2);
            spinner.setSelection(2, true);
        }

        Toast.makeText(getContext(), R.string.fouled_out, Toast.LENGTH_LONG).show();
    }

    private void callTimeout(){
        changeAdapters(4);

        String[] array = new String[getSpinnerList().length+1];
        for(int x = 0 ; x < getSpinnerList().length; x++){
            array[x] = getSpinnerList()[x];
        }
        array[array.length-1] = "Team speech";
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, array);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(4, true);

        gameSpeedBar.setVisibility(View.INVISIBLE);
        gameSpeedText.setVisibility(View.INVISIBLE);
        deadBall.setText(getString(R.string.timeout_instruction));
        deadBall.setVisibility(View.VISIBLE);

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

        Team playerTeam;
        if(game.getHomeTeam().isPlayerControlled()){
            playerTeam = game.getHomeTeam();
        }
        else{
            playerTeam = game.getAwayTeam();
        }

        if(pendingPace > 0) {
            playerTeam.setPace(pendingPace);
        }
        if(pendingAgro > 0) {
            playerTeam.setAggression(pendingAgro);
        }
        if(pendingOffThrees > 0){
            playerTeam.setOffenseFavorsThrees(pendingOffThrees);
        }
        if(pendingDefThrees > 0) {
            playerTeam.setDefenseFavorsThrees(pendingDefThrees);
        }

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
                game.getAwayTeamName() + " Roster",
                "Team Strategy"
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
                        spinner.setSelection(0, true);
                        changeAdapters(0);
                    }
                }
                else if(adapterType == 4) {
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
                    deadBall.setText(getString(R.string.alert_dead_ball));

                    updateGRA = true;

                    isInTimeout = false;
                    changeAdapters(0);
                }

            }
        });
    }

    private void setStrategyView(){
        Team playerTeam;
        if(game.getHomeTeam().isPlayerControlled()){
           playerTeam = game.getHomeTeam();
        }
        else{
            playerTeam = game.getAwayTeam();
        }

        pendingOffThrees = playerTeam.getOffenseFavorsThrees();
        pendingDefThrees = playerTeam.getDefenseFavorsThrees();
        pendingAgro = playerTeam.getAggression();
        pendingPace = playerTeam.getPace();

        offThrees.setProgress((int) (pendingOffThrees / 70.0 * 100.0 - 30));
        defThrees.setProgress((int) (pendingDefThrees / 70.0 * 100.0 - 30));
        agro.setProgress(pendingAgro + 10);
        pace.setProgress((int) ((pendingPace - 55) / 35.0 * 100.0));

        offThrees.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int offThreeProgress;
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
                pendingOffThrees = offThreeProgress;
            }
        });

        defThrees.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int defThreeProgress;
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
                pendingDefThrees = defThreeProgress;
            }
        });

        agro.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int agroProgress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                agroProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pendingAgro = agroProgress - 10;
            }
        });

        pace.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int paceProgress;
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
                pendingPace = paceProgress;
            }
        });

        tbIntentFoul.setChecked(false);
        tbIntentFoul.setVisibility(View.VISIBLE);
        tvIntentFoul.setVisibility(View.VISIBLE);

        tbIntentFoul.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            game.setPlayerIntentFoul(isChecked);
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
        if(adapterType == 3 && type != 3){
            frame.removeView(strategyView);
            frame.addView(recyclerView);

            fab.setVisibility(View.VISIBLE);
            gameSpeedText.setVisibility(View.VISIBLE);
            gameSpeedBar.setVisibility(View.VISIBLE);
            deadBall.setVisibility(View.INVISIBLE);
        }

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
            frame.removeView(recyclerView);
            frame.addView(strategyView);

            fab.setVisibility(View.INVISIBLE);
            gameSpeedText.setVisibility(View.GONE);
            gameSpeedBar.setVisibility(View.GONE);
            deadBall.setVisibility(View.GONE);
        }
        else if(type == 4){
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

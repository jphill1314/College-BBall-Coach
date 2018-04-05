package com.coaching.jphil.collegebasketballcoach.fragments;


import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.GameStatsDB;
import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.GameAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.GameRosterAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.GameSpeechAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.GameEvent;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.activeCount;
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
    private int gameSpeed = 5;
    private boolean isInTimeout = false;
    private boolean forceSub = false;
    private boolean alertDeadBall = false;
    private boolean showDeadBallAlerts;
    private boolean pauseSim = false;

    public FloatingActionButton fab;
    private TextView homeScore, awayScore, half, time, homeTO, awayTO, homeFouls, awayFouls, homeName, awayName;
    private TextView deadBall, gameSpeedText, tvIntentFoul, posLabel, label1, label2, label3, label4;
    private TextView h2FG, h3FG, hFT, hA, hOB, hDB, hS, hTO, a2FG, a3FG, aFT, aA, aOB, aDB, aS, aTO, hName, aName;
    private SeekBar gameSpeedBar;
    private Spinner spinner, rosterSpinner;
    private ToggleButton tbIntentFoul;
    private FrameLayout frame;
    private View strategyView, gameRosterView, teamStatsView;
    private ArrayAdapter<String> spinnerAdapter, rosterSpinnerAdapter;

    private SeekBar offThrees, defThrees, pace, agro;
    private int pendingOffThrees, pendingDefThrees, pendingPace, pendingAgro;

    private RecyclerView recyclerView, rosterRecycler;
    private RecyclerView.LayoutManager manager, rosterManager;
    private RecyclerView.Adapter adapter;
    private GameRosterAdapter grAdapter;
    private boolean updateGRA = false;
    private int adapterType = 0;
    private boolean[] displayPlay;

    private ArrayList<GameStatsDB> stats;

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
        activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        activity.actionBar.setDisplayHomeAsUpEnabled(false);
        activity.actionBar.setTitle("Game Day");

        View view = inflater.inflate(R.layout.fragment_game, container, false);

        frame = view.findViewById(R.id.game_frame_layout);
        strategyView = inflater.inflate(R.layout.fragment_strategy, container, false);
        gameRosterView = inflater.inflate(R.layout.game_roster_view, container, false);
        teamStatsView = inflater.inflate(R.layout.game_team_stats_view, container, false);

        createTeamStatsView();

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

        homeScore.setText(getString(R.string.scores, game.getHomeScore()));
        awayScore.setText(getString(R.string.scores, game.getAwayScore()));

        homeTO.setText(getString(R.string.timeout_left, 4));
        awayTO.setText(getString(R.string.timeout_left, 4));

        homeFouls.setText(getString(R.string.team_fouls, 0));
        awayFouls.setText(getString(R.string.team_fouls, 0));

        half = view.findViewById(R.id.current_half);
        time = view.findViewById(R.id.current_time);
        time.setText("20:00 (30)");

        half.setText(getString(R.string.half, 1));

        fab = view.findViewById(R.id.game_fab);
        if(game.getHomeTeam().isPlayerControlled()){
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(game.getHomeTeam().getColorLight())));
        }
        else{
            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(game.getAwayTeam().getColorLight())));
        }

        recyclerView = (RecyclerView) inflater.inflate(R.layout.game_list_view, container, false);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        rosterSpinner = gameRosterView.findViewById(R.id.game_roster_spinner);
        rosterSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.game_roster_spinner));
        rosterSpinner.setAdapter(rosterSpinnerAdapter);


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


        rosterRecycler = gameRosterView.findViewById(R.id.roster_list);
        rosterManager = new LinearLayoutManager(getContext());
        rosterRecycler.setLayoutManager(rosterManager);

        homeName = view.findViewById(R.id.home_team_name);
        homeName.setText(game.getHomeTeam().getMascot());
        awayName = view.findViewById(R.id.away_team_name);
        awayName.setText(game.getAwayTeam().getMascot());

        posLabel = gameRosterView.findViewById(R.id.pos_label);
        label1 = gameRosterView.findViewById(R.id.label1);
        label2 = gameRosterView.findViewById(R.id.label2);
        label3 = gameRosterView.findViewById(R.id.label3);
        label4 = gameRosterView.findViewById(R.id.label4);

        if(game.getHomeTeam().isPlayerControlled()){
            grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers(), 0, this);
        }
        else{
            grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers(), 0, this);
        }

        setClickListeners();
        setStrategyView();

        setHasOptionsMenu(true);

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        displayPlay = new boolean[]{prefs.getBoolean(getString(R.string.shared_pref_disp_scoring), true),
                prefs.getBoolean(getString(R.string.shared_pref_disp_cop), true),
                prefs.getBoolean(getString(R.string.shared_pref_disp_fouls), true),
                prefs.getBoolean(getString(R.string.shared_pref_disp_misc), true)};

        showDeadBallAlerts = prefs.getBoolean(getString(R.string.shared_pref_alert_dead_ball), true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.game_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.view_options:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.game_options_prompt));
                builder.setMultiChoiceItems(getResources().getStringArray(R.array.game_view_options),
                        displayPlay, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                displayPlay[i] = b;
                            }
                        });
                builder.show();
                break;
            case R.id.pause_options:
                if(showDeadBallAlerts){
                    item.setTitle(getString(R.string.pause_option));
                }
                else{
                    item.setTitle(getString(R.string.unpause_option));
                }
                showDeadBallAlerts = !showDeadBallAlerts;
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("stop", "onStop called for GameFragment");
        if(gameAsync != null) {
            Log.d("stop", "canceling gameAsync");
            gameAsync.cancel(true);
        }

        if(game.isPlayed()){
            boolean bigWin, badLoss;
            if(game.getHomeTeam().isPlayerControlled()){
                bigWin = (game.getHomeTeam().getOverallRating() + 10 <= game.getAwayTeam().getOverallRating()) && game.homeTeamWin();
                badLoss = (game.getHomeTeam().getOverallRating() - 10 >= game.getAwayTeam().getOverallRating()) && !game.homeTeamWin();
                for(Coach c: game.getHomeTeam().getCoaches()){
                    c.recruitRecruits(bigWin, badLoss, game.getHomeTeam().getNumberOfReturningPlayers());

                }
            }
            else{
                bigWin = (game.getAwayTeam().getOverallRating() + 10 <= game.getHomeTeam().getOverallRating()) && !game.homeTeamWin();
                badLoss = (game.getAwayTeam().getOverallRating() - 10 >= game.getHomeTeam().getOverallRating()) && game.homeTeamWin();
                for(Coach c: game.getAwayTeam().getCoaches()){
                    c.recruitRecruits(bigWin, badLoss, game.getAwayTeam().getNumberOfReturningPlayers());
                }
            }
        }
        ((MainActivity)getActivity()).actionBar.setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).actionBar.setTitle((((MainActivity) getActivity()).currentTeam.getFullName()));
        ((MainActivity)getActivity()).drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.shared_pref_disp_scoring), displayPlay[0]);
        editor.putBoolean(getString(R.string.shared_pref_disp_cop), displayPlay[1]);
        editor.putBoolean(getString(R.string.shared_pref_disp_fouls), displayPlay[2]);
        editor.putBoolean(getString(R.string.shared_pref_disp_misc), displayPlay[3]);
        editor.putBoolean(getString(R.string.shared_pref_alert_dead_ball), showDeadBallAlerts);
        editor.apply();
    }

    private class SimGame extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute(){
            if(!game.getIsInProgress()) {
                game.setSavePlays(true);
                game.preGameSetUp();
            }
            else{
                changeAdapters(0);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            do{
                int playResult = game.simPlay();
                while(playResult != -1 && !isCancelled()){
                    if(playResult == 1 && !isCancelled()) {
                        isInTimeout = true;
                        publishProgress();
                        while(isInTimeout && !isCancelled()){
                            try {
                                sleep(500);
                            }
                            catch (InterruptedException e){
                                Log.e("sleep error", e.toString());
                                cancel(true);
                            }
                        }
                    }
                    else if(playResult == 2 && showDeadBallAlerts && !isCancelled()){
                        alertDeadBall = true;
                        publishProgress();
                        while(alertDeadBall && !isCancelled()){
                            try {
                                sleep(500);
                            }
                            catch (InterruptedException e){
                                Log.e("sleep error", e.toString());
                                cancel(true);
                            }
                        }
                    }
                    else if(playResult == 3 && !isCancelled()){
                        forceSub = true;
                        publishProgress();
                        while(forceSub){
                            try {
                                sleep(500);
                            }
                            catch (InterruptedException e){
                                Log.e("sleep error", e.toString());
                                cancel(true);
                            }
                        }
                    }
                    else if(!isCancelled()){
                        if(adapter.getItemCount() != game.getPlaysOfType(convertDisplayPlay()).size()) {
                            publishProgress();
                            try {
                                if (gameSpeed != 1) {
                                    sleep(gameSpeed * 250);
                                } else {
                                    sleep(100);
                                }
                            } catch (InterruptedException e) {
                                Log.e("sleep error", e.toString());
                                cancel(true);
                            }
                        }
                    }
                    while(pauseSim && !isCancelled()){
                        try{
                            sleep(250);
                        }
                        catch (InterruptedException e){
                            Log.e("sleep error", e.toString());
                            cancel(true);
                        }
                    }
                    if(!isCancelled()) {
                        playResult = game.simPlay();
                    }
                }
            }while(game.startNextHalf() && !isCancelled());

            if(!isCancelled()) {
                game.setIsPlayed(true);

                stats = new ArrayList<>();
                stats.addAll(game.getHomeTeam().playGame(game.homeTeamWin()));
                stats.addAll(game.getAwayTeam().playGame(!game.homeTeamWin()));
            }
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

            new DataAsync().execute();
        }

        @Override
        protected void onCancelled(){
            gameAsync = null;
        }

        private void updateUI(){
            homeScore.setText(getString(R.string.scores, game.getHomeScore()));
            awayScore.setText(getString(R.string.scores, game.getAwayScore()));

            homeFouls.setText(getString(R.string.team_fouls, game.getHomeFouls()));
            awayFouls.setText(getString(R.string.team_fouls, game.getAwayFouls()));

            half.setText(getString(R.string.half, game.getHalf()));
            time.setText(getString(R.string.game_time, game.getFormattedTime(), game.getShotClock()));

            if(adapterType == 0) {
                //((GameAdapter) adapter).setPlays(game.getPlays());
                ((GameAdapter)adapter).setPlays(game.getPlaysOfType(convertDisplayPlay()));
                adapter.notifyDataSetChanged();
                grAdapter.notifyDataSetChanged();
            }
            else if(adapterType == 4){
                initTeamStatsView();
            }
            else{
                adapter.notifyDataSetChanged();
                grAdapter.notifyDataSetChanged();
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
        changeAdapters(5);

        String[] array = new String[getSpinnerList().length+1];
        for(int x = 0 ; x < getSpinnerList().length; x++){
            array[x] = getSpinnerList()[x];
        }
        array[array.length-1] = "Team speech";
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, array);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(5, true);

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
                "Team Strategy",
                "Team Comparison"
        };
    }

    private void setClickListeners(){
        gameSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                gameSpeed = 11 - i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pauseSim = gameSpeed == 11;

                if(pauseSim){
                    gameSpeedText.setText(getString(R.string.sim_paused));
                }
                else{
                    gameSpeedText.setText(getString(R.string.game_speed));
                }
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

                        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_timeout));
                    }
                    else if(game.getPlayerWantsTO()){
                        game.setPlayerWantsTO(false);
                    }
                    else {
                        game.setPlayerWantsTO(true);
                    }
                }
                else if(adapterType == 1 || adapterType == 2){
                    boolean success;
                    if(game.getHomeTeam().isPlayerControlled()){
                        success = game.getHomeTeam().updateSubs(grAdapter.getSubs());
                    }
                    else{
                        success = game.getAwayTeam().updateSubs(grAdapter.getSubs());
                    }

                    if(success){
                        fab.setVisibility(View.GONE);
                        if(forceSub){
                            forceSub = false;
                            game.getHomeTeam().makeSubs();
                            game.getAwayTeam().makeSubs();
                        }
                    }

                }
                else if(adapterType == 5) {
                    if (game.getHomeTeam().isPlayerControlled()) {
                        game.coachTalk(game.getHomeTeam(), !game.getIsNeutralCourt(), game.getAwayScore() - game.getHomeScore(), ((GameSpeechAdapter) adapter).getSelectedValue());
                        game.coachTalk(game.getAwayTeam(), false, game.getHomeScore() - game.getAwayScore());
                    } else {
                        game.coachTalk(game.getHomeTeam(), !game.getIsNeutralCourt(), game.getAwayScore() - game.getHomeScore());
                        game.coachTalk(game.getAwayTeam(), false, game.getHomeScore() - game.getAwayScore(), ((GameSpeechAdapter) adapter).getSelectedValue());
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

        rosterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                changeRosterAdapter(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers(), 0, this);
        }
        else{
            grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers(), 0, this);
        }
    }

    private void createTeamStatsView(){
        hName = teamStatsView.findViewById(R.id.home_team);
        aName = teamStatsView.findViewById(R.id.away_team);

        h2FG = teamStatsView.findViewById(R.id.home_two_point);
        h3FG = teamStatsView.findViewById(R.id.home_three_point);
        hFT = teamStatsView.findViewById(R.id.home_free_throw);
        hA = teamStatsView.findViewById(R.id.home_assists);
        hOB = teamStatsView.findViewById(R.id.home_o_board);
        hDB = teamStatsView.findViewById(R.id.home_d_board);
        hS = teamStatsView.findViewById(R.id.home_steals);
        hTO = teamStatsView.findViewById(R.id.home_turnovers);

        a2FG = teamStatsView.findViewById(R.id.away_two_point);
        a3FG = teamStatsView.findViewById(R.id.away_three_point);
        aFT = teamStatsView.findViewById(R.id.away_free_throw);
        aA = teamStatsView.findViewById(R.id.away_assists);
        aOB = teamStatsView.findViewById(R.id.away_o_board);
        aDB = teamStatsView.findViewById(R.id.away_d_board);
        aS = teamStatsView.findViewById(R.id.away_steals);
        aTO = teamStatsView.findViewById(R.id.away_turnovers);
    }

    private void initTeamStatsView(){
        h2FG.setText(game.getHomeTeam().getTwoPointMakes() + "/" + game.getHomeTeam().getTwoPointAttempts());
        h3FG.setText(game.getHomeTeam().getThreePointMakes() + "/" + game.getHomeTeam().getThreePointAttempts());
        hFT.setText(game.getHomeTeam().getFreeThrowMakes() + "/" + game.getHomeTeam().getFreeThrowAttempts());
        hA.setText(""+game.getHomeTeam().getAssists());
        hOB.setText(""+game.getHomeTeam().getoBoards());
        hDB.setText(""+game.getHomeTeam().getdBoards());
        hS.setText(""+game.getHomeTeam().getSteals());
        hTO.setText(""+game.getHomeTeam().getTurnovers());

        a2FG.setText(game.getAwayTeam().getTwoPointMakes() + "/" + game.getAwayTeam().getTwoPointAttempts());
        a3FG.setText(game.getAwayTeam().getThreePointMakes() + "/" + game.getAwayTeam().getThreePointAttempts());
        aFT.setText(game.getAwayTeam().getFreeThrowMakes() + "/" + game.getAwayTeam().getFreeThrowAttempts());
        aA.setText(""+game.getAwayTeam().getAssists());
        aOB.setText(""+game.getAwayTeam().getoBoards());
        aDB.setText(""+game.getAwayTeam().getdBoards());
        aS.setText(""+game.getAwayTeam().getSteals());
        aTO.setText(""+game.getAwayTeam().getTurnovers());

        hName.setText(game.getHomeTeamName());
        aName.setText(game.getAwayTeamName());
    }

    private void changeAdapters(int type){
        if(type == 0){
            if(adapterType == 1 || adapterType == 2){
                frame.removeView(gameRosterView);
                frame.addView(recyclerView);
            }
            else if(adapterType == 3){
                frame.removeView(strategyView);
                frame.addView(recyclerView);
            }
            else if(adapterType == 4){
                frame.removeView(teamStatsView);
                frame.addView(recyclerView);
            }

            adapter = new GameAdapter(new ArrayList<>(game.getPlays()), game.getAwayTeam().getColorLight());
            if(gameAsync == null){
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
            }
            else if(alertDeadBall){
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp));
                deadBall.setVisibility(View.VISIBLE);
            }
            else {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_call_timeout));
            }
            fab.setVisibility(View.VISIBLE);

            if(isInTimeout || alertDeadBall){
                gameSpeedText.setVisibility(View.INVISIBLE);
                gameSpeedBar.setVisibility(View.INVISIBLE);
                deadBall.setVisibility(View.VISIBLE);
            }
            else{
                gameSpeedText.setVisibility(View.VISIBLE);
                gameSpeedBar.setVisibility(View.VISIBLE);
            }
        }
        else if(type == 1){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
            if(adapterType == 0 || adapterType == 5){
                frame.removeView(recyclerView);
                frame.addView(gameRosterView);
            }
            else if(adapterType == 3){
                frame.removeView(strategyView);
                frame.addView(gameRosterView);
            }
            else if(adapterType == 4){
                frame.removeView(teamStatsView);
                frame.addView(gameRosterView);
            }

            fab.setVisibility(View.GONE);
            if(game.getHomeTeam().isPlayerControlled()){
                grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers(), 0, this);
            }
            else{
                grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers(), 0);
            }
            rosterRecycler.setAdapter(grAdapter);
            grAdapter.notifyDataSetChanged();
            rosterSpinner.setSelection(0, false);

            if(alertDeadBall || isInTimeout){
                gameSpeedText.setVisibility(View.INVISIBLE);
                gameSpeedBar.setVisibility(View.INVISIBLE);
                deadBall.setVisibility(View.VISIBLE);
            }
            else{
                gameSpeedText.setVisibility(View.VISIBLE);
                gameSpeedBar.setVisibility(View.VISIBLE);
            }
        }
        else if(type == 2){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
            if(adapterType == 0 || adapterType == 5){
                frame.removeView(recyclerView);
                frame.addView(gameRosterView);
            }
            else if(adapterType == 3){
                frame.removeView(strategyView);
                frame.addView(gameRosterView);
            }
            else if(adapterType == 4){
                frame.removeView(teamStatsView);
                frame.addView(gameRosterView);
            }

            fab.setVisibility(View.GONE);
            if(game.getAwayTeam().isPlayerControlled()){
                grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers(), 0, this);
            }
            else{
                grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers(), 0);
            }
            rosterRecycler.setAdapter(grAdapter);
            grAdapter.notifyDataSetChanged();
            rosterSpinner.setSelection(0, false);

            if(alertDeadBall || isInTimeout){
                gameSpeedText.setVisibility(View.INVISIBLE);
                gameSpeedBar.setVisibility(View.INVISIBLE);
                deadBall.setVisibility(View.VISIBLE);
            }
            else{
                gameSpeedText.setVisibility(View.VISIBLE);
                gameSpeedBar.setVisibility(View.VISIBLE);
            }
        }
        else if(type == 3){
            if(adapterType == 0 || adapterType == 5) {
                frame.removeView(recyclerView);
                frame.addView(strategyView);
            }
            else if(adapterType == 1 || adapterType == 2){
                frame.removeView(gameRosterView);
                frame.addView(strategyView);
            }
            else if(adapterType == 4){
                frame.removeView(teamStatsView);
                frame.addView(strategyView);
            }

            fab.setVisibility(View.GONE);
            gameSpeedText.setVisibility(View.GONE);
            gameSpeedBar.setVisibility(View.GONE);
            deadBall.setVisibility(View.GONE);
        }
        else if(type == 4){
            if(adapterType == 0 || adapterType == 5) {
                frame.removeView(recyclerView);
                frame.addView(teamStatsView);
            }
            else if(adapterType == 1 || adapterType == 2){
                frame.removeView(gameRosterView);
                frame.addView(teamStatsView);
            }
            else if(adapterType == 3){
                frame.removeView(strategyView);
                frame.addView(teamStatsView);
            }
            fab.setVisibility(View.GONE);
            gameSpeedText.setVisibility(View.GONE);
            gameSpeedBar.setVisibility(View.GONE);
            deadBall.setVisibility(View.GONE);

            rosterSpinner.setSelection(0, false);
            initTeamStatsView();
        }
        else if(type == 5){
            if(adapterType == 1 || adapterType == 2) {
                frame.removeView(gameRosterView);
                frame.addView(recyclerView);
            }
            else if(adapterType == 3){
                frame.removeView(strategyView);
                frame.addView(recyclerView);
            }
            else if(adapterType == 4){
                frame.removeView(teamStatsView);
                frame.addView(recyclerView);
            }

            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
            fab.setVisibility(View.VISIBLE);
            if(game.getTimeRemaining() == 20 * 60 || (game.getHalf() > 2 && game.getTimeRemaining() == 5 * 60)){
                adapter = new GameSpeechAdapter(getResources().getStringArray(R.array.pre_game_speeches));
                deadBall.setText(getString(R.string.pre_game_instruction));
            }
            else {
                adapter = new GameSpeechAdapter(getResources().getStringArray(R.array.timeout_array));
            }
            deadBall.setVisibility(View.VISIBLE);
            gameSpeedText.setVisibility(View.INVISIBLE);
            gameSpeedBar.setVisibility(View.INVISIBLE);
        }


        adapterType = type;
        recyclerView.setAdapter(adapter);
    }

    private void changeRosterAdapter(int type){
        if(game.getHomeTeam().isPlayerControlled()) {
            if(adapterType == 1) {
                grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers(), type, this);
            }
            else{
                grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers(), type, this);
            }
        }
        else{
            if(adapterType == 2) {
                grAdapter = new GameRosterAdapter(game.getAwayTeam().getPlayers(), type, this);
            }
            else{
                grAdapter = new GameRosterAdapter(game.getHomeTeam().getPlayers(), type, this);
            }
        }

        if(type == 0){
            posLabel.setVisibility(View.VISIBLE);
            label1.setText(getString(R.string.pref_pos));
            label2.setText(getString(R.string.condition));
            label3.setText(getString(R.string.rating));
            label4.setText(getString(R.string.fouls));
        }
        else if(type == 1){
            posLabel.setVisibility(View.GONE);
            label1.setText(getString(R.string.two_point));
            label2.setText(getString(R.string.three_point));
            label3.setText(getString(R.string.free_throw));
            label4.setText(getString(R.string.assists));
        }
        else if(type == 2){
            posLabel.setVisibility(View.GONE);
            label1.setText(getString(R.string.o_board));
            label2.setText(getString(R.string.d_board));
            label3.setText(getString(R.string.steals));
            label4.setText(getString(R.string.to));
        }

        rosterRecycler.setAdapter(grAdapter);
    }

    private Integer[] convertDisplayPlay(){
        int numTrue = 1;
        for(boolean b: displayPlay){
            if(b){
                numTrue++;
            }
        }
        Integer[] display = new Integer[numTrue];
        display[0] = 0;
        int index = 1;
        if(displayPlay[0]){
            display[index++] = 1;
        }
        if(displayPlay[1]){
            display[index++] = 2;
        }
        if(displayPlay[2]){
            display[index++] = 3;
        }
        if(displayPlay[3]){
            display[index] = -1;
        }

        return display;
    }

    private class DataAsync extends AsyncTask<String, String, String>{

        private AppDatabase db;


        @Override
        protected void onPreExecute(){
            db = Room.databaseBuilder(GameFragment.this.getContext().getApplicationContext(), AppDatabase.class, "basketballdb").build();
            Log.d("save games", "Start saving games");
        }

        @Override
        protected void onPostExecute(String result){
            db.close();
            Log.d("save games", "Finished saving games");
        }

        @Override
        protected String doInBackground(String... strings){
            GameStatsDB[] gameStatsDB = new GameStatsDB[stats.size()];
            for(int x = 0; x < gameStatsDB.length; x++){
                gameStatsDB[x] = stats.get(x);
            }

            db.appDAO().insertGamesStats(gameStatsDB);

            return null;
        }
    }
}

package com.coaching.jphil.collegebasketballcoach.fragments;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameStatsDB;
import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.GameSpeechAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.ScheduleAdapter;
import com.coaching.jphil.collegebasketballcoach.adapters.TournamentGameAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.NationalChampionship;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private MainActivity mainActivity;

    private Button simGame, viewTournament;
    private boolean playerSeasonFinished = false;
    private boolean startNewSeason = false;
    private boolean allConferencesInPostSeason;
    private boolean allConferencesHaveChamp;
    private boolean isInTournamentView;

    private ArrayList<GameStatsDB> stats;

    private SimAsync async;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        isInTournamentView = false;

        TextView tvSeason = view.findViewById(R.id.schedule_season);
        mainActivity = (MainActivity) getActivity();

        tvSeason.setText(getResources().getString(R.string.season_name, mainActivity.getPlayerTeam().getCurrentSeasonYear(), mainActivity.getPlayerTeam().getCurrentSeasonYear() + 1));

        recyclerView = view.findViewById(R.id.schedule_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new ScheduleAdapter(mainActivity.currentTeam.getSchedule()
                , mainActivity.currentTeam);
        recyclerView.setAdapter(adapter);

        simGame = view.findViewById(R.id.sim_game);
        viewTournament = view.findViewById(R.id.tourn_view);

        async = null;
        updateUI();

        if(!mainActivity.currentTeam.isPlayerControlled()){
            simGame.setVisibility(View.GONE);
        }

        simGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(simGame.getText().equals(getString(R.string.new_season))){
                    startNewSeason();
                    simGame.setEnabled(false);
                }
                else if(playerSeasonFinished){
                    if(async == null){
                        async = new SimAsync();
                        async.execute();
                    }
                }
                else if(!startNewSeason){
                    if(async == null){
                        async = new SimAsync();
                        async.execute();
                    }
                }

                if(isInTournamentView){
                    if (mainActivity.championship != null) {
                        ((TournamentGameAdapter) adapter).changeGames(mainActivity.championship.getGames());
                    }
                    else {
                        ((TournamentGameAdapter) adapter).changeGames(mainActivity.currentConference.getTournamentGames());
                    }
                }
                else {
                    ((ScheduleAdapter) adapter).changeGames(mainActivity.currentTeam.getSchedule());
                }
                adapter.notifyDataSetChanged();
            }
        });

        viewTournament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainActivity.championship != null){
                    adapter = new TournamentGameAdapter(mainActivity.championship.getTournament().getGames(), mainActivity.championship.getTeams());
                }
                else {
                    adapter = new TournamentGameAdapter(mainActivity.currentConference.getTournamentGames(), mainActivity.currentConference.getStandings());
                }
                recyclerView.setAdapter(adapter);
                viewTournament.setVisibility(View.GONE);
                simGame.setVisibility(View.VISIBLE);
                isInTournamentView = true;
            }
        });

        return view;
    }

    @Override
    public void onStop(){
        super.onStop();

        if(async != null){
            async.cancel(true);
        }
    }

    private class SimAsync extends AsyncTask<String, Integer, Integer>{

        public SimAsync(){}

        @Override
        protected void onPreExecute(){
            simGame.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int index = simulateGames(mainActivity.currentTeam);
            if(index > -1){
                return index;
            }

            return index;
        }

        @Override
        protected void onPostExecute(Integer results){
            Log.d("results", "Results1: " + results);
            if(results > -1){
                GameFragment frag = new GameFragment();
                Bundle args = new Bundle();
                args.putInt("game", results);

                frag.setArguments(args);

                mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .addToBackStack("game")
                        .commit();
            }
            else if(results == -2){
                startTournament(!allConferencesHaveChamp);
//                if(mainActivity.getPlayerTeam().isSeasonOver()){
//                    simRestOfSeason();
//                }
            }
            else if(results == -100){
                if(mainActivity.championship != null && mainActivity.championship.hasChampion()) {
                    Log.d("Champ", "Champion: " + mainActivity.championship.getTournament().getChampion().getFullName());
                    startNewSeason = true;
                }
                else if(mainActivity.championship != null){
                    for(Game g: mainActivity.championship.getGames()){
                        Log.d("champs", g.getHomeTeamName() + " vs. " + g.getAwayTeamName());
                    }
//                    if(mainActivity.getPlayerTeam().isSeasonOver()){
//                        simRestOfSeason();
//                    }
                }
                else{
                    Log.d("results", "Results: " + results);
                    startTournament(false);
                    if(mainActivity.getPlayerTeam().isSeasonOver()){
                        simRestOfSeason();
                    }
                }
            }
            async = null;
            adapter.notifyDataSetChanged();
            simGame.setEnabled(true);
            updateUI();

            new DataAsync().execute();

            Log.d("sim", "finished simming: " + results);
        }

        @Override
        protected void onProgressUpdate(Integer... integers){
            adapter.notifyDataSetChanged();
        }

        private Integer simulateGames(Team team) {
            stats = new ArrayList<>();
            if (!allConferencesInPostSeason) {
                for (Game game : mainActivity.masterSchedule) {
                    if (!game.isPlayed()) {
                        if (game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                            if (team.isPlayerControlled()) {
                                //stats.addAll(game.simulateGame());
                                //return -1;
                                return mainActivity.masterSchedule.indexOf(game);
                            }
                        } else {
                            stats.addAll(game.simulateGame());
                        }
                    }
                }
                return -2;
            }
            else if (!allConferencesHaveChamp) {
                allConferencesHaveChamp = true;
                for (Conference c : mainActivity.conferences) {
                    for (Tournament t : c.getTournaments()) {
                        if(t.getGames() == null){
                            t.generateNextRound();
                        }
                        for (Game game : t.getGames()) {
                            if (!game.isPlayed()) {
                                if (game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                                    if (team.isPlayerControlled()) {
                                        //stats.addAll(game.simulateGame());
                                        //return -1;
                                        return mainActivity.masterSchedule.indexOf(game);
                                    }
                                } else {
                                    stats.addAll(game.simulateGame());
                                }
                            }
                        }
                        t.generateNextRound();
                    }
                    if(c.getChampion() == null){
                        Log.d("champs", c.getName() + " doesn't have a champ yet");
                        allConferencesHaveChamp = false;
                        c.generateTournament();
                    }
                }
                return -2;
            }
            else if(mainActivity.championship != null){
                for (Game game : mainActivity.championship.getGames()) {
                    if (!game.isPlayed()) {
                        if (game.getHomeTeam().equals(team) || game.getAwayTeam().equals(team)) {
                            if (team.isPlayerControlled()) {
                                //stats.addAll(game.simulateGame());
                                //return -1;
                                return mainActivity.masterSchedule.indexOf(game);
                            }
                        } else {
                            stats.addAll(game.simulateGame());
                        }
                    }
                }
                mainActivity.championship.generateNextRound();
            }

            return -100;
        }

        private void simRestOfSeason(){
            async = new SimAsync();
            async.execute();
        }
    }

    private class DataAsync extends AsyncTask<String, String, String>{

        private AppDatabase db;


        @Override
        protected void onPreExecute(){
            db = Room.databaseBuilder(ScheduleFragment.this.getContext().getApplicationContext(), AppDatabase.class, "basketballdb").build();
        }

        @Override
        protected void onPostExecute(String result){
            db.close();
        }

        @Override
        protected String doInBackground(String... strings){
            GameStatsDB[] gameStatsDB = new GameStatsDB[stats.size()];
            ArrayList<Integer> gameIndexs = new ArrayList<>();

            for(int x = 0; x < gameStatsDB.length; x++){
                gameStatsDB[x] = stats.get(x);
                if(!gameIndexs.contains(gameStatsDB[x].gameId)){
                    gameIndexs.add(gameStatsDB[x].gameId);
                }
            }


            GameDB[] games = new GameDB[gameIndexs.size()];
            for(int x = 0; x < gameIndexs.size(); x++){
                games[x] = new GameDB();
                Game game = null;
                for(Game g: mainActivity.masterSchedule){
                    if(g.getId() == gameIndexs.get(x)){
                        game = g;
                        break;
                    }
                }
                if(game == null && mainActivity.championship != null){
                    for(Game g: mainActivity.championship.getGames()){
                        if(g.getId() == gameIndexs.get(x)){
                            game = g;
                            break;
                        }
                    }
                }

                if(game != null) {
                    games[x].gameID = game.getId();
                    games[x].homeTeamID = game.getHomeTeam().getId();
                    games[x].awayTeamID = game.getAwayTeam().getId();

                    games[x].homeScore = game.getHomeScore();
                    games[x].awayScore = game.getAwayScore();

                    games[x].isNeutralCourt = game.getIsNeutralCourt();
                    games[x].isPlayed = game.isPlayed();
                }
                else{
                    Log.e("Save Error", "No game with an ID of: " + gameIndexs.get(x));
                }
            }

            db.appDAO().insertGames(games);
            db.appDAO().insertGamesStats(gameStatsDB);
            Log.d("Saves", "Finished saving games");

            return null;
        }
    }

    private void updateUI(){
        allConferencesInPostSeason = true;
        allConferencesHaveChamp = true;
        for(Conference c: mainActivity.conferences){
            if(!c.isInPostSeason()){
                allConferencesInPostSeason = false;
                allConferencesHaveChamp = false;
            }
        }

        if(allConferencesInPostSeason){
            for(Conference c: mainActivity.conferences){
                if(!c.isSeasonFinished()){
                    allConferencesHaveChamp = false;
                }
            }
        }

        if(!mainActivity.getPlayerTeam().hasUnplayedGames()){
            if(!mainActivity.getPlayerTeam().getConference().isInPostSeason()){
                simGame.setText(getString(R.string.start_tournament));
                playerSeasonFinished = false;
            }
            else if(mainActivity.championship != null){
                if(mainActivity.championship.hasChampion()){
                    playerSeasonFinished = true;
                    startNewSeason = true;
                    simGame.setText(getString(R.string.new_season));
                }
                else {
                    playerSeasonFinished = false;
                }
            }
            else {
                playerSeasonFinished = true;
            }
        }

        if(!isInTournamentView && (mainActivity.currentConference.isInPostSeason() || mainActivity.championship != null)){
            viewTournament.setVisibility(View.VISIBLE);
            simGame.setVisibility(View.INVISIBLE);
        }
    }

    private void startTournament(boolean generateConfTourn){
        if(generateConfTourn) {
            for (Conference c : mainActivity.conferences) {
                c.generateTournament();
            }
        }
        else{
            mainActivity.generateNationalChampionship();
        }

        simGame.setText(R.string.sim_game);
        if(isInTournamentView){
            if (mainActivity.championship != null) {
                ((TournamentGameAdapter) adapter).changeGames(mainActivity.championship.getGames());
                ((TournamentGameAdapter)adapter).changeTeams(mainActivity.championship.getTeams());
            }
            else {
                ((TournamentGameAdapter) adapter).changeGames(mainActivity.currentConference.getTournamentGames());
            }
        }
        else {
            ((ScheduleAdapter) adapter).changeGames(mainActivity.currentTeam.getSchedule());
        }
        adapter.notifyDataSetChanged();
    }

    private void startNewSeason(){
        mainActivity.startNewSeason();
        startNewSeason = false;
    }
}

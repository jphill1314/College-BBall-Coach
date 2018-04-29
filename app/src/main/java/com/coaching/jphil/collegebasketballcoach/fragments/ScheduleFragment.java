package com.coaching.jphil.collegebasketballcoach.fragments;


import android.arch.persistence.room.Room;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameStatsDB;
import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.adapters.ScheduleAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;

import java.util.ArrayList;


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
    private int currentView;
    private MainActivity mainActivity;

    //private Button simGame, viewTournament;
    private FloatingActionButton nextAction;
    private boolean playerSeasonFinished = false;
    private boolean startNewSeason = false;
    private boolean allConferencesInPostSeason;
    private boolean allConferencesHaveChamp;
    private boolean isInTournamentView;

    private ArrayList<GameStatsDB> stats;

    private SimAsync async;
    private DataAsync dataAsync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        isInTournamentView = false;

        mainActivity = (MainActivity) getActivity();

        recyclerView = view.findViewById(R.id.schedule_list);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        nextAction = view.findViewById(R.id.adavance_fab);
        nextAction.setVisibility(View.INVISIBLE);
        nextAction.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(mainActivity.currentTeam.getColorLight())));

        async = null;

        setupAdapter();
        updateUI();

        return view;
    }

    @Override
    public void onStop(){
        super.onStop();

        if(async != null){
            async.cancel(true);
        }
    }

    public void setupAdapter(){
        if(mainActivity.currentTeam != null && mainActivity.currentTeam.getSchedule() != null) {
            adapter = new ScheduleAdapter(mainActivity.currentTeam.getSchedule(), mainActivity.currentTeam, this, 0);
            currentView = 0;
            recyclerView.setAdapter(adapter);

            nextAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    async = new SimAsync();
                    async.execute();
                    adapter.notifyDataSetChanged();
                }
            });

            nextAction.setVisibility(View.VISIBLE);
        }
        else{
            mainActivity.loadData("load for schedule");
        }
    }

    private class SimAsync extends AsyncTask<String, Integer, Integer>{

        public SimAsync(){}

        @Override
        protected void onPreExecute(){
            nextAction.setVisibility(View.GONE);
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
            }
            else if(results == -100){
                if(mainActivity.championship != null && mainActivity.championship.hasChampion()) {
                    startNewSeason = true;
                }
                else{
                    startTournament(false);
                    if(mainActivity.getPlayerTeam().isSeasonOver()){
                        simRestOfSeason();
                    }
                }
            }
            async = null;
            if(currentView == 1){
                ((ScheduleAdapter)adapter).changeGames(mainActivity.currentConference.getTournamentGames());
            }
            else if(currentView == 2){
                ((ScheduleAdapter)adapter).changeGames(mainActivity.championship.getGames());
            }
            adapter.notifyDataSetChanged();
            nextAction.setVisibility(View.VISIBLE);
            updateUI();

            dataAsync = new DataAsync();
            dataAsync.execute("");
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
            if(db == null || !db.isOpen()) {
                db = Room.databaseBuilder(ScheduleFragment.this.getContext().getApplicationContext(), AppDatabase.class, "basketballdb").build();
            }
        }

        @Override
        protected void onPostExecute(String result){
            if(db.isOpen()){
                db.close();
            }

            dataAsync = null;
        }

        @Override
        protected String doInBackground(String... strings){
            if(strings[0].equals("tournament")){
                saveTournamentGames();
            }
            else{
                saveGameAndStats();
            }

            return null;
        }

        private void saveGameAndStats(){
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
            }

            db.appDAO().insertGames(games);
            db.appDAO().insertGamesStats(gameStatsDB);
        }

        private void saveTournamentGames(){
            int numGames = 0;
            for(Conference c: mainActivity.conferences){
                for(Tournament t: c.getTournaments()){
                    for(Game g: t.getGames()){
                        numGames++;
                    }
                }
            }

            GameDB[] games = new GameDB[numGames];
            int gameIndex = 0;
            for(Conference c: mainActivity.conferences){
                for(Tournament t: c.getTournaments()){
                    for(Game game: t.getGames()){
                        games[gameIndex] = new GameDB();
                        games[gameIndex].gameID = game.getId();
                        games[gameIndex].homeTeamID = game.getHomeTeam().getId();
                        games[gameIndex].awayTeamID = game.getAwayTeam().getId();

                        games[gameIndex].homeScore = game.getHomeScore();
                        games[gameIndex].awayScore = game.getAwayScore();

                        games[gameIndex].isNeutralCourt = game.getIsNeutralCourt();
                        games[gameIndex].isPlayed = game.isPlayed();
                        gameIndex++;
                    }
                }
            }

            db.appDAO().insertGames(games);
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
                //simGame.setText(getString(R.string.start_tournament));
                playerSeasonFinished = false;
            }
            else if(mainActivity.championship != null){
                if(mainActivity.championship.hasChampion()){
                    playerSeasonFinished = true;
                    startNewSeason = true;
                    //simGame.setText(getString(R.string.new_season));
                }
                else {
                    playerSeasonFinished = false;
                }
            }
            else {
                playerSeasonFinished = true;
            }
        }

        if(startNewSeason){
            nextAction.setVisibility(View.GONE);
        }
    }

    private void startTournament(boolean generateConfTourn){
        if(generateConfTourn) {
            for (Conference c : mainActivity.conferences) {
                c.generateTournament();
            }
            new DataAsync().execute("tournament");
        }
        else{
            if(mainActivity.championship == null) {
                mainActivity.generateNationalChampionship();
            }
        }

        ((ScheduleAdapter) adapter).changeGames(mainActivity.currentTeam.getSchedule());
        adapter.notifyDataSetChanged();
    }

    public void startNewSeason(){
        if(startNewSeason && mainActivity.championship.hasChampion()) {
            nextAction.setVisibility(View.GONE);
            mainActivity.startNewSeason();
            startNewSeason = false;
        }
        else{
            Toast.makeText(getContext(), "The season is not over yet!", Toast.LENGTH_LONG).show();
        }
    }

    public void viewConferenceTournament(){
        if(mainActivity.currentConference.isInPostSeason()){
            adapter = new ScheduleAdapter(mainActivity.currentConference.getTournamentGames(), mainActivity.currentTeam, mainActivity.currentConference.getStandings(), this, 1);
            recyclerView.setAdapter(adapter);
            currentView = 1;
        }
        else{
            Toast.makeText(getContext(), "You must finish the season before entering the tournament", Toast.LENGTH_LONG).show();
        }
    }

    public void viewChampionship(){
        if(mainActivity.championship != null){
            adapter = new ScheduleAdapter(mainActivity.championship.getGames(), mainActivity.currentTeam, mainActivity.championship.getTeams(), this, 2);
            recyclerView.setAdapter(adapter);
            currentView = 2;
        }
        else{
            Toast.makeText(getContext(), "All conference tournaments are not yet finished", Toast.LENGTH_LONG).show();
        }
    }
}

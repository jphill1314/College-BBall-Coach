package com.coaching.jphil.collegebasketballcoach;

import android.arch.persistence.room.Room;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.CoachDB;
import com.coaching.jphil.collegebasketballcoach.Database.ConferenceDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.PlayerDB;
import com.coaching.jphil.collegebasketballcoach.Database.RecruitDB;
import com.coaching.jphil.collegebasketballcoach.Database.TeamDB;
import com.coaching.jphil.collegebasketballcoach.Database.TournamentDB;
import com.coaching.jphil.collegebasketballcoach.adapters.NavDrawerAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
import com.coaching.jphil.collegebasketballcoach.basketballSim.ScheduleGenerator;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.NationalChampionship;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StaggeredTenTeam;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StandardEightTeam;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StandardTenTeam;
import com.coaching.jphil.collegebasketballcoach.fragments.GameFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.RecruitFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.ScheduleFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StaffFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StandingsFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StrategyFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.TrainingFragment;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kobakei.ratethisapp.RateThisApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private RecyclerView drawerList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    private AppDatabase db;

    public ArrayList<Conference> conferences;
    public ArrayList<Game> masterSchedule;

    public Team currentTeam;
    public Conference currentConference;
    public NationalChampionship championship;

    public FloatingActionButton homeButton;
    public ActionBar actionBar;

    private FirebaseAnalytics firebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RateThisApp.onCreate(this);
        RateThisApp.showRateDialogIfNeeded(this);

        if(db == null && conferences == null){
            new DataAsync().execute("load");
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        invalidateOptionsMenu();
    }

    private void initUI(){
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);

        homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeButton.setVisibility(View.GONE);
                currentTeam = getPlayerTeam();
                currentConference = getPlayerConference();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new RosterFragment())
                        .commit();

                actionBar.setTitle(currentTeam.getFullName());
                updateColors();
            }
        });

        manager = new LinearLayoutManager(this);
        drawerList.setLayoutManager(manager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionBar.setTitle(currentTeam.getFullName());
        updateColors();

        adapter = new NavDrawerAdapter(getResources().getStringArray(R.array.drawer_items));
        drawerList.setAdapter(adapter);


    }

    public void onDataAsyncFinish(){
        currentTeam = getPlayerTeam();
        currentConference = getPlayerConference();

        initUI();

        updateFragment(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        super.onStop();

        new DataAsync().execute("save");
    }

    public void updateFragment(int position){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); i++){
            fm.popBackStack();
        }

        android.support.v4.app.FragmentTransaction t = fm.beginTransaction();
        boolean makeSwitch = false;
        String text = "";

        switch(position){
            case 0:
                t.replace(R.id.content_frame, new RosterFragment());
                makeSwitch = true;
                actionBar.setTitle(getString(R.string.roster_title, currentTeam.getFullName()));
                firebaseAnalytics.setCurrentScreen(this, "RosterFragment", "RosterFragment");
                break;
            case 1:
                t.replace(R.id.content_frame, new ScheduleFragment());
                makeSwitch = true;
                actionBar.setTitle(getString(R.string.season_name, currentTeam.getMascot(), currentTeam.getCurrentSeasonYear(), currentTeam.getCurrentSeasonYear() + 1));
                firebaseAnalytics.setCurrentScreen(this, "ScheduleFragment", "ScheduleFragment");
                break;
            case 2:
                t.replace(R.id.content_frame, new StandingsFragment());
                actionBar.setTitle(getString(R.string.standing_title));
                makeSwitch = true;
                firebaseAnalytics.setCurrentScreen(this, "StandingsFragment", "StandingsFragment");
                break;
            case 3:
                if(currentTeam.isPlayerControlled()) {
                    t.replace(R.id.content_frame, new RecruitFragment());
                    actionBar.setTitle(getString(R.string.recruiting_title, currentTeam.getFullName()));
                    makeSwitch = true;
                    firebaseAnalytics.setCurrentScreen(this, "RecruitFragment", "RecruitFragment");
                }
                else{
                    text = "You can't view another team's recruits!";
                }
                break;
            case 4:
                if(currentTeam.isPlayerControlled()) {
                    t.replace(R.id.content_frame, new StrategyFragment());
                    actionBar.setTitle(getString(R.string.strategy_title, currentTeam.getFullName()));
                    makeSwitch = true;
                    firebaseAnalytics.setCurrentScreen(this, "StrategyFragment", "StrategyFragment");
                }
                else{
                    text = "You can't view another team's strategy!";
                }
                break;
            case 5:
                t.replace(R.id.content_frame, new StaffFragment());
                actionBar.setTitle(getString(R.string.staff_title, currentTeam.getFullName()));
                makeSwitch = true;
                firebaseAnalytics.setCurrentScreen(this, "StaffFragment", "StaffFragment");
                break;
            case 6:
                if(currentTeam.isPlayerControlled()) {
                    t.replace(R.id.content_frame, new TrainingFragment());
                    actionBar.setTitle(getString(R.string.training_title, currentTeam.getFullName()));
                    makeSwitch = true;
                    firebaseAnalytics.setCurrentScreen(this, "TrainingFragment", "TrainingFragment");
                }
                else{
                    text = "You can't view another team's practice plans!";
                }
                break;
        }

        if(makeSwitch) {
            t.commit();
        }
        else{
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        }
        drawerLayout.closeDrawer(drawerList);
    }

    public void startNewSeason(){
        championship = null;
        masterSchedule = new ArrayList<>();

        for(Conference c: conferences){
            c.startNewSeason();
        }

        Bundle bundle = new Bundle();
        bundle.putString("team_name", currentTeam.getFullName());
        bundle.putInt("season", currentTeam.getCurrentSeasonYear());
        firebaseAnalytics.logEvent("start_new_season", bundle);

        new DataAsync().execute("new season");
    }

    public Team getPlayerTeam(){
        for(Conference c: conferences){
            for(Team t: c.getTeams()){
                if(t.isPlayerControlled()){
                    return t;
                }
            }
        }
        return null;
    }

    public Conference getPlayerConference(){
        for(Conference c: conferences){
            for(Team t: c.getTeams()){
                if(t.isPlayerControlled()){
                    return c;
                }
            }
        }
        return null;
    }

    public void generateNationalChampionship(){
        ArrayList<Team> champs = new ArrayList<>();
        ArrayList<Team> others = RPIRanking();
        for(Conference c: conferences){
            champs.add(c.getChampion());
            others.remove(champs.get(champs.size()-1));
        }
        while(champs.size() < 16){
            if(others.get(0).getWinPercent() > 50) {
                champs.add(others.get(0));
                if(others.get(0).isSeasonOver()){
                    others.get(0).toggleSeasonOver();
                }
            }
            others.remove(0);
        }

        int changes;
        do{
            changes = 0;
            for(int x = 0; x < champs.size() - 1; x++){
                for(int y = x + 1; y < champs.size(); y++){
                    if(champs.get(x).getRPI() < champs.get(y).getRPI()){
                        Collections.swap(champs, x, y);
                        changes++;
                    }
                }
            }
        }while(changes != 0);

        championship = new NationalChampionship(champs);
    }

    private ArrayList<Team> RPIRanking(){
        int changes;
        ArrayList<Team> standing = new ArrayList<>();
        for(Conference c: conferences){
            standing.addAll(c.getTeams());
        }

        do{
            changes = 0;
            for(int x = 0; x < standing.size() - 1; x++){
                for(int y = x + 1; y < standing.size(); y++){
                    if(standing.get(x).getRPI() < standing.get(y).getRPI()){
                        Collections.swap(standing, x, y);
                        changes++;
                    }
                }
            }
        }while(changes != 0);

        return standing;
    }

    public void addGameToMasterSchedule(Game game){
        if(!masterSchedule.contains(game)){
            game.setId(masterSchedule.size());
            masterSchedule.add(game);
        }
    }

    public void updateColors(){
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(currentTeam.getColorMain())));
        if(Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(currentTeam.getColorDark()));
        }

        homeButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(currentTeam.getColorLight())));
    }

    public void logGameStartedEvent(int gameID){
        Bundle bundle = new Bundle();
        bundle.putString("team_name", currentTeam.getFullName());
        bundle.putInt("game_id", gameID);
        firebaseAnalytics.logEvent("game_started", bundle);
    }

    public void changeScreenToGameFragment(){
        firebaseAnalytics.setCurrentScreen(this, "GameFragment", "GameFragment");
    }

    public void leaveGameFragment(){
        firebaseAnalytics.setCurrentScreen(this, "ScheduleFragment", "ScheduleFragment");
    }

    public void loadData(String type){
        new DataAsync().execute(type);
    }

    private class DataAsync extends AsyncTask<String, String, String> {
        private int nationalChampGameIndex = 10000; // this is to separate national championship games from regular season / conference tournament games

        @Override
        protected String doInBackground(String... strings){
            if(strings[0].contains("load")){
                if(db != null) {
                    if (!db.isOpen()) {
                        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                    }
                }
                else{
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                }
                loadData();

                if(strings[0].equals("load for game")){
                    return "loaded for game";
                }
                else if(strings[0].equals("load for schedule")){
                    return "loaded for schedule";
                }
                else if(strings[0].equals("load for roster")){
                    return "loaded for roster";
                }
                else if(strings[0].equals("load for standings")){
                    return "loaded for standings";
                }
                return "loaded";
            }
            else if(strings[0].equals("save")){
                if(db != null) {
                    if (!db.isOpen()) {
                        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                    }
                }
                else{
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                }
                saveData();
            }
            else if(strings[0].equals("new season")){
                if(db == null || !db.isOpen()){
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                }
                db.appDAO().deleteGameStats();
                db.appDAO().deleteTournaments();
                db.appDAO().deleteGameDB();

                PlayerDB[] players = db.appDAO().loadAllPlayers();
                ArrayList<PlayerDB> removedPlayers = new ArrayList<>();
                for(PlayerDB player: players){
                    if(player.year == 3){
                        removedPlayers.add(player);
                    }
                }
                db.appDAO().deletePlayers(removedPlayers);
                generateNonConferenceGames();
                saveData();
                saveGames();
                loadData();
                return "new season";
            }
            else if(strings[0].equals("delete all")){
                if(db != null) {
                    if (!db.isOpen()) {
                        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                    }
                }
                else{
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                }
                clearData();
                return "data cleared";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if(result != null) {
                if(result.equals("loaded for game")){
                    GameFragment game = (GameFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if(game != null){
                        game.startGameAfterLoad();
                        currentTeam = getPlayerTeam();
                        currentConference = getPlayerConference();
                    }
                }
                else if(result.equals("loaded for schedule")){
                    ScheduleFragment schedule = (ScheduleFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if(schedule != null){
                        currentTeam = getPlayerTeam();
                        currentConference = getPlayerConference();
                        schedule.setupAdapter();
                    }
                }
                else if(result.equals("loaded for roster")){
                    RosterFragment roster = (RosterFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if(roster != null){
                        currentTeam = getPlayerTeam();
                        currentConference = getPlayerConference();
                        roster.setupAdapter();
                    }
                }
                else if(result.equals("loaded for standings")){
                    StandingsFragment standings = (StandingsFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
                    if(standings != null){
                        currentTeam = getPlayerTeam();
                        currentConference = getPlayerConference();
                        standings.setupAdapter();
                    }
                }
                else{
                    onDataAsyncFinish();
                }
            }

            if(db.isOpen()){
                db.close();
            }
        }

        private void saveData(){
            int teamIndex = 0;
            int coachIndex = 0;
            int playerIndex = 0;
            int tournamentIndex = 0;

            if(db != null){
                if(!db.isOpen()) {
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                }
                for(int q = 0; q < conferences.size(); q++) {
                    ConferenceDB conferenceDB = new ConferenceDB();
                    conferenceDB.conferenceID = q;
                    conferenceDB.name = conferences.get(q).getName();
                    conferenceDB.type = conferences.get(q).getType();

                    db.appDAO().insertConferences(conferenceDB);

                    ArrayList<Team> teams = conferences.get(q).getTeams();
                    TeamDB[] teamsDB = new TeamDB[teams.size()];
                    RecruitDB[] recruits = null;

                    int numPlayers = 0;
                    int numCoaches = 0;

                    for (int i = 0; i < teams.size(); i++) {
                        teamsDB[i] = new TeamDB();
                        teamsDB[i].id = teams.get(i).getId();
                        teamsDB[i].conferenceID = q;
                        teamsDB[i].isPlayerControlled = teams.get(i).isPlayerControlled();
                        teamsDB[i].schoolName = teams.get(i).getSchoolName();
                        teamsDB[i].schoolMascot = teams.get(i).getMascot();

                        teamsDB[i].colorMain = teams.get(i).getColorMain();
                        teamsDB[i].colorDark = teams.get(i).getColorDark();
                        teamsDB[i].colorLight = teams.get(i).getColorLight();

                        teamsDB[i].offFavorsThrees = teams.get(i).getOffenseFavorsThrees();
                        teamsDB[i].defFavorsThrees = teams.get(i).getDefenseFavorsThrees();
                        teamsDB[i].aggression = teams.get(i).getAggression();
                        teamsDB[i].pace = teams.get(i).getPace();
                        teamsDB[i].isSeasonOver = teams.get(i).isSeasonOver();

                        teamsDB[i].currentYear = teams.get(i).getCurrentSeasonYear();

                        if(teams.get(i).isPlayerControlled() && teams.get(i).getRecruits() != null){
                            recruits = new RecruitDB[teams.get(i).getRecruits().size()];
                            int rIndex = 0;
                            for (Recruit recruit : teams.get(i).getRecruits()) {
                                recruits[rIndex] = new RecruitDB();

                                recruits[rIndex].recruitID = recruit.getId();
                                recruits[rIndex].teamID = teams.get(i).getId();

                                recruits[rIndex].firstName = recruit.getFirstName();
                                recruits[rIndex].lastName = recruit.getLastName();
                                recruits[rIndex].pos = recruit.getPosition();
                                recruits[rIndex].interest = recruit.getInterest();
                                recruits[rIndex].rating = recruit.getRating();
                                recruits[rIndex].isCommitted = recruit.getIsCommitted();
                                rIndex++;
                            }

                        }

                        if(!teams.get(i).getPlayers().get(0).isSavedInProgress()) {
                            numPlayers += teams.get(i).getPlayers().size();
                        }
                        numCoaches += teams.get(i).getCoaches().size();
                    }
                    db.appDAO().insertTeams(teamsDB);
                    if(recruits != null) {
                        db.appDAO().insertRecruits(recruits);
                    }



                    PlayerDB[] players = new PlayerDB[numPlayers];
                    int pIndex = 0;
                    for (int i = 0; i < teams.size(); i++) {
                        for (Player player : teams.get(i).getPlayers()) {
                            if(!player.isSavedInProgress()){
                                players[pIndex] = new PlayerDB();
                                player.prepareForSaving();
                                if (player.getId() != -1) {
                                    players[pIndex].playerId = player.getId();
                                }

                                players[pIndex].teamID = teams.get(i).getId();
                                players[pIndex].lastName = player.getlName();
                                players[pIndex].firstName = player.getfName();
                                players[pIndex].year = player.getYear();
                                players[pIndex].pos = player.getPosition();
                                players[pIndex].trainingAs = player.getTrainingAs();
                                players[pIndex].currentRosterLocation = teams.get(i).getPlayers().indexOf(player);

                                players[pIndex].closeRangeShot = player.getCloseRangeShot();
                                players[pIndex].midRangeShot = player.getMidRangeShot();
                                players[pIndex].longRangeShot = player.getLongRangeShot();
                                players[pIndex].freeThrowShot = player.getFreeThrowShot();
                                players[pIndex].postMove = player.getPostMove();
                                players[pIndex].ballHandling = player.getBallHandling();
                                players[pIndex].passing = player.getPassing();
                                players[pIndex].screening = player.getScreening();
                                players[pIndex].offBallMovement = player.getOffBallMovement();

                                players[pIndex].postDefense = player.getPostDefense();
                                players[pIndex].perimeterDefense = player.getPerimeterDefense();
                                players[pIndex].onBallDefense = player.getOnBallDefense();
                                players[pIndex].offBallDefense = player.getOffBallDefense();
                                players[pIndex].stealing = player.getStealing();
                                players[pIndex].rebounding = player.getRebounding();

                                players[pIndex].stamina = player.getStamina();
                                players[pIndex].aggressiveness = player.getAggressiveness();
                                players[pIndex].workEthic = player.getWorkEthic();

                                players[pIndex].gamesPlayed = player.getGamesPlayed();
                                players[pIndex].totalMinutes = player.getTotalMinutes();

                                players[pIndex].closeRangeShotProgress = player.getCloseRangeShotProgress();
                                players[pIndex].midRangeShotProgress = player.getMidRangeShotProgress();
                                players[pIndex].longRangeShotProgress = player.getLongRangeShotProgress();
                                players[pIndex].freeThrowShotProgress = player.getFreeThrowShotProgress();
                                players[pIndex].postMoveProgress = player.getPostMoveProgress();
                                players[pIndex].ballHandlingProgress = player.getBallHandlingProgress();
                                players[pIndex].passingProgress = player.getPassingProgress();
                                players[pIndex].screeningProgress = player.getScreeningProgress();
                                players[pIndex].offballMovementProgress = player.getOffBallMovementProgress();

                                players[pIndex].postDefenseProgress = player.getPostDefenseProgress();
                                players[pIndex].perimeterDefenseProgress = player.getPerimeterDefenseProgress();
                                players[pIndex].onBallDefenseProgress = player.getOnBallDefenseProgress();
                                players[pIndex].offBallDefenseProgress = player.getOffBallDefenseProgress();
                                players[pIndex].stealingProgress = player.getStealingProgress();
                                players[pIndex].reboundingProgress = player.getReboundingProgress();

                                players[pIndex].staminaProgress = player.getStaminaProgress();


                                pIndex++;
                            }
                        }
                    }
                    playerIndex += numPlayers;

                    db.appDAO().insertPlayers(players);

                    CoachDB[] coaches = new CoachDB[numCoaches];
                    int cIndex = 0;

                    for (int i = 0; i < teams.size(); i++) {
                        for (Coach coach : teams.get(i).getCoaches()) {
                            coaches[cIndex] = new CoachDB();
                            coaches[cIndex].coachID = cIndex + coachIndex;
                            coaches[cIndex].teamID = teams.get(i).getId();

                            coaches[cIndex].firstName = coach.getFirstName();
                            coaches[cIndex].lastName = coach.getLastName();
                            coaches[cIndex].pos = coach.getPosition();

                            coaches[cIndex].shotTeaching = coach.getShotTeaching();
                            coaches[cIndex].ballControlTeaching = coach.getBallControlTeaching();
                            coaches[cIndex].screenTeaching = coach.getScreenTeaching();
                            coaches[cIndex].offPositionTeaching = coach.getOffPositionTeaching();

                            coaches[cIndex].defPositionTeaching = coach.getDefPositionTeaching();
                            coaches[cIndex].defOnBallTeaching = coach.getDefOnBallTeaching();
                            coaches[cIndex].defOffBallTeaching = coach.getDefOffBallTeaching();
                            coaches[cIndex].reboundTeaching = coach.getReboundTeaching();
                            coaches[cIndex].stealTeaching = coach.getStealTeaching();

                            coaches[cIndex].conditioningTeaching = coach.getConditioningTeaching();

                            coaches[cIndex].recruitingAbility = coach.getRecruitingAbility();

                            coaches[cIndex].tendencyToSub = coach.getTendencyToSub();

                            String recuitIds = "";
                            if(coach.getRecruits() != null){
                                for(Recruit r: coach.getRecruits()){
                                    recuitIds += r.getId() + ",";
                                }
                            }

                            coaches[cIndex].recruitIds = recuitIds;

                            cIndex++;
                        }
                    }
                    coachIndex += numCoaches;
                    db.appDAO().insertCoaches(coaches);

                    if(conferences.get(q).getTournaments() != null) {
                        TournamentDB[] tournaments = new TournamentDB[conferences.get(q).getTournaments().size()];
                        for(int x = 0; x < tournaments.length; x++){
                            tournaments[x] = new TournamentDB();
                            tournaments[x].tournamentID = x + tournamentIndex;
                            tournaments[x].name = conferences.get(q).getTournaments().get(x).getName();
                            tournaments[x].hasChampion = conferences.get(q).getTournaments().get(x).isHasChampion();
                            tournaments[x].playAtNeutralCourt = conferences.get(q).getTournaments().get(x).isPlayAtNeutralCourt();


                            tournaments[x].teamIDs = "";
                            tournaments[x].gameIDs = "";
                            for(int z = 0; z < conferences.get(q).getTournaments().get(x).getGames().size(); z++){
                                tournaments[x].gameIDs += conferences.get(q).getTournaments().get(x).getGames().get(z).getId() + ",";
                            }

                            for(int z = 0; z < conferences.get(q).getTournaments().get(x).getTeams().size(); z++){
                                tournaments[x].teamIDs += (conferences.get(q).getTournaments().get(x).getTeams().get(z).getId()) + ",";
                            }

                            tournaments[x].conferenceId = q;
                        }
                        tournamentIndex += tournaments.length;
                        db.appDAO().insertTournaments(tournaments);
                    }
                    teamIndex += teams.size();
                }
                if(championship != null){
                    TournamentDB champ = new TournamentDB();
                    champ.tournamentID = -1;
                    champ.name = championship.getTournament().getName();
                    champ.hasChampion = championship.getTournament().isHasChampion();
                    champ.playAtNeutralCourt = championship.getTournament().isPlayAtNeutralCourt();
                    champ.conferenceId = -1;

                    champ.teamIDs = "";
                    champ.gameIDs = "";

                    for(int z = 0; z < championship.getTournament().getTeams().size(); z++){
                        champ.teamIDs += championship.getTournament().getTeams().get(z).getId() + ",";
                    }

                    GameDB[] games = new GameDB[championship.getGames().size()];
                    for(int z = 0; z < championship.getGames().size(); z++){
                        games[z] = new GameDB();
                        games[z].gameID = z + nationalChampGameIndex;
                        for(int t = 0; t < championship.getTournament().getTeams().size(); t++) {
                            if(championship.getTournament().getTeams().get(t).getId() == championship.getGames().get(z).getHomeTeam().getId()) {
                                games[z].homeTeamID = t;
                            }
                            else if(championship.getTournament().getTeams().get(t).getId() == championship.getGames().get(z).getAwayTeam().getId())
                                games[z].awayTeamID = t;
                        }

                        games[z].homeScore = championship.getGames().get(z).getHomeScore();
                        games[z].awayScore = championship.getGames().get(z).getAwayScore();

                        games[z].isNeutralCourt = championship.getGames().get(z).getIsNeutralCourt();
                        games[z].isPlayed = championship.getGames().get(z).isPlayed();

                        championship.getGames().get(z).setId(z + nationalChampGameIndex);

                        champ.gameIDs += championship.getGames().get(z).getId() + ",";
                    }
                    db.appDAO().insertGames(games);
                    db.appDAO().insertTournaments(champ);
                }
            }

            db.close();
        }

        private void saveGames(){
            if(!db.isOpen()){
                db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
            }

            GameDB[] games = new GameDB[masterSchedule.size()];
            for(int x = 0; x < masterSchedule.size(); x++){
                games[x] = new GameDB();
                games[x].gameID = masterSchedule.get(x).getId();
                games[x].homeTeamID = masterSchedule.get(x).getHomeTeam().getId();
                games[x].awayTeamID = masterSchedule.get(x).getAwayTeam().getId();

                games[x].homeScore = masterSchedule.get(x).getHomeScore();
                games[x].awayScore = masterSchedule.get(x).getAwayScore();

                games[x].isNeutralCourt = masterSchedule.get(x).getIsNeutralCourt();
                games[x].isPlayed = masterSchedule.get(x).isPlayed();
            }

            db.appDAO().insertGames(games);
        }

        private void loadData(){
            if(db != null) {
                TeamDB[] teamsDB = db.appDAO().loadAllTeams();
                PlayerDB[] players = db.appDAO().loadAllPlayers();
                GameDB[] games = db.appDAO().loadAllGames();
                CoachDB[] coaches = db.appDAO().loadAllCoaches();
                RecruitDB[] recruits = db.appDAO().loadAllRecruits();
                ConferenceDB[] conference = db.appDAO().loadAllConferences();
                TournamentDB[] tournaments = db.appDAO().loadAllTournaments();

                conferences = new ArrayList<>();
                masterSchedule = new ArrayList<>();
                for (int c = 0; c < conference.length; c++) {
                    if(conference[c].type == 0) {
                        conferences.add(new StandardTenTeam(conference[c].name, MainActivity.this));
                    }
                    else if(conference[c].type == 1){
                        conferences.add(new StaggeredTenTeam(conference[c].name, MainActivity.this));
                    }
                    else if(conference[c].type == 2){
                        conferences.add(new StandardEightTeam(conference[c].name, MainActivity.this));
                    }

                }

                ArrayList<Team> teams = new ArrayList<>();
                for (int i = 0; i < teamsDB.length; i++) {
                    teams.add(new Team(teamsDB[i].schoolName, teamsDB[i].schoolMascot, teamsDB[i].isPlayerControlled,
                             teamsDB[i].offFavorsThrees, teamsDB[i].defFavorsThrees, teamsDB[i].aggression, teamsDB[i].pace,
                            teamsDB[i].currentYear, teamsDB[i].isSeasonOver, teamsDB[i].colorMain,
                            teamsDB[i].colorDark, teamsDB[i].colorLight, teamsDB[i].id, MainActivity.this));
                }

                int changes;
                do{
                    changes = 0;
                    for(int x = 0; x < players.length - 1; x++){
                        for(int y = x + 1; y < players.length; y++){
                            if(players[x].currentRosterLocation > players[y].currentRosterLocation){
                                PlayerDB temp = players[x];
                                players[x] = players[y];
                                players[y] = temp;
                                changes++;
                            }
                        }
                    }
                }while(changes != 0);

                for (PlayerDB player : players) {
                    teams.get(player.teamID).addPlayer(new Player(player.lastName, player.firstName, player.playerId,
                            player.pos, player.year, player.trainingAs, player.closeRangeShot, player.midRangeShot,
                            player.longRangeShot, player.freeThrowShot, player.postMove, player.ballHandling,
                            player.passing, player.screening, player.offBallMovement, player.postDefense,
                            player.perimeterDefense, player.onBallDefense, player.offBallDefense,
                            player.stealing, player.rebounding, player.stamina, player.aggressiveness,
                            player.workEthic, player.gamesPlayed, player.totalMinutes));


                    teams.get(player.teamID).getPlayers().get(teams.get(player.teamID).getPlayers().size()-1)
                            .setProgress(player.closeRangeShotProgress, player.midRangeShotProgress,
                                    player.longRangeShotProgress, player.freeThrowShotProgress,
                                    player.postMoveProgress, player.ballHandlingProgress,
                                    player.passingProgress, player.screeningProgress, player.offballMovementProgress,
                                    player.postDefenseProgress, player.perimeterDefenseProgress,
                                    player.onBallDefenseProgress, player.offBallDefenseProgress,
                                    player.stealingProgress, player.reboundingProgress,
                                    player.staminaProgress);

                    if(player.savedInProgress){
                        teams.get(player.teamID).getPlayers().get(teams.get(player.teamID).getPlayers().size()-1).setSavedInProgress(true);
                    }
                }

                for (RecruitDB recruit : recruits) {
                    teams.get(recruit.teamID).addRecruit(new Recruit(recruit.firstName, recruit.lastName,
                            recruit.pos, recruit.rating, recruit.interest, recruit.isCommitted,
                            recruit.recruitID));
                }

                for (CoachDB coach : coaches) {
                    teams.get(coach.teamID).addCoach(new Coach(coach.firstName, coach.lastName, coach.pos,
                            coach.shotTeaching, coach.ballControlTeaching, coach.screenTeaching, coach.offPositionTeaching,
                            coach.defPositionTeaching, coach.defOnBallTeaching, coach.defOffBallTeaching,
                            coach.reboundTeaching, coach.stealTeaching, coach.conditioningTeaching,
                            coach.recruitingAbility, coach.tendencyToSub));

                    if(coach.recruitIds.length() > 0) {
                        for (String s : Arrays.asList(coach.recruitIds.split(","))) {
                            int i = Integer.parseInt(s);
                            teams.get(coach.teamID).getCoaches().get(teams.get(coach.teamID).getCoaches().size()-1)
                                    .addRecruit(teams.get(coach.teamID).getRecruits().get(i));
                        }
                    }
                }



                for(int x = 0; x < teams.size(); x++){
                    conferences.get(teamsDB[x].conferenceID).addTeam(teams.get(x));
                }

                masterSchedule = new ArrayList<>();
                for (GameDB game : games) {
                    if(game.gameID < nationalChampGameIndex) {
                        Game g = new Game(teams.get(game.homeTeamID), teams.get(game.awayTeamID),
                                game.gameID, game.homeScore, game.awayScore, game.isInProgress,
                                game.isPlayed, game.isNeutralCourt);
                        masterSchedule.add(g);
                        if(g.getHomeTeam().getConference().equals(g.getAwayTeam().getConference())){
                            g.getHomeTeam().getConference().addGame(g);
                        }
                    }
                }

                if(tournaments.length > 0){
                    championship = null;
                    for(TournamentDB t: tournaments){
                        Tournament tourn = new Tournament(t.name, t.playAtNeutralCourt, t.hasChampion);
                        if(t.conferenceId != -1) {
                            for (String s : Arrays.asList(t.gameIDs.split(","))) {
                                try {
                                    int i = Integer.parseInt(s);
                                    if (i < masterSchedule.size()) {
                                        tourn.addGame(masterSchedule.get(i));
                                    }
                                }catch(NumberFormatException e){
                                    Crashlytics.logException(e);
                                }
                            }
                            for (String s : Arrays.asList(t.teamIDs.split(","))) {
                                try {
                                    int i = Integer.parseInt(s);
                                    for (Team team : conferences.get(t.conferenceId).getTeams()) {
                                        if (team.getId() == i) {
                                            tourn.addTeam(team);
                                        }
                                    }
                                }
                                catch (NumberFormatException e){
                                    Crashlytics.logException(e);
                                }
                            }
                            conferences.get(t.conferenceId).addTournament(tourn);
                        }
                        else{
                            for (String s : Arrays.asList(t.teamIDs.split(","))) {
                                Team team = null;
                                for(Conference c: conferences){
                                    for(Team team1: c.getTeams()){
                                        try {
                                            if (team1.getId() == Integer.parseInt(s)) {
                                                team = team1;
                                            }
                                        }
                                        catch (NumberFormatException e){
                                            Crashlytics.logException(e);
                                        }
                                    }
                                }
                                if(team != null){
                                    tourn.addTeam(team);
                                }
                            }
                            for(GameDB game : games){
                                if(game.gameID >= nationalChampGameIndex){
                                    tourn.addGame(new Game(tourn.getTeams().get(game.homeTeamID),
                                            tourn.getTeams().get(game.awayTeamID),game.gameID,
                                            game.homeScore, game.awayScore, game.isInProgress,
                                            game.isPlayed, game.isNeutralCourt));
                                }
                            }

                            championship = new NationalChampionship(tourn);
                        }
                    }
                }
            }
            for(Conference c: conferences){
                c.getStandings();
            }

            db.close();
        }

        private void clearData(){
            if(db != null){
                db.appDAO().deleteGameStats();
                db.appDAO().deleteRecruitDB();
                db.appDAO().deleteTournaments();
                db.appDAO().deleteGameDB();
                db.appDAO().deletePlayerDB();
                db.appDAO().deleteCoachDB();
                db.appDAO().deleteTeamDB();
                db.appDAO().deleteConferences();
                db.appDAO().deleteGameEvents();
            }
        }

        private void generateNonConferenceGames(){
            ArrayList<Game> nonCon = new ScheduleGenerator().generateSchedule(conferences);
            ArrayList<Game> con = new ArrayList<>();
            for(Conference c: conferences){
                con.addAll(c.getMasterSchedule());
            }
            Collections.shuffle(con);
            Collections.shuffle(nonCon);

            masterSchedule = new ArrayList<>();
            masterSchedule.addAll(nonCon);
            masterSchedule.addAll(con);

            for(int x = 0; x < masterSchedule.size(); x++){
                masterSchedule.get(x).getHomeTeam().addGameToSchedule(masterSchedule.get(x));
                masterSchedule.get(x).getAwayTeam().addGameToSchedule(masterSchedule.get(x));
                masterSchedule.get(x).setId(x);
            }
        }
    }


}




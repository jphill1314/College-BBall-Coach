package com.coaching.jphil.collegebasketballcoach;


import android.arch.persistence.room.Room;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.CoachDB;
import com.coaching.jphil.collegebasketballcoach.Database.ConferenceDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.PlayerDB;
import com.coaching.jphil.collegebasketballcoach.Database.RecruitDB;
import com.coaching.jphil.collegebasketballcoach.Database.TeamDB;
import com.coaching.jphil.collegebasketballcoach.adapters.NavDrawerAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.NationalChampionship;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StaggeredTenTeam;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StandardTenTeam;
import com.coaching.jphil.collegebasketballcoach.fragments.RecruitFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.ScheduleFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StaffFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StandingsFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StrategyFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.TrainingFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView drawerList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    private AppDatabase db;

    public ArrayList<Conference> conferences;

    public Team currentTeam;
    public Conference currentConference;
    public NationalChampionship championship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.drawer_list);

        manager = new LinearLayoutManager(this);
        drawerList.setLayoutManager(manager);

        Drawable[] drawables = new Drawable[7];
        for(int x = 0; x < drawables.length; x++){
            drawables[x] = getResources().getDrawable(R.drawable.ic_assignment_black_24dp);
        }

        adapter = new NavDrawerAdapter(getResources().getStringArray(R.array.drawer_items), drawables);
        drawerList.setAdapter(adapter);

        if(db == null){
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
            new DataAsync().execute("load");
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        new DataAsync().execute("save");
    }

    public void updateFragment(int position){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); i++){
            fm.popBackStack();
        }

        android.support.v4.app.FragmentTransaction t = fm.beginTransaction();

        switch(position){
            case 0:
                t.replace(R.id.content_frame, new RosterFragment());
                break;
            case 1:
                t.replace(R.id.content_frame, new ScheduleFragment());
                break;
            case 2:
                t.replace(R.id.content_frame, new StandingsFragment());
                break;
            case 3:
                t.replace(R.id.content_frame, new RecruitFragment());
                break;
            case 4:
                t.replace(R.id.content_frame, new StrategyFragment());
                break;
            case 5:
                t.replace(R.id.content_frame, new StaffFragment());
                break;
            case 6:
                t.replace(R.id.content_frame, new TrainingFragment());
                break;
        }
        t.commit();
        drawerLayout.closeDrawer(drawerList);
    }

    private void generateConference(String[] names, String[] mascots, String confName, int minRating, int type, boolean player){
        ArrayList<Team> teams = new ArrayList<>();
        Random r = new Random();

        for(int i = 0; i < names.length; i++){
            int rating = r.nextInt(15) + minRating;
            if(i != 0) {
                teams.add(new Team(names[i], mascots[i], getPlayers(10, rating), getCoaches(4, rating), false, this));
            }
            else{
                if(player) {
                    rating = 90;
                    teams.add(new Team(names[i], mascots[i], getPlayers(10, rating), getCoaches(4, rating), true, this));
                }
                else{
                    teams.add(new Team(names[i], mascots[i], getPlayers(10, rating), getCoaches(4, rating), false, this));
                }
            }
        }

        if(conferences == null){
            conferences = new ArrayList<>();
        }

        if(type == 0){
            conferences.add(new StandardTenTeam(confName, teams, this));
        }
        else if(type == 1) {
            conferences.add(new StaggeredTenTeam(confName, teams, this));
        }
    }

    public void startNewSeason(){
        int numFinished = 0;
        championship = null;
        for(Conference c: conferences){
            if(c.isSeasonFinished()) {
                numFinished++;
            }
            else{
                c.generateTournament();
            }
        }

        if(numFinished == conferences.size()){
            for(Conference c: conferences){
                c.startNewSeason();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new RosterFragment())
                    .commit();
        }
    }

    private ArrayList<Player> getPlayers(int numPlayers, int teamRating){
        ArrayList<Player> players = new ArrayList<Player>();
        String[] lastNames = getResources().getStringArray(R.array.last_names);
        String[] firstNames = getResources().getStringArray(R.array.first_names);
        Random r = new Random();

        for (int i = 0; i < 5; i++) {
            players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                    (i % 5) + 1, r.nextInt(4), teamRating));
        }
        for (int i = 5; i < numPlayers; i++) {
            players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                    (i % 5) + 1, r.nextInt(4), teamRating - r.nextInt(10)));
        }
        return players;
    }

    private ArrayList<Coach> getCoaches(int numCoaches, int teamRating){
        ArrayList<Coach> coaches = new ArrayList<Coach>();
        String[] lastNames = getResources().getStringArray(R.array.last_names);
        String[] firstNames = getResources().getStringArray(R.array.first_names);
        Random r = new Random();

        coaches.add(new Coach(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)], 1, teamRating + 5));
        for(int i = 1; i < numCoaches; i++){
            coaches.add(new Coach(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)], 2, teamRating - 5));
        }
        return coaches;
    }

    private class DataAsync extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings){
            if(strings[0].equals("load")){
                loadData();
                return "loaded";
            }
            else if(strings[0].equals("save")){
                saveData();
            }
            else if(strings[0].equals("new season")){
                //saveData();
                return "new season";
            }
            else if(strings[0].equals("delete all")){
                clearData();
                return "data cleared";
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if(result != null) {
                if (result.equals("loaded") || result.equals("new season")) {
                    if (conferences == null) {
                        newGameSetup();
                    } else if (conferences.size() == 0) {
                        newGameSetup();
                    }

                    currentTeam = getPlayerTeam();
                    currentConference = getPlayerConference();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new RosterFragment())
                            .commit();
                } else if (result.equals("data cleared")) {
                    newGameSetup();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new RosterFragment())
                            .commit();
                }
            }
        }

        private void saveData(){
            int gameIndex = 0;
            int teamIndex = 0;
            int coachIndex = 0;
            int playerIndex = 0;

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
                        teamsDB[i].id = i + teamIndex;
                        teamsDB[i].conferenceID = q;
                        teamsDB[i].isPlayerControlled = teams.get(i).isPlayerControlled();
                        teamsDB[i].schoolName = teams.get(i).getSchoolName();
                        teamsDB[i].schoolMascot = teams.get(i).getMascot();

                        teamsDB[i].wins = teams.get(i).getWins();
                        teamsDB[i].loses = teams.get(i).getLoses();

                        teamsDB[i].offFavorsThrees = teams.get(i).getOffenseFavorsThrees();
                        teamsDB[i].defFavorsThrees = teams.get(i).getDefenseFavorsThrees();
                        teamsDB[i].defTendToHelp = teams.get(i).getDefenseTendToHelp();
                        teamsDB[i].pace = teams.get(i).getPace();

                        teamsDB[i].offenseFocus = teams.get(i).getOffenseFocus();
                        teamsDB[i].perimeterFocus = teams.get(i).getPerimeterFocus();
                        teamsDB[i].skillsFocus = teams.get(i).getSkillFocus();

                        if(teams.get(i).isPlayerControlled()){
                            recruits = new RecruitDB[teams.get(i).getRecruits().size()];
                            int rIndex = 0;
                            for (Recruit recruit : teams.get(i).getRecruits()) {
                                recruits[rIndex] = new RecruitDB();

                                recruits[rIndex].recruitID = rIndex;
                                recruits[rIndex].teamID = teamIndex;

                                recruits[rIndex].firstName = recruit.getFirstName();
                                recruits[rIndex].lastName = recruit.getLastName();
                                recruits[rIndex].pos = recruit.getPosition();
                                recruits[rIndex].interest = recruit.getInterest();
                                recruits[rIndex].rating = recruit.getRating();
                                recruits[rIndex].isCommitted = recruit.getIsCommitted();
                                recruits[rIndex].isRecentlyRecruited = recruit.getIsRecentlyRecruited();
                                rIndex++;
                            }

                        }

                        numPlayers += teams.get(i).getPlayers().size();
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
                            players[pIndex] = new PlayerDB();
                            players[pIndex].playerId = pIndex + playerIndex;
                            players[pIndex].teamID = i + teamIndex;
                            players[pIndex].lastName = player.getlName();
                            players[pIndex].firstName = player.getfName();
                            players[pIndex].year = player.getYear();
                            players[pIndex].pos = player.getPosition();
                            players[pIndex].minutes = player.getMinutes();

                            players[pIndex].closeRangeShot = player.getCloseRangeShot();
                            players[pIndex].midRangeShot = player.getMidRangeShot();
                            players[pIndex].longRangeShot = player.getLongRangeShot();
                            players[pIndex].ballHandling = player.getBallHandling();
                            players[pIndex].passing = player.getPassing();
                            players[pIndex].screening = player.getScreening();

                            players[pIndex].postDefense = player.getPostDefense();
                            players[pIndex].perimeterDefense = player.getPerimeterDefense();
                            players[pIndex].onBallDefense = player.getOnBallDefense();
                            players[pIndex].offBallDefense = player.getOffBallDefense();
                            players[pIndex].stealing = player.getStealing();
                            players[pIndex].rebounding = player.getRebounding();

                            players[pIndex].stamina = player.getStamina();

                            players[pIndex].gamesPlayed = player.getGamesPlayed();
                            players[pIndex].totalMinutes = player.getTotalMinutes();

                            pIndex++;
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
                            coaches[cIndex].teamID = i + teamIndex;

                            coaches[cIndex].firstName = coach.getFirstName();
                            coaches[cIndex].lastName = coach.getLastName();
                            coaches[cIndex].pos = coach.getPosition();

                            coaches[cIndex].shotTeaching = coach.getShotTeaching();
                            coaches[cIndex].ballControlTeaching = coach.getBallControlTeaching();
                            coaches[cIndex].screenTeaching = coach.getScreenTeaching();

                            coaches[cIndex].defPositionTeaching = coach.getDefPositionTeaching();
                            coaches[cIndex].defOnBallTeaching = coach.getDefOnBallTeaching();
                            coaches[cIndex].defOffBallTeaching = coach.getDefOffBallTeaching();
                            coaches[cIndex].reboundTeaching = coach.getReboundTeaching();
                            coaches[cIndex].stealTeaching = coach.getStealTeaching();

                            coaches[cIndex].conditioningTeaching = coach.getConditioningTeaching();

                            coaches[cIndex].workingWithGuards = coach.getWorkingWithGuards();
                            coaches[cIndex].workingWithBigs = coach.getWorkingWithBigs();
                            cIndex++;
                        }
                    }
                    coachIndex += numCoaches;
                    db.appDAO().insertCoaches(coaches);


                    Map<String, String> teamIDMap = new HashMap<>();
                    for (int i = 0; i < teams.size(); i++) {
                        teamIDMap.put(teams.get(i).getFullName(), Integer.toString(i+teamIndex));
                    }

                    ArrayList<Game> masterSchedule = conferences.get(q).getMasterSchedule();
                    GameDB[] games = new GameDB[masterSchedule.size()];
                    for (int z = 0; z < masterSchedule.size(); z++) {
                        games[z] = new GameDB();
                        games[z].gameID = z + gameIndex;
                        games[z].homeTeamID = Integer.parseInt(teamIDMap.get(masterSchedule.get(z).getHomeTeamName()));
                        games[z].awayTeamID = Integer.parseInt(teamIDMap.get(masterSchedule.get(z).getAwayTeamName()));

                        games[z].homeScore = masterSchedule.get(z).getHomeScore();
                        games[z].awayScore = masterSchedule.get(z).getAwayScore();

                        games[z].isNeutralCourt = masterSchedule.get(z).getIsNeutralCourt();
                        games[z].isPlayed = masterSchedule.get(z).isPlayed();
                    }
                    gameIndex += masterSchedule.size();
                    teamIndex += teams.size();
                    db.appDAO().insertGames(games);

//                    if(conferences.get(q).getTournaments() != null) {
//                        TournamentDB[] tournaments = new TournamentDB[conferences.get(q).getTournaments().size()];
//                        for(int x = 0; x < tournaments.length; x++){
//                            tournaments[x] = new TournamentDB();
//                            tournaments[x].tournamentID = x;
//                            tournaments[x].name = conferences.get(q).getTournaments().get(x).getName();
//                            tournaments[x].hasChampion = conferences.get(q).getTournaments().get(x).isHasChampion();
//                            tournaments[x].playAtNeutralCourt = conferences.get(q).getTournaments().get(x).isPlayAtNeutralCourt();
//
//
//                        }
//                    } TODO: save and load tournaments
                }
                db.close();
            }
        }

        private void loadData(){
            if(db != null) {
                TeamDB[] teamsDB = db.appDAO().loadAllTeams();
                PlayerDB[] players = db.appDAO().loadAllPlayers();
                GameDB[] games = db.appDAO().loadAllGames();
                CoachDB[] coaches = db.appDAO().loadAllCoaches();
                RecruitDB[] recruits = db.appDAO().loadAllRecruits();
                ConferenceDB[] conference = db.appDAO().loadAllConferences();

                conferences = new ArrayList<>();
                for (int c = 0; c < conference.length; c++) {
                    if(conference[c].type == 0) {
                        conferences.add(new StandardTenTeam(conference[c].name, getApplicationContext()));
                    }
                    else{
                        conferences.add(new StaggeredTenTeam(conference[c].name, getApplicationContext()));
                    }

                }

                ArrayList<Team> teams = new ArrayList<>();
                for (int i = 0; i < teamsDB.length; i++) {
                    teams.add(new Team(teamsDB[i].schoolName, teamsDB[i].schoolMascot, teamsDB[i].isPlayerControlled,
                            teamsDB[i].wins, teamsDB[i].loses, teamsDB[i].offFavorsThrees,
                            teamsDB[i].defFavorsThrees, teamsDB[i].defTendToHelp, teamsDB[i].pace,
                            teamsDB[i].offenseFocus, teamsDB[i].perimeterFocus, teamsDB[i].skillsFocus, getApplicationContext()));
                }

                for (PlayerDB player : players) {
                    teams.get(player.teamID).addPlayer(new Player(player.lastName, player.firstName, player.pos,
                            player.year, player.minutes, player.closeRangeShot, player.midRangeShot,
                            player.longRangeShot, player.ballHandling, player.screening, player.postDefense,
                            player.perimeterDefense, player.onBallDefense, player.offBallDefense,
                            player.stealing, player.rebounding, player.stamina, player.gamesPlayed,
                            player.totalMinutes));
                }

                for (CoachDB coach : coaches) {
                    teams.get(coach.teamID).addCoach(new Coach(coach.firstName, coach.lastName, coach.pos,
                            coach.shotTeaching, coach.ballControlTeaching, coach.screenTeaching, coach.defPositionTeaching,
                            coach.defOnBallTeaching, coach.defOffBallTeaching, coach.reboundTeaching, coach.stealTeaching,
                            coach.conditioningTeaching, coach.workingWithGuards, coach.workingWithBigs));
                }

                for (RecruitDB recruit : recruits) {
                    teams.get(recruit.teamID).addRecruit(new Recruit(recruit.firstName, recruit.lastName,
                            recruit.pos, recruit.rating, recruit.interest, recruit.isCommitted,
                            recruit.isRecentlyRecruited));
                }

                for (GameDB game : games) {
                    conferences.get(teamsDB[game.homeTeamID].conferenceID).addGame(new Game(teams.get(game.homeTeamID), teams.get(game.awayTeamID),
                            game.homeScore, game.awayScore, game.isPlayed, game.isNeutralCourt));
                }

                for(int x = 0; x < teams.size(); x++){
                    conferences.get(teamsDB[x].conferenceID).addTeam(teams.get(x));
                }
            }
        }

        private void clearData(){
            if(db != null){
                db.appDAO().deleteGameDB();
                db.appDAO().deletePlayerDB();
                db.appDAO().deleteCoachDB();
                db.appDAO().deleteTeamDB();
                db.appDAO().deleteConferences();
            }
        }
    }

    private Team getPlayerTeam(){
        for(Conference c: conferences){
            for(Team t: c.getTeams()){
                if(t.isPlayerControlled()){
                    return t;
                }
            }
        }
        return null;
    }

    private Conference getPlayerConference(){
        for(Conference c: conferences){
            for(Team t: c.getTeams()){
                if(t.isPlayerControlled()){
                    return c;
                }
            }
        }
        return null;
    }

    private void newGameSetup(){
        String[] names = {"Wofford", "UNCG", "ETSU", "Furman", "Mercer", "Western Carolina", "Samford", "The Citadel", "Chattanooga", "VMI"};
        String[] mascots = {"Terriers", "Spartans", "Bucs", "Paladins", "Bears", "Catamounts", "Bulldogs", "Bulldogs", "Mocs", "Keydets"};

        generateConference(names, mascots, "Southern Conference", 50, 0, true);

        names = new String[]{"Boston", "New York", "Rhode Island", "Philadelphia", "Michigan", "Ohio", "Chicago", "Indianapolis", "Vermont", "NY State"};
        generateConference(names, mascots, "Northern Conference", 60, 0, false);

        names = new String[]{"San Francisco", "Los Angles", "San Diego", "Seattle", "Portland", "Arizona", "Utah", "Los Vegas", "New Mexico", "Texas"};
        generateConference(names, mascots, "Western Conference", 40, 1, false);

        names = new String[]{"North Dakota", "South Dakota", "Montana", "Oklahoma", "Iowa", "Denver", "Kansas City", "St. Louis", "Colorado", "St. Paul"};
        generateConference(names, mascots, "Central Conference", 55, 0, false);

        championship = null;

    }

    public void generateNationalChampionship(){
        ArrayList<Team> champs = new ArrayList<>();
        for(Conference c: conferences){
            champs.add(c.getChampion());
        }

        championship = new NationalChampionship(champs);
    }
}




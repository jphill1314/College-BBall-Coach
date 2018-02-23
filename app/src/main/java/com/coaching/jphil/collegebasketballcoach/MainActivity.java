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
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;
import com.coaching.jphil.collegebasketballcoach.fragments.RecruitFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.ScheduleFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StaffFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StandingsFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StrategyFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.TrainingFragment;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView drawerList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;

    private AppDatabase db;

    public Team[] teams;
    public int playerTeamIndex = 2;
    public ArrayList<Game> masterSchedule;
    public Tournament tourny;

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

    private void generateTeams(){
        String[] names = {"ETSU", "UNCG", "Wofford", "Furman", "Mercer", "Western Carolina", "Samford", "The Citadel", "Chattanooga", "VMI"};
        String[] mascots = {"Bucs", "Spartans", "Terriers", "Paladins", "Bears", "Catamounts", "Bulldogs", "Bulldogs", "Mocs", "Keydets"};
        teams = new Team[names.length];
        masterSchedule = new ArrayList<Game>();
        Random r = new Random();

        for(int i = 0; i < teams.length; i++){
            int rating = r.nextInt(15) + 50;
            teams[i] = new Team(names[i], mascots[i], getPlayers(10, rating, false), getCoaches(4, rating));
        }
        generateSchedule();
        teams[playerTeamIndex].setRecruits(getRecruits(teams[playerTeamIndex].getOverallRating()));
    }

    public void generateSchedule(){
        if(masterSchedule != null){
            if(masterSchedule.size() > 0){
                masterSchedule.clear();
            }
        }
        else{
            masterSchedule = new ArrayList<Game>();
        }

        for (int x = 0; x < teams.length; x++) {
            for (int y = 0; y < teams.length; y++) {
                if (x != y) {
                    masterSchedule.add(new Game(teams[x], teams[y]));
                }
            }
        }


        Collections.shuffle(masterSchedule);
    }

    public void startNewSeason(){
        if(tourny == null){
            createTournament();
        }
        else {
            tourny = null;
            for (Team team : teams) {
                team.newSeason();
                int maxImprovement = 0;
                for (Coach coach : team.getCoaches()) {
                    maxImprovement += coach.getOverallRating();
                }
                maxImprovement = (int) ((maxImprovement / team.getCoaches().size()) / 7.0);

                Iterator<Player> iterator = team.getPlayers().iterator();
                while (iterator.hasNext()) {
                    Player player = iterator.next();
                    player.newSeason(maxImprovement, team.getOffenseFocus(), team.getPerimeterFocus(), team.getSkillFocus());

                    if (player.getYear() > 3) {
                        iterator.remove();
                    }
                }
                if (team.equals(teams[playerTeamIndex])) {
                    for (Recruit recruit : teams[playerTeamIndex].getRecruits()) {
                        if (recruit.getIsCommitted()) {
                            teams[playerTeamIndex].addPlayer(recruit.startNewSeason());
                        }
                    }
                }
                if (team.getNumberOfPlayers() < 10) {
                    team.addPlayers(getPlayers(10 - team.getNumberOfPlayers(), team.getOverallRating(), true));
                }
            }

            generateSchedule();

            teams[playerTeamIndex].setRecruits(getRecruits(teams[playerTeamIndex].getOverallRating()));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new RosterFragment())
                    .commit();
        }
    }

    public void createTournament(){
        ArrayList<Team> standing = new ArrayList<>(Arrays.asList(teams));
        int changes = 0;

        do{
            changes = 0;
            for(int x = 0; x < standing.size() - 1; x++){
                for(int y = x + 1; y < standing.size(); y++) {
                    if (standing.get(x).getWins() < standing.get(y).getWins()) {
                        Collections.swap(standing, x, y);
                        changes++;
                    }
                    else if(standing.get(x).getWins() == standing.get(y).getWins()){
                        if(standing.get(x).getLoses() > standing.get(y).getLoses()){
                            Collections.swap(standing, x, y);
                            changes++;
                        }
                    }
                }
            }
        }while(changes != 0);

        standing.remove(standing.size()-1);
        standing.remove(standing.size()-1);

        tourny = new Tournament(standing, "Test", true);
    }

    private ArrayList<Player> getPlayers(int numPlayers, int teamRating, boolean onlyFreshman){
        ArrayList<Player> players = new ArrayList<Player>();
        String[] lastNames = getResources().getStringArray(R.array.last_names);
        String[] firstNames = getResources().getStringArray(R.array.first_names);
        Random r = new Random();

        if(!onlyFreshman) {
            for (int i = 0; i < 5; i++) {
                players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                        (i % 5) + 1, r.nextInt(4), teamRating));
            }
            for (int i = 5; i < numPlayers; i++) {
                players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                        (i % 5) + 1, r.nextInt(4), teamRating - r.nextInt(10)));
            }
        }
        else{
            for(int i = 0; i < numPlayers; i++){
                players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                        r.nextInt(4) + 1, 0, teamRating - r.nextInt(10)));
            }
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

    private ArrayList<Recruit> getRecruits(int teamRating){
        ArrayList<Recruit> recruits = new ArrayList<Recruit>();
        String[] lastNames = getResources().getStringArray(R.array.last_names);
        String[] firstNames = getResources().getStringArray(R.array.first_names);

        Random r = new Random();
        for(int x = 0; x < 15; x++){
            recruits.add(new Recruit(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)],
                    (x % 5) + 1, teamRating + 5 - r.nextInt(20), teams[playerTeamIndex]));
        }

        return recruits;
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
                    if (teams == null) {
                        generateTeams();
                    } else if (teams.length == 0) {
                        generateTeams();
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new RosterFragment())
                            .commit();
                } else if (result.equals("data cleared")) {
                    generateTeams();

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new RosterFragment())
                            .commit();
                }
            }
        }

        private void saveData(){
            if(db != null){
                if(!db.isOpen()) {
                    db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
                }
                TeamDB[] teamsDB = new TeamDB[teams.length];
                int numPlayers = 0;
                int numCoaches = 0;

                for (int i = 0; i < teams.length; i++) {
                    teamsDB[i] = new TeamDB();
                    teamsDB[i].id = i;
                    teamsDB[i].schoolName = teams[i].getSchoolName();
                    teamsDB[i].schoolMascot = teams[i].getMascot();

                    teamsDB[i].wins = teams[i].getWins();
                    teamsDB[i].loses = teams[i].getLoses();

                    teamsDB[i].offFavorsThrees = teams[i].getOffenseFavorsThrees();
                    teamsDB[i].defFavorsThrees = teams[i].getDefenseFavorsThrees();
                    teamsDB[i].defTendToHelp = teams[i].getDefenseTendToHelp();
                    teamsDB[i].pace = teams[i].getPace();

                    teamsDB[i].offenseFocus = teams[i].getOffenseFocus();
                    teamsDB[i].perimeterFocus = teams[i].getPerimeterFocus();
                    teamsDB[i].skillsFocus = teams[i].getSkillFocus();

                    numPlayers += teams[i].getPlayers().size();
                    numCoaches += teams[i].getCoaches().size();
                }
                db.appDAO().insertTeams(teamsDB);

                PlayerDB[] players = new PlayerDB[numPlayers];
                int pIndex = 0;
                for (int i = 0; i < teams.length; i++) {
                    for (Player player : teams[i].getPlayers()) {
                        players[pIndex] = new PlayerDB();
                        players[pIndex].playerId = pIndex;
                        players[pIndex].teamID = i;
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

                        pIndex++;
                    }
                }

                db.appDAO().insertPlayers(players);

                CoachDB[] coaches = new CoachDB[numCoaches];
                int cIndex = 0;

                for (int i = 0; i < teams.length; i++) {
                    for (Coach coach : teams[i].getCoaches()) {
                        coaches[cIndex] = new CoachDB();
                        coaches[cIndex].coachID = cIndex;
                        coaches[cIndex].teamID = i;

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

                db.appDAO().insertCoaches(coaches);

                RecruitDB[] recruits = new RecruitDB[teams[playerTeamIndex].getRecruits().size()];
                int rIndex = 0;
                for(Recruit recruit:teams[playerTeamIndex].getRecruits()){
                    recruits[rIndex] = new RecruitDB();

                    recruits[rIndex].recruitID = rIndex;
                    recruits[rIndex].teamID = playerTeamIndex;

                    recruits[rIndex].firstName = recruit.getFirstName();
                    recruits[rIndex].lastName = recruit.getLastName();
                    recruits[rIndex].pos = recruit.getPosition();
                    recruits[rIndex].interest = recruit.getInterest();
                    recruits[rIndex].rating = recruit.getRating();
                    recruits[rIndex].isCommitted = recruit.getIsCommitted();
                    recruits[rIndex].isRecentlyRecruited = recruit.getIsRecentlyRecruited();
                    rIndex++;
                }

                db.appDAO().insertRecruits(recruits);

                Map<String, String> teamIDMap = new HashMap<String, String>();
                for (int i = 0; i < teams.length; i++) {
                    teamIDMap.put(teams[i].getFullName(), Integer.toString(i));
                }

                GameDB[] games = new GameDB[masterSchedule.size()];
                for (int z = 0; z < masterSchedule.size(); z++) {
                    games[z] = new GameDB();
                    games[z].gameID = z;
                    games[z].homeTeamID = Integer.parseInt(teamIDMap.get(masterSchedule.get(z).getHomeTeamName()));
                    games[z].awayTeamID = Integer.parseInt(teamIDMap.get(masterSchedule.get(z).getAwayTeamName()));

                    games[z].homeScore = masterSchedule.get(z).getHomeScore();
                    games[z].awayScore = masterSchedule.get(z).getAwayScore();

                    games[z].isNeutralCourt = masterSchedule.get(z).getIsNeutralCourt();
                    games[z].isPlayed = masterSchedule.get(z).isPlayed();
                }

                db.appDAO().insertGames(games);
                db.close();
            }
        }

        private void loadData(){
            if(db != null){
                TeamDB[] teamsDB = db.appDAO().loadAllTeams();
                PlayerDB[] players = db.appDAO().loadAllPlayers();
                GameDB[] games = db.appDAO().loadAllGames();
                CoachDB[] coaches = db.appDAO().loadAllCoaches();
                RecruitDB[] recruits = db.appDAO().loadAllRecruits();

                teams = new Team[teamsDB.length];
                for(int i = 0; i < teamsDB.length; i++){
                    teams[i] = new Team(teamsDB[i].schoolName, teamsDB[i].schoolMascot,
                            teamsDB[i].wins, teamsDB[i].loses, teamsDB[i].offFavorsThrees,
                            teamsDB[i].defFavorsThrees, teamsDB[i].defTendToHelp, teamsDB[i].pace,
                            teamsDB[i].offenseFocus, teamsDB[i].perimeterFocus, teamsDB[i].skillsFocus);
                }

                for(PlayerDB player: players){
                    teams[player.teamID].addPlayer(new Player(player.lastName, player.firstName, player.year,
                            player.pos, player.minutes, player.closeRangeShot, player.midRangeShot,
                            player.longRangeShot, player.ballHandling, player.screening, player.postDefense,
                            player.perimeterDefense, player.onBallDefense, player.offBallDefense,
                            player.stealing, player.rebounding, player.stamina));
                }

                for(CoachDB coach: coaches){
                    teams[coach.teamID].addCoach(new Coach(coach.firstName, coach.lastName, coach.pos,
                            coach.shotTeaching, coach.ballControlTeaching, coach.screenTeaching, coach.defPositionTeaching,
                            coach.defOnBallTeaching, coach.defOffBallTeaching, coach.reboundTeaching, coach.stealTeaching,
                            coach.conditioningTeaching, coach.workingWithGuards, coach.workingWithBigs));
                }

                for(RecruitDB recruit: recruits){
                    teams[recruit.teamID].addRecruit(new Recruit(recruit.firstName, recruit.lastName,
                            recruit.pos, recruit.rating, recruit.interest, recruit.isCommitted,
                            recruit.isRecentlyRecruited));
                }

                masterSchedule = new ArrayList<Game>();
                for(GameDB game: games){
                    masterSchedule.add(new Game(teams[game.homeTeamID], teams[game.awayTeamID],
                            game.homeScore, game.awayScore, game.isPlayed, game.isNeutralCourt));
                }
            }
        }

        private void clearData(){
            if(db != null){
                db.appDAO().deleteGameDB();
                db.appDAO().deletePlayerDB();
                db.appDAO().deleteCoachDB();
                db.appDAO().deleteTeamDB();
            }
        }
    }
}




package com.coaching.jphil.collegebasketballcoach;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.coaching.jphil.collegebasketballcoach.Database.AppDAO;
import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.PlayerDB;
import com.coaching.jphil.collegebasketballcoach.Database.TeamDB;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.fragments.RosterFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.ScheduleFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StandingsFragment;
import com.coaching.jphil.collegebasketballcoach.fragments.StrategyFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private AppDatabase db;

    public Team[] teams;
    public int playerTeamIndex = 2;
    public ArrayList<Game> masterSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerItems = getResources().getStringArray(R.array.drawer_items);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.drawer_list_item, mDrawerItems);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                updateFragment(i);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        if(db == null){
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "basketballdb").build();
            new LoadDataAsync().execute();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        new SaveDataAsync().execute();
    }

    public void updateFragment(int position){
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();

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
                break;
            case 4:
                t.replace(R.id.content_frame, new StrategyFragment());
                break;
            case 5:
                break;
            case 6:
                break;
        }
        t.commit();
    }

    private void generateTeams(){
        String[] names = {"ETSU", "UNCG", "Wofford", "Furman", "Mercer", "Western Carolina", "Samford", "The Citadel", "Chattanooga", "VMI"};
        String[] mascots = {"Bucs", "Paladins", "Terriers", "Paladins", "Bears", "Catamounts", "Bulldogs", "Bulldogs", "Mocs", "Keydets"};
        teams = new Team[names.length];
        masterSchedule = new ArrayList<Game>();
        Random r = new Random();

        for(int i = 0; i < teams.length; i++){
            teams[i] = new Team(names[i], mascots[i], getPlayers(10, r.nextInt(15) + 50));
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
        new ClearDataAsync().execute();
    }

    private Player[] getPlayers(int numPlayers, int teamRating){
        Player[] players = new Player[numPlayers];
        String[] lastNames = getResources().getStringArray(R.array.last_names);
        String[] firstNames = getResources().getStringArray(R.array.first_names);
        Random r = new Random();

        for(int i = 0; i < 5; i++){
            players[i] = new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],(i%5) + 1, teamRating);
        }
        for(int i = 5; i < numPlayers; i++){
            players[i] = new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],(i%5) + 1, teamRating - r.nextInt(10));
        }
        return players;
    }

    private class SaveDataAsync extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            saveData();
            return null;
        }

        private void saveData(){
            if(db != null){
                TeamDB[] teamsDB = new TeamDB[teams.length];
                int numPlayers = 0;

                for(int i = 0; i < teams.length; i++){
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

                    numPlayers += teams[i].getPlayers().length;
                }
                db.appDAO().insertTeams(teamsDB);

                Map<String, String> teamIDMap = new HashMap<String, String>();
                for(int a = 0; a < teams.length; a++){
                    teamIDMap.put(teams[a].getFullName(), Integer.toString(a));
                }

                PlayerDB[] players = new PlayerDB[numPlayers];
                int pIndex = 0;
                for(int i = 0; i < teams.length; i++){
                    for(Player player: teams[i].getPlayers()){
                        players[pIndex] = new PlayerDB();
                        players[pIndex].playerId = pIndex;
                        players[pIndex].teamID = Integer.parseInt(teamIDMap.get(teams[i].getFullName()));
                        players[pIndex].lastName = player.getlName();
                        players[pIndex].firstName = player.getfName();
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

                GameDB[] games = new GameDB[masterSchedule.size()];
                for(int z = 0; z < masterSchedule.size(); z++){
                    games[z] = new GameDB();
                    games[z].gameID = z;
                    games[z].homeTeamID = Integer.parseInt(teamIDMap.get(masterSchedule.get(z).getHomeTeamName()));
                    games[z].awayTeamID = Integer.parseInt(teamIDMap.get(masterSchedule.get(z).getAwayTeamName()));

                    games[z].homeScore = masterSchedule.get(z).getHomeScore();
                    games[z].awayScore = masterSchedule.get(z).getAwayScore();

                    games[z].isPlayed = masterSchedule.get(z).isPlayed();
                }

                db.appDAO().insertGames(games);
                db.close();
            }
        }
    }

    private class LoadDataAsync extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings){
            loadData();
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            if(teams == null){
                generateTeams();
            }
            else if(teams.length == 0){
                generateTeams();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new RosterFragment())
                    .commit();
        }

        private void loadData(){
            if(db != null){
                TeamDB[] teamsDB = db.appDAO().loadAllTeams();
                PlayerDB[] players = db.appDAO().loadAllPlayers();
                GameDB[] games = db.appDAO().loadAllGames();

                teams = new Team[teamsDB.length];
                for(int i = 0; i < teamsDB.length; i++){
                    teams[i] = new Team(teamsDB[i].schoolName, teamsDB[i].schoolMascot,
                            teamsDB[i].wins, teamsDB[i].loses, teamsDB[i].offFavorsThrees,
                            teamsDB[i].defFavorsThrees, teamsDB[i].defTendToHelp, teamsDB[i].pace);
                }

                for(PlayerDB player: players){
                    teams[player.teamID].addPlayer(new Player(player.lastName, player.firstName,
                            player.pos, player.minutes, player.closeRangeShot, player.midRangeShot,
                            player.longRangeShot, player.ballHandling, player.screening, player.postDefense,
                            player.perimeterDefense, player.onBallDefense, player.offBallDefense,
                            player.stealing, player.rebounding, player.stamina));
                }

                masterSchedule = new ArrayList<Game>();
                for(GameDB game: games){
                    masterSchedule.add(new Game(teams[game.homeTeamID], teams[game.awayTeamID],
                            game.homeScore, game.awayScore, game.isPlayed));
                }
            }
        }
    }

    private class ClearDataAsync extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings){
            if(db != null){
                db.appDAO().deleteGameDB();
                db.appDAO().deletePlayerDB();
                db.appDAO().deleteTeamDB();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            generateTeams();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new RosterFragment())
                    .commit();
        }
    }

}




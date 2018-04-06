package com.coaching.jphil.collegebasketballcoach.fragments.StartScreenFragments;


import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.Database.AppDatabase;
import com.coaching.jphil.collegebasketballcoach.Database.CoachDB;
import com.coaching.jphil.collegebasketballcoach.Database.ConferenceDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.PlayerDB;
import com.coaching.jphil.collegebasketballcoach.Database.RecruitDB;
import com.coaching.jphil.collegebasketballcoach.Database.TeamDB;
import com.coaching.jphil.collegebasketballcoach.Database.TournamentDB;
import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.StartScreenActivity;
import com.coaching.jphil.collegebasketballcoach.adapters.ColorSpinnerAdapter;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
import com.coaching.jphil.collegebasketballcoach.basketballSim.ScheduleGenerator;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StaggeredTenTeam;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StandardEightTeam;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.StandardTenTeam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamCreatorFragment extends Fragment {


    public TeamCreatorFragment() {
        // Required empty public constructor
    }

    private EditText schoolName, mascot, coach;
    private Spinner confSelector, colorSelector;
    private boolean schoolEnter, mascotEnter, coachEnter;
    private FloatingActionButton fab;

    private int teamStrength, confStrength;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team_creator, container, false);

        schoolEnter = false;
        mascotEnter = false;
        coachEnter = false;

        teamStrength = 0;
        confStrength = 0;

        schoolName = view.findViewById(R.id.school_name);
        schoolName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                schoolEnter = (editable.toString().length() > 0);
                showFab();
            }
        });

        mascot = view.findViewById(R.id.mascot);
        mascot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mascotEnter = editable.toString().length() > 0;
                showFab();
            }
        });

        coach = view.findViewById(R.id.coach_name);
        coach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                coachEnter = editable.toString().length() > 0;
                showFab();
            }
        });

        colorSelector = view.findViewById(R.id.color_spinner);
        colorSelector.setAdapter(new ColorSpinnerAdapter(getContext(), R.layout.player_info_list_item,
                getResources().getStringArray(R.array.colors)));


        confSelector = view.findViewById(R.id.conference_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.conference_names));
        confSelector.setAdapter(adapter);

        fab = view.findViewById(R.id.confirm_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setVisibility(View.GONE);
                db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "basketballdb").build();
                new NewGameAsync().execute();
            }
        });

        ((RadioButton)view.findViewById(R.id.team_normal)).setChecked(true);
        ((RadioButton)view.findViewById(R.id.conf_normal)).setChecked(true);

        ((RadioGroup)view.findViewById(R.id.radio_team)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                onRadioButtonClicked(i);
            }
        });

        ((RadioGroup)view.findViewById(R.id.radio_conf)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                onRadioButtonClicked(i);
            }
        });


        return view;
    }

    private void showFab(){
        if(schoolEnter && mascotEnter && coachEnter){
            fab.setVisibility(View.VISIBLE);
        }
        else{
            fab.setVisibility(View.GONE);
        }
    }

    public void onRadioButtonClicked(int id){
        switch (id){
            case R.id.team_good:
                teamStrength = 1;
                break;
            case R.id.team_normal:
                teamStrength = 0;
                break;
            case R.id.team_poor:
                teamStrength = -1;
                break;
            case R.id.conf_good:
                confStrength = 1;
                break;
            case R.id.conf_normal:
                confStrength = 0;
                break;
            case R.id.conf_poor:
                confStrength = -1;
                break;
        }
    }

    private ArrayList<Conference> conferences;
    private ArrayList<Game> masterSchedule;
    AppDatabase db;

    private class NewGameAsync extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings){
            clearData();
            newGameSetup();
            saveData();
            return "";
        }

        @Override
        protected void onPostExecute(String results){
            db = null;
            startActivity(new Intent(getActivity(), MainActivity.class));

            SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
            editor.putBoolean(getString(R.string.shared_pref_never_opened), false);
            editor.apply();
            getActivity().finish();
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

        private void saveData(){
            int teamIndex = 0;
            int coachIndex = 0;
            int playerIndex = 0;

            Log.d("save", "Saving data...");

            if(db != null){
                if(!db.isOpen()) {
                    db = Room.databaseBuilder(getContext(), AppDatabase.class, "basketballdb").build();
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

                        teamsDB[i].colorMain = teams.get(i).getColorMain();
                        teamsDB[i].colorDark = teams.get(i).getColorDark();
                        teamsDB[i].colorLight = teams.get(i).getColorLight();

                        teamsDB[i].offFavorsThrees = teams.get(i).getOffenseFavorsThrees();
                        teamsDB[i].defFavorsThrees = teams.get(i).getDefenseFavorsThrees();
                        teamsDB[i].aggression = teams.get(i).getAggression();
                        teamsDB[i].pace = teams.get(i).getPace();
                        teamsDB[i].isSeasonOver = teams.get(i).isSeasonOver();

                        teamsDB[i].currentYear = teams.get(i).getCurrentSeasonYear();

                        teams.get(i).setId(i + teamIndex);

                        if(teams.get(i).isPlayerControlled() && teams.get(i).getRecruits() != null){
                            recruits = new RecruitDB[teams.get(i).getRecruits().size()];
                            int rIndex = 0;
                            for (Recruit recruit : teams.get(i).getRecruits()) {
                                recruits[rIndex] = new RecruitDB();

                                recruits[rIndex].recruitID = recruit.getId();
                                recruits[rIndex].teamID = teamIndex;

                                recruits[rIndex].firstName = recruit.getFirstName();
                                recruits[rIndex].lastName = recruit.getLastName();
                                recruits[rIndex].pos = recruit.getPosition();
                                recruits[rIndex].interest = recruit.getInterest();
                                recruits[rIndex].rating = recruit.getRating();
                                recruits[rIndex].isCommitted = recruit.getIsCommitted();
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
                            players[pIndex].teamID = i + teamIndex;
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

                    teamIndex += teams.size();
                }
            }

            GameDB[] games = new GameDB[masterSchedule.size()];
            for(int g = 0; g < masterSchedule.size(); g++) {
                games[g] = new GameDB();
                games[g].gameID = masterSchedule.get(g).getId();
                games[g].homeTeamID = masterSchedule.get(g).getHomeTeam().getId();
                games[g].awayTeamID = masterSchedule.get(g).getAwayTeam().getId();

                games[g].homeScore = masterSchedule.get(g).getHomeScore();
                games[g].awayScore = masterSchedule.get(g).getAwayScore();

                games[g].isNeutralCourt = masterSchedule.get(g).getIsNeutralCourt();
                games[g].isPlayed = masterSchedule.get(g).isPlayed();
            }
            db.appDAO().insertGames(games);

            db.close();
        }

        private void newGameSetup(){
            boolean[] playerConf = new boolean[11];
            int[] confStrengths = new int[11];
            Random r = new Random();
            for(int x = 0; x < playerConf.length; x++){
                if(x == confSelector.getSelectedItemPosition()){
                    playerConf[x] = true;
                    if(confStrength == -1){
                        confStrengths[x] = 30 + r.nextInt(20);
                    }
                    else if(confStrength == 1){
                        confStrengths[x] = 50 + + r.nextInt(20);
                    }
                    else{
                        confStrengths[x] = 40 + r.nextInt(20);
                    }
                }
                else{
                    playerConf[x] = false;
                    confStrengths[x] = 35 + r.nextInt(40);
                }
            }


            String[] names = {"Boston", "Providence", "Manhattan", "Albany", "Burlington", "Manchester", "Long Island", "New Haven", "Augusta", "Flushing"};
            String[] mascots = {"Colonist", "Preachers", "Liberty", "Cougars", "Fighting Kittens", "Hunters", "Particles", "Whales", "Lobsters", "Cheesemakers"};
            generateConference(names, mascots, "Northeastern Athletic Association", confStrengths[0], 0, playerConf[0]);

            names = new String[]{"Cleveland", "Detroit", "Milwaukee", "Chicago", "Green Bay", "Indianapolis", "Cincinnati", "Pittsburgh", "Duluth", "Toledo"};
            mascots = new String[]{"Rockers", "Motors", "Horses", "Politicians", "Cheese", "Racers", "Log Drivers", "Forges", "Bears", "Chipmunks"};
            generateConference(names, mascots, "Great Lakes Conference", confStrengths[1], 0, playerConf[1]);

            names = new String[]{"DC", "Richmond", "Charlotte", "Columbia", "Atlanta", "Baltimore", "Nashville", "Raleigh", "Charleston", "Birmingham"};
            mascots = new String[]{"Lobbyists", "Bulls", "Bankers", "Cows", "News", "Fishermen", "Musicians", "Hogs", "Plane Builders", "Letters"};
            generateConference(names, mascots, "Tobacco Conference", confStrengths[2], 0, playerConf[2]);

            names = new String[]{"Tampa", "Tallahassee", "Mobile", "Houston", "San Antonio", "Austin", "Orlando", "Montgomery", "Baton Rogue", "New Orleans"};
            mascots = new String[]{"Crocodiles", "Alligators", "Shipbuilders", "Scientists", "Capybaras", "Camels", "Beavers", "Riders", "Squirrels", "Party"};
            generateConference(names, mascots, "Gulf Coast Conference", confStrengths[3], 0, playerConf[3]);

            names = new String[]{"Las Vegas", "Reno", "Phoenix", "Tucson", "Albuquerque", "El Paso", "Salt Lake City", "Amarillo"};
            mascots = new String[]{"Gamblers", "Skiers", "Drought", "Oxen", "Balloons", "Cowboys", "Saints", "Armadillos"};
            generateConference(names, mascots, "Desert Conference", confStrengths[4], 2, playerConf[4]);

            names = new String[]{"Denver", "Rapid City", "Bismark", "Missoula", "Idaho Falls", "Spokane", "Boulder", "Jackson"};
            mascots = new String[]{"Mountaineers", "Toads", "Bees", "Roosters", "Farmers", "Sloths", "Basilisks", "Gysers"};
            generateConference(names, mascots, "Mountain Athletic Association", confStrengths[5], 2, playerConf[5]);

            names = new String[]{"Dallas", "Ft. Worth", "Oklahoma City", "Kansas City", "St. Louis", "Iowa City", "Omaha", "Minneapolis", "Wichita", "Des Moines"};
            mascots = new String[]{"Engineers", "Planes", "Lemurs", "Wagon Riders", "Explorers", "Iowans", "Ballers", "Koalas", "Wombats", "Harvesters"};
            generateConference(names, mascots, "Middle America Conference", confStrengths[6], 0, playerConf[6]);

            names = new String[]{"Sacramento", "San Francisco", "Los Angeles", "San Diego", "Anaheim", "Long Beach", "San Jose", "Oakland", "Redding", "Fresno"};
            mascots = new String[]{"Panthers", "Seals", "Celebrities", "Captains", "Pufferfish", "Anglerfish", "Nerds", "Freighters", "Redwoods", "Sequoias"};
            generateConference(names, mascots, "California Conference", confStrengths[7], 0, playerConf[7]);

            names = new String[]{"Portland", "Seattle", "Salem", "Olympia", "Anchorage", "Tacoma", "Honolulu", "Yakima"};
            mascots = new String[]{"Hipsters", "Coffeemakers", "Sharks", "Olympians", "Mushers", "Crabs", "Wave Riders", "Yaks"};
            generateConference(names, mascots, "Western Conference", confStrengths[8], 2, playerConf[8]);

            names = new String[]{"Miami", "Jacksonville", "Philadelphia", "Wilmington", "Savannah", "Norfolk", "Dover", "Newark"};
            mascots = new String[]{"Detectives", "Swordfish", "Founders", "Fear", "Jellyfish", "Eagles", "Monsters", "Sea Bass"};
            generateConference(names, mascots, "Atlantic Athletic Association", confStrengths[9], 2, playerConf[9]);

            names = new String[]{"Calgary", "Edmonton", "Winnipeg", "Toronto", "Ottawa", "Montréal", "Québec", "Moncton", "Saskatchewan", "Vancouver"};
            mascots = new String[]{"Polar Bears", "Caribou", "Wolves", "Lions", "Unicorns", "Narwhals", "Révolution", "Chameleons", "Reindeer", "Tigers"};
            generateConference(names, mascots, "Canadian Athletic Conference", confStrengths[10], 0, playerConf[10]);

            generateNonConferenceGames();
        }

        private void generateConference(String[] names, String[] mascots, String confName, int minRating, int type, boolean player){
            ArrayList<Team> teams = new ArrayList<>();
            Random r = new Random();

            for(int i = 0; i < names.length; i++){
                int numPlayers = 12 + r.nextInt(4);
                int rating = r.nextInt(15) + minRating;
                if(i != 0) {
                    teams.add(new Team(names[i], mascots[i], getPlayers(numPlayers, rating), getCoaches(4, rating, false), false, getTeamColors(), getActivity()));
                }
                else{
                    if(player) {
                        rating = minRating + r.nextInt(15) + teamStrength * 10;
                        teams.add(new Team(schoolName.getText().toString(), mascot.getText().toString(), getPlayers(numPlayers, rating), getCoaches(4, rating, true), true,
                                getTeamColors(colorSelector.getSelectedItemPosition()), getActivity()));
                    }
                    else{
                        teams.add(new Team(names[i], mascots[i], getPlayers(numPlayers, rating), getCoaches(4, rating, false), false, getTeamColors(), getActivity()));
                    }
                }
            }

            if(conferences == null){
                conferences = new ArrayList<>();
            }

            if(type == 0){
                conferences.add(new StandardTenTeam(confName, teams, getActivity()));
            }
            else if(type == 1) {
                conferences.add(new StaggeredTenTeam(confName, teams, getActivity()));
            }
            else if(type == 2){
                conferences.add(new StandardEightTeam(confName, teams, getActivity()));
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
                if(i < 10) {
                    players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                            (i % 5) + 1, r.nextInt(4), teamRating - r.nextInt(10)));
                }
                else{
                    players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                            r.nextInt(4) + 1, r.nextInt(4), teamRating - r.nextInt(15)));
                }
            }
            return players;
        }

        private ArrayList<Coach> getCoaches(int numCoaches, int teamRating, boolean isPlayer){
            ArrayList<Coach> coaches = new ArrayList<Coach>();
            String[] lastNames = getResources().getStringArray(R.array.last_names);
            String[] firstNames = getResources().getStringArray(R.array.first_names);
            Random r = new Random();

            if(isPlayer){
                String[] name = coach.getText().toString().split(" ");
                if(name.length > 2){
                    for(int x = 2; x < name.length; x++){
                        name[1] += " " + name[x];
                    }
                }
                else if(name.length == 1){
                    String temp = name[0];
                    name = new String[]{"" , temp};
                }
                coaches.add(new Coach(name[0], name[1], 1, teamRating + 5));
            }
            else {
                coaches.add(new Coach(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)], 1, teamRating + 5));
            }

            for(int i = 1; i < numCoaches; i++){
                coaches.add(new Coach(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)], 2, teamRating - 5));
            }
            return coaches;
        }

        private int[] getTeamColors(){
            Random r = new Random();
            int color = r.nextInt(15);
            int[] colors = new int[3];

            switch (color){
                case 0:
                    colors[0] = R.color.redPrimary;
                    colors[1] = R.color.redDark;
                    colors[2] = R.color.redAccent;
                    break;
                case 1:
                    colors[0] = R.color.pinkPrimary;
                    colors[1] = R.color.pinkDark;
                    colors[2] = R.color.pinkAccent;
                    break;
                case 2:
                    colors[0] = R.color.purplePrimary;
                    colors[1] = R.color.purpleDark;
                    colors[2] = R.color.purpleAccent;
                    break;
                case 3:
                    colors[0] = R.color.deepPurplePrimary;
                    colors[1] = R.color.deepPurpleDark;
                    colors[2] = R.color.deepPurpleAccent;
                    break;
                case 4:
                    colors[0] = R.color.indigoPrimary;
                    colors[1] = R.color.indigoDark;
                    colors[2] = R.color.indigoAccent;
                    break;
                case 5:
                    colors[0] = R.color.bluePrimary;
                    colors[1] = R.color.blueDark;
                    colors[2] = R.color.blueAccent;
                    break;
                case 6:
                    colors[0] = R.color.lightBluePrimary;
                    colors[1] = R.color.lightBlueDark;
                    colors[2] = R.color.lightBlueAccent;
                    break;
                case 7:
                    colors[0] = R.color.cyanPrimary;
                    colors[1] = R.color.cyanDark;
                    colors[2] = R.color.cyanAccent;
                    break;
                case 8:
                    colors[0] = R.color.tealPrimary;
                    colors[1] = R.color.tealDark;
                    colors[2] = R.color.tealAccent;
                    break;
                case 9:
                    colors[0] = R.color.greenPrimary;
                    colors[1] = R.color.greenDark;
                    colors[2] = R.color.greenAccent;
                    break;
                case 10:
                    colors[0] = R.color.lightGreenPrimary;
                    colors[1] = R.color.lightGreenDark;
                    colors[2] = R.color.lightGreenAccent;
                    break;
                case 11:
                    colors[0] = R.color.yellowPrimary;
                    colors[1] = R.color.yellowDark;
                    colors[2] = R.color.yellowAccent;
                    break;
                case 12:
                    colors[0] = R.color.orangePrimary;
                    colors[1] = R.color.orangeDark;
                    colors[2] = R.color.orangeAccent;
                    break;
                case 13:
                    colors[0] = R.color.deepOrangePrimary;
                    colors[1] = R.color.deepOrangeDark;
                    colors[2] = R.color.deepOrangeAccent;
                    break;
                case 14:
                    colors[0] = R.color.blueGreyPrimary;
                    colors[1] = R.color.blueGreyDark;
                    colors[2] = R.color.blueGreyAccent;
                    break;
            }

            return colors;
        }

        private int[] getTeamColors(int color){
            int[] colors = new int[3];

            switch (color){
                case 0:
                    colors[0] = R.color.redPrimary;
                    colors[1] = R.color.redDark;
                    colors[2] = R.color.redAccent;
                    break;
                case 1:
                    colors[0] = R.color.pinkPrimary;
                    colors[1] = R.color.pinkDark;
                    colors[2] = R.color.pinkAccent;
                    break;
                case 2:
                    colors[0] = R.color.purplePrimary;
                    colors[1] = R.color.purpleDark;
                    colors[2] = R.color.purpleAccent;
                    break;
                case 3:
                    colors[0] = R.color.deepPurplePrimary;
                    colors[1] = R.color.deepPurpleDark;
                    colors[2] = R.color.deepPurpleAccent;
                    break;
                case 4:
                    colors[0] = R.color.indigoPrimary;
                    colors[1] = R.color.indigoDark;
                    colors[2] = R.color.indigoAccent;
                    break;
                case 5:
                    colors[0] = R.color.bluePrimary;
                    colors[1] = R.color.blueDark;
                    colors[2] = R.color.blueAccent;
                    break;
                case 6:
                    colors[0] = R.color.lightBluePrimary;
                    colors[1] = R.color.lightBlueDark;
                    colors[2] = R.color.lightBlueAccent;
                    break;
                case 7:
                    colors[0] = R.color.cyanPrimary;
                    colors[1] = R.color.cyanDark;
                    colors[2] = R.color.cyanAccent;
                    break;
                case 8:
                    colors[0] = R.color.tealPrimary;
                    colors[1] = R.color.tealDark;
                    colors[2] = R.color.tealAccent;
                    break;
                case 9:
                    colors[0] = R.color.greenPrimary;
                    colors[1] = R.color.greenDark;
                    colors[2] = R.color.greenAccent;
                    break;
                case 10:
                    colors[0] = R.color.lightGreenPrimary;
                    colors[1] = R.color.lightGreenDark;
                    colors[2] = R.color.lightGreenAccent;
                    break;
                case 11:
                    colors[0] = R.color.yellowPrimary;
                    colors[1] = R.color.yellowDark;
                    colors[2] = R.color.yellowAccent;
                    break;
                case 12:
                    colors[0] = R.color.orangePrimary;
                    colors[1] = R.color.orangeDark;
                    colors[2] = R.color.orangeAccent;
                    break;
                case 13:
                    colors[0] = R.color.deepOrangePrimary;
                    colors[1] = R.color.deepOrangeDark;
                    colors[2] = R.color.deepOrangeAccent;
                    break;
                case 14:
                    colors[0] = R.color.blueGreyPrimary;
                    colors[1] = R.color.blueGreyDark;
                    colors[2] = R.color.blueGreyAccent;
                    break;
            }

            return colors;
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

            for(Conference c: conferences){
                for(Team t: c.getTeams()){
                    Log.d("game", t.getFullName() + " has " + t.getNumberOfGames() + " games scheduled");
                }
            }
        }
    }
}

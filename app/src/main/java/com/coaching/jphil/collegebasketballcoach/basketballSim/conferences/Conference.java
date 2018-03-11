package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.content.Context;
import android.util.Log;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Coach;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Recruit;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Jake on 3/1/2018.
 */

public abstract class Conference {

    private String name;
    private ArrayList<Team> teams;
    private ArrayList<Game> masterSchedule;
    private ArrayList<Tournament> tournaments;

    private Context context;

    public Conference(String name, ArrayList<Team> teams, Context context){
        this.name = name;
        this.teams = teams;
        this.context = context;

        generateMasterSchedule();
        for(Team t: teams){
            if(t.isPlayerControlled()){
                t.setRecruits(getRecruits(t.getOverallRating(), t));
            }
        }
        tournaments = null;
    }

    public Conference(String name, Context context){
        this.name = name;
        this.context = context;
    }

    public void addTeam(Team team){
        if(teams == null){
            teams = new ArrayList<>();
        }
        teams.add(team);
    }

    public void addGame(Game game){
        if(masterSchedule == null){
            masterSchedule = new ArrayList<>();
        }
        masterSchedule.add(game);
    }

    public void addTournament(Tournament tournament){
        if(tournaments == null){
            tournaments = new ArrayList<>();
        }
        tournaments.add(tournament);
    }

    public void startNewSeason(){
        generateMasterSchedule();
        for(Team t: teams){
            if(t.isPlayerControlled()){
                t.setRecruits(getRecruits(t.getOverallRating(), t));
            }
            t.newSeason();
        }
        tournaments = null;
    }

    private ArrayList<Recruit> getRecruits(int teamRating, Team team){
        ArrayList<Recruit> recruits = new ArrayList<>();
        String[] lastNames = context.getResources().getStringArray(R.array.last_names);
        String[] firstNames = context.getResources().getStringArray(R.array.first_names);

        Random r = new Random();
        for(int x = 0; x < 15; x++){
            recruits.add(new Recruit(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)],
                    (x % 5) + 1, teamRating + 5 - r.nextInt(20), team));
        }

        return recruits;
    }

    private void generateMasterSchedule(){
        // TODO: improve when the date of the games matter
        if(masterSchedule != null){
            if(masterSchedule.size() > 0){
                masterSchedule.clear();
            }
        }
        else{
            masterSchedule = new ArrayList<>();
        }

        for (int x = 0; x < teams.size(); x++) {
            for (int y = 0; y < teams.size(); y++) {
                if (x != y) {
                    masterSchedule.add(new Game(teams.get(x), teams.get(y)));
                    Log.v("Schedule", masterSchedule.get(masterSchedule.size()-1).getHomeTeamName() + " vs. " +
                            masterSchedule.get(masterSchedule.size()-1).getAwayTeamName());
                }
            }
        }
        Collections.shuffle(masterSchedule);
    }

    public void addTournamentGames(){
        if(tournaments != null){
            for(Tournament t: tournaments){
                for(Game g: t.getGames()){
                    if(!masterSchedule.contains(g)) {
                        addGame(g);
                    }
                }
            }
        }
    }

    public ArrayList<Team> getStandings(){
        ArrayList<Team> standing = new ArrayList<>(teams);

        int changes = 0;
        //TODO: sort by win percentage instead of # of wins
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

        return standing;
    }

    public ArrayList<Game> getMasterSchedule(){
        return masterSchedule;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Tournament> getTournaments(){
        return tournaments;
    }

    public ArrayList<Team> getTeams(){
        return teams;
    }

    public boolean isInPostSeason(){
        return tournaments != null;
    }

    public abstract void generateTournament();

    public abstract boolean isSeasonFinished();

    public abstract Team getChampion();

    public abstract int getType();
}

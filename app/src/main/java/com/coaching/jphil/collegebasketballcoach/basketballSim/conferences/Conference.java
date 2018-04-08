package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.content.Context;

import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jake on 3/1/2018.
 */

public abstract class Conference {

    private String name;
    private ArrayList<Team> teams;
    private ArrayList<Game> masterSchedule;
    private ArrayList<Tournament> tournaments;

    private int id;

    private Context context;

    public Conference(String name, ArrayList<Team> teams, Context context){
        this.name = name;
        this.teams = teams;
        this.context = context;

        generateMasterSchedule();
        for(Team t: teams){
            t.setConference(this);
            if(t.isPlayerControlled()){
                t.firstSeason();
            }
        }
        tournaments = null;
    }

    public Conference(String name, Context context){
        this.name = name;
        this.context = context;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public void addTeam(Team team){
        if(teams == null){
            teams = new ArrayList<>();
        }
        teams.add(team);
        team.setConference(this);
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
        tournaments = null;
        for(Team t: teams) {
            t.newSeason();
        }
        generateMasterSchedule();

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
                }
            }
        }
        Collections.shuffle(masterSchedule);
    }

    void addTournamentGames(){
        if(tournaments != null){
            for(Tournament t: tournaments){
                for(Game g: t.getGames()){
                    if(!masterSchedule.contains(g)) {
                        addGame(g);
                    }
                    ((MainActivity)context).addGameToMasterSchedule(g);
                }
            }
        }
    }

    public ArrayList<Team> getStandings(){
        ArrayList<Team> standing = teams;
        int totalGames = (teams.size() * 2) - 2;

        int changes ;
        do{
            changes = 0;
            for(int x = 0; x < standing.size() - 1; x++){
                for(int y = x + 1; y < standing.size(); y++) {
                    if(standing.get(x).getConferenceWinPercent(totalGames) < standing.get(y).getConferenceWinPercent(totalGames)){
                        Collections.swap(standing, x, y);
                    }
                    else if(standing.get(x).getConferenceWinPercent(totalGames) == standing.get(y).getConferenceWinPercent(totalGames)){
                        if(standing.get(x).getConferenceWins(totalGames) < standing.get(y).getConferenceWins(totalGames)){
                            Collections.swap(standing, x, y);
                        }
                        else if (standing.get(x).getConferenceWins(totalGames) == standing.get(y).getConferenceWins(totalGames)){
                            if(standing.get(x).getWinPercent() < standing.get(y).getWinPercent()) {
                                Collections.swap(standing, x, y);
                                changes++;
                            }
                            else if(standing.get(x).getWinPercent() == standing.get(y).getWinPercent()) {
                                if (standing.get(x).getWins() < standing.get(y).getWins()) {
                                    Collections.swap(standing, x, y);
                                    changes++;
                                }
                            }
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

    boolean allGamesPlayed(){
        for(Tournament t: getTournaments()){
            for(Game g: t.getGames()){
                if(!g.isPlayed()){
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<Game> getTournamentGames(){
        if(tournaments == null){
            return null;
        }

        ArrayList<Game> games = new ArrayList<>();
        for(Tournament t: tournaments){
            games.addAll(t.getGames());
        }
        return games;
    }

    public abstract void generateTournament();

    public abstract boolean isSeasonFinished();

    public abstract Team getChampion();

    public abstract int getType();
}

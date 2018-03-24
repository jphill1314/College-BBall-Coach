package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.util.Log;

import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;

/**
 * Created by Jake on 3/3/2018.
 */

public class NationalChampionship {

    private ArrayList<Team> teams;
    private Tournament tournament;

    private boolean hasChampion;

    public NationalChampionship(ArrayList<Team> teams){
        this.teams = teams;
        hasChampion = false;
        generateChampionship();
    }

    public NationalChampionship(Tournament tournament){
        this.tournament = tournament;
        teams = this.tournament.getTeams();
    }

    private void generateChampionship(){
        tournament = new Tournament(teams, "National Championship", true);
    }

    public void generateNextRound(){
        if(!tournament.isHasChampion()) {
            tournament.generateNextRound();
        }
        else{
            hasChampion = true;
            for(Game g: tournament.getGames()){
                Log.d("Nat Champ", g.getHomeTeamName() + " vs " + g.getAwayTeamName());
            }
        }
    }

    public ArrayList<Game> getGames(){
        return tournament.getGames();
    }

    public boolean hasChampion(){
        return hasChampion;
    }

    public Tournament getTournament(){
        return tournament;
    }

    public ArrayList<Team> getTeams(){
        return teams;
    }

}

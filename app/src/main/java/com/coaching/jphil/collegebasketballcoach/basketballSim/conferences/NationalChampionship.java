package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

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

    private void generateChampionship(){
        tournament = new Tournament(teams, "National Championship", true);
    }

    public void playNextRound(){
        if(!tournament.isHasChampion()) {
            tournament.playNextRound();
        }
        else{
            hasChampion = true;
        }
    }

    public boolean hasChampion(){
        return hasChampion;
    }
}

package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.content.Context;

import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;

/**
 * Created by Jake on 3/22/2018.
 */

public class StandardEightTeam extends Conference{

    public StandardEightTeam(String name, ArrayList<Team> teams, Context context){
        super(name, teams, context);
    }

    public StandardEightTeam(String name, Context context){
        super(name, context);
    }

    @Override
    public void generateTournament() {
        ArrayList<Team> teams = getStandings();
        if(getTournaments() == null) {
            addTournament(new Tournament(teams, getName() + " Championship", true));
        }
        else if(allGamesPlayed()){
            for(Tournament t: getTournaments()){
                t.playNextRound();
            }
        }
        addTournamentGames();
    }

    @Override
    public boolean isSeasonFinished(){
        if(getTournaments() != null) {
            if (getTournaments().size() == 1) {
                if (getTournaments().get(0).isHasChampion()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Team getChampion(){
        if(isSeasonFinished()){
            return getTournaments().get(0).getChampion();
        }
        return null;
    }

    @Override
    public int getType(){
        return 2;
    }
}

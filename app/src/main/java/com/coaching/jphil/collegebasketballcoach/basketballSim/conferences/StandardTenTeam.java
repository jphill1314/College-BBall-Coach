package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.content.Context;

import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;

/**
 * Created by Jake on 3/1/2018.
 */

public class StandardTenTeam extends Conference {

    public StandardTenTeam(String name, ArrayList<Team> teams, Context context){
        super(name, teams, context);
    }

    public StandardTenTeam(String name, Context context){
        super(name, context);
    }

    @Override
    public void generateTournament() {
        ArrayList<Team> teams = getStandings();
        if(getTournaments() == null) {
            ArrayList<Team> PlayIn7v10 = new ArrayList<>();
            ArrayList<Team> PlayIn8v9 = new ArrayList<>();

            PlayIn7v10.add(teams.get(6));
            PlayIn7v10.add(teams.get(9));

            PlayIn8v9.add(teams.get(7));
            PlayIn8v9.add(teams.get(8));

            addTournament(new Tournament(PlayIn7v10, "7 vs 10 Play In", true));
            addTournament(new Tournament(PlayIn8v9, "8 vs 9 Play in", true));
        }
        else if(getTournaments().size() == 2){
            for(Tournament t: getTournaments()){
                while(!t.isHasChampion()) {
                    t.playNextRound();
                }
            }
            if(getTournaments().get(1).getChampion().equals(teams.get(7))){
                teams.remove(8);
            }
            else{
                teams.remove(7);
            }

            if(getTournaments().get(0).getChampion().equals(teams.get(6))){
                teams.remove(8);
            }
            else{
                teams.remove(6);
            }
            addTournament(new Tournament(teams, getName() + " Championship", true));
        }
        else if(getTournaments().size() == 3){
            Tournament t = getTournaments().get(2);
            t.playNextRound();
        }
        addTournamentGames();
    }

    @Override
    public boolean isSeasonFinished(){
        if(getTournaments() != null) {
            if (getTournaments().size() == 3) {
                if (getTournaments().get(2).isHasChampion()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Team getChampion(){
        if(isSeasonFinished()){
            return getTournaments().get(2).getChampion();
        }
        return null;
    }

    @Override
    public int getType(){
        return 0;
    }
}

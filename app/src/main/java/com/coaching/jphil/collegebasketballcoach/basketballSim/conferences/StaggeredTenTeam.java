package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.content.Context;

import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;

/**
 * Created by Jake on 3/2/2018.
 */

public class StaggeredTenTeam extends Conference {

    public StaggeredTenTeam(String name, ArrayList<Team> teams, Context context){
        super(name, teams, context);
    }

    public StaggeredTenTeam(String name, Context context){
        super(name, context);
    }

    @Override
    public void generateTournament() {
        ArrayList<Team> teams = getStandings();
        if(getTournaments() == null) {
            ArrayList<Team> FirstRound58 = new ArrayList<>();
            ArrayList<Team> FirstRound67 = new ArrayList<>();

            FirstRound58.add(teams.get(4));
            FirstRound58.add(teams.get(7));

            FirstRound67.add(teams.get(5));
            FirstRound67.add(teams.get(6));

            addTournament(new Tournament(FirstRound58, getName() + " Championship First Round", true));
            addTournament(new Tournament(FirstRound67,  getName() + " Championship First Round", true));
        }
        else if(getTournaments().size() == 2){
            for(Tournament t: getTournaments()){
                while(!t.isHasChampion()) {
                    t.playNextRound();
                }
            }
            ArrayList<Team> SecondRound4 = new ArrayList<>();
            ArrayList<Team> SecondRound3 = new ArrayList<>();

            SecondRound4.add(teams.get(3));
            SecondRound4.add(getTournaments().get(0).getChampion());

            SecondRound3.add(teams.get(2));
            SecondRound3.add(getTournaments().get(1).getChampion());

            addTournament(new Tournament(SecondRound4, getName() + " Championship Second Round", true));
            addTournament(new Tournament(SecondRound3, getName() + " Championship Second Round", true));
        }
        else if(getTournaments().size() == 4){
            for(Tournament t: getTournaments()){
                while(!t.isHasChampion()) {
                    t.playNextRound();
                }
            }
            ArrayList<Team> FinalFour = new ArrayList<>();

            FinalFour.add(teams.get(0));
            FinalFour.add(teams.get(1));
            FinalFour.add(getTournaments().get(2).getChampion());
            FinalFour.add(getTournaments().get(3).getChampion());

            addTournament(new Tournament(FinalFour, getName() + " Championship", true));
        }
        else if(getTournaments().size() == 5){
            Tournament t = getTournaments().get(4);
            t.playNextRound();
        }
        addTournamentGames();
    }

    @Override
    public boolean isSeasonFinished(){
        if(getTournaments() != null) {
            if (getTournaments().size() == 5) {
                if (getTournaments().get(4).isHasChampion()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Team getChampion(){
        if(isSeasonFinished()){
            return getTournaments().get(4).getChampion();
        }
        return null;
    }

    @Override
    public int getType(){
        return 1;
    }
}

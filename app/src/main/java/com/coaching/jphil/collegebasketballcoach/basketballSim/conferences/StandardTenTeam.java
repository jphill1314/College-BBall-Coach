package com.coaching.jphil.collegebasketballcoach.basketballSim.conferences;

import android.content.Context;
import android.util.Log;

import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Tournament;

import java.util.ArrayList;
import java.util.Iterator;

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
        ArrayList<Team> teams = new ArrayList<>(getTeams());
        if(getTournaments() == null) {
            ArrayList<Team> PlayIn7v10 = new ArrayList<>();
            ArrayList<Team> PlayIn8v9 = new ArrayList<>();

            PlayIn7v10.add(teams.get(6));
            PlayIn7v10.add(teams.get(9));

            PlayIn8v9.add(teams.get(7));
            PlayIn8v9.add(teams.get(8));

            addTournament(new Tournament(PlayIn8v9, "8 vs 9 Play in", true));
            addTournament(new Tournament(PlayIn7v10, "7 vs 10 Play In", true));
        }
        else if(getTournaments().size() == 2 && allGamesPlayed()){
            Iterator<Team> itr = teams.iterator();
            while(itr.hasNext()){
                Team t = itr.next();
                Log.d("Team", "Team: " + t.getFullName());
                if(t.isSeasonOver()){
                    Log.d("Team", "Removed");
                    itr.remove();
                }
            }
            addTournament(new Tournament(teams, getName() + " Championship", true));
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

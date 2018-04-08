package com.coaching.jphil.collegebasketballcoach.basketballSim;

import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by jphil on 3/17/2018.
 */

public class ScheduleGenerator {

    public ScheduleGenerator(){}

    private int numberOfGames = 25;

    public ArrayList<Game> generateSchedule(ArrayList<Conference> conferences){
        ArrayList<Game> masterSchedule = new ArrayList<>();
        Random r = new Random();
        ArrayList<Team> allTeams = new ArrayList<>();
        for(Conference c: conferences){
            allTeams.addAll(c.getTeams());
        }

        Collections.shuffle(allTeams);

        for(int x = 0; x < allTeams.size() - conferences.get(conferences.size()-1).getTeams().size(); x++){
            Team t = allTeams.get(x);

            while(t.getNumberOfGames() < numberOfGames){
                Team opponent = allTeams.get(r.nextInt(allTeams.size()-x) + x);
                int repeats = 0;
                while(t.getOpponents().contains(opponent) || opponent.getNumberOfGames() >= numberOfGames || t.equals(opponent)){
                    opponent = allTeams.get(r.nextInt(allTeams.size()-x) + x);
                    repeats++;
                    if(repeats > 100){
                        return masterSchedule;
                    }
                }

                if(r.nextBoolean()) {
                    masterSchedule.add(new Game(t, opponent)); // add dates here later
                }
                else{
                    masterSchedule.add(new Game(opponent, t));
                }
            }
        }
        return masterSchedule;
    }
}

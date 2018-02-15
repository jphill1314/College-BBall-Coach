package com.coaching.jphil.collegebasketballcoach.basketballSim;

/**
 * Created by jphil on 2/14/2018.
 */

public class Game {

    Team homeTeam, awayTeam;

    public Game(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public String getHomeTeamName(){
        return homeTeam.getFullName();
    }

    public String getAwayTeamName(){
        return awayTeam.getFullName();
    }
}

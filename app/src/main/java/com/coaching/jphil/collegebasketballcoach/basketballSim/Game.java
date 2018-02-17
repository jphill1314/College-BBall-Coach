package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Game {

    private int homeCourtAdvantage = 3;
    private int scoreVariability = 14; // +/- how much the margin can vary from its relative efficiency

    private Team homeTeam, awayTeam;
    private int homeScore, awayScore;

    public Game(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        homeScore = -1;
        awayScore = -1;
    }

    public String getHomeTeamName(){
        return homeTeam.getFullName();
    }

    public String getAwayTeamName(){
        return awayTeam.getFullName();
    }

    public Team getHomeTeam(){
        return homeTeam;
    }

    public Team getAwayTeam(){
        return awayTeam;
    }

    public int getHomeScore(){
        return homeScore;
    }

    public int getAwayScore(){
        return awayScore;
    }

    public String getFormattedScore(){
        if(homeScore != -1 && awayScore != -1) {
            return homeScore + " - " + awayScore;
        }
        else{
            return " - ";
        }
    }

    public void simulateGame(){
        // The big issue with the current sim is that it doesn't take into account how teams
        // would play against each other. For example if one team wants to let the other team
        // shoot a bunch of threes, that should give that team a boost to their 3-point shooting

        int pace = (int)((homeTeam.getPace() + awayTeam.getPace()) / 2.0);
        Random r = new Random();
        if(homeScore == -1 && awayScore == -1){
            int homeMargin = (int)((pace / 100.0) * (homeTeam.getTotalEfficiency() - awayTeam.getTotalEfficiency()));

            homeMargin += 2 * r.nextInt(scoreVariability) - scoreVariability;
            homeMargin += homeCourtAdvantage;

            homeScore = (int) ((pace / 100.0) * homeTeam.getOffensiveEfficiency());
            awayScore = homeScore - homeMargin;
        }
    }
}

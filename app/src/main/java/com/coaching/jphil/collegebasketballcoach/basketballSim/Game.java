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
    private boolean isNeutralCourt;
    private boolean isPlayed;

    public Game(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        isPlayed = false;
        isNeutralCourt = false;
    }

    public Game(Team homeTeam, Team awayTeam, boolean isNeutralCourt){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.isNeutralCourt = false;
        isPlayed = false;
    }

    public Game(Team homeTeam, Team awayTeam, int homeScore, int awayScore, boolean isPlayed, boolean isNeutralCourt){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.isPlayed = isPlayed;
        this.isNeutralCourt = isNeutralCourt;
    }

    public String getHomeTeamName(){
        return homeTeam.getFullName();
    }

    public String getAwayTeamName(){
        return awayTeam.getFullName();
    }

    public boolean isPlayed(){
        return isPlayed;
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

    public boolean getIsNeutralCourt(){
        return isNeutralCourt;
    }

    public void setIsNeutralCourt(boolean isNeutralCourt){
        this.isNeutralCourt = isNeutralCourt;
    }

    public boolean homeTeamWin(){
        if(isPlayed && (homeScore > awayScore)){
            return true;
        }
        else{
            return false;
        }
    }

    public String getFormattedScore(){
        if(isPlayed) {
            return homeScore + " - " + awayScore;
        }
        else{
            return " - ";
        }
    }

    public boolean simulateGame(){
        // The big issue with the current sim is that it doesn't take into account how teams
        // would play against each other. For example if one team wants to let the other team
        // shoot a bunch of threes, that should give that team a boost to their 3-point shooting
        if(homeTeam.getTotalMinutes() == 200 && awayTeam.getTotalMinutes() == 200) {
            int pace = (int) ((homeTeam.getPace() + awayTeam.getPace()) / 2.0);
            Random r = new Random();
            if (!isPlayed) {
                int homeMargin = (int) ((pace / 100.0) * (homeTeam.getTotalEfficiency() - awayTeam.getTotalEfficiency()));

                homeMargin += 2 * r.nextInt(scoreVariability) - scoreVariability;
                if(!isNeutralCourt) {
                    homeMargin += homeCourtAdvantage;
                }

                homeScore = (int) ((pace / 100.0) * homeTeam.getOffensiveEfficiency());
                awayScore = homeScore - homeMargin;
            }

            isPlayed = true;
            homeTeam.playGame(homeTeamWin());
            awayTeam.playGame(!homeTeamWin());
            return true;
        }
        else{
            return false;
        }
    }
}

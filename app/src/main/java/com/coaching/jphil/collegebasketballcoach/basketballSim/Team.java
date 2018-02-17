package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jphil on 2/14/2018.
 */

public class Team {

    private double maxOffensiveEfficiency = 120.0;
    private double minDefensiveEfficiency = 70.0;

    private Player[] players;
    private int gamesPlayed;

    private int wins, loses;

    private String schoolName, mascot;

    // Strategy
    private int offenseFavorsThrees = 50;
    private int defenseFavorsThrees = 50;
    private int defenseTendToHelp = 50;
    private int pace = 70;

    public Team(String schoolName, String mascot, Player[] players){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.players = players;
        gamesPlayed = 0;
        wins = 0;
        loses = 0;
    }

    public String getFullName(){
        return schoolName + " " + mascot;
    }

    public Player[] getPlayers(){
        return players;
    }

    public int getGamesPlayed(){
        return gamesPlayed;
    }

    public void playGame(boolean wonGame){
        gamesPlayed++;
        if(wonGame){
            wins++;
        }
        else{
            loses++;
        }
    }

    public int getWins(){
        return wins;
    }

    public int getLoses(){
        return loses;
    }

    public void setOffenseFavorsThrees(int value){
        offenseFavorsThrees = value;
    }

    public void setDefenseFavorsThrees(int value){
        defenseFavorsThrees = value;
    }

    public void setDefenseTendToHelp(int value){
        defenseTendToHelp = value;
    }

    public void setPace(int value){
        pace = value;
    }

    public int getOffenseFavorsThrees() {
        return offenseFavorsThrees;
    }

    public int getDefenseFavorsThrees() {
        return defenseFavorsThrees;
    }

    public int getDefenseTendToHelp() {
        return defenseTendToHelp;
    }

    public int getPace(){
        return pace;
    }

    public double getTotalEfficiency(){
        double offense = 0;
        double defense = 0;

        for(int x = 0; x < players.length; x++){
            offense += (players[x].getMinutes()/40.0) * players[x].getOffensiveEfficiency(offenseFavorsThrees, pace);
            defense += (players[x].getMinutes()/40.0) * players[x].getDefensiveEfficiency(defenseTendToHelp, defenseFavorsThrees, pace);
        }

        offense = offense / 3.2;
        defense = defense / 4.5;

        offense = offense / 100 * maxOffensiveEfficiency;
        defense = 100 / defense * minDefensiveEfficiency;

        Log.v("eff", offense + " " + defense);
        return offense - defense;
    }

    public double getOffensiveEfficiency(){
        double offense = 0;

        for(int x = 0; x < players.length; x++){
            offense += (players[x].getMinutes()/40.0) * players[x].getOffensiveEfficiency(offenseFavorsThrees, pace);
        }

        offense = offense / 2.5;
        offense = offense / 100 * maxOffensiveEfficiency;

        return  offense;
    }
}

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
    private ArrayList<Game> games;
    private int gamesPlayed;

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
        games = new ArrayList<Game>();
    }

    public String getFullName(){
        return schoolName + " " + mascot;
    }

    public Player[] getPlayers(){
        return players;
    }

    public ArrayList<Game> getGames(){
        return games;
    }

    public void addGame(Game game){
        games.add(game);
    }

    public Game getNextGame(){
        if(games.get(gamesPlayed) != null){
            return  games.get(gamesPlayed);
        }
        return null;

    }

    public void playGame(){
        gamesPlayed++;
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

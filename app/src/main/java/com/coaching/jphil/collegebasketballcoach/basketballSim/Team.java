package com.coaching.jphil.collegebasketballcoach.basketballSim;

import java.util.ArrayList;

/**
 * Created by jphil on 2/14/2018.
 */

public class Team {

    Player[] players;
    ArrayList<Game> games;

    String schoolName, mascot;

    public Team(String schoolName, String mascot, Player[] players){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.players = players;
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


}

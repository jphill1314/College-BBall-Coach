package com.coaching.jphil.collegebasketballcoach.basketballSim;

/**
 * Created by jphil on 3/26/2018.
 */

public class GameEvent {

    private String event;
    private int type;
    private boolean homeTeam;

    GameEvent(String event, int type, boolean homeTeam){
        this.event = event; // 0 = extra info, -1 = nothing, 1 = points scored, 2 = change of poss, 3 = foul
        this.type = type;
        this.homeTeam = homeTeam;
    }

    public void appendString(String extra){
        event += extra;
    }

    public String getEvent(){
        return event;
    }

    public int getType(){
        return type;
    }

    public boolean isHomeTeam(){
        return homeTeam;
    }
}

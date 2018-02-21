package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.Random;

/**
 * Created by Jake on 2/20/2018.
 */

public class Recruit {

    private String firstName, lastName;
    private int position;
    private int rating;
    private int interest; // 0-100 with higher meaning more interest in a program
    private boolean isCommitted;
    private boolean isRecentlyRecruited;

    public Recruit(String firstName, String lastName, int position, int rating, Team team){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.rating = rating;
        isCommitted = false;
        isRecentlyRecruited = false;

        generateInterest(team);
    }

    public Recruit(String firstName, String lastName, int position, int rating, int interest,
                   boolean isCommitted, boolean isRecentlyRecruited){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.rating = rating;
        this.interest = interest;
        this.isCommitted = isCommitted;
        this.isRecentlyRecruited = isRecentlyRecruited;
    }

    private void generateInterest(Team team){
        interest = 0;
        if(team.getOverallRating() > rating){
            interest += 50 + (team.getOverallRating() - rating);
        }
        else if(team.getOverallRating() + 10 >= rating){
            interest += 25;
        }

        if(team.getNumberOfPlayersAtPosition(position, false) > 1){
            interest -= 10 * team.getNumberOfPlayersAtPosition(position, false);
        }
        else{
            interest += 25;
        }
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public int getPosition(){
        return position;
    }

    public String getPositionAsString(){
        switch (position){
            case 1:
                return "PG";
            case 2:
                return "SG";
            case 3:
                return "SF";
            case 4:
                return "PF";
            case 5:
                return "C";
        }
        Log.v("POS", position + "");
        return "ERROR";
    }

    public int getRating(){
        return rating;
    }

    public int getInterest(){
        return interest;
    }

    public boolean getIsCommitted(){
        return isCommitted;
    }

    public void attemptToRecruit(){
        if(!isRecentlyRecruited && !isCommitted) {
            if (interest >= 75) {
                isCommitted = true;
            } else {
                Random r = new Random();
                interest += r.nextInt(50) - 25;

                if(interest < 0){
                    interest = 0;
                }
            }
            isRecentlyRecruited = true;
        }
    }

    public void setIsRecentlyRecruited(boolean bool){
        isRecentlyRecruited = bool;
    }

    public boolean getIsRecentlyRecruited(){
        return isRecentlyRecruited;
    }

    public Player startNewSeason(){
        if(isCommitted){
            Random r = new Random();
            return new Player(lastName, firstName, position, 0, rating + 5 - r.nextInt(10));
        }
        return null;
    }
}

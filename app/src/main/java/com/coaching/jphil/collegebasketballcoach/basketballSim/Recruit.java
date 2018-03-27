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
    private boolean isRecruited;

    private int id;

    public Recruit(String firstName, String lastName, int position, int rating, Team team, int id){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.rating = rating;
        this.id = id;

        isCommitted = false;
        isRecruited = false;

        generateInterest(team);
    }

    public Recruit(String firstName, String lastName, int position, int rating, int interest,
                   boolean isCommitted, int id){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.rating = rating;
        this.interest = interest;
        this.isCommitted = isCommitted;
        this.id = id;
    }

    private void generateInterest(Team team){
        interest = 0;
        if(team.getOverallRating() > rating){
            interest += 25 + (team.getOverallRating() - rating);
        }
        else if(team.getOverallRating() + 10 >= rating){
            interest += 10;
        }

        if(team.getNumberOfPlayersAtPosition(position, false) > 1){
            interest -= 10 * team.getNumberOfPlayersAtPosition(position, false);
        }
        else{
            interest += 25;
        }

        if(interest < 0){
            interest = 0;
        }
        if(interest > 100){
            interest = 100;
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

    public int getId(){
        return id;
    }

    public boolean getIsCommitted(){
        return isCommitted;
    }

    void toggleIsRecruited(){
        isRecruited = !isRecruited;
    }

    public boolean isRecruited(){
        return isRecruited;
    }

    Player startNewSeason(){
        if(isCommitted){
            Random r = new Random();
            return new Player(lastName, firstName, position, 0, rating + 5 - r.nextInt(10));
        }
        return null;
    }

    void loseInterest(){
        Random r = new Random();
        interest -= r.nextInt(3);

        if(interest < 0){
            interest = 0;
        }
    }

    boolean attemptToRecruit(int recruitingAbility, boolean bigWin, boolean badLoss, int spotsAvailable){
        Random r = new Random();

        if(bigWin){
            interest += r.nextInt(10);
        }
        else if(badLoss){
            interest -= r.nextInt(10);
        }

        if(interest < 0){
            interest = 0;
        }
        else if(interest > 100){
            interest = 100;
        }

        return (spotsAvailable > 0) && commit(recruitingAbility);
    }

    private boolean commit(int recruitingAbility){
        Random r = new Random();
        if((recruitingAbility / 10 + interest >= r.nextInt(100 * 20)) || interest == 100){
            isCommitted = true;
        }
        return isCommitted;
    }
}

package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Player {

    private String lName, fName;
    private int position;
    private int year;
    private int overallRating;
    private int ratingVariability = 10; // when attributes are generated, how much variability +/-
    private int gameVariability = 5;
    private int gameModifier = 0;

    private int minutes;

    // Offensive attributes
    private int closeRangeShot;
    private int midRangeShot;
    private int longRangeShot;
    private int ballHandling;
    private int passing;
    private int screening;
    private int offBallMovement;

    // Defensive attributes
    private int postDefense;
    private int perimeterDefense;
    private int onBallDefense;
    private int offBallDefense;
    private int stealing;
    private int rebounding;

    // Mental attributes
    // add these later, maybe

    // Physical attributes
    private int stamina;

    // Tracked Stats
    private int gamesPlayed;
    private int totalMinutes;


    public Player(String lName, String fName, int position, int year, int overallRating){
        this.lName = lName;
        this.fName = fName;
        this.year = year;
        this.position = position;
        generateAttributes(overallRating);

        gamesPlayed = 0;
        totalMinutes = 0;

        minutes = 20;
    }

    public Player(String lName, String fName, int position, int year, int minutes, int closeShot, int midShot,
                  int longShot, int ballHandle, int screen, int offBallMove, int postDef, int perDef, int onBall,
                  int offBall, int steal, int rebound, int stamina, int gamesPlayed, int totalMinutes){
        this.lName = lName;
        this.fName = fName;
        this.year = year;
        this.position = position;
        this.minutes = minutes;

        closeRangeShot = closeShot;
        midRangeShot = midShot;
        longRangeShot = longShot;
        ballHandling = ballHandle;
        screening = screen;
        offBallMovement = offBallMove;

        postDefense = postDef;
        perimeterDefense = perDef;
        onBallDefense = onBall;
        offBallDefense = offBall;
        stealing = steal;
        rebounding = rebound;

        this.stamina = stamina;

        this.gamesPlayed = gamesPlayed;
        this.totalMinutes = totalMinutes;

        calculateRating();
    }

    public String getlName() {
        return lName;
    }

    public String getfName() {
        return fName;
    }

    public String getFullName(){
        return fName + " " + lName;
    }

    public int getYear(){
        return year;
    }

    public String getYearAsString(){
        switch (year){
            case 0:
                return "FR";
            case 1:
                return "SO";
            case 2:
                return "JR";
            case 3:
                return "SR";
        }
        Log.v("year", year +"");
        return "Error";
    }

    public void newSeason(int maxImprovement, int games, int offenseFocus, int perimeterFocus, int skillFocus){
        year++;
        int improve = maxImprovement * totalMinutes / (games * 30);
        if(improve > maxImprovement){
            improve = maxImprovement;
        }

        improveAttributes(improve, offenseFocus, perimeterFocus, skillFocus);

        gamesPlayed = 0;
        totalMinutes = 0;
    }

    public void playGame(){
        if(minutes > 0){
            gamesPlayed++;
            totalMinutes += minutes;
        }
        gameModifier = 0;
    }

    private void improveAttributes(int maxImprovement, int offenseFocus, int perimeterFocus, int skillFocus){
        Random r = new Random();

        closeRangeShot += r.nextInt((int)(maxImprovement * (offenseFocus + (100-perimeterFocus) + skillFocus) / 100.0));
        midRangeShot += r.nextInt((int)(maxImprovement * (offenseFocus + 50 + skillFocus) / 100.0));
        longRangeShot += r.nextInt((int)(maxImprovement * (offenseFocus + (perimeterFocus) + skillFocus) / 100.0));
        ballHandling += r.nextInt((int)(maxImprovement * (offenseFocus + (perimeterFocus) + skillFocus) / 100.0));
        passing += r.nextInt((int)(maxImprovement * (offenseFocus + 50 + skillFocus) / 100.0));
        screening += r.nextInt((int)(maxImprovement * (offenseFocus + (100-perimeterFocus) + skillFocus) / 100.0));

        postDefense += r.nextInt((int)(maxImprovement * ((100-offenseFocus) + (100-perimeterFocus) + skillFocus) / 100.0));
        perimeterDefense += r.nextInt((int)(maxImprovement * ((100-offenseFocus) + (perimeterFocus) + skillFocus) / 100.0));
        onBallDefense += r.nextInt((int)(maxImprovement * ((100-offenseFocus) + 50 + skillFocus) / 100.0));
        offBallDefense += r.nextInt((int)(maxImprovement * ((100-offenseFocus) + 50 + skillFocus) / 100.0));
        stealing += r.nextInt((int)(maxImprovement * ((100-offenseFocus) + (perimeterFocus) + skillFocus) / 100.0));
        rebounding += r.nextInt((int)(maxImprovement * ((100-offenseFocus) + (100-perimeterFocus) + skillFocus) / 100.0));

        stamina += r.nextInt((int)(maxImprovement * (1 - skillFocus/100.0)));

        calculateRating();
    }

    public int getPosition() {
        return position;
    }

    public String getPositionAbr(){
        switch(position){
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
        return "Error";
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public int getMinutes(){
        return minutes;
    }

    public void setMinutes(int minutes){
        this.minutes = minutes;
    }

    private void generateAttributes(int rating){
        Random r = new Random();

        // Offensive
        closeRangeShot = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        midRangeShot = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        longRangeShot = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        ballHandling = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        passing = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        screening = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        offBallMovement = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;

        // Defensive
        postDefense = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        perimeterDefense = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        onBallDefense = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        offBallDefense = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        stealing = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;
        rebounding = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;

        // Physical
        stamina = rating + (2 * r.nextInt(ratingVariability)) - ratingVariability;

        calculateRating();
    }

    private void calculateRating(){
        // Update this to take player's position into account

        overallRating = (int)((closeRangeShot + midRangeShot + longRangeShot + ballHandling + passing
        + screening + offBallMovement + postDefense + perimeterDefense + onBallDefense + offBallDefense + stealing + rebounding
        + stamina) / 14.0);
    }

    public double getOffensiveEfficiency(int favorsThrees, int pace){
        double efficiency = 0.0;

        efficiency += (1 - favorsThrees / 100.0) * closeRangeShot;
        efficiency += .5 * midRangeShot;
        efficiency += (favorsThrees / 100.0) * longRangeShot;

        efficiency += (50.0 / pace) * (ballHandling + passing + screening); // slower pace favors good ball handlers, passers, and screeners
        efficiency += (pace / 80.0) * stamina; // slower pace helps players with low stamina

        efficiency = efficiency / 5.5; //should give perfect player an efficiency of 100

        return efficiency;
    }

    public double getDefensiveEfficiency(int tendToHelp, int favorsThrees, int pace){
        double efficiency = 0.0;

        efficiency += (1 - tendToHelp / 100.0) * onBallDefense;
        efficiency += (tendToHelp / 100.0) * offBallDefense;

        efficiency += (1 - favorsThrees / 100.0) * postDefense;
        efficiency += (favorsThrees / 100.0) * perimeterDefense;

        efficiency += stealing + rebounding;

        efficiency += (pace / 80.0) * stamina;

        efficiency = efficiency / 5.0;

        return efficiency;
    }

    public int getCloseRangeShot() {
        return closeRangeShot + gameModifier;
    }

    public int getMidRangeShot() {
        return midRangeShot + gameModifier;
    }

    public int getLongRangeShot() {
        return longRangeShot + gameModifier;
    }

    public int getBallHandling() {
        return ballHandling + gameModifier;
    }

    public int getPassing() {
        return passing + gameModifier;
    }

    public int getScreening() {
        return screening + gameModifier;
    }

    public int getOffBallMovement(){
        return offBallMovement + gameModifier;
    }

    public int getPostDefense() {
        return postDefense + gameModifier;
    }

    public int getPerimeterDefense() {
        return perimeterDefense + gameModifier;
    }

    public int getOnBallDefense() {
        return onBallDefense + gameModifier;
    }

    public int getOffBallDefense() {
        return offBallDefense + gameModifier;
    }

    public int getStealing() {
        return stealing + gameModifier;
    }

    public int getRebounding() {
        return rebounding + gameModifier;
    }

    public int getStamina() {
        return stamina;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public void setGameModifiers(boolean homeTeam, int advantage){
        gameModifier = (int) (Math.random() * 2 * gameVariability) - gameVariability;
        if(homeTeam){
            // players play better at home
            gameModifier += 3;
        }
        if(advantage > 0){
            if(advantage > 30){
                gameModifier -= (int) (30 * 0.7);
            }
            else{
                gameModifier -= (int) (advantage * (1 - advantage/100.0));
            }

        }

    }

}

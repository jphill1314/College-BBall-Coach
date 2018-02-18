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

    private int minutes;

    // Offensive attributes
    private int closeRangeShot;
    private int midRangeShot;
    private int longRangeShot;
    private int ballHandling;
    private int passing;
    private int screening;

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

    public Player(String lName, String fName, int position, int year, int overallRating){
        this.lName = lName;
        this.fName = fName;
        this.year = year;
        this.position = position;
        generateAttributes(overallRating);

        minutes = 20;
    }

    public Player(String lName, String fName, int position, int year, int minutes, int closeShot, int midShot,
                  int longShot, int ballHandle, int screen, int postDef, int perDef, int onBall,
                  int offBall, int steal, int rebound, int stamina){
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

        postDefense = postDef;
        perimeterDefense = perDef;
        onBallDefense = onBall;
        offBallDefense = offBall;
        stealing = steal;
        rebounding = rebound;

        this.stamina = stamina;

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
        return "Error";
    }

    public void newSeason(int maxImprovement){
        year++;
        improveAttributes(maxImprovement);
    }

    private void improveAttributes(int maxImprovement){
        Random r = new Random();

        closeRangeShot += r.nextInt(maxImprovement);
        midRangeShot += r.nextInt(maxImprovement);
        longRangeShot += r.nextInt(maxImprovement);
        ballHandling += r.nextInt(maxImprovement);
        passing += r.nextInt(maxImprovement);
        screening += r.nextInt(maxImprovement);

        postDefense += r.nextInt(maxImprovement);
        perimeterDefense += r.nextInt(maxImprovement);
        onBallDefense += r.nextInt(maxImprovement);
        offBallDefense += r.nextInt(maxImprovement);
        stealing += r.nextInt(maxImprovement);
        rebounding += r.nextInt(maxImprovement);
        stamina += r.nextInt(maxImprovement);

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
        + screening + postDefense + perimeterDefense + onBallDefense + offBallDefense + stealing + rebounding
        + stamina) / 13.0);
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
        return closeRangeShot;
    }

    public int getMidRangeShot() {
        return midRangeShot;
    }

    public int getLongRangeShot() {
        return longRangeShot;
    }

    public int getBallHandling() {
        return ballHandling;
    }

    public int getPassing() {
        return passing;
    }

    public int getScreening() {
        return screening;
    }

    public int getPostDefense() {
        return postDefense;
    }

    public int getPerimeterDefense() {
        return perimeterDefense;
    }

    public int getOnBallDefense() {
        return onBallDefense;
    }

    public int getOffBallDefense() {
        return offBallDefense;
    }

    public int getStealing() {
        return stealing;
    }

    public int getRebounding() {
        return rebounding;
    }

    public int getStamina() {
        return stamina;
    }
}

package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Player {

    private String lName, fName;
    private int position;
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

    public Player(String lName, String fName, int position, int overallRating){
        this.lName = lName;
        this.fName = fName;
        this.position = position;
        generateAttributes(overallRating);

        minutes = 20;
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

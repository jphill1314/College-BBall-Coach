package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Player {

    private String lName, fName;
    private int position;
    private int currentPosition;
    private int year;
    private int overallRating;
    private int ratingVariability = 10; // when attributes are generated, how much variability +/-
    private int gameVariability = 10;
    private int offensiveModifier = 0;
    private int defensiveModifier = 0;

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

    private int fouls;
    private int twoPointShotAttempts;
    private int twoPointShotMade;
    private int threePointShotAttempts;
    private int threePointShotMade;
    private int freeThrowAttempts;
    private int freeThrowMade;
    private int assists;
    private int oRebounds;
    private int dRebounds;
    private int steals;
    private int turnovers;

    private double fatigue;
    private int timePlayed; // time in seconds

    private boolean prepareForSave = false;


    public Player(String lName, String fName, int position, int year, int overallRating){
        this.lName = lName;
        this.fName = fName;
        this.year = year;
        this.position = position;
        currentPosition = position;
        generateAttributes(overallRating);

        gamesPlayed = 0;
        totalMinutes = 0;

        minutes = 20;

        prepareForSave = false;
    }

    public Player(String lName, String fName, int position, int year, int minutes, int closeShot, int midShot,
                  int longShot, int ballHandle, int pass, int screen, int offBallMove, int postDef, int perDef, int onBall,
                  int offBall, int steal, int rebound, int stamina, int gamesPlayed, int totalMinutes){
        this.lName = lName;
        this.fName = fName;
        this.year = year;
        this.position = position;
        currentPosition = position;
        this.minutes = minutes;

        closeRangeShot = closeShot;
        midRangeShot = midShot;
        longRangeShot = longShot;
        ballHandling = ballHandle;
        passing = pass;
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

        prepareForSaving();
        prepareForSave = false;

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

    public void prepareForSaving(){
        fatigue = 0;
        timePlayed = 0;
        offensiveModifier = 0;
        defensiveModifier = 0;

        prepareForSave = true;
    }

    void newSeason(int maxImprovement, int games, int offenseFocus, int perimeterFocus, int skillFocus){
        year++;
        int improve = (int)Math.ceil(maxImprovement * totalMinutes / (games * 30));
        if(improve > maxImprovement){
            improve = maxImprovement;
        }
        if(improve < 1){
            improve = 1;
        }

        improveAttributes(improve, offenseFocus, perimeterFocus, skillFocus);

        gamesPlayed = 0;
        totalMinutes = 0;
    }

    void playGame(){
        if(timePlayed > 0){
            gamesPlayed++;
            totalMinutes += timePlayed / 60;
            if(timePlayed % 60 > 30){
                totalMinutes++;
            }
        }
        offensiveModifier = 0;
        defensiveModifier = 0;
        fatigue = 0;
    }

    void preGameSetup(){
        fouls = 0;
        twoPointShotAttempts = 0;
        twoPointShotMade = 0;
        threePointShotAttempts = 0;
        threePointShotMade = 0;
        freeThrowAttempts = 0;
        freeThrowMade = 0;
        assists = 0;
        oRebounds = 0;
        dRebounds = 0;
        steals = 0;
        turnovers = 0;
        timePlayed = 0;
        fatigue = 0;

        currentPosition = position;

        prepareForSave = false;
    }

    void addFoul(){
        fouls++;
    }

    void addTwoPointShot(boolean made){
        twoPointShotAttempts++;
        if(made){
            twoPointShotMade++;
        }
    }

    void addThreePointShot(boolean made){
        threePointShotAttempts++;
        if(made){
            threePointShotMade++;
        }
    }

    void addFreeThrowShot(boolean made){
        freeThrowAttempts++;
        if(made){
            freeThrowMade++;
        }
    }

    void addAssist(){
        assists++;
    }

    void addRebound(boolean offensive){
        if(offensive){
            oRebounds++;
        }
        else{
            dRebounds++;
        }
    }

    void addTurnover(){
        turnovers++;
    }

    void addSteal(){
        steals++;
    }

    public void setCurrentPosition(int pos){
        if(pos < 5) {
            currentPosition = pos;
        }
        else{
            currentPosition = position;
        }
    }

    public int getCurrentPosition(){
        return currentPosition;
    }

    public int getFouls() {
        return fouls;
    }

    public int getTwoPointShotAttempts() {
        return twoPointShotAttempts;
    }

    public int getTwoPointShotMade() {
        return twoPointShotMade;
    }

    public int getThreePointShotAttempts() {
        return threePointShotAttempts;
    }

    public int getThreePointShotMade() {
        return threePointShotMade;
    }

    public int getFreeThrowAttempts() {
        return freeThrowAttempts;
    }

    public int getFreeThrowMade() {
        return freeThrowMade;
    }

    public int getAssists() {
        return assists;
    }

    public int getoRebounds() {
        return oRebounds;
    }

    public int getdRebounds() {
        return dRebounds;
    }

    public int getSteals(){
        return steals;
    }

    public int getTurnovers(){
        return turnovers;
    }

    void addTimePlayed(int time, int event){
        // event: 0 = nothing, 1 = change of possession, -1 = timeout / on bench, 10 = halftime
        timePlayed += time;
        if(time > 0){
            if(event == 1){
                fatigue += 3 * (1.5 - (stamina / 100.0));
                if(fatigue > 100){
                    fatigue = 100.0;
                }
            }
        }

        if(event == 10){
            fatigue *= .5;
        }
        else if(event < 0){
            if(fatigue > 0) {
                fatigue -= 1;
            }
        }
    }

    private void improveAttributes(int maxImprovement, int offenseFocus, int perimeterFocus, int skillFocus){
        Random r = new Random();

        closeRangeShot += r.nextInt((int)Math.ceil(maxImprovement * (offenseFocus + (100-perimeterFocus) + skillFocus) / 100.0));
        midRangeShot += r.nextInt((int)Math.ceil(maxImprovement * (offenseFocus + 50 + skillFocus) / 100.0));
        longRangeShot += r.nextInt((int)Math.ceil(maxImprovement * (offenseFocus + (perimeterFocus) + skillFocus) / 100.0));
        ballHandling += r.nextInt((int)Math.ceil(maxImprovement * (offenseFocus + (perimeterFocus) + skillFocus) / 100.0));
        passing += r.nextInt((int)Math.ceil(maxImprovement * (offenseFocus + 50 + skillFocus) / 100.0));
        screening += r.nextInt((int)Math.ceil(maxImprovement * (offenseFocus + (100-perimeterFocus) + skillFocus) / 100.0));

        postDefense += r.nextInt((int)Math.ceil(maxImprovement * ((100-offenseFocus) + (100-perimeterFocus) + skillFocus) / 100.0));
        perimeterDefense += r.nextInt((int)Math.ceil(maxImprovement * ((100-offenseFocus) + (perimeterFocus) + skillFocus) / 100.0));
        onBallDefense += r.nextInt((int)Math.ceil(maxImprovement * ((100-offenseFocus) + 50 + skillFocus) / 100.0));
        offBallDefense += r.nextInt((int)Math.ceil(maxImprovement * ((100-offenseFocus) + 50 + skillFocus) / 100.0));
        stealing += r.nextInt((int)Math.ceil(maxImprovement * ((100-offenseFocus) + (perimeterFocus) + skillFocus) / 100.0));
        rebounding += r.nextInt((int)Math.ceil(maxImprovement * ((100-offenseFocus) + (100-perimeterFocus) + skillFocus) / 100.0));

        stamina += r.nextInt((int)Math.ceil(maxImprovement * (1 - skillFocus/100.0)));

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
        if(gamesPlayed == 0){
            return 0;
        }
        else {
            return totalMinutes / gamesPlayed;
        }
    }

    private void generateAttributes(int rating){
        Random r = new Random();
        rating += 10;

        double[] closeWeight = new double[] {.6, .7, .8, 1.0, 1.0};
        double[] midWeight = new double[] {.8, .8, .8, .7, .7};
        double[] longWeight = new double[] {.8, 1.0, .8, .5, .5};
        double[] ballWeight = new double[] {1.1, .8, .8, .6, .5};
        double[] passWeight = new double[] {1.1, .8, .8, .6, .5};
        double[] screenWeight = new double[] {.4, .5, .6, .9, 1.0};
        double[] offMoveWeight = new double[] {.8, 1.0, .8, .7, .7};

        double[] postDefWeight = new double[] {.4, .5, .6, .9, 1.0};
        double[] perimDefWeight = new double[] {1.0, 1.0, .9, .5, .5};
        double[] onBallWeight = new double[] {1.0, .8, .8, .9, 1.0};
        double[] offBallWeight = new double[] {.8, .8, .8, .7, .6};
        double[] stealWeight = new double[] {.8, .8, .8, .5, .5};
        double[] reboundWeight = new double[] {.4, .5, .7, 1.2, 1.2};

        // Offensive
        closeRangeShot = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * closeWeight[position-1]);
        midRangeShot = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * midWeight[position-1]);
        longRangeShot = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * longWeight[position-1]);
        ballHandling = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * ballWeight[position-1]);
        passing = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * passWeight[position-1]);
        screening = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * screenWeight[position-1]);
        offBallMovement = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * offMoveWeight[position-1]);

        // Defensive
        postDefense = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * postDefWeight[position-1]);
        perimeterDefense = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * perimDefWeight[position-1]);
        onBallDefense = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * onBallWeight[position-1]);
        offBallDefense = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * offBallWeight[position-1]);
        stealing = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * stealWeight[position-1]);
        rebounding = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * reboundWeight[position-1]);

        // Physical
        stamina = r.nextInt(40) + 40;

        calculateRating();
    }

    private void calculateRating(){
        double[] closeWeight = new double[] {.6, .7, .8, 1.0, 1.0};
        double[] midWeight = new double[] {.8, .8, .8, .7, .7};
        double[] longWeight = new double[] {.8, 1.0, .8, .5, .5};
        double[] ballWeight = new double[] {1.1, .8, .8, .6, .5};
        double[] passWeight = new double[] {1.1, .8, .8, .6, .5};
        double[] screenWeight = new double[] {.4, .5, .6, .9, 1.0};
        double[] offMoveWeight = new double[] {.8, 1.0, .8, .7, .7};

        double[] postDefWeight = new double[] {.4, .5, .6, .9, 1.0};
        double[] perimDefWeight = new double[] {1.0, 1.0, .9, .5, .5};
        double[] onBallWeight = new double[] {1.0, .8, .8, .9, 1.0};
        double[] offBallWeight = new double[] {.8, .8, .8, .7, .6};
        double[] stealWeight = new double[] {.8, .8, .8, .5, .5};
        double[] reboundWeight = new double[] {.4, .5, .7, 1.2, 1.2};

        overallRating = (int) (closeRangeShot * closeWeight[currentPosition-1] +
                midRangeShot * midWeight[currentPosition-1] + longRangeShot * longWeight[currentPosition-1] +
                ballHandling * ballWeight[currentPosition-1] + passing * passWeight[currentPosition-1] +
                screening * screenWeight[currentPosition-1] + offBallMovement * offMoveWeight[currentPosition-1] +
                postDefense * postDefWeight[currentPosition-1] + perimeterDefense * perimDefWeight[currentPosition-1] +
                onBallDefense * onBallWeight[currentPosition-1] + offBallDefense * offBallWeight[currentPosition-1] +
                stealing * stealWeight[currentPosition-1] + rebounding * reboundWeight[currentPosition-1]);

        if(currentPosition < 4){
            overallRating /= 10;
        }
        else{
            overallRating = (int) (overallRating / 9.7);
        }
    }

    public int calculateRatingAtPosition(int position){
        double[] closeWeight = new double[] {.6, .7, .8, 1.0, 1.0};
        double[] midWeight = new double[] {.8, .8, .8, .7, .7};
        double[] longWeight = new double[] {.8, 1.0, .8, .5, .5};
        double[] ballWeight = new double[] {1.1, .8, .8, .6, .5};
        double[] passWeight = new double[] {1.1, .8, .8, .6, .5};
        double[] screenWeight = new double[] {.4, .5, .6, .9, 1.0};
        double[] offMoveWeight = new double[] {.8, 1.0, .8, .7, .7};

        double[] postDefWeight = new double[] {.4, .5, .6, .9, 1.0};
        double[] perimDefWeight = new double[] {1.0, 1.0, .9, .5, .5};
        double[] onBallWeight = new double[] {1.0, .8, .8, .9, 1.0};
        double[] offBallWeight = new double[] {.8, .8, .8, .7, .6};
        double[] stealWeight = new double[] {.8, .8, .8, .5, .5};
        double[] reboundWeight = new double[] {.4, .5, .7, 1.2, 1.2};

        int rating = (int) (closeRangeShot * closeWeight[position-1] +
                midRangeShot * midWeight[position-1] + longRangeShot * longWeight[position-1] +
                ballHandling * ballWeight[position-1] + passing * passWeight[position-1] +
                screening * screenWeight[position-1] + offBallMovement * offMoveWeight[position-1] +
                postDefense * postDefWeight[position-1] + perimeterDefense * perimDefWeight[position-1] +
                onBallDefense * onBallWeight[position-1] + offBallDefense * offBallWeight[position-1] +
                stealing * stealWeight[position-1] + rebounding * reboundWeight[position-1]);

        if(position < 4){
            return rating / 10;
        }
        else{
            return  (int) (rating/ 9.7);
        }
    }

    public int getCloseRangeShot() {
        return (int)((closeRangeShot + offensiveModifier) * getFatigueFactor());
    }

    public int getMidRangeShot() {
        return (int) ((midRangeShot + offensiveModifier) * getFatigueFactor());
    }

    public int getLongRangeShot() {
        return (int) ((longRangeShot + offensiveModifier) * getFatigueFactor());
    }

    public int getFreeThrowShot(){
        return (int)(((getCloseRangeShot() + getMidRangeShot() + getLongRangeShot()) / 3.0) * getFatigueFactor());
    }

    public int getBallHandling() {
        return (int) ((ballHandling + offensiveModifier) * getFatigueFactor());
    }

    public int getPassing() {
        return (int) ((passing + offensiveModifier) * getFatigueFactor());
    }

    public int getScreening() {
        return (int) ((screening + offensiveModifier) * getFatigueFactor());
    }

    public int getOffBallMovement(){
        return (int) ((offBallMovement + offensiveModifier) * getFatigueFactor());
    }

    public int getPostDefense() {
        return (int) ((postDefense + defensiveModifier) * getFatigueFactor());
    }

    public int getPerimeterDefense() {
        return (int) ((perimeterDefense + defensiveModifier) * getFatigueFactor());
    }

    public int getOnBallDefense() {
        return (int) ((onBallDefense + defensiveModifier) * getFatigueFactor());
    }

    public int getOffBallDefense() {
        return (int) ((offBallDefense + defensiveModifier) * getFatigueFactor());
    }

    public int getStealing() {
        return (int) ((stealing + defensiveModifier) * getFatigueFactor());
    }

    public int getRebounding() {
        return (int) ((rebounding + defensiveModifier) * getFatigueFactor());
    }

    public int getStamina() {
        return stamina;
    }

    public int getFatigue(){
        if(fatigue < 1){
            return 1;
        }
        return (int) fatigue;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getTotalMinutes() {
        return totalMinutes;
    }

    public int getMinutesPlayed(){
        return timePlayed / 60;
    }

    private double getFatigueFactor(){
        if(prepareForSave){
            return 1;
        }
        return (1 - ((Math.exp((fatigue) / 25)) / 100));
    }

    public void setGameModifiers(boolean homeTeam, int scoreDif, int coachType){
        // for coach type: 0=no effect, 1=less variability, 2=extra variability (good or bad), 3=defensive focus, 4=offensive focus
        int maxModifier = 25;
        double gameModifier = (Math.random() * 2 * gameVariability) - gameVariability;
        if(coachType == 1){
            gameModifier /= 2;
        }
        else if(coachType == 2){
            gameModifier *= 2;
        }

        if(homeTeam){
            // players play better at home
            gameModifier += 3;
        }
        if(scoreDif > 0){
            if(scoreDif > 30){
                gameModifier -= maxModifier;
            }
            else{
                gameModifier -= scoreDif * (1 - scoreDif/100.0);
            }

        }

        if(coachType == 3){
            offensiveModifier += gameModifier;
            defensiveModifier += gameModifier + 3;
        }
        else if(coachType == 4){
            offensiveModifier += gameModifier + 3;
            defensiveModifier += gameModifier;
        }
        else{
            offensiveModifier += gameModifier;
            defensiveModifier += gameModifier;
        }

        if(offensiveModifier > maxModifier){
            offensiveModifier = maxModifier;
        }
        else if(offensiveModifier < -maxModifier){
            offensiveModifier = -maxModifier;
        }

        if(defensiveModifier > maxModifier){
            defensiveModifier = maxModifier;
        }
        else if(defensiveModifier < -maxModifier){
            defensiveModifier = -maxModifier;
        }
    }

    public boolean isEligible(){
        return fouls < 5;
    }

    public boolean isInFoulTrouble(int half, int timeRemaining){
        int min = timeRemaining / 60;
        if(half == 1 && fouls == 2){
            return true;
        }
        if(half == 2){
            if(fouls == 3 && min > 14){
                return true;
            }
            if(fouls == 4 && min > 6){
                return true;
            }
        }
        return false;
    }

    public String getTwoPointStats(){
        return twoPointShotMade + "/" + twoPointShotAttempts;
    }

    public String getThreePointStats(){
        return threePointShotMade + "/" + threePointShotAttempts;
    }

    public String getFreeThrowStats(){
        return freeThrowMade + "/" + freeThrowAttempts;
    }

}

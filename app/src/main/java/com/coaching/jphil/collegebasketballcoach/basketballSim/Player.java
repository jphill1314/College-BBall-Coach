package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.ArrayList;
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
    private int trainingAs;

    private int ratingVariability = 10; // when attributes are generated, how much variability +/-
    private int gameVariability = 10;
    private int offensiveModifier = 0;
    private int defensiveModifier = 0;

    // Offensive attributes
    private int closeRangeShot;
    private int midRangeShot;
    private int longRangeShot;
    private int freeThrowShot;
    private int postMove;
    private int ballHandling;
    private int passing;
    private int screening;
    private int offBallMovement;

    private int closeRangeShotProgress;
    private int midRangeShotProgress;
    private int longRangeShotProgress;
    private int freeThrowShotProgress;
    private int postMoveProgress;
    private int ballHandlingProgress;
    private int passingProgress;
    private int screeningProgress;
    private int offBallMovementProgress;

    // Defensive attributes
    private int postDefense;
    private int perimeterDefense;
    private int onBallDefense;
    private int offBallDefense;
    private int stealing;
    private int rebounding;

    private int postDefenseProgress;
    private int perimeterDefenseProgress;
    private int onBallDefenseProgress;
    private int offBallDefenseProgress;
    private int stealingProgress;
    private int reboundingProgress;

    // Other Attributes
    private int stamina;
    private int aggressiveness;
    private int workEthic;

    private int staminaProgress;

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

    private int playerId;

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

        prepareForSave = false;
    }

    public Player(String lName, String fName, int id, int position, int year, int train, int closeShot, int midShot,
                  int longShot, int freeThrow, int postOff, int ballHandle, int pass, int screen, int offBallMove, int postDef, int perDef, int onBall,
                  int offBall, int steal, int rebound, int stamina, int aggressive, int work, int gamesPlayed, int totalMinutes){
        this.lName = lName;
        this.fName = fName;
        this.playerId = id;
        this.year = year;
        this.position = position;
        trainingAs = train;
        currentPosition = position;

        closeRangeShot = closeShot;
        midRangeShot = midShot;
        longRangeShot = longShot;
        freeThrowShot = freeThrow;
        postMove = postOff;
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
        aggressiveness = aggressive;
        workEthic = work;

        this.gamesPlayed = gamesPlayed;
        this.totalMinutes = totalMinutes;

        prepareForSaving();
        prepareForSave = false;

        calculateRating();
    }

    public void setProgress(int closeShot, int midShot, int longShot, int freeThrow, int postOff,
                            int ballHandle, int pass, int screen, int offBallMove, int postDef,
                            int perDef, int onBall, int offBall, int steal, int rebound, int stamina){
        closeRangeShotProgress = closeShot;
        midRangeShotProgress = midShot;
        longRangeShotProgress = longShot;
        freeThrowShotProgress = freeThrow;
        postMoveProgress = postOff;
        ballHandlingProgress = ballHandle;
        passingProgress = pass;
        screeningProgress = screen;
        offBallMovementProgress = offBallMove;

        postDefenseProgress = postDef;
        perimeterDefenseProgress = perDef;
        onBallDefenseProgress = onBall;
        offBallDefenseProgress = offBall;
        stealingProgress = steal;
        reboundingProgress = rebound;

        staminaProgress = stamina;
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

    public int getId(){
        return playerId;
    }

    public void setPlayerId(int id){
        playerId = id;
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

    void newSeason(ArrayList<Coach> coaches){
        year++;

        practice(coaches, 5);

        gamesPlayed = 0;
        totalMinutes = 0;
    }

    void playGame(ArrayList<Coach> coaches){
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

        practice(coaches, 1);
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
        if(pos <= 5 && pos > 0) {
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
                fatigue -= .1;
            }
        }
    }

    private void practice(ArrayList<Coach> coaches, int time){
        Random r = new Random();
        /*
        Values of Training as:
        0 = guard
        1 = big
        2 = pg
        3 = sg
        4 = sf
        5 = pf
        6 = c
        7 = 3-point specialist
        8 = rebound specialist
        9 = defensive specialist
        10 = energy guy
         */

        int shooting = 0;
        int screenTeach = 0;
        int offPos = 0;
        int defPos = 0;
        int ballHandle = 0;
        int onBall = 0;
        int offBall = 0;
        int rebound = 0;
        int steal = 0;
        int condition = 0;

        for(Coach c: coaches){
            shooting += c.getShotTeaching();
            ballHandle += c.getBallControlTeaching();
            offPos += c.getOffPositionTeaching();
            screenTeach += c.getScreenTeaching();
            defPos += c.getDefPositionTeaching();
            onBall += c.getDefOnBallTeaching();
            offBall += c.getDefOffBallTeaching();
            rebound += c.getReboundTeaching();
            steal += c.getStealTeaching();
            condition += c.getConditioningTeaching();
        }

        shooting = (int)(shooting * (workEthic / 100.0));
        ballHandle = (int) (ballHandle * (workEthic / 100.0));
        offPos = (int) (ballHandle * (workEthic / 100.0));
        screenTeach = (int) (screenTeach * (workEthic / 100.0));
        defPos = (int) (defPos * (workEthic / 100.0));
        onBall = (int) (onBall * (workEthic / 100.0));
        offBall = (int) (offBall * (workEthic / 100.0));
        rebound = (int) (rebound * (workEthic / 100.0));
        steal = (int) (steal  * (workEthic / 100.0));
        condition = (int) (condition  * (workEthic / 100.0));

        if(shooting < 1){
            shooting = 1;
        }
        if(ballHandle < 1){
            ballHandle = 1;
        }
        if(offPos < 1){
            offPos = 1;
        }
        if(screenTeach < 1){
            screenTeach = 1;
        }
        if(defPos < 1){
            defPos = 1;
        }
        if(onBall < 1){
            onBall = 1;
        }
        if(offBall < 1){
            offBall = 1;
        }
        if(rebound < 1){
            rebound = 1;
        }
        if(steal < 1){
            steal = 1;
        }
        if(condition < 1){
            condition = 1;
        }

        switch (trainingAs){
            case 0:
                // guard
                midRangeShotProgress += r.nextInt(shooting) + 2;
                longRangeShotProgress += r.nextInt(shooting) + 2;
                freeThrowShotProgress += r.nextInt(shooting) + 2;
                ballHandlingProgress += r.nextInt(ballHandle) + 2;
                passingProgress += r.nextInt(ballHandle) + 2;
                offBallMovementProgress += r.nextInt(offPos) + 2;

                perimeterDefenseProgress += r.nextInt(defPos) + 2;
                onBallDefenseProgress += r.nextInt(onBall) + 2;
                offBallDefenseProgress += r.nextInt(offBall) + 2;
                stealingProgress += r.nextInt(steal) + 2;
                break;
            case 1:
                // big
                closeRangeShotProgress += r.nextInt(shooting) + 2;
                midRangeShotProgress += r.nextInt(shooting) + 2;
                postMoveProgress += r.nextInt(shooting) + 2;
                screeningProgress += r.nextInt(screenTeach) + 2;
                freeThrowShotProgress += r.nextInt(shooting) + 2;

                postDefenseProgress += r.nextInt(defPos) + 2;
                onBallDefenseProgress += r.nextInt(onBall) + 2;
                offBallDefenseProgress += r.nextInt(offBall) + 2;
                reboundingProgress += r.nextInt(rebound) + 4;
                break;
            case 2:
                // pg
                midRangeShotProgress += r.nextInt(shooting) + 1;
                longRangeShotProgress += r.nextInt(shooting) + 2;
                freeThrowShotProgress += r.nextInt(shooting) + 1;
                ballHandlingProgress += r.nextInt(ballHandle) + 4;
                passingProgress += r.nextInt(ballHandle) + 4;
                offBallMovementProgress += r.nextInt(offPos) + 2;

                perimeterDefenseProgress += r.nextInt(defPos) + 2;
                onBallDefenseProgress += r.nextInt(onBall) + 2;
                offBallDefenseProgress += r.nextInt(offBall) + 1;
                stealingProgress += r.nextInt(steal) + 1;
                break;
            case 3:
                // sg
                midRangeShotProgress += r.nextInt(shooting) + 3;
                longRangeShotProgress += r.nextInt(shooting) + 4;
                freeThrowShotProgress += r.nextInt(shooting) + 4;
                ballHandlingProgress += r.nextInt(ballHandle) + 1;
                passingProgress += r.nextInt(ballHandle) + 1;
                offBallMovementProgress += r.nextInt(offPos) + 4;

                perimeterDefenseProgress += r.nextInt(defPos) + 2;
                onBallDefenseProgress += r.nextInt(onBall) + 1;
                offBallDefenseProgress += r.nextInt(offBall) + 1;
                stealingProgress += r.nextInt(steal) + 1;
                break;
            case 4:
                // sf
                closeRangeShotProgress += r.nextInt(shooting) + 1;
                midRangeShotProgress += r.nextInt(shooting) + 1;
                longRangeShotProgress += r.nextInt(shooting) + 1;
                freeThrowShotProgress += r.nextInt(shooting) + 1;
                ballHandlingProgress += r.nextInt(ballHandle) + 1;
                passingProgress += r.nextInt(ballHandle) + 1;
                offBallMovementProgress += r.nextInt(offPos) + 1;

                perimeterDefenseProgress += r.nextInt(defPos) + 1;
                onBallDefenseProgress += r.nextInt(onBall) + 1;
                offBallDefenseProgress += r.nextInt(offBall) + 1;
                stealingProgress += r.nextInt(steal) + 1;
                reboundingProgress += r.nextInt(rebound) + 1;
                break;
            case 5:
                // pf
                closeRangeShotProgress += r.nextInt(shooting) + 2;
                midRangeShotProgress += r.nextInt(shooting) + 2;
                postMoveProgress += r.nextInt(shooting) + 2;
                screeningProgress += r.nextInt(screenTeach) + 1;
                freeThrowShotProgress += r.nextInt(shooting) + 2;

                postDefenseProgress += r.nextInt(defPos) + 2;
                onBallDefenseProgress += r.nextInt(onBall) + 2;
                offBallDefenseProgress += r.nextInt(offBall) + 2;
                reboundingProgress += r.nextInt(rebound) + 4;
                break;
            case 6:
                // c
                closeRangeShotProgress += r.nextInt(shooting) + 2;
                midRangeShotProgress += r.nextInt(shooting) + 1;
                postMoveProgress += r.nextInt(shooting) + 2;
                screeningProgress += r.nextInt(screenTeach) + 2;
                freeThrowShotProgress += r.nextInt(shooting) + 2;

                postDefenseProgress += r.nextInt(defPos) + 2;
                onBallDefenseProgress += r.nextInt(onBall) + 2;
                offBallDefenseProgress += r.nextInt(offBall) + 1;
                reboundingProgress += r.nextInt(rebound) + 4;
                break;
            case 7:
                // 3-point specialist
                longRangeShotProgress += r.nextInt(2 * shooting) + 3;
                freeThrowShotProgress += r.nextInt(2 * shooting) + 3;
                offBallMovementProgress += r.nextInt(2 * offPos) + 3;
                break;
            case 8:
                // Rebound specialist
                reboundingProgress += r.nextInt(2 * rebound) + 3;
                postDefenseProgress += r.nextInt(2 * defPos) + 3;
                break;
            case 9:
                // Defensive specialist
                postDefenseProgress += r.nextInt(2 * defPos);
                perimeterDefenseProgress += r.nextInt(2 * defPos);
                onBallDefenseProgress += r.nextInt(2 * onBall);
                offBallDefenseProgress += r.nextInt(2 * offBall);
                break;
            case 10:
                // Energy guy
                staminaProgress += r.nextInt(2 * condition);
                stealingProgress += r.nextInt(2 * steal);
                reboundingProgress += r.nextInt(2 * rebound);
                onBallDefenseProgress += r.nextInt(2 * onBall);
                break;
        }

        if(time == 1 && totalMinutes > 10 * gamesPlayed) {
            closeRangeShotProgress += r.nextInt(2 * time);
            midRangeShotProgress += r.nextInt(2 * time);
            longRangeShotProgress += r.nextInt(2 * time);
            freeThrowShotProgress += r.nextInt(2 * time);
            postMoveProgress = r.nextInt(2 * time);
            ballHandlingProgress += r.nextInt(2 * time);
            passingProgress += r.nextInt(2 * time);
            screeningProgress += r.nextInt(2 * time);
            offBallMovementProgress += r.nextInt(2 * time);

            postDefenseProgress += r.nextInt(2 * time);
            perimeterDefenseProgress += r.nextInt(2 * time);
            onBallDefenseProgress += r.nextInt(2 * time);
            offBallDefenseProgress += r.nextInt(2 * time);
            stealingProgress += r.nextInt(2 * time);
            reboundingProgress += r.nextInt(2 * time);

            staminaProgress += r.nextInt(2 * time);
        }
        improveAttributes(time);
    }

    private void improveAttributes(int time){
        // time == 1 after each game
        // time == 5 after each season

        int modifier = 500;

        closeRangeShot += closeRangeShotProgress / modifier;
        if(closeRangeShot > 100){
            closeRangeShot = 100;
        }
        closeRangeShotProgress = closeRangeShotProgress % modifier;

        midRangeShot += midRangeShotProgress / modifier;
        if(midRangeShot > 100){
            midRangeShot = 100;
        }
        midRangeShotProgress = midRangeShotProgress % modifier;

        longRangeShot += longRangeShotProgress / modifier;
        if(longRangeShot > 100){
            longRangeShot = 100;
        }
        longRangeShotProgress = longRangeShotProgress % modifier;

        freeThrowShot += freeThrowShotProgress / modifier;
        if(freeThrowShot > 100){
            freeThrowShot = 100;
        }
        freeThrowShotProgress = freeThrowShotProgress % modifier;

        postMove += postMoveProgress / modifier;
        if(postMove > 100){
            postMove = 100;
        }
        postMoveProgress = postMoveProgress % modifier;

        ballHandling += ballHandlingProgress / modifier;
        if(ballHandling > 100){
            ballHandling = 100;
        }
        ballHandlingProgress = ballHandlingProgress % modifier;

        passing += passingProgress / modifier;
        if(passing > 100){
            passing = 100;
        }
        passingProgress = passingProgress % modifier;

        screening += screeningProgress / modifier;
        if(screening > 100){
            screening = 100;
        }
        screeningProgress = screeningProgress % modifier;

        postDefense += postDefenseProgress / modifier;
        if(postDefense > 100){
            postDefense = 100;
        }
        postDefenseProgress = postDefenseProgress % modifier;

        perimeterDefense += perimeterDefenseProgress / modifier;
        if(perimeterDefense > 100){
            perimeterDefense = 100;
        }
        perimeterDefenseProgress = perimeterDefenseProgress % modifier;

        onBallDefense += onBallDefenseProgress / modifier;
        if(onBallDefense > 100){
            onBallDefense = 100;
        }
        onBallDefenseProgress = onBallDefenseProgress % modifier;

        offBallDefense += offBallDefenseProgress / modifier;
        if(offBallDefense > 100){
            offBallDefense = 100;
        }
        offBallDefenseProgress = offBallDefenseProgress % modifier;

        stealing += stealingProgress / modifier;
        if(stealing > 100){
            stealing = 100;
        }
        stealingProgress = stealingProgress % modifier;

        rebounding += reboundingProgress / modifier;
        if(rebounding > 100){
            rebounding = 100;
        }
        reboundingProgress = reboundingProgress % modifier;

        stamina += staminaProgress / modifier;
        if(stamina > 100){
            stamina = 100;
        }
        staminaProgress = staminaProgress % modifier;

        Random r = new Random();

        if(time > 1) {
            aggressiveness += r.nextInt(time * 2) - time;
            if(aggressiveness > 100){
                aggressiveness = 100;
            }
            else if(aggressiveness < 0){
                aggressiveness = 0;
            }

            workEthic += r.nextInt(time * 2) - time;
            if(workEthic > 100){
                workEthic = 100;
            }
            else if(workEthic < 0){
                workEthic = 0;
            }
        }
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
        double[] ftWeight = new double[] {.8, .8, .8, .6, .6};
        double[] postOffWeight = new double[]{.2, .2, .5, 1.3, 1.3};
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
        freeThrowShot = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * ftWeight[position-1]);
        postMove = (int) ((rating + (2 * r.nextInt(ratingVariability)) - ratingVariability) * postOffWeight[position-1]);
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
        stamina = r.nextInt(60) + 40;
        aggressiveness = r.nextInt(100);
        workEthic = r.nextInt(100);

        if(position < 4){
            trainingAs = 0;
        }
        else{
            trainingAs = 1;
        }


        calculateRating();
    }

    private void calculateRating(){
        double[] closeWeight = new double[] {.6, .7, .8, 1.0, 1.0};
        double[] midWeight = new double[] {.8, .8, .8, .7, .7};
        double[] longWeight = new double[] {.8, 1.0, .8, .5, .5};
        double[] ftWeight = new double[] {.8, .8, .8, .6, .6};
        double[] postOffWeight = new double[]{.2, .2, .5, 1.3, 1.3};
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
                freeThrowShot * ftWeight[currentPosition-1] + postMove * postOffWeight[currentPosition-1] +
                ballHandling * ballWeight[currentPosition-1] + passing * passWeight[currentPosition-1] +
                screening * screenWeight[currentPosition-1] + offBallMovement * offMoveWeight[currentPosition-1] +
                postDefense * postDefWeight[currentPosition-1] + perimeterDefense * perimDefWeight[currentPosition-1] +
                onBallDefense * onBallWeight[currentPosition-1] + offBallDefense * offBallWeight[currentPosition-1] +
                stealing * stealWeight[currentPosition-1] + rebounding * reboundWeight[currentPosition-1]);

        double div = closeWeight[currentPosition-1] + midWeight[currentPosition-1] + longWeight[currentPosition-1] +
                ftWeight[currentPosition-1] + postOffWeight[currentPosition-1] + ballWeight[currentPosition-1] +
                passWeight[currentPosition-1] + screenWeight[currentPosition-1] + offMoveWeight[currentPosition-1] +
                postDefWeight[currentPosition-1] + perimDefWeight[currentPosition-1] + onBallWeight[currentPosition-1] +
                offBallWeight[currentPosition-1] + stealWeight[currentPosition-1] + reboundWeight[currentPosition-1];

        overallRating = (int) (overallRating / div);
    }

    public int calculateRatingAtPosition(int position){
        double[] closeWeight = new double[] {.6, .7, .8, 1.0, 1.0};
        double[] midWeight = new double[] {.8, .8, .8, .7, .7};
        double[] longWeight = new double[] {.8, 1.0, .8, .5, .5};
        double[] ftWeight = new double[] {.8, .8, .8, .6, .6};
        double[] postOffWeight = new double[]{.2, .2, .5, 1.3, 1.3};
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
                freeThrowShot * ftWeight[position-1] + postMove * postOffWeight[position-1] +
                ballHandling * ballWeight[position-1] + passing * passWeight[position-1] +
                screening * screenWeight[position-1] + offBallMovement * offMoveWeight[position-1] +
                postDefense * postDefWeight[position-1] + perimeterDefense * perimDefWeight[position-1] +
                onBallDefense * onBallWeight[position-1] + offBallDefense * offBallWeight[position-1] +
                stealing * stealWeight[position-1] + rebounding * reboundWeight[position-1]);

        double div = closeWeight[position-1] + midWeight[position-1] + longWeight[position-1] +
                ftWeight[position-1] + postOffWeight[position-1] + ballWeight[position-1] +
                passWeight[position-1] + screenWeight[position-1] + offMoveWeight[position-1] +
                postDefWeight[position-1] + perimDefWeight[position-1] + onBallWeight[position-1] +
                offBallWeight[position-1] + stealWeight[position-1] + reboundWeight[position-1];

        return  (int) (rating / div);
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
        return (int)((freeThrowShot + offensiveModifier) * getFatigueFactor());
    }

    public int getPostMove(){
        return (int)((postMove + offensiveModifier) * getFatigueFactor());
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

    public int getAggressiveness(){
        return aggressiveness;
    }

    public int getWorkEthic(){
        return workEthic;
    }

    public int getTrainingAs(){
        return trainingAs;
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

    public int getCloseRangeShotProgress() {
        return closeRangeShotProgress;
    }

    public int getMidRangeShotProgress() {
        return midRangeShotProgress;
    }

    public int getLongRangeShotProgress() {
        return longRangeShotProgress;
    }

    public int getFreeThrowShotProgress() {
        return freeThrowShotProgress;
    }

    public int getPostMoveProgress() {
        return postMoveProgress;
    }

    public int getBallHandlingProgress() {
        return ballHandlingProgress;
    }

    public int getPassingProgress() {
        return passingProgress;
    }

    public int getScreeningProgress() {
        return screeningProgress;
    }

    public int getOffBallMovementProgress() {
        return offBallMovementProgress;
    }

    public int getPostDefenseProgress() {
        return postDefenseProgress;
    }

    public int getPerimeterDefenseProgress() {
        return perimeterDefenseProgress;
    }

    public int getOnBallDefenseProgress() {
        return onBallDefenseProgress;
    }

    public int getOffBallDefenseProgress() {
        return offBallDefenseProgress;
    }

    public int getStealingProgress() {
        return stealingProgress;
    }

    public int getReboundingProgress() {
        return reboundingProgress;
    }

    public int getStaminaProgress() {
        return staminaProgress;
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

    public void setTraining(int type){
        trainingAs = type;
    }

    public int[] getOffensiveAttributes(){
        return new int[]{closeRangeShot, midRangeShot, longRangeShot, freeThrowShot, postMove, ballHandling, passing, screening, offBallMovement};
    }

    public int[] getDefensiveAttributes(){
        return new int[]{postDefense, perimeterDefense, onBallDefense, offBallDefense, stealing, rebounding};
    }

    public int[] getOtherAttributes(){
        return new int[]{stamina, aggressiveness, workEthic};
    }
}

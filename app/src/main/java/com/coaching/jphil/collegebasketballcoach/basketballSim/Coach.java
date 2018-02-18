package com.coaching.jphil.collegebasketballcoach.basketballSim;

import java.util.Random;

/**
 * Created by jphil on 2/18/2018.
 */

public class Coach {

    private String firstName, lastName;
    private int position, overallRating;

    // Training Attributes
    private int shotTeaching;
    private int ballControlTeaching;
    private int screenTeaching;

    private int defPositionTeaching;
    private int defOnBallTeaching;
    private int defOffBallTeaching;
    private int reboundTeaching;
    private int stealTeaching;

    private int conditioningTeaching;

    private int workingWithGuards;
    private int workingWithBigs;

    public Coach(String firstName, String lastName, int position, int ability){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        generateAttributes(ability);
    }

    public Coach(String firstName, String lastName, int position, int shotTeaching, int ballControlTeaching,
                 int screenTeaching, int defPositionTeaching, int defOnBallTeaching, int defOffBallTeaching,
                 int reboundTeaching, int stealTeaching, int conditioningTeaching, int workingWithGuards, int workingWithBigs){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;

        this.shotTeaching = shotTeaching;
        this.screenTeaching = screenTeaching;
        this.ballControlTeaching = ballControlTeaching;

        this.defPositionTeaching = defPositionTeaching;
        this.defOnBallTeaching = defOnBallTeaching;
        this.defOffBallTeaching = defOffBallTeaching;
        this.reboundTeaching = reboundTeaching;
        this.stealTeaching = stealTeaching;

        this.conditioningTeaching = conditioningTeaching;

        this.workingWithBigs = workingWithBigs;
        this.workingWithGuards = workingWithGuards;

        calculateOverallRating();
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
                return "Head Coach";
            case 2:
                return "Assistant Coach";
        }
        return "Error!";
    }

    public int getOverallRating(){
        return overallRating;
    }

    public int getShotTeaching() {
        return shotTeaching;
    }

    public int getBallControlTeaching() {
        return ballControlTeaching;
    }

    public int getScreenTeaching() {
        return screenTeaching;
    }

    public int getDefPositionTeaching() {
        return defPositionTeaching;
    }

    public int getDefOnBallTeaching() {
        return defOnBallTeaching;
    }

    public int getDefOffBallTeaching() {
        return defOffBallTeaching;
    }

    public int getReboundTeaching() {
        return reboundTeaching;
    }

    public int getStealTeaching() {
        return stealTeaching;
    }

    public int getConditioningTeaching() {
        return conditioningTeaching;
    }

    public int getWorkingWithGuards() {
        return workingWithGuards;
    }

    public int getWorkingWithBigs() {
        return workingWithBigs;
    }

    private void generateAttributes(int ability){
        int abilityVariability = 10;
        Random r = new Random();

        shotTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        ballControlTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        screenTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        defPositionTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        defOnBallTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        defOffBallTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        reboundTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        stealTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        conditioningTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        workingWithGuards = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        workingWithBigs = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        calculateOverallRating();
    }

    private void calculateOverallRating(){
        overallRating = (shotTeaching + ballControlTeaching + screenTeaching + defPositionTeaching
        + defOnBallTeaching + defOffBallTeaching + reboundTeaching + stealTeaching + conditioningTeaching
        + workingWithBigs + workingWithGuards) / 11;
    }
}

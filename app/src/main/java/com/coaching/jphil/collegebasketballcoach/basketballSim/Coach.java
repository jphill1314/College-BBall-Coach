package com.coaching.jphil.collegebasketballcoach.basketballSim;

import java.util.ArrayList;
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
    private int offPositionTeaching;

    private int defPositionTeaching;
    private int defOnBallTeaching;
    private int defOffBallTeaching;
    private int reboundTeaching;
    private int stealTeaching;

    private int conditioningTeaching;

    private int tendencyToSub;

    private int recruitingAbility;
    private ArrayList<Recruit> recruits;

    public Coach(String firstName, String lastName, int position, int ability){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;

        recruits = new ArrayList<>();

        generateAttributes(ability);
    }

    public Coach(String firstName, String lastName, int position, int shotTeaching, int ballControlTeaching,
                 int screenTeaching, int offPositionTeaching, int defPositionTeaching, int defOnBallTeaching,
                 int defOffBallTeaching, int reboundTeaching, int stealTeaching, int conditioningTeaching,
                 int recruitingAbility, int tendencyToSub){
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;

        this.shotTeaching = shotTeaching;
        this.screenTeaching = screenTeaching;
        this.ballControlTeaching = ballControlTeaching;
        this.offPositionTeaching = offPositionTeaching;

        this.defPositionTeaching = defPositionTeaching;
        this.defOnBallTeaching = defOnBallTeaching;
        this.defOffBallTeaching = defOffBallTeaching;
        this.reboundTeaching = reboundTeaching;
        this.stealTeaching = stealTeaching;

        this.conditioningTeaching = conditioningTeaching;
        this.recruitingAbility = recruitingAbility;

        this.tendencyToSub = tendencyToSub;

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

    public ArrayList<Recruit> getRecruits(){
        return recruits;
    }

    public boolean addRecruit(Recruit recruit){
        if(recruits == null){
            recruits = new ArrayList<>();
        }

        if(recruits.size() < 2) {
            recruits.add(recruit);
            recruit.toggleIsRecruited();
            return true;
        }
        return false;
    }

    public void removeRecruit(Recruit recruit){
        if(recruits != null){
            recruits.remove(recruit);
            recruit.toggleIsRecruited();
        }
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

    public int getTendencyToSub(){
        return tendencyToSub;
    }

    public int getRecruitingAbility(){
        return recruitingAbility;
    }

    public int getOffPositionTeaching() {
        return offPositionTeaching;
    }

    public void recruitRecruits(boolean bigWin, boolean badLoss, int spots){
        for(Recruit r: recruits){
            r.attemptToRecruit(recruitingAbility, bigWin, badLoss, spots);
        }
    }

    private void generateAttributes(int ability){
        int abilityVariability = 10;
        Random r = new Random();

        shotTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        ballControlTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        screenTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        offPositionTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        defPositionTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        defOnBallTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        defOffBallTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        reboundTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;
        stealTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        conditioningTeaching = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        recruitingAbility = ability + 2 * r.nextInt(abilityVariability) - abilityVariability;

        tendencyToSub = 25 + r.nextInt(50);

        calculateOverallRating();
    }

    private void calculateOverallRating(){
        overallRating = (shotTeaching + ballControlTeaching + screenTeaching + defPositionTeaching
        + defOnBallTeaching + defOffBallTeaching + reboundTeaching + stealTeaching + conditioningTeaching
        + offPositionTeaching + recruitingAbility) / 11;
    }

}

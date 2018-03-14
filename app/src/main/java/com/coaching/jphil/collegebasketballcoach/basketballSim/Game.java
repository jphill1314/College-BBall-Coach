package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Game {

    private int homeCourtAdvantage = 3;
    private int scoreVariability = 14; // +/- how much the margin can vary from its relative efficiency
    private int randomBoundValue = 25;

    private Team homeTeam, awayTeam;
    private int homeScore, awayScore;
    private boolean isNeutralCourt;
    private boolean isPlayed;

    private int id;

    public Game(Team homeTeam, Team awayTeam){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        isPlayed = false;
        isNeutralCourt = false;
    }

    public Game(Team homeTeam, Team awayTeam, boolean isNeutralCourt){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.isNeutralCourt = isNeutralCourt;
        isPlayed = false;
    }

    public Game(Team homeTeam, Team awayTeam, int homeScore, int awayScore, boolean isPlayed, boolean isNeutralCourt){
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.isPlayed = isPlayed;
        this.isNeutralCourt = isNeutralCourt;
    }

    public String getHomeTeamName(){
        return homeTeam.getFullName();
    }

    public String getAwayTeamName(){
        return awayTeam.getFullName();
    }

    public boolean isPlayed(){
        return isPlayed;
    }

    public Team getHomeTeam(){
        return homeTeam;
    }

    public Team getAwayTeam(){
        return awayTeam;
    }

    public int getHomeScore(){
        return homeScore;
    }

    public int getAwayScore(){
        return awayScore;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public boolean getIsNeutralCourt(){
        return isNeutralCourt;
    }

    public boolean homeTeamWin(){
        if(isPlayed && (homeScore > awayScore)){
            return true;
        }
        else{
            return false;
        }
    }

    public String getFormattedScore(){
        if(isPlayed) {
            return homeScore + " - " + awayScore;
        }
        else{
            return " - ";
        }
    }

    public boolean simulateGame(){
        if(homeTeam.getTotalMinutes() == 200 && awayTeam.getTotalMinutes() == 200) {
            preGameSetUp();
            do {
                while (simPlay()) {
                    continue;
                }
            } while (startNextHalf());
            isPlayed = true;

            homeTeam.playGame(homeTeamWin());
            awayTeam.playGame(!homeTeamWin());

            return true;
        }
        return false;
    }

    private ArrayList<String> plays;
    private int half, timeRemaining, playerWithBall, location, passesSinceShot;
    private boolean madeShot, deadBall, homeTeamHasBall, homeTeamHasPosArrow;
    private boolean[] timeouts;
    private Random r;

    private int homeSteals, awaySteals;
    private int[] homeShots, homeMade, awayShots, awayMade;
    private int[] homeRebounds, awayRebounds;
    private ArrayList<Integer> passes;

    private void preGameSetUp(){
        plays = new ArrayList<>();
        passes = new ArrayList<>();
        r = new Random();
        half = 1;
        timeRemaining = 20 * 60;
        homeScore = 0;
        awayScore = 0;
        playerWithBall = 1;
        location = -1;
        passesSinceShot = 0;

        homeShots = new int[]{0, 0, 0};
        awayShots = new int[]{0, 0, 0};
        homeSteals = 0;
        awaySteals = 0;
        homeMade = new int[]{0, 0, 0};
        awayMade = new int[]{0, 0, 0};

        homeRebounds = new int[]{0,0};
        awayRebounds = new int[]{0,0};

        for(Player p: homeTeam.getPlayers()){
            p.setGameModifiers(!isNeutralCourt, homeTeam.getOverallRating() - awayTeam.getOverallRating());
        }
        for(Player p: awayTeam.getPlayers()){
            p.setGameModifiers(false, awayTeam.getOverallRating() - homeTeam.getOverallRating());
        }

        // very simple jump ball
        homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding();
        homeTeamHasPosArrow = !(homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding());
        madeShot = false;
        deadBall = false;
    }

    private boolean startNextHalf(){
        if(half == 1){
            // setup 2nd half
            half++;
            timeRemaining = 20 * 60;
            homeTeamHasBall = homeTeamHasPosArrow;
            homeTeamHasPosArrow = !homeTeamHasPosArrow;
            deadBall = true;
            madeShot = false;

            return true;
        }
        else if(homeScore == awayScore){
            // setup overtime
            half++;
            timeRemaining = 5 * 60;
            homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding();
            homeTeamHasPosArrow = !(homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding());
            madeShot = false;
            deadBall = false;
            return true;
        }

        isPlayed = true;
        return false;
    }

    private boolean simPlay(){
        if(deadBall){
            if(homeTeamHasBall){
                homeScore += getPass(homeTeam, awayTeam);
            }
            else{
                awayScore += getPass(awayTeam, homeTeam);
            }
        }
        else{
            int shotChance = passesSinceShot * r.nextInt(randomBoundValue);
            if(shotChance > 3 * randomBoundValue){
                if(homeTeamHasBall){
                    homeScore += getShot(homeTeam, awayTeam, false);
                }
                else{
                    awayScore += getShot(awayTeam, homeTeam, false);
                }
            }
            else{
                if(homeTeamHasBall){
                    homeScore += getPass(homeTeam, awayTeam);
                }
                else{
                    awayScore += getPass(awayTeam, homeTeam);
                }
            }
        }

        //System.out.println(getTimeAsString() + " Home: " + homeScore + " Away: " + awayScore);
        if(timeRemaining > 0){
            return true;
        }
        else{
            timeRemaining = 0;
            return false;
        }
    }

    private String getTimeAsString(){
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining - (minutes * 60);

        return "Half: " + half + " " + minutes + ":" + seconds;
    }

    private int getPass(Team offense, Team defense){
        Player passer, target;
        Player passDef, targetDef;

        if(deadBall){
            getInbounder(offense);
            deadBall = false;
            madeShot = false;
        }

        passer = offense.getPlayers().get(playerWithBall - 1);
        passDef = defense.getPlayers().get(passer.getPosition() - 1);

        target = getTarget(offense);
        targetDef = defense.getPlayers().get(target.getPosition() - 1);

        int passSuccess = (passer.getPassing() + target.getOffBallMovement()) / (r.nextInt(randomBoundValue) + 1);
        int stealSuccess = (passDef.getOnBallDefense() + targetDef.getOffBallDefense()) / (r.nextInt(randomBoundValue) + 1);

        if(passSuccess >= 25){
            // successful shot
            passesSinceShot++;
//            if(homeTeamHasBall){
//                System.out.println("Home passes the ball");
//            }
//            else{
//                System.out.println("Away passes the ball");
//            }
            if(location < 1){
                location = 1;
                timeRemaining -= (int)(10 - (offense.getPace() / 90.0) * r.nextInt(7));
            }
            playerWithBall = target.getPosition();
            if(passSuccess > 50 && location > -1){
                // pass leading to a shot
                timeRemaining -= r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(4);
                return getShot(offense, defense, true);
            }
        }
        else if(stealSuccess > 75){
            // unsuccessful pass -> turnover
            passes.add(passesSinceShot);
            passesSinceShot = 0;
            if(r.nextBoolean()){
                // ball is stolen by off ball defender
                playerWithBall = target.getPosition();
            }


            if(homeTeamHasBall){
                awaySteals++;
            }
            else{
                homeSteals++;
            }

//            if(homeTeamHasBall){
//                System.out.println("Home turns the ball over");
//            }
//            else{
//                System.out.println("Away turns the ball over");
//            }
            changePossession();
        }

        timeRemaining -= r.nextInt(5) - (offense.getPace() / 90.0) * r.nextInt(3);
        return 0;
    }

    private void getInbounder(Team team){
        int pgRate = team.getPlayers().get(0).getPassing() + r.nextInt(randomBoundValue);
        int sgRate = team.getPlayers().get(1).getPassing() + r.nextInt(randomBoundValue);
        int sfRate = team.getPlayers().get(2).getPassing() + r.nextInt(randomBoundValue);

        if(sfRate > pgRate && sfRate > sgRate){
            playerWithBall = 3;
        }
        else if(sgRate > pgRate && sgRate > sfRate){
            playerWithBall = 2;
        }
        else{
            playerWithBall = 1;
        }
    }

    private Player getTarget(Team team){
        int pgRate = 0;
        int sgRate = 0;
        int sfRate = 0;
        int pfRate = 0;
        int cRate = 0;

        if(playerWithBall != 1){
            pgRate = team.getPlayers().get(0).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if(playerWithBall != 2){
            sgRate = team.getPlayers().get(1).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if(playerWithBall != 3){
            sfRate = team.getPlayers().get(2).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if(playerWithBall != 4){
            pfRate = team.getPlayers().get(3).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if(playerWithBall != 5){
            cRate = team.getPlayers().get(4).getOffBallMovement() + r.nextInt(randomBoundValue);
        }

        if(cRate > pfRate && cRate > sfRate && cRate > sgRate && cRate > pgRate){
            return team.getPlayers().get(4);
        }
        else if(pfRate > cRate && pfRate > sfRate && pfRate > sgRate && pfRate > pgRate){
            return team.getPlayers().get(3);
        }
        else if(sfRate > cRate && sfRate > pfRate && sfRate > sgRate && sfRate > pgRate){
            return team.getPlayers().get(2);
        }
        else if((sgRate > cRate && sgRate > pfRate && sgRate > sfRate && sgRate > pgRate) || playerWithBall == 1){
            return team.getPlayers().get(1);
        }
        else{
            return team.getPlayers().get(0);
        }

    }

    private int getShot(Team offense, Team defense, boolean assisted){
        passes.add(passesSinceShot);
        passesSinceShot = 0;
        int shotLocation;
        Player shooter = offense.getPlayers().get(playerWithBall - 1);

        int shotClose = (int) (shooter.getCloseRangeShot() * (1 - offense.getOffenseFavorsThrees()/100.0) *
                (1 - defense.getDefenseFavorsThrees()/100.0) + r.nextInt(randomBoundValue));

        int shotMid = (int) (shooter.getMidRangeShot() * .4 + r.nextInt(randomBoundValue));

        int shotLong = (int) (shooter.getLongRangeShot() * (offense.getOffenseFavorsThrees()/100.0) *
                (defense.getDefenseFavorsThrees()/100.0) + r.nextInt(randomBoundValue));

        if(shooter.getPosition() == 1 || shooter.getPosition() == 2){
            // preference for guards to favor 3s
            shotLong += 20;
        }
        else{
            shotClose += 20;
        }

        if(shotClose > shotMid && shotClose > shotLong){
            shotLocation = 1;
        }
        else if(shotMid > shotClose && shotMid > shotLong){
            shotLocation = 2;
        }
        else{
            shotLocation = 3;
        }

        if(location < 1){
            shotLocation = 3;
        }

        int shotSuccess;
        if(shotLocation == 1){
            // take a close range shot
            shotSuccess = (int) (shooter.getCloseRangeShot() -
                    ((defense.getPlayers().get(playerWithBall - 1).getOnBallDefense() +
                            defense.getPlayers().get(playerWithBall - 1).getPostDefense())) / 2.0 +
                    r.nextInt(randomBoundValue));
        }
        else if(shotLocation == 2){
            shotSuccess = (int) (shooter.getMidRangeShot() -
                    ((defense.getPlayers().get(playerWithBall - 1).getOnBallDefense() +
                            defense.getPlayers().get(playerWithBall - 1).getPosition() +
                            defense.getPlayers().get(playerWithBall - 1).getPerimeterDefense()) / 3.0) +
                    r.nextInt(randomBoundValue));
        }
        else{
            shotSuccess = (int) (shooter.getLongRangeShot() -
                    ((defense.getPlayers().get(playerWithBall - 1).getOnBallDefense() +
                            defense.getPlayers().get(playerWithBall - 1).getPerimeterDefense())) / 2.0 +
                    r.nextInt(randomBoundValue));
        }

        if(assisted){
            // shots off of good passes go in more often!
            shotSuccess += 20;
        }


        if(homeTeamHasBall){
            homeShots[shotLocation - 1]++;
        }
        else{
            awayShots[shotLocation - 1]++;
        }

        if(shotSuccess > 0 && r.nextBoolean()){
            // second parameter is a totally baseless assertion for balance
            timeRemaining -= r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(3);
            deadBall = true;
            madeShot = true;


            if(homeTeamHasBall){
                //System.out.println("Home makes a shot");
                homeMade[shotLocation - 1]++;
            }
            else{
                //System.out.println("Away makes a shot");
                awayMade[shotLocation - 1]++;
            }
            changePossession();
            if(shotLocation == 1){
                return 2;
            }
            else return shotLocation;
        }

//        if(homeTeamHasBall){
//            System.out.println("Home misses a shot");
//        }
//        else{
//            System.out.println("Away misses a shot");
//        }

        // missed shot
        getRebound(offense, defense);
        timeRemaining -= r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(4);
        return 0;
    }

    private void getRebound(Team offense, Team defense){
        int[] offChance = getReboundChance(offense);
        int[] defChance = getReboundChance(defense);

        offChance[playerWithBall-1] -= 15; // less likely to get own rebound
        int offHigh = 0;
        int defHigh = 0;

        for(int x = 1; x < 5; x++){
            if(offChance[x] >= offChance[offHigh]){
                offHigh = x;
            }

            if(defChance[x] >= defChance[defHigh]){
                defHigh = x;
            }
        }

        if(offChance[offHigh] - 5 > defChance[defHigh]){
            if(homeTeamHasBall){
                homeRebounds[0]++;
            }
            else{
                awayRebounds[0]++;
            }
            playerWithBall = offHigh + 1;
        }
        else{
            if(!homeTeamHasBall){
                homeRebounds[1]++;
            }
            else{
                awayRebounds[1]++;
            }
            playerWithBall = defHigh + 1;
            changePossession();
        }
    }

    private int[] getReboundChance(Team team){
        int[] values = new int[5];

        for(int x = 0; x < 5; x++){
            values[x] = team.getPlayers().get(x).getRebounding() + r.nextInt(randomBoundValue);
        }
        return values;
    }

    private void changePossession(){
        homeTeamHasBall = !homeTeamHasBall;
        if(location == -1){
            location = 1;
        }
        else if(location == 1){
            location = -1;
        }
    }}

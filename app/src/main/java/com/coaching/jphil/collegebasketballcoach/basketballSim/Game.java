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

    public Game(Team homeTeam, Team awayTeam) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        isPlayed = false;
        isNeutralCourt = false;
    }

    public Game(Team homeTeam, Team awayTeam, boolean isNeutralCourt) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.isNeutralCourt = isNeutralCourt;
        isPlayed = false;
    }

    public Game(Team homeTeam, Team awayTeam, int homeScore, int awayScore, boolean isPlayed, boolean isNeutralCourt) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.isPlayed = isPlayed;
        this.isNeutralCourt = isNeutralCourt;
    }

    public String getHomeTeamName() {
        return homeTeam.getFullName();
    }

    public String getAwayTeamName() {
        return awayTeam.getFullName();
    }

    public boolean isPlayed() {
        return isPlayed;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsNeutralCourt() {
        return isNeutralCourt;
    }

    public boolean homeTeamWin() {
        if (isPlayed && (homeScore > awayScore)) {
            return true;
        } else {
            return false;
        }
    }

    public String getFormattedScore() {
        if (isPlayed) {
            return homeScore + " - " + awayScore;
        } else {
            return " - ";
        }
    }

    public void setIsPlayed(boolean isPlayed){
        this.isPlayed = isPlayed;
    }

    public int getShotClock(){
        return shotClock;
    }

    public int getHalf(){
        return half;
    }

    public int getTimeRemaining(){
        return timeRemaining;
    }

    public String getFormattedTime(){
        int min = timeRemaining / 60;
        int sec = timeRemaining - min * 60;
        if(sec >= 10){
            return min + ":" + sec;
        }
        else if(sec != 0){
            return min + ":0" + sec;
        }
        else{
            return min + ":00";
        }
    }

    public ArrayList<String> getPlays(){
        return plays;
    }

    public boolean simulateGame() {
        if (homeTeam.getTotalMinutes() == 200 && awayTeam.getTotalMinutes() == 200) {
            preGameSetUp();
            do {
                while (simPlay()) {
                    continue;
                }
            } while (startNextHalf());
            isPlayed = true;

            homeTeam.playGame(homeTeamWin());
            awayTeam.playGame(!homeTeamWin());

            if(homeTeam.isPlayerControlled() || awayTeam.isPlayerControlled()) {
                Log.v("Game", getLoggerOutput());
            }
            return true;
        }
        return false;
    }

    //TODO: save game stats to DB and continue to balance for realism
    private ArrayList<String> plays;
    private int half, timeRemaining, playerWithBall, location, passesSinceShot, shotClock;
    private boolean madeShot, deadBall, homeTeamHasBall, homeTeamHasPosArrow;
    private boolean[] timeouts;
    private Random r;

    private int homeSteals, awaySteals;
    private int[] homeShots, homeMade, awayShots, awayMade;
    private int[] homeRebounds, awayRebounds;

    private String currentPlay;

    public void preGameSetUp() {
        plays = new ArrayList<>();
        r = new Random();
        half = 1;
        timeRemaining = 20 * 60;
        shotClock = 30;
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

        homeRebounds = new int[]{0, 0};
        awayRebounds = new int[]{0, 0};

        for (Player p : homeTeam.getPlayers()) {
            p.setGameModifiers(!isNeutralCourt, homeTeam.getOverallRating() - awayTeam.getOverallRating());
        }
        for (Player p : awayTeam.getPlayers()) {
            p.setGameModifiers(false, awayTeam.getOverallRating() - homeTeam.getOverallRating());
        }

        // very simple jump ball
        homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding();
        homeTeamHasPosArrow = !(homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding());
        madeShot = false;
        deadBall = false;

        if(homeTeamHasBall){
            plays.add(homeTeam.getFullName() + " has won the tip off!");
        }
        else{
            plays.add(awayTeam.getFullName() + " has won the tip off!");
        }
    }

    public boolean startNextHalf() {
        if (half == 1) {
            // setup 2nd half
            half++;
            timeRemaining = 20 * 60;
            shotClock = 30;
            homeTeamHasBall = homeTeamHasPosArrow;
            homeTeamHasPosArrow = !homeTeamHasPosArrow;
            deadBall = true;
            madeShot = false;

            return true;
        } else if (homeScore == awayScore) {
            // setup overtime
            half++;
            timeRemaining = 5 * 60;
            shotClock = 30;
            homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding();
            homeTeamHasPosArrow = !(homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding());
            madeShot = false;
            deadBall = false;
            return true;
        }

        isPlayed = true;
        return false;
    }

    public boolean simPlay() {
        currentPlay = "";
        if (deadBall) {
            if (homeTeamHasBall) {
                homeScore += getPass(homeTeam, awayTeam);
            } else {
                awayScore += getPass(awayTeam, homeTeam);
            }
        } else {
            int shotChance = passesSinceShot * r.nextInt(randomBoundValue);
            int shotUrgency = 5;
            if(homeTeamHasBall){
                shotUrgency *= homeTeam.getPace() / 30;
            }
            else{
                shotUrgency *= awayTeam.getPace() / 30;
            }
            if (shotChance > 2 *randomBoundValue || (shotClock < shotUrgency && r.nextBoolean())) {
                if (homeTeamHasBall) {
                    homeScore += getShot(homeTeam, awayTeam, false);
                } else {
                    awayScore += getShot(awayTeam, homeTeam, false);
                }
            } else {
                if (homeTeamHasBall) {
                    homeScore += getPass(homeTeam, awayTeam);
                } else {
                    awayScore += getPass(awayTeam, homeTeam);
                }
            }
        }

        if(shotClock <= 0){
            currentPlay += "\nTurnover! Shot clock violation!";
            changePossession();
        }

        if(!currentPlay.equals("") && !currentPlay.contains(plays.get(0).substring(15))) {
            plays.add(0, getFormattedTime() + " (" + shotClock + ") - " + currentPlay);
        }

        if (timeRemaining > 0) {
            return true;
        } else {
            timeRemaining = 0;
            return false;
        }
    }

    private int getPass(Team offense, Team defense) {
        Player passer, target;
        Player passDef, targetDef;
        passesSinceShot++;

        if (deadBall) {
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

        if (passSuccess >= 0) {
            // successful pass
            if (location < 1) {
                location = 1;
                int timeChange = (int) (8 - (offense.getPace() / 90.0) * r.nextInt(6));
                timeRemaining -= timeChange;
                shotClock -= timeChange;
            }
            currentPlay += passer.getFullName() + " passes the ball to " + target.getFullName();
            playerWithBall = target.getPosition();

            if (passSuccess > 50 && location > -1) {
                // pass leading to a shot
                int timeChange = (int) (8 - (offense.getPace() / 90.0) * r.nextInt(4));
                timeRemaining -= timeChange;
                shotClock -= timeChange;
                return getShot(offense, defense, true);
            }
        } else if (stealSuccess > 75) {
            // unsuccessful pass -> turnover
            passesSinceShot = 0;
            currentPlay += passer.getFullName() + " turns the ball over! ";
            if (r.nextBoolean()) {
                // ball is stolen by off ball defender
                currentPlay += targetDef.getFullName() + " has stolen the ball!";
                playerWithBall = target.getPosition();
            }
            else{
                currentPlay += passDef.getFullName() + " has stolen the ball!";
            }


            if (homeTeamHasBall) {
                awaySteals++;
            } else {
                homeSteals++;
            }

            changePossession();
        }

        if(currentPlay.length() == 0) {
            currentPlay += passer.getFullName() + " is dribbling with the ball";
        }
        int timeChange = (int) (4 - (offense.getPace() / 90.0) * r.nextInt(3));
        timeRemaining -= timeChange;
        shotClock -= timeChange;
        return 0;
    }

    private void getInbounder(Team team) {
        int pgRate = team.getPlayers().get(0).getPassing() + r.nextInt(randomBoundValue);
        int sgRate = team.getPlayers().get(1).getPassing() + r.nextInt(randomBoundValue);
        int sfRate = team.getPlayers().get(2).getPassing() + r.nextInt(randomBoundValue);

        if (sfRate > pgRate && sfRate > sgRate) {
            playerWithBall = 3;
        } else if (sgRate > pgRate && sgRate > sfRate) {
            playerWithBall = 2;
        } else {
            playerWithBall = 1;
        }

        currentPlay += team.getFullName() + " will run an inbounds play here.\n";
        passesSinceShot--;
    }

    private Player getTarget(Team team) {
        int pgRate = 0;
        int sgRate = 0;
        int sfRate = 0;
        int pfRate = 0;
        int cRate = 0;

        if (playerWithBall != 1) {
            pgRate = team.getPlayers().get(0).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if (playerWithBall != 2) {
            sgRate = team.getPlayers().get(1).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if (playerWithBall != 3) {
            sfRate = team.getPlayers().get(2).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if (playerWithBall != 4) {
            pfRate = team.getPlayers().get(3).getOffBallMovement() + r.nextInt(randomBoundValue);
        }
        if (playerWithBall != 5) {
            cRate = team.getPlayers().get(4).getOffBallMovement() + r.nextInt(randomBoundValue);
        }

        if (cRate > pfRate && cRate > sfRate && cRate > sgRate && cRate > pgRate) {
            return team.getPlayers().get(4);
        } else if (pfRate > cRate && pfRate > sfRate && pfRate > sgRate && pfRate > pgRate) {
            return team.getPlayers().get(3);
        } else if (sfRate > cRate && sfRate > pfRate && sfRate > sgRate && sfRate > pgRate) {
            return team.getPlayers().get(2);
        } else if ((sgRate > cRate && sgRate > pfRate && sgRate > sfRate && sgRate > pgRate) || playerWithBall == 1) {
            return team.getPlayers().get(1);
        } else {
            return team.getPlayers().get(0);
        }

    }

    private int getShot(Team offense, Team defense, boolean assisted) {
        passesSinceShot = 0;
        int shotLocation;
        Player shooter = offense.getPlayers().get(playerWithBall - 1);

        int shotClose = (int) (shooter.getCloseRangeShot() * (1 - offense.getOffenseFavorsThrees() / 100.0) *
                (1 - defense.getDefenseFavorsThrees() / 100.0) + r.nextInt(randomBoundValue));

        int shotMid = (int) (shooter.getMidRangeShot() * .4 + r.nextInt(randomBoundValue));

        int shotLong = (int) (shooter.getLongRangeShot() * (offense.getOffenseFavorsThrees() / 100.0) *
                (defense.getDefenseFavorsThrees() / 100.0) + r.nextInt(randomBoundValue));

        if (shooter.getPosition() == 1 || shooter.getPosition() == 2) {
            // preference for guards to favor 3s
            shotLong += 20;
        } else {
            shotClose += 20;
        }

        if(currentPlay.length() != 0){
            currentPlay += "\n";
        }
        if (shotClose > shotMid && shotClose > shotLong) {
            currentPlay += shooter.getFullName() + " shoots from up close and ";
            shotLocation = 1;
        } else if (shotMid > shotClose && shotMid > shotLong) {
            currentPlay += shooter.getFullName() + " shoots from mid-range and ";
            shotLocation = 2;
        } else {
            currentPlay += shooter.getFullName() + " shoots from 3 and ";
            shotLocation = 3;
        }

        if (location < 1) {
            shotLocation = 3;
        }

        int shotSuccess;
        if (shotLocation == 1) {
            // take a close range shot
            shotSuccess = (int) (shooter.getCloseRangeShot() -
                    ((defense.getPlayers().get(playerWithBall - 1).getOnBallDefense() +
                            defense.getPlayers().get(playerWithBall - 1).getPostDefense())) / 2.0 +
                    r.nextInt(randomBoundValue));
        } else if (shotLocation == 2) {
            shotSuccess = (int) (shooter.getMidRangeShot() -
                    ((defense.getPlayers().get(playerWithBall - 1).getOnBallDefense() +
                            defense.getPlayers().get(playerWithBall - 1).getPosition() +
                            defense.getPlayers().get(playerWithBall - 1).getPerimeterDefense()) / 3.0) +
                    r.nextInt(randomBoundValue));
        } else {
            shotSuccess = (int) (shooter.getLongRangeShot() -
                    ((defense.getPlayers().get(playerWithBall - 1).getOnBallDefense() +
                            defense.getPlayers().get(playerWithBall - 1).getPerimeterDefense())) / 2.0 +
                    r.nextInt(randomBoundValue));
        }

        if (assisted) {
            // shots off of good passes go in more often!
            shotSuccess += 20;
        }


        if (homeTeamHasBall) {
            homeShots[shotLocation - 1]++;
        } else {
            awayShots[shotLocation - 1]++;
        }

        if (shotSuccess > 0 && r.nextBoolean()) {
            // second parameter is a totally baseless assertion for balance
            int timeChange = (int) (r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(3));
            timeRemaining -= timeChange;
            shotClock -= timeChange;
            deadBall = true;
            madeShot = true;

            currentPlay += "makes it!";
            if (homeTeamHasBall) {
                //System.out.println("Home makes a shot");
                homeMade[shotLocation - 1]++;
            } else {
                //System.out.println("Away makes a shot");
                awayMade[shotLocation - 1]++;
            }
            changePossession();
            if (shotLocation == 1) {
                return 2;
            } else return shotLocation;
        }

//        if(homeTeamHasBall){
//            System.out.println("Home misses a shot");
//        }
//        else{
//            System.out.println("Away misses a shot");
//        }
        currentPlay += "misses it!";
        // missed shot
        getRebound(offense, defense);
        int timeChange = (int) (r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(4));
        timeRemaining -= timeChange;
        shotClose -= timeChange;
        return 0;
    }

    private void getRebound(Team offense, Team defense) {
        int[] offChance = getReboundChance(offense);
        int[] defChance = getReboundChance(defense);

        offChance[playerWithBall - 1] -= 15; // less likely to get own rebound
        int offHigh = 0;
        int defHigh = 0;

        for (int x = 1; x < 5; x++) {
            if (offChance[x] >= offChance[offHigh]) {
                offHigh = x;
            }

            if (defChance[x] >= defChance[defHigh]) {
                defHigh = x;
            }
        }

        if (offChance[offHigh] - 5 > defChance[defHigh]) {
            if (homeTeamHasBall) {
                homeRebounds[0]++;
            } else {
                awayRebounds[0]++;
            }
            playerWithBall = offHigh + 1;
            currentPlay += "\n" + offense.getPlayers().get(playerWithBall-1).getFullName() + " grabs the offensive rebound.";
            if(timeRemaining >= 30) {
                shotClock = 30;
            }
            else{
                shotClock = timeRemaining;
            }
        } else {
            if (!homeTeamHasBall) {
                homeRebounds[1]++;
            } else {
                awayRebounds[1]++;
            }
            currentPlay += "\n" + defense.getPlayers().get(playerWithBall-1).getFullName() + " grabs the defensive rebound.";
            playerWithBall = defHigh + 1;
            changePossession();
        }
    }

    private int[] getReboundChance(Team team) {
        int[] values = new int[5];

        for (int x = 0; x < 5; x++) {
            values[x] = team.getPlayers().get(x).getRebounding() + r.nextInt(randomBoundValue);
        }
        return values;
    }

    private void changePossession() {
        homeTeamHasBall = !homeTeamHasBall;
        if (location == -1) {
            location = 1;
        } else if (location == 1) {
            location = -1;
        }
        if(timeRemaining >= 30) {
            shotClock = 30;
        }
        else{
            shotClock = timeRemaining;
        }
        passesSinceShot = 0;
    }

    private String getLoggerOutput() {
        return "Home:\n" + getShotStats(homeShots, homeMade) + "\nsteals: " + homeSteals +
               "\nRebounds: " + homeRebounds[0] + " - " + homeRebounds[1] +
               "\nAway:\n" + getShotStats(awayShots, awayMade) + "\nsteals: " + awaySteals +
               "\nRebounds: " + awayRebounds[0] + " - " + awayRebounds[1];
    }

    private String getShotStats(int[] taken, int[] made){
        double close = (made[0] * 1.0) / taken[0];
        double mid = (made[1] * 1.0) / taken[1];
        double longR = (made[2] * 1.0) / taken[2];

        return "close: " + made[0] + "/" + taken[0] + " - " + close +
               "\nmid: " + made[1] + "/" + taken[1] + " - " + mid +
               "\nlong: " + made[2] + "/" + taken[2] + " - " + longR;
    }
}

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
        savePlays = false;
        if (homeTeam.getTotalMinutes() == 200 && awayTeam.getTotalMinutes() == 200) {
            preGameSetUp();
            do {
                int lastPlay = simPlay();
                while (lastPlay != -1) {
                    if(lastPlay == 1){
                        homeTeam.getCoachTalk(awayScore - homeScore);
                        awayTeam.getCoachTalk(homeScore - awayScore);
                    }

                    lastPlay = simPlay();
                }
            } while (startNextHalf());
            isPlayed = true;

            homeTeam.playGame(homeTeamWin());
            awayTeam.playGame(!homeTeamWin());
            return true;
        }
        return false;
    }

    //TODO: save game stats to DB and continue to balance for realism
    private ArrayList<String> plays;
    private int half, timeRemaining, playerWithBall, location, passesSinceShot, shotClock, lastPlayerWithBall;
    private boolean madeShot, deadBall, homeTeamHasBall, homeTeamHasPosArrow;
    private boolean[] mediaTimeouts;
    private boolean playerWantsTO, recentTO;
    private int homeTimeouts, awayTimeouts, homeFouls, awayFouls;
    private boolean savePlays = false;
    private Random r;

    private String currentPlay, foulString;

    public void setSavePlays(boolean savePlays){
        this.savePlays = savePlays;
    }

    public void preGameSetUp() {
        plays = new ArrayList<>();
        r = new Random();
        half = 1;
        timeRemaining = 20 * 60;
        shotClock = 30;
        homeScore = 0;
        awayScore = 0;
        homeFouls = 0;
        awayFouls = 0;
        playerWithBall = 1;
        lastPlayerWithBall = 1;
        location = -1;
        passesSinceShot = 0;

        mediaTimeouts = new boolean[]{false, false, false, false, false, false, false, false, false, false};
        playerWantsTO = false;
        recentTO = false;
        homeTimeouts = 4;
        awayTimeouts = 4;

        // very simple jump ball
        homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding();
        homeTeamHasPosArrow = !(homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding());
        madeShot = false;
        deadBall = false;

        if(homeTeamHasBall){
            plays.add(getFormattedTime() + " (30) - " + homeTeam.getFullName() + " has won the tip off!");
        }
        else{
            plays.add(getFormattedTime() + " (30) - " + awayTeam.getFullName() + " has won the tip off!");
        }

        for(Player p: homeTeam.getPlayers()){
            p.preGameSetup();
        }
        for(Player p: awayTeam.getPlayers()){
            p.preGameSetup();
        }
    }

    public void coachTalk(Team team, boolean homeCourt, int scoreDif){
        for(Player p: team.getPlayers()){
            p.setGameModifiers(homeCourt, scoreDif, team.getCoachTalk(scoreDif));
        }
    }

    public void coachTalk(Team team, boolean homeCourt, int scoreDif, int talkType){
        for(Player p: team.getPlayers()){
            p.setGameModifiers(homeCourt, scoreDif, talkType);
        }
    }

    private boolean callTimeout(){
        if(playerWantsTO){
            if(homeTeam.isPlayerControlled()){
                if(canCallTimeout(homeTeam)) {
                    homeTimeouts--;
                    playerWantsTO = false;
                    return true;
                }
            }
            else{
                if(canCallTimeout(awayTeam)) {
                    awayTimeouts--;
                    playerWantsTO = false;
                    return true;
                }
            }
        }
        else{
            if(homeTeam.isPlayerControlled()){
                if(awayTeam.getTimeout(awayScore - homeScore)){
                    if(canCallTimeout(awayTeam)){
                        awayTimeouts--;
                        return true;
                    }
                }
            }
            else{
                if(homeTeam.getTimeout(homeScore - awayScore)){
                    if(canCallTimeout(homeTeam)){
                        homeTimeouts--;
                        return true;
                    }
                }
            }
        }
        return callMediaTimeout();
    }

    private boolean callMediaTimeout(){
        int min = timeRemaining / 60;
        if(deadBall && !madeShot) {
            if (half == 1) {
                if (!mediaTimeouts[0] && min < 16 && min >= 12){
                    mediaTimeouts[0] = true;
                    return true;
                }
                else if(!mediaTimeouts[1] && min < 12 && min >= 8){
                    mediaTimeouts[1] = true;
                    return true;
                }
                else if(!mediaTimeouts[2] && min < 8 && min >= 4){
                    mediaTimeouts[2] = true;
                    return true;
                }
                else if(!mediaTimeouts[3] && min < 4){
                    mediaTimeouts[3] = true;
                    return true;
                }
            }
            else if(half == 2){
                if (!mediaTimeouts[4] && min < 16 && min >= 12){
                    mediaTimeouts[4] = true;
                    return true;
                }
                else if(!mediaTimeouts[5] && min < 12 && min >= 8){
                    mediaTimeouts[5] = true;
                    return true;
                }
                else if(!mediaTimeouts[6] && min < 8 && min >= 4){
                    mediaTimeouts[6] = true;
                    return true;
                }
                else if(!mediaTimeouts[7] && min < 4){
                    mediaTimeouts[7] = true;
                    return true;
                }
            }
        }

        if(!mediaTimeouts[8] && min == 20 && half == 1){
            mediaTimeouts[8] = true;
            return true;
        }
        if(!mediaTimeouts[9] && (min == 20 && half == 2) || (min == 5 && half > 2)){
            mediaTimeouts[9] = true;
            return true;
        }

        return false;
    }

    private boolean canCallTimeout(Team team){
        if(team.equals(homeTeam) && homeTeamHasBall && homeTimeouts > 0){
            return true;
        }

        if(team.equals(awayTeam) && !homeTeamHasBall && awayTimeouts > 0){
            return true;
        }
        return false;
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

            homeFouls = 0;
            awayFouls = 0;

            if(homeTimeouts == 4){
                homeTimeouts = 3;
            }
            if(awayTimeouts == 4){
                awayTimeouts = 3;
            }

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

            homeTimeouts++;
            awayTimeouts++;
            mediaTimeouts[9] = false;
            return true;
        }

        isPlayed = true;
        return false;
    }

    public int simPlay() {
        /*
        returns 0 if there is time remaining
                -1 if there is no time remaining
                1 if there is a timeout called
         */
        if(callTimeout() && (!recentTO || playerWantsTO)){
            if(plays.size() != 1 && savePlays) {
                plays.add(0, "Timeout called!");
            }

            deadBall = true;
            madeShot = true; // this is to prevent media TOs from being called immediately after player TOs
            recentTO = true;
            return 1;
        }

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
            deadBall = true;
            madeShot = false;
            changePossession();
        }

        if(!currentPlay.equals("") && savePlays) {
            if (plays.get(0).length() > 15) {
                if (!currentPlay.contains(plays.get(0).substring(15))) {
                    plays.add(0, getFormattedTime() + " (" + shotClock + ") - " + currentPlay);
                }
            }
            else{
                plays.add(0, getFormattedTime() + " (" + shotClock + ") - " + currentPlay);
            }
        }
        if (timeRemaining > 0) {
            return 0;
        } else {
            timeRemaining = 0;
            return -1;
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
            currentPlay += passer.getFullName() + " passes the ball to " + target.getFullName() + ".";
            lastPlayerWithBall = playerWithBall;
            playerWithBall = target.getPosition();

            if(getFoul(1)){
                int timeChange = (int) (4 - (offense.getPace() / 90.0) * r.nextInt(3));
                timeRemaining -= timeChange;
                shotClock -= timeChange;
                currentPlay += foulString;
                return shootBonus();
            }

            if ((passSuccess > 50 && location > -1)) {
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

            changePossession();
        }
        int timeChange = (int) (4 - (offense.getPace() / 90.0) * r.nextInt(3));
        timeRemaining -= timeChange;
        shotClock -= timeChange;

        if(currentPlay.length() == 0) {
            currentPlay += passer.getFullName() + " is dribbling with the ball";
            if(getFoul(0)){
                currentPlay += foulString;
                return shootBonus();
            }
            else if(getFoul(-1)){
                currentPlay += foulString;
            }
            else if(getFoul(-2)){
                currentPlay += foulString;
            }
        }
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

        boolean isFouled = getFoul(2);
        if(isFouled){
            currentPlay += "is fouled ";
            shotSuccess -= 30;
        }

        if (shotSuccess > 0 && r.nextBoolean()) {
            // second parameter is a totally baseless assertion for balance
            int timeChange = (int) (r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(3));
            timeRemaining -= timeChange;
            shotClock -= timeChange;
            deadBall = true;

            if(isFouled){
                currentPlay += "and makes the shot anyways!" + foulString;
                madeShot = false; // this is so media timeouts can be called on and-1s
            }
            else {
                currentPlay += "makes it!";
                madeShot = true;
            }

            if(assisted){
                offense.getPlayers().get(lastPlayerWithBall-1).addAssist();
            }

            changePossession();
            if (shotLocation == 1 || shotLocation == 2) {
                shooter.addTwoPointShot(true);
                if(isFouled){
                    return 2 + shootFreeThrows(shooter, 1);
                }
                return 2;
            } else {
                shooter.addThreePointShot(true);
                if(isFouled){
                    return 3 + shootFreeThrows(shooter, 1);
                }
                return 3;
            }
        }

        int timeChange = (int) (r.nextInt(6) - (offense.getPace() / 90.0) * r.nextInt(4));
        timeRemaining -= timeChange;
        shotClock -= timeChange;

        if(isFouled){
            currentPlay += "and misses the shot." + foulString;
        }
        else {
            currentPlay += "misses it!";
        }
        if(shotLocation == 1 || shotLocation == 2){
            if(isFouled){
                return shootFreeThrows(shooter, 2);
            }
            shooter.addTwoPointShot(false);
        }
        else{
            if(isFouled){
                return shootFreeThrows(shooter, 3);
            }
            shooter.addThreePointShot(false);
        }

        getRebound(offense, defense);
        return 0;
    }

    private int shootBonus(){
        if(homeTeamHasBall){
            if(awayFouls >= 7){
                if(awayScore >= 10){
                    return shootFreeThrows(homeTeam.getPlayers().get(playerWithBall - 1), 2);
                }
                else{
                    return shootFreeThrows(homeTeam.getPlayers().get(playerWithBall - 1), -1);
                }
            }
        }
        else{
            if(homeFouls >= 7){
                if(homeFouls >= 10){
                    return shootFreeThrows(awayTeam.getPlayers().get(playerWithBall - 1), 2);
                }
                else{
                    return shootFreeThrows(awayTeam.getPlayers().get(playerWithBall - 1), -1);
                }
            }
        }
        return 0;
    }

    private int shootFreeThrows(Player player, int attempts){
        int made = 0;
        boolean madeLast = false;
        if(attempts > 0) {
            for (int x = 0; x < attempts; x++) {
                if (player.getFreeThrowShot() > r.nextInt(100)) {
                    made++;
                    player.addFreeThrowShot(true);
                    if (x == attempts - 1) {
                        madeLast = true;
                    }
                } else {
                    player.addFreeThrowShot(false);
                    if (x == attempts - 1) {
                        madeLast = false;
                    }
                }
            }
        }
        else{
            // 1 and 1
            if(player.getFreeThrowShot() > r.nextInt(100)){
                made++;
                player.addFreeThrowShot(true);
                if(player.getFreeThrowShot() > r.nextInt(100)){
                    made++;
                    player.addFreeThrowShot(true);
                    madeLast = true;
                }
                else{
                    player.addFreeThrowShot(false);
                    madeLast = false;
                }
            }
            else{
                player.addFreeThrowShot(false);
                madeLast = false;
            }
        }
        if(made == 0){
            if(attempts == 1){
                currentPlay += "\n" + player.getFullName() + " misses his free throw.";
            }
            else if(attempts > 1){
                currentPlay += "\n" + player.getFullName() + " misses all of his free throws!";
            }
            else{
                currentPlay += "\n" + player.getFullName() + " misses the front end of the 1 and 1!";
            }
        }
        else{
            currentPlay += "\n" + player.getFullName() + " makes " + made + " of " + attempts + " free throws.";
        }
        if(!madeLast){
            if(homeTeamHasBall) {
                getRebound(homeTeam, awayTeam);
            }
            else{
                getRebound(awayTeam, homeTeam);
            }
        }
        return made;
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
            playerWithBall = offHigh + 1;
            currentPlay += "\n" + offense.getPlayers().get(playerWithBall-1).getFullName() + " grabs the offensive rebound.";
            offense.getPlayers().get(playerWithBall-1).addRebound(true);
            if(timeRemaining >= 30) {
                shotClock = 30;
            }
            else{
                shotClock = timeRemaining;
            }
        } else {
            playerWithBall = defHigh + 1;
            currentPlay += "\n" + defense.getPlayers().get(playerWithBall-1).getFullName() + " grabs the defensive rebound.";
            defense.getPlayers().get(playerWithBall-1).addRebound(false);
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

    private boolean isFoul(Player player, int situation){
        /*
        situation: 0 = normal defending, 1 = on a pass, 2 = on a shot
        situation: -1 = charging situation, -2 = off-ball situation
         */


        // TODO: make the player's ability affect the probability of the foul
        int foulFactor = 100; //higher number == fewer fouls
        if(situation == 0 && r.nextInt(foulFactor) < 2){
            player.addFoul();
            foulString = "\n" + player.getFullName() + " has been called for a defensive foul on the floor.";
            return true;
        }
        else if(situation == 1 && r.nextInt(foulFactor) < 5) {
            player.addFoul();
            foulString = "\n" + player.getFullName() + " has been called for a defensive foul defending the pass.";
            return true;
        }
        else if(situation == 2 && r.nextInt(foulFactor) < 20){
            foulString = "\n" + player.getFullName() + " has been called for a shooting foul.";
            player.addFoul();
            return true;
        }
        else if(situation == -1 && r.nextInt(foulFactor) < 10){
            player.addFoul();
            foulString = "\n" + player.getFullName() + " has been called for a charge.";
            changePossession();
            return true;
        }
        else if(situation == -2 && r.nextInt(foulFactor) < 5){
            player.addFoul();
            foulString = "\n" + player.getFullName() + " has been called for an off-ball foul.";
            changePossession();
            return true;
        }


        return false;
    }

    private int[] getFoulChance(Team team, int situation){
        /*
        situation: 0 = normal defending, 1 = on a pass, 2 = on a shot
        situation: -1 = charging situation, -2 = off-ball situation
        */

        int[] values = new int[5];

        for (int x = 0; x < 5; x++) {
            if(situation == 0 || situation == 1) {
                if(x != playerWithBall - 1) {
                    values[x] = r.nextInt(100) / team.getPlayers().get(x).getOffBallDefense();
                }
                else{
                    values[x] = r.nextInt(100) /  team.getPlayers().get(x).getOnBallDefense();
                }
            }
            else if(situation == 2){
                if(x == playerWithBall - 1){
                    values[x] = r.nextInt(100) / team.getPlayers().get(x).getOnBallDefense();
                }
                else{
                    values[x] = -100;
                }
            }
            else if(situation == -1){
                if(x == playerWithBall - 1){
                    values[x] = r.nextInt(100) / team.getPlayers().get(x).getBallHandling();
                }
                else{
                    values[x] = -100;
                }
            }
            else if(situation == -2){
                if(x != playerWithBall - 1){
                    values[x] = r.nextInt(100) / team.getPlayers().get(x).getOffBallMovement();
                }
                else{
                    values[x] = -100;
                }
            }
        }
        return values;
    }

    private boolean getFoul(int situation){
        /*
        situation: 0 = normal defending, 1 = on a pass, 2 = on a shot, 3 = on a rebound
        situation: -1 = charging situation, -2 = off-ball situation
        */
        int[] chance;
        boolean homeFoul;
        if(situation >= 0){
            if(homeTeamHasBall){
                chance = getFoulChance(awayTeam, situation);
                homeFoul = false;
            }
            else{
                chance = getFoulChance(homeTeam, situation);
                homeFoul = true;
            }
        }
        else{
            if(homeTeamHasBall){
                chance = getFoulChance(homeTeam, situation);
                homeFoul = true;
            }
            else{
                chance = getFoulChance(awayTeam, situation);
                homeFoul = false;
            }
        }

        int playerIndex = 0;
        for(int x = 1; x < 5; x++){
            if (chance[x] > chance[playerIndex]) {
                playerIndex = x;
            }
        }

        if(homeFoul){
            if(isFoul(homeTeam.getPlayers().get(playerIndex), situation)){
                deadBall = true;
                madeShot = false;
                homeFouls++;
                if(shotClock < 20 && situation != 2){
                    shotClock = 20;
                }
                else{
                    shotClock = 30;
                }
                return true;
            }
        }
        else{
            if(isFoul(awayTeam.getPlayers().get(playerIndex), situation)){
                deadBall = true;
                madeShot = false;
                awayFouls++;
                if(shotClock < 20 && situation != 2){
                    shotClock = 20;
                }
                else{
                    shotClock = 30;
                }
                return  true;
            }
        }
        return false;
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
        recentTO = false;
    }

    public void setPlayerWantsTO(boolean to){
        playerWantsTO = to;
    }

    public int getHomeTimeouts(){
        return homeTimeouts;
    }

    public int getAwayTimeouts(){
        return awayTimeouts;
    }

    public int getHomeFouls(){
        return homeFouls;
    }

    public int getAwayFouls(){
        return awayFouls;
    }

    public boolean getPlayerWantsTO(){
        return playerWantsTO;
    }
}

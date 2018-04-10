package com.coaching.jphil.collegebasketballcoach.basketballSim;

import com.coaching.jphil.collegebasketballcoach.Database.GameDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameEventDB;
import com.coaching.jphil.collegebasketballcoach.Database.GameStatsDB;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Game {

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

        this.homeTeam.addOpponent(awayTeam);
        this.awayTeam.addOpponent(homeTeam);
    }

    public Game(Team homeTeam, Team awayTeam, boolean isNeutralCourt) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;

        this.isNeutralCourt = isNeutralCourt;
        isPlayed = false;

        this.homeTeam.addOpponent(awayTeam);
        this.awayTeam.addOpponent(homeTeam);

        this.homeTeam.addGameToSchedule(this);
        this.awayTeam.addGameToSchedule(this);
    }

    public Game(Team homeTeam, Team awayTeam, int id, int homeScore, int awayScore, boolean isInProgress, boolean isPlayed, boolean isNeutralCourt) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.id = id;

        this.homeScore = homeScore;
        this.awayScore = awayScore;

        this.isPlayed = isPlayed;
        this.isNeutralCourt = isNeutralCourt;
        this.isInProgress = isInProgress;

        this.homeTeam.addOpponent(awayTeam);
        this.awayTeam.addOpponent(homeTeam);

        this.homeTeam.addGameToSchedule(this);
        this.awayTeam.addGameToSchedule(this);
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

    public ArrayList<GameEvent> getPlays(){
        return plays;
    }

    public ArrayList<GameStatsDB> simulateGame() {
        savePlays = false;
        debug = true;

        preGameSetUp();
        do {
            int lastPlay = simPlay();
            while (lastPlay != -1) {
                if(lastPlay == 1){
                    coachTalk(homeTeam, !isNeutralCourt, awayScore-homeScore, homeTeam.getCoachTalk(awayScore - homeScore));
                    coachTalk(awayTeam, false, homeScore-awayScore, awayTeam.getCoachTalk(homeScore - awayScore));
                }
                lastPlay = simPlay();
            }
        } while (startNextHalf());
        isPlayed = true;

        ArrayList<GameStatsDB> stats = new ArrayList<>();
        stats.addAll(homeTeam.playGame(homeTeamWin()));
        stats.addAll(awayTeam.playGame(!homeTeamWin()));

        for(GameStatsDB db: stats){
            db.gameId = id;
        }

        boolean bigWin, badLoss;

        if(getHomeTeam().isPlayerControlled()){
            bigWin = (getHomeTeam().getOverallRating() + 10 <= getAwayTeam().getOverallRating()) && homeTeamWin();
            badLoss = (getHomeTeam().getOverallRating() - 10 >= getAwayTeam().getOverallRating()) && !homeTeamWin();
            for(Coach c: getHomeTeam().getCoaches()){
                c.recruitRecruits(bigWin, badLoss, getHomeTeam().getNumberOfReturningPlayers());

            }
        }
        else if(awayTeam.isPlayerControlled()){
            bigWin = (getAwayTeam().getOverallRating() + 10 <= getHomeTeam().getOverallRating()) && !homeTeamWin();
            badLoss = (getAwayTeam().getOverallRating() - 10 >= getHomeTeam().getOverallRating()) && homeTeamWin();
            for(Coach c: getAwayTeam().getCoaches()){
                c.recruitRecruits(bigWin, badLoss, getAwayTeam().getNumberOfReturningPlayers());
            }
        }
        return stats;
    }

    private ArrayList<GameEvent> plays;
    private int half, timeRemaining, playerWithBall, location, shotClock;
    private int lastPlayerWithBall, lastShotClock, lastTimeRemaining, playType;
    private boolean madeShot, deadBall, homeTeamHasBall, homeTeamHasPosArrow, debug = false;
    private boolean[] mediaTimeouts;
    private boolean playerWantsTO, recentTO, playerIntentFoul;
    private int homeTimeouts, awayTimeouts, homeFouls, awayFouls;
    private boolean savePlays = false;
    private boolean playerFouledOut = false;
    private boolean alertedDeadBall = true;
    private boolean shootFreeThrows = false;
    private boolean isInProgress = false;
    private int freeThrows;
    private Player freeThrowShooter;
    private Random r;

    private String currentPlay, foulString;

    // TODO: prevent a player from being subbed if he is shooting free throws

    public void setSavePlays(boolean savePlays){
        this.savePlays = savePlays;
    }

    public void preGameSetUp() {
        plays = new ArrayList<>();
        r = new Random();
        half = 1;
        timeRemaining = 20 * 60;
        lastTimeRemaining = timeRemaining;
        shotClock = 30;
        lastShotClock = 0;
        homeScore = 0;
        awayScore = 0;
        homeFouls = 0;
        awayFouls = 0;
        playerWithBall = 1;
        lastPlayerWithBall = 1;
        location = -1;
        playType = 0;

        mediaTimeouts = new boolean[]{false, false, false, false, false, false, false, false, false, false};
        playerWantsTO = false;
        recentTO = false;
        isInProgress = true;
        homeTimeouts = 4;
        awayTimeouts = 4;

        // very simple jump ball
        homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() + r.nextInt(100) > awayTeam.getPlayers().get(4).getRebounding() + r.nextInt(100);
        homeTeamHasPosArrow = !homeTeamHasBall;
        madeShot = false;
        deadBall = false;


        if(homeTeamHasBall) {
            plays.add(new GameEvent(getFormattedTime() + " (30) - " + homeTeam.getFullName() + " won the tip off!", 0, homeTeamHasBall));
        }
        else{
            plays.add(new GameEvent(getFormattedTime() + " (30) - " + awayTeam.getFullName() + " won the tip off!", 0, homeTeamHasBall));
        }

        homeTeam.preGameSetup();
        awayTeam.preGameSetup();
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
        if(callMediaTimeout()){
            return true;
        }

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
        return false;
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
        if(team.equals(homeTeam) && (homeTeamHasBall || deadBall) && homeTimeouts > 0){
            return true;
        }

        if(team.equals(awayTeam) && (!homeTeamHasBall || deadBall) && awayTimeouts > 0){
            return true;
        }

        return false;
    }

    public boolean startNextHalf() {
        if(timeRemaining <= 0) {
            if (half == 1) {
                // setup 2nd half
                half++;
                timeRemaining = 20 * 60;
                lastTimeRemaining = timeRemaining;
                shotClock = 30;
                homeTeamHasBall = homeTeamHasPosArrow;
                homeTeamHasPosArrow = !homeTeamHasPosArrow;
                deadBall = true;
                madeShot = false;

                homeFouls = 0;
                awayFouls = 0;

                playType = 0;

                if (homeTimeouts == 4) {
                    homeTimeouts = 3;
                }
                if (awayTimeouts == 4) {
                    awayTimeouts = 3;
                }

                for (Player p : homeTeam.getPlayers()) {
                    p.addTimePlayed(0, 10);
                }
                for (Player p : awayTeam.getPlayers()) {
                    p.addTimePlayed(0, 10);
                }

                return true;
            } else if (homeScore == awayScore) {
                // setup overtime
                half++;
                timeRemaining = 5 * 60;
                lastTimeRemaining = timeRemaining;
                shotClock = 30;
                homeTeamHasBall = homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding();
                homeTeamHasPosArrow = !(homeTeam.getPlayers().get(4).getRebounding() > awayTeam.getPlayers().get(4).getRebounding());
                madeShot = false;
                deadBall = false;
                playType = 0;

                homeTimeouts++;
                awayTimeouts++;
                mediaTimeouts[9] = false;
                return true;
            }

            if (savePlays) {
                plays.add(0, new GameEvent("Game Over!", 0, false));
            }
            isPlayed = true;
            isInProgress = false;
        }
        return false;
    }

    public int simPlay() {
        /*
        returns 0 if there is time remaining
                -1 if there is no time remaining
                1 if there is a timeout called
         */
        if(playerFouledOut){
            if(homeTeam.isPlayerControlled() && !debug){
                awayTeam.aiMakeSubs(half, timeRemaining);
                if(playerFouledOut(homeTeam)){
                    return 3;
                }
                else{
                    playerFouledOut = false;
                }
            }
            else if(awayTeam.isPlayerControlled() && !debug){
                homeTeam.aiMakeSubs(half, timeRemaining);
                if(playerFouledOut(awayTeam)){
                    return 3;
                }
                else{
                    playerFouledOut = false;
                }
            }
            else{
                homeTeam.aiMakeSubs(half, timeRemaining);
                awayTeam.aiMakeSubs(half, timeRemaining);
                playerFouledOut = false;
            }
            homeTeam.makeSubs();
            awayTeam.makeSubs();
        }

        playType = 0;
        currentPlay = "";
        if(callTimeout() && (!recentTO || playerWantsTO)){
            if(plays.size() != 1 && savePlays) {
                plays.add(0, new GameEvent("Timeout called!", 0, false));
            }

            if(timeRemaining == 20 * 60 && half == 1){
                deadBall = false;
                madeShot = false;
            }
            else {
                deadBall = true;
                madeShot = false;
            }
            playType = -1;

            if(homeTeam.isPlayerControlled() && !debug){
                awayTeam.aiMakeSubs(half, timeRemaining);
            }
            else if(awayTeam.isPlayerControlled() && !debug){
                homeTeam.aiMakeSubs(half, timeRemaining);
            }
            else{
                homeTeam.aiMakeSubs(half, timeRemaining);
                awayTeam.aiMakeSubs(half, timeRemaining);
            }
            homeTeam.makeSubs();
            awayTeam.makeSubs();

            alertedDeadBall = true;

            recentTO = true;
            return 1;
        }

        if(playerIntentFoul && !shootFreeThrows){
            if(homeTeamHasBall && !homeTeam.isPlayerControlled()){
                intentionallyFoul(homeTeam, awayTeam);
                plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 3, true));
            }
            else if(!homeTeamHasBall && !awayTeam.isPlayerControlled()){
                intentionallyFoul(awayTeam, homeTeam);
                plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 3, false));
            }
        }

        if (deadBall) {
            if(!alertedDeadBall){
                alertedDeadBall = true;
                return 2;
            }
            if(!madeShot) {
                if(homeTeam.isPlayerControlled() && !debug){
                    awayTeam.aiMakeSubs(half, timeRemaining);
                }
                else if(awayTeam.isPlayerControlled() && !debug){
                    homeTeam.aiMakeSubs(half, timeRemaining);
                }
                else{
                    homeTeam.aiMakeSubs(half, timeRemaining);
                    awayTeam.aiMakeSubs(half, timeRemaining);
                }
                homeTeam.makeSubs();
                awayTeam.makeSubs();
            }

            if(shootFreeThrows){
                if(homeTeamHasBall) {
                    homeScore += shootFreeThrows(freeThrowShooter, freeThrows);
                }
                else{
                    awayScore += shootFreeThrows(freeThrowShooter, freeThrows);
                }
                shootFreeThrows = false;
            }
            else if (homeTeamHasBall) {
                homeScore += getPass(homeTeam, awayTeam);
            }
            else {
                awayScore += getPass(awayTeam, homeTeam);
            }
        } else {
            int shotUrgency;
            if(homeTeamHasBall){
                shotUrgency = (20 * 60) / homeTeam.getPace();
            }
            else{
                shotUrgency = (20 * 60) / awayTeam.getPace();
            }
            if (shotClock < shotUrgency && r.nextDouble() > .5 && location == 1) {
                if (homeTeamHasBall) {
                    homeScore += getShot(homeTeam, awayTeam, false);
                } else {
                    awayScore += getShot(awayTeam, homeTeam, false);
                }
            }
            else if(shotClock <= 5 && r.nextDouble() > .05){
                if (homeTeamHasBall) {
                    homeScore += getShot(homeTeam, awayTeam, false);
                } else {
                    awayScore += getShot(awayTeam, homeTeam, false);
                }
            }
            else {
                if (homeTeamHasBall) {
                    homeScore += getPass(homeTeam, awayTeam);
                } else {
                    awayScore += getPass(awayTeam, homeTeam);
                }
            }
        }

        if(shotClock <= 0 && timeRemaining > 0){
            plays.get(0).appendString("\nTurnover! Shot clock violation!");
            deadBall = true;
            alertedDeadBall = false;
            madeShot = false;
            if(homeTeamHasBall){
                homeTeam.getPlayers().get(playerWithBall - 1).addTurnover();
                homeTeam.addTurnover();
            }
            else{
                awayTeam.getPlayers().get(playerWithBall - 1).addTurnover();
                awayTeam.addTurnover();
            }

            changePossession();
        }

        if (timeRemaining > 0) {
            int count = 0;
            for(Player p: homeTeam.getPlayers()){
                if(count < 5) {
                    p.addTimePlayed(lastTimeRemaining - timeRemaining, playType);
                }
                else{
                    p.addTimePlayed(0, -1);
                }
                count++;
            }
            count = 0;
            for(Player p: awayTeam.getPlayers()){
                if(count < 5) {
                    p.addTimePlayed(lastTimeRemaining - timeRemaining, playType);
                }
                else{
                    p.addTimePlayed(0, -1);
                }
                count++;
            }

            lastTimeRemaining = timeRemaining;
            return 0;
        } else {
            int count = 0;
            for(Player p: homeTeam.getPlayers()){
                if(count < 5) {
                    p.addTimePlayed(lastTimeRemaining, playType);
                }
                else{
                    p.addTimePlayed(0, -1);
                }
                count++;
            }
            count = 0;
            for(Player p: awayTeam.getPlayers()){
                if(count < 5) {
                    p.addTimePlayed(lastTimeRemaining, playType);
                }
                else{
                    p.addTimePlayed(0, -1);
                }
                count++;
            }
            timeRemaining = 0;
            return -1;
        }
    }

    private int getPass(Team offense, Team defense) {
        Player passer, target;
        Player passDef, targetDef;

        if (deadBall) {
            getInbounder(offense);
        }

        passer = offense.getPlayers().get(playerWithBall - 1);
        passDef = defense.getPlayers().get(passer.getCurrentPosition() - 1);

        target = getTarget(offense);
        targetDef = defense.getPlayers().get(target.getCurrentPosition() - 1);

        int passSuccess = (passer.getPassing() + target.getOffBallMovement()) / (r.nextInt(randomBoundValue) + 1);
        int stealSuccess = (passDef.getOnBallDefense() + targetDef.getOffBallDefense()) / (r.nextInt(randomBoundValue) + 1);

        if (passSuccess >= (defense.getAggression() + (passDef.getAggressiveness() + targetDef.getAggressiveness()) / 15)) {
            // successful pass
            deadBall = false;
            madeShot = false;

            currentPlay += passer.getFullName() + " passes the ball to " + target.getFullName();
            int ob = 0;
            if(location != -1){
                currentPlay += ".";
                ob = getBallOutOfBounds(target, targetDef);
            }

            if(ob == 0) {
                boolean startInBackcourt = false;
                if (location < 1) {
                    startInBackcourt = true;
                    location = 1;

                    currentPlay += " and he brings the ball into the front court.";
                    smartTimeChange((int) (9 - (offense.getPace() / 90.0) * r.nextInt(6)));
                }

                lastPlayerWithBall = playerWithBall;
                playerWithBall = target.getCurrentPosition();

                if(!startInBackcourt) {
                    if (getFoul(1)) {
                        currentPlay += foulString;
                        shootBonus();
                        if (!currentPlay.equals("") && savePlays) {
                            plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 3, homeTeamHasBall));
                        }
                        return 0;
                    }

                    if ((passSuccess > (20 + defense.getAggression() / 2))) {
                        // pass leading to a shot
                        smartTimeChange((int) (8 - (offense.getPace() / 90.0) * r.nextInt(4)));
                        return getShot(offense, defense, true);
                    }

                    int post = getPostMove(target, targetDef);
                    if (post >= 0) {
                        // pass to the post with a post move
                        return post;
                    }
                    smartTimeChange((int) (8 - (offense.getPace() / 90.0) * r.nextInt(4)));
                }


                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 0, homeTeamHasBall));
                }
                return 0;
            }
            else {
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;

                smartTimeChange((int) (4 - (offense.getPace() / 90.0) * r.nextInt(3)));
                if(ob == -1){
                    passer.addTurnover();
                    offense.addTurnover();
                    if(!currentPlay.equals("") && savePlays) {
                        plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
                    }
                    changePossession();
                }
                else{
                    if(!currentPlay.equals("") && savePlays) {
                        plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
                    }
                }
                return 0;
            }
        }
        else if(passSuccess < ((defense.getAggression() + (passDef.getAggressiveness() + targetDef.getAggressiveness()) / 15)) - 6){
            if(r.nextInt(100) > 60){
                currentPlay += target.getFullName() + " cannot control the pass and has lost the ball out of bounds!";
                target.addTurnover();
                offense.addTurnover();
                deadBall = true;
                madeShot = false;
            }
            else{
                currentPlay += passer.getFullName() + " has thrown the ball away!";
                passer.addTurnover();
                offense.addTurnover();
                deadBall = true;
                madeShot = false;
            }

            smartTimeChange((int) (4 - (offense.getPace() / 90.0) * r.nextInt(3)));
            if (savePlays) {
                plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
            }
            changePossession();

            return 0;
        }
        else if(deadBall){
            if(r.nextInt(15) == 4 && !madeShot){
                currentPlay += passer.getFullName() + " cannot find the open man and is called for a 5 second violation!";
                passer.addTurnover();
                offense.addTurnover();
                if(savePlays){
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
                }
                changePossession();
            }
            else{
                currentPlay = "";
            }
            return 0;
        }

        if (stealSuccess > (100 - defense.getAggression() - ((passDef.getAggressiveness() + targetDef.getAggressiveness()) / 2))) {
            // unsuccessful pass -> turnover
            currentPlay += passer.getFullName() + " turns the ball over! ";
            passer.addTurnover();
            offense.addTurnover();
            if (r.nextBoolean()) {
                // ball is stolen by off ball defender
                currentPlay += targetDef.getFullName() + " has stolen the ball!";
                playerWithBall = target.getCurrentPosition();
                targetDef.addSteal();
                defense.addSteal();
            }
            else{
                currentPlay += passDef.getFullName() + " has stolen the ball!";
                passDef.addSteal();
                defense.addSteal();
            }

            smartTimeChange((int) (4 - (offense.getPace() / 90.0) * r.nextInt(3)));
            if(!currentPlay.equals("") && savePlays) {
                plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
            }
            changePossession();
            return 0;
        }

        smartTimeChange((int) (4 - (offense.getPace() / 90.0) * r.nextInt(3)));
        if(currentPlay.length() == 0) {
            if(location == 1) {
                currentPlay += passer.getFullName() + " is dribbling with the ball.";
            }
            else{
                location = 1;
                currentPlay += passer.getFullName() + " brings the ball into the front court.";
            }

            int ob = getBallOutOfBounds(passer, passDef);
            if(ob == -1){
                deadBall = true;
                alertedDeadBall = false;
                madeShot = false;
                passer.addTurnover();
                offense.addTurnover();
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
                }
                changePossession();
                return 0;
            }
            else if(ob == 1){
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, -1, homeTeamHasBall));
                }
                return 0;
            }
            else if(getFoul(0)){
                currentPlay += foulString;
                shootBonus();
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 3, homeTeamHasBall));
                }
                return 0;
            }
            else if(getFoul(-1)){
                currentPlay += foulString;
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 3, !homeTeamHasBall));
                }
                return 0;
            }
            else if(getFoul(-2)){
                currentPlay += foulString;
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 3, !homeTeamHasBall));
                }
                return 0;
            }
        }

        if(!currentPlay.equals("") && savePlays) {
            plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, -1, homeTeamHasBall));
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
        int shotLocation;
        Player shooter = offense.getPlayers().get(playerWithBall - 1);

        int shotClose = (int) (shooter.getCloseRangeShot() * (1 - (offense.getOffenseFavorsThrees() / 100.0)) *
                (1 - (defense.getDefenseFavorsThrees() / 100.0)) + r.nextInt(randomBoundValue));

        int shotMid = (int) (shooter.getMidRangeShot() * .2 + r.nextInt(randomBoundValue));

        int shotLong = (int) (shooter.getLongRangeShot() * (offense.getOffenseFavorsThrees() / 100.0) *
                (defense.getDefenseFavorsThrees() / 100.0) + r.nextInt(randomBoundValue));

        if(currentPlay.length() != 0){
            currentPlay += "\n";
        }
        if (shotClose > shotMid && shotClose > shotLong) {
            currentPlay += shooter.getFullName() + " shoots from up close";
            shotLocation = 1;
        } else if (shotMid > shotClose && shotMid > shotLong) {
            currentPlay += shooter.getFullName() + " shoots from mid-range";
            shotLocation = 2;
        } else {
            currentPlay += shooter.getFullName() + " shoots from 3";
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
                            defense.getPlayers().get(playerWithBall - 1).getPostDefense() +
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
            shotSuccess += 30;
        }

        boolean isFouled = getFoul(2);
        if(isFouled){
            currentPlay += ", is fouled";
            shotSuccess -= 30;
        }

        if (shotSuccess > defense.getAggression() && r.nextBoolean()) {
            // second parameter is a totally baseless assertion for balance
            smartTimeChange((int) (6 - (offense.getPace() / 90.0) * r.nextInt(4)));
            deadBall = true;

            if(isFouled){
                currentPlay += " and makes the shot anyways!" + foulString;
                madeShot = false; // this is so media timeouts can be called on and-1s
                alertedDeadBall = false;
            }
            else {
                currentPlay += " and makes it!";
                madeShot = true;
            }

            if(assisted){
                offense.getPlayers().get(lastPlayerWithBall-1).addAssist();
                offense.addAssist();
            }


            if (shotLocation == 1 || shotLocation == 2) {
                shooter.addTwoPointShot(true);
                offense.addTwoPointShot(true);
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
                }
                if(isFouled){
                    freeThrowShooter = shooter;
                    freeThrows = 1;
                    shootFreeThrows = true;
                }
                else{

                    changePossession();
                }

                return 2;
            } else {
                shooter.addThreePointShot(true);
                offense.addThreePointShot(true);
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 2, homeTeamHasBall));
                }
                if(isFouled){
                    freeThrowShooter = shooter;
                    freeThrows = 1;
                    shootFreeThrows = true;
                }
                else{
                    changePossession();
                }

                return 3;
            }
        }

        if(isFouled){
            currentPlay += ", but misses the shot." + foulString;
        }
        else {
            smartTimeChange((int) (6 - (offense.getPace() / 90.0) * r.nextInt(4)));
            currentPlay += ", but misses it!";
        }
        if(shotLocation == 1 || shotLocation == 2){
            if(isFouled){
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;
                freeThrowShooter = shooter;
                freeThrows = 2;
                shootFreeThrows = true;
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 3, homeTeamHasBall));
                }
                return 0;
            }
            shooter.addTwoPointShot(false);
            offense.addTwoPointShot(false);
        }
        else{
            if(isFouled){
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;
                freeThrowShooter = shooter;
                freeThrows = 3;
                shootFreeThrows = true;
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 3, homeTeamHasBall));
                }
                return 0;
            }
            shooter.addThreePointShot(false);
            offense.addThreePointShot(false);
        }

        getRebound(offense, defense);
        return 0;
    }

    private void shootBonus(){
        if(homeTeamHasBall){
            if(awayFouls >= 7){
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;
                freeThrowShooter = homeTeam.getPlayers().get(playerWithBall - 1);
                shootFreeThrows = true;

                if(awayScore >= 10){
                    freeThrows = 2;
                }
                else{
                    freeThrows = -1;
                }
            }
        }
        else{
            if(homeFouls >= 7){
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;
                freeThrowShooter = awayTeam.getPlayers().get(playerWithBall - 1);
                shootFreeThrows = true;
                if(homeFouls >= 10){
                    freeThrows = 2;
                }
                else{
                    freeThrows = -1;
                }
            }
        }
    }

    private int shootFreeThrows(Player player, int attempts){
        int made = 0;
        location = 1;
        boolean madeLast = false;
        if(attempts > 0) {
            for (int x = 0; x < attempts; x++) {
                if (player.getFreeThrowShot() > r.nextInt(100)) {
                    made++;
                    player.addFreeThrowShot(true);
                    if(homeTeamHasBall){
                        homeTeam.addFreeThrowShot(true);
                    }
                    else{
                        awayTeam.addFreeThrowShot(true);
                    }

                    if (x == attempts - 1) {
                        madeLast = true;
                    }
                } else {
                    player.addFreeThrowShot(false);
                    if(homeTeamHasBall){
                        homeTeam.addFreeThrowShot(false);
                    }
                    else{
                        awayTeam.addFreeThrowShot(false);
                    }


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
                if(homeTeamHasBall){
                    homeTeam.addFreeThrowShot(true);
                }
                else{
                    awayTeam.addFreeThrowShot(true);
                }

                if(player.getFreeThrowShot() > r.nextInt(100)){
                    made++;
                    player.addFreeThrowShot(true);
                    if(homeTeamHasBall){
                        homeTeam.addFreeThrowShot(true);
                    }
                    else{
                        awayTeam.addFreeThrowShot(true);
                    }


                    madeLast = true;
                }
                else{
                    player.addFreeThrowShot(false);
                    if(homeTeamHasBall){
                        homeTeam.addFreeThrowShot(false);
                    }
                    else{
                        awayTeam.addFreeThrowShot(false);
                    }

                    madeLast = false;
                }
            }
            else{
                player.addFreeThrowShot(false);
                if(homeTeamHasBall){
                    homeTeam.addFreeThrowShot(false);
                }
                else{
                    awayTeam.addFreeThrowShot(false);
                }

                madeLast = false;
            }
        }

        if(made == 0){
            if(attempts == 1){
                currentPlay += player.getFullName() + " misses his free throw.";
            }
            else if(attempts > 1){
                currentPlay += player.getFullName() + " misses all of his free throws!";
            }
            else{
                currentPlay += player.getFullName() + " misses the front end of the 1 and 1!";
            }
        }
        else{
            if(attempts == -1){
                if(made == 1) {
                    currentPlay += player.getFullName() + " makes front end of the 1 and 1, but misses the second.";
                }
                else{
                    currentPlay += player.getFullName() + " makes both shots in the 1 and 1 situation";
                }
            }
            else {
                currentPlay += player.getFullName() + " makes " + made + " of " + attempts + " free throws.";
            }
        }

        if(!madeLast){
            if(homeTeamHasBall) {
                getRebound(homeTeam, awayTeam);
            }
            else{
                getRebound(awayTeam, homeTeam);
            }
        }
        else{
            deadBall = true;
            madeShot = true;
            if(!currentPlay.equals("") && savePlays) {
                plays.add(0, new GameEvent(getFormattedTime() + " (" + shotClock + ") - " + currentPlay, 1, homeTeamHasBall));
            }
            changePossession();
        }
        return made;
    }

    private void getRebound(Team offense, Team defense) {
        lastShotClock = shotClock;
        deadBall = false;
        madeShot = false;

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

        if (offChance[offHigh] - 15 > defChance[defHigh]) {
            playerWithBall = offHigh + 1;
            currentPlay += "\n" + offense.getPlayers().get(playerWithBall-1).getFullName() + " grabs the offensive rebound.";
            offense.getPlayers().get(playerWithBall-1).addRebound(true);
            offense.addRebound(true);
            if(timeRemaining >= 30) {
                shotClock = 30;
            }
            else{
                shotClock = timeRemaining;
            }
            if(!currentPlay.equals("") && savePlays) {
                plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 0, homeTeamHasBall));
            }
        } else {
            playerWithBall = defHigh + 1;
            currentPlay += "\n" + defense.getPlayers().get(playerWithBall-1).getFullName() + " grabs the defensive rebound.";
            defense.getPlayers().get(playerWithBall-1).addRebound(false);
            defense.addRebound(false);
            if(!currentPlay.equals("") && savePlays) {
                plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 2, homeTeamHasBall));
            }
            changePossession();
        }
    }

    private int[] getReboundChance(Team team) {
        int[] values = new int[5];

        for (int x = 0; x < 5; x++) {
            values[x] = team.getPlayers().get(x).getRebounding() + ((team.getPlayers().get(x).getAggressiveness()) / 5) + r.nextInt(4 * randomBoundValue);
        }
        return values;
    }

    private boolean isFoul(Team team, Player player, int situation){
        /*
        situation: 0 = normal defending, 1 = on a pass, 2 = on a shot
        situation: -1 = charging situation, -2 = off-ball situation
         */

        int foulFactor = 100 - team.getAggression() + ((player.getAggressiveness() - 50) / 10); //higher number == fewer fouls
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
            player.addTurnover();
            team.addTurnover();
            changePossession();
            return true;
        }
        else if(situation == -2 && r.nextInt(foulFactor) < 5){
            player.addFoul();
            foulString = "\n" + player.getFullName() + " has been called for an off-ball foul.";
            player.addTurnover();
            team.addTurnover();
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
                    values[x] = r.nextInt(100) / (team.getPlayers().get(x).getOffBallDefense());
                }
                else{
                    values[x] = r.nextInt(100) /  (team.getPlayers().get(x).getOnBallDefense());
                }
            }
            else if(situation == 2){
                if(x == playerWithBall - 1){
                    values[x] = r.nextInt(100) / (team.getPlayers().get(x).getOnBallDefense());
                }
                else{
                    values[x] = -100;
                }
            }
            else if(situation == -1){
                if(x == playerWithBall - 1){
                    values[x] = r.nextInt(100) / (team.getPlayers().get(x).getBallHandling());
                }
                else{
                    values[x] = -100;
                }
            }
            else if(situation == -2){
                if(x != playerWithBall - 1){
                    values[x] = r.nextInt(100) / (team.getPlayers().get(x).getOffBallMovement());
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
            if(isFoul(homeTeam, homeTeam.getPlayers().get(playerIndex), situation)){
                deadBall = true;
                alertedDeadBall = false;
                madeShot = false;
                homeFouls++;
                if(shotClock < 20 && situation != 2){
                    shotClock = 20;
                }
                else{
                    shotClock = 30;
                }
                playerFouledOut = playerFouledOut(homeTeam);
                return true;
            }
        }
        else{
            if(isFoul(awayTeam, awayTeam.getPlayers().get(playerIndex), situation)){
                deadBall = true;
                alertedDeadBall = false;
                madeShot = false;
                awayFouls++;
                if(shotClock < 20 && situation != 2){
                    shotClock = 20;
                }
                else{
                    shotClock = 30;
                }
                playerFouledOut = playerFouledOut(awayTeam);
                return  true;
            }
        }
        return false;
    }

    private boolean playerFouledOut(Team team){
        for(int x = 0; x < 5; x++){
            if(!team.getPlayers().get(x).isEligible()){
                foulString += "\nThat's " + team.getPlayers().get(x).getFullName() + "'s 5th foul!" +
                        " He has fouled out!";
                return true;
            }
        }
        return false;
    }

    private int getBallOutOfBounds(Player withBall, Player ballDef){
        // returns 0 if nothing happens, 1 if the defense knocks the ball out, and -1 if its a turnover
        int chance = withBall.getBallHandling() + r.nextInt(randomBoundValue) -
                (ballDef.getStealing() + ballDef.getOnBallDefense() + r.nextInt(randomBoundValue)) / 2;

        if(chance < -25 - ballDef.getAggressiveness() / 10){
            if(r.nextDouble() > .75){
                currentPlay += " " + withBall.getFullName() + " has lost the ball out of bounds! You can credit " +
                        ballDef.getFullName() + " with causing that turnover!";
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;
                return -1;
            }
            else{
                currentPlay += " " + ballDef.getFullName() + " has knocked the ball out of bounds!";
                deadBall = true;
                madeShot = false;
                alertedDeadBall = false;
                return 1;
            }
        }


        return 0;
    }

    private int getPostMove(Player withBall, Player ballDef) {
        int postChance = withBall.getPostMove() - ballDef.getPostDefense() + r.nextInt(randomBoundValue);

        // TODO: add a chance for fouls here
        if(postChance > 40){
            int timeChange;
            if(homeTeamHasBall) {
                timeChange = (int) (8 - (homeTeam.getPace() / 90.0) * r.nextInt(4));
            }
            else{
                timeChange = (int) (8 - (awayTeam.getPace() / 90.0) * r.nextInt(4));
            }
            smartTimeChange(timeChange);
            lastShotClock = shotClock;

            currentPlay += " " + withBall.getFullName() + " goes to work in the post";
            if(r.nextDouble() > .5 || postChance > 60){
                currentPlay += " and scores!";
                withBall.addTwoPointShot(true);
                if(homeTeamHasBall){
                    homeTeam.addTwoPointShot(true);
                }
                else{
                    awayTeam.addTwoPointShot(true);
                }

                deadBall= true;
                madeShot = true;
                if(!currentPlay.equals("") && savePlays) {
                    plays.add(0, new GameEvent(getFormattedTime() + " (" + lastShotClock + ") - " + currentPlay, 1, homeTeamHasBall));
                }
                changePossession();
                return 2;
            }
            else{
                currentPlay += ", but cannot get his shot to fall.";
                withBall.addTwoPointShot(false);
                if(homeTeamHasBall){
                    homeTeam.addTwoPointShot(false);
                }
                else{
                    awayTeam.addTwoPointShot(false);
                }

                if(homeTeamHasBall) {
                    getRebound(homeTeam, awayTeam);
                }
                else{
                    getRebound(awayTeam, homeTeam);
                }

                return 0;
            }
        }
        return -1;
    }

    private void intentionallyFoul(Team offense, Team defense){
        Player fouled, fouler;

        if(deadBall) {
            getInbounder(offense);
            fouled = getTarget(offense);

            currentPlay = offense.getPlayers().get(playerWithBall-1).getFullName() + " inbounds the ball to " +
                    fouled.getFullName() + " who is intentionally fouled by ";
        }
        else{
            fouled = offense.getPlayers().get(playerWithBall-1);
            currentPlay = fouled.getFullName() + " is intentionally fouled by ";
        }

        if(location == -1) {
            fouler = defense.getPlayers().get(r.nextInt(4));
            location = 1;
        }
        else{
            fouler = defense.getPlayers().get(fouled.getCurrentPosition()-1);
        }

        currentPlay += fouler.getFullName();

        fouler.addFoul();
        if(homeTeamHasBall){
            awayFouls++;
        }
        else{
            homeFouls++;
        }

        playerWithBall = fouled.getCurrentPosition();
        shootBonus();
        deadBall = true;
        alertedDeadBall = false;
        madeShot = false;
        playerFouledOut = playerFouledOut(defense);

        timeRemaining -= r.nextInt(4);
        if(shotClock < 20 && freeThrows == 0){
            shotClock = 20;
        }
        else{
            shotClock = 30;
        }

        if(shotClock > timeRemaining){
            shotClock = timeRemaining;
        }

    }

    private void changePossession() {
        lastShotClock = shotClock;
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
        recentTO = false;
        playType = 1;
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

    public void setPlayerIntentFoul(boolean wantFoul){
        playerIntentFoul = wantFoul;
    }

    public boolean getIsInProgress(){
        return isInProgress;
    }

    private void smartTimeChange(int timeChange){
        timeRemaining -= timeChange;
        shotClock -= timeChange;

        if(shotClock < 0){
            timeRemaining -= shotClock;
            shotClock = 0;
        }
    }

    public ArrayList<GameEvent> getPlaysOfType(Integer... types){
        ArrayList<GameEvent> newPlays = new ArrayList<>();
        if(plays != null) {
            for (GameEvent e : plays) {
                for (int x : types) {
                    if (e.getType() == x) {
                        newPlays.add(e);
                    }
                }
            }
        }
        return newPlays;
    }

    public int getPlayerWithBall() {
        return playerWithBall;
    }

    public int getLocation() {
        return location;
    }

    public int getLastPlayerWithBall() {
        return lastPlayerWithBall;
    }

    public int getLastShotClock() {
        return lastShotClock;
    }

    public int getLastTimeRemaining() {
        return lastTimeRemaining;
    }

    public int getPlayType() {
        return playType;
    }

    public boolean isMadeShot() {
        return madeShot;
    }

    public boolean isDeadBall() {
        return deadBall;
    }

    public boolean isHomeTeamHasBall() {
        return homeTeamHasBall;
    }

    public boolean isHomeTeamHasPosArrow() {
        return homeTeamHasPosArrow;
    }

    public boolean[] getMediaTimeouts() {
        return mediaTimeouts;
    }

    public boolean isPlayerWantsTO() {
        return playerWantsTO;
    }

    public boolean isRecentTO() {
        return recentTO;
    }

    public boolean isPlayerIntentFoul() {
        return playerIntentFoul;
    }

    public boolean isSavePlays() {
        return savePlays;
    }

    public boolean isPlayerFouledOut() {
        return playerFouledOut;
    }

    public boolean isAlertedDeadBall() {
        return alertedDeadBall;
    }

    public boolean isShootFreeThrows() {
        return shootFreeThrows;
    }

    public boolean isInProgress() {
        return isInProgress;
    }

    public int getFreeThrows() {
        return freeThrows;
    }

    public Player getFreeThrowShooter() {
        return freeThrowShooter;
    }

    public void setUpFromDB(GameDB gameDB){
        plays = new ArrayList<>();
        r = new Random();
        half = gameDB.half;
        timeRemaining = gameDB.timeRemaining;
        lastTimeRemaining = gameDB.lastTimeRemaining;
        shotClock = gameDB.shotClock;
        lastShotClock = gameDB.lastShotClock;
        homeScore = gameDB.homeScore;
        awayScore = gameDB.awayScore;
        homeFouls = gameDB.homeFouls;
        awayFouls = gameDB.awayFouls;
        playerWithBall = gameDB.playerWithBall;
        lastPlayerWithBall = gameDB.lastPlayerWithBall;
        location = gameDB.location;
        playType = gameDB.playType;

        mediaTimeouts = new boolean[]{gameDB.mediaTimeout1, gameDB.mediaTimeout2, gameDB.mediaTimeout3,
                gameDB.mediaTimeout4, gameDB.mediaTimeout5, gameDB.mediaTimeout6, gameDB.mediaTimeout7,
                gameDB.mediaTimeout8, gameDB.mediaTimeout9, gameDB.mediaTimeout10};
        playerWantsTO = gameDB.playerWantsTO;
        recentTO = gameDB.recentTO;
        isInProgress = true;
        homeTimeouts = gameDB.homeTimeouts;
        awayTimeouts = gameDB.awayTimeouts;

        savePlays = gameDB.savePlays;
    }

    public void setGameEvents(GameEventDB[] gameEvents){
        plays = new ArrayList<>();
        for(GameEventDB event : gameEvents){
            plays.add(new GameEvent(event.event, event.type, event.homeTeam));
        }
    }
}

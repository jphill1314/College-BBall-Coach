package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jphil on 2/17/2018.
 */

@Entity(foreignKeys = {@ForeignKey(entity = TeamDB.class, parentColumns = "id", childColumns = "homeTeamID"),
        @ForeignKey(entity = TeamDB.class, parentColumns = "id", childColumns = "awayTeamID")})
public class GameDB {

    @PrimaryKey
    public int gameID;

    public int homeTeamID;
    public int awayTeamID;

    public int homeScore;
    public int awayScore;

    public boolean isNeutralCourt;
    public boolean isPlayed;


    public boolean isInProgress;
    public int half;
    public int timeRemaining;
    public int playerWithBall;
    public int location;
    public int shotClock;
    public int lastPlayerWithBall;
    public int lastShotClock;
    public int lastTimeRemaining;
    public int playType;
    public boolean madeShot;
    public boolean deadBall;
    public boolean homeTeamHasBall;
    public boolean homeTeamHasPosArrow;
    public boolean mediaTimeout1;
    public boolean mediaTimeout2;
    public boolean mediaTimeout3;
    public boolean mediaTimeout4;
    public boolean mediaTimeout5;
    public boolean mediaTimeout6;
    public boolean mediaTimeout7;
    public boolean mediaTimeout8;
    public boolean mediaTimeout9;
    public boolean mediaTimeout10;
    public boolean playerWantsTO;
    public boolean recentTO;
    public boolean playerIntentFoul;
    public int homeTimeouts;
    public int awayTimeouts;
    public int homeFouls;
    public int awayFouls;
    public boolean savePlays;
    public boolean playerFouledOut;
    public boolean alertedDeadBall;
    public boolean shootFreeThrows;
    public int freeThrows;
    public int freeThrowShooterID;
}

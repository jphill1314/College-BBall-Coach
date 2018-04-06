package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jphil on 2/17/2018.
 */

@Entity(foreignKeys = @ForeignKey(entity = TeamDB.class, parentColumns = "id", childColumns = "teamID"))
public class PlayerDB {

    @PrimaryKey(autoGenerate = true)
    public int playerId;

    public int teamID;

    public String lastName;
    public String firstName;
    public int pos;
    public int year;
    public int trainingAs;
    public int currentRosterLocation;

    // Offensive attributes
    public int closeRangeShot;
    public int midRangeShot;
    public int longRangeShot;
    public int freeThrowShot;
    public int postMove;
    public int ballHandling;
    public int passing;
    public int screening;
    public int offBallMovement;

    public int closeRangeShotProgress;
    public int midRangeShotProgress;
    public int longRangeShotProgress;
    public int freeThrowShotProgress;
    public int postMoveProgress;
    public int ballHandlingProgress;
    public int passingProgress;
    public int screeningProgress;
    public int offballMovementProgress;

    // Defensive attributes
    public int postDefense;
    public int perimeterDefense;
    public int onBallDefense;
    public int offBallDefense;
    public int stealing;
    public int rebounding;

    public int postDefenseProgress;
    public int perimeterDefenseProgress;
    public int onBallDefenseProgress;
    public int offBallDefenseProgress;
    public int stealingProgress;
    public int reboundingProgress;

    // Other attributes
    public int stamina;
    public int aggressiveness;
    public int workEthic;

    public int staminaProgress;

    // Tracked Stats
    public int gamesPlayed;
    public int totalMinutes;

    // Stuff saved for games in progress
    public int gameFouls = -1;
    public int gameTwoPointShotAttempts;
    public int gameTwoPointShotMade;
    public int gameThreePointShotAttempts;
    public int gameThreePointShotMade;
    public int gameFreeThrowAttempts;
    public int gameFreeThrowMade;
    public int gameAssists;
    public int gameORebounds;
    public int gameDRebounds;
    public int gameSteals;
    public int gameTurnovers;
    public int gameTimePlayed;
    public double gameFatigue;
    public int gameRosterLocation;
    public int offensiveModifier;
    public int defensiveModifier;
}

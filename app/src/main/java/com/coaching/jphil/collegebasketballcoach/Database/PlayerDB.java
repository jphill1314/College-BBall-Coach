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

    @PrimaryKey
    public int playerId;

    public int teamID;

    public String lastName;
    public String firstName;
    public int pos;
    public int year;

    public int minutes;

    // Offensive attributes
    public int closeRangeShot;
    public int midRangeShot;
    public int longRangeShot;
    public int ballHandling;
    public int passing;
    public int screening;
    public int offBallMovement;

    // Defensive attributes
    public int postDefense;
    public int perimeterDefense;
    public int onBallDefense;
    public int offBallDefense;
    public int stealing;
    public int rebounding;

    // Mental attributes
    // add these later, maybe

    // Physical attributes
    public int stamina;

    // Tracked Stats
    public int gamesPlayed;
    public int totalMinutes;
}

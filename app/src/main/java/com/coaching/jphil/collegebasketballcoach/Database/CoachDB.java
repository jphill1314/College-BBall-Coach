package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jphil on 2/18/2018.
 */

@Entity(foreignKeys = @ForeignKey(entity = TeamDB.class, parentColumns = "id", childColumns = "teamID"))
public class CoachDB {

    @PrimaryKey
    public int coachID;

    public int teamID;

    public String firstName;
    public String lastName;
    public int pos;

    public int shotTeaching;
    public int ballControlTeaching;
    public int screenTeaching;
    public int offPositionTeaching;

    public int defPositionTeaching;
    public int defOnBallTeaching;
    public int defOffBallTeaching;
    public int reboundTeaching;
    public int stealTeaching;

    public int conditioningTeaching;

    public int recruitingAbility;

    public int tendencyToSub;

    public String recruitIds;
}

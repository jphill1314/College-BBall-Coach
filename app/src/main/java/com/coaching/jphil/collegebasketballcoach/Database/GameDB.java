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

    public boolean isPlayed;
}

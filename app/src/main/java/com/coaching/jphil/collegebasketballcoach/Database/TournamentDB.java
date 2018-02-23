package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Jake on 2/22/2018.
 */

@Entity (foreignKeys = {@ForeignKey(entity = TeamDB.class, parentColumns = "id", childColumns = "teamID"),
                @ForeignKey(entity = GameDB.class, parentColumns = "gameID", childColumns = "gamesID")})
public class TournamentDB {

    @PrimaryKey
    public int tournamentID;

    public int[] teamID;
    public int[] gamesID;

    public String name;

    public boolean hasChampion;
    public boolean playAtNeutralCourt;


}

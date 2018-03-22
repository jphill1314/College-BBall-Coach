package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Jake on 3/21/2018.
 */

@Entity (foreignKeys = {@ForeignKey(entity = PlayerDB.class, parentColumns = "playerId", childColumns = "playerId"),
                        @ForeignKey(entity = GameDB.class, parentColumns = "gameID", childColumns = "gameId")})
public class GameStatsDB {

    @PrimaryKey(autoGenerate = true)
    public int gameStatsId;

    public int playerId;
    public int gameId;
    public int minutes;
    public int fouls;
    public int twoPointShotAttempts;
    public int twoPointShotMade;
    public int threePointShotAttempts;
    public int threePointShotMade;
    public int freeThrowAttempts;
    public int freeThrowMade;
    public int assists;
    public int oRebounds;
    public int dRebounds;
    public int steals;
    public int turnovers;

    public String getStatsAsString(){
        int points = freeThrowMade + 2 * twoPointShotMade + 3 * threePointShotMade;
        int rebounds = oRebounds + dRebounds;

        return "Minutes: " + minutes + "\nPoints: " + points + "\nRebounds: " + rebounds +
                "\nAssists: " + assists + "\nSteals: " + steals + "\nTurnovers: " + turnovers;
    }
}

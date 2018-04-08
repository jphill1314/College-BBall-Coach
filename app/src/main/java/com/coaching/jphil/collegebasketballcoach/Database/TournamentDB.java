package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Jake on 2/22/2018.
 */
@Entity
public class TournamentDB {

    @PrimaryKey
    public int tournamentID;

    public String teamIDs;
    public String gameIDs;
    public int conferenceId;

    public String name;

    public boolean hasChampion;
    public boolean playAtNeutralCourt;


}

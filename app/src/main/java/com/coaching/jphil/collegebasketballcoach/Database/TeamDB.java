package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jphil on 2/17/2018.
 */

@Entity (foreignKeys = @ForeignKey(entity = ConferenceDB.class, parentColumns = "conferenceID", childColumns = "conferenceID"))
public class TeamDB {

    @PrimaryKey
    public int id;

    public boolean isPlayerControlled;

    public int conferenceID;

    public String schoolName;
    public String schoolMascot;

    public int currentYear;

    public int offFavorsThrees;
    public int defFavorsThrees;
    public int aggression;
    public int pace;

    public int colorMain;
    public int colorDark;
    public int colorLight;

    public boolean isSeasonOver;
}

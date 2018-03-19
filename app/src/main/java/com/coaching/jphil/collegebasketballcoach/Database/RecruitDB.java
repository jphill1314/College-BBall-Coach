package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Jake on 2/20/2018.
 */

@Entity (foreignKeys = @ForeignKey(entity = TeamDB.class, parentColumns = "id", childColumns = "teamID"))
public class RecruitDB {

    @PrimaryKey
    public int recruitID;

    public int teamID;

    public String firstName;
    public String lastName;

    public int pos;
    public int rating;
    public int interest;

    public boolean isCommitted;
}

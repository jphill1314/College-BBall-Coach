package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Jake on 3/1/2018.
 */

@Entity
public class ConferenceDB {

    @PrimaryKey
    public int conferenceID;

    public String name;
    public int type; // 0 = standardTenTeam
}

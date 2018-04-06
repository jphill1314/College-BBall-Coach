package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class GameEventDB {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String event;
    public int type;
    public boolean homeTeam;
}

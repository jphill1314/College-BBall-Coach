package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by jphil on 2/17/2018.
 */

@Entity
public class TeamDB {

    @PrimaryKey
    public int id;

    public String schoolName;
    public String schoolMascot;

    public int wins;
    public int loses;

    public int offFavorsThrees;
    public int defFavorsThrees;
    public int defTendToHelp;
    public int pace;
}

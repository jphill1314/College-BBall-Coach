package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by jphil on 2/17/2018.
 */

@Database(entities = {TeamDB.class, PlayerDB.class, GameDB.class, CoachDB.class, RecruitDB.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AppDAO appDAO();
}

package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.coaching.jphil.collegebasketballcoach.adapters.RecruitAdapter;

/**
 * Created by jphil on 2/17/2018.
 */

@Dao
public interface AppDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTeams(TeamDB... teams);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertPlayers(PlayerDB... players);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertGames(GameDB... games);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertCoaches(CoachDB... coaches);

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    public void insertRecruits(RecruitDB... recruits);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertConferences(ConferenceDB... conferences);


    @Query("SELECT * FROM TeamDB")
    public TeamDB[] loadAllTeams();

    @Query("SELECT * FROM PlayerDB")
    public PlayerDB[] loadAllPlayers();

    @Query("SELECT * FROM GameDB")
    public GameDB[] loadAllGames();

    @Query("SELECT * FROM CoachDB")
    public CoachDB[] loadAllCoaches();

    @Query("SELECT * FROM RecruitDB")
    public RecruitDB[] loadAllRecruits();

    @Query("SELECT * FROM ConferenceDB")
    public ConferenceDB[] loadAllConferences();


    @Query("DELETE FROM TeamDB")
    public void deleteTeamDB();

    @Query("DELETE FROM PlayerDB")
    public void deletePlayerDB();

    @Query("DELETE FROM GameDB")
    public void deleteGameDB();

    @Query("DELETE FROM CoachDB")
    public void deleteCoachDB();

    @Query("DELETE FROM RecruitDB")
    public void deleteRecruitDB();

    @Query("DELETE FROM ConferenceDB")
    public void deleteConferences();

}

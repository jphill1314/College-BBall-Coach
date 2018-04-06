package com.coaching.jphil.collegebasketballcoach.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTournaments(TournamentDB... tournaments);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertGamesStats(GameStatsDB... gameStats);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertGameEvents(ArrayList<GameEventDB> gameEvents);


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

    @Query("SELECT * FROM TournamentDB")
    public TournamentDB[] loadAllTournaments();

    @Query("SELECT * FROM GameStatsDB where playerId = :playerId")
    public GameStatsDB[] loadPlayerStats(int playerId);

    @Query("SELECT * FROM GameDB where gameID = :gameId")
    public GameDB loadGameById(int gameId);

    @Query("SELECT * FROM PlayerDB where teamID = :teamId")
    public PlayerDB[] loadPlayersByTeam(int teamId);

    @Query("SELECT * FROM GameEventDB")
    public GameEventDB[] loadAllEvents();


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

    @Query("DELETE FROM TournamentDB")
    public void deleteTournaments();

    @Query("DELETE FROM GameStatsDB")
    public void deleteGameStats();

    @Query("DELETE FROM GameEventDB")
    public void deleteGameEvents();

    @Delete
    public void deletePlayers(ArrayList<PlayerDB> players);
}

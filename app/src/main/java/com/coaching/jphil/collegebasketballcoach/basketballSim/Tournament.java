package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jake on 2/21/2018.
 */

public class Tournament {

    private ArrayList<Team> teams;
    private ArrayList<Game> games;

    private String name;

    private boolean hasChampion;
    private boolean playAtNeutralCourt;


    // teams must be sorted in their seeding and must be of size 2^n
    public Tournament(ArrayList<Team> teams, String name, boolean playAtNeutralCourt){
        this.teams = teams;
        this.name = name;
        this.playAtNeutralCourt = playAtNeutralCourt;

        games = new ArrayList<>();
        generateNextRound();
        for(Team t: teams){
            Log.v("tourn", t.getFullName());
        }
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void setGames(ArrayList<Game> games) {
        this.games = games;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasChampion() {
        return hasChampion;
    }

    public void setHasChampion(boolean hasChampion) {
        this.hasChampion = hasChampion;
    }

    public boolean isPlayAtNeutralCourt() {
        return playAtNeutralCourt;
    }

    public void setPlayAtNeutralCourt(boolean playAtNeutralCourt) {
        this.playAtNeutralCourt = playAtNeutralCourt;
    }

    public void generateNextRound(){
        if(games.size() == 0){
            if(teams.size() == 2){
                games.add(new Game(teams.get(0), teams.get(1), playAtNeutralCourt));
            }
            else if(teams.size() == 4){
                games.add(new Game(teams.get(0), teams.get(3), playAtNeutralCourt));
                games.add(new Game(teams.get(1), teams.get(2), playAtNeutralCourt));
            }
            else if(teams.size() == 8){
                games.add(new Game(teams.get(0), teams.get(7), playAtNeutralCourt));
                games.add(new Game(teams.get(1), teams.get(6), playAtNeutralCourt));
                games.add(new Game(teams.get(2), teams.get(5), playAtNeutralCourt));
                games.add(new Game(teams.get(3), teams.get(4), playAtNeutralCourt));
            }
            else if(teams.size() == 16){
                games.add(new Game(teams.get(0), teams.get(15), playAtNeutralCourt));
                games.add(new Game(teams.get(1), teams.get(14), playAtNeutralCourt));
                games.add(new Game(teams.get(2), teams.get(13), playAtNeutralCourt));
                games.add(new Game(teams.get(3), teams.get(12), playAtNeutralCourt));
                games.add(new Game(teams.get(4), teams.get(11), playAtNeutralCourt));
                games.add(new Game(teams.get(5), teams.get(10), playAtNeutralCourt));
                games.add(new Game(teams.get(6), teams.get(9), playAtNeutralCourt));
                games.add(new Game(teams.get(7), teams.get(8), playAtNeutralCourt));
            }
        }
        else if(games.size() == 2 || games.size() == 6 || games.size() == 14){
            // one game left to play
            int gameOne = games.size() - 2;
            int gameTwo = games.size() - 1;

            Team hTeam, aTeam;
            if(games.get(gameOne).homeTeamWin()){
                hTeam = games.get(gameOne).getHomeTeam();
            }
            else{
                hTeam = games.get(gameOne).getAwayTeam();
            }

            if(games.get(gameTwo).homeTeamWin()){
                aTeam = games.get(gameTwo).getHomeTeam();
            }
            else{
                aTeam = games.get(gameTwo).getAwayTeam();
            }
            games.add(new Game(hTeam, aTeam, true));
        }
        else if(games.size() == 4 || games.size() == 12){
            // two games left to play
            int gameMod = games.size() - 4;

            for(int x = 0; x < 2; x++){
                Team hTeam, aTeam;
                if(games.get(2*x).homeTeamWin()){
                    hTeam = games.get(2*x + gameMod).getHomeTeam();
                }
                else{
                    hTeam = games.get(2*x + gameMod).getAwayTeam();
                }

                if(games.get(1).homeTeamWin()){
                    aTeam = games.get(2*x + 1 + gameMod).getHomeTeam();
                }
                else{
                    aTeam = games.get(2*x + 1 + gameMod).getAwayTeam();
                }
                games.add(new Game(hTeam, aTeam, true));
            }
        }
        else if(games.size() == 8){
            for(int x = 0; x < 4; x++){
                Team hTeam, aTeam;
                if(games.get(2*x).homeTeamWin()){
                    hTeam = games.get(2*x).getHomeTeam();
                }
                else{
                    hTeam = games.get(2*x).getAwayTeam();
                }

                if(games.get(1).homeTeamWin()){
                    aTeam = games.get(2*x + 1).getHomeTeam();
                }
                else{
                    aTeam = games.get(2*x + 1).getAwayTeam();
                }
                games.add(new Game(hTeam, aTeam, true));
            }
        }
    }

    public void playNextRound() {
        if (!hasChampion) {
            boolean playedAGame = false;
            for (Game game : games) {
                if (!game.isPlayed()) {
                    game.simulateGame();
                    playedAGame = true;
                }
            }

            if (!playedAGame) {
                hasChampion = true;
            }
            else{
                generateNextRound();
            }
        }
    }

    public Team getChampion(){
        if(hasChampion){
            Game champGame = games.get(games.size()-1);
            if(champGame.homeTeamWin()){
                return champGame.getHomeTeam();
            }
            else{
                return champGame.getAwayTeam();
            }
        }
        return null;
    }
}

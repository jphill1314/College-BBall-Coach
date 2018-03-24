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

    public Tournament(String name, boolean playAtNeutralCourt, boolean hasChampion){
        this.name = name;
        this.playAtNeutralCourt = playAtNeutralCourt;
        this.hasChampion = hasChampion;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team){
        if(teams == null){
            teams = new ArrayList<>();
        }
        teams.add(team);
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void addGame(Game game){
        if(games == null){
            games = new ArrayList<>();
        }
        games.add(game);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasChampion() {
        if(games.size() == teams.size()-1){
            if(games.get(games.size()-1).isPlayed()){
                return true;
            }
        }
        return false;
    }

    public boolean isPlayAtNeutralCourt() {
        return playAtNeutralCourt;
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
                games.add(new Game(teams.get(0), teams.get(7), playAtNeutralCourt));//1
                games.add(new Game(teams.get(3), teams.get(4), playAtNeutralCourt));//4

                games.add(new Game(teams.get(1), teams.get(6), playAtNeutralCourt));//2
                games.add(new Game(teams.get(2), teams.get(5), playAtNeutralCourt));//3
            }
            else if(teams.size() == 16){
                games.add(new Game(teams.get(0), teams.get(15), playAtNeutralCourt));//1
                games.add(new Game(teams.get(7), teams.get(8), playAtNeutralCourt));//8

                games.add(new Game(teams.get(2), teams.get(13), playAtNeutralCourt));//3
                games.add(new Game(teams.get(4), teams.get(11), playAtNeutralCourt));//5

                games.add(new Game(teams.get(1), teams.get(14), playAtNeutralCourt));//2
                games.add(new Game(teams.get(6), teams.get(9), playAtNeutralCourt));//7

                games.add(new Game(teams.get(3), teams.get(12), playAtNeutralCourt));//4
                games.add(new Game(teams.get(5), teams.get(10), playAtNeutralCourt));//6
            }
        }
        else if(games.size() == 2 || games.size() == 6 || games.size() == 14){
            // one game left to play
            int gameOne = games.size() - 2;
            int gameTwo = games.size() - 1;

            Team hTeam, aTeam;
            if(games.get(gameOne).homeTeamWin()){
                hTeam = games.get(gameOne).getHomeTeam();
                games.get(gameOne).getAwayTeam().toggleSeasonOver();
            }
            else{
                hTeam = games.get(gameOne).getAwayTeam();
                games.get(gameOne).getHomeTeam().toggleSeasonOver();
            }

            if(games.get(gameTwo).homeTeamWin()){
                aTeam = games.get(gameTwo).getHomeTeam();
                games.get(gameTwo).getAwayTeam().toggleSeasonOver();
            }
            else{
                aTeam = games.get(gameTwo).getAwayTeam();
                games.get(gameTwo).getHomeTeam().toggleSeasonOver();
            }
            games.add(new Game(hTeam, aTeam, true));
        }
        else if(games.size() == 4 || games.size() == 12){
            // two games left to play
            int gameMod = games.size() - 4;

            for(int x = 0; x < 2; x++){
                Team hTeam, aTeam;
                if(games.get(2*x+gameMod).homeTeamWin()){
                    hTeam = games.get(2*x + gameMod).getHomeTeam();
                    games.get(2*x + gameMod).getAwayTeam().toggleSeasonOver();
                }
                else{
                    hTeam = games.get(2*x + gameMod).getAwayTeam();
                    games.get(2*x + gameMod).getHomeTeam().toggleSeasonOver();
                }

                if(games.get(2*x+1+gameMod).homeTeamWin()){
                    aTeam = games.get(2*x + 1 + gameMod).getHomeTeam();
                    games.get(2*x + 1 + gameMod).getAwayTeam().toggleSeasonOver();
                }
                else{
                    aTeam = games.get(2*x + 1 + gameMod).getAwayTeam();
                    games.get(2*x + 1 + gameMod).getHomeTeam().toggleSeasonOver();
                }
                games.add(new Game(hTeam, aTeam, true));
            }
        }
        else if(games.size() == 8){
            for(int x = 0; x < 4; x++){
                Team hTeam, aTeam;
                if(games.get(2*x).homeTeamWin()){
                    hTeam = games.get(2*x).getHomeTeam();
                    games.get(2*x).getAwayTeam().toggleSeasonOver();
                }
                else{
                    hTeam = games.get(2*x).getAwayTeam();
                    games.get(2*x).getHomeTeam().toggleSeasonOver();
                }

                if(games.get(1).homeTeamWin()){
                    aTeam = games.get(2*x + 1).getHomeTeam();
                    games.get(2*x + 1).getAwayTeam().toggleSeasonOver();
                }
                else{
                    aTeam = games.get(2*x + 1).getAwayTeam();
                    games.get(2*x + 1).getHomeTeam().toggleSeasonOver();
                }
                games.add(new Game(hTeam, aTeam, true));
            }
        }
        getChampion();
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
        if(isHasChampion()){
            Game champGame = games.get(games.size()-1);
            if(champGame.homeTeamWin()){
                if(!champGame.getAwayTeam().isSeasonOver()){
                    champGame.getAwayTeam().toggleSeasonOver();
                }
                return champGame.getHomeTeam();
            }
            else{
                if(!champGame.getHomeTeam().isSeasonOver()){
                    champGame.getHomeTeam().toggleSeasonOver();
                }
                return champGame.getAwayTeam();
            }
        }
        return null;
    }

}

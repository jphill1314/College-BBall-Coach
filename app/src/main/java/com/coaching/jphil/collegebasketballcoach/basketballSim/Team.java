package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by jphil on 2/14/2018.
 */

public class Team {

    private double maxOffensiveEfficiency = 120.0;
    private double minDefensiveEfficiency = 70.0;

    private ArrayList<Player> players;
    private ArrayList<Coach> coaches;
    private int gamesPlayed;

    private int wins, loses, overallRating;

    private String schoolName, mascot;

    private int tClose, tMid, tLong, tHandle, tPass, tScreen;
    private int tPost, tPerim, tOnBall, tOffBall, tSteal, tRebound;
    private int tStamina;

    // Strategy
    private int offenseFavorsThrees = 50;
    private int defenseFavorsThrees = 50;
    private int defenseTendToHelp = 50;
    private int pace = 70;

    public Team(String schoolName, String mascot, ArrayList<Player> players, ArrayList<Coach> coaches){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.players = players;
        this.coaches = coaches;

        gamesPlayed = 0;
        wins = 0;
        loses = 0;

        setEqualTrainingFocus();
        setOverallRating();
    }

    public Team(String schoolName, String mascot, int wins, int loses, int offenseFavorsThrees,
                int defenseFavorsThrees, int defenseTendToHelp, int pace){
        this.schoolName = schoolName;
        this.mascot = mascot;

        this.wins = wins;
        this.loses = loses;
        gamesPlayed = this.wins + this.loses;

        this.offenseFavorsThrees = offenseFavorsThrees;
        this.defenseFavorsThrees = defenseFavorsThrees;
        this.defenseTendToHelp = defenseTendToHelp;
        this.pace = pace;
    }

    public void addPlayers(ArrayList<Player> players){
        if(this.players == null) {
            this.players = players;
        }
        else{
            this.players.addAll(players);
        }
        setOverallRating();
    }

    public void addPlayer(Player player){
        if(players == null){
            players = new ArrayList<Player>();
            players.add(player);
        }
        else{
            players.add(player);
        }
        setOverallRating();
    }

    public void removePlayer(Player player){
        players.remove(player);
    }

    public void addCoach(Coach coach){
        if(coaches == null){
            coaches = new ArrayList<Coach>();
            coaches.add(coach);
        }
        else{
            coaches.add(coach);
        }
    }

    public String getFullName(){
        return schoolName + " " + mascot;
    }

    public String getSchoolName(){
        return schoolName;
    }

    public String getMascot(){
        return mascot;
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public int getNumberOfPlayers(){
        return players.size();
    }

    public ArrayList<Coach> getCoaches(){
        return coaches;
    }

    public int getGamesPlayed(){
        return gamesPlayed;
    }

    public void newSeason(){
        wins = 0;
        loses = 0;
        gamesPlayed = 0;
    }

    public void playGame(boolean wonGame){
        gamesPlayed++;
        if(wonGame){
            wins++;
        }
        else{
            loses++;
        }
    }

    public int getWins(){
        return wins;
    }

    public int getLoses(){
        return loses;
    }

    public int getOverallRating(){
        return overallRating;
    }

    public void setOffenseFavorsThrees(int value){
        offenseFavorsThrees = value;
    }

    public void setDefenseFavorsThrees(int value){
        defenseFavorsThrees = value;
    }

    public void setDefenseTendToHelp(int value){
        defenseTendToHelp = value;
    }

    public void setPace(int value){
        pace = value;
    }

    public int getOffenseFavorsThrees() {
        return offenseFavorsThrees;
    }

    public int getDefenseFavorsThrees() {
        return defenseFavorsThrees;
    }

    public int getDefenseTendToHelp() {
        return defenseTendToHelp;
    }

    public int getPace(){
        return pace;
    }

    public int getTotalMinutes(){
        int minutes = 0;
        for(Player player: players){
            minutes += player.getMinutes();
        }

        return minutes;
    }

    private void setOverallRating(){
        overallRating = 0;
        for(Player p : players){
            overallRating += p.getOverallRating();
        }

        overallRating = overallRating / players.size();
    }

    public double getTotalEfficiency(){
        double offense = 0;
        double defense = 0;

        for(Player player:players){
            offense += player.getMinutes()/40.0 * player.getOffensiveEfficiency(offenseFavorsThrees, pace);
            defense += player.getMinutes()/40.0 * player.getDefensiveEfficiency(defenseTendToHelp, defenseFavorsThrees, pace);
        }

        offense = offense / 3.2;
        defense = defense / 4.5;

        offense = offense / 100 * maxOffensiveEfficiency;
        defense = 100 / defense * minDefensiveEfficiency;

        Log.v("eff", offense + " " + defense);
        return offense - defense;
    }

    public double getOffensiveEfficiency(){
        double offense = 0;

        for(Player player:players){
            offense += player.getMinutes()/40.0 * player.getOffensiveEfficiency(offenseFavorsThrees, pace);
        }

        offense = offense / 2.5;
        offense = offense / 100 * maxOffensiveEfficiency;

        return  offense;
    }

    private void setEqualTrainingFocus(){
        tClose = 100 / 13 + 1;
        tMid = 100 / 13 + 1;
        tLong = 100 / 13 + 1;
        tHandle = 100 / 13 + 1;
        tPass = 100 / 13 + 1;
        tScreen = 100 / 13 + 1;
        tPost = 100 / 13 + 1;
        tPerim = 100 / 13 + 1;
        tOnBall = 100 / 13 + 1;
        tOffBall = 100 / 13;
        tSteal = 100 / 13;
        tRebound = 100 / 13;
        tStamina = 100 / 13;
    }

    public int gettClose() {
        return tClose;
    }

    public void settClose(int tClose) {
        this.tClose = tClose;
    }

    public int gettMid() {
        return tMid;
    }

    public void settMid(int tMid) {
        this.tMid = tMid;
    }

    public int gettLong() {
        return tLong;
    }

    public void settLong(int tLong) {
        this.tLong = tLong;
    }

    public int gettHandle() {
        return tHandle;
    }

    public void settHandle(int tHandle) {
        this.tHandle = tHandle;
    }

    public int gettPass() {
        return tPass;
    }

    public void settPass(int tPass) {
        this.tPass = tPass;
    }

    public int gettScreen() {
        return tScreen;
    }

    public void settScreen(int tScreen) {
        this.tScreen = tScreen;
    }

    public int gettPost() {
        return tPost;
    }

    public void settPost(int tPost) {
        this.tPost = tPost;
    }

    public int gettPerim() {
        return tPerim;
    }

    public void settPerim(int tPerim) {
        this.tPerim = tPerim;
    }

    public int gettOnBall() {
        return tOnBall;
    }

    public void settOnBall(int tOnBall) {
        this.tOnBall = tOnBall;
    }

    public int gettOffBall() {
        return tOffBall;
    }

    public void settOffBall(int tOffBall) {
        this.tOffBall = tOffBall;
    }

    public int gettSteal() {
        return tSteal;
    }

    public void settSteal(int tSteal) {
        this.tSteal = tSteal;
    }

    public int gettRebound() {
        return tRebound;
    }

    public void settRebound(int tRebound) {
        this.tRebound = tRebound;
    }

    public int gettStamina() {
        return tStamina;
    }

    public void settStamina(int tStamina) {
        this.tStamina = tStamina;
    }
}

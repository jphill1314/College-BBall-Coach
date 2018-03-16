package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.content.Context;
import android.util.Log;

import com.coaching.jphil.collegebasketballcoach.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by jphil on 2/14/2018.
 */

public class Team {

    private boolean isPlayerControlled;
    private Context context;

    private double maxOffensiveEfficiency = 120.0;
    private double minDefensiveEfficiency = 70.0;

    private ArrayList<Player> players, rosterPlayers, subPlayers;
    private ArrayList<Coach> coaches;
    private ArrayList<Recruit> recruits;
    private int gamesPlayed;

    private int wins, loses, overallRating;

    private String schoolName, mascot;

    private int offenseFocus, perimeterFocus, skillFocus;

    // Strategy
    private int offenseFavorsThrees = 50;
    private int defenseFavorsThrees = 50;
    private int defenseTendToHelp = 50;
    private int pace = 70;

    private int lastScoreDif = 0;

    private int id;

    public Team(String schoolName, String mascot, ArrayList<Player> players, ArrayList<Coach> coaches,
                boolean isPlayerControlled, Context context){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.players = players;
        this.coaches = coaches;
        this.context = context;

        this.isPlayerControlled = isPlayerControlled;

        gamesPlayed = 0;
        wins = 0;
        loses = 0;

        setEqualTrainingFocus();
        setOverallRating();
    }

    public Team(String schoolName, String mascot, boolean isPlayerControlled, int wins, int loses, int offenseFavorsThrees,
                int defenseFavorsThrees, int defenseTendToHelp, int pace, int offenseFocus,
                int perimeterFocus, int skillFocus, Context context){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.isPlayerControlled = isPlayerControlled;
        this.context = context;

        this.wins = wins;
        this.loses = loses;
        gamesPlayed = this.wins + this.loses;

        this.offenseFavorsThrees = offenseFavorsThrees;
        this.defenseFavorsThrees = defenseFavorsThrees;
        this.defenseTendToHelp = defenseTendToHelp;
        this.pace = pace;

        this.offenseFocus = offenseFocus;
        this.perimeterFocus = perimeterFocus;
        this.skillFocus = skillFocus;
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

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
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

    public void setRecruits(ArrayList<Recruit> recruits){
        this.recruits = recruits;
    }

    public ArrayList<Recruit> getRecruits(){
        return recruits;
    }

    public void addRecruit(Recruit recruit){
        if(recruits == null){
            recruits = new ArrayList<Recruit>();
        }
        recruits.add(recruit);
    }

    public void newSeason(){
        int improve = 0;
        for(Coach c: coaches){
            improve+= c.getOverallRating();
        }
        improve = (improve / coaches.size()) / 10;

        Iterator<Player> itr = players.iterator();
        while(itr.hasNext()){
            Player p = itr.next();
            p.newSeason(improve, gamesPlayed, offenseFocus, perimeterFocus, skillFocus);
            if(p.getYear() > 3){
                itr.remove();
            }
        }

        wins = 0;
        loses = 0;
        gamesPlayed = 0;

        if(players.size() < 10){
            generateFreshman(10 - players.size());
        }
    }

    public void preGameSetup(){
        rosterPlayers = new ArrayList<>(players);
        subPlayers = new ArrayList<>(players);

        for(Player p: players){
            p.preGameSetup();
        }
    }

    public void playGame(boolean wonGame){
        gamesPlayed++;
        if(wonGame){
            wins++;
        }
        else{
            loses++;
        }
        for(Player p: players){
            p.playGame();
        }

        players = new ArrayList<>(rosterPlayers);
        lastScoreDif = 0;
    }

    public int getCoachTalk(int scoreDif){
        if(scoreDif < lastScoreDif - 5){
            lastScoreDif = scoreDif;
            if(scoreDif > 15){
                return 3; // more effect
            }
            return 0; // no effect
        }
        else{
            lastScoreDif = scoreDif;
            return 1; // smaller effect
        }
    }

    public boolean getTimeout(int scoreDif){
        // scoreDif needs to be teamScore - opponentScore
        return (scoreDif < lastScoreDif - 8) && (Math.random() > .5) && (lastScoreDif < 30);
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

    public boolean isPlayerControlled(){
        return isPlayerControlled;
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
        offenseFocus = 50;
        perimeterFocus = 50;
        skillFocus = 50;
    }

    public int getOffenseFocus() {
        return offenseFocus;
    }

    public void setOffenseFocus(int offenseFocus) {
        this.offenseFocus = offenseFocus;
    }

    public int getPerimeterFocus() {
        return perimeterFocus;
    }

    public void setPerimeterFocus(int perimeterFocus) {
        this.perimeterFocus = perimeterFocus;
    }

    public int getSkillFocus() {
        return skillFocus;
    }

    public void setSkillFocus(int skillFocus) {
        this.skillFocus = skillFocus;
    }

    public int getNumberOfPlayersAtPosition(int position, boolean countSeniors){
        int num = 0;
        for(Player player:players){
            if(player.getPosition() == position){
                if(!countSeniors && player.getYear() != 3){
                    num++;
                }
            }
        }
        return num;
    }

    private void generateFreshman(int numPlayers){
        Random r = new Random();
        String[] lastNames = context.getResources().getStringArray(R.array.last_names);
        String[] firstNames = context.getResources().getStringArray(R.array.first_names);

        for(int i = 0; i < numPlayers; i++){
            players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                    r.nextInt(4) + 1, 0, overallRating - r.nextInt(10)));
        }
    }

    public boolean updateSubs(int i1, int i2){
        if(subPlayers.get(i2).isEligible()) {
            Collections.swap(subPlayers, i1, i2);
            return true;
        }
        return false;
    }

    public void makeSubs(){
        players = new ArrayList<>(subPlayers);
    }

    public void aiMakeSubs(int half, int timeRemaining){
        int tendToSub = coaches.get(0).getTendencyToSub();

        Random r = new Random();

        for(int x = 0; x < 5; x++){
            if(subPlayers.get(x).isEligible()){
                if(subPlayers.get(x).getFatigue() > r.nextInt(101) - tendToSub ||
                        (subPlayers.get(x).isInFoulTrouble(half, timeRemaining) && r.nextInt(35) + tendToSub > 75)){
                    int sub = findSub(half, timeRemaining);
                    if(subPlayers.get(x).getOverallRating() / subPlayers.get(x).getFatigue() <
                            subPlayers.get(sub).getOverallRating()/ subPlayers.get(sub).getFatigue()){
                        updateSubs(x, sub);
                    }
                }
            }
            else{
                updateSubs(x, findSub(half, timeRemaining));
            }
        }
    }

    private int findSub(int half, int timeRemaining){
        int indexOfBest = 5;
        Random r = new Random();
        Player best = subPlayers.get(5);
        for(int x = 6; x < subPlayers.size(); x++){
            if(subPlayers.get(x).isEligible()) {
                if (best.getOverallRating() / best.getFatigue() <
                        subPlayers.get(x).getOverallRating() / subPlayers.get(x).getFatigue() &&
                        (!subPlayers.get(x).isInFoulTrouble(half, timeRemaining) &&
                                r.nextInt(35) + coaches.get(0).getTendencyToSub() > 75)) {
                    indexOfBest = x;
                    best = subPlayers.get(indexOfBest);
                }
            }
        }

        return indexOfBest;
    }

}

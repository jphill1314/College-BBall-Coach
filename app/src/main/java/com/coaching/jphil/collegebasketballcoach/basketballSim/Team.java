package com.coaching.jphil.collegebasketballcoach.basketballSim;

import android.content.Context;
import android.util.Log;

import com.coaching.jphil.collegebasketballcoach.Database.GameStatsDB;
import com.coaching.jphil.collegebasketballcoach.MainActivity;
import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.conferences.Conference;

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

    private ArrayList<Player> players, rosterPlayers, subPlayers;
    private ArrayList<Coach> coaches;
    private ArrayList<Recruit> recruits;
    private ArrayList<Team> opponents;
    private ArrayList<Game> schedule;
    private Conference conference;
    private int gamesPlayed;

    private int wins, loses, overallRating;

    private String schoolName, mascot;
    private int colorMain, colorDark, colorLight;

    private int numberOfGames;

    // Strategy
    private int offenseFavorsThrees;
    private int defenseFavorsThrees;
    private int aggression;
    private int pace;

    private int lastScoreDif = 0;

    private int twoPointAttempts;
    private int twoPointMakes;
    private int threePointAttempts;
    private int threePointMakes;
    private int freeThrowAttempts;
    private int freeThrowMakes;
    private int assists;
    private int oBoards;
    private int dBoards;
    private int steals;
    private int turnovers;

    private int currentSeasonYear;

    private boolean seasonOver;

    private int id;

    public Team(String schoolName, String mascot, ArrayList<Player> players, ArrayList<Coach> coaches,
                boolean isPlayerControlled, int[] colors, Context context){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.players = players;
        this.coaches = coaches;
        this.context = context;

        colorMain = colors[0];
        colorDark = colors[1];
        colorLight = colors[2];

        this.isPlayerControlled = isPlayerControlled;

        gamesPlayed = 0;
        wins = 0;
        loses = 0;

        numberOfGames = 0;

        currentSeasonYear = 2017;

        opponents = new ArrayList<>();

        seasonOver = false;

        setLineup();
        setOverallRating();
        generateStrategy();
    }

    public Team(String schoolName, String mascot, boolean isPlayerControlled, int offenseFavorsThrees,
                int defenseFavorsThrees, int agression, int pace, int year, boolean isSeasonOver,
                int colorMain, int colorDark, int colorLight, int id, Context context){
        this.schoolName = schoolName;
        this.mascot = mascot;
        this.isPlayerControlled = isPlayerControlled;
        this.context = context;
        this.id = id;

        this.colorMain = colorMain;
        this.colorLight = colorLight;
        this.colorDark = colorDark;

        opponents = new ArrayList<>();

        this.wins = 0;
        this.loses = 0;
        gamesPlayed = this.wins + this.loses;

        numberOfGames = 0;

        this.offenseFavorsThrees = offenseFavorsThrees;
        this.defenseFavorsThrees = defenseFavorsThrees;
        this.aggression = agression;
        this.pace = pace;

        this.seasonOver = isSeasonOver;

        if(this.offenseFavorsThrees == 0){
            this.offenseFavorsThrees = 50;
        }
        if(this.defenseFavorsThrees == 0){
            this.defenseFavorsThrees = 50;
        }
        if(this.pace < 55){
            this.pace = 70;
        }
        currentSeasonYear = year;
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

    public int getCurrentSeasonYear(){
        return currentSeasonYear;
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

    public int getNumberOfReturningPlayers(){
        int num = 0;
        for(Player p: players){
            if(p.getYear() < 3){
                num++;
            }
        }

        for(Recruit r: recruits){
            if(r.getIsCommitted()){
                num++;
            }
        }
        return num;
    }

    public ArrayList<Coach> getCoaches(){
        return coaches;
    }

    public String[] getCoachesNamesAndAbility(){
        String[] names = new String[coaches.size()];
        for(int x = 0; x < coaches.size(); x++){
            names[x] = coaches.get(x).getFullName() + "\t" + coaches.get(x).getRecruitingAbility();
        }
        return names;
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
            recruits = new ArrayList<>();
        }
        recruits.add(recruit);
    }

    public void newSeason(){
        Iterator<Player> itr = players.iterator();
        while(itr.hasNext()){
            Player p = itr.next();
            p.newSeason(coaches);
            if(p.getYear() > 3){
                itr.remove();
            }
        }

        if(recruits != null) {
            for (Recruit r : recruits) {
                Player newPlayer = r.startNewSeason();
                if (newPlayer != null) {
                    players.add(newPlayer);
                }
            }
        }

        for(Coach c: coaches){
            c.newSeason();
        }

        generateRecruits();

        wins = 0;
        loses = 0;
        gamesPlayed = 0;
        numberOfGames = 0;

        seasonOver = false;

        schedule = new ArrayList<>();
        opponents = new ArrayList<>();

        if(!isPlayerControlled) {
            int newNum = (int) (12 + Math.random() * 4);
            Log.d("New Season", "Number of players to make: " + (newNum - players.size()));
            if (players.size() < newNum) {
                generateFreshman(newNum - players.size());
            }
        }

        int[] needs = positionNeeds(true);
        int unmetNeeds = 0;
        for(int x : needs){
            if(x > 0){
                unmetNeeds += x;
            }
        }

        if(unmetNeeds > 0){
            generateFreshman(unmetNeeds);
        }

        setLineup();
        currentSeasonYear++;
        setOverallRating();
    }

    public void firstSeason(){
        generateRecruits();
    }

    private void generateRecruits(){
        recruits = new ArrayList<>();
        int[] needs = positionNeeds(false);

        String[] firstNames = context.getResources().getStringArray(R.array.first_names);
        String[] lastNames = context.getResources().getStringArray(R.array.last_names);

        Random r = new Random();

        for(int y = 0; y < needs.length; y++){
            for(int x = 0; x < needs[y] * 2; x++){
                int rating = overallRating + 20 - r.nextInt(40);
                if(rating > 100){
                    rating = 100;
                }
                recruits.add(new Recruit(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)],
                        y+1, rating, this, recruits.size()));
            }
        }

        int current = recruits.size();
        for(int x = 0; x < 25 - current; x++){
            int rating = overallRating + 30 - r.nextInt(60);
            recruits.add(new Recruit(firstNames[r.nextInt(firstNames.length)], lastNames[r.nextInt(lastNames.length)],
                    r.nextInt(5) + 1, rating, this, recruits.size()));
        }
    }

    public int getColorMain(){
        return colorMain;
    }

    public int getColorDark(){
        return colorDark;
    }

    public int getColorLight(){
        return colorLight;
    }

    void preGameSetup(){
        rosterPlayers = new ArrayList<>(players);
        subPlayers = new ArrayList<>(players);

        for(Player p: players){
            p.preGameSetup();
        }

        twoPointAttempts = 0;
        twoPointMakes = 0;
        threePointAttempts = 0;
        threePointMakes = 0;
        freeThrowAttempts = 0;
        freeThrowMakes = 0;
        assists = 0;
        oBoards = 0;
        dBoards = 0;
        steals = 0;
        turnovers = 0;
    }

    public ArrayList<GameStatsDB> playGame(boolean wonGame){
        gamesPlayed++;
        if(wonGame){
            wins++;
        }
        else{
            loses++;
        }

        ArrayList<GameStatsDB> stats = new ArrayList<>();
        for(Player p: players){
            stats.add(p.playGame(coaches));
        }

        players = new ArrayList<>(rosterPlayers);
        lastScoreDif = 0;

        if(recruits != null){
            for(Recruit r: recruits){
                r.loseInterest();
            }
        }
        setOverallRating();
        return stats;
    }

    int getCoachTalk(int scoreDif){
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

    boolean getTimeout(int scoreDif){
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

    public void setAggression(int value){
        aggression = value;
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

    public int getAggression() {
        return aggression;
    }

    public int getPace(){
        return pace;
    }

    public int getWinPercent(){
        if(wins == 0){
            return 0;
        }
        if(loses == 0){
            return 100;
        }
        else{
            return (int) ((wins * 1.0) / gamesPlayed * 100);
        }
    }

    private void setOverallRating(){
        overallRating = 0;
        for(Player p : players){
            overallRating += p.getOverallRating();
        }

        overallRating = overallRating / players.size();
    }

    public int getNumberOfPlayersAtPosition(int position, boolean countSeniors){
        int num = 0;
        for(Player player:players){
            if(player.getPosition() == position){
                if(!countSeniors){
                    if(player.getYear() != 3) {
                        num++;
                    }
                }
                else{
                    num++;
                }
            }
        }
        return num;
    }

    private void generateFreshman(int numPlayers){
        Random r = new Random();
        int vary = 10;
        String[] lastNames = context.getResources().getStringArray(R.array.last_names);
        String[] firstNames = context.getResources().getStringArray(R.array.first_names);

        int rating = overallRating;
        if(isPlayerControlled){
            rating -= 25;
        }

        for(int x = 1; x < 6; x++){
            while(getNumberOfPlayersAtPosition(x, true) < 2){
                players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                        x, 0, rating - r.nextInt(2 * vary) + vary));
                numPlayers--;
            }
        }

        for(int i = 0; i < numPlayers; i++){
            players.add(new Player(lastNames[r.nextInt(lastNames.length)], firstNames[r.nextInt(firstNames.length)],
                    r.nextInt(4) + 1, 0, rating - r.nextInt(2 * vary) + vary));
        }
    }

    private int[] positionNeeds(boolean countSeniors){
        int[] needs = new int[] {0,0,0,0,0};
        for(int x = 1; x < 6; x++){
            needs[x-1] = 2 - getNumberOfPlayersAtPosition(x, countSeniors);
        }
        return needs;
    }

    public boolean updateSubs(int i1, int i2){
        if(subPlayers.get(i2).isEligible()) {
            Collections.swap(subPlayers, i1, i2);
            return true;
        }
        return false;
    }

    public boolean updateSubs(ArrayList<Player> newSubs){
        subPlayers = new ArrayList<>(newSubs);
        for(int x = 0; x < 5; x++){
            if(!newSubs.get(x).isEligible()){
                return false;
            }
        }
        return true;
    }

    public void makeSubs(){
        players = new ArrayList<>(subPlayers);
        updateCurrentPositions();
    }

    public void makeSubs(ArrayList<Player> subs){
        players = new ArrayList<>(subs);
        updateCurrentPositions();
    }

    private void updateCurrentPositions(){
        for(int x = 0; x < players.size(); x++){
            if(x < 5) {
                players.get(x).setCurrentPosition(x + 1);
            }
            else{
                players.get(x).setCurrentPosition(players.get(x).getPosition());
            }
        }
    }

    void aiMakeSubs(int half, int timeRemaining){
        int tendToSub = coaches.get(0).getTendencyToSub();

        Random r = new Random();

        for(int x = 0; x < 5; x++){
            if(subPlayers.get(x).isEligible()){
                if(subPlayers.get(x).getFatigue() > r.nextInt(10) - 5 + tendToSub ||
                        (subPlayers.get(x).isInFoulTrouble(half, timeRemaining) && tendToSub > r.nextInt(30))){
                    int sub = findSub(half, timeRemaining, subPlayers.get(x).getCurrentPosition(), false);
                    if(subPlayers.get(x).calculateRatingAtPosition(subPlayers.get(x).getCurrentPosition()) / subPlayers.get(x).getFatigue() <
                            subPlayers.get(sub).calculateRatingAtPosition(subPlayers.get(x).getCurrentPosition())/ subPlayers.get(sub).getFatigue()){
                        updateSubs(x, sub);
                    }
                }
            }
            else{
                updateSubs(x, findSub(half, timeRemaining, subPlayers.get(x).getCurrentPosition(), true));
            }
        }
    }

    private int findSub(int half, int timeRemaining, int position, boolean mustSub){
        int indexOfBest = -5;
        Random r = new Random();
        Player best = subPlayers.get(5);

        for(int x = 5; x < subPlayers.size(); x++){
            if(indexOfBest == -5 && subPlayers.get(x).isEligible()){
                indexOfBest = x;
                best = subPlayers.get(x);
                continue;
            }
            if(subPlayers.get(x).isEligible()) {
                if (best.calculateRatingAtPosition(position) / best.getFatigue() <
                        subPlayers.get(x).calculateRatingAtPosition(position) / subPlayers.get(x).getFatigue() &&
                        (mustSub || (!subPlayers.get(x).isInFoulTrouble(half, timeRemaining) ||
                                coaches.get(0).getTendencyToSub() > r.nextInt(30)))) {
                    indexOfBest = x;
                    best = subPlayers.get(indexOfBest);
                }
            }
        }

        return indexOfBest;
    }

    public int getNumberOfGames(){
        return numberOfGames;
    }

    void addOpponent(Team team){
        if(!opponents.contains(team)) {
            opponents.add(team);
        }
        numberOfGames++;
    }

    ArrayList<Team> getOpponents(){
        return opponents;
    }

    public void setConference(Conference conference){
        this.conference = conference;
    }

    public Conference getConference(){
        return conference;
    }

    public void addGameToSchedule(Game game){
        if(schedule == null){
            schedule = new ArrayList<>();
        }
        schedule.add(game);

        if(game.isPlayed()){
            if(game.homeTeamWin()){
                if(game.getHomeTeam().equals(this)){
                    wins++;
                }
                else{
                    loses++;
                }
            }
        }
    }

    public boolean hasUnplayedGames(){
        for(Game g: schedule){
            if(!g.isPlayed()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Game> getSchedule(){
        return schedule;
    }

    void addTwoPointShot(boolean made){
        twoPointAttempts++;
        if(made){
            twoPointMakes++;
        }
    }

    void addThreePointShot(boolean made){
        threePointAttempts++;
        if(made){
            threePointMakes++;
        }
    }

    void addFreeThrowShot(boolean made){
        freeThrowAttempts++;
        if(made){
            freeThrowMakes++;
        }
    }

    void addAssist(){
        assists++;
    }

    void addRebound(boolean offensive){
        if(offensive){
            oBoards++;
        }
        else{
            dBoards++;
        }
    }

    void addTurnover(){
        turnovers++;
    }

    void addSteal(){
        steals++;
    }

    public int getTwoPointAttempts() {
        return twoPointAttempts;
    }

    public int getTwoPointMakes() {
        return twoPointMakes;
    }

    public int getThreePointAttempts() {
        return threePointAttempts;
    }

    public int getThreePointMakes() {
        return threePointMakes;
    }

    public int getFreeThrowAttempts() {
        return freeThrowAttempts;
    }

    public int getFreeThrowMakes() {
        return freeThrowMakes;
    }

    public int getAssists() {
        return assists;
    }

    public int getoBoards() {
        return oBoards;
    }

    public int getdBoards() {
        return dBoards;
    }

    public int getSteals() {
        return steals;
    }

    public int getTurnovers() {
        return turnovers;
    }

    private void generateStrategy(){
        pace = (int)((getAverageStamina() / 100.0) * 35 + 55);
        aggression = 0;

        offenseFavorsThrees = (int)((getAverageThreePointShot() * 1.0 / getAverageCloseShot()) * 25 + 25);
        defenseFavorsThrees = (int)((getAveragePerimDef() * 1.0 / getAveragePostDef()) * 25 + 25);

        if(offenseFavorsThrees > 75){
            offenseFavorsThrees = 75;
        }
        else if(offenseFavorsThrees < 25){
            offenseFavorsThrees = 25;
        }

        if(defenseFavorsThrees > 75){
            defenseFavorsThrees = 75;
        }
        else if(defenseFavorsThrees < 25){
            defenseFavorsThrees = 25;
        }


    }

    private int getAverageStamina(){
        int total = 0;
        for(Player p: players){
            total += p.getStamina();
        }

        return total / players.size();
    }

    private int getAverageThreePointShot(){
        int total = 0;
        for(Player p: players){
            total += p.getLongRangeShot();
        }
        return total / players.size();
    }

    private int getAverageCloseShot(){
        int total = 0;
        for(Player p: players){
            total += p.getCloseRangeShot();
        }
        return total / players.size();
    }

    private int getAveragePostDef(){
        int total = 0;
        for(Player p: players){
            total += p.getPostDefense();
        }
        return total / players.size();
    }

    private int getAveragePerimDef(){
        int total = 0;
        for(Player p: players){
            total += p.getPerimeterDefense();
        }
        return total / players.size();
    }

    public boolean isSeasonOver(){
        return seasonOver;
    }

    public void toggleSeasonOver(){
        seasonOver = !seasonOver;
    }

    private int[] getConferenceRecord(){
        int[] record = new int[]{0,0};
        Conference conf = null;
        for(Conference c: ((MainActivity)context).conferences){
            if(c.getTeams().contains(this)){
                conf = c;
            }
        }

        if(conf != null) {
            for (Game g : schedule) {
                if (g.getHomeTeam().equals(this)) {
                    if(conf.getTeams().contains(g.getAwayTeam())) {
                        if(g.isPlayed()) {
                            if (g.homeTeamWin()) {
                                record[0]++;
                            } else {
                                record[1]++;
                            }
                        }
                    }
                }
                else {
                    if(conf.getTeams().contains(g.getHomeTeam())) {
                        if(g.isPlayed()) {
                            if (g.homeTeamWin()) {
                                record[1]++;
                            } else {
                                record[0]++;
                            }
                        }
                    }
                }
            }
        }

        return record;
    }

    public int getConferenceWins(){
        return getConferenceRecord()[0];
    }

    public int getConferenceLoses(){
        return getConferenceRecord()[1];
    }

    public int getConferenceWinPercent(){
        int[] record = getConferenceRecord();
        if(record[1] == 0){
            return 100;
        }
        if(record[0] == 0){
            return 0;
        }

        return (int) ((record[0] * 1.0) / (record[0] + record[1]));
    }

    private double getOpponentWinPercent(){
        int oppWins = 0;
        int oppLoses = 0;
        for(Game g: schedule){
            if(g.isPlayed()){
                if(g.getHomeTeam().equals(this)){
                    oppWins += g.getAwayTeam().getWins();
                    oppLoses += g.getAwayTeam().getLoses();
                }
                else{
                    oppWins += g.getHomeTeam().getWins();
                    oppLoses += g.getHomeTeam().getLoses();
                }
            }
        }
        return (oppWins * 1.0) / (oppLoses + oppWins);
    }

    public double getRPI(){
        if(gamesPlayed > 0) {
            double opponentWP = getOpponentWinPercent();
            double oppOppWP = 0;

            for (Team t : opponents) {
                oppOppWP += t.getOpponentWinPercent();
            }
            oppOppWP = oppOppWP / opponents.size();
            return (.25 * (getWinPercent() / 100.0) + .5 * opponentWP + .25 * oppOppWP);
        }
        return 0;
    }

    private void setLineup(){
        ArrayList<Player> pgs = new ArrayList<>();
        ArrayList<Player> sgs = new ArrayList<>();
        ArrayList<Player> sfs = new ArrayList<>();
        ArrayList<Player> pfs = new ArrayList<>();
        ArrayList<Player> cs = new ArrayList<>();

        for(Player p: players){
            switch (p.getPosition()){
                case 1:
                    pgs.add(p);
                    break;
                case 2:
                    sgs.add(p);
                    break;
                case 3:
                    sfs.add(p);
                    break;
                case 4:
                    pfs.add(p);
                    break;
                case 5:
                    cs.add(p);
                    break;
            }
        }

        sortPlayersByRating(pgs);
        sortPlayersByRating(sgs);
        sortPlayersByRating(sfs);
        sortPlayersByRating(pfs);
        sortPlayersByRating(cs);

        players = new ArrayList<>();
        players.add(pgs.get(0));
        players.add(sgs.get(0));
        players.add(sfs.get(0));
        players.add(pfs.get(0));
        players.add(cs.get(0));

        players.add(pgs.get(1));
        players.add(sgs.get(1));
        players.add(sfs.get(1));
        players.add(pfs.get(1));
        players.add(cs.get(1));

        for(int x = 2; x < pgs.size(); x++){
            players.add(pgs.get(x));
        }
        for(int x = 2; x < sgs.size(); x++){
            players.add(sgs.get(x));
        }
        for(int x = 2; x < sfs.size(); x++){
            players.add(sfs.get(x));
        }
        for(int x = 2; x < pfs.size(); x++){
            players.add(pfs.get(x));
        }
        for(int x = 2; x < cs.size(); x++){
            players.add(cs.get(x));
        }
    }

    private void sortPlayersByRating(ArrayList<Player> list){
        int chagnes;
        do{
            chagnes = 0;
            for(int x = 0; x < list.size()-1; x++){
                for(int y = x + 1; y < list.size(); y++){
                    if(list.get(x).getOverallRating() < list.get(y).getOverallRating()){
                        Collections.swap(list, x, y);
                        chagnes++;
                    }
                }
            }
        }while(chagnes != 0);
    }
}

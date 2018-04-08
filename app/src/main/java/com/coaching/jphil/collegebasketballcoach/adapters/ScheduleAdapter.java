package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;
import com.coaching.jphil.collegebasketballcoach.fragments.ScheduleFragment;

import java.util.ArrayList;

/**
 * Created by jphil on 2/14/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<Game> games;
    private ArrayList<Team> teams;
    private Team playerTeam;
    private Context context;
    private ScheduleFragment frag;
    private int type;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView homeName, awayName, homeScore, awayScore, info;
        View view;
        public ViewHolder(View view){
            super(view);

            this.view = view;
            homeName = view.findViewById(R.id.home_team_name);
            awayName = view.findViewById(R.id.away_team_name);
            homeScore = view.findViewById(R.id.home_score);
            awayScore = view.findViewById(R.id.away_score);
            info = view.findViewById(R.id.extra_info);
        }
    }

    public ScheduleAdapter(ArrayList<Game> games, Team team, ScheduleFragment frag, int type){
        this.games = games;
        this.frag = frag;
        this.type = type;
        playerTeam = team;
    }

    public ScheduleAdapter(ArrayList<Game> games, Team team, ArrayList<Team> teams, ScheduleFragment frag, int type){
        this.games = games;
        this.frag = frag;
        this.type = type;
        this.teams = teams;
        playerTeam = team;
    }

    public void changeGames(ArrayList<Game> games){
        this.games = games;
        notifyDataSetChanged();
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        if(position < games.size()) {
            holder.view.setOnClickListener(null);
            if(type == 0 || teams == null) {
                holder.awayName.setText(games.get(position).getAwayTeamName());
                holder.homeName.setText(context.getResources().getString(R.string.home_name, games.get(position).getHomeTeamName()));
            }
            else{
                holder.awayName.setText(context.getResources().getString(R.string.team_tourn, teams.indexOf(games.get(position).getAwayTeam())+1, games.get(position).getAwayTeamName()));
                holder.homeName.setText(context.getResources().getString(R.string.team_tourn, teams.indexOf(games.get(position).getHomeTeam())+1, games.get(position).getHomeTeamName()));
            }

            if (games.get(position).isPlayed() || games.get(position).isInProgress()) {
                holder.homeScore.setText(context.getResources().getString(R.string.scores, games.get(position).getHomeScore()));
                holder.awayScore.setText(context.getResources().getString(R.string.scores, games.get(position).getAwayScore()));
            }
            else {
                holder.homeScore.setText(games.get(position).getHomeTeam().getRecordAsString());
                holder.awayScore.setText(games.get(position).getAwayTeam().getRecordAsString());
            }

            if(type == 0){
                if(games.get(position).isPlayed()){
                    holder.info.setText(context.getResources().getString(R.string.postgame_info));
                }
                else if(games.get(position).isInProgress()){
                    holder.info.setText(context.getResources().getString(R.string.game_in_progress));
                }
                else{
                    holder.info.setText(context.getResources().getString(R.string.pregame_info, (position+1)));
                }
            }
            else{
                holder.info.setText("");
            }
        }
        else{
            if(type == 0) {
                if(position == games.size()) {
                    holder.homeName.setText("");
                    holder.awayName.setText("");
                    holder.homeScore.setText("");
                    holder.awayScore.setText("");
                    holder.info.setText(context.getResources().getString(R.string.enter_tourn));

                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            frag.viewConferenceTournament();
                        }
                    });
                }
                else{
                    holder.homeName.setText("");
                    holder.awayName.setText("");
                    holder.homeScore.setText("");
                    holder.awayScore.setText("");
                    holder.info.setText(context.getResources().getString(R.string.enter_champ));

                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            frag.viewChampionship();
                        }
                    });
                }
            }
            else{
                if(type == 1){
                    holder.homeName.setText("");
                    holder.awayName.setText("");
                    holder.homeScore.setText("");
                    holder.awayScore.setText("");
                    holder.info.setText(context.getResources().getString(R.string.enter_champ));

                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            frag.viewChampionship();
                        }
                    });
                }
                else{
                    holder.homeName.setText("");
                    holder.awayName.setText("");
                    holder.homeScore.setText("");
                    holder.awayScore.setText("");
                    holder.info.setText(context.getResources().getString(R.string.new_season));

                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            frag.startNewSeason();

                        }
                    });
                }
            }
        }
    }

    @Override
    public int getItemCount(){
        if(type == 0) {
            return games.size() + 2;
        }
        else{
            return games.size() + 1;
        }
    }
}

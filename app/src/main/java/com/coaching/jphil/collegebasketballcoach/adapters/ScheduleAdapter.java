package com.coaching.jphil.collegebasketballcoach.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;

/**
 * Created by jphil on 2/14/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private ArrayList<Game> games;
    private Team playerTeam;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvDate, tvDesc, tvScore;
        public ViewHolder(View view){
            super(view);
            tvDate = view.findViewById(R.id.schedule_date);
            tvDesc = view.findViewById(R.id.schedule_desc);
            tvScore = view.findViewById(R.id.schedule_score);
        }
    }

    public ScheduleAdapter(ArrayList<Game> games, Team playerTeam){
        this.games = games;
        this.playerTeam = playerTeam;
    }

    public void changeGames(ArrayList<Game> games){
        this.games = games;
        notifyDataSetChanged();
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvDate.setText("Game #" + (position + 1));
        if(games.get(position).getHomeTeam().equals(playerTeam)){
            holder.tvDesc.setText("vs. " + games.get(position).getAwayTeamName());
            if(games.get(position).isPlayed() && games.get(position).homeTeamWin()){
                holder.tvScore.setBackgroundResource(R.color.winner);
            }
            else if(games.get(position).isPlayed()){
                holder.tvScore.setBackgroundResource(R.color.loser);
            }
            else{
                holder.tvScore.setBackgroundResource(R.color.defaultTV);
            }

        }
        else{
            holder.tvDesc.setText("@ " + games.get(position).getHomeTeamName());
            if(games.get(position).isPlayed() && !games.get(position).homeTeamWin()){
                holder.tvScore.setBackgroundResource(R.color.winner);
            }
            else if(games.get(position).isPlayed()){
                holder.tvScore.setBackgroundResource(R.color.loser);
            }
            else{
                holder.tvScore.setBackgroundResource(R.color.defaultTV);
            }
        }

        holder.tvScore.setText(games.get(position).getFormattedScore());
    }

    @Override
    public int getItemCount(){
        return games.size();
    }
}

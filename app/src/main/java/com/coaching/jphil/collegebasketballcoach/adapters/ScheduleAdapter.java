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
            tvDate = (TextView) view.findViewById(R.id.schedule_date);
            tvDesc = (TextView) view.findViewById(R.id.schedule_desc);
            tvScore = (TextView) view.findViewById(R.id.schedule_score);
        }
    }

    public ScheduleAdapter(ArrayList<Game> games, Team playerTeam){
        this.games = games;
        this.playerTeam = playerTeam;
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvDate.setText("Game #" + (position + 1));
        if(games.get(position).getHomeTeam().getFullName().equals(playerTeam.getFullName())){
            holder.tvDesc.setText("vs. " + games.get(position).getAwayTeamName());
        }
        else{
            holder.tvDesc.setText("@ " + games.get(position).getHomeTeamName());
        }

        holder.tvScore.setText(games.get(position).getFormattedScore());
    }

    @Override
    public int getItemCount(){
        return games.size();
    }
}

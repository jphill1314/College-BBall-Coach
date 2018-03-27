package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jphil on 3/24/2018.
 */

public class TournamentGameAdapter extends RecyclerView.Adapter<TournamentGameAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView topTeamName, topTeamScore, bottomTeamName, bottomTeamScore;


        public ViewHolder(View view){
            super(view);

            topTeamName = view.findViewById(R.id.top_team_name);
            topTeamScore = view.findViewById(R.id.top_team_score);
            bottomTeamName = view.findViewById(R.id.bottom_team_name);
            bottomTeamScore = view.findViewById(R.id.bottom_team_score);
        }
    }


    private ArrayList<Game> games;
    private ArrayList<Team> teams;
    private Context context;

    public TournamentGameAdapter(ArrayList<Game> games, ArrayList<Team> teams){
        this.games = games;
        this.teams = teams;
    }

    public void changeGames(ArrayList<Game> games){
        this.games = games;
        notifyDataSetChanged();
    }

    public void changeTeams(ArrayList<Team> teams){
        this.teams = teams;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tournament_game_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.topTeamName.setText(context.getResources().getString(R.string.team_tourn, teams.indexOf(games.get(position).getHomeTeam())+1, games.get(position).getHomeTeamName()));
        holder.bottomTeamName.setText(context.getResources().getString(R.string.team_tourn, teams.indexOf(games.get(position).getAwayTeam())+1, games.get(position).getAwayTeamName()));

        if(games.get(position).isPlayed()){
            holder.topTeamScore.setText(String.format(Locale.US, "%3d", games.get(position).getHomeScore()));
            holder.bottomTeamScore.setText(String.format(Locale.US, "%3d", games.get(position).getAwayScore()));

            if(games.get(position).homeTeamWin()){
                holder.topTeamScore.setBackgroundColor(Color.GREEN);
                holder.bottomTeamScore.setBackgroundColor(Color.RED);
            }
            else{
                holder.topTeamScore.setBackgroundColor(Color.RED);
                holder.bottomTeamScore.setBackgroundColor(Color.GREEN);
            }
        }
        else{
            holder.topTeamScore.setText("-");
            holder.bottomTeamScore.setText("-");
            holder.topTeamScore.setBackgroundColor(Color.rgb(250,250,250));
            holder.bottomTeamScore.setBackgroundColor(Color.rgb(250,250,250));
        }

        if(games.get(position).getHomeTeam().isPlayerControlled()){
            holder.topTeamName.setBackgroundColor(Color.rgb(225,225,225));
        }
        else{
            holder.topTeamName.setBackgroundColor(Color.rgb(250,250,250));
        }

        if(games.get(position).getAwayTeam().isPlayerControlled()){
            holder.bottomTeamName.setBackgroundColor(Color.rgb(225,225,225));
        }
        else{
            holder.bottomTeamName.setBackgroundColor(Color.rgb(250,250,250));
        }
    }

    @Override
    public int getItemCount() {
        if(games == null){
            return 0;
        }
        return games.size();
    }
}

package com.coaching.jphil.collegebasketballcoach.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Game;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by jphil on 2/17/2018.
 */

public class StandingAdapter extends RecyclerView.Adapter<StandingAdapter.ViewHolder> {

    private ArrayList<Team> standing;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvPos, tvName, tvWins, tvLoses;

        public ViewHolder(View view){
            super(view);

            tvPos = view.findViewById(R.id.standing_position);
            tvName = view.findViewById(R.id.standing_team);
            tvWins = view.findViewById(R.id.standing_wins);
            tvLoses = view.findViewById(R.id.standing_loses);
        }
    }

    public StandingAdapter(ArrayList<Team> teams){
        standing = teams;
        generateStandings();
    }

    @Override
    public StandingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.standings_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        holder.tvPos.setText(Integer.toString(position + 1));
        holder.tvName.setText(standing.get(position).getFullName() + " (" + standing.get(position).getOverallRating() + ")");
        holder.tvWins.setText(Integer.toString(standing.get(position).getWins()));
        holder.tvLoses.setText(Integer.toString(standing.get(position).getLoses()));
    }

    @Override
    public int getItemCount(){
        return standing.size();
    }

    private void generateStandings(){
        int changes = 0;

        do{
            changes = 0;
            for(int x = 0; x < standing.size() - 1; x++){
                for(int y = x + 1; y < standing.size(); y++) {
                    if (standing.get(x).getWins() < standing.get(y).getWins()) {
                        Collections.swap(standing, x, y);
                        changes++;
                    }
                    else if(standing.get(x).getWins() == standing.get(y).getWins()){
                        if(standing.get(x).getLoses() > standing.get(y).getLoses()){
                            Collections.swap(standing, x, y);
                            changes++;
                        }
                    }
                }
            }
        }while(changes != 0);
    }
}

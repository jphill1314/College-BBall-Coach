package com.coaching.jphil.collegebasketballcoach.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.Player;

import java.util.ArrayList;


/**
 * Created by jphil on 3/15/2018.
 */

public class GameRosterAdapter extends RecyclerView.Adapter<GameRosterAdapter.ViewHolder> {

    private ArrayList<Player> players;
    private boolean[] pendingSub;
    private int index1, index2;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView pos, name, stats;
        View view;

        public ViewHolder(View view){
            super(view);

            pos = view.findViewById(R.id.player_position);
            name = view.findViewById(R.id.player_name);
            stats = view.findViewById(R.id.player_stats);

            this.view = view;
        }
    }

    public GameRosterAdapter(ArrayList<Player> players){
        this.players = players;
        index1 = -1;
        index2 = -1;
        pendingSub = new boolean[players.size()];
        for(int x = 0; x < players.size(); x++){
            pendingSub[x] = false;
        }
    }

    public int[] getIndexes(){
        int[] values = new int[]{index1, index2};
        if(index1 != -1) {
            pendingSub[index1] = true;
        }
        if(index2 != -1) {
            pendingSub[index2] = true;
        }
        index1 = -1;
        index2 = -1;
        notifyDataSetChanged();
        return values;
    }

    public void clearPending(){
        for(int x = 0; x < pendingSub.length; x++){
            pendingSub[x] = false;
        }
        notifyDataSetChanged();
    }

    @Override
    public GameRosterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_roster_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameRosterAdapter.ViewHolder holder, int position){
        String ps;
        switch (position){
            case 0:
                ps = "PG";
                break;
            case 1:
                ps = "SG";
                break;
            case 2:
                ps = "SF";
                break;
            case 3:
                ps = "PF";
                break;
            case 4:
                ps = "C";
                break;
            default:
                ps = "-";
        }
        holder.pos.setText(ps);
        holder.name.setText(players.get(position).getFullName());
        holder.stats.setText(players.get(position).getGameStatsAsString());

        holder.view.setBackgroundColor(Color.rgb(250, 250,250));

        if(players.get(position).getFatigue() > 60){
            holder.name.setBackgroundColor(Color.rgb(255, 0, 0));
        }
        else if(players.get(position).getFatigue() > 40){
            holder.name.setBackgroundColor(Color.rgb(255, 255, 0));
        }
        else {
            holder.name.setBackgroundColor(Color.rgb(0, 255, 0));
        }

        if(position == index1 || position == index2){
            holder.view.setBackgroundColor(Color.LTGRAY);
        }

        if(pendingSub[position]){
            holder.view.setBackgroundColor(Color.DKGRAY);
        }

        if(!players.get(position).isEligible()){
            holder.view.setBackgroundColor(Color.BLACK);
        }

        final int pos = position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index1 == -1 && !pendingSub[pos]){
                    index1 = pos;
                }
                else if(pos != index1 && !pendingSub[pos]){
                    index2 = pos;
                }
                else{
                    index1 = -1;
                    index2 = -1;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount(){
        return players.size();
    }
}

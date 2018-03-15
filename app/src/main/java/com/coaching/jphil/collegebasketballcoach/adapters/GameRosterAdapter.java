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
    }

    public int[] getIndexes(){
        int[] values = new int[]{index1, index2};
        index1 = -1;
        index2 = -1;
        notifyDataSetChanged();
        return values;
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

        holder.view.setBackgroundColor(Color.rgb(250,250,250));

        if(position == index1 || position == index2){
            holder.view.setBackgroundColor(Color.LTGRAY);
        }

        if(!players.get(position).isEligible()){
            holder.view.setBackgroundColor(Color.RED);
            if(index1 == position){
                index1 = -1;
                index2 = -1;
            }
            else if(index2 == position){
                index2 = -1;
            }
        }

        final int pos = position;
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index1 == -1){
                    index1 = pos;
                }
                else if(pos != index1){
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

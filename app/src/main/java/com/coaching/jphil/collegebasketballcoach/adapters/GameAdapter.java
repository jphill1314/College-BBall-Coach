package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;
import com.coaching.jphil.collegebasketballcoach.basketballSim.GameEvent;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by jphil on 3/13/2018.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView  play;
        View view;

        public ViewHolder(View view){
            super(view);

            this.view = view;
            play = view.findViewById(R.id.play);
        }
    }

    private ArrayList<GameEvent> plays;
    private ArrayList<String> talks;

    public GameAdapter(ArrayList<GameEvent> plays){
        this.plays = plays;
    }

    public GameAdapter(ArrayList<String> talks, int extra){
        this.talks = talks;
    }

    public void setPlays(ArrayList<GameEvent>  newPlays){
        plays = newPlays;
    }


    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameAdapter.ViewHolder holder, int position) {
        holder.view.setBackgroundColor(Color.rgb(250,250,250));
        if(plays != null) {
            holder.play.setText(plays.get(position).getEvent());
            if(plays.get(position).isHomeTeam()){
                holder.view.setBackgroundColor(Color.rgb(240,240,240));
            }
            else{
                holder.view.setBackgroundColor(Color.rgb(250,250,250));
            }
        }
        else if(talks != null){
            holder.play.setText(talks.get(position));
        }

    }

    @Override
    public int getItemCount() {
        if(plays == null){
            if(talks == null) {
                return 0;
            }
            return talks.size();
        }
        return plays.size();
    }
}

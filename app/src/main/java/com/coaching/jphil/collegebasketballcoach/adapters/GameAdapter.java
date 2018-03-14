package com.coaching.jphil.collegebasketballcoach.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;

import java.util.ArrayList;


/**
 * Created by jphil on 3/13/2018.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView  play;

        public ViewHolder(View view){
            super(view);

            play = view.findViewById(R.id.play);
        }
    }

    private ArrayList<String> plays;

    public GameAdapter(ArrayList<String> plays){
        this.plays = plays;
    }

    public void setPlays(ArrayList<String>  newPlays){
        plays = newPlays;
    }

    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameAdapter.ViewHolder holder, int position) {
        holder.play.setText(plays.get(position));
    }

    @Override
    public int getItemCount() {
        if(plays == null){
            return 0;
        }
        return plays.size();
    }
}

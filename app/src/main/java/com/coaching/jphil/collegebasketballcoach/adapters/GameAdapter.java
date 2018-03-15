package com.coaching.jphil.collegebasketballcoach.adapters;

import android.content.Context;
import android.graphics.Color;
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
    private int displayType; // 0 = show plays, 1 = show coach talks
    private int selectedValue = 0;

    public GameAdapter(ArrayList<String> plays, int type){
        this.plays = plays;
        displayType = type;
    }

    public void setPlays(ArrayList<String>  newPlays){
        plays = newPlays;
    }

    public void setDisplayType(int type){
        displayType = type;
    }

    public int getSelectedValue(){
        return selectedValue;
    }

    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GameAdapter.ViewHolder holder, int position) {
        holder.play.setText(plays.get(position));
        holder.play.setBackgroundColor(Color.rgb(250,250,250));


        if(displayType == 1) {
            if(position == selectedValue){
                holder.play.setBackgroundColor(Color.GRAY);
            }

            final int pos = position;
            holder.play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedValue = pos;
                    notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if(plays == null){
            return 0;
        }
        return plays.size();
    }
}

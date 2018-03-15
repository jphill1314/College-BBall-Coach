package com.coaching.jphil.collegebasketballcoach.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coaching.jphil.collegebasketballcoach.R;

/**
 * Created by jphil on 3/15/2018.
 */

public class GameSpeechAdapter extends RecyclerView.Adapter<GameSpeechAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;

        public ViewHolder(View view){
            super(view);
            text = view.findViewById(R.id.play);
        }
    }

    private String[] speeches;
    private int selectedValue;

    public GameSpeechAdapter(String[] speeches){
        this.speeches = speeches;
        selectedValue = 0;
    }

    public int getSelectedValue(){
        return selectedValue;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.text.setText(speeches[position]);
        holder.text.setBackgroundColor(Color.rgb(250,250,250));

        if(position == selectedValue){
            holder.text.setBackgroundColor(Color.GRAY);
        }

        final int pos = position;
        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedValue = pos;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return speeches.length;
    }




}
